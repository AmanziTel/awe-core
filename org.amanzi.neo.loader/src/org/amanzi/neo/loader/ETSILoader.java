/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.neo.loader;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.index.MultiPropertyIndex.MultiTimeIndexConverter;
import org.amanzi.neo.loader.etsi.commands.AbstractETSICommand;
import org.amanzi.neo.loader.etsi.commands.CommandSyntax;
import org.amanzi.neo.loader.etsi.commands.ETSICommandPackage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Node;

/**
 * Loader of ETSI data
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ETSILoader extends DriveLoader {
	
	/*
	 * 
	 */
	private static final String UNSOLICITED = "<UNSOLICITED>";
	
	/*
	 * ETSI log file extension
	 */
	private static final String ETSI_LOG_FILE_EXTENSION = ".log";

	/*
	 * Timestamp format for ETSI log files
	 */
	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	
	/*
	 * Formatter for timestamp
	 */
	private SimpleDateFormat timestampFormat;
	
	/*
	 * Previous Mp Node
	 */
	private Node previousMpNode;
	
	/*
	 * Previous Ms Node 
	 */
	private Node previousMsNode;
	
	/*
	 * List of mm nodes
	 */
	private ArrayList<Node> mmNodes = new ArrayList<Node>();
	
	/*
	 * Current name of file node
	 */
	private String currentFileName;
	
	/*
	 * Is new file proccesses
	 */
	private boolean newFile = true;
	
	/**
	 * Creates a loader
	 * 
	 * @param directoryName name of directory to import
	 * @param display
	 * @param dataset name of dataset
	 */
	public ETSILoader(String directoryName, Display display, String dataset) {
		if (dataset == null) {
			int startIndex = directoryName.lastIndexOf(File.separator);
			if (startIndex < 0) {
				startIndex = 0;
			}
			else {
				startIndex++;
			}
			dataset = directoryName.substring(startIndex);
		}
		initialize("ETSI", null, directoryName, display, dataset);
		this.filename = directoryName;
		
		addDriveIndexes();
		
		timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws IOException {
		ArrayList<File> allFiles = getAllLogFilePathes(filename);
		monitor.beginTask("Loading ETSI data", allFiles.size());
		for (File logFile : allFiles) {
			monitor.subTask("Loading file " + logFile.getAbsolutePath());
			
			filename = logFile.getAbsolutePath();
			newFile = true;
			typedProperties = null;
	
			super.run(null);
			
			monitor.worked(1);
		}
		
		super.cleanupGisNode();
		super.finishUpGis(getDatasetNode());
		basename = dataset;
		printStats(false);
	}
	
	@Override
	protected final void findOrCreateFileNode(Node mp) {
		int startIndex = filename.lastIndexOf(File.separator);
		if (startIndex < 0) {
			startIndex = 0;
		}
		else {
			startIndex++;
		}
		
		//we should load all files #1, #2 and #3 in a single file node
		if (newFile) {
			String tempName = filename.substring(startIndex, filename.lastIndexOf("#"));
			if ((currentFileName == null) || (!currentFileName.equals(tempName))) {
				currentFileName = tempName;
				filename = tempName;
				basename = filename;
				file = null;
				previousMpNode = null;
			}
			newFile = false;
		}
		
		super.findOrCreateFileNode(mp);
	}
	
	@Override
	protected void parseLine(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, "|");
		if (!tokenizer.hasMoreTokens()) {
			return;
		}
		
		//get a timestamp
		String timestamp = tokenizer.nextToken();
		
		if (!tokenizer.hasMoreTokens()) {
			//if no more tokens than skip parsing
			return;
		}
		
		//next is a kind of command, we should skip it
		tokenizer.nextToken();
		
		if (!tokenizer.hasMoreTokens()) {
			//if no more tokens than skip parsing
			return;
		}

		//get a name of command
		String commandName = tokenizer.nextToken();
		
		if (!tokenizer.hasMoreTokens()) {
			//if last token is command name than it's Port.write command, do not proccess it
			return;
		}
		
		if (ETSICommandPackage.isETSICommand(commandName) || (commandName.equals(UNSOLICITED))) {
			CommandSyntax syntax = ETSICommandPackage.getCommandSyntax(commandName);
			if (syntax == CommandSyntax.SET) { 
				int equalsIndex = commandName.indexOf("=");
				tokenizer = new StringTokenizer(commandName.substring(equalsIndex).trim());
				commandName = commandName.substring(0, equalsIndex);
			}
			
			AbstractETSICommand command = ETSICommandPackage.getCommand(commandName, syntax);
			if (command == null) {
				return;
			}
			
			//get a real name of command without set or get postfix
			Node mpNode = createMpNode(timestamp);
			if (mpNode != null) {
				//parse parameters of command
				HashMap<String, Object> parameters = null;
				
				parameters = command.getResults(syntax, tokenizer);
				if (command != null) {
					commandName = command.getName();
				}
				else {
					commandName = ETSICommandPackage.getRealCommandName(commandName);
				}
					
				if (!commandName.equals(UNSOLICITED)) {
					createMsNode(mpNode, commandName, parameters);
				}
				
				if (syntax == CommandSyntax.EXECUTE) {
					while (tokenizer.hasMoreTokens()) {
						String maybeTimestamp = tokenizer.nextToken();						
						if (maybeTimestamp.startsWith("~")) {
							timestamp = maybeTimestamp;
						}
						else if (maybeTimestamp.startsWith("+")) {
							int colonIndex = maybeTimestamp.indexOf(":");
							commandName = maybeTimestamp.substring(1, colonIndex);
							StringTokenizer paramTokenizer = new StringTokenizer(maybeTimestamp.substring(colonIndex + 1).trim());
							syntax = ETSICommandPackage.getCommandSyntax(commandName);
							command = ETSICommandPackage.getCommand(commandName, syntax);
						
							if (command != null) {
								//should be a result of command
								parameters = command.getResults(syntax, paramTokenizer);
								if (command != null) {
									commandName = command.getName();
								}
								else {
									commandName = ETSICommandPackage.getRealCommandName(commandName);
								}
							}
							createMsNode(mpNode, commandName, parameters);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Creates Ms Node
	 *
	 * @param mpNode parent mp node
	 * @param commandName name of command
	 * @param parameters parameters of command
	 */
	private void createMsNode(Node mpNode, String commandName, HashMap<String, Object> parameters) {
		Node msNode = neo.createNode();
		msNode.setProperty(INeoConstants.COMMAND_PROPERTY_NAME, commandName);
		msNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.HEADER_MS);
		
		if (parameters != null) {
			for (String name : parameters.keySet()) {
				Object value = parameters.get(name);
				if (!(value instanceof List<?>)) {
					//add value to statistics
					Header header = headers.get(name);
					if (header == null) {
						header = new Header(name, name, 1);
						
						headers.put(name, header);
					}
					header.parseCount++;
					header.incValue(value);
					header.incType(value.getClass());
					
					msNode.setProperty(name, parameters.get(name));
				}
				else {
					setPropertyToMmNodes(msNode, name, (List<?>)value);
				}
			}
		}
		
		mmNodes.clear();
		
		mpNode.createRelationshipTo(msNode, GeoNeoRelationshipTypes.CHILD);
		
		if (previousMsNode != null) {
			previousMsNode.createRelationshipTo(msNode, GeoNeoRelationshipTypes.NEXT);
		}
		previousMsNode = msNode;
	}
	
	/**
	 * Creates Mm nodes for multiple properties of Ms node
	 *
	 * @param msNode parent ms node
	 * @param name name of property
	 * @param properties list of values for this property
	 */
	private void setPropertyToMmNodes(Node msNode, String name, List<?> properties) {
		Node previousMmNode = null;
		
		for (int i = 0; i < properties.size(); i++) {
			Node mmNode = null; 
			if (mmNodes.size() > i) {
				mmNode = mmNodes.get(i);
			}
			
			if (mmNode == null) {
				mmNode = neo.createNode();
				mmNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, "mm");	
				
				msNode.createRelationshipTo(mmNode, GeoNeoRelationshipTypes.CHILD);
			}
			
			if (previousMmNode != null) {
				previousMmNode.createRelationshipTo(mmNode, GeoNeoRelationshipTypes.NEXT);
			}
			previousMmNode = mmNode;				
			
			mmNodes.add(mmNode);
			Object value = properties.get(i);
			if (value != null) {
				mmNode.setProperty(name, properties.get(i)); 
			}						
		}
	}
	
	/**
	 * Creates Mp node
	 *
	 * @param timestamp timestamp
	 * @return created node
	 */
	private Node createMpNode(String timestamp) {
		Node mpNode = neo.createNode();
		mpNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.MP_TYPE_NAME);
		
		try {
			long timestampValue = timestampFormat.parse(timestamp).getTime();
			
			updateTimestampMinMax(timestampValue);
			
			mpNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestampValue);
		}
		catch (ParseException e) {
			debug(e.getMessage());
			return null;
		}
		
		index(mpNode);
		findOrCreateFileNode(mpNode);
		
		if (previousMpNode != null) {
			previousMpNode.createRelationshipTo(mpNode, GeoNeoRelationshipTypes.NEXT);
		}
		previousMpNode = mpNode;
		previousMsNode = null;
		
		return mpNode;
	}
	
	/**
	 * Calculates list of files to import
	 *
	 * @param directoryName directory to import
	 * @return list of files to import
	 */
	private ArrayList<File> getAllLogFilePathes(String directoryName) {
		File directory = new File(directoryName);
		ArrayList<File> result = new ArrayList<File>();
		
		for (File childFile : directory.listFiles()) {
			if (childFile.isDirectory()) {
				result.addAll(getAllLogFilePathes(childFile.getAbsolutePath()));
			}
			else if (childFile.isFile() &&
					 childFile.getName().endsWith(ETSI_LOG_FILE_EXTENSION)) {
				result.add(childFile);
			}
		}
		
		return result;
		
	}
	
	/**
	 * Add Timestamp index
	 */
	private void addDriveIndexes() {
        try {
            addIndex(new MultiPropertyIndex<Long>(INeoConstants.TIMESTAMP_INDEX_NAME + dataset, new String[] {INeoConstants.PROPERTY_TIMESTAMP_NAME},
                    new MultiTimeIndexConverter(), 10));            
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }	
}
