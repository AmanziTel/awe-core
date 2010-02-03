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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.amanzi.awe.views.calls.CallTimePeriods;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.CallProperties;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.enums.CallProperties.CallType;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * Class for creating Call Statistics
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class CallStatistics {
    
    /*
     * a Hour period
     */
    private static final long HOUR = 1000 * 60 * 60;
    
    /*
     * a Day period
     */
    private static final long DAY = 24 * HOUR;
    
    /*
     * a Week period
     */
    private static final long WEEK = 7 * DAY;
    
    /*
     * a Month period
     */
    private static final long MONTH = 28 * DAY;
    
    /*
     * Type of Statistics calculation
     */
    enum StatisticsType {
        MIN, MAX, COUNT, SUM;
    }
    
    /**
     * Headers of Statistics
     * 
     * @author Lagutko_N
     * @since 1.0.0
     */
    enum StatisticsHeaders {
        CALL_ATTEMPT_COUNT("SL-SRV-SC-1_CALL_ATTEMPT_COUNT", 0, StatisticsType.COUNT),
        SUCC_SETUP_COUNT("SL-SRV-SC-1_SUCC_SETUP_COUNT", 1, StatisticsType.COUNT),
        SETUP_TM_Z1_P1("SL-SRV-SC-2_SETUP_TM_Z1_P1", 2, StatisticsType.COUNT),
        SETUP_TM_Z1_P2("SL-SRV-SC-2_SETUP_TM_Z1_P2", 3, StatisticsType.COUNT),
        SETUP_TM_Z1_P3("SL-SRV-SC-2_SETUP_TM_Z1_P3", 4, StatisticsType.COUNT),
        SETUP_TM_Z1_P4("SL-SRV-SC-2_SETUP_TM_Z1_P4", 5, StatisticsType.COUNT),
        SETUP_TM_Z1_L1("SL-SRV-SC-2_SETUP_TM_Z1_L1", 6, StatisticsType.COUNT),
        SETUP_TM_Z1_L2("SL-SRV-SC-2_SETUP_TM_Z1_L2", 7, StatisticsType.COUNT),
        SETUP_TM_Z1_L3("SL-SRV-SC-2_SETUP_TM_Z1_L3", 8, StatisticsType.COUNT),
        SETUP_TM_Z1_L4("SL-SRV-SC-2_SETUP_TM_Z1_L4", 9, StatisticsType.COUNT),
        SETUP_TIME_MIN("SL-SRV-SC-2_SETUP_TIME_MIN", 10, StatisticsType.MIN),
        SETUP_TIME_MAX("SL-SRV-SC-2_SETUP_TIME_MAX", 11, StatisticsType.MAX),
        SETUP_TOTAL_DUR("SL-SRV-SC-2_SETUP_TOTAL_DUR", 12, StatisticsType.SUM);
        
        
        private static class StatisticsHeaderComaprator implements Comparator<StatisticsHeaders> {

            @Override
            public int compare(StatisticsHeaders o1, StatisticsHeaders o2) {
                return o2.getIndex() - o1.getIndex();
            }
            
        }
        
        private String headerTitle;
        
        private int headerIndex;
        
        private StatisticsType type;
        
        private StatisticsHeaders(String headerTitle, int headerIndex, StatisticsType type) {
            this.headerTitle = headerTitle;            
            this.headerIndex = headerIndex;
            this.type = type;
        }
        
        @Override
        public String toString() {
            return headerTitle;
        }
        
        public String getTitle() {
            return headerTitle;
        }
        
        public int getIndex() {
            return headerIndex;
        }
        
        public static StatisticsHeaders getHeaderByTitle(String title) {
            for (StatisticsHeaders header : values()) {
                if (header.getTitle().equals(title)) {
                    return header;
                }
            }
            return null;
        }
        
        public static StatisticsHeaders[] getOrderedHeaders() {
            StatisticsHeaders[] headers = values();
            Arrays.sort(headers, 0, headers.length - 1, new StatisticsHeaderComaprator());
            return headers;
        }
        
        public StatisticsType getType() {
            return type;
        }
    }
    
    private String amsDatasetName;
    
    private NeoService neoService;
    
    private HashMap<CallTimePeriods, Node> previousSCellNodes = new HashMap<CallTimePeriods, Node>();
    
    private HashMap<CallTimePeriods, Node> previousSRowNodes = new HashMap<CallTimePeriods, Node>();
    
    public CallStatistics(String amsDatasetName) {
        neoService = NeoServiceProvider.getProvider().getService();
        
        this.amsDatasetName = amsDatasetName; 
    }
    
    public void createStatistics() throws Exception {
        Node datasetNode = NeoUtils.getAllDatasetNodes(neoService).get(amsDatasetName);
        
        Pair<Long, Long> minMax = getTimeBounds(datasetNode);
        long minTime = minMax.getLeft();
        long maxTime = minMax.getRight();
        
        CallTimePeriods period = getHighestPeriod(minTime, maxTime);
        
        Node parentNode = createRootStatisticsNode(datasetNode);
        
        for (Node probe : NeoUtils.getAllProbesOfDataset(datasetNode)) {
            String probeName = (String)probe.getProperty(INeoConstants.PROPERTY_NAME_NAME);
            Node probeCallsNode = NeoUtils.getCallsNode(datasetNode, probeName, probe, neoService);
            String callProbeName = (String)probeCallsNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
            
            MultiPropertyIndex<Long> timeIndex = NeoUtils.getTimeIndexProperty(callProbeName);
            
            createStatistics(parentNode, null, null, probe, timeIndex, period, minTime, maxTime);
        }
    }
    
    private Node createRootStatisticsNode(Node datasetNode) {
        Node result = neoService.createNode();
        
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.CALL_ANALYZIS_ROOT);
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, INeoConstants.CALL_ANALYZIS_ROOT);
        
        datasetNode.createRelationshipTo(result, ProbeCallRelationshipType.CALL_ANALYZIS);
        
        return result;
    }
    
    private CallTimePeriods getHighestPeriod(long minTime, long maxTime) {
        long delta = maxTime - minTime;
        
        if (delta > MONTH) {
            return CallTimePeriods.MONTHLY;
        }
        if (delta > WEEK) {
            return CallTimePeriods.WEEKLY;
        }
        if (delta > DAY) {
            return CallTimePeriods.DAILY;
        }
        
        return CallTimePeriods.HOURLY;
    }
    
    private Pair<Long, Long> getTimeBounds(Node dataset) throws Exception {
        Transaction transaction = neoService.beginTx();
        
        Pair<Long, Long> minMax = null;
        
        try {
            Node gisNode = NeoUtils.findGisNodeByChild(dataset);
            
            Long minTime = (Long)gisNode.getProperty(INeoConstants.MIN_TIMESTAMP);
            Long maxTime = (Long)gisNode.getProperty(INeoConstants.MAX_TIMESTAMP);
            
            minMax = new Pair<Long, Long>(minTime, maxTime);
            
            transaction.success();
        }
        catch (Exception e) {
            transaction.failure();
            NeoCorePlugin.error(null, e);
            throw e;
        }
        finally {
            transaction.finish();
        }
        
        return minMax;
    }
    
    private Long getSCellTime(Node sCell) {
        return (Long)sCell.getProperty(INeoConstants.PROPERTY_TIME_NAME);
    }
    
    private Statistics getStatisticsFromDatabase(Node statisticsNode, final long minDate, final long maxDate) {
        Iterator<Node> statisticsNodes = statisticsNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                if (NeoUtils.getNodeType(currentPos.currentNode()).equals(INeoConstants.S_CELL)) {
                    Long sCellTime = getSCellTime(currentPos.currentNode());
                    if ((sCellTime >= minDate) && (sCellTime < maxDate)) {
                        return true;
                    }
                }
                return false;                
            }
        }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
        
        boolean hasInDb = false;
        Statistics statistics = new Statistics();
        while (statisticsNodes.hasNext()) {
            hasInDb = true;
            Node sCell = statisticsNodes.next();
            
            String headerName = (String)sCell.getProperty(INeoConstants.PROPERTY_NAME_NAME);
            Object value = (String)sCell.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
            StatisticsHeaders header = StatisticsHeaders.getHeaderByTitle(headerName);
            
            statistics.updateHeaderWithCall(header, value, sCell);
        }
        
        if (hasInDb) {
            return statistics;
        }
        else {
            return null;
        }
    }
    
    private Statistics createStatistics(Node parentNode, Node highStatisticsNode, Node highLevelSRow, Node probeNode, MultiPropertyIndex<Long> timeIndex, CallTimePeriods period, long startDate, long endDate) {
        Statistics statistics = new Statistics();
        
        startDate = period.getFirstTime(startDate);
        long nextStartDate = period.addPeriod(startDate);
        
        Node statisticsNode = getStatisticsNode(parentNode, period);
        if (highStatisticsNode != null) {
            highStatisticsNode.createRelationshipTo(statisticsNode, GeoNeoRelationshipTypes.SOURCE);
        }
        
        do {
            Node sRow = createSRowNode(statisticsNode, new Date(startDate), probeNode, highLevelSRow, period);
            
            Statistics periodStatitics;
            if (period == CallTimePeriods.HOURLY) {
                periodStatitics = getStatisticsByHour(timeIndex, startDate);
            }
            else {
                periodStatitics = getStatisticsFromDatabase(statisticsNode, startDate, nextStartDate);
                if (periodStatitics == null) {
                    periodStatitics = createStatistics(parentNode, statisticsNode, sRow, probeNode, timeIndex, period.getUnderlyingPeriod(), startDate, nextStartDate);
                }
            }
            
            for (StatisticsHeaders header : StatisticsHeaders.getOrderedHeaders()) {
                createSCellNode(sRow, periodStatitics, header, period);
            }
            
            updateStatistics(statistics, periodStatitics);
            
            startDate = nextStartDate;
            nextStartDate = period.addPeriod(startDate);
        }
        while (nextStartDate < endDate);
        
        
        
        return statistics;
    }
    
    private Node getStatisticsNode(Node parent, final CallTimePeriods period) {
        Iterator<Node> nodes = parent.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return period.getId().equals(NeoUtils.getNodeName(currentPos.currentNode()));
            }
        }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
        
        if (nodes.hasNext()) {
            return nodes.next();
        }
        else {        
            Node result = neoService.createNode();
        
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, period.getId());
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.CALL_ANALYZIS);
            parent.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
            
            return result;
        }
    }
    
    private Node createSRowNode(Node parent, Date startDate, Node probeNode, Node highLevelSRow, CallTimePeriods period) {
        Node result = neoService.createNode();
        
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.S_ROW);
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, startDate.toString());
        result.setProperty(INeoConstants.PROPERTY_TIME_NAME, startDate.getTime());
        
        result.createRelationshipTo(probeNode, GeoNeoRelationshipTypes.SOURCE);
        
        Node previousNode = previousSRowNodes.get(period);
        if (previousNode == null) {
            parent.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
        }
        else {
            previousNode.createRelationshipTo(result, GeoNeoRelationshipTypes.NEXT);
        }
        previousSRowNodes.put(period, result);
        
        if (highLevelSRow != null) {
            highLevelSRow.createRelationshipTo(result, GeoNeoRelationshipTypes.SOURCE);
        }
        
        return result;
    }
    
    private Node createSCellNode(Node parent, Statistics statistics, StatisticsHeaders header, CallTimePeriods period) {
        Node result = neoService.createNode();
        
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.S_CELL);
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, header.getTitle());
        Object value = statistics.get(header);
        if (value == null) {
            value = 0;            
        }
        else {        
            for (Node callNode : statistics.getAllAffectedCalls(header)) {
                result.createRelationshipTo(callNode, GeoNeoRelationshipTypes.SOURCE);            
            }
            statistics.getAllAffectedCalls(header).clear();
        }
        result.setProperty(INeoConstants.PROPERTY_VALUE_NAME, value);
        
        statistics.updateSourceNodes(header, result);
        
        Node previousNode = previousSCellNodes.get(period);
        if (previousNode == null) {
            parent.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
        }
        else {
            previousNode.createRelationshipTo(result, GeoNeoRelationshipTypes.NEXT);
        }
        previousSCellNodes.put(period, result);
        
        return result;
    }
    
    private void updateStatistics(Statistics original, Statistics newValues) { 
        for (Entry<StatisticsHeaders, Object> entry : newValues.entrySet()) {
            StatisticsHeaders header = entry.getKey();
            original.updateHeader(header, entry.getValue());
            original.copyAllSourceNodes(header, newValues.getAllAffectedCalls(header));
        }
            
    }
    
    private Statistics getStatisticsByHour(MultiPropertyIndex<Long> timeIndex, long startTime) {
        Statistics statistics = new Statistics();
        
        long endTime = startTime + HOUR;
        
        Collection<Node> callNodes = timeIndex.find(new Long[] {startTime}, new Long[] {endTime});
        
        for (Node singleNode : callNodes) {
            updateCallStatistics(singleNode, statistics);
        }
        
        return statistics;
    }
    
    private void updateCallStatistics(Node callNode, Statistics statistics) {
        statistics.updateHeaderWithCall(StatisticsHeaders.CALL_ATTEMPT_COUNT, 1, callNode);
        
        CallType callType = CallType.valueOf((String)callNode.getProperty(CallProperties.CALL_TYPE.getId()));
        if (callType == CallType.SUCCESS) {
            long connectionTime = (Long)callNode.getProperty(CallProperties.SETUP_DURATION.getId());
            processConnectionTime(connectionTime, statistics, callNode);
        }
    }
    
    private void processConnectionTime(long connectionTime, Statistics statistics, Node callNode) {
        float connTimeSec = (float)connectionTime / StatisticsConstants.MILLISECONDS_FACTOR;
        
        if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_LIMIT) {        
            statistics.updateHeaderWithCall(StatisticsHeaders.SUCC_SETUP_COUNT, 1, callNode);
            statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TIME_MAX, connTimeSec, callNode);
            statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TIME_MIN, connTimeSec, callNode);
            statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TOTAL_DUR, connTimeSec, callNode);
        }
        else {
            return;
        }
        
        if (connTimeSec > StatisticsConstants.INDIV_CALL_CONN_TIME_P1) {
            if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_P2) {
                statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TM_Z1_P1, 1, callNode);
            }
            else if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_P3) {
                statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TM_Z1_P2, 1, callNode);
            }
            else if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_P4) {
                statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TM_Z1_P3, 1, callNode);
            }
            else if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_L1) {
                statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TM_Z1_P4, 1, callNode);
            }
            else if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_L2) {
                statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TM_Z1_L1, 1, callNode);
            }
            else if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_L3) {
                statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TM_Z1_L2, 1, callNode);
            }
            else if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_L4) {
                statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TM_Z1_L3, 1, callNode);
            }
            else if (connTimeSec < StatisticsConstants.INDIV_CALL_CONN_TIME_LIMIT) {
                statistics.updateHeaderWithCall(StatisticsHeaders.SETUP_TM_Z1_L4, 1, callNode);
            }
        }
    }

}
