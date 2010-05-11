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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.enums.StatisticsType;
import org.amanzi.awe.views.calls.statistics.CallStatistics;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
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

/**
 * <p>
 * Common class for all ams statistics tests.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class AmsStatisticsTest {
    
    protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
    protected static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat(TIMESTAMP_FORMAT);
    private static final String PATH_SEPERATOR = "\\";
    
    private static final String DATABASE_NAME = "neo_test";
    private static final String DATA_SAVER_DIR = "neo_call";
    private static final String USER_HOME = "user.home";
    private static final String AMANZI_STR = ".amanzi";
    private static final String MAIN_DIRECTORY = "call_stat_test";
    
    protected static final int DAY = 24;
    protected static final long MILLISECONDS = 1000;
    
    private static final String PROBE_NAME_PREFIX = "PROBE";
    
    protected static final String CTSDC_COMMAND = "AT+CTSDC";
    protected static final String ATA_COMMAND = "ATA";
    protected static final String ATD_COMMAND = "atd";
    protected static final String CTCC_COMMAND = "+CTCC";
    protected static final String ATH_COMMAND = "ATH";
    protected static final String CTCR_COMMAND = "+CTCR";
    protected static final String PESQ_COMMAND = "PESQ.run";
    
    private static String mainDirectoryName;
    private static GraphDatabaseService neo;
    
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
     * Returns name of directory for save generated data.
     *
     * @return String
     */
    protected String getDataDirectoryName(){
        return mainDirectoryName+PATH_SEPERATOR+DATA_SAVER_DIR;
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
        HashMap<Integer, ProbeStat> generated = generateDataFiles(aHours,aDrift,aCallsPerHour,aCallPerHourVariance,aProbes, dataDir);
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
    private HashMap<Integer, ProbeStat> generateDataFiles(Integer aHours, Integer aDrift, 
            Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes, String dataDir) throws IOException, ParseException {
        IDataGenerator generator = getDataGenerator(aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes, dataDir);
        List<CallGroup> generated = ((GeneratedCallsData)generator.generate()).getData();
        return buildStatisticsByGenerated(generated, aHours);
    }
    
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
    private void assertResult(HashMap<Integer, ProbeStat> generated,CallStatistics statistics, Integer hours){
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
     * Assert statistics in period.
     *
     * @param statNode Node (root node for statistics)
     * @param period CallTimePeriods (statistics period)
     * @param generated HashMap<Integer, CallStatData> (data that was generated)
     */
    private void assertPeriodStatistics(Node statNode,final CallTimePeriods period, HashMap<Integer, ProbeStat> generated){
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
    private void assertRow(Node row,HashMap<Integer, ProbeStat> generated, CallTimePeriods period){
        Traverser probeTraverse = row.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator(){
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return NodeTypes.getNodeType(node, getNeo()).equals(NodeTypes.PROBE);
            }
        }, GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING);
        List<Node> allProbes = new ArrayList<Node>(probeTraverse.getAllNodes());
        int probesCount = allProbes.size();
        assertEquals("Incorrect count of probes linked to s_row.",1,probesCount);
        Node probe = allProbes.get(0);
        Integer prNum = getProbeNumber(probe);
        ProbeStat statData = generated.get(prNum);
        Date rowTime = getRowTime(row);
        HashMap<IStatisticsHeader, Number> prData = statData.getStatisticsByPeriod(period).getRowValuesForCheck(rowTime);
        HashMap<IStatisticsHeader, Number> sourceData = null;        
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
    private void assertCells(Node row, HashMap<IStatisticsHeader, Number> etalon, HashMap<IStatisticsHeader, Number> source){
        HashMap<IStatisticsHeader, Number> cells = buildCellDataMap(row);
        int cellCount = cells.size();
        assertEquals("Wrong cell count.",getCallType().getHeaders().size(), cellCount);
        for(IStatisticsHeader header : getCallType().getHeaders()){
            assertCellValue(etalon, source, cells, header);
        }
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
                    return currentPos.depth()>0&&NodeTypes.getNodeType(node, getNeo()).equals(NodeTypes.S_CELL);
                }            
            }, GeoNeoRelationshipTypes.SOURCE,Direction.OUTGOING);
            IStatisticsHeader header = getCallType().getHeaderByTitle((String)cell.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            Number etalon = getCellsValue(traverse.getAllNodes(),header);
            Number value = getCellValue(cell, header);
            assertEquals("Value in cell "+header+" is not conform to source.", etalon, value);
        }
    }
    
    /**
     * Check cell value
     *
     * @param etalon HashMap<StatisticsHeaders, Long> (values by generated data)
     * @param source HashMap<StatisticsHeaders, Long> (values by source data, null if statistics is hourly)
     * @param cells HashMap<StatisticsHeaders, Long> (real values)
     * @param cellType StatisticsHeaders (cell header)
     */
    private void assertCellValue(HashMap<IStatisticsHeader, Number> etalon, HashMap<IStatisticsHeader, Number> source,
            HashMap<IStatisticsHeader, Number> cells, IStatisticsHeader cellType) {
        Number assertionValue = cells.get(cellType);
        assertFalse("Cell "+cellType+" not found.",assertionValue == null);
        assertEquals("Wrong value in cell "+cellType+".", etalon.get(cellType), assertionValue);
        if(source!=null){
            assertEquals("Wrong value in cell "+cellType+" by sources.", source.get(cellType), assertionValue);
        }
    }
    
    /**
     * Gets values from source cells.
     *
     * @param cells Collection of Nodes.
     * @param header cell header.
     * @return Long.
     */
    private Number getCellsValue(Collection<Node> cells, IStatisticsHeader header){
        Number result = null;
        for(Node cell : cells){
            Number value = getCellValue(cell, header);
            if(result == null){
                result = value;
                continue;
            }
            switch (header.getType()) {
            case MAX:
                if(value.doubleValue()>value.doubleValue()){
                    result = value;
                }
                break;
            case MIN:
                if(value.doubleValue()<result.doubleValue()){
                    result = value;
                }
                break;
            case SUM:
                result = result.floatValue()+value.floatValue();
                break;
            case COUNT:
                result = result.longValue()+value.longValue();
                break;
            default:
                throw new IllegalArgumentException("Unknown header type: "+header.getType()+".");
            }
        }
        return result==null?0L:result;
    }
    
    /**
     * Build statistics by source nodes.
     *
     * @param row Node (source row)
     * @return HashMap<StatisticsHeaders, Long>
     */
    private HashMap<IStatisticsHeader, Number> buildStatDataByNodes(Node row){
        HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>(getCallType().getHeaders().size());
        Traverser traverse = row.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return currentPos.depth()>0&&NodeTypes.getNodeType(node, getNeo()).equals(NodeTypes.S_ROW);
            }            
        }, GeoNeoRelationshipTypes.SOURCE,Direction.OUTGOING);
        Collection<Node> allNodes = traverse.getAllNodes();
        assertFalse("Daily row has no sourses!",allNodes.isEmpty());
        for(Node sRow : allNodes){
           HashMap<IStatisticsHeader, Number> cellMap = buildCellDataMap(sRow);
           for(IStatisticsHeader header : getCallType().getHeaders()){
               Number currValue = cellMap.get(header);
               if(currValue == null){
                   continue;
               }
               Number resValue = result.get(header);
               if(resValue == null){
                   result.put(header, currValue);
                   continue;
               }
               switch (header.getType()) {
               case MAX:
                   if(currValue.doubleValue()>resValue.doubleValue()){
                       result.put(header, currValue);
                   }
                   break;
               case MIN:
                   if(resValue.equals(0)||(currValue.doubleValue()<resValue.doubleValue())){
                       result.put(header, currValue);
                   }
                   break;
               case SUM:
                   resValue = resValue.floatValue()+currValue.floatValue();
                   result.put(header, resValue);
                   break;
               case COUNT:
                   resValue = resValue.longValue()+currValue.longValue();
                   result.put(header, resValue);
                   break;
               default:
                   throw new IllegalArgumentException("Unknown header type: "+header.getType()+".");
               }
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
    private HashMap<IStatisticsHeader, Number> buildCellDataMap(Node row){
        HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>();
        Traverser traverse = NeoUtils.getChildTraverser(row);
        for(Node cell : traverse.getAllNodes()){
            IStatisticsHeader header = getCallType().getHeaderByTitle((String)cell.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            Number value = getCellValue(cell, header);
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
    private Number getCellValue(Node cell, IStatisticsHeader header){
        if(header.getType().equals(StatisticsType.MAX)
                ||header.getType().equals(StatisticsType.MIN)
                ||header.getType().equals(StatisticsType.SUM)){
            return (Float)cell.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
        }
        Integer value = (Integer)cell.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
        return value.longValue();
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
     * @param generated List of CallGroups
     * @param hours int (count of hours)
     * @return HashMapHashMap<Integer, ProbeStat>
     * @throws ParseException (problem in gets parameters)
     */
    private HashMap<Integer, ProbeStat> buildStatisticsByGenerated(List<CallGroup> generated, int hours)throws ParseException{
        if(hours> DAY){
            return buildStatistcsByPeriod(generated, CallTimePeriods.MONTHLY);
        }
        if(hours>1){
            return buildStatistcsByPeriod(generated, CallTimePeriods.DAILY);
        }
        return buildStatistcsByPeriod(generated, CallTimePeriods.HOURLY);
    }
    
    /**
     * Build statistics by period.
     *
     * @param generated List of CallGroups
     * @param period CallTimePeriods
     * @return HashMap<Integer, ProbeStat>
     */
    private HashMap<Integer, ProbeStat> buildStatistcsByPeriod(List<CallGroup> generated, CallTimePeriods period) throws ParseException{
        if(period.equals(CallTimePeriods.HOURLY)){
            return buildHourlyStatistics(generated);
        }
        CallTimePeriods undPeriod = period.getUnderlyingPeriod();
        HashMap<Integer, ProbeStat> statistics = buildStatistcsByPeriod(generated, undPeriod);        
        for(Integer probe : statistics.keySet()){
            ProbeStat curr = statistics.get(probe);
            curr.addStatistcs(collectStatisticsByUnderling(curr.getStatisticsByPeriod(undPeriod), period));
        }
        return statistics;
    }
    
    /**
     * Get Statistics from underling
     *
     * @param undStatistics PeriodStat (underling statistics)
     * @param period CallTimePeriods (current period)
     * @return PeriodStat
     */
    private PeriodStat collectStatisticsByUnderling(PeriodStat undStatistics, CallTimePeriods period){
        List<Date> times = undStatistics.getAllTimesSorted();
        PeriodStat result = new PeriodStat(period);
        Date start = times.get(0);
        Date lastDate = times.get(times.size()-1);
        start = new Date(period.getFirstTime(start.getTime()));
        Date end = getNextStartDate(period, lastDate, start);
        do{
            List<HashMap<IStatisticsHeader, Number>> dataInBorders = undStatistics.getDataInBorders(start, end);
            if (!dataInBorders.isEmpty()) {
                HashMap<IStatisticsHeader, Number> newRow = new HashMap<IStatisticsHeader, Number>();
                for (HashMap<IStatisticsHeader, Number> row : dataInBorders) {
                    updateStatRow(newRow, row);
                }
                result.addRow(start, newRow);
            }
            start = end;
            end = getNextStartDate(period, lastDate, start);
        }while(end.before(lastDate));
        return result;
    }
    
    private Date getNextStartDate(CallTimePeriods period, Date endDate, Date currentStartDate) {
        Date nextStartDate = new Date(period.addPeriod(currentStartDate.getTime()));
        if(!period.equals(CallTimePeriods.HOURLY)&&(nextStartDate.after(endDate))){
            nextStartDate = endDate;
        }
        return nextStartDate;
    }
    
    /**
     * Build Hourly statistics from generated data
     *
     * @param generated List of CallGroups
     * @return HashMap<Integer, ProbeStat>
     */
    private HashMap<Integer, ProbeStat> buildHourlyStatistics(List<CallGroup> generated) throws ParseException{
        HashMap<Integer, ProbeStat> result = new HashMap<Integer, ProbeStat>();
        for(CallGroup group : generated){
            Integer probe = group.getSourceProbe();
            ProbeStat stat = result.get(probe);
            if(stat == null){
                stat = new ProbeStat(probe);
                result.put(probe, stat);
            }
            PeriodStat periodStat = stat.getStatisticsByPeriod(CallTimePeriods.HOURLY);
            if(periodStat == null){
                periodStat = new PeriodStat(CallTimePeriods.HOURLY);
                stat.addStatistcs(periodStat);
            }
            for(CallData call : group.getData()){
                Date start = new Date(CallTimePeriods.HOURLY.getFirstTime(getCallStartTime(call).getTime()));
                HashMap<IStatisticsHeader, Number> newValues = getStatValuesFromCall(call);
                HashMap<IStatisticsHeader, Number> rowValues = periodStat.getRowValues(start);
                if(rowValues==null){
                    periodStat.addRow(start, newValues);
                    continue;
                }
                updateStatRow(rowValues, newValues);
            }
        }
        return result;
    }
    
    /**
     * Update statistics row
     *
     * @param updated HashMap<StatisticsHeaders, Long>
     * @param source HashMap<StatisticsHeaders, Long>
     */
    private void updateStatRow(HashMap<IStatisticsHeader, Number> updated, HashMap<IStatisticsHeader, Number> source){
        for(IStatisticsHeader header : getCallType().getHeaders()){
            Number updValue = updated.get(header);
            Number sourceValue = source.get(header);
            if(updValue == null){
                updated.put(header, sourceValue);
                continue;
            }
            if(sourceValue == null){
                continue;
            }
            switch (header.getType()) {
            case MAX:
                if(sourceValue.doubleValue()>updValue.doubleValue()){
                    updValue = sourceValue;
                }
                break;
            case MIN:
                if(sourceValue.doubleValue()<updValue.doubleValue()){
                    updValue = sourceValue;
                }
                break;
            case SUM:
                updValue = updValue.floatValue()+sourceValue.floatValue();
                break;
            case COUNT:
                updValue = updValue.longValue()+sourceValue.longValue();
                break;
            default:
                throw new IllegalArgumentException("Unknown header type: "+header.getType()+".");
            }
            updated.put(header, updValue);
        }
    }
    
    protected Float getSumFromList(List<Float> list){
        if(list==null||list.isEmpty()){
            return null;
        }
        Float result = 0f;
        for(Float curr : list){
            result+=curr;
        }
        return result;
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
     * @return Returns type of calls.
     */
    protected abstract StatisticsCallType getCallType();
    
    /**
     * Get statistics values from call data.
     *
     * @param call CallData
     * @return HashMap<StatisticsHeaders, Long>
     */
    protected abstract HashMap<IStatisticsHeader, Number> getStatValuesFromCall(CallData call) throws ParseException;
    
    /**
     * Gets time of call starts.
     *
     * @param call CallData
     * @return Date
     */
    protected abstract Date getCallStartTime(CallData call);
    
    /**
     * <p>
     *  To keep all statistics for one probe.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    public static class ProbeStat{
        
        private Integer probeKey;
        private HashMap<CallTimePeriods, PeriodStat> data;
        
        public ProbeStat(Integer probe) {
            probeKey = probe;
            data = new HashMap<CallTimePeriods, PeriodStat>();
        }
        
        /**
         * @return Returns the probeKey.
         */
        public Integer getProbeKey() {
            return probeKey;
        }
        
        public PeriodStat getStatisticsByPeriod(CallTimePeriods period){
            return data.get(period);
        }
        
        public void addStatistcs(PeriodStat statistics){
            data.put(statistics.getPeriod(), statistics);
        }
    }
    
    /**
     * <p>
     * To keep information about statistics in period for one probe.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    public class PeriodStat{
        
        private CallTimePeriods period;
        private HashMap<Date, HashMap<IStatisticsHeader, Number>> data;
        
        public PeriodStat(CallTimePeriods periodKey) {
            period = periodKey;
            data = new HashMap<Date, HashMap<IStatisticsHeader,Number>>();
        }
        
        /**
         * @return Returns the period.
         */
        public CallTimePeriods getPeriod() {
            return period;
        }
        
        /**
         * 
         *
         * @param time
         * @param header
         * @param value
         */
        public void addRow(Date time, HashMap<IStatisticsHeader, Number> row){
            data.put(time, row);
        }
        
        /**
         * Get values map from row.
         *
         * @param timeKey Date
         * @return HashMap<StatisticsHeaders, Number> 
         */
        public HashMap<IStatisticsHeader, Number> getRowValues(Date timeKey){
            return data.get(timeKey);
        }
        
        /**
         * Get values map from row.
         *
         * @param timeKey Date
         * @return HashMap<StatisticsHeaders, Long> 
         */
        public HashMap<IStatisticsHeader, Number> getRowValuesForCheck(Date timeKey){
            HashMap<IStatisticsHeader, Number> real = data.get(timeKey);
            HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>();
            for(IStatisticsHeader header : getCallType().getHeaders()){
                Number value = real.get(header);
                if(value == null){
                    value = 0;
                }
                switch (header.getType()) {
                case MAX:
                case MIN:
                case SUM:
                    result.put(header, value.floatValue());
                    break;
                case COUNT:
                    result.put(header, value.longValue());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown header type: "+header.getType()+".");
                }
            }
            return result;
        }
        
        public List<Date> getAllTimesSorted(){
            List<Date> result = new ArrayList<Date>(data.keySet());
            Collections.sort(result);
            return result;
        }
        
        public List<HashMap<IStatisticsHeader, Number>> getDataInBorders(Date start, Date end){
            List<HashMap<IStatisticsHeader, Number>> result = new ArrayList<HashMap<IStatisticsHeader, Number>>();
            List<Date> allTimes = getAllTimesSorted();
            int num = 0;
            Date curr = allTimes.get(num++);
            while (curr.before(start)&&num<allTimes.size()) {
                curr = allTimes.get(num++);
            }
            while (curr.before(end)&&num<allTimes.size()){
                result.add(data.get(curr));
                curr = allTimes.get(num++);
            }
            return result;
        }
    }
}
