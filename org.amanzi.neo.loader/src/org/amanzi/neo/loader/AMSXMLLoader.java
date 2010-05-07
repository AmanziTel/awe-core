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
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.model.ams01.Attachment;
import org.amanzi.neo.loader.model.ams01.CellResel;
import org.amanzi.neo.loader.model.ams01.CompleteGpsData;
import org.amanzi.neo.loader.model.ams01.Events;
import org.amanzi.neo.loader.model.ams01.GroupAttach;
import org.amanzi.neo.loader.model.ams01.Handover;
import org.amanzi.neo.loader.model.ams01.HandoverIsInclusive;
import org.amanzi.neo.loader.model.ams01.InterfaceData;
import org.amanzi.neo.loader.model.ams01.IsInconclusive;
import org.amanzi.neo.loader.model.ams01.ItsiAttach;
import org.amanzi.neo.loader.model.ams01.MptSync;
import org.amanzi.neo.loader.model.ams01.NeighborData;
import org.amanzi.neo.loader.model.ams01.Ntpq;
import org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap;
import org.amanzi.neo.loader.model.ams01.ReceiveMsg;
import org.amanzi.neo.loader.model.ams01.ReceiveMsgIsInclusive;
import org.amanzi.neo.loader.model.ams01.ServingData;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.exolab.castor.xml.Unmarshaller;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

// TODO: Auto-generated Javadoc
/**
 * TODO Purpose of
 * <p>
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AMSXMLLoader extends DriveLoader {
    /*
     * Header Index for Real Dataset
     */
    /** The Constant REAL_DATASET_HEADER_INDEX. */
    private static final int REAL_DATASET_HEADER_INDEX = 0;
    private static SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss,SSSz");

    /*
     * Header Index for Call Dataset
     */
    /** The Constant CALL_DATASET_HEADER_INDEX. */
    private static final int CALL_DATASET_HEADER_INDEX = 1;
    /*
     * Header Index for Probe Network Dataset
     */
    /** The Constant PROBE_NETWORK_HEADER_INDEX. */
    private static final int PROBE_NETWORK_HEADER_INDEX = 2;

    /** The directory name. */
    private final String directoryName;

    /** The network name. */
    private final String networkName;

    /** The network gis. */
    private Node networkGis;

    /** The network node. */
    private Node networkNode;

    /** The gis dataset. */
    private Node gisDataset;

    /** The last dataset node. */
    private Node lastDatasetNode;

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

        addDriveIndexes();

        // timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    }

    /**
     * Adds the drive indexes.
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
                return pathname.isDirectory()||LoaderUtils.getFileExtension(pathname.getName()).equalsIgnoreCase(".xml");
            }
        });

        monitor = SubMonitor.convert(monitor, allFiles.size());
        monitor.beginTask("Loading AMS data", allFiles.size());
        lastDatasetNode = null;
        Transaction tx = neo.beginTx();
        try {
            initializeNetwork(networkName);
            initializeDatasets(dataset);
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
            time=System.currentTimeMillis()-time;
            System.out.println(time);
            time = System.currentTimeMillis();
            Map<String, Probe> probeMap = formatProbeData(interfaceData);
            time=System.currentTimeMillis()-time;
            System.out.println(time);
            for (Map.Entry<String, Probe> entry:probeMap.entrySet()){
                storeProbe(entry.getValue());
            }
            for (Events event:interfaceData.getEvents()){
                storeEvent(event);
            }

        } catch (Exception e) {
            NeoLoaderPlugin.error(String.format("File %s not parsed", logFile.getName()));
            NeoLoaderPlugin.exception(e);
        }
    }





    private void storeEvent(Events event) throws ParseException {
        CellResel cr = event.getCellResel();
        
        if (cr!=null){
            Node eventNode=neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode=eventNode;
            Date date = getTime(cr.getCellReselReq());
            long timestamp = date.getTime();
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode , "cellReselReq",timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_EVENT_TYPE,CellResel.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_PROBE_ID,cr.getProbeID());
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            
            index(eventNode);
            
        }
        GroupAttach gr = event.getGroupAttach();
        if (gr!=null){

            Node eventNode=neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode=eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_EVENT_TYPE,GroupAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_PROBE_ID,gr.getProbeID());
            Node lastMM=null;
            Date date = getTime(gr.getGroupAttachTime());
            long timestamp = date.getTime();
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode , "groupAttachTime",timestamp);
            if (gr.hasErrorCode()){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode , "errorCode",gr.getErrorCode());
            }
            for (Attachment at:gr.getAttachment()){
                Node mm=neo.createNode();
                NeoUtils.addChild(eventNode, mm, lastMM, neo);
                lastMM=mm;
                mm.setProperty("groupType",at.getGroupType());
                mm.setProperty("gssi",at.getGssi());
            }
            index(eventNode);
        }  
        Handover handover = event.getHandover();
        if (handover!=null){
            Node eventNode=neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode=eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_EVENT_TYPE,Handover.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_PROBE_ID,handover.getProbeID());
            if (handover.hasErrorCode()){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"errorCode",handover.getErrorCode());
            }
            if (handover.hasLocationAreaAfter()){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"locationAreaAfter",handover.getLocationAreaAfter());
            }
            if (handover.hasLocationAreaBefore()){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"locationAreaBefore",handover.getLocationAreaBefore());
            }
            Date date = getTime(handover.getHo_Req());
            long timestamp = date.getTime();
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"ho_Req",timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"ho_accept",getTime(handover.getHo_Accept()).getTime());
            HandoverIsInclusive isIncl = handover.getHandoverIsInclusive();
            if (isIncl!=null){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"reason",isIncl.getReason());
               if (isIncl.hasErrCode()){
                   setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"errorCode",isIncl.getErrCode());
               }
            }
            index(eventNode);
        }
        ItsiAttach itAt=event.getItsiAttach();
        if (itAt!=null){
            Node eventNode=neo.createNode();
            NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
            lastDatasetNode=eventNode;
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_EVENT_TYPE,ItsiAttach.class.getName());
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_PROBE_ID,itAt.getProbeID());
            Date date = getTime(itAt.getItsiAtt_Req());
            long timestamp = date.getTime();
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"itsiAtt_Req",timestamp);
            setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"itsiAtt_Accept",getTime(itAt.getItsiAtt_Accept()).getTime());
            if (itAt.hasErrorCode()){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"errorCode",itAt.getErrorCode());
            }
            if (itAt.hasLocationAreaAfter()){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"locationAreaAfter",itAt.getLocationAreaAfter());
            }
            if (itAt.hasLocationAreaBefore()){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"locationAreaBefore",itAt.getLocationAreaBefore());
            }
            IsInconclusive isIncl = itAt.getIsInconclusive();
            if (isIncl!=null){
                setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"reason",isIncl.getReason());
               if (isIncl.hasErrCode()){
                   setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"errorCode",isIncl.getErrCode());
               }
            }
            index(eventNode);
        }
       
        ReceiveMsg rMsg=event.getReceiveMsg();
       if (rMsg!=null){
           Node eventNode=neo.createNode();
           NeoUtils.addChild(datasetNode, eventNode, lastDatasetNode, neo);
           lastDatasetNode=eventNode;
           setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_EVENT_TYPE,ItsiAttach.class.getName());
           setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,INeoConstants.M_PROBE_ID,rMsg.getProbeID());
           Date date = getTime(rMsg.getReceiveTime());
           long timestamp = date.getTime();
           eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
           updateTimestampMinMax(REAL_DATASET_HEADER_INDEX, timestamp);
           setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"receiveTime",timestamp);
           if (rMsg.hasDataLength()){
               setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"dataLength",rMsg.getDataLength());
           }
           

            ReceiveMsgIsInclusive isIncl = rMsg.getReceiveMsgIsInclusive();
           if (isIncl!=null){
               setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"reason",isIncl.getReason());
              if (isIncl.hasErrCode()){
                  setNewIndexProperty(getHeaderMap(REAL_DATASET_HEADER_INDEX).headers,eventNode ,"errorCode",isIncl.getErrCode());
              }
           }
           index(eventNode);
       }
       event.getSendMsg();
       event.getToc();
       event.getTpc();
       event.getTtc();
    }

    /**
     *
     * @param cellReselReq
     * @return
     * @throws ParseException 
     */
    private Date getTime(String timestamp) throws ParseException {
        int i = timestamp.lastIndexOf(':');
        StringBuilder time=new StringBuilder(timestamp.substring(0, i)).append(timestamp.substring(i+1,timestamp.length()));
        return formatter.parse(time.toString());
    }

    /**
     * Store probe.
     *
     * @param probe the probe
     */
    private void storeProbe(Probe probe) {
        Node probeNew = NeoUtils.findOrCreateProbeNode(networkNode, probe.getId(), neo);
       ProbeIDNumberMap map = probe.getProbeIDNumberMap();
       setNewIndexProperty(getHeaderMap(PROBE_NETWORK_HEADER_INDEX).headers,probeNew , "phoneNumber", map.getPhoneNumber());
       String fr = map.getFrequency();
       if (fr!=null){
           setNewIndexProperty(getHeaderMap(PROBE_NETWORK_HEADER_INDEX).headers,probeNew , "phoneNumber", Double.parseDouble(fr));
       }
       setNewIndexProperty(getHeaderMap(PROBE_NETWORK_HEADER_INDEX).headers,probeNew , "phoneNumber", map.getLocationArea());
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

            // callDataset = getVirtualDataset(DriveTypes.AMS_CALLS);

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
        for (CompleteGpsData elem : interfaceData.getGpsData().getCompleteGpsDataList().getCompleteGpsData()) {
            String id = elem.getProbeID();
            Probe probe = result.get(id);
            if (probe == null) {
                probe = new Probe(id);
                result.put(id, probe);
            }
            probe.addCompleteGpsData(elem);
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
        ProbeIDNumberMap probeIDNumberMap=null;
        
        /** The mpt sync. */
        List<MptSync> mptSync=new LinkedList<MptSync>();
        
        /** The neighbor data. */
        List<NeighborData> neighborData=new LinkedList<NeighborData>();
        
        /** The ntpq. */
        List<Ntpq> ntpq=new LinkedList<Ntpq>();
        
        /** The serving data. */
        List<ServingData> servingData=new LinkedList<ServingData>();
        
        /** The complete gps data. */
        List<CompleteGpsData> completeGpsData=new LinkedList<CompleteGpsData>();
        
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
            probeIDNumberMap=idMap;
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
}
