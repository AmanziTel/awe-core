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

package org.amanzi.awe.views.calls.statistics;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.CallAnalyserPlugin;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.InclInconclusiveStates;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.CallProperties;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Call statistics with include inconclusive calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CallStatisticsInconclusive extends CallStatistics {
    
    private InclInconclusiveStates inclState;

    /**
     * Constructor.
     * @param drive
     * @param service
     * @param monitor
     * @throws IOException 
     */
    public CallStatisticsInconclusive(Node drive, GraphDatabaseService service, IProgressMonitor monitor, InclInconclusiveStates state) throws IOException {
        super();
        inclState = state;
        initialyzeStatistics(drive, service, monitor);        
    }

    /**
     * Constructor.
     * @param drive
     * @param service
     * @throws IOException 
     */
    public CallStatisticsInconclusive(Node drive, GraphDatabaseService service, InclInconclusiveStates state) throws IOException {
        super();
        inclState = state;
        initialyzeStatistics(drive, service, new NullProgressMonitor());        
    }
    
    @Override
    protected void initialyzeStatistics(Node drive, GraphDatabaseService service, IProgressMonitor aMonitor) throws IOException {
        initFields(drive, service, aMonitor);
        setStatisticNode(createStatisticInconclusive());
        
        finishInitialize(inclState);
    }

    /**
     * Create statistics with inconclusive calls.
     *
     * @return HashMap<StatisticsCallType, Node>
     * @throws IOException
     */
    private HashMap<StatisticsCallType, Node> createStatisticInconclusive() throws IOException {
        startTransaction(false);
        Node parentNode = null;
        getMonitor().subTask("Search statistics in data base");
        HashMap<StatisticsCallType, Node> result = new HashMap<StatisticsCallType, Node>();
        final GraphDatabaseService neoService = getNeoService();
        try{
            Node datasetNode = getDatasetNode();
            Iterator<Node> analyzisNodes = datasetNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, 
                    new ReturnableEvaluator() {                
                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            if(currentPos.isStartNode()){
                                return false;
                            }
                            Node node = currentPos.currentNode();
                            if(!NodeTypes.getNodeType(node, neoService).equals(NodeTypes.CALL_ANALYSIS_ROOT)){
                                return false;
                            }
                            InclInconclusiveStates inconclusive = InclInconclusiveStates.getStateById(node.getProperty(INeoConstants.PROPERTY_INCONCLUSIVE_STATE, "").toString());
                            return inconclusive!=null&&inconclusive.equals(inclState);
                        }
                    }, ProbeCallRelationshipType.CALL_ANALYSIS, Direction.OUTGOING).iterator();
            
            int canceledCount = 0;
            while (analyzisNodes.hasNext()) {
                Node analyzisNode = analyzisNodes.next();
                StatisticsCallType type = StatisticsCallType.getTypeById((String)analyzisNode.getProperty(CallProperties.CALL_TYPE.getId()));
                result.put(type, analyzisNode);
                boolean canceled = (Boolean)analyzisNode.getProperty(INeoConstants.PROPERTY_CANCELED_NAME,false);
                if(canceled){
                    canceledCount++;
                }
            }
            
            if (!result.isEmpty()&&canceledCount==0) {
                return result;
            }
            
            if(canceledCount>0){
                deleteCanceledStatistics(result);
            }
            
            HashMap<StatisticsCallType, Node> sourseStatistics = createStatistics();
            startTransaction(true);//transaction was finished in create source statistics.
            Pair<Long, Long> minMax = getTimeBounds(datasetNode);
            long minTime = minMax.getLeft();
            long maxTime = minMax.getRight();
            CallTimePeriods period = CallStatisticsUtills.getHighestPeriod(minTime, maxTime);            
            List<StatisticsCallType> callTypes = StatisticsCallType.getTypesByLevel(StatisticsCallType.FIRST_LEVEL);
            IProgressMonitor subMonitor = SubMonitor.convert(getMonitor(), callTypes.size());
            subMonitor.beginTask("Create AMS statistics include inconclusive events", callTypes.size());
            for (StatisticsCallType callType : callTypes) {
                subMonitor.subTask("Build "+callType.getViewName()+" statistics.");
                Collection<Node> probesByCallType = NeoUtils.getAllProbesOfDataset(datasetNode, callType.getId());
                if (probesByCallType.isEmpty()) {
                    subMonitor.worked(1);
                    continue;
                }
                Node sourceRoot = sourseStatistics.get(callType);
                parentNode = createRootStatisticsNode(datasetNode, callType,sourceRoot,inclState);
                result.put(callType, parentNode);
                for (Node probe : probesByCallType) {
                    if(getMonitor().isCanceled()){
                        break;
                    }
                    String probeName = (String)probe.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                    subMonitor.subTask("Build "+callType.getViewName()+" statistics for probe "+probeName+".");
                    CallAnalyserPlugin.info("Build "+callType.getViewName()+" statistics for probe "+probeName+".");
                    Node probeCallsNode = NeoUtils.getCallsNode(datasetNode, probeName, probe, neoService);
                    String callProbeName = (String)probeCallsNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                    if (NeoUtils.findMultiPropertyIndex(NeoUtils.getTimeIndexName(callProbeName), neoService) != null) {                
                        MultiPropertyIndex<Long> timeIndex = NeoUtils.getTimeIndexProperty(callProbeName);
                        timeIndex.initialize(neoService, null);
                        createStatisticsInconclusive(parentNode, sourceRoot, null, null, probe, timeIndex, period, callType, minTime, maxTime);                        
                    }
                    commit();
                }
                getPreviousSRowNodes().clear();
                if(getMonitor().isCanceled()){
                    break;
                }
                subMonitor.worked(1);
            }
            getTransaction().success();
            
        }catch (Exception e) {            
            getTransaction().failure();
            NeoCorePlugin.error(null, e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        finally {
            getTransaction().finish();
        }
        return result;
    }
    
    /**
     * Create statistics with inconclusive calls.
     *
     * @param parentNode
     * @param sourseRootNode
     * @param highStatisticsNode
     * @param highLevelSRow
     * @param probeNode
     * @param timeIndex
     * @param period
     * @param callType
     * @param startDate
     * @param endDate
     * @return Statistics
     */
    private Statistics createStatisticsInconclusive(Node parentNode, Node sourseRootNode, Node highStatisticsNode, Node highLevelSRow, Node probeNode, MultiPropertyIndex<Long> timeIndex, CallTimePeriods period, StatisticsCallType callType, long startDate, long endDate) {
        Statistics statistics = new Statistics();
        long currentStartDate = period.getFirstTime(startDate);
        long nextStartDate = CallStatisticsUtills.getNextStartDate(period, endDate, currentStartDate);
        
        if (startDate > currentStartDate) {
            currentStartDate = startDate;
        }
        Node sourceStatNode = getStatisticsNodeFromDB(sourseRootNode, period);
        HashMap<Long, Node> sourceRows = getSourceRows(sourceStatNode, probeNode);
        Node statisticsNode = getStatisticsNode(parentNode,sourceStatNode, period);
        if (highStatisticsNode != null) {
            highStatisticsNode.createRelationshipTo(statisticsNode, GeoNeoRelationshipTypes.SOURCE);
        }
        
        do {
            if(getMonitor().isCanceled()){
                break;
            }
            Node sourceRow = sourceRows.get(period.getFirstTime(currentStartDate));
            Node row = createSRowNode(statisticsNode, period.getFirstTime(currentStartDate), sourceRow==null?probeNode:sourceRow, highLevelSRow, period);
            
            Statistics periodStatitics = new Statistics();
            if (period == CallTimePeriods.HOURLY) {
                periodStatitics = getStatisticsByHour(sourceRow, timeIndex, callType, currentStartDate, nextStartDate,inclState);
            }
            else {
                periodStatitics = getStatisticsFromDatabase(statisticsNode, callType, currentStartDate, nextStartDate, sourceRow==null?probeNode:sourceRow, true);
                if (periodStatitics == null) {
                    periodStatitics = createStatisticsInconclusive(parentNode, sourseRootNode, statisticsNode, row, probeNode, timeIndex, period.getUnderlyingPeriod(), callType, currentStartDate, nextStartDate);
                }
            }
            
            for (IStatisticsHeader header : callType.getHeaders()) {                
                createSCellNode(row, periodStatitics, header, period);
            }
            
            updateStatistics(statistics, periodStatitics);       
            
            getPreviousSCellNodes().put(period, null);
            currentStartDate = nextStartDate;
            nextStartDate = CallStatisticsUtills.getNextStartDate(period, endDate, currentStartDate);
            commit();
        }
        while (currentStartDate < endDate);
        return statistics;
    }
    
    /**
     * Get source statistics rows.
     *
     * @param sourceRoot
     * @param probeNode
     * @return HashMap<Long, Node>
     */
    private HashMap<Long, Node> getSourceRows(Node sourceRoot,Node probeNode){
        HashMap<Long, Node> result = new HashMap<Long, Node>();
        if(sourceRoot == null){
            return result;
        }
        final String probeName = NeoUtils.getNodeName(probeNode, getNeoService());
        Traverser rows = NeoUtils.getChildTraverser(sourceRoot, new ReturnableEvaluator() {            
                                                    @Override
                                                    public boolean isReturnableNode(TraversalPosition currentPos) {
                                                        Node node = currentPos.currentNode();
                                                        for(Relationship relation : node.getRelationships(GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING)){
                                                            Node source = relation.getEndNode();
                                                            NodeTypes sourceType = NodeTypes.getNodeType(source, getNeoService());
                                                            if(sourceType!=null&&sourceType.equals(NodeTypes.PROBE)){
                                                                String name = NeoUtils.getNodeName(source, getNeoService());
                                                                if(name.equals(probeName)){
                                                                    return true;
                                                                }
                                                            }
                                                        }
                                                        return false;
                                                    }
                                                });
        for(Node row : rows){
            Long time = (Long)row.getProperty(INeoConstants.PROPERTY_TIME_NAME, 0L);
            result.put(time, row);
        }
        return result;
    }
}
