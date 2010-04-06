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

package org.amanzi.awe.views.calls.testing;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.amanzi.awe.views.calls.CallTimePeriods;
import org.amanzi.awe.views.calls.statistics.CallStatistics;
import org.amanzi.awe.views.calls.statistics.IStatisticsConstants;
import org.amanzi.awe.views.calls.statistics.CallStatistics.StatisticsHeaders;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.CallProperties.CallType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.GeneratedCallsData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.AMSLoader;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import static org.junit.Assert.*;

/**
 * Abstract class for testing CallStatistics.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class CallStatisticsTest {
    
    private static final String DATABASE_NAME = "neo_test";
    private static final String DATA_SAVER_DIR = "neo_call";
    private static final String USER_HOME = "user.home";
    private static final String AMANZI_STR = ".amanzi";
    private static final String MAIN_DIRECTORY = "call_stat_test";
    
    private static final String PATH_SEPERATOR = "\\";
    protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
    protected static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat(TIMESTAMP_FORMAT);
    
    private static final int CALL_DURATION_PERIODS_COUNT = 8;
    private static final float MAX_CALL_DURATION = 1000;
    
    private static final long MILLISECONDS = 1000;
    protected static final int DAY = 24;
    
    protected static final String CTSDC_COMMAND = "AT+CTSDC";
    protected static final String ATA_COMMAND = "ATA";
    protected static final String ATD_COMMAND = "atd";
    protected static final String CTCC_COMMAND = "+CTCC";
    
    private static final String PROBE_NAME_PREFIX = "PROBE";
    
    private static final int ETALON_CELL_COUNT = 13;
    
    private static String mainDirectoryName;
    private static GraphDatabaseService neo;
    
    private float[] callDurationBorders;
    
    /**
     * Initialize project service.
     */
    protected static void initProjectService(){
        NeoCorePlugin.getDefault().initProjectService(getNeo());
    }

    /**
     * Create new empty main directory instead old one.
     */
    protected static void prepareMainDirectory() {
        clearMainDirectory();
        initEmptyMainDirectory();
    }

    /**
     * Delete main directory.
     */
    protected static void clearMainDirectory() {
        File dir = new File(getUserHome());
        if(dir.exists() && dir.isDirectory()){
            dir = new File(dir,AMANZI_STR);
            if(dir.exists() && dir.isDirectory()){
                dir = new File(dir,MAIN_DIRECTORY);
                if(dir.exists()){
                    if(dir.isDirectory()){
                        clearDirectory(dir);
                    }
                    dir.delete();
                }
            }
        }
    }
    
    /**
     * Clear directory.
     * @param directory File (for clear)
     */
    protected static void clearDirectory(File directory){
        if(directory.exists()){
            for(File file : directory.listFiles()){
                if(file.isDirectory()){
                    clearDirectory(file);
                }
                file.delete();
            }
        }
    } 
    
    /**
     * Create new main directory.
     */
    protected static void initEmptyMainDirectory(){
        File dir = new File(getUserHome());
        if(!dir.exists()){
            dir.mkdir();
        }
        dir = new File(dir,AMANZI_STR);
        if(!dir.exists()){
            dir.mkdir();    
        }
        dir = new File(dir,MAIN_DIRECTORY);
        if(!dir.exists()){
            dir.mkdir();    
        }
        mainDirectoryName = dir.getPath();
    }

    /**
     * Get name of %USER_HOME% directory.
     *
     * @return String
     */
    private static String getUserHome() {
        return System.getProperty(USER_HOME);
    }
    
    /**
     * Gets neo service.
     * @return EmbeddedGraphDatabase
     */
    public static GraphDatabaseService getNeo(){
        if (neo == null){
            neo = new EmbeddedGraphDatabase(getDbDirectoryName());
        }
        return neo;
    }
    
    /**
     * Get name of data base directory.
     * (Create directory if it not exists)
     *
     * @return String
     */
    private static String getDbDirectoryName(){
        File dir = new File(mainDirectoryName,DATABASE_NAME);
        if(!dir.exists()){
            dir.mkdir();
        }
        return dir.getPath();
    }
    
    /**
     * Shutdown database service.
     */
    protected void shutdownNeo() {
        if(neo!=null){
            neo.shutdown();
            neo = null;
        }
    }
    
    /**
     * Execute test with different parameters.
     *
     * @param aHours Integer (hours count)
     * @param aDrift Integer (drift from 00:00)
     * @param aCallsPerHour Integer (count of calls per hour)
     * @param aCallPerHourVariance Integer (variance of count of calls per hour)
     * @param aProbes Integer (count of probes)
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    protected void executeTest(Integer aHours, Integer aDrift,
            Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes)throws IOException, ParseException{
        String dataDir = getDataDirectoryName();
        HashMap<Integer, CallStatData> generated = generateDataFiles(aHours,aDrift,aCallsPerHour,aCallPerHourVariance,aProbes, dataDir);
        Node datasetNode = loadData(dataDir);
        Transaction tx = getNeo().beginTx();
        try {
            CallStatistics statistics = new CallStatistics(datasetNode, getNeo());
            assertResult(generated, statistics, aHours);
        } finally {
            tx.finish();
        }
    }

    /**
     * Generate data for gets statistics.
     *
     * @param aHours Integer (hours count)
     * @param aDrift Integer (drift for first hour from 00:00)
     * @param aCallsPerHour Integer (count of calls per hour)
     * @param aCallPerHourVariance Integer (variance of count of calls per hour)
     * @param aProbes Integer (count of probes)
     * @param dataDir String (directory for save data)
     * @return HashMap<Integer, CallStatData>
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    private HashMap<Integer, CallStatData> generateDataFiles(Integer aHours, Integer aDrift, 
            Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes, String dataDir) throws IOException, ParseException {
        IDataGenerator generator = getDataGenerator(aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes, dataDir);
        List<CallGroup> generated = ((GeneratedCallsData)generator.generate()).getData();
        return buildStatisticsByGenerated(generated);
    }

    /**
     * Returns data generator.
     *
     * @param aHours Integer (hours count)
     * @param aDrift Integer (drift for first hour from 00:00)
     * @param aCallsPerHour Integer (count of calls per hour)
     * @param aCallPerHourVariance Integer (variance of count of calls per hour)
     * @param aProbes Integer (count of probes)
     * @param dataDir String (directory for save data)
     * @return AmsDataGenerator
     */
    protected abstract IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes, String dataDir);

    /**
     * Load generated data.
     *
     * @param dataDir String (directory for get data)
     * @return Node (virtual dataset Node)
     * @throws IOException (problem in data generation)
     */
    private Node loadData(String dataDir) throws IOException {
        AMSLoader loader = new AMSLoader(dataDir, "test", "test network", getNeo());
        loader.setLimit(5000);
        loader.run(new NullProgressMonitor());
        Node datasetNode = loader.getDatasetNode();
        return NeoUtils.findOrCreateVirtualDatasetNode(datasetNode, DriveTypes.AMS_CALLS, getNeo());
    }
    
    /**
     * Assert statistics result.
     *
     * @param generated HashMap<Integer, CallStatData> (data that was generated)
     * @param statistics CallStatistics (for check)
     * @param hours Integer (count of hours)
     */
    private void assertResult(HashMap<Integer, CallStatData> generated,CallStatistics statistics, Integer hours){
        Node hourlyNode = statistics.getPeriodNode(CallTimePeriods.HOURLY, getCallType());
        assertPeriodStatistics(hourlyNode, CallTimePeriods.HOURLY, generated);
        if(hours>1){
            Node dailyNode = statistics.getPeriodNode(CallTimePeriods.DAILY, getCallType());
            assertPeriodStatistics(dailyNode, CallTimePeriods.DAILY, generated);
        }
        if(hours>DAY){
            Node weekNode = statistics.getPeriodNode(CallTimePeriods.WEEKLY, getCallType());
            assertPeriodStatistics(weekNode, CallTimePeriods.WEEKLY, generated);
            Node monthNode = statistics.getPeriodNode(CallTimePeriods.MONTHLY, getCallType());
            assertPeriodStatistics(monthNode, CallTimePeriods.MONTHLY, generated);
        }
    }
    
    /**
     * @return Returns type of calls.
     */
    protected abstract CallType getCallType();
    
    /**
     * Assert statistics in period.
     *
     * @param statNode Node (root node for statistics)
     * @param period CallTimePeriods (statistics period)
     * @param generated HashMap<Integer, CallStatData> (data that was generated)
     */
    private void assertPeriodStatistics(Node statNode,final CallTimePeriods period, HashMap<Integer, CallStatData> generated){
        assertFalse(period.getId()+" node does not exists.",statNode==null);
        assertUnderlyingStatCount(statNode, period);
        Traverser traverse = NeoUtils.getChildTraverser(statNode);
        for(Node row : traverse.getAllNodes()){
            assertRow(row, generated, period);
        }
    }

    /**
     * Assert underlying statistics nodes count.
     *
     * @param statNode Node (root node for statistics)
     * @param period CallTimePeriods (statistics period)
     */
    private void assertUnderlyingStatCount(Node statNode, final CallTimePeriods period) {
        final CallTimePeriods underlyingPeriod = period.getUnderlyingPeriod();
        if(underlyingPeriod==null){
            return;
        }
        Traverser underlyingTraverser = statNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator(){
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return NeoUtils.getNodeName(node,getNeo()).equalsIgnoreCase(underlyingPeriod.getId());
            }
        }, GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING);
        List<Node> allUnderlying = new ArrayList<Node>(underlyingTraverser.getAllNodes());
        int underlyingCount = allUnderlying.size();
        assertEquals("Incorrect count of "+underlyingPeriod.getId()+" nodes linked to "+period.getId()+".",1,underlyingCount);
    }
   
    /**
     * Assert statistics row.
     *
     * @param row Node
     * @param generated HashMap<Integer, CallStatData> (data that was generated)
     * @param isHourly boolean (is it hourly statistics?)
     * @param period CallTimePeriods (statistics period)
     */
    private void assertRow(Node row,HashMap<Integer, CallStatData> generated, CallTimePeriods period){
        Traverser probeTraverse = row.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator(){
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return NodeTypes.getNodeType(node, neo).equals(NodeTypes.PROBE);
            }
        }, GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING);
        List<Node> allProbes = new ArrayList<Node>(probeTraverse.getAllNodes());
        int probesCount = allProbes.size();
        assertEquals("Incorrect count of probes linked to s_row.",1,probesCount);
        Node probe = allProbes.get(0);
        Integer prNum = getProbeNumber(probe);
        CallStatData callStatData = generated.get(prNum);
        Date rowTime = getRowTime(row);
        HashMap<StatisticsHeaders, Long> prData = callStatData.getStatisticsByPeriod(rowTime, period);
        HashMap<StatisticsHeaders, Long> sourceData = null;        
        if (period.getUnderlyingPeriod()!=null) {
            sourceData = buildStatDataByNodes(row);
        }
        assertCells(row, prData, sourceData);
        if(period.getUnderlyingPeriod()!=null){
            assertCellsBySource(row);
        }
    }
    
    /**
     * Assert values in cells.
     *
     * @param row Node
     * @param etalon HashMap<StatisticsHeaders, Long> (values by generated data)
     * @param source HashMap<StatisticsHeaders, Long> (values by source data, null if statistics is hourly)
     */
    private void assertCells(Node row, HashMap<StatisticsHeaders, Long> etalon, HashMap<StatisticsHeaders, Long> source){
        HashMap<StatisticsHeaders, Long> cells = buildCellDataMap(row);
        int cellCount = cells.size();
        assertEquals("Wrong cell count.",ETALON_CELL_COUNT, cellCount);
        assertCellValue(etalon, source, cells, StatisticsHeaders.CALL_ATTEMPT_COUNT);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SUCC_SETUP_COUNT);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TM_Z1_P1);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TM_Z1_P2);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TM_Z1_P3);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TM_Z1_P4);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TM_Z1_L1);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TM_Z1_L2);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TM_Z1_L3);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TM_Z1_L4);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TIME_MIN);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TIME_MAX);
        assertCellValue(etalon, source, cells, StatisticsHeaders.SETUP_TOTAL_DUR);
    }
    
    /**
     * Check cell value by source cells.
     * (Not used in hourly assert)
     *
     * @param aCell Node
     */
    private void assertCellsBySource(Node aCell){
        Traverser cellTraverse = NeoUtils.getChildTraverser(aCell);
        for(Node cell : cellTraverse.getAllNodes()){
            Traverser traverse = cell.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return currentPos.depth()>0&&NodeTypes.getNodeType(node, neo).equals(NodeTypes.S_CELL);
                }            
            }, GeoNeoRelationshipTypes.SOURCE,Direction.OUTGOING);
            StatisticsHeaders header = StatisticsHeaders.getHeaderByTitle((String)cell.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            Long etalon = getCellsValue(traverse.getAllNodes(),header);
            Long value = getCellValue(cell, header);
            assertEquals("Value in cell "+header+" is not conform to source.", etalon, value);
        }
    }
    
    /**
     * Gets values from source cells.
     *
     * @param cells Collection of Nodes.
     * @param header cell header.
     * @return Long.
     */
    private Long getCellsValue(Collection<Node> cells, StatisticsHeaders header){
        Long result = null;
        for(Node cell : cells){
            Long value = getCellValue(cell, header);
            if(result == null){
                result = value;
                continue;
            }
            if(header.equals(StatisticsHeaders.SETUP_TIME_MIN)){
                if(value<result){
                    result = value;
                }
                continue;
            }
            if(header.equals(StatisticsHeaders.SETUP_TIME_MAX)){
                if(value>result){
                    result = value;
                }
                continue;
            }
            result+=value;
        }
        return result==null?0L:result;
    }

    /**
     * Check cell value
     *
     * @param etalon HashMap<StatisticsHeaders, Long> (values by generated data)
     * @param source HashMap<StatisticsHeaders, Long> (values by source data, null if statistics is hourly)
     * @param cells HashMap<StatisticsHeaders, Long> (real values)
     * @param cellType StatisticsHeaders (cell header)
     */
    private void assertCellValue(HashMap<StatisticsHeaders, Long> etalon, HashMap<StatisticsHeaders, Long> source,
            HashMap<StatisticsHeaders, Long> cells, StatisticsHeaders cellType) {
        Long assertionValue = cells.get(cellType);
        assertFalse("Cell "+cellType+" not found.",assertionValue == null);
        assertEquals("Wrong value in cell "+cellType+".", etalon.get(cellType), assertionValue);
        if(source!=null){
            assertEquals("Wrong value in cell "+cellType+" by sources.", source.get(cellType), assertionValue);
        }
    }
    
    /**
     * Build statistics by source nodes.
     *
     * @param row Node (source row)
     * @return HashMap<StatisticsHeaders, Long>
     */
    private HashMap<StatisticsHeaders, Long> buildStatDataByNodes(Node row){
        HashMap<StatisticsHeaders, Long> result = new HashMap<StatisticsHeaders, Long>(ETALON_CELL_COUNT);
        Traverser traverse = row.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return currentPos.depth()>0&&NodeTypes.getNodeType(node, neo).equals(NodeTypes.S_ROW);
            }            
        }, GeoNeoRelationshipTypes.SOURCE,Direction.OUTGOING);
        Collection<Node> allNodes = traverse.getAllNodes();
        assertFalse("Daily row has no sourses!",allNodes.isEmpty());
        for(Node sRow : allNodes){
           HashMap<StatisticsHeaders, Long> cellMap = buildCellDataMap(sRow);
           for(StatisticsHeaders header : StatisticsHeaders.values()){
               Long currValue = cellMap.get(header);
               if(currValue == null){
                   continue;
               }
               Long resValue = result.get(header);
               if(resValue == null){
                   result.put(header, currValue);
                   continue;
               }
               if(header.equals(StatisticsHeaders.SETUP_TIME_MIN)){
                   if(resValue==0||(currValue>0&&currValue<resValue)){                       
                       result.put(header, currValue);                       
                   }
                   continue;
               }
               if(header.equals(StatisticsHeaders.SETUP_TIME_MAX)){
                   if(currValue>resValue){
                       result.put(header, currValue);                       
                   }
                   continue;
               }
               result.put(header, resValue+currValue);
           }
        }
        return result;
    }
    
    /**
     * Gets row values.
     *
     * @param row Node
     * @return HashMap<StatisticsHeaders, Long>
     */
    private HashMap<StatisticsHeaders, Long> buildCellDataMap(Node row){
        HashMap<StatisticsHeaders, Long> result = new HashMap<StatisticsHeaders, Long>();
        Traverser traverse = NeoUtils.getChildTraverser(row);
        for(Node cell : traverse.getAllNodes()){
            StatisticsHeaders header = StatisticsHeaders.getHeaderByTitle((String)cell.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            Long value = getCellValue(cell, header);
            result.put(header, value);
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
    private Long getCellValue(Node cell, StatisticsHeaders header){
        if(header.equals(StatisticsHeaders.SETUP_TIME_MAX)
                ||header.equals(StatisticsHeaders.SETUP_TIME_MIN)
                ||header.equals(StatisticsHeaders.SETUP_TOTAL_DUR)){
            Float value = MILLISECONDS*(Float)cell.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
            return Math.round(value.doubleValue());
        }
        Integer value = (Integer)cell.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
        return value.longValue();
    }
    
    /**
     * Returns name of directory for save generated data.
     *
     * @return String
     */
    private String getDataDirectoryName(){
        return mainDirectoryName+PATH_SEPERATOR+DATA_SAVER_DIR;
    }
    
    /**
     * Returns probe number.
     *
     * @param probe Node
     * @return Integer
     */
    private Integer getProbeNumber(Node probe){
        String prName = (String)probe.getProperty(INeoConstants.PROPERTY_NAME_NAME);
        String numString  = prName.substring(PROBE_NAME_PREFIX.length());
        return Integer.parseInt(numString);
    }
    
    /**
     * Returns time of statistics row.
     *
     * @param row Node
     * @return Date
     */
    private Date getRowTime(Node row){
        Long timestamp = (Long)row.getProperty(INeoConstants.PROPERTY_TIME_NAME);
        return new Date(timestamp);
    }
  
    /**
     * Build statistics by generated data.
     *
     * @param generated List of CallPairs
     * @return HashMap<Integer, CallStatData>
     * @throws ParseException (problem in gets parameters)
     */
    private HashMap<Integer, CallStatData> buildStatisticsByGenerated(List<CallGroup> generated)throws ParseException{
        HashMap<Integer, CallStatData> result = new HashMap<Integer, CallStatData>();
        for(CallGroup pair : generated){
            Integer source = pair.getSourceProbe();
            CallStatData data = result.get(source);
            if(data == null){
                data = new CallStatData();
                result.put(source, data);
            }
            for(CallData call : pair.getData()){
                Date start = getCallStartTime(call);
                Long duration = getCallDuration(call, start);
                Integer periode = getDurationPeriod(duration);
                data.addCall(periode, start, duration);
            }
        }
        return result;
    }
    
    /**
     * Gets time of call starts.
     *
     * @param call CallData
     * @return Date
     */
    protected abstract Date getCallStartTime(CallData call);
    
    /**
     * Gets call duration.
     *
     * @param data ProbeData
     * @param start Date (call start)
     * @return
     * @throws ParseException (problem in gets parameters)
     */
    protected abstract Long getCallDuration(CallData call, Date start)throws ParseException;
    
    /**
     * Gets number of call duration period.
     *
     * @param duration Long
     * @return Integer
     */
    private Integer getDurationPeriod(Long duration){
        float[] durationBorders = callDurationBorders;
        for(int i=0; i<CALL_DURATION_PERIODS_COUNT; i++){
            float start = MILLISECONDS * durationBorders[i];
            float end = MILLISECONDS * durationBorders[i+1];
            if(start<=duration && duration<end){
                return i;
            }
        }
        return CALL_DURATION_PERIODS_COUNT;
    }
    
    /**
     * Initialize Call duration borders.
     */
    protected void initCallDurationBorders(){
        callDurationBorders = new float[CALL_DURATION_PERIODS_COUNT+2];
        IStatisticsConstants constants = getStatisticsConstants();
        callDurationBorders[0] = constants.getCallConnTimeP1();        
        callDurationBorders[1] = constants.getCallConnTimeP2();
        callDurationBorders[2] = constants.getCallConnTimeP3();
        callDurationBorders[3] = constants.getCallConnTimeP4();
        callDurationBorders[4] = constants.getCallConnTimeL1();
        callDurationBorders[5] = constants.getCallConnTimeL2();
        callDurationBorders[6] = constants.getCallConnTimeL3();
        callDurationBorders[7] = constants.getCallConnTimeL4();
        callDurationBorders[8] = constants.getCallConnTimeLimit();
        callDurationBorders[9] = MAX_CALL_DURATION;
    }
    
    /**
     * @return Statistics constants for concrete type.
     */
    protected abstract IStatisticsConstants getStatisticsConstants();
    
    /**
     * For saving call information builded by generated data.
     * <p>
     *
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    protected class CallStatData{
        
        /**
         * All calls:
         * Key: period number, Value: Map - key: Start time, value: duration.
         */
        private HashMap<Integer, HashMap<Date, Long>> data = new HashMap<Integer, HashMap<Date,Long>>();
        
        /**
         * Get all calls by duration period.
         *
         * @param periode Integer (call duration period number)
         * @param calls HashMap<Integer, HashMap<Date, Long>>
         * @return HashMap<Date, Long>
         */
        private HashMap<Date, Long> getAllCallsFrom(Integer periode,HashMap<Integer, HashMap<Date, Long>> calls){
            return calls.get(periode);
        }
        
        /**
         * Returns calls count.
         *
         * @param calls HashMap<Integer, HashMap<Date, Long>>
         * @return Integer.
         */
        private Integer getCallCount(HashMap<Integer, HashMap<Date, Long>> calls){
            int result = 0;
            for(int i=0;i<=CALL_DURATION_PERIODS_COUNT;i++){
                result+=getCallCount(i,calls);
            }
            return result;
        }
        
        /**
         * Returns setup calls count.
         *
         * @param calls HashMap<Integer, HashMap<Date, Long>>
         * @return Integer.
         */
        private Integer getSetupCount(HashMap<Integer, HashMap<Date, Long>> calls){
            int result = 0;
            for(int i=0;i<CALL_DURATION_PERIODS_COUNT;i++){
                result+=getCallCount(i,calls);
            }
            return result;
        }
        
        /**
         * Returns calls count in period.
         *
         * @param periode Integer
         * @param calls HashMap<Integer, HashMap<Date, Long>>
         * @return Integer
         */
        private Integer getCallCount(Integer periode,HashMap<Integer, HashMap<Date, Long>> calls){
            HashMap<Date, Long> allCallsFrom = getAllCallsFrom(periode,calls);
            return allCallsFrom==null?0:allCallsFrom.size();
        }
        
        /**
         * Add new call to data.
         *
         * @param periode Integer call duration period number
         * @param start Date call start time
         * @param duration Long call duration
         */
        public void addCall(Integer periode, Date start, Long duration){
            HashMap<Date, Long> calls = getAllCallsFrom(periode,data);
            if(calls == null){
                calls = new HashMap<Date, Long>();
                data.put(periode, calls);
            }
            calls.put(start, duration);
        }
        
        /**
         * Build call statistics by period.
         *
         * @param start Date start time 
         * @param period CallTimePeriods (statistics period)
         * @return HashMap<StatisticsHeaders, Long>
         */
        public HashMap<StatisticsHeaders, Long> getStatisticsByPeriod(Date start, CallTimePeriods period){
            return buildStatistics(start, new Date(period.addPeriod(start.getTime())));
        }
        
        /**
         * Build call statistics by time period.
         *
         * @param start Date
         * @param end Date
         * @return HashMap<StatisticsHeaders, Long>
         */
        private HashMap<StatisticsHeaders, Long> buildStatistics(Date start, Date end){
            HashMap<StatisticsHeaders, Long> result = new HashMap<StatisticsHeaders, Long>();
            HashMap<Integer, HashMap<Date, Long>> calls = getCallsInPeriod(start, end);
            result.put(StatisticsHeaders.CALL_ATTEMPT_COUNT, getCallCount(calls).longValue());
            result.put(StatisticsHeaders.SUCC_SETUP_COUNT, getSetupCount(calls).longValue());
            result.put(StatisticsHeaders.SETUP_TM_Z1_P1, getCallCount(0,calls).longValue());
            result.put(StatisticsHeaders.SETUP_TM_Z1_P2, getCallCount(1,calls).longValue());
            result.put(StatisticsHeaders.SETUP_TM_Z1_P3, getCallCount(2,calls).longValue());
            result.put(StatisticsHeaders.SETUP_TM_Z1_P4, getCallCount(3,calls).longValue());
            result.put(StatisticsHeaders.SETUP_TM_Z1_L1, getCallCount(4,calls).longValue());
            result.put(StatisticsHeaders.SETUP_TM_Z1_L2, getCallCount(5,calls).longValue());
            result.put(StatisticsHeaders.SETUP_TM_Z1_L3, getCallCount(6,calls).longValue());
            result.put(StatisticsHeaders.SETUP_TM_Z1_L4, getCallCount(7,calls).longValue());
            result.put(StatisticsHeaders.SETUP_TIME_MIN, getMinTime(calls));
            result.put(StatisticsHeaders.SETUP_TIME_MAX, getMaxTime(calls));
            result.put(StatisticsHeaders.SETUP_TOTAL_DUR, getTotalTime(calls));
            return result;
        }
        
        /**
         * Gets minimal duration time in calls.
         *
         * @param calls HashMap<Integer, HashMap<Date, Long>>
         * @return Long
         */
        private Long getMinTime(HashMap<Integer, HashMap<Date, Long>> calls){
            for(int i=0; i<CALL_DURATION_PERIODS_COUNT; i++){
                HashMap<Date, Long> callsInPeriode = getAllCallsFrom(i, calls);
                if (callsInPeriode!=null && !callsInPeriode.isEmpty()) {
                    List<Long> times = new ArrayList<Long>(callsInPeriode.values());
                    Collections.sort(times);
                    return times.get(0);
                }
            }
            return 0L;
        }
        
        /**
         * Gets maximal duration time in calls.
         *
         * @param calls HashMap<Integer, HashMap<Date, Long>>
         * @return Long
         */
        private Long getMaxTime(HashMap<Integer, HashMap<Date, Long>> calls){
            for(int i=CALL_DURATION_PERIODS_COUNT-1; i>=0; i--){
                HashMap<Date, Long> callsInPeriode = getAllCallsFrom(i, calls);
                if (callsInPeriode!=null && !callsInPeriode.isEmpty()) {
                    List<Long> times = new ArrayList<Long>(callsInPeriode.values());
                    Collections.sort(times);
                    return times.get(times.size()-1);
                }
            }
            return 0L;
        }
        
        /**
         * Gets total duration time in calls.
         *
         * @param calls HashMap<Integer, HashMap<Date, Long>>
         * @return Long
         */
        private Long getTotalTime(HashMap<Integer, HashMap<Date, Long>> calls){
            Float result = 0.0f;
            for(int i=0;i<CALL_DURATION_PERIODS_COUNT;i++){
                HashMap<Date, Long> callsInPeriode = getAllCallsFrom(i, calls);
                if (callsInPeriode!=null) {
                    for (Long time : callsInPeriode.values()) {
                        result += (float)time/MILLISECONDS; //to solve rounding problem
                    }
                }
            }
            result = result*MILLISECONDS;
            return Math.round(result.doubleValue());
        }
        
        /**
         * Gets all calls in time period.
         *
         * @param start Date
         * @param end Date
         * @return HashMap<Integer, HashMap<Date, Long>>
         */
        private HashMap<Integer, HashMap<Date, Long>> getCallsInPeriod(Date start, Date end){
            HashMap<Integer, HashMap<Date,Long>> result = new HashMap<Integer, HashMap<Date,Long>>();
            for(int i=0;i<=CALL_DURATION_PERIODS_COUNT;i++){
                HashMap<Date, Long> callsInPeriode = getAllCallsFrom(i, data);
                if (callsInPeriode!=null) {
                    for (Date date : callsInPeriode.keySet()) {
                        if ((start.before(date)||start.equals(date)) && date.before(end)) {
                            HashMap<Date, Long> found = result.get(i);
                            if (found == null) {
                                found = new HashMap<Date, Long>();
                                result.put(i, found);
                            }
                            found.put(date, callsInPeriode.get(date));
                        }
                    }
                }
            }
            return result;
        }
    }
}
