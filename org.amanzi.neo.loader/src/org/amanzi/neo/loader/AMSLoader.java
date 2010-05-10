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
import java.util.Arrays;
import java.util.Date;
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
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.enums.CallProperties.CallResult;
import org.amanzi.neo.core.enums.CallProperties.CallType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.loader.ams.commands.AMSCommandPackage;
import org.amanzi.neo.loader.ams.commands.AbstractAMSCommand;
import org.amanzi.neo.loader.ams.commands.CCI;
import org.amanzi.neo.loader.ams.commands.CTSDC;
import org.amanzi.neo.loader.ams.commands.CommandSyntax;
import org.amanzi.neo.loader.ams.parameters.AMSCommandParameters;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;


/**
 * Loader of AMS data
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class AMSLoader extends DriveLoader {
    private static final Logger LOGGER = Logger.getLogger(AMSLoader.class);

    /**
     * Class that calculates a general parameters of Call
     * 
     * @author Lagutko_N
     * @since 1.0.0
     */
	public static class Call {
		
	    /*
	     * List of Duration Parameters
	     */
		private final ArrayList<Long> callParameters = new ArrayList<Long>();
		
		/*
		 * Index of Call Setup Begin timestamp parameter 
		 */
		private final int callSetupBegin = 0;
		
		/*
		 * Index of Call Setup End timestamp parameter
		 */
		private final int callSetupEnd = 1;
		
		/*
		 * Index of Call Termination Begin timestamp parameter
		 */
        private final int callTerminationBegin = 2;
		
        /*
         * Index of Call Termination End timestamp parameter
         */
		private final int callTerminationEnd = 3;
		
		/*
		 * Index of last processed parameter
		 */
		private int lastProccessedParameter;
		
		/*
		 * Type of Call
		 */
		private CallProperties.CallResult callResult;
        
		private CallType callType;
		
        /*
         * List of Nodes that creates this call
         */
		private final ArrayList<Node> relatedNodes = new ArrayList<Node>();
		
		private Node callerProbe;
		
		private final ArrayList<Node> calleeProbes = new ArrayList<Node>();
		
		/*
		 * Listening quality
		 */
		private float[] lq = new float[0];
		
		/*
		 * Audio delay
		 */
		private float[] delay = new float[0];
		
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
		 * @return Returns the callResult.
		 */
        public CallProperties.CallResult getCallResult() {
			return callResult;
		}

		/**
		 * @param callResult The callResult to set.
		 */
        public void setCallResult(CallProperties.CallResult callResult) {
			this.callResult = callResult;
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
			
			if ((getCallTerminationBegin() == 0) &&
			    (getCallSetupEnd() == 0)) {
			    setCallSetupEndTime(callTerminationEnd);
			}
			
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
        public float[] getLq() {
            return lq;
        }

        /**
         * @param lq The lq to set.
         */
        public void addLq(float lq) {
            this.lq = addToArray(this.lq, lq);
        }
        
        /**
         * Add new element to Array
         *
         * @param original original Array
         * @param value value to add
         * @return changed array
         */
        private float[] addToArray(float[] original, float value) {
            float[] result = new float[original.length + 1];
            result = Arrays.copyOf(this.lq, result.length);
            result[result.length - 1] = value;
            return result;
        }

        /**
         * @return Returns the delay.
         */
        public float[] getDelay() {
            return delay;
        }

        /**
         * @param delay The delay to set.
         */
        public void addDelay(float delay) {
            this.delay = addToArray(this.delay, delay);
        }

        /**
         * @return Returns the callerProbe.
         */
        public Node getCallerProbe() {
            return callerProbe;
        }

        /**
         * @param callerProbe The callerProbe to set.
         */
        public void setCallerProbe(Node callerProbe) {
            this.callerProbe = callerProbe;
        }

        /**
         * @return Returns the calleeProbes.
         */
        public ArrayList<Node> getCalleeProbes() {
            return calleeProbes;
        }
        
        public void addCalleeProbe(Node calleeProbe) {
            if (!calleeProbe.equals(callerProbe) && !calleeProbes.contains(calleeProbe)) {
                calleeProbes.add(calleeProbe);
            }
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
		
	}
	
	/**
	 * Enum that describes type of Event for Call 
	 * 
	 * @author Lagutko_N
	 * @since 1.0.0
	 */
	private enum CallEvents {
	    CALL_SETUP_BEGIN("AT+CTSDC"),		
	    CALL_TERMINATION_BEGIN("ATH"),
		TERMINATION_END("CTCR"),
		INCOMING_CALL_SETUP_BEGIN("ATA"),
		CALL_SETUP_END("CTCC"),
		ERROR("CME ERROR"),
		PESQ("PESQ"),
		OUTGOING_CALL("atd"),
		INCOMING_CALL("CTICN"),
		SEND_MESSAGE_BEGIN("AT+CMGS"),
	    SEND_MESSAGE_END("CTSDSR"),
	    MESS_SETUP_BEGIN("AT+CTSDS"),
	    MESS_ACKN_GET("CMGS"),
	    START_ITSI("AT+CSPTR"),
	    UPDATE_START_END("AT+CREG");
		
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
	 * AMS log file extension
	 */
	private static final String AMS_LOG_FILE_EXTENSION = ".log";

	/*
	 * Timestamp format for AMS log files
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
	
	protected static final String TIME_FORMAT = "HH:mm:ss";
	
	/*
	 * Formatter for timestamp
	 */
	private final SimpleDateFormat timestampFormat;
	
	/*
	 * Previous M Node
	 */
	private Node previousMNode;
	
	/*
	 * List of mm nodes
	 */
	private final ArrayList<Node> mmNodes = new ArrayList<Node>();
	
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
	private final String directoryName;
	
	/*
	 * Node for current directory
	 */
	private Node currentDirectoryNode;
	
	/*
	 * Cache of probes
	 */
	private final HashMap<String, Pair<Node, Node>> probesCache = new HashMap<String, Pair<Node, Node>>();
	
	/*
	 * Currently processed ProbeCalls Node
	 */
	private Node currentProbeCalls;
	
	private Node callerProbeCalls;
	
	// private Node lastCallInProbe;

    private Node callDataset;
	
    /*
     * Last call in Real Dataset
     */
	private Node lastCallInDataset;
	
	/*
	 * Name of Probes Network
	 */
	private final String networkName;
	
	/*
	 * Timestamp Index for Calls
	 */
	private final HashMap<String, MultiPropertyIndex<Long>> callTimestampIndexes = new HashMap<String, MultiPropertyIndex<Long>>();
	
	private boolean newDirectory;
	
	private Call call;
	private List<Call> messages;
	private CallType loadedType;
	
	private String prevCommandTimestamp;
	
	private String prevCommandName;

    private Node probeNode;
    
    private final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
	
	/**
	 * Creates a loader
	 * 
	 * @param directoryName name of directory to import
	 * @param display
	 * @param datasetName name of dataset
	 */
	public AMSLoader(String directoryName, Display display, String datasetName, String networkName) {
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
		
		initialize("AMS", null, directoryName, display, datasetName);
		
		addDriveIndexes();
		
		timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
	}
	
	/**
     * Creates a loader
     * 
     * @param directoryName name of directory to import
     * @param datasetName name of dataset
     * @param networkName
     * @param neo
     */
    public AMSLoader(String directoryName, String datasetName, String networkName, GraphDatabaseService neo) {
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
        
        initialize("AMS", neo, directoryName, null, datasetName);
        
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
		Node gis = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(),NetworkTypes.PROBE);				
		this.networkNode = findOrCreateNetworkNode(gis);			
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
	    Long startTime = System.currentTimeMillis();
        monitor.beginTask("Loading AMS data", 2);
        monitor.subTask("Searching for files to load");
        ArrayList<File> allFiles = getAllLogFilePathes(filename);
        
        monitor = SubMonitor.convert(monitor, allFiles.size());
        monitor.beginTask("Loading AMS data", allFiles.size());
        Transaction tx = neo.beginTx();
        try {
            initializeNetwork(networkName);
            initializeDatasets(dataset);
            Node dirNode = null;
            for (File logFile : allFiles) {
                monitor.subTask("Loading file " + logFile.getAbsolutePath());
                
                String probeName = initializeProbeNodes(logFile);
                filename = logFile.getAbsolutePath();
                currentDirectoryNode = findOrCreateDirectoryNode(logFile.getParentFile());
                if(dirNode != null && !currentDirectoryNode.equals(dirNode)){
                    tx = commit(tx);
                }
                dirNode = currentDirectoryNode;
                
                newFile = true;
                headersMap.clear();
                getHeaderMap(REAL_DATASET_HEADER_INDEX).typedProperties = null;
                getHeaderMap(CALL_DATASET_HEADER_INDEX).typedProperties = null;
    
                super.run(null);
                
                monitor.worked(1);
                
                updateProbeCache(probeName);
            }
        
            saveData();
            saveProperties();
            finishUpIndexes();
            finishUp();
        
            cleanupGisNode();
            //finishUpGis(getDatasetNode());
        }
        finally {
            tx.success();
            tx.finish();
        }
        
        basename = dataset;
        printStats(false);
		
		Long endTime = System.currentTimeMillis();
		LOGGER.debug("====================== Load time: "+(endTime-startTime)+"=============================");
	}
	

	
	/**
	 * Updates cache for probes
	 *
	 * @param probeName
	 */
	private void updateProbeCache(String probeName) {        
        probesCache.put(probeName, new Pair<Node, Node>(currentProbeCalls, probeNode));
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
		    probeNode = NeoUtils.findOrCreateProbeNode(networkNode, probeName, neo);
			currentProbeCalls = NeoUtils.getCallsNode(callDataset, probeName, probeNode, neo);
		}
		else {
		    probeNode = probeNodes.getRight();
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
            if (ind == directoryPath.length()) {
                return getDatasetNode();
            }
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
            directoryNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.DIRECTORY.getId());
            directoryNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, child);
            newDirectory = true;
        }
        else {
            newDirectory = false;
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
	    //System.out.println("Parse line: "+line);
	    if (newDirectory) {
	        saveData();
	        newDirectory = false;
	        call = null;
	        messages = null;
	    }
	    
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
		if (maybePESQ.startsWith("Port.write")) {
		    prevCommandName = commandName;
		    prevCommandTimestamp = timestamp;
		}
		else if (maybePESQ.startsWith("Port.read")) {
            if ((prevCommandName != null) && commandName.equals(prevCommandName) && (commandName.contains(CTSDC.COMMAND_NAME))) {
                timestamp = prevCommandTimestamp;
            }
        }
		else {
		    prevCommandName = null;
		}
		
		if (!tokenizer.hasMoreTokens()||maybePESQ.startsWith("Port.write")) {
			//if last token is command name than it's Port.write command, do not proccess it
			return;
		}
		
		boolean unsolicited = commandName.equals(UNSOLICITED);
		if (AMSCommandPackage.isAMSCommand(commandName) || (unsolicited)) {
			CommandSyntax syntax = AMSCommandPackage.getCommandSyntax(commandName);
			if (syntax == CommandSyntax.SET) { 
			    int equalsIndex = commandName.indexOf("=");			    
				if (!commandName.contains("AT+CMGS")) {
				    tokenizer = new StringTokenizer(commandName.substring(equalsIndex + 1).trim());
                }else{
                    //add message data.
                    tokenizer = new StringTokenizer(commandName.substring(equalsIndex + 1).trim()+","+tokenizer.nextToken("|"));
                }
				commandName = commandName.substring(0, equalsIndex);
			}
			
			AbstractAMSCommand command = AMSCommandPackage.getCommand(commandName, syntax);
			if (command == null) {
				return;
			}
			
			boolean isCallCommand = unsolicited ? true : processCommand(timestamp, command, syntax, tokenizer, false);
			if (command != null) {
				commandName = command.getName();
			}
			else {
				commandName = AMSCommandPackage.getRealCommandName(commandName);
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
						StringTokenizer paramTokenizer;
                        if (commandName.contains("CTSDSR")) {
                            //add message data.
                            paramTokenizer = new StringTokenizer(maybeTimestamp.substring(colonIndex + 1).trim()+","+tokenizer.nextToken());
                        }else{
                            paramTokenizer = new StringTokenizer(maybeTimestamp.substring(colonIndex + 1).trim());
                        }
						syntax = AMSCommandPackage.getCommandSyntax(commandName);
						command = AMSCommandPackage.getCommand(commandName, syntax);
					
						if (command != null) {
							//should be a result of command						    
							processCommand(timestamp, command, syntax, paramTokenizer, isCallCommand);
							if (command != null) {
								commandName = command.getName();
							}
							else {
								commandName = AMSCommandPackage.getRealCommandName(commandName);
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
	    CommandSyntax syntax = AMSCommandPackage.getCommandSyntax(commandName);
	    AbstractAMSCommand command = AMSCommandPackage.getCommand(commandName, syntax);
	    
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
        setProperty(headers, mNode, INeoConstants.PROPERTY_TYPE_EVENT, commandName);
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
				mmNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.MM.getId());
			}
			
			if (previousMmNode != null) {
				previousMmNode.createRelationshipTo(mmNode, GeoNeoRelationshipTypes.NEXT);
			}
			else {
			    msNode.createRelationshipTo(mmNode, GeoNeoRelationshipTypes.CHILD);
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
		mNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
		
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
		return super.getAllLogFilePathes(directoryName, AMS_LOG_FILE_EXTENSION);
		
	}
	
	/**
	 * Add Timestamp index
	 */
	private void addDriveIndexes() {
        try {
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(dataset));
            addIndex(NodeTypes.CALL.getId(), NeoUtils.getTimeIndexProperty(DriveTypes.AMS_CALLS.getFullDatasetName(dataset)));
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
	private boolean processCommand(String timestamp, AbstractAMSCommand command, CommandSyntax syntax, StringTokenizer tokenizer, boolean callCommandResult) {
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
        if (command.getName().equals(new CCI().getName())) {
            Object value = result.get(INeoConstants.PROBE_LA);
            if (value != null) {                
                probeNode.setProperty(INeoConstants.PROBE_LA, value);
            }
            value = result.get("F");
            if (value != null) {
                probeNode.setProperty(INeoConstants.PROBE_F, value);
            }

        }
		Node mNode = createMNode(timestampValue);
		
		if (isCallCommand) {
			String commandName = command.getName();
			
			CallEvents event = CallEvents.getCallEvent(commandName);
			
			boolean processEvent = (event == CallEvents.ERROR) ? callCommandResult : true;
			//do not proccess event if it's error for not Call Command result
			if (processEvent) {			    
				processCallEvent(mNode, event, timestampValue, result);
			}
		}
		
		updateMNode(mNode, command.getName(), result);		
		
		return isCallCommand;
	}
	
	/**
	 * Processes CallEvent
	 *
	 * @param relatedNode node that occure this event
	 * @param event type of event
	 * @param timestamp timestamp of event
	 */
	private void processCallEvent(Node relatedNode, CallEvents event, long timestamp, HashMap<String, Object> properties) {
	    switch (event) {	   
	    case CALL_SETUP_BEGIN:
	        if (messages == null) {
                if (call == null) {
                    call = new Call();
                    call.setCallResult(CallProperties.CallResult.SUCCESS);
                }
                call.setCallerProbe(currentProbeCalls);
                callerProbeCalls = currentProbeCalls;
                call.setCallSetupBeginTime(timestamp);
                if (isEmergencyCall(properties)) {
                    call.setCallType(CallType.EMERGENCY);
                } else if (isHelpCall(properties)) {
                    call.setCallType(CallType.HELP);
                } else if (isGroupCall(properties)) {
                    call.setCallType(CallType.GROUP);
                } else {
                    call.setCallType(CallType.INDIVIDUAL);
                }
            }
            break;
	    case CALL_SETUP_END:
	        if (messages==null) {
                if (call == null) {
                    call = new Call();
                    call.setCallResult(CallProperties.CallResult.SUCCESS);
                    call.setCallSetupEndTime(timestamp);
                } else if ((call.getCallType() != null)
                        && (((call.getCallType() == CallType.GROUP) || (call.getCallType().equals(CallType.EMERGENCY))) && (currentProbeCalls
                                .equals(callerProbeCalls)))
                        || (((call.getCallType() == CallType.INDIVIDUAL) || (call.getCallType() == CallType.HELP)) && (!currentProbeCalls
                                .equals(callerProbeCalls)))) {
                    if (((call.getCallSetupEnd() == 0) || call.getCallType() == CallType.GROUP)
                            && (call.getCallSetupBegin() < timestamp)) {
                        call.setCallSetupEndTime(timestamp);
                    }
                }
            }
            break;
	    case PESQ:
	        if (call != null) {
	            call.addLq((Float)properties.get(AMSCommandParameters.PESQ_LISTENING_QUALITIY.getName()));
	            call.addDelay((Float)properties.get(AMSCommandParameters.ESTIMATED_DELAY.getName()));
	        }
	        break;
	    case CALL_TERMINATION_BEGIN:
	        if (call != null) {
	            call.setCallTerminationBegin(timestamp);
	        }
	        break;
	    case TERMINATION_END:
	        if (call != null) {
	            call.setCallTerminationEnd(timestamp);
	        }
	        break;
	    case ERROR:
	        if (call != null) {
	            call.error(timestamp);
	        }
	        break;
	    case MESS_SETUP_BEGIN:
	        if (messages == null) {
                messages = new ArrayList<Call>();                
            }
	        if (isTSMMessage(properties)) {
                loadedType = CallType.TSM;
            }else if (isAlarmMessage(properties)) {
                loadedType = CallType.ALARM;
            }else{
                loadedType = CallType.SDS;
            }
	        boolean add = false;
            for(Call message : messages){
                if(message.getCallSetupBegin()==0){
                    message.setCallSetupBeginTime(timestamp);
                    message.setCallerProbe(currentProbeCalls);
                    message.setCallType(loadedType);
                    add = true;
                    break;
                }
            }
            if(!add){
                Call message = new Call();
                message.setCallSetupBeginTime(timestamp);
                message.setCallerProbe(currentProbeCalls);
                message.setCallType(loadedType);
                messages.add(message);
            }
	        callerProbeCalls = currentProbeCalls;
	        break;
	    case SEND_MESSAGE_BEGIN:
	        if (messages != null) {
	            boolean added = false;
	            for(Call message : messages){
                    if(message.getCallSetupEnd()==0){
                        message.setCallSetupEndTime(timestamp);
                        added = true;
                        break;
                    }
                }
	            if(!added){
	                Call message = new Call();	                
	                message.setCallSetupEndTime(timestamp);
	                messages.add(message);
	            }
            }
	        break;
	    case SEND_MESSAGE_END:
	        if(messages==null){
	            messages = new ArrayList<Call>();
            }
	        boolean founded = false;
            for(Call message : messages){
                if(message.getCallTerminationBegin()==0){
                    message.setCallTerminationBegin(timestamp);
                    message.setCallResult(CallResult.SUCCESS);
                    founded = true;
                    break;
                }
            }
            if(!founded){
                Call message = new Call();
                message.setCallTerminationBegin(timestamp);
                message.setCallResult(CallResult.SUCCESS);
                messages.add(message);
            }
	        break;
	    case MESS_ACKN_GET:
	        if(messages!=null){
	            boolean added = false;
	            for(Call message : messages){
	                if(message.getCallTerminationEnd()==0){
	                    message.setCallTerminationEnd(timestamp);
	                    message.setCallResult(CallResult.SUCCESS);
	                    added = true;
	                    break;
	                }
	            }
	            if(!added){
	                Call message = new Call();
	                message.setCallTerminationEnd(timestamp);
	                message.setCallResult(CallResult.SUCCESS);
	                messages.add(message);
	            }
            }
	        break;
	    case START_ITSI:
            if(call==null){
                call = new Call();
                call.setCallResult(CallProperties.CallResult.SUCCESS);
            }
            call.setCallerProbe(currentProbeCalls);
            callerProbeCalls = currentProbeCalls;            
            call.setCallType(CallType.ITSI_ATTACH);
            break;
	    case UPDATE_START_END:
            if(call!=null && CallType.ITSI_ATTACH.equals(call.getCallType())){
                if (!(call.getCallSetupBegin()>0)) {
                    call.setCallSetupBeginTime(timestamp);
                }else{
                    call.setCallTerminationEnd(timestamp);
                }
            }
            break;
	    default:
	        LOGGER.warn("Unknown call event "+event+".");
	    }
	    
	    if (call != null) {
	        call.addRelatedNode(relatedNode);
	        call.addCalleeProbe(currentProbeCalls);
	    }
	    if (messages!=null){
	        for(Call message : messages){
	            message.addRelatedNode(relatedNode);
	            message.addCalleeProbe(currentProbeCalls);
            }
	    }
	}
	
	private void saveData(){
	    saveCall(call);
	    if(messages!=null&&!messages.isEmpty()){
	        for(Call message : messages){
	            saveCall(message);
	        }
	    }
	}
	
	/**
	 * Creates a Call node and sets properties
	 */
	private void saveCall(Call call) {	    
        if ((call != null) && (call.getCallType() != null)) {
            CallType callType = call.getCallType();
	        Transaction tx = neo.beginTx();
	        try {
	            switch (callType) {
                case INDIVIDUAL:
                case GROUP:
                case EMERGENCY:
                case HELP:
                    storeRealCall(call);
                    break;
                case SDS:
                case TSM:
                case ALARM:
                    storeMessageCall(call);
                    break;
                case ITSI_ATTACH:
                    storeITSICall(call);
                    break;
                default:
                    NeoCorePlugin.error("Unknown call type "+callType+".", null);
                }
                tx.success();
	        }
	        catch (Exception e) {
	            tx.failure();
	            NeoCorePlugin.error(null, e);
	        }
	        finally {
	            tx.finish();
	        }
		}
	}

    private void storeRealCall(Call call) {
        Node probeCallNode = call.getCallerProbe();
        Node callNode = createCallNode(call.getCallSetupBegin(), call.getRelatedNodes(), probeCallNode);

        long setupDuration = call.getCallSetupEnd() - call.getCallSetupBegin();
        long terminationDuration = call.getCallTerminationEnd() - call.getCallTerminationBegin();
        long callDuration = call.getCallTerminationEnd() - call.getCallSetupBegin();

        LinkedHashMap<String, Header> headers = getHeaderMap(CALL_DATASET_HEADER_INDEX).headers;

        setProperty(headers, callNode, CallProperties.SETUP_DURATION.getId(), setupDuration);
        setProperty(headers, callNode, CallProperties.CALL_TYPE.getId(), call.getCallType().toString());
        setProperty(headers, callNode, CallProperties.CALL_RESULT.getId(), call.getCallResult().toString());
        setProperty(headers, callNode, CallProperties.CALL_DURATION.getId(), callDuration);
        setProperty(headers, callNode, CallProperties.TERMINATION_DURATION.getId(), terminationDuration);
        
        callNode.setProperty(CallProperties.LQ.getId(), call.getLq());
        callNode.setProperty(CallProperties.DELAY.getId(), call.getDelay());
        
        callNode.createRelationshipTo(probeCallNode, ProbeCallRelationshipType.CALLER);
        
        for (Node calleeProbe : call.getCalleeProbes()) {
            callNode.createRelationshipTo(calleeProbe, ProbeCallRelationshipType.CALLEE);
        }
        
        probeCallNode.setProperty(call.getCallType().getProperty(), true);
    }
    
    private void storeMessageCall(Call call) {
        Node probeCallNode = call.getCallerProbe();
        Node callNode = createCallNode(call.getCallSetupBegin(), call.getRelatedNodes(), probeCallNode);

        long receivedTime = call.getCallTerminationBegin() - call.getCallSetupEnd();
        long acknTime = call.getCallTerminationEnd()-call.getCallTerminationBegin();
        
        LinkedHashMap<String, Header> headers = getHeaderMap(CALL_DATASET_HEADER_INDEX).headers;

        if (call.getCallType().equals(CallType.ALARM)) {
            setProperty(headers, callNode, CallProperties.ALM_MESSAGE_DELAY.getId(), receivedTime);
            setProperty(headers, callNode, CallProperties.ALM_FIRST_MESS_DELAY.getId(), acknTime);
        } else {
            setProperty(headers, callNode, CallProperties.MESS_RECEIVE_TIME.getId(), receivedTime);
            setProperty(headers, callNode, CallProperties.MESS_ACKNOWLEDGE_TIME.getId(), acknTime);
        }
        setProperty(headers, callNode, CallProperties.CALL_TYPE.getId(), call.getCallType().toString());
        setProperty(headers, callNode, CallProperties.CALL_RESULT.getId(), call.getCallResult().toString());
        
        callNode.createRelationshipTo(probeCallNode, ProbeCallRelationshipType.CALLER);
        
        for (Node calleeProbe : call.getCalleeProbes()) {
            callNode.createRelationshipTo(calleeProbe, ProbeCallRelationshipType.CALLEE);
        }
        
        probeCallNode.setProperty(call.getCallType().getProperty(), true);
    }
    
    private void storeITSICall(Call call) {
        Node probeCallNode = call.getCallerProbe();
        Node callNode = createCallNode(call.getCallSetupBegin(), call.getRelatedNodes(), probeCallNode);

        long updateTime = call.getCallTerminationEnd() - call.getCallSetupBegin();
        
        LinkedHashMap<String, Header> headers = getHeaderMap(CALL_DATASET_HEADER_INDEX).headers;
        setProperty(headers, callNode, CallProperties.CALL_DURATION.getId(), updateTime);

        setProperty(headers, callNode, CallProperties.CALL_TYPE.getId(), call.getCallType().toString());
        setProperty(headers, callNode, CallProperties.CALL_RESULT.getId(), call.getCallResult().toString());
        
        callNode.createRelationshipTo(probeCallNode, ProbeCallRelationshipType.CALLER);
        
        for (Node calleeProbe : call.getCalleeProbes()) {
            callNode.createRelationshipTo(calleeProbe, ProbeCallRelationshipType.CALLEE);
        }
        
        probeCallNode.setProperty(call.getCallType().getProperty(), true);
    }

	/**
	 * Creates new Call Node
	 *
	 * @param timestamp timestamp of Call
	 * @param relatedNodes list of M node that creates this call
	 * @return created Node
	 */
	private Node createCallNode(long timestamp, ArrayList<Node> relatedNodes, Node probeCalls) {
		Transaction transaction = neo.beginTx();
		Node result = null;
		try {
			result = neo.createNode();
			result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.CALL.getId());
			result.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
			String probeName = NeoUtils.getNodeName(probeCalls,neo);
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, getCallName(probeName, timestamp));
            updateTimestampMinMax(CALL_DATASET_HEADER_INDEX, timestamp);
			index(result);
			
			//index for Probe Calls
			
            MultiPropertyIndex<Long> callIndex = getProbeCallsIndex(probeName);
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
	
	private String getCallName(String probeName, long timestamp){
	    StringBuffer result = new StringBuffer(probeName.split(" ")[0]).append("_").append(timeFormat.format(new Date(timestamp)));
	    return result.toString();
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
    
    private boolean isGroupCall(HashMap<String, Object> parameters) {
        return (parameters.get(AMSCommandParameters.COMMS_TYPE.getName()).equals(NeoLoaderPluginMessages.CTCC_Comms_Type_1) &&
                parameters.get(AMSCommandParameters.HOOK.getName()).equals(NeoLoaderPluginMessages.CTCC_Hook_1) &&
                parameters.get(AMSCommandParameters.SIMPLEX.getName()).equals(NeoLoaderPluginMessages.CTCC_Simplex_1) &&
                parameters.get(AMSCommandParameters.SLOTS_CODEC.getName()).equals(NeoLoaderPluginMessages.CTCC_Slots_Codec_1));
    }
    
    private boolean isTSMMessage(HashMap<String, Object> parameters) {
        return (parameters.get(AMSCommandParameters.AI_SERVICE.getName()).equals(NeoLoaderPluginMessages.CTSDS_ai_service_13)&&
                !parameters.get(AMSCommandParameters.ACCESS_PRIORITY.getName()).equals(NeoLoaderPluginMessages.CTSDS_access_priority_emer));
    }
    
    private boolean isAlarmMessage(HashMap<String, Object> parameters) {
        return parameters.get(AMSCommandParameters.ACCESS_PRIORITY.getName()).equals(NeoLoaderPluginMessages.CTSDS_access_priority_emer);
    }
    
    private boolean isEmergencyCall(HashMap<String, Object> parameters) {//TODO correct priority
        return (isGroupCall(parameters)&&!parameters.get(AMSCommandParameters.PRIORITY.getName()).equals(NeoLoaderPluginMessages.CTSDC_Priority_0));
    }
    
    private boolean isHelpCall(HashMap<String, Object> parameters) {//TODO correct priority
        return (!isGroupCall(parameters)&&!parameters.get(AMSCommandParameters.PRIORITY.getName()).equals(NeoLoaderPluginMessages.CTSDC_Priority_0));
    }
}
