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
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.CallProperties.CallResult;
import org.amanzi.neo.core.enums.CallProperties.CallType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
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
public class AMSLoader extends AbstractCallLoader {
    private static final Logger LOGGER = Logger.getLogger(AMSLoader.class);

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
		CALL_SETUP_END("CTCC"),
		ERROR("CME ERROR"),
		PESQ("PESQ"),
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


	

	
	/*
	 * Name of Probes Network
	 */
	private final String networkName;
	
	
	private boolean newDirectory;
	
	private Call call;
	private List<Call> messages;
	private CallType loadedType;
	
	private String prevCommandTimestamp;
	
	private String prevCommandName;

    private String probeName;
	private Node probeNode;
    

	
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
			
			callDataset = getVirtualDataset(DriveTypes.AMS_CALLS,true);
			
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
                
                probeName = getRealProbeName(logFile);
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
                
                //updateProbeCache(probeName);
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
	private void initializeProbeNodes(Object laObj, Object frObj) {
	    if(laObj==null||frObj==null){
	        return;
	    }
	    Integer la = (Integer)laObj;
	    Float frequency = (Float)frObj;
		String name = NeoUtils.buildProbeName(probeName, la, frequency);
		
		Pair<Node, Node> probeNodes = probesCache.get(name);		
		if (probeNodes == null) {            
		    probeNode = NeoUtils.findOrCreateProbeNode(networkNode, name, neo);
		    probeNode.setProperty(INeoConstants.PROBE_LA, la);        
            probeNode.setProperty(INeoConstants.PROBE_F, frequency);
			currentProbeCalls = NeoUtils.getCallsNode(callDataset, name, probeNode, neo);
			updateProbeCache(name);
		} else {
		    probeNode = probeNodes.getRight();
			currentProbeCalls = probeNodes.getLeft();
		}		
	}
	
	private String getRealProbeName(File logFile){
	    String fileName = logFile.getName();
        int index = fileName.indexOf("#");
        String name = fileName;
        if (index > -1) {
            name = name.substring(0, index);
        }        
        return name;
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
				String params = commandName.substring(equalsIndex + 1).trim();
                if (!commandName.contains("AT+CMGS")) {
				    tokenizer = new StringTokenizer(params);
                }else{
                    //add message data.
                    String mayBeMessage = tokenizer.nextToken("|");
                    if(mayBeMessage.contains(CallEvents.ERROR.commandName)){
                        tokenizer = new StringTokenizer(params);
                    }else{
                        tokenizer = new StringTokenizer(params+","+mayBeMessage);
                    }
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
				mmNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.MM.getId());if (previousMmNode != null) {
	                previousMmNode.createRelationshipTo(mmNode, GeoNeoRelationshipTypes.NEXT);
	            }
	            else {
	                msNode.createRelationshipTo(mmNode, GeoNeoRelationshipTypes.CHILD);
	            }
	            previousMmNode = mmNode;
			}				
			
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
	    //try to parse timestam
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
            Object laValue = result.get(INeoConstants.PROBE_LA);
            Object frValue = result.get("F");
            initializeProbeNodes(laValue, frValue);
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
	    Call founded;
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
	            //call.addDelay((Float)properties.get(AMSCommandParameters.ESTIMATED_DELAY.getName()));//TODO make correct getting data
	        }
	        break;
	    case CALL_TERMINATION_BEGIN:
	        if (call != null) {
	            call.setCallTerminationBegin(timestamp);
	        }
	        break;
	    case TERMINATION_END:
	        if (call != null) {
	            if ((call.getCallType() != null)
                        && (((call.getCallType() == CallType.GROUP) || (call.getCallType().equals(CallType.EMERGENCY))) && (currentProbeCalls
                                .equals(callerProbeCalls)))
                        || (((call.getCallType() == CallType.INDIVIDUAL) || (call.getCallType() == CallType.HELP)))) {
	                call.setCallTerminationEnd(timestamp);
                }
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
	        founded = null;
            for(Call message : messages){
                if(message.getCallSetupBegin()==0){
                    founded = message;
                    break;
                }
            }
            if(founded==null){
                founded = new Call();
                messages.add(founded);
            }
            founded.setCallSetupBeginTime(timestamp);
            founded.setCallerProbe(currentProbeCalls);
            founded.setCallType(loadedType);            
	        callerProbeCalls = currentProbeCalls;
	        break;
	    case SEND_MESSAGE_BEGIN:
	        if (messages != null) {
	            founded = null;
	            for(Call message : messages){
                    if(message.getCallSetupEnd()==0){
                        founded = message;
                        break;
                    }
                }
	            if(founded==null){
	                founded = new Call();
	                messages.add(founded);
	            }
	            founded.setCallSetupEndTime(timestamp);
	            if(properties.get(AMSCommandParameters.SENDED_MESSAGE.getName())==null){
	                founded.setCallResult(CallResult.FAILURE);
	            }
            }
	        break;
	    case SEND_MESSAGE_END:
	        if(messages==null){
	            messages = new ArrayList<Call>();
            }
	        founded = null;
            for(Call message : messages){
                if(message.getCallTerminationBegin()==0){
                    founded = message;
                    break;
                }
            }
            if(founded==null){
                founded = new Call();                
                messages.add(founded);
            }
            founded.setCallTerminationBegin(timestamp);
            if (founded.getCallResult()==null) {
                founded.setCallResult(CallResult.SUCCESS);
            }
            break;
	    case MESS_ACKN_GET:
	        if(messages!=null){
	            founded = null;
	            for(Call message : messages){
	                if(message.getCallTerminationEnd()==0){
	                    founded = message;
	                    break;
	                }
	            }
	            if(founded==null){
	                founded = new Call();
	                messages.add(founded);
	            }
	            founded.setCallTerminationEnd(timestamp);
	            if (founded.getCallResult()==null) {
	                founded.setCallResult(CallResult.SUCCESS);
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
    protected String getPrymaryType(Integer key) {
        switch (key) {
        case REAL_DATASET_HEADER_INDEX:
            return NodeTypes.M.getId();
        case CALL_DATASET_HEADER_INDEX:
            return NodeTypes.CALL.getId();
        default:
            return null;
        }
    }
    @Override
    protected boolean needParceHeaders() {
    	return false;
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
