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
import org.amanzi.neo.loader.etsi.commands.ETSICommandPackage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ETSILoader extends DriveLoader {
	
	private static final String TIMESTAMP_FORMAT = "HH:mm:ss,SSS";
	
	private SimpleDateFormat timestampFormat;
	
	private Node previousMpNode;
	
	private ArrayList<Node> mmNodes = new ArrayList<Node>();
	
	private String currentFileName;
	
	private boolean newFile = true;
	
	/**
	 * 
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
		for (File logFile : getAllLogFilePathes(filename)) {
			filename = logFile.getAbsolutePath();
			newFile = true;
			typedProperties = null;
	
			super.run(monitor);
		}
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
		
		if (ETSICommandPackage.isETSICommand(commandName)) {
			//get a real name of command without set or get postfix
			Node mpNode = createMpNode(timestamp);
			if (mpNode != null) {			
				String realName = ETSICommandPackage.getRealCommandName(commandName);
				
				//parse parameters of command
				HashMap<String, Object> parameters = null;
				
				AbstractETSICommand command = ETSICommandPackage.getCommand(realName);
				if (command != null) {					
					parameters = command.getResults(tokenizer);
				}
				
				createMsNode(mpNode, realName, parameters);
			}
		}
	}
	
	private void createMsNode(Node mpNode, String commandName, HashMap<String, Object> parameters) {
		Node msNode = neo.createNode();
		msNode.setProperty("command", commandName);
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
	}
	
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
	
	private Node createMpNode(String timestamp) {
		Node mpNode = neo.createNode();
		mpNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.MP_TYPE_NAME);
		
		timestamp = timestamp.substring(timestamp.indexOf(" ") + 1, timestamp.length());
		try {
			long timestampValue = timestampFormat.parse(timestamp).getTime();
			
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
		
		return mpNode;
	}
	
	private ArrayList<File> getAllLogFilePathes(String directoryName) {
		File directory = new File(directoryName);
		ArrayList<File> result = new ArrayList<File>();
		
		for (File childFile : directory.listFiles()) {
			if (childFile.isDirectory()) {
				result.addAll(getAllLogFilePathes(childFile.getAbsolutePath()));
			}
			else if (childFile.isFile()) {
				result.add(childFile);
			}
		}
		
		return result;
		
	}
	
	private void addDriveIndexes() {
        try {
            addIndex(new MultiPropertyIndex<Long>(INeoConstants.TIMESTAMP_INDEX_NAME + dataset, new String[] {INeoConstants.PROPERTY_TIMESTAMP_NAME},
                    new MultiTimeIndexConverter(), 10));            
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

}
