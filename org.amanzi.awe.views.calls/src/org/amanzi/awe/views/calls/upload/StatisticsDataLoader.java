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

package org.amanzi.awe.views.calls.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.amanzi.awe.views.calls.enums.StatisticsType;
import org.amanzi.awe.views.calls.statistics.CallStatisticsUtills;
import org.amanzi.awe.views.calls.statistics.Statistics;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.CallProperties;
import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NetworkTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.enums.ProbeCallRelationshipType;
import org.amanzi.neo.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;

/**
 * <p>
 * Load statistics data from csv files.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class StatisticsDataLoader {
    
    private static final Logger LOGGER = Logger.getLogger(StatisticsDataLoader.class);
    
    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final String COLUMN_SEPARATOR = ",";
    
    private static final String TIME_SEPARATOR = "_";
    private static final String DAY_PATTERN = "yyyyMMdd";
    private static final String TIME_PATTERN = "HHmm";
    
    private static final String START_COLUMN_NAME = "STARTTIME";
    private static final String END_COLUMN_NAME = "ENDTIME";
    private static final String PROBE_COLUMN_NAME = "HOST";
    private static final String LA_COLUMN_NAME = "LA";
    private static final String FREQUENCY_COLUMN_NAME = "FREQUENCY";
    private static final int MIN_COLUMN_COUNT = 6;
    
    private String directoryName;
    private String networkName;
    private String datasetName;
    
    private Long minTime;
    private Long maxTime;
    
    private boolean isTest=false;
    
    private GraphDatabaseService service;
    private Transaction transaction;
    
    private Node network;
    private Node virtualDataset;
    private HashMap<String, Node> gisNodes = new HashMap<String, Node>();
    private HashMap<String, Node> probes = new HashMap<String, Node>();
    
    private HashMap<StatisticsCallType,HashMap<CallTimePeriods, Node>> previousRowsAll = new HashMap<StatisticsCallType,HashMap<CallTimePeriods, Node>>();
    private HashMap<StatisticsCallType,HashMap<CallTimePeriods, Node>> previousCellsAll = new HashMap<StatisticsCallType,HashMap<CallTimePeriods, Node>>();
    
    private HashMap<StatisticsCallType, StatInfo> statRoots = new HashMap<StatisticsCallType, StatInfo>();
    
    private HeaderMap headerMap;
    
    /**
     * Constructor.
     * @param directory String (name of file directory)
     * @param dataset String (virtual dataset name)
     * @param network String (network name)
     * @param neo GraphDatabaseService
     */
    public StatisticsDataLoader(String directory, String dataset, String network, GraphDatabaseService neo){
        directoryName = directory;
        networkName = network;
        datasetName = dataset;
        service = neo;
        if(service == null){
            service = NeoServiceProviderUi.getProvider().getService();
        }
        initHeaders();
    }
    
    /**
     * Constructor.
     * @param directory String (name of file directory)
     * @param dataset String (virtual dataset name)
     * @param network String (network name)
     * @param neo GraphDatabaseService
     */
    public StatisticsDataLoader(String directory, String dataset, String network, GraphDatabaseService neo, boolean testing){
        this(directory, dataset, network, neo);
        isTest = testing;
    }
    
    /**
     * @return Returns the virtualDataset.
     */
    public Node getVirtualDataset() {
        return virtualDataset;
    }
    
    /**
     * Run to load data.
     *
     * @param monitor IProgressMonitor
     * @throws IOException (problem in load data)
     */
    public void run(IProgressMonitor monitor) throws IOException {
        if(monitor == null){
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask("Loading AMS statistics data", 2);
        monitor.subTask("Searching for files to load");
        ArrayList<File> allFiles = getAllFiles(directoryName);
        int filesCount = allFiles.size();
        IProgressMonitor subMonitor = SubMonitor.convert(monitor, filesCount);
        transaction = service.beginTx();
        try{
            subMonitor.beginTask("Loading AMS statistics data", filesCount);
            network = findOrCreateNetworkNode(); 
            virtualDataset = findVirtualDataset();
            for (File file : allFiles) {
                if(monitor.isCanceled()){
                    break;
                }
                subMonitor.subTask("Loading file " + file.getAbsolutePath());
                loadFile(file);
                subMonitor.worked(1);                   
            }
            subMonitor.done();
            buildHighPeriodStatistics(monitor);
            finish();
        }catch(Throwable e){
            LOGGER.error("Problem in loader.",e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        finally{
            commit(false);
        }
    }
    
    /**
     * Initialize headers.
     */
    private void initHeaders(){
        headerMap = new HeaderMap();
        headerMap.addDataHeader(new Header(START_COLUMN_NAME, null,null, ValueType.TIME));
        headerMap.addDataHeader(new Header(END_COLUMN_NAME, null, null, ValueType.TIME));
        headerMap.addDataHeader(new Header(PROBE_COLUMN_NAME, null, null, ValueType.STRING));
        headerMap.addDataHeader(new Header(LA_COLUMN_NAME, null, null, ValueType.INTEGER));
        headerMap.addDataHeader(new Header(FREQUENCY_COLUMN_NAME, null, null, ValueType.FLOAT));
    }
    
    /**
     * Get all files from directory
     *
     * @param directoryName String
     * @return List of File.
     */
    private ArrayList<File> getAllFiles(String directoryName) {
        File directory = new File(directoryName);
        ArrayList<File> result = new ArrayList<File>();        
        for (File childFile : directory.listFiles()) {
            if (childFile.isDirectory()) {
                result.addAll(getAllFiles(childFile.getPath()));
            }
            else if (childFile.isFile() && childFile.getName().endsWith(CSV_FILE_EXTENSION)) {
                result.add(childFile);
            }
        }
        return result;
        
    }
    
    /**
     * Commit changes.
     *
     * @param restart boolean (need to restart transaction)
     */
    private void commit(boolean restart) {
        if (transaction != null) {
            transaction.success();
            transaction.finish();
            if (restart) {
                transaction = service.beginTx();
            } else {
                transaction = null;
            }
        }
    }
    
    /**
     * Finish all.
     */
    private void finish(){ 
        if (minTime!=null&&maxTime!=null) {
            network.setProperty(INeoConstants.MIN_TIMESTAMP, minTime);
            network.setProperty(INeoConstants.MAX_TIMESTAMP, maxTime);
            virtualDataset.setProperty(INeoConstants.MIN_TIMESTAMP, minTime);
            virtualDataset.setProperty(INeoConstants.MAX_TIMESTAMP, maxTime);
        }
        if (!isTest) {
            NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(new UpdateDatabaseEvent(UpdateViewEventType.GIS));
        }
        try {
            addDataToCatalog();
        } catch (MalformedURLException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
    
    /**
     * Add loaded data to catalog.
     *
     * @throws MalformedURLException
     */
    private void addDataToCatalog() throws MalformedURLException {
        NeoServiceProviderUi neoProvider = NeoServiceProviderUi.getProvider();
        if (neoProvider != null) {
            String databaseLocation = neoProvider.getDefaultDatabaseLocation();
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            URL url = new URL("file://" + databaseLocation);
            List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
            for (IService service : services) {
                if (catalog.getById(IService.class, service.getIdentifier(), new NullProgressMonitor()) != null) {
                    catalog.replace(service.getIdentifier(), service);
                } else {
                    catalog.add(service);
                }
            }
            neoProvider.commit();
        }
    }
    
    /**
     * @return dataset node
     */
    private Node findOrCreateNetworkNode() {        
        if (networkName == null || networkName.isEmpty()) {
            return null;
        }
        else {
            networkName = networkName.trim();
        }
        
        Node gis = findOrCreateGISNode(networkName,null, GisTypes.NETWORK.getHeader(),NetworkTypes.PROBE);              
        Node result = NeoUtils.findOrCreateNetworkNode(gis, networkName, directoryName, service);            
        return result;
    }
    
    /**
     * Gets gis node for dataset or network.
     *
     * @param mainNode Node
     * @param gisType String
     * @return Node
     */
    private Node findOrCreateGISNode(String gisName, Node mainNode, String gisType, NetworkTypes fileType) {
        if (gisName==null||gisName.length()==0) {
            gisName = NeoUtils.getNodeName(mainNode, service);
        }
        Node gis = gisNodes.get(gisName);

        if (gis == null) {
            Transaction transaction = service.beginTx();
            try {
                Node reference = service.getReferenceNode();
                gis = NeoUtils.findGisNode(gisName, service);
                if (gis == null) {
                    gis = NeoUtils.createGISNode(reference, gisName, gisType, service);
                    if (mainNode!=null) {
                        boolean hasRelationship = false;
                        for (Relationship relation : gis.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)) {
                            if (relation.getEndNode().equals(mainNode)) {
                                hasRelationship = true;
                            }
                        }
                        if (!hasRelationship) {
                            gis.createRelationshipTo(mainNode, GeoNeoRelationshipTypes.NEXT);
                        }
                    }
                    if(fileType!=null){
                        fileType.setTypeToNode(gis, service);
                    }
                }
                gisNodes.put(gisName, gis);
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
        return gis;
    }
    
    /**
     * @return empty virtual dataset node (calls).
     */
    private Node findVirtualDataset() {
        Node realDataset = findDatasetNode();
        Node result;
        DriveTypes amsCalls = DriveTypes.AMS_CALLS;
        if(realDataset!=null){
            result = NeoUtils.findOrCreateVirtualDatasetNode(realDataset, amsCalls, service);
            datasetName = NeoUtils.getNodeName(result,service);
        }else{
            datasetName = amsCalls.getFullDatasetName(datasetName);
            result = service.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.DATASET.getId());
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, datasetName);
            result.setProperty(INeoConstants.DRIVE_TYPE, amsCalls.getId());
        }        
        findOrCreateGISNode(datasetName,result, GisTypes.DRIVE.getHeader(),null); 
        return result;
    }
    
    /**
     * Find real dataset node.
     *
     * @return Node
     */
    protected final Node findDatasetNode() {        
        if (datasetName == null || datasetName.isEmpty()) {
            return null;
        }
        Traverser traverse = NeoUtils.getAllDatasetTraverser(service.getReferenceNode());
        for (Node node : traverse) {
            if (node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(datasetName)) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * Load data from file.
     *
     * @param file File
     * @throws IOException (problem in reading file)
     * @throws ParseException (problem in parse data)
     */
    private void loadFile(File file) throws IOException, ParseException{
        FileInputStream is = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        boolean hasHeaders = false;
        while ((line = reader.readLine()) != null) {
            if(!hasHeaders){
                hasHeaders = parseHeaders(line);
                if(!headerMap.hasStatisticsHeaders()){
                    break;
                }
            }else{
                parseLine(line);
            }
        }
        commit(true);
    }
    
    /**
     * Parse headers for file.
     *
     * @param line String
     * @return boolean
     */
    private boolean parseHeaders(String line){
        String[] cells = line.split(COLUMN_SEPARATOR);
        int callCount = cells.length;
        if(callCount<2){
            return false;
        }
        headerMap.clear();
        for(int i=0; i<callCount;i++){
            headerMap.addHeader(i, cells[i]);
        }
        return true;
    }
    
    /**
     * Parse line
     *
     * @param line String
     * @throws ParseException (problem in parse)
     */
    private void parseLine(String line) throws ParseException{
        LineWrapper lw = headerMap.buildLineWrapper(line);
        if(lw == null||!lw.hasAnyStatistics()){
            return;
        }
        String probeName = lw.getProbeName();
        Integer la = lw.getLa();
        Float freq = lw.getFreq();
        Long start = lw.getStart();
        Long end = lw.getEnd();
        updateMinMax(start, end);
        List<StatisticsCallType> allTypes = StatisticsCallType.getTypesByLevel(StatisticsCallType.FIRST_LEVEL);
        for(StatisticsCallType callType : allTypes){
            if(!lw.hasStatistics(callType)){
                continue;
            }            
            Node periodNode = getPeriodNode(callType, CallTimePeriods.HOURLY,null);
            Node probe = getProbe(callType, probeName, la, freq);
            statRoots.get(callType).addProbe(probe);
            Node row = createRow(callType,CallTimePeriods.HOURLY,start, periodNode, probe, null);
            Statistics statistics = lw.getStatistics(callType);
            for(IStatisticsHeader header : callType.getHeaders()){
                createCell(callType,CallTimePeriods.HOURLY,row,header,statistics);
            }
            previousCellsAll.get(callType).put(CallTimePeriods.HOURLY, null);
            commit(true);
        }
    }
    
    /**
     * Update min/max times.
     *
     * @param start Long
     * @param end Long
     */
    private void updateMinMax(Long start, Long end){
        if(minTime==null||start<minTime){
            minTime=start;
        }
        if(maxTime==null||end>maxTime){
            maxTime=end;
        }
    }
    
    /**
     * Get period node.
     *
     * @param callType StatisticsCallType
     * @param period CallTimePeriods
     * @return Node
     */
    private Node getPeriodNode(StatisticsCallType callType, CallTimePeriods period, Node parentStat){
        StatInfo statInfo = statRoots.get(callType);
        if(statInfo==null){
            Node root = createRootStatisticsNode(callType);
            statInfo = new StatInfo(root);
            statRoots.put(callType, statInfo);
        }
        Node result = statInfo.getPeriodNode(period);
        if(result == null){
            result = createPeriodNode(statInfo.getRoot(), period, statInfo.getPeriodNode(period.getUnderlyingPeriod()), parentStat);
            statInfo.addPeriodNode(period, result);
        }
        return result;
    }
    
    /**
     * Create root of statistics
     *
     * @param callType StatisticsCallType
     * @return Node
     */
    private Node createRootStatisticsNode(StatisticsCallType callType) {
        Node result = service.createNode();
        
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.CALL_ANALYSIS_ROOT.getId());
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, INeoConstants.CALL_ANALYZIS_ROOT);
        result.setProperty(INeoConstants.PROPERTY_VALUE_NAME, NeoUtils.getNodeName(virtualDataset,service));
        result.setProperty(CallProperties.CALL_TYPE.getId(), callType.toString());
        
        virtualDataset.createRelationshipTo(result, ProbeCallRelationshipType.CALL_ANALYSIS);
        
        return result;
    }
    
    /**
     * Create node for statistics period.
     *
     * @param parent Node (statistics root)
     * @param period CallTimePeriods
     * @param undPeriodNode (underling period node)
     * @return Node
     */
    private Node createPeriodNode(Node parent, CallTimePeriods period, Node undPeriodNode,Node parentPeriodNode) {
        Node result = service.createNode();
        
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, period.getId());
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.CALL_ANALYSIS.getId());
        parent.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
        
        if (undPeriodNode!=null) {
            result.createRelationshipTo(undPeriodNode, GeoNeoRelationshipTypes.SOURCE);
        }
        if (parentPeriodNode!=null) {
            parentPeriodNode.createRelationshipTo(result, GeoNeoRelationshipTypes.SOURCE);
        }
        return result;
    }
    
    /**
     * Get probe.
     *
     * @param callType StatisticsCallType
     * @param probeName String
     * @param la Integer
     * @param frequency Float
     * @return Node
     */
    private Node getProbe(StatisticsCallType callType, String probeName, Integer la, Float frequency){
        Node probe = probes.get(probeName);
        if(probe==null){            
            probe = NeoUtils.findOrCreateProbeNode(network, probeName, service);
            probe.setProperty(INeoConstants.PROBE_LA, la); 
            probe.setProperty(INeoConstants.PROBE_F, frequency);

            probes.put(probeName, probe);
        }
        Node calls = getProbeCalls(probe, probeName);
        calls.setProperty(callType.getId().getProperty(), true);
        if(!calls.hasRelationship(ProbeCallRelationshipType.PROBE_DATASET, Direction.INCOMING)){
            virtualDataset.createRelationshipTo(calls, ProbeCallRelationshipType.PROBE_DATASET);
        }
        return probe;
    }
    
    /**
     * Find or create probe calls
     *
     * @param probe Node
     * @param probeName String
     * @return Node
     * @throws IOException
     */
    private Node getProbeCalls(Node probe, String probeName){
        Relationship link = probe.getSingleRelationship(ProbeCallRelationshipType.CALLS, Direction.OUTGOING);
        String callProbeName = probeName + " - " + datasetName;
        if(link == null){
            Node calls = service.createNode();
            calls.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.CALLS.getId());
            calls.setProperty(INeoConstants.PROPERTY_NAME_NAME, callProbeName);
            calls.setProperty(NodeTypes.DATASET.getId(), datasetName);

            probe.createRelationshipTo(calls, ProbeCallRelationshipType.CALLS);
            return calls;
        }
        return link.getEndNode();
    }
    
    /**
     * Create row.
     *
     * @param callType StatisticsCallType
     * @param period CallTimePeriods
     * @param start Long
     * @param parent Node
     * @param probe Node
     * @param sourceRows List of Nodes
     * @return Node
     */
    private Node createRow(StatisticsCallType callType, CallTimePeriods period, Long start, Node parent, Node probe, Node highLevelRow){
        Node result = service.createNode();
        String name = NeoUtils.getFormatDateStringForSrow(start, period.addPeriod(start), "HH:mm", period.getId());
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_ROW.getId());
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
        result.setProperty(INeoConstants.PROPERTY_TIME_NAME, start);
        
        result.createRelationshipTo(probe, GeoNeoRelationshipTypes.SOURCE);
        
        HashMap<CallTimePeriods, Node> previousRows = previousRowsAll.get(callType);
        if(previousRows==null){
            previousRows = new HashMap<CallTimePeriods, Node>();
            previousRowsAll.put(callType, previousRows);
        }
        Node previous = previousRows.get(period);
        NeoUtils.addChild(parent, result, previous, service);
        previousRows.put(period, result);
        
        if (highLevelRow != null) {
            highLevelRow.createRelationshipTo(result, GeoNeoRelationshipTypes.SOURCE);
        }
        
        return result;
    }
    
    /**
     * Create cell node.
     *
     * @param callType StatisticsCallType
     * @param period CallTimePeriods
     * @param row Node
     * @param header IStatisticsHeader
     * @param statistics Statistics
     * @return Node
     */
    private Node createCell(StatisticsCallType callType, CallTimePeriods period, Node row, IStatisticsHeader header, Statistics statistics){
        Node result = service.createNode();
        
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_CELL.getId());
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, header.getTitle());
        Object value = statistics.get(header);
        if (value != null) {
            result.setProperty(INeoConstants.PROPERTY_VALUE_NAME, value);                       
        }
        HashMap<CallTimePeriods, Node> previousCells = previousCellsAll.get(callType);
        if(previousCells==null){
            previousCells = new HashMap<CallTimePeriods, Node>();
            previousCellsAll.put(callType, previousCells);
        }
        Node previous = previousCells.get(period);
        NeoUtils.addChild(row, result, previous, service);
        previousCells.put(period, result);
        
        ArrayList<Node> sourceCells = statistics.getAllAffectedCalls(header);
        if (sourceCells != null) {
            for(Node sourceCell : sourceCells){
                result.createRelationshipTo(sourceCell, GeoNeoRelationshipTypes.SOURCE);
            }
            statistics.getAllAffectedCalls(header).clear();
        }
        statistics.updateSourceNodes(header, result);
        return result;
    }
    
    /**
     * Build statistics for higher periods.
     *
     * @param monitor
     */
    private void buildHighPeriodStatistics(IProgressMonitor monitor){
        CallTimePeriods highestPeriod = CallStatisticsUtills.getHighestPeriod(minTime, maxTime-CallStatisticsUtills.HOUR);
        monitor.subTask("Build higher periods statistics");
        for(StatisticsCallType callType : statRoots.keySet()){  
            monitor.subTask("Build statistics for "+callType.getViewName()+" calls.");
            StatInfo statInfo = statRoots.get(callType);
            for(Node probe : statInfo.getLinkedProbes()){
                if(monitor.isCanceled()){
                    break;
                }
                buildHighStatistics(callType,highestPeriod,null,null,probe,minTime,maxTime);
            }
            commit(true);
        }
    }
    
    /**
     * Build statistics for higher periods.
     *
     * @param callType StatisticsCallType
     * @param period  CallTimePeriods
     * @param parentStat Node
     * @param highLevelRow Node
     * @param probe Node
     * @param startAll Node
     * @param endAll Node
     * @return Statistics
     */
    private Statistics buildHighStatistics(StatisticsCallType callType, CallTimePeriods period,Node parentStat, Node highLevelRow, Node probe, Long startAll, Long endAll){
        if(period.equals(CallTimePeriods.HOURLY)){
            return getHourlyStatistics(callType, highLevelRow, probe, startAll, endAll);
        }
        Statistics result = new Statistics();
        Node periodNode = getPeriodNode(callType, period,parentStat);
        Long start = period.getFirstTime(startAll);
        Long end = CallStatisticsUtills.getNextStartDate(period, endAll, start);
        if(start<startAll){
            start = startAll;
        }
        do{
            Node row = createRow(callType, period, period.getFirstTime(start), periodNode, probe, highLevelRow);
            Statistics currStatistics = buildHighStatistics(callType, period.getUnderlyingPeriod(), periodNode,row, probe, start, end);
            for(IStatisticsHeader header : callType.getHeaders()){
                createCell(callType,CallTimePeriods.HOURLY,row,header,currStatistics);
            }
            previousCellsAll.get(callType).put(CallTimePeriods.HOURLY, null);
            
            updateStatistics(result, currStatistics);
            
            start = end;
            end = CallStatisticsUtills.getNextStartDate(period, endAll, start);
        }while(start<endAll);
        return result;
    }
    
    /**
     * Get hourly statistics for database.
     *
     * @param callType StatisticsCallType
     * @param probe Node
     * @param start Node
     * @param end Node
     * @return Statistics
     */
    private Statistics getHourlyStatistics(StatisticsCallType callType, Node highLevelRow, final Node probe,final Long start,final Long end){
        Node periodNode = getPeriodNode(callType, CallTimePeriods.HOURLY, null);
        Traverser rows = NeoUtils.getChildTraverser(periodNode, new ReturnableEvaluator() {            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                if(!NeoUtils.isSRowNode(node)){
                    return false;
                }
                Long rowTime = (Long)node.getProperty(INeoConstants.PROPERTY_TIME_NAME, 0L);
                if(rowTime<start||rowTime>=end){
                    return false;
                }
                Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING);
                return relation!=null&&relation.getEndNode().equals(probe);
            }
        });
        Statistics result = new Statistics();
        for(Node row : rows){
            Traverser cells = NeoUtils.getChildTraverser(row);
            for (Node cell : cells) {
                IStatisticsHeader header = callType.getHeaderByTitle((String)cell.getProperty(INeoConstants.PROPERTY_NAME_NAME));
                Number value = getCellValue(cell, header);
                result.updateHeaderWithCall(header, value, cell);
            }
            if (highLevelRow!=null) {
                highLevelRow.createRelationshipTo(row, GeoNeoRelationshipTypes.SOURCE);
            }
        }
        return result;
    }
    
    /**
     * Gets value from cell by cell type.
     *
     * @param cell Node
     * @param header StatisticsHeaders
     * @return Long
     */
    protected Number getCellValue(Node cell, IStatisticsHeader header){
        if(header.getType().equals(StatisticsType.COUNT)){
            Integer value = (Integer)cell.getProperty(INeoConstants.PROPERTY_VALUE_NAME,0);
            return value;
        }
        return (Float)cell.getProperty(INeoConstants.PROPERTY_VALUE_NAME,0f);
    }
    
    /**
     * Update statistics.
     *
     * @param original Statistics
     * @param newValues Statistics
     */
    private void updateStatistics(Statistics original, Statistics newValues) { 
        for (Entry<IStatisticsHeader, Object> entry : newValues.entrySet()) {
            IStatisticsHeader header = entry.getKey();
            original.updateHeader(header, entry.getValue());
            original.copyAllSourceNodes(header, newValues.getAllAffectedCalls(header));
        }            
    }
    
    /**
     * <p>
     * Types of values.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private enum ValueType{
        TIME,
        STRING,
        INTEGER,
        FLOAT
    }
    
    /**
     * <p>
     * Column header.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class Header{
        
        private String name;
        private ValueType parsedType;
        private StatisticsCallType callType;
        private IStatisticsHeader realHeader;
        
        public Header(String columnName, StatisticsCallType statType, IStatisticsHeader real, ValueType type) {
            name = columnName;
            parsedType = type;
            realHeader = real;
            callType = statType;
        }
        
        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }
        
        /**
         * @return Returns the callType.
         */
        public StatisticsCallType getCallType() {
            return callType;
        }
        
        /**
         * @return Returns the realHeader.
         */
        public IStatisticsHeader getRealHeader() {
            return realHeader;
        }
        
        /**
         * Parse value
         *
         * @param value String
         * @return Object
         * @throws ParseException (problem in parse)
         */
        public Object parseValue(String value) throws ParseException{
            switch (parsedType) {
            case TIME:
                return getTimeFromString(value);
            case STRING:
                return value;
            case INTEGER:
                if(value==null||value.length()==0){
                    return null;
                }
                return Integer.valueOf(value);
            case FLOAT:
                if(value==null||value.length()==0){
                    return null;
                }
                return Float.valueOf(value);
            default:
                return null;
            }
        }
        
        /**
         * Parse string to time.
         *
         * @param value String.
         * @return Long
         * @throws ParseException (problem in parse)
         */
        private Long getTimeFromString(String value) throws ParseException{
            if(value==null||value.length()==0){
                return null;
            }
            String[] parts = value.split(TIME_SEPARATOR);
            if(parts.length<2){
                return null;
            }
            SimpleDateFormat sf = new SimpleDateFormat(DAY_PATTERN);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(sf.parse(parts[0]));
            sf = new SimpleDateFormat(TIME_PATTERN);
            Calendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(sf.parse(parts[1]));
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            return calendar.getTimeInMillis();
        }
    }
    
    /**
     * <p>
     * Map of all headers.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class HeaderMap{
        
        private HashMap<Integer, Header> allHeaders = new HashMap<Integer, Header>();
        
        private List<Header> unknownHeaders = new ArrayList<Header>();
        private HashMap<String, Header> dataHeaders = new HashMap<String, Header>();
        
        private boolean hasStatisticsHeaders = false;
        
        /**
         * Add header to map.
         *
         * @param number int
         * @param name String
         * @return Header.
         */
        public Header addHeader(int number, String name){
            Header header = allHeaders.get(number);
            if(header!=null){
                return header;
            }
            header = dataHeaders.get(name);
            if(header!=null){
                allHeaders.put(number, header);
                return header;
            }
            HeaderTypes headerType = HeaderTypes.getTypeByHeader(name);
            if(headerType!=null){
                StatisticsCallType statType = headerType.getRealType();
                IStatisticsHeader statHeader = getStatisticsHeader(statType, name);
                if (statHeader!=null) {
                    ValueType type = statHeader.getType().equals(StatisticsType.COUNT) ? ValueType.INTEGER : ValueType.FLOAT;
                    header = new Header(name, statType, statHeader, type);
                    hasStatisticsHeaders = true;
                }
            }
            if(header==null){
                header = new Header(name, null, null, ValueType.STRING);
                unknownHeaders.add(header);
            }
            allHeaders.put(number, header);
            return header;
        }
        
        /**
         * Find real statistics header.
         *
         * @param callType StatisticsCallType
         * @param name String
         * @return StatisticsHeaders
         */
        private IStatisticsHeader getStatisticsHeader(StatisticsCallType callType, String name){
            if(callType==null){
                return null;
            }
            if(callType.equals(StatisticsCallType.GROUP)&&name.equals("SL-SRV-GC-1_ATTEMPT")){
                return StatisticsHeaders.CALL_ATTEMPT_COUNT;
            }
            for(IStatisticsHeader header : callType.getHeaders()){
                if(name.endsWith(header.getTitle())){
                    return header;
                }
            }            
            return null; 
        }
        
        /**
         * @return has statistics headers.
         */
        public boolean hasStatisticsHeaders(){
            return hasStatisticsHeaders;
        }
        
        /**
         * Add header with additional data
         *
         * @param header
         */
        public void addDataHeader(Header header){
            dataHeaders.put(header.getName(), header);
        }
        
        /**
         * Clear header map.
         */
        public void clear(){
            unknownHeaders.clear();
            allHeaders.clear();
        }
        
        /**
         * Build line wrapper.
         *
         * @param line String 
         * @return LineWrapper
         * @throws ParseException
         */
        public LineWrapper buildLineWrapper(String line) throws ParseException{
            if(line==null||line.length()==0){
                return null;
            }
            String[] splitted = line.split(COLUMN_SEPARATOR);
            int count = splitted.length;
            if(count<MIN_COLUMN_COUNT){
                return null;
            }
            LineWrapper result = new LineWrapper();
            for(int i=0; i<count;i++){
                Header header = allHeaders.get(i);
                if(header==null||unknownHeaders.contains(header)){
                    continue;
                }
                Object value = header.parseValue(splitted[i]);
                if(value==null){
                    continue;
                }
                if(header.getName().equals(START_COLUMN_NAME)){
                    result.setStart((Long)value);
                    continue;
                }
                if(header.getName().equals(END_COLUMN_NAME)){
                    result.setEnd((Long)value);
                    continue;
                }
                if(header.getName().equals(PROBE_COLUMN_NAME)){
                    result.setProbeName((String)value);
                    continue;
                }
                if(header.getName().equals(LA_COLUMN_NAME)){
                    result.setLa((Integer)value);
                    continue;
                }
                if(header.getName().equals(FREQUENCY_COLUMN_NAME)){
                    result.setFreq((Float)value);
                    continue;
                }
                result.updateStatistics(header.getCallType(), header.getRealHeader(), value);
            }
            return result;
        }
        
    }
    
    /**
     * <p>
     * Types of headers.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private enum HeaderTypes{
        
        INDIVIDUAL(StatisticsCallType.INDIVIDUAL, "SL-SRV-SC"),
        GROUP(StatisticsCallType.GROUP, "SL-SRV-GC"),
        ITSI_ATTACH(StatisticsCallType.ITSI_ATTACH,"SL-INH-ATT"),
        ITSI_CC(StatisticsCallType.ITSI_CC,"SL-INH-CC_RES"),
        ITSI_HO(StatisticsCallType.ITSI_HO,"SL-INH-CC_HO"),
        TSM(StatisticsCallType.TSM,"SL-SRV-TSM"),
        SDS(StatisticsCallType.SDS,"SL-SRV-SDS"),
        EMERGENCY(StatisticsCallType.EMERGENCY,"SL-SRV-EC-1"),
        HELP(StatisticsCallType.HELP,"SL-SRV-EC-2"),
        ALARM(StatisticsCallType.ALARM,"SL-SRV-ALM"),
        CS_DATA(StatisticsCallType.CS_DATA,"SL-SRV-CSD"),
        PS_DATA(StatisticsCallType.PS_DATA,"SL-SRV-IP");        
        
        private StatisticsCallType realType;
        private String prefix;
        
        /**
         * Constructor.
         * @param type
         * @param headerPrefix
         */
        private HeaderTypes(StatisticsCallType type, String headerPrefix) {
            realType = type;
            prefix = headerPrefix;
        }
        
        /**
         * @return Returns the realType.
         */
        public StatisticsCallType getRealType() {
            return realType;
        }
        
        /**
         * Get type by header.
         *
         * @param header String
         * @return HeaderTypes
         */
        public static HeaderTypes getTypeByHeader(String header){
            for(HeaderTypes type : values()){
                if(header.startsWith(type.prefix)){
                    return type;
                }
            }
            return null;
        }
    }
    
    /**
     * <p>
     * Line wrapper.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class LineWrapper{
        
        private Long start;
        private Long end;
        private String probeName;
        private Integer la;
        private Float freq;
        private HashMap<StatisticsCallType, Statistics> allStatistics = new HashMap<StatisticsCallType, Statistics>();
        private Set<StatisticsCallType> havingStatistics = new HashSet<StatisticsCallType>();
        
        /**
         * @return Returns the start.
         */
        public Long getStart() {
            return start;
        }
        
        /**
         * @param start The start to set.
         */
        public void setStart(Long start) {
            this.start = start;
        }
        
        /**
         * @return Returns the end.
         */
        public Long getEnd() {
            return end;
        }
        
        /**
         * @param end The end to set.
         */
        public void setEnd(Long end) {
            this.end = end;
        }
        
        /**
         * @return Returns the probeName.
         */
        public String getProbeName() {
            return probeName;
        }
        
        /**
         * @param probeName The probeName to set.
         */
        public void setProbeName(String probeName) {
            this.probeName = probeName;
        }
        
        /**
         * @return Returns the la.
         */
        public Integer getLa() {
            return la;
        }
        
        /**
         * @param la The la to set.
         */
        public void setLa(Integer la) {
            this.la = la;
        }
        
        /**
         * @return Returns the freq.
         */
        public Float getFreq() {
            return freq;
        }
        
        /**
         * @param freq The freq to set.
         */
        public void setFreq(Float freq) {
            this.freq = freq;
        }
        
        /**
         * Has satistics for callType.
         *
         * @param callType
         * @return boolean
         */
        public boolean hasStatistics(StatisticsCallType callType){
            return havingStatistics.contains(callType);
        }
        
        /**
         * Has statistics for any call type.
         *
         * @return boolean
         */
        public boolean hasAnyStatistics(){
            return !havingStatistics.isEmpty();
        }
        
        /**
         * @return Returns the statistics.
         */
        public Statistics getStatistics(StatisticsCallType callType) {
            return allStatistics.get(callType);
        }
        
        /**
         * Update statistics.
         *
         * @param header IStatisticsHeader
         * @param value Object
         */
        public void updateStatistics(StatisticsCallType callType, IStatisticsHeader header, Object value){
            if(value==null){
                return;
            }
            Statistics statistics = allStatistics.get(callType);
            if(statistics==null){
                statistics = new Statistics();
                allStatistics.put(callType, statistics);
            }
            if(header.getType().equals(StatisticsType.COUNT)&&!(((Number)value).doubleValue() == 0.0)){
                havingStatistics.add(callType);
            }
            statistics.updateHeader(header, value);
        }
        
    }
    
    /**
     * <p>
     * Statistics information.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class StatInfo{
        private Node root;
        private HashMap<CallTimePeriods, Node> periodNodes = new HashMap<CallTimePeriods, Node>();
        private Set<Node> linkedProbes = new HashSet<Node>();
        
        public StatInfo(Node rootNode) {
            root = rootNode;
        }
        
        /**
         * @return Returns the root.
         */
        public Node getRoot() {
            return root;
        }
        
        /**
         * Returns the periodNode for call type.
         * @param period CallTimePeriods
         * @return Node
         */
        public Node getPeriodNode(CallTimePeriods period) {
            return periodNodes.get(period);
        }
        
        /**
         * Add period node
         * @param period CallTimePeriods
         * @param node Node
         */
        public void addPeriodNode(CallTimePeriods period, Node node) {
            periodNodes.put(period,node);
        }
        
        /**
         * @return Returns the linkedProbes.
         */
        public Set<Node> getLinkedProbes() {
            return linkedProbes;
        }
        
        /**
         * Add probe.
         *
         * @param probe Node
         */
        public void addProbe(Node probe){
            linkedProbes.add(probe);
        }
    }
    
}
