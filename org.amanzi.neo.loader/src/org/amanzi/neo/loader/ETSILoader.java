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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.etsi.commands.AbstractETSICommand;
import org.amanzi.neo.loader.etsi.commands.CommandSyntax;
import org.amanzi.neo.loader.etsi.commands.ETSICommandPackage;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * Loader of ETSI data
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ETSILoader extends DriveLoader {
	
	public enum CallType {
		SUCCESS, FAILURE;
	}
	
	public enum CallDirection {
		INCOMING, OUTGOING;
	}
	
	private class Call {
		
		private ArrayList<Long> callParameters = new ArrayList<Long>();
		
		private int callSetupBegin = 0;
		
		private int callSetupEnd = 1;
		
		private CallType callType;
		
		private CallDirection callDirection;
		
		private int callTerminationBegin = 2;
		
		private int callTerminationEnd = 3;
		
		private int lastProccessedParameter;
		
		private ArrayList<Node> relatedNodes = new ArrayList<Node>();
		
		public Call() {
			callParameters.add(0l);
			callParameters.add(0l);
			callParameters.add(0l);
			callParameters.add(0l);
		}

		/**
		 * @return Returns the callBeginTime.
		 */
		public long getCallSetupBegin() {
			return callParameters.get(callSetupBegin);
		}

		/**
		 * @param callSetupBeginTime The callBeginTime to set.
		 */
		public void setCallSetupBeginTime(long callSetupBeginTime) {
			callParameters.set(this.callSetupBegin, callSetupBeginTime);
			lastProccessedParameter = this.callSetupBegin;
		}

		/**
		 * @return Returns the callEndTime.
		 */
		public long getCallSetupEnd() {
			return callParameters.get(callSetupEnd);
		}

		/**
		 * @param callSetupEndTime The callEndTime to set.
		 */
		public void setCallSetupEndTime(long callSetupEndTime) {
			callParameters.set(this.callSetupEnd, callSetupEndTime);
			lastProccessedParameter = this.callSetupEnd;
		}

		public void addRelatedNode(Node mNode) {
			relatedNodes.add(mNode);
		}
		
		public ArrayList<Node> getRelatedNodes() {
			return relatedNodes;
		}

		/**
		 * @return Returns the callType.
		 */
		public CallType getCallType() {
			return callType;
		}

		/**
		 * @param callType The callType to set.
		 */
		public void setCallType(CallType callType) {
			this.callType = callType;
		}

		/**
		 * @return Returns the callDirection.
		 */
		public CallDirection getCallDirection() {
			return callDirection;
		}

		/**
		 * @param callDirection The callDirection to set.
		 */
		public void setCallDirection(CallDirection callDirection) {
			this.callDirection = callDirection;
		}

		/**
		 * @return Returns the callTerminationBegin.
		 */
		public long getCallTerminationBegin() {
			return callParameters.get(callTerminationBegin);
		}

		/**
		 * @param callTerminationBegin The callTerminationBegin to set.
		 */
		public void setCallTerminationBegin(long callTerminationBegin) {
			callParameters.set(this.callTerminationBegin, callTerminationBegin);
			lastProccessedParameter = this.callTerminationBegin;
		}

		/**
		 * @return Returns the callTerminationEnd.
		 */
		public long getCallTerminationEnd() {
			return callParameters.get(callTerminationEnd);
		}

		/**
		 * @param callTerminationEnd The callTerminationEnd to set.
		 */
		public void setCallTerminationEnd(long callTerminationEnd) {
			callParameters.set(this.callTerminationEnd, callTerminationEnd);
			lastProccessedParameter = this.callTerminationEnd;
		}
		
		public void error(long timestamp) {
			switch (lastProccessedParameter) {
			case 0: 
			case 2:
				callParameters.set(lastProccessedParameter + 1, timestamp);
				break;
			}
		}
		
	}
	
	private enum CallEvents {
		OUTGOING_CALL_SETUP_BEGIN("atd"),
		OUTGOING_CALL_SETUP_END("CTOCP"),
		OUTGOING_CALL_TERMINATION_BEGIN("ATH"),
		TERMINATION_END("CTCR"),
		INCOMING_CALL_SETUP_BEGIN("ATA"),
		INCOMING_CALL_SETUP_END("CTCC"),
		ERROR("CME ERROR");
		
		
		private String commandName;
		
		private CallEvents(String commandName) {
			this.commandName = commandName;
		}
		
		public static CallEvents getCallEvent(String commandName) {
			for (CallEvents event : values()) {
				if (event.commandName.equals(commandName)) {
					return event;
				}
			}
			
			return null;
		}
	}
	
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
	
	private static final int REAL_DATASET_HEADER_INDEX = 0;
	
	private static final int CALL_DATASET_HEADER_INDEX = 1;
	
	/*
	 * Formatter for timestamp
	 */
	private SimpleDateFormat timestampFormat;
	
	/*
	 * Previous M Node
	 */
	private Node previousMNode;
	
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
	
	/*
	 * Network node for Probes data
	 */
	private Node networkNode;
	
	/*
	 * Name of directory
	 */
	private String directoryName;
	
	/*
	 * Node for current directory
	 */
	private Node currentDirectoryNode;
	
	private Call call;
	
	private HashMap<String, Pair<Node, Node>> probesCache = new HashMap<String, Pair<Node, Node>>();
	
	private Node currentProbeCalls;
	
	private Node lastCallInProbe;

    private Node callDataset;
	
	private Node lastCallInDataset;
	
	private String networkName;
	
	/**
	 * Creates a loader
	 * 
	 * @param directoryName name of directory to import
	 * @param display
	 * @param datasetName name of dataset
	 */
	public ETSILoader(String directoryName, Display display, String datasetName, String networkName) {
        driveType = DriveTypes.AMS;
        if (datasetName == null) {
			int startIndex = directoryName.lastIndexOf(File.separator);
			if (startIndex < 0) {
				startIndex = 0;
			}
			else {
				startIndex++;
			}
			datasetName = directoryName.substring(startIndex);
		}
		
        
		this.directoryName = directoryName;
		this.filename = directoryName;		
		this.networkName = networkName;
		
		initialize("ETSI", null, directoryName, display, datasetName);
		
		addDriveIndexes();
		
		timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
	}
	
	/**
	 * Initializes Network for probes
	 *
	 * @param networkName name of network
	 */
	private void initializeNetwork(String networkName) {
		String oldBasename = basename;
		if ((networkName == null) || (networkName.length() == 0)) {
			networkName = basename + " Probes";
		}
		else {
			networkName = networkName.trim();
		}
		
		basename = networkName;
		this.networkNode = findOrCreateNetworkNode(null, true);			
		findOrCreateGISNode(this.networkNode, GisTypes.NETWORK.getHeader());
		gis = null;		
		basename = oldBasename;
	}
	
	private void initializeDatasets(String datasetName) {
		Transaction tx = neo.beginTx();
		try {
			datasetNode = findOrCreateDatasetNode(neo.getReferenceNode(), dataset);
			findOrCreateGISNode(datasetNode, GisTypes.DRIVE.getHeader());
			
			callDataset = getVirtualDataset(DriveTypes.AMS_CALLS);
			
			tx.success();
		}
		catch (Exception e) {
			tx.failure();
			NeoCorePlugin.error(null, e);
			throw new RuntimeException(e);
		}
		finally {
			tx.finish();
		}
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws IOException {
		monitor.beginTask("Loading ETSI data", 2);
		monitor.subTask("Searching for files to load");
		ArrayList<File> allFiles = getAllLogFilePathes(filename);
		
		monitor = SubMonitor.convert(monitor, allFiles.size());
		monitor.beginTask("Loading ETSI data", allFiles.size());
        
        initializeNetwork(networkName);
		initializeDatasets(dataset);
        
		for (File logFile : allFiles) {
			monitor.subTask("Loading file " + logFile.getAbsolutePath());
			
			String probeName = initializeProbeNodes(logFile);
			filename = logFile.getAbsolutePath();
			currentDirectoryNode = findOrCreateDirectoryNode(null, logFile.getParentFile());
			
			newFile = true;
            getHeaderMap(1).typedProperties = null;

			super.run(null);
			
			monitor.worked(1);
			
			updateProbeCache(probeName);
		}
		
		cleanupGisNode();
		finishUpGis(getDatasetNode());
		
		basename = dataset;
		printStats(false);
	}
	
	private void updateProbeCache(String probeName) {
		Transaction tx = neo.beginTx();
		try {
			if (lastCallInProbe != null) {
				currentProbeCalls.setProperty(INeoConstants.LAST_CALL_NODE_ID_PROPERTY_NAME, lastCallInProbe.getId());
			}
			tx.success();
		}
		finally {
			tx.finish();
		}
		probesCache.put(probeName, new Pair<Node, Node>(currentProbeCalls, lastCallInProbe));
	}
	
	private String initializeProbeNodes(File logFile) {
		String fileName = logFile.getName();
		int index = fileName.indexOf("#");
		String probeName = fileName;
		if (index > -1) {
			probeName = probeName.substring(0, index);
		}
		
		Pair<Node, Node> probeNodes = probesCache.get(probeName);
		if (probeNodes == null) {
			Node probeNode = NeoUtils.findOrCreateProbeNode(networkNode, probeName, neo);
			currentProbeCalls = NeoUtils.getCallsNode(callDataset, probeName, probeNode, neo);
			lastCallInProbe = NeoUtils.getLastCallFromProbeCalls(currentProbeCalls, neo);
		}
		else {
			currentProbeCalls = probeNodes.getLeft();
			lastCallInProbe = probeNodes.getRight();
		}
		
		return probeName;
	}
	
	/**
	 * Searches for Directory node and creates it if it cannot be found
	 *
	 * @param parentDirectoryNode node of parent directory
	 * @param directoryFile file of Directory
	 * @return founded directory node
	 */
	private Node findOrCreateDirectoryNode(Node parentDirectoryNode, File directoryFile) {
		Transaction tx = neo.beginTx();
		try {			
			final String directoryName = directoryFile.getName();
			String directoryPath = directoryFile.getPath();
			
			if (!directoryPath.equals(this.directoryName)) {
				parentDirectoryNode = findOrCreateDirectoryNode(parentDirectoryNode, directoryFile.getParentFile());
			}
		
			if (parentDirectoryNode == null) {
				parentDirectoryNode = getDatasetNode();
				if (parentDirectoryNode == null) {
					parentDirectoryNode = datasetNode;
				}
			}
		
			Iterator<Node> directoryIterator = parentDirectoryNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
			
				@Override
				public boolean isReturnableNode(TraversalPosition currentPos) {
					return currentPos.currentNode().hasProperty(INeoConstants.PROPERTY_TYPE_NAME) &&
						   currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(INeoConstants.DIRECTORY_TYPE_NAME) &&
						   currentPos.currentNode().hasProperty(INeoConstants.PROPERTY_NAME_NAME) &&
						   currentPos.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(directoryName);
				}
			}, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
		
			if (directoryIterator.hasNext()) {
				tx.success();
				return directoryIterator.next();
			}
			else {
				Node directoryNode = neo.createNode();
				directoryNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.DIRECTORY_TYPE_NAME);
				directoryNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, directoryName);
				directoryNode.setProperty(INeoConstants.PROPERTY_FILENAME_NAME, directoryPath);
				
				parentDirectoryNode.createRelationshipTo(directoryNode, GeoNeoRelationshipTypes.NEXT);
				
				tx.success();
				return directoryNode;
			}
		}
		catch (Exception e) {
			NeoLoaderPlugin.exception(e);
			tx.failure();
			return null;
		}
		finally {
			tx.finish();
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
		
		//we should load all files #1, #2 and #3 in a single file node
		if (newFile) {
			String tempName = filename.substring(startIndex, filename.lastIndexOf("#"));
			if ((currentFileName == null) || (!currentFileName.equals(tempName))) {
				currentFileName = tempName;
				filename = tempName;
				basename = filename;
				file = null;
				previousMNode = null;
			}
			newFile = false;
		}
		
		if (file == null) {
			Iterator<Node> files = currentDirectoryNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
				
				@Override
				public boolean isReturnableNode(TraversalPosition currentPos) {
					return currentPos.currentNode().hasProperty(INeoConstants.PROPERTY_TYPE_NAME) &&
						   currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(INeoConstants.FILE_TYPE_NAME) &&
						   currentPos.currentNode().hasProperty(INeoConstants.PROPERTY_NAME_NAME) &&
						   currentPos.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(basename);
				}
			}, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
			
			if (files.hasNext()) {
				file = files.next();
			}
			else {
				file = neo.createNode();
				file.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.FILE_TYPE_NAME);
				file.setProperty(INeoConstants.PROPERTY_NAME_NAME, basename);
				file.setProperty(INeoConstants.PROPERTY_FILENAME_NAME, filename);
				currentDirectoryNode.createRelationshipTo(file, GeoNeoRelationshipTypes.NEXT);
			}
			file.createRelationshipTo(mp, GeoNeoRelationshipTypes.NEXT);
		}		
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
			
			boolean isCallCommand = processCommand(timestamp, command, syntax, tokenizer, false);
			if (command != null) {
				commandName = command.getName();
			}
			else {
				commandName = ETSICommandPackage.getRealCommandName(commandName);
			}
					
			if (syntax == CommandSyntax.EXECUTE) {
				while (tokenizer.hasMoreTokens()) {
					String maybeTimestamp = tokenizer.nextToken();						
					if (maybeTimestamp.startsWith("~")) {
						timestamp = maybeTimestamp.substring(1);						
					}
					else if (maybeTimestamp.startsWith("+")) {
						int colonIndex = maybeTimestamp.indexOf(":");
						commandName = maybeTimestamp.substring(1, colonIndex);
						StringTokenizer paramTokenizer = new StringTokenizer(maybeTimestamp.substring(colonIndex + 1).trim());
						syntax = ETSICommandPackage.getCommandSyntax(commandName);
						command = ETSICommandPackage.getCommand(commandName, syntax);
					
						if (command != null) {
							//should be a result of command
							processCommand(timestamp, command, syntax, paramTokenizer, isCallCommand);
							if (command != null) {
								commandName = command.getName();
							}
							else {
								commandName = ETSICommandPackage.getRealCommandName(commandName);
							}
						}
					}
				}	
			}
		}
	}
	
	/**
	 * Creates Ms Node
	 *
	 * @param mNode parent m node
	 * @param commandName name of command
	 * @param parameters parameters of command
	 */
	private void updateMNode(Node mNode, String commandName, HashMap<String, Object> parameters) {
		LinkedHashMap<String, Header> headers = getHeaderMap(REAL_DATASET_HEADER_INDEX).headers;
		mNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, commandName);
		if (parameters != null) {
			for (String name : parameters.keySet()) {
				Object value = parameters.get(name);
				if (!(value instanceof List<?>)) {
				    setProperty(headers, mNode, name, parameters.get(name));
				}
				else {
					setPropertyToMmNodes(mNode, name, (List<?>)value);
				}
			}
		}
		
		mmNodes.clear();
	}
	
	private void setProperty(LinkedHashMap<String, Header> headers, Node node, String propertyName, Object value) {
	    //add value to statistics
        Header header = headers.get(propertyName);
        if (header == null) {
            header = new Header(propertyName, propertyName, 1);
            
            headers.put(propertyName, header);
        }
        header.parseCount++;
        header.incValue(value);
        header.incType(value.getClass());
        
        node.setProperty(propertyName, value);
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
	private Node createMNode(long timestamp) {
		Node mNode = neo.createNode();
		mNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.HEADER_M);
		
		updateTimestampMinMax(timestamp);
			
		mNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
		
		index(mNode);
		findOrCreateFileNode(mNode);
		
		if (previousMNode != null) {
			previousMNode.createRelationshipTo(mNode, GeoNeoRelationshipTypes.NEXT);
		}
		previousMNode = mNode;
		
		return mNode;
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
            addIndex(INeoConstants.HEADER_M, NeoUtils.getTimeIndexProperty(dataset));
            addIndex(INeoConstants.CALL_TYPE_NAME, NeoUtils.getTimeIndexProperty(DriveTypes.AMS_CALLS.getFullDatasetName(dataset)));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
	
	private boolean processCommand(String timestamp, AbstractETSICommand command, CommandSyntax syntax, StringTokenizer tokenizer, boolean callCommandResult) {
		//try to parse timestamp
		long timestampValue;
		try {
			timestampValue = timestampFormat.parse(timestamp).getTime();
		}
		catch (ParseException e) {
			error(e.getMessage());
			return false;
		}
		
		boolean isCallCommand = command.isCallCommand();
		
		HashMap<String, Object> result = command.getResults(syntax, tokenizer);
		
		Node mNode = createMNode(timestampValue);
		
		if (isCallCommand) {
			String commandName = command.getName();
			
			CallEvents event = CallEvents.getCallEvent(commandName);
			
			boolean processEvent = (event == CallEvents.ERROR) ? callCommandResult : true;
			//do not proccess event if it's error for not Call Command result
			if (processEvent) {
				processCallEvent(mNode, event, timestampValue);
			}
		}
		
		if (!command.getName().equals(UNSOLICITED)) {
			updateMNode(mNode, command.getName(), result);
		}
		
		return isCallCommand;
	}
	
	private void processCallEvent(Node relatedNode, CallEvents event, long timestamp) {
		switch (event) {
		case OUTGOING_CALL_SETUP_BEGIN:
			call = new Call();
			call.setCallDirection(CallDirection.OUTGOING);
			call.setCallSetupBeginTime(timestamp);			
			break;
		case OUTGOING_CALL_SETUP_END:
			if (call != null) {
				call.setCallSetupEndTime(timestamp);
			}
			break;
		case OUTGOING_CALL_TERMINATION_BEGIN:
			if (call != null) {
				call.setCallTerminationBegin(timestamp);				
			}
			break;
		case TERMINATION_END:
			if (call != null) {
				call.setCallTerminationEnd(timestamp);
				call.setCallType(CallType.SUCCESS);
				call.addRelatedNode(relatedNode);
				saveCall();
			}
			break;
		case INCOMING_CALL_SETUP_BEGIN:
			call = new Call();
			call.setCallSetupBeginTime(timestamp);
			call.setCallDirection(CallDirection.INCOMING);
			break;
		case INCOMING_CALL_SETUP_END:
			if (call != null) {
				call.setCallSetupEndTime(timestamp);
			}
			break;
		case ERROR:
			if (call != null) {
				call.error(timestamp);
				call.setCallType(CallType.FAILURE);
				call.setCallTerminationEnd(timestamp);
				call.addRelatedNode(relatedNode);
				saveCall();
			}
		default:
			return;
		}
		if (call != null) {
			call.addRelatedNode(relatedNode);
		}
	}
	
	private void saveCall() {
		if (call != null) {
			Node callNode = createCallNode(call.getCallSetupBegin(), call.getRelatedNodes());
			
			long setupDuration = call.getCallSetupEnd() - call.getCallSetupBegin();
			long terminationDuration = call.getCallTerminationEnd() - call.getCallTerminationBegin();
			
			LinkedHashMap<String, Header> headers = getHeaderMap(CALL_DATASET_HEADER_INDEX).headers;
			
			callNode.setProperty(INeoConstants.CALL_SETUP_DURATION, setupDuration);
			setProperty(headers, callNode, INeoConstants.CALL_SETUP_DURATION, setupDuration);
			
			callNode.setProperty(INeoConstants.CALL_TYPE, call.getCallType().toString());
			setProperty(headers, callNode, INeoConstants.CALL_TYPE, call.getCallType().toString());
			
			callNode.setProperty(INeoConstants.CALL_DIRECTION, call.getCallDirection().toString());
			setProperty(headers, callNode, INeoConstants.CALL_DIRECTION, call.getCallDirection().toString());
			
			if (call.getCallDirection() == CallDirection.OUTGOING) {
				callNode.setProperty(INeoConstants.CALL_TERMINATION, terminationDuration);
				setProperty(headers, callNode, INeoConstants.CALL_TERMINATION, call.getCallDirection().toString());
			}
			
			call = null;
		}
	}

	private Node createCallNode(long timestamp, ArrayList<Node> relatedNodes) {
		Transaction transaction = neo.beginTx();
		Node result = null;
		try {
			result = neo.createNode();
			result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.CALL_TYPE_NAME);
			result.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
			
			index(result);
			
			//create relationship to M node
			for (Node mNode : relatedNodes) {
				result.createRelationshipTo(mNode, GeoNeoRelationshipTypes.CHILD);
			}
			
			//create relationship to Probe Calls
			if (lastCallInProbe != null) {
				lastCallInProbe.createRelationshipTo(result, ProbeCallRelationshipType.NEXT_CALL);
			}
			else {
				//create relationshiop to Probe Calls node
				currentProbeCalls.createRelationshipTo(result, ProbeCallRelationshipType.NEXT_CALL);				
			}
			lastCallInProbe = result;
			
			//create relationship to Dataset Calls
			if (lastCallInDataset == null) {
				callDataset.createRelationshipTo(result, GeoNeoRelationshipTypes.NEXT);
			}
			else {
				lastCallInDataset.createRelationshipTo(result, GeoNeoRelationshipTypes.NEXT);
			}
			lastCallInDataset = result;
			
			transaction.success();
		}
		catch (Exception e) {
			NeoCorePlugin.error(null, e);
		}
		finally {
			transaction.finish();
		}
		
		return result;
	}

    @Override
    protected Node getStoringNode(Integer key) {
    	switch (key) {
    	case REAL_DATASET_HEADER_INDEX:
    		return datasetNode;
    	case CALL_DATASET_HEADER_INDEX:
    		return callDataset;
    	default:
    		return null;    			
    	}
    }
    
    @Override
    protected boolean needParceHeaders() {
    	return false;
    }
}
