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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.AMSLoader.Call;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.model.ams01.Attachment;
import org.amanzi.neo.loader.model.ams01.CellResel;
import org.amanzi.neo.loader.model.ams01.CompleteGpsData;
import org.amanzi.neo.loader.model.ams01.Events;
import org.amanzi.neo.loader.model.ams01.GpsData;
import org.amanzi.neo.loader.model.ams01.GroupAttach;
import org.amanzi.neo.loader.model.ams01.Handover;
import org.amanzi.neo.loader.model.ams01.HandoverIsInclusive;
import org.amanzi.neo.loader.model.ams01.InterfaceData;
import org.amanzi.neo.loader.model.ams01.IsInconclusive;
import org.amanzi.neo.loader.model.ams01.ItsiAttach;
import org.amanzi.neo.loader.model.ams01.MptSync;
import org.amanzi.neo.loader.model.ams01.NeighborData;
import org.amanzi.neo.loader.model.ams01.Ntpq;
import org.amanzi.neo.loader.model.ams01.PdResult;
import org.amanzi.neo.loader.model.ams01.PesqResult;
import org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap;
import org.amanzi.neo.loader.model.ams01.ReceiveMsg;
import org.amanzi.neo.loader.model.ams01.ReceiveMsgIsInclusive;
import org.amanzi.neo.loader.model.ams01.SendMsg;
import org.amanzi.neo.loader.model.ams01.SendMsgIsInclusive;
import org.amanzi.neo.loader.model.ams01.SendReport;
import org.amanzi.neo.loader.model.ams01.ServingData;
import org.amanzi.neo.loader.model.ams01.TOCIsInclusive;
import org.amanzi.neo.loader.model.ams01.TPCIsInclusive;
import org.amanzi.neo.loader.model.ams01.TTCIsInclusive;
import org.amanzi.neo.loader.model.ams01.TTCPesqResult;
import org.amanzi.neo.loader.model.ams01.Toc;
import org.amanzi.neo.loader.model.ams01.Tpc;
import org.amanzi.neo.loader.model.ams01.Ttc;
import org.amanzi.neo.loader.model.ams01.types.DirectionType;
import org.amanzi.neo.loader.model.ams01.types.MsgTypeType;
import org.amanzi.neo.loader.model.ams01.types.StatusType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.exolab.castor.xml.Unmarshaller;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * AMS XML Loader
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AMSXMLLoader extends DriveLoader {
    /**
     * Header Index for Real Dataset
     */
    private static final int REAL_DATASET_HEADER_INDEX = 0;

    /** The formatter. */
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss,SSSz");

    /**
     * Header Index for Call Dataset
     */
    private static final int CALL_DATASET_HEADER_INDEX = 1;
    /**
     * Header Index for Probe Network Dataset
     */
    /** The Constant PROBE_NETWORK_HEADER_INDEX. */
    private static final int PROBE_NETWORK_HEADER_INDEX = 2;

    /** The directory name. */
    private final String directoryName;

    /** The network name. */
    private String networkName;

    /** The network gis. */
    private Node networkGis;

    /** The network node. */
    private Node networkNode;

    /** The gis dataset. */
    private Node gisDataset;

    /** The last dataset node. */
    private Node lastDatasetNode;
    
    /** The last call node. */
    private  Node lastCallInDataset=null;

    /** The call dataset. */
    private Node callDataset;
    private final Map<String, Node> probeCache=new HashMap<String, Node>();
    private final Map<String, String> telephon=new HashMap<String, String>();

    private AMSCall tocttc;

    /**
     * Creates a loader.
     * 
     * @param directoryName name of directory to import
     * @param display the display
     * @param datasetName name of dataset
     * @param networkName the network name
     */
    public AMSXMLLoader(String directoryName, Display display, String datasetName, String networkName) {
        driveType = DriveTypes.AMS;
        if (datasetName == null) {
            int startIndex = directoryName.lastIndexOf(File.separator);
            if (startIndex < 0) {
                startIndex = 0;
            } else {
                startIndex++;
            }
            datasetName = directoryName.substring(startIndex);
        }

        this.directoryName = directoryName;
        this.filename = directoryName;
        this.networkName = networkName;

        initialize("AMS", null, directoryName, display, datasetName);

        // timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    }

    /**
     * Adds the drive indexes.
     */
    private void addDriveIndexes() {
        try {
            addIndex(NodeTypes.PROBE.getId(), NeoUtils.getLocationIndexProperty(networkName));
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(dataset));
            addIndex(NodeTypes.CALL.getId(), NeoUtils.getTimeIndexProperty(DriveTypes.AMS_CALLS.getFullDatasetName(dataset)));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Run.
     * 
     * @param monitor the monitor
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        Long startTime = System.currentTimeMillis();
        monitor.beginTask("Loading AMS data", 2);
        monitor.subTask("Searching for files to load");
        List<File> allFiles = LoaderUtils.getAllFiles(directoryName, new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || LoaderUtils.getFileExtension(pathname.getName()).equalsIgnoreCase(".xml");
            }
        });

        monitor = SubMonitor.convert(monitor, allFiles.size());
        monitor.beginTask("Loading AMS data", allFiles.size());
        lastDatasetNode = null;
        Transaction tx = neo.beginTx();
        try {
            initializeNetwork(networkName);
            initializeDatasets(dataset);
            addDriveIndexes();
            initializeIndexes();
            long count = 0;
            for (File logFile : allFiles) {
                monitor.subTask("Loading file " + logFile.getAbsolutePath());
                handleFile(logFile);
                if (count++ > 10) {
                    commit(tx);
                }
                monitor.worked(1);
            }

            saveProperties();
            finishUpIndexes();
            finishUp();

            cleanupGisNode();
            finishUpGis(getDatasetNode());
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * Handle file.
     * 
     * @param logFile the log file
     */
    private void handleFile(File logFile) {
        try {
            long time = System.currentTimeMillis();
            filename = logFile.getAbsolutePath();
            Pair<Boolean, Node> pair = NeoUtils.findOrCreateFileNode(neo, datasetNode, basename, filename);
            file = pair.getRight();
            Unmarshaller u = new Unmarshaller(InterfaceData.class);
            u.setValidation(false);
            InterfaceData interfaceData = (InterfaceData)u.unmarshal(new FileReader(logFile));
            time = System.currentTimeMillis() - time;
            System.out.println(time);
            time = System.currentTimeMillis();
            Map<String, Probe> probeMap = formatProbeData(interfaceData);
            time = System.currentTimeMillis() - time;
            System.out.println(time);
            for (Map.Entry<String, Probe> entry : probeMap.entrySet()) {
                storeProbe(entry.getValue());
            }
            for (Events event : interfaceData.getEvents()) {
                storeEvent(event);
            }
            handleCalls();

        } catch (Exception e) {
            NeoLoaderPlugin.error(String.format("File %s not parsed", logFile.getName()));
            NeoLoaderPlugin.exception(e);
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void handleCalls() {
        if (tocttc!=null){
            storeRealCall(tocttc);
        }
        tocttc=null;
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
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, AMSLoader.getCallName(probeName, timestamp));
            updateTimestampMinMax(CALL_DATASET_HEADER_INDEX, timestamp);
            index(result);
            
            
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
    private void storeRealCall(Call call) {
        Node probeCallNode = call.getCallerProbe();
        Node callNode = createCallNode(call.getCallSetupBegin(), call.getRelatedNodes(), probeCallNode);

        long setupDuration = call.getCallSetupEnd() - call.getCallSetupBegin();
        long terminationDuration = call.getCallTerminationEnd() - call.getCallTerminationBegin();
        long callDuration = call.getCallTerminationEnd() - call.getCallSetupBegin();

        LinkedHashMap<String, Header> headers = getHeaderMap(CALL_DATASET_HEADER_INDEX).headers;

        setIndexProperty(headers, callNode, CallProperties.SETUP_DURATION.getId(), setupDuration);
        setIndexProperty(headers, callNode, CallProperties.CALL_TYPE.getId(), call.getCallType().toString());
        setIndexProperty(headers, callNode, CallProperties.CALL_RESULT.getId(), call.getCallResult().toString());
        setIndexProperty(headers, callNode, CallProperties.CALL_DURATION.getId(), callDuration);
        setIndexProperty(headers, callNode, CallProperties.TERMINATION_DURATION.getId(), terminationDuration);
        
        callNode.setProperty(CallProperties.LQ.getId(), call.getLq());
        callNode.setProperty(CallProperties.DELAY.getId(), call.getDelay());
        
        callNode.createRelationshipTo(probeCallNode, ProbeCallRelationshipType.CALLER);
        
        for (Node calleeProbe : call.getCalleeProbes()) {
            callNode.createRelationshipTo(calleeProbe, ProbeCallRelationshipType.CALLEE);
        }
        
        probeCallNode.setProperty(call.getCallType().getProperty(), true);
    }

    /**
     * Store event.
     * 
     * @param event the event
     * @throws ParseException the parse exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void storeEvent(Events event) throws ParseException, IOException {
        CellResel cr = event.getCellResel();

        if (cr != null) {
            Node eventNode = neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            long timestamp = getTime(cr.getCellReselReq());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "cellReselReq", timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, CellResel.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, cr.getProbeID());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);

            index(eventNode);

        }
        GroupAttach gr = event.getGroupAttach();
        if (gr != null) {

            Node eventNode = neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, GroupAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, gr.getProbeID());

            long timestamp = getTime(gr.getGroupAttachTime());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "groupAttachTime", timestamp);
            if (gr.hasErrorCode()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errorCode", gr.getErrorCode());
            }
            Node lastMM = null;
            for (Attachment at : gr.getAttachment()) {
                Node mm = neo.createNode();
                NeoUtils.addChild(eventNode, mm, lastMM, neo);
                NodeTypes.MM.setNodeType(mm, neo);
                lastMM = mm;
                mm.setProperty("groupType", at.getGroupType());
                mm.setProperty("gssi", at.getGssi());
            }
            index(eventNode);
        }
        Handover handover = event.getHandover();
        if (handover != null) {
            Node eventNode = neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, Handover.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, handover.getProbeID());
            if (handover.hasErrorCode()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errorCode", handover.getErrorCode());
            }
            if (handover.hasLocationAreaAfter()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "locationAreaAfter", handover.getLocationAreaAfter());
            }
            if (handover.hasLocationAreaBefore()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "locationAreaBefore", handover.getLocationAreaBefore());
            }
            long timestamp = getTime(handover.getHo_Req());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "ho_Req", timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "ho_accept", getTime(handover.getHo_Accept()));
            HandoverIsInclusive isIncl = handover.getHandoverIsInclusive();
            if (isIncl != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "reason", isIncl.getReason());
                if (isIncl.hasErrCode()) {
                    setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errCode", isIncl.getErrCode());
                }
            }
            index(eventNode);
        }
        ItsiAttach itAt = event.getItsiAttach();
        if (itAt != null) {
            Node eventNode = neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, ItsiAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, itAt.getProbeID());
            long timestamp = getTime(itAt.getItsiAtt_Req());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "itsiAtt_Req", timestamp);
            String itsiAttAccept = itAt.getItsiAtt_Accept();
            if (itsiAttAccept != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "itsiAtt_Accept", getTime(itsiAttAccept));
            }
            if (itAt.hasErrorCode()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errorCode", itAt.getErrorCode());
            }
            if (itAt.hasLocationAreaAfter()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "locationAreaAfter", itAt.getLocationAreaAfter());
            }
            if (itAt.hasLocationAreaBefore()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "locationAreaBefore", itAt.getLocationAreaBefore());
            }
            IsInconclusive isIncl = itAt.getIsInconclusive();
            if (isIncl != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "reason", isIncl.getReason());
                if (isIncl.hasErrCode()) {
                    setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errCode", isIncl.getErrCode());
                }
            }
            index(eventNode);
        }

        ReceiveMsg rMsg = event.getReceiveMsg();
        if (rMsg != null) {
            Node eventNode = neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, ItsiAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, rMsg.getProbeID());
            long timestamp =  getTime(rMsg.getReceiveTime());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "receiveTime", timestamp);
            if (rMsg.hasDataLength()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "dataLength", rMsg.getDataLength());
                ByteArrayOutputStream barMsg = new ByteArrayOutputStream(rMsg.getDataLength());
                barMsg.write(rMsg.getDataTxt());
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "dataTxt", barMsg.toString());
            }
            if (rMsg.hasMsgRef()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "msgRef", rMsg.getMsgRef());
            }
            MsgTypeType mType = rMsg.getReceiveMsgMsgType();
            if (mType != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "message_type", mType.getType());
            }
            ReceiveMsgIsInclusive isIncl = rMsg.getReceiveMsgIsInclusive();
            if (isIncl != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "reason", isIncl.getReason());
                if (isIncl.hasErrCode()) {
                    setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errCode", isIncl.getErrCode());
                }
            }
            index(eventNode);
        }
        SendMsg sMsg = event.getSendMsg();
        if (sMsg != null) {
            Node eventNode = neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, ItsiAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, sMsg.getProbeID());
            long timestamp = getTime(sMsg.getSendTime());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "sendTime", timestamp);
            if (sMsg.hasDataLength()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "dataLength", sMsg.getDataLength());
                ByteArrayOutputStream barMsg = new ByteArrayOutputStream(sMsg.getDataLength());
                barMsg.write(sMsg.getDataTxt());
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "dataTxt", barMsg.toString());

            }
            if (sMsg.hasMsgRef()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "msgRef", sMsg.getMsgRef());
            }
            if (sMsg.hasErrorCode()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errCode", sMsg.getErrorCode());
            }
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "calledNumber", sMsg.getCalledNumber());
            MsgTypeType mType = sMsg.getMsgType();
            if (mType != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "message_type", mType.getType());
            }
            SendMsgIsInclusive isIncl = sMsg.getSendMsgIsInclusive();
            if (isIncl != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "reason", isIncl.getReason());
                if (isIncl.hasErrCode()) {
                    setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errCode", isIncl.getErrCode());
                }
            }

            Node lastMM = null;
            for (SendReport report : sMsg.getSendReport()) {
                Node mm = neo.createNode();
                NeoUtils.addChild(eventNode, mm, lastMM, neo);
                NodeTypes.MM.setNodeType(mm, neo);
                lastMM = mm;
                mm.setProperty("reportTime", getTime(report.getReportTime()));
                StatusType status = report.getStatus();
                if (status != null) {
                    mm.setProperty("status_type", status.getType());
                }
            }
            index(eventNode);
        }

        Toc toc = event.getToc();
        if (toc != null) {
            Node eventNode = neo.createNode();

            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, ItsiAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, toc.getProbeID());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "calledNumber", toc.getCalledNumber());
            long timestamp = getTime(toc.getConfigTime());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "config_time", timestamp);
            String connectTime = toc.getConnectTime();
            if (connectTime != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "connect_time", getTime(connectTime));
            }
            String disconnectTime = toc.getDisconnectTime();
            if (disconnectTime != null) {
                long disconTime = getTime(disconnectTime);
                
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "disconnect_time", disconTime);
            }
            String setupTime = toc.getSetupTime();
            if (setupTime != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "setup_time", getTime(setupTime));
            }
            if (toc.hasErrorCode()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errorCode", toc.getErrorCode());
            }
            if (toc.hasCauseForTermination()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "causeForTermination", toc.getCauseForTermination());
            }
            if (toc.hasHook()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "hook", toc.getHook());
            }
            if (toc.hasPriority()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "priopity", toc.getPriority());
            }
            if (toc.hasSimplex()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "simplex", toc.getSimplex());
            }
            if(toc.hasHook()&&toc.hasSimplex()&&toc.getHook()==0&&toc.getSimplex()==0){
                tocttc = new AMSCall();
                tocttc.addRelatedNode(eventNode);
                if (disconnectTime != null) {
                    tocttc.setCallTerminationBegin(getTime(disconnectTime));
                }
                tocttc.setCallerProbe(probeCache.get(toc.getProbeID()));
                tocttc.setCallerPhoneNumber(toc.getCalledNumber());
                tocttc.setCallSetupBeginTime(timestamp);
            }
            Node lastMM = null;
            for (PesqResult result : toc.getPesqResult()) {
                Node mm = neo.createNode();
                NeoUtils.addChild(eventNode, mm, lastMM, neo);
                NodeTypes.MM.setNodeType(mm, neo);
                lastMM = mm;
                if (result.hasDelay()) {
                    if (tocttc!=null){
                        tocttc.addDelay( result.getDelay());
                    }
                    mm.setProperty("delay", result.getDelay());

                }
                if (result.hasPesq()) {
                    //TODO check documentation - pesq == lq?
                    if (tocttc!=null){
                        tocttc.addLq(Double.valueOf( result.getPesq()).floatValue());
                    }
                    mm.setProperty("pesq", result.getPesq());

                }
                mm.setProperty("sendSampleStart", getTime(result.getSendSampleStart()));
            }
            TOCIsInclusive isIncl = toc.getTOCIsInclusive();
            if (isIncl != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "reason", isIncl.getReason());
                if (isIncl.hasErrCode()) {
                    setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errCode", isIncl.getErrCode());
                }
            }
            index(eventNode);
        }

        Tpc tpc = event.getTpc();
        if (tpc != null) {
            Node eventNode = neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, ItsiAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, tpc.getProbeID());

            long timestamp =getTime(tpc.getSetupTime());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "setup_time", timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "connect_time", getTime(tpc.getConnectTime()));
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "release_time", getTime(tpc.getReleaseTime()));
            if (tpc.hasErrorCode()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errorCode", tpc.getErrorCode());
            }
            if (tpc.hasCauseForTermination()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "causeForTermination", tpc.getCauseForTermination());
            }
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "FtpConnAccept", tpc.getFtpConnAccept());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "FtpConnReq", tpc.getFtpConnReq());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "PdpAccept", tpc.getPdpAccept());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "PdpRequest", tpc.getPdpRequest());

            Node lastMM = null;
            for (PdResult result : tpc.getPdResult()) {
                Node mm = neo.createNode();
                NeoUtils.addChild(eventNode, mm, lastMM, neo);
                NodeTypes.MM.setNodeType(mm, neo);
                lastMM = mm;
                if (result.hasSize()) {
                    mm.setProperty("size", result.getSize());

                }
                mm.setProperty("TransmitStart", result.getTransmitStart());
                mm.setProperty("TransmitStart", result.getTransmitEnd());
                DirectionType dir = result.getDirection();
                if (dir != null) {
                    mm.setProperty("type", dir.getType());
                }
            }
            TPCIsInclusive isIncl = tpc.getTPCIsInclusive();
            if (isIncl != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "reason", isIncl.getReason());
                if (isIncl.hasErrCode()) {
                    setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errCode", isIncl.getErrCode());
                }
            }
  
            index(eventNode);
        };
        Ttc ttc = event.getTtc();
        if (ttc != null) {
            Node eventNode = neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode = eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_EVENT_TYPE, ItsiAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, INeoConstants.M_PROBE_ID, ttc.getProbeID());

            long timestamp =  getTime(ttc.getIndicationTime());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "indication_time", timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "connect_time", getTime(ttc.getConnectTime()));
            Long answTime = getTime(ttc.getAnswerTime());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "answer_time", answTime);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "release_time", getTime(ttc.getReleaseTime()));
            if (ttc.hasErrorCode()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errorCode", ttc.getErrorCode());
            }
            if (ttc.hasCauseForTermination()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "causeForTermination", ttc.getCauseForTermination());
            }
            if (ttc.hasHook()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "hook", ttc.getHook());
            }
            if (ttc.hasSimplex()) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "simplex", ttc.getSimplex());
            }

            Node lastMM = null;
            for (TTCPesqResult result : ttc.getTTCPesqResult()) {
                Node mm = neo.createNode();
                NeoUtils.addChild(eventNode, mm, lastMM, neo);
                NodeTypes.MM.setNodeType(mm, neo);
                lastMM = mm;
                if (result.hasDelay()) {
                    mm.setProperty("delay", result.getDelay());
                }
                if (result.hasPesq()) {
                    mm.setProperty("pesq", result.getPesq());
                }
                mm.setProperty("sendSampleStart", getTime(result.getSendSampleStart()));

            }
            TTCIsInclusive isIncl = ttc.getTTCIsInclusive();
            if (isIncl != null) {
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "reason", isIncl.getReason());
                if (isIncl.hasErrCode()) {
                    setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers, eventNode, "errCode", isIncl.getErrCode());
                }
            }
            if (tocttc!=null){
                assert tocttc.getCallerPhoneNumber().equals(ttc.getCallingNumber());
                tocttc.setCallSetupEndTime(answTime);
                tocttc.setCallSetupEndTime(answTime);
                if (ttc.hasErrorCode()){
                    tocttc.setCallResult(CallResult.FAILURE);
                }
            }
            index(eventNode);
        }

    }

    /**
     * Gets the time.
     * 
     * @param timestamp the timestamp
     * @return the time
     * @throws ParseException the parse exception
     */
    private static Long getTime(String timestamp) throws ParseException {
        int i = timestamp.lastIndexOf(':');
        StringBuilder time = new StringBuilder(timestamp.substring(0, i)).append(timestamp.substring(i + 1, timestamp.length()));
        return formatter.parse(time.toString()).getTime();
    }

    /**
     * Store probe.
     * 
     * @param probe the probe
     */
    private void storeProbe(Probe probe) {
        if(probeCache.get(probe.getId())!=null){
            return;
        }
        Node probeNew = NeoUtils.findOrCreateProbeNode(networkNode, probe.getId(), neo);
        probeCache.put(probe.getId(),probeNew);
        ProbeIDNumberMap map = probe.getProbeIDNumberMap();
        if (map == null) {
            NeoLoaderPlugin.info(String.format("Probe %s not stored. Reason: not found ProbeIDNumberMap", probe.getId()));
            return;
        }
        setNewIndexProperty(getHeaderMap(PROBE_NETWORK_HEADER_INDEX).headers, probeNew, "phoneNumber", map.getPhoneNumber());
//        if (telephon.get(probe.getId())==null){
//            telephon.put(probe.getId(), map.getPhoneNumber());
//        }
        String fr = map.getFrequency();
        if (fr != null) {
            setNewIndexProperty(getHeaderMap(PROBE_NETWORK_HEADER_INDEX).headers, probeNew, "frequency", Double.parseDouble(fr));
        }
        setNewIndexProperty(getHeaderMap(PROBE_NETWORK_HEADER_INDEX).headers, probeNew, "locationArea", map.getLocationArea());
        // parce GPS data
        //TODO now we do not store gps data, because we have several locations for one probe.
        if (/*remove after investigate*/false&&!probeNew.hasProperty(INeoConstants.PROPERTY_LAT_NAME)) {
            for (CompleteGpsData gpsData : probe.getCompleteGpsData()) {
                GPSData data = new GPSData(gpsData);
                if (data.getLat() != null) {
                    probeNew.setProperty(INeoConstants.PROPERTY_LAT_NAME, data.getLat().doubleValue());
                    probeNew.setProperty(INeoConstants.PROPERTY_LON_NAME, data.getLon().doubleValue());
                    GisProperties gisProperties = gisNodes.get(networkName);
                    gisProperties.updateBBox(data.getLat(), data.getLon());
                    gisProperties.checkCRS(data.getLat(), data.getLon(), null);
                    gisProperties.incSaved();
                }
            }
        }

        index(probeNew);
    }

    /**
     * Initializes Network for probes.
     * 
     * @param networkName name of network
     */
    private void initializeNetwork(String networkName) {
        String oldBasename = basename;
        if ((networkName == null) || (networkName.length() == 0)) {
            networkName = basename + " Probes";
        } else {
            networkName = networkName.trim();
        }

        basename = networkName;
        networkGis = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(), NetworkTypes.PROBE);
        networkNode = findOrCreateNetworkNode(networkGis);
        this.networkName = basename;
        basename = oldBasename;
    }

    /**
     * Initializes a Call dataset.
     * 
     * @param datasetName name of Real dataset
     */
    private void initializeDatasets(String datasetName) {
        Transaction tx = neo.beginTx();
        try {
            datasetNode = findOrCreateDatasetNode(neo.getReferenceNode(), dataset);
            gisDataset = findOrCreateGISNode(datasetNode, GisTypes.DRIVE.getHeader());

            callDataset = getVirtualDataset(DriveTypes.AMS_CALLS);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            NeoCorePlugin.error(null, e);
            throw new RuntimeException(e);
        } finally {
            tx.finish();
        }
    }

    /**
     * Gets the storing node.
     * 
     * @param key the key
     * @return the storing node
     */
    @Override
    protected Node getStoringNode(Integer key) {
        switch (key) {
        case REAL_DATASET_HEADER_INDEX:
            return gisNodes.get(dataset).getGis();
        case CALL_DATASET_HEADER_INDEX:
            return gisNodes.get(DriveTypes.AMS_CALLS.getFullDatasetName(dataset)).getGis();
        case PROBE_NETWORK_HEADER_INDEX:
            return gisNodes.get(networkName).getGis();
        default:
            return null;
        }
    }

    /**
     * Parses the line.
     * 
     * @param line the line
     */
    @Override
    protected void parseLine(String line) {
        // not use
    }

    /**
     * Format probe data.
     * 
     * @param interfaceData the interface data
     * @return the map
     */
    private Map<String, Probe> formatProbeData(InterfaceData interfaceData) {
        Map<String, Probe> result = new HashMap<String, Probe>();
        for (ProbeIDNumberMap idMap : interfaceData.getCommonTestData().getProbeIDNumberMap()) {
            String id = idMap.getProbeID();
            Probe probe = result.get(id);
            if (probe == null) {
                probe = new Probe(id);
                result.put(id, probe);
            }
            probe.setProbeIDNumberMap(idMap);
        }
        for (MptSync elem : interfaceData.getCommonTestData().getMptSync()) {
            String id = elem.getProbeID();
            Probe probe = result.get(id);
            if (probe == null) {
                probe = new Probe(id);
                result.put(id, probe);
            }
            probe.addMptSync(elem);
        }
        for (NeighborData elem : interfaceData.getCommonTestData().getNeighborData()) {
            String id = elem.getProbeID();
            Probe probe = result.get(id);
            if (probe == null) {
                probe = new Probe(id);
                result.put(id, probe);
            }
            probe.addNeighborData(elem);
        }
        for (Ntpq elem : interfaceData.getCommonTestData().getNtpq()) {
            String id = elem.getProbeID();
            Probe probe = result.get(id);
            if (probe == null) {
                probe = new Probe(id);
                result.put(id, probe);
            }
            probe.addNtpq(elem);
        }
        for (ServingData elem : interfaceData.getCommonTestData().getServingData()) {
            String id = elem.getProbeID();
            Probe probe = result.get(id);
            if (probe == null) {
                probe = new Probe(id);
                result.put(id, probe);
            }
            probe.addServingData(elem);
        }
        GpsData gpsData = interfaceData.getGpsData();
        if (gpsData != null) {
            for (CompleteGpsData elem : gpsData.getCompleteGpsDataList().getCompleteGpsData()) {
                String id = elem.getProbeID();
                Probe probe = result.get(id);
                if (probe == null) {
                    probe = new Probe(id);
                    result.put(id, probe);
                }
                probe.addCompleteGpsData(elem);
            }
        }

        return result;
    }

    /**
     * The Class Probe.
     */
    private static class Probe {

        /** The id. */
        final String id;

        /** The probe id number map. */
        ProbeIDNumberMap probeIDNumberMap = null;

        /** The mpt sync. */
        List<MptSync> mptSync = new LinkedList<MptSync>();

        /** The neighbor data. */
        List<NeighborData> neighborData = new LinkedList<NeighborData>();

        /** The ntpq. */
        List<Ntpq> ntpq = new LinkedList<Ntpq>();

        /** The serving data. */
        List<ServingData> servingData = new LinkedList<ServingData>();

        /** The complete gps data. */
        List<CompleteGpsData> completeGpsData = new LinkedList<CompleteGpsData>();

        /**
         * Instantiates a new probe.
         * 
         * @param id the id
         */
        public Probe(String id) {
            super();
            this.id = id;
        }

        /**
         * Adds the complete gps data.
         * 
         * @param elem the elem
         */
        public void addCompleteGpsData(CompleteGpsData elem) {
            completeGpsData.add(elem);
        }

        /**
         * Adds the serving data.
         * 
         * @param elem the elem
         */
        public void addServingData(ServingData elem) {
            servingData.add(elem);
        }

        /**
         * Adds the ntpq.
         * 
         * @param elem the elem
         */
        public void addNtpq(Ntpq elem) {
            ntpq.add(elem);
        }

        /**
         * Adds the neighbor data.
         * 
         * @param elem the elem
         */
        public void addNeighborData(NeighborData elem) {
            neighborData.add(elem);
        }

        /**
         * Adds the mpt sync.
         * 
         * @param elem the elem
         */
        public void addMptSync(MptSync elem) {
            mptSync.add(elem);
        }

        /**
         * Sets the probe id number map.
         * 
         * @param idMap the new probe id number map
         */
        public void setProbeIDNumberMap(ProbeIDNumberMap idMap) {
            probeIDNumberMap = idMap;
        }

        /**
         * Gets the id.
         * 
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * Gets the probe id number map.
         * 
         * @return the probe id number map
         */
        public ProbeIDNumberMap getProbeIDNumberMap() {
            return probeIDNumberMap;
        }

        /**
         * Gets the mpt sync.
         * 
         * @return the mpt sync
         */
        public List<MptSync> getMptSync() {
            return mptSync;
        }

        /**
         * Gets the neighbor data.
         * 
         * @return the neighbor data
         */
        public List<NeighborData> getNeighborData() {
            return neighborData;
        }

        /**
         * Gets the ntpq.
         * 
         * @return the ntpq
         */
        public List<Ntpq> getNtpq() {
            return ntpq;
        }

        /**
         * Gets the serving data.
         * 
         * @return the serving data
         */
        public List<ServingData> getServingData() {
            return servingData;
        }

        /**
         * Gets the complete gps data.
         * 
         * @return the complete gps data
         */
        public List<CompleteGpsData> getCompleteGpsData() {
            return completeGpsData;
        }

    }

    /**
     * <p>
     * GPS data contains parsed information from CompleteGpsData For now supports only
     * </p>
     * .
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class GPSData {

        /** The lat. */
        private Float lat;

        /** The lon. */
        private Float lon;

        /** The delivery time. */
        private Long deliveryTime;

        /** The probe id. */
        private final String probeId;

        /** The gps sentence. */
        private final String gpsSentence;

        /**
         * Instantiates a new gPS data.
         * 
         * @param data the data
         */
        public GPSData(CompleteGpsData data) {
            try {
                deliveryTime = getTime(data.getDeliveryTime());
            } catch (ParseException e) {
                NeoLoaderPlugin.exception(e);
                deliveryTime = null;
            }
            probeId = data.getProbeID();
            gpsSentence = data.getGpsSentence();
            parseGPSCommand();
        }

        /**
         * Parse gps command.
         */
        private void parseGPSCommand() {
            String[] commandArr = gpsSentence.split(",");
            if (commandArr.length != 7 || !commandArr[0].equalsIgnoreCase("$GPGLL") || !commandArr[6].equalsIgnoreCase("A")) {
                lat = null;
                lon = null;
                return;
            }
            lat = Float.parseFloat(commandArr[1]);
            if (commandArr[2].equalsIgnoreCase("S")) {
                lat = -lat;
            }
            lon = Float.parseFloat(commandArr[3]);
            if (commandArr[4].equalsIgnoreCase("W")) {
                lon = -lon;
            }
        }

        /**
         * Gets the lat.
         * 
         * @return the lat
         */
        public Float getLat() {
            return lat;
        }

        /**
         * Gets the lon.
         * 
         * @return the lon
         */
        public Float getLon() {
            return lon;
        }

        /**
         * Gets the delivery time.
         * 
         * @return the delivery time
         */
        public Long getDeliveryTime() {
            return deliveryTime;
        }

        /**
         * Gets the probe id.
         * 
         * @return the probe id
         */
        public String getProbeId() {
            return probeId;
        }

        /**
         * Gets the gps sentence.
         * 
         * @return the gps sentence
         */
        public String getGpsSentence() {
            return gpsSentence;
        }

    }
    public static class AMSCall extends AMSLoader.Call{
        protected String callerPhoneNumber;

        public String getCallerPhoneNumber() {
            return callerPhoneNumber;
        }

        public void setCallerPhoneNumber(String callerPhoneNumber) {
            this.callerPhoneNumber = callerPhoneNumber;
        }
        
    }
}
