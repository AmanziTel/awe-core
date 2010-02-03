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
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.CallProperties;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.enums.CallProperties.CallDirection;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.loader.etsi.commands.AbstractETSICommand;
import org.amanzi.neo.loader.etsi.commands.CommandSyntax;
import org.amanzi.neo.loader.etsi.commands.ETSICommandPackage;
import org.amanzi.neo.loader.etsi.commands.PESQ;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.RelationshipType;
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
	

    /**
     * Class that calculates a general parameters of Call
     * 
     * @author Lagutko_N
     * @since 1.0.0
     */
	private class Call {
		
	    /*
	     * List of Duration Parameters
	     */
		private ArrayList<Long> callParameters = new ArrayList<Long>();
		
		/*
		 * Index of Call Setup Begin timestamp parameter 
		 */
		private int callSetupBegin = 0;
		
		/*
		 * Index of Call Setup End timestamp parameter
		 */
		private int callSetupEnd = 1;
		
		/*
		 * Index of Call Termination Begin timestamp parameter
		 */
        private int callTerminationBegin = 2;
		
        /*
         * Index of Call Termination End timestamp parameter
         */
		private int callTerminationEnd = 3;
		
		/*
		 * Index of last processed parameter
		 */
		private int lastProccessedParameter;
		
		/*
		 * Type of Call
		 */
		private CallProperties.CallType callType;
        
		/*
		 * Direction of Call
		 */
        private CallProperties.CallDirection callDirection;
		
        /*
         * List of Nodes that creates this call
         */
		private ArrayList<Node> relatedNodes = new ArrayList<Node>();
		
		/*
		 * Listening quality
		 */
		private float lq;
		
		/*
		 * Audio delay
		 */
		private float delay;
		
		/**
		 * Default constructor
		 * 
		 * Sets zeros to timestamps
		 */
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
        public CallProperties.CallType getCallType() {
			return callType;
		}

		/**
		 * @param callType The callType to set.
		 */
        public void setCallType(CallProperties.CallType callType) {
			this.callType = callType;
		}

		/**
		 * @return Returns the callDirection.
		 */
        public CallProperties.CallDirection getCallDirection() {
			return callDirection;
		}

		/**
		 * @param callDirection The callDirection to set.
		 */
        public void setCallDirection(CallProperties.CallDirection callDirection) {
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
		
		/**
		 * Handles error
		 *
		 * @param timestamp
		 */
		public void error(long timestamp) {
			switch (lastProccessedParameter) {
			case 0: 
			case 2:
			    //if an error was on beginning of operation than set an end time as time of error
				callParameters.set(lastProccessedParameter + 1, timestamp);
				break;
			}
		}

        /**
         * @return Returns the lq.
         */
        public float getLq() {
            return lq;
        }

        /**
         * @param lq The lq to set.
         */
        public void setLq(float lq) {
            this.lq = lq;
        }

        /**
         * @return Returns the delay.
         */
        public float getDelay() {
            return delay;
        }

        /**
         * @param delay The delay to set.
         */
        public void setDelay(float delay) {
            this.delay = delay;
        }
		
	}
	
	/**
	 * Enum that describes type of Event for Call 
	 * 
	 * @author Lagutko_N
	 * @since 1.0.0
	 */
	private enum CallEvents {
		OUTGOING_CALL_SETUP_BEGIN("atd"),
		OUTGOING_CALL_SETUP_END("CTOCP"),
		OUTGOING_CALL_TERMINATION_BEGIN("ATH"),
		TERMINATION_END("CTCR"),
		INCOMING_CALL_SETUP_BEGIN("ATA"),
		INCOMING_CALL_SETUP_END("CTCC"),
		ERROR("CME ERROR"),
		PESQ("PESQ");
		
		/*
		 * Name of Command that causes this Event
		 */
		private String commandName;
		
		private CallEvents(String commandName) {
			this.commandName = commandName;
		}
		
		/**
		 * Returns CallEvent by command name
		 *
		 * @param commandName
		 * @return
		 */
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
	 * Name of UNSOLICITED command
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
	 * Header Index for Real Dataset
	 */
	private static final int REAL_DATASET_HEADER_INDEX = 0;
	
	/*
	 * Header Index for Call Dataset
	 */
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
	
	/*
	 * Call that should be created by log information
	 */
	private Call call;
	
	/*
	 * Cache of probes
	 */
	private HashMap<String, Pair<Node, Node>> probesCache = new HashMap<String, Pair<Node, Node>>();
	
	/*
	 * Currently processed ProbeCalls Node
	 */
	private Node currentProbeCalls;
	

    // private Node lastCallInProbe;

    private Node callDataset;
	
    /*
     * Last call in Real Dataset
     */
	private Node lastCallInDataset;
	
	/*
	 * Name of Probes Network
	 */
	private String networkName;
	
	/*
	 * Timestamp Index for Calls
	 */
	private HashMap<String, MultiPropertyIndex<Long>> callTimestampIndexes = new HashMap<String, MultiPropertyIndex<Long>>();
	
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
		basename = oldBasename;
	}
	
	/**
	 * Initializes a Call dataset
	 *
	 * @param datasetName name of Real dataset
	 */
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
            currentDirectoryNode = findOrCreateDirectoryNode(logFile.getParentFile());
			
			newFile = true;
            headersMap.clear();
            getHeaderMap(REAL_DATASET_HEADER_INDEX).typedProperties = null;
            getHeaderMap(CALL_DATASET_HEADER_INDEX).typedProperties = null;

			super.run(null);
			
			monitor.worked(1);
			
			updateProbeCache(probeName);
		}
		
		cleanupGisNode();
		finishUpGis(getDatasetNode());
		
		basename = dataset;
		printStats(false);
	}
	
	/**
	 * Updates cache for probes
	 *
	 * @param probeName
	 */
	private void updateProbeCache(String probeName) {        
        probesCache.put(probeName, new Pair<Node, Node>(currentProbeCalls, null));
	}
	
	/**
	 * Initializes Probe Node by filename
	 *
	 * @param logFile
	 * @return
	 */
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
			getProbeCallsIndex(NeoUtils.getNodeName(currentProbeCalls));
		}
		else {
			currentProbeCalls = probeNodes.getLeft();
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
    private Node findOrCreateDirectoryNode(File directoryFile) {
		Transaction tx = neo.beginTx();
        Exception ex = null;
		try {			
			String directoryPath = directoryFile.getPath();
            int ind = directoryPath.lastIndexOf(directoryName) + directoryName.length();
            // ind = directoryPath.indexOf(File.separator, ind);
            String createPath = directoryPath.substring(ind + 1);
            StringTokenizer st = new StringTokenizer(createPath, File.separator);
            LinkedList<String> childs = new LinkedList<String>();
            while (st.hasMoreTokens()) {
                String child = st.nextToken();
                childs.add(child);
            }
            return createDirPath(getDatasetNode(), childs);
		}
		catch (Exception e) {
            e.printStackTrace();
            ex = e;
			NeoLoaderPlugin.exception(e);
			tx.failure();
			return null;
		}
		finally {
            if (ex == null) {
                tx.success();
            }
			tx.finish();
		}		
	}

    /**
     * Create path
     * 
     * @param parentNode - parent node
     * @param createPath - list of chield (path(2) is child of path(1))
     * @return last subnode
     */
    private Node createDirPath(Node parentNode, LinkedList<String> path) {
        if (path.isEmpty()) {
            return parentNode;
        }
        String child = path.removeFirst();
        Pair<Boolean, Node> subDir = NeoUtils.findOrCreateChildNode(neo, parentNode, child);
        if (subDir.getLeft()) {
            Node directoryNode = subDir.getRight();
            directoryNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.DIRECTORY_TYPE_NAME);
            directoryNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, child);
        }
        return createDirPath(subDir.getRight(), path);
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
            file = NeoUtils.findOrCreateFileNode(neo, currentDirectoryNode, basename, filename).getRight();
            file.createRelationshipTo(mp, GeoNeoRelationshipTypes.CHILD);
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
		String maybePESQ = tokenizer.nextToken();
		if (processPESQCommand(timestamp, maybePESQ, tokenizer)) {
		    return;
		}
		
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
	 * Tries to process PESQ command
	 *
	 * @param timestamp timestamp of command
	 * @param commandName name of command
	 * @param tokenizer tokens of paramters
	 * @return true if it was PESQ command, false otherwise
	 */
	private boolean processPESQCommand(String timestamp, String commandName, StringTokenizer tokenizer) {
	    CommandSyntax syntax = ETSICommandPackage.getCommandSyntax(commandName);
	    AbstractETSICommand command = ETSICommandPackage.getCommand(commandName, syntax);
	    
	    if (command != null) {
	        processCommand(timestamp, command, syntax, tokenizer, false);
	        
	        return true;
	    }
	    
	    return false;
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
	
	/**
	 * Sets a property to node and updates statistics
	 *
	 * @param headers statistics headers to update
	 * @param node node to set property
	 * @param propertyName name of property to set
	 * @param value value of property
	 */
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
		
        updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
			
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
	
	/**
	 * Returns an index for Probe Calls
	 *
	 * @param probeCallsName name of Probe Calls
	 * @return timestamp index of Probe Calls
	 */
	private MultiPropertyIndex<Long> getProbeCallsIndex(String probeCallsName) {
	    MultiPropertyIndex<Long> result = callTimestampIndexes.get(probeCallsName);
	    
	    if (result == null) {
	        Transaction tx = neo.beginTx();
	        try {
	            result = NeoUtils.getTimeIndexProperty(probeCallsName);	            
	            result.initialize(neo, null);
	            
	            callTimestampIndexes.put(probeCallsName, result);
	            tx.success();
	        } catch (IOException e) {
	            tx.failure();
	            throw (RuntimeException)new RuntimeException().initCause(e);
	        }
	        finally {
	            tx.finish();
	        }
	    }
	    
	    return result;
	}
	
	/**
	 * Processes parsed Command
	 *
	 * @param timestamp timestamp of command
	 * @param command a command
	 * @param syntax syntax of command
	 * @param tokenizer tokenizer with parameters of command
	 * @param callCommandResult is this command part of call result 
	 * @return is this command was a Call Command
	 */
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
	
	/**
	 * Processes CallEvent
	 *
	 * @param relatedNode node that occure this event
	 * @param event type of event
	 * @param timestamp timestamp of event
	 */
	private void processCallEvent(Node relatedNode, CallEvents event, long timestamp) {
		switch (event) {
		case OUTGOING_CALL_SETUP_BEGIN:
			call = new Call();
            call.setCallDirection(CallProperties.CallDirection.OUTGOING);
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
                call.setCallType(CallProperties.CallType.SUCCESS);
				call.addRelatedNode(relatedNode);
				saveCall();
			}
			break;
		case INCOMING_CALL_SETUP_BEGIN:
			call = new Call();
			call.setCallSetupBeginTime(timestamp);
            call.setCallDirection(CallProperties.CallDirection.INCOMING);
			break;
		case INCOMING_CALL_SETUP_END:
			if (call != null) {
				call.setCallSetupEndTime(timestamp);
			}
			break;
		case ERROR:
			if (call != null) {
				call.error(timestamp);
                call.setCallType(CallProperties.CallType.FAILURE);
				call.setCallTerminationEnd(timestamp);
				call.addRelatedNode(relatedNode);
				saveCall();
			}
		case PESQ:
		    if (call != null) {
		        float lq = (Float)relatedNode.getProperty(PESQ.PESQ_LISTENING_QUALITIY);
		        call.setLq(lq);
		        
		        float delay = (Float)relatedNode.getProperty(PESQ.ESTIMATED_DELAY);
		        call.setDelay(delay);
		    }
		default:
			return;
		}
		if (call != null) {
			call.addRelatedNode(relatedNode);
		}
	}
	
	/**
	 * Creates a Call node and sets properties
	 */
	private void saveCall() {
		if (call != null) {
			Node callNode = createCallNode(call.getCallSetupBegin(), call.getRelatedNodes());
			
			long setupDuration = call.getCallSetupEnd() - call.getCallSetupBegin();
			long terminationDuration = call.getCallTerminationEnd() - call.getCallTerminationBegin();
			long callDuration = call.getCallTerminationEnd() - call.getCallSetupBegin();
			
			LinkedHashMap<String, Header> headers = getHeaderMap(CALL_DATASET_HEADER_INDEX).headers;
			
            setProperty(headers, callNode, CallProperties.SETUP_DURATION.getId(), setupDuration);
			setProperty(headers, callNode, CallProperties.CALL_TYPE.getId(), call.getCallType().toString());
			setProperty(headers, callNode, CallProperties.CALL_DIRECTION.getId(), call.getCallDirection().toString());
            setProperty(headers, callNode, CallProperties.CALL_DURATION.getId(), callDuration);
            setProperty(headers, callNode, CallProperties.LQ.getId(), call.getLq());
            setProperty(headers, callNode, CallProperties.DELAY.getId(), call.getDelay());
			
            if (call.getCallDirection() == CallProperties.CallDirection.OUTGOING) {
                setProperty(headers, callNode, CallProperties.TERMINATION_DURATION.getId(), terminationDuration);
			}
            
            RelationshipType relation = call.getCallDirection() == CallDirection.OUTGOING ? ProbeCallRelationshipType.CALLER : ProbeCallRelationshipType.CALLEE;
            callNode.createRelationshipTo(currentProbeCalls, relation);
			call = null;
		}
	}

	/**
	 * Creates new Call Node
	 *
	 * @param timestamp timestamp of Call
	 * @param relatedNodes list of M node that creates this call
	 * @return created Node
	 */
	private Node createCallNode(long timestamp, ArrayList<Node> relatedNodes) {
		Transaction transaction = neo.beginTx();
		Node result = null;
		try {
			result = neo.createNode();
			result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.CALL_TYPE_NAME);
			result.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(CALL_DATASET_HEADER_INDEX, timestamp);
			index(result);
			
			//index for Probe Calls
			MultiPropertyIndex<Long> callIndex = getProbeCallsIndex(NeoUtils.getNodeName(currentProbeCalls));
			callIndex.add(result);
			
			//create relationship to M node
			for (Node mNode : relatedNodes) {
				result.createRelationshipTo(mNode, ProbeCallRelationshipType.CALL_M);
			}			

			//create relationship to Dataset Calls
			if (lastCallInDataset == null) {
                callDataset.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
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
            return gisNodes.get(dataset).getGis();
    	case CALL_DATASET_HEADER_INDEX:
            return gisNodes.get(DriveTypes.AMS_CALLS.getFullDatasetName(dataset)).getGis();
    	default:
    		return null;    			
    	}
    }
    
    @Override
    protected boolean needParceHeaders() {
    	return false;
    }
    
    @Override
    protected void finishUpIndexes() {
        for (MultiPropertyIndex<Long> singleIndex : callTimestampIndexes.values()) {
            singleIndex.finishUp();
        }
        super.finishUpIndexes();
    }
    
    @Override
    protected ArrayList<Node> getGisNodes() {
        ArrayList<Node> result = new ArrayList<Node>();
        
        Iterator<Node> gisNodes = datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return NeoUtils.isGisNode(currentPos.currentNode());
            }
        }, GeoNeoRelationshipTypes.CHILD, Direction.INCOMING,
           GeoNeoRelationshipTypes.VIRTUAL_DATASET, Direction.OUTGOING).iterator();
        
        while (gisNodes.hasNext()) {
            result.add(gisNodes.next());            
        }
        
        return result;
    }
}
