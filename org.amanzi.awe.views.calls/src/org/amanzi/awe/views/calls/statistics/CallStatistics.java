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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.enums.StatisticsType;
import org.amanzi.awe.views.calls.statistics.constants.GroupCallConstants;
import org.amanzi.awe.views.calls.statistics.constants.ICallStatisticsConstants;
import org.amanzi.awe.views.calls.statistics.constants.IndividualCallConstants;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.CallProperties;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;

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
     * Name of AMS dataset
     */
    private String amsDatasetName;
    
    /*
     * Neo Service 
     */
    private GraphDatabaseService neoService;
    
    /*
     * Map of previous s_cell nodes for each period
     */
    private HashMap<CallTimePeriods, Node> previousSCellNodes = new HashMap<CallTimePeriods, Node>();
    
    /*
     * Map of previous s_row nodes for each period
     */
    private HashMap<CallTimePeriods, Node> previousSRowNodes = new HashMap<CallTimePeriods, Node>();

    /*
     * Dataset Node for calculating statistics
     */
    private Node datasetNode;

    /*
     * Root statistics Node
     */
    private HashMap<StatisticsCallType, Node> statisticNode = new HashMap<StatisticsCallType, Node>();

    /*
     * Highest period 
     */
    private CallTimePeriods highPeriod;
    
    private HashMap<StatisticsCallType, ICallStatisticsConstants> statisticsConstants = new HashMap<StatisticsCallType, ICallStatisticsConstants>();
    
   /**
     * Creates Calculator of Call Statistics
     * 
     * @param drive Dataset Node
     * @throws IOException if was problem in initializing of indexes
     */
    public CallStatistics(Node drive, GraphDatabaseService service) throws IOException {
        assert drive != null;
        datasetNode = drive;
        neoService = service;
        
        statisticsConstants.put(StatisticsCallType.INDIVIDUAL, new IndividualCallConstants());
        statisticsConstants.put(StatisticsCallType.GROUP, new GroupCallConstants());
        
        Transaction tx = neoService.beginTx();
        try {
            statisticNode = createStatistics();
            Pair<Long, Long> minMax = getTimeBounds(datasetNode);
            long minTime = minMax.getLeft();
            long maxTime = minMax.getRight();
            highPeriod = getHighestPeriod(minTime, maxTime);            
        }
        catch (Exception e) {
            tx.failure();
        } finally {
            tx.finish();
        }
    }
    
    /**
     * Creates Statistics Node and calculates Call Statistics
     * 
     * @return Root statistics Node
     * @throws IOException 
     */
    private HashMap<StatisticsCallType, Node> createStatistics() throws IOException {
        Transaction tx = neoService.beginTx();
        Node parentNode = null;
        HashMap<StatisticsCallType, Node> result = new HashMap<StatisticsCallType, Node>();
        
        try {
            if (datasetNode == null) {
                datasetNode = NeoUtils.getAllDatasetNodes(neoService).get(amsDatasetName);
            }
            
            Iterator<Node> analyzisNodes = datasetNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, ProbeCallRelationshipType.CALL_ANALYZIS, Direction.OUTGOING).iterator();
            
            while (analyzisNodes.hasNext()) {
                Node analyzisNode = analyzisNodes.next();
                StatisticsCallType type = StatisticsCallType.getTypeById((String)analyzisNode.getProperty(CallProperties.CALL_TYPE.getId()));
                result.put(type, analyzisNode);
            }
            
            if (!result.isEmpty()) {
                return result;
            }
            
            Pair<Long, Long> minMax = getTimeBounds(datasetNode);
            long minTime = minMax.getLeft();
            long maxTime = minMax.getRight();
        
            CallTimePeriods period = getHighestPeriod(minTime, maxTime);
            
            for (StatisticsCallType callType : StatisticsCallType.getTypesByLevel(StatisticsCallType.FIRST_LEVEL)) {
                Collection<Node> probesByCallType = NeoUtils.getAllProbesOfDataset(datasetNode, callType.getId());
                if (probesByCallType.isEmpty()) {
                    continue;
                }
                
                parentNode = createRootStatisticsNode(datasetNode, callType);
                result.put(callType, parentNode);
                for (Node probe : probesByCallType) {
                    String probeName = (String)probe.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                    Node probeCallsNode = NeoUtils.getCallsNode(datasetNode, probeName, probe, neoService);
                    String callProbeName = (String)probeCallsNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                
                    if (NeoUtils.findMultiPropertyIndex(NeoUtils.getTimeIndexName(callProbeName), neoService) != null) {                
                        MultiPropertyIndex<Long> timeIndex = NeoUtils.getTimeIndexProperty(callProbeName);
                        timeIndex.initialize(neoService, null);
                        createStatistics(parentNode, null, null, probe, timeIndex, period, callType, minTime, maxTime);                        
                    }
                }
                previousSRowNodes.clear();
            }
            /*parentNode = createAggrStatisticsByPeriod(null,period, result);
            if (parentNode!=null) {
                //result.put(StatisticsCallType.AGGREGATION_STATISTICS, parentNode); TODO uncomment after finish
            }*/
            tx.success();
        }
        catch (Exception e) {            
            tx.failure();
            NeoCorePlugin.error(null, e);
        }
        finally {
            tx.finish();
        }
        return result;
    }
    
    private Node createRootStatisticsNode(Node datasetNode, StatisticsCallType callType) {
        Node result = neoService.createNode();
        
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.CALL_ANALYZIS_ROOT.getId());
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, INeoConstants.CALL_ANALYZIS_ROOT);
        result.setProperty(INeoConstants.PROPERTY_VALUE_NAME, NeoUtils.getNodeName(datasetNode,neoService));
        result.setProperty(CallProperties.CALL_TYPE.getId(), callType.toString());
        
        datasetNode.createRelationshipTo(result, ProbeCallRelationshipType.CALL_ANALYZIS);
        
        return result;
    }
    
    /**
     * Create second level statistics.
     *
     * @param parent Node
     * @param highestPeriod CallTimePeriods
     * @return Node (root for statistics)
     */
    private Node createAggrStatisticsByPeriod(Node parent, CallTimePeriods period, HashMap<StatisticsCallType, Node> sourceStatistics){
        if(period==null){
            return parent; 
        }   
        Node rootNode = createAggrStatisticsByPeriod(parent, period.getUnderlyingPeriod(), sourceStatistics);
        boolean hasStatistics = false;
        HashMap<Long, Statistics> rows = new HashMap<Long, Statistics>();
        HashMap<Long, List<Node>> sourceRows = new HashMap<Long, List<Node>>();
        
        StatisticsCallType currType = StatisticsCallType.INDIVIDUAL;        
        Node periodNode = getPeriodNode(period, currType, sourceStatistics);
        boolean hasIndividual = periodNode!=null;
        if(hasIndividual){
            hasStatistics = true;
            HashMap<Long, Node> currSourceRows = getAllPeriodRows(periodNode);            
            for(Long time : currSourceRows.keySet()){
                Node source = currSourceRows.get(time);
                Statistics row = rows.get(time);
                if(row==null){
                    row = new Statistics();
                    rows.put(time, row);
                }
                List<Node> srows = sourceRows.get(time);
                if(srows==null){
                    srows = new ArrayList<Node>();
                    sourceRows.put(time, srows);
                }
                srows.add(source);
                HashMap<StatisticsHeaders, Node> sourceCells = getAllRowCells(source, currType);
                Node cell = sourceCells.get(StatisticsHeaders.SUCC_SETUP_COUNT);
                StatisticsHeaders currHeader = StatisticsHeaders.SC_SUCC_SETUP_COUNT;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.CALL_ATTEMPT_COUNT);
                currHeader = StatisticsHeaders.SC_ATTEMPT_COUNT;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.SETUP_TIME_MAX);
                currHeader = StatisticsHeaders.SC_SETUP_TIME_MAX;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.SETUP_TIME_MIN);
                currHeader = StatisticsHeaders.SC_SETUP_TIME_MIN;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.SETUP_TOTAL_DUR);
                currHeader = StatisticsHeaders.SC_SETUP_TIME_TOTAL;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.CALL_DISC_TIME);
                currHeader = StatisticsHeaders.SC_CALL_DISC_TIME;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_SUCC);
                currHeader = StatisticsHeaders.SC_AUDIO_QUAL_SUCC;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                currHeader = StatisticsHeaders.SC_AUDIO_QUAL_COUNT;
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_P1);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_P2);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_P3);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_P4);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_L1);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_L2);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_L3);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_L4);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_MAX);
                currHeader = StatisticsHeaders.SC_AUDIO_QUAL_MAX;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_MIN);
                currHeader = StatisticsHeaders.SC_AUDIO_QUAL_MIN;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_TOTAL);
                currHeader = StatisticsHeaders.SC_AUDIO_QUAL_TOTAL;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                /*currHeader = StatisticsHeaders.SC_DELAY_COUNT;
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_COUNT_P1);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_COUNT_P2);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_COUNT_P3);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_COUNT_P4);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_COUNT_L1);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_COUNT_L2);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_COUNT_L3);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_COUNT_L4);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_MAX);
                currHeader = StatisticsHeaders.SC_DELAY_MAX;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_MIN);
                currHeader = StatisticsHeaders.SC_DELAY_MIN;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.IND_DELAY_TOTAL);
                currHeader = StatisticsHeaders.SC_DELAY_TOTAL;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);*/
            }            
        }
        
        currType = StatisticsCallType.GROUP;        
        periodNode = getPeriodNode(period, currType, sourceStatistics);
        boolean hasGroup = periodNode!=null;
        if(hasGroup){
            hasStatistics = true;
            HashMap<Long, Node> currSourceRows = getAllPeriodRows(periodNode);            
            for(Long time : currSourceRows.keySet()){
                Node source = currSourceRows.get(time);
                Statistics row = rows.get(time);
                if(row==null){
                    row = new Statistics();
                    rows.put(time, row);
                }
                List<Node> srows = sourceRows.get(time);
                if(srows==null){
                    srows = new ArrayList<Node>();
                    sourceRows.put(time, srows);
                }
                srows.add(source);
                HashMap<StatisticsHeaders, Node> sourceCells = getAllRowCells(source, currType);
                Node cell = sourceCells.get(StatisticsHeaders.SUCC_SETUP_COUNT);
                StatisticsHeaders currHeader = StatisticsHeaders.GC_SUCC_SETUP_COUNT;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.CALL_ATTEMPT_COUNT);
                currHeader = StatisticsHeaders.GC_ATTEMPT_COUNT;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.SETUP_TIME_MAX);
                currHeader = StatisticsHeaders.GC_SETUP_TIME_MAX;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.SETUP_TIME_MIN);
                currHeader = StatisticsHeaders.GC_SETUP_TIME_MIN;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.SETUP_TOTAL_DUR);
                currHeader = StatisticsHeaders.GC_SETUP_TIME_TOTAL;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.CALL_DISC_TIME);
                currHeader = StatisticsHeaders.GC_CALL_DISC_TIME;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_SUCC);
                currHeader = StatisticsHeaders.GC_AUDIO_QUAL_SUCC;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                currHeader = StatisticsHeaders.GC_AUDIO_QUAL_COUNT;
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_P1);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_P2);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_P3);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_P4);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_L1);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_L2);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_L3);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_L4);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_MAX);
                currHeader = StatisticsHeaders.GC_AUDIO_QUAL_MAX;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_MIN);
                currHeader = StatisticsHeaders.GC_AUDIO_QUAL_MIN;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.AUDIO_QUAL_TOTAL);
                currHeader = StatisticsHeaders.GC_AUDIO_QUAL_TOTAL;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                /*currHeader = StatisticsHeaders.GC_DELAY_COUNT;
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_COUNT_P1);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_COUNT_P2);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_COUNT_P3);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_COUNT_P4);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_COUNT_L1);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_COUNT_L2);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_COUNT_L3);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_COUNT_L4);                
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_MAX);
                currHeader = StatisticsHeaders.GC_DELAY_MAX;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_MIN);
                currHeader = StatisticsHeaders.GC_DELAY_MIN;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.GR_DELAY_TOTAL);
                currHeader = StatisticsHeaders.GC_DELAY_TOTAL;
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);*/
            }            
        }
        
        currType = StatisticsCallType.ITSI_CC;        
        periodNode = getPeriodNode(period, currType, sourceStatistics);
        boolean hasItsiCc = periodNode!=null;
        if(hasItsiCc){
            hasStatistics = true;
            HashMap<Long, Node> currSourceRows = getAllPeriodRows(periodNode);            
            for(Long time : currSourceRows.keySet()){
                Node source = currSourceRows.get(time);
                Statistics row = rows.get(time);
                if(row==null){
                    row = new Statistics();
                    rows.put(time, row);
                }
                List<Node> srows = sourceRows.get(time);
                if(srows==null){
                    srows = new ArrayList<Node>();
                    sourceRows.put(time, srows);
                }
                srows.add(source);
                HashMap<StatisticsHeaders, Node> sourceCells = getAllRowCells(source, currType);                
                StatisticsHeaders currHeader = StatisticsHeaders.INH_CC_ATTEMPT;
                Node cell = sourceCells.get(StatisticsHeaders.CC_HO_ATTEMPTS);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.CC_RES_ATTEMPTS);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                currHeader = StatisticsHeaders.INH_CC_SUCCESS;
                cell = sourceCells.get(StatisticsHeaders.CC_HO_SUCCESS);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                cell = sourceCells.get(StatisticsHeaders.CC_RES_SUCCESS);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
            }            
        }
        
        currType = StatisticsCallType.TSM;        
        periodNode = getPeriodNode(period, currType, sourceStatistics);
        boolean hasTsm = periodNode!=null;
        if(hasTsm){
            hasStatistics = true;
            HashMap<Long, Node> currSourceRows = getAllPeriodRows(periodNode);            
            for(Long time : currSourceRows.keySet()){
                Node source = currSourceRows.get(time);
                Statistics row = rows.get(time);
                if(row==null){
                    row = new Statistics();
                    rows.put(time, row);
                }
                List<Node> srows = sourceRows.get(time);
                if(srows==null){
                    srows = new ArrayList<Node>();
                    sourceRows.put(time, srows);
                }
                srows.add(source);
                HashMap<StatisticsHeaders, Node> sourceCells = getAllRowCells(source, currType);                
                StatisticsHeaders currHeader = StatisticsHeaders.TSM_ATTEMPT;
                Node cell = sourceCells.get(StatisticsHeaders.TSM_MESSAGE_ATTEMPT);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                currHeader = StatisticsHeaders.TSM_SUCCESS;
                cell = sourceCells.get(StatisticsHeaders.TSM_MESSAGE_SUCC);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
            }            
        }
        
        currType = StatisticsCallType.SDS;        
        periodNode = getPeriodNode(period, currType, sourceStatistics);
        boolean hasSds = periodNode!=null;
        if(hasSds){
            hasStatistics = true;
            HashMap<Long, Node> currSourceRows = getAllPeriodRows(periodNode);            
            for(Long time : currSourceRows.keySet()){
                Node source = currSourceRows.get(time);
                Statistics row = rows.get(time);
                if(row==null){
                    row = new Statistics();
                    rows.put(time, row);
                }
                List<Node> srows = sourceRows.get(time);
                if(srows==null){
                    srows = new ArrayList<Node>();
                    sourceRows.put(time, srows);
                }
                srows.add(source);
                HashMap<StatisticsHeaders, Node> sourceCells = getAllRowCells(source, currType);                
                StatisticsHeaders currHeader = StatisticsHeaders.SDS_ATTEMPT;
                Node cell = sourceCells.get(StatisticsHeaders.SDS_MESSAGE_ATTEMPT);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                currHeader = StatisticsHeaders.SDS_SUCCESS;
                cell = sourceCells.get(StatisticsHeaders.SDS_MESSAGE_SUCC);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
            }            
        }

        currType = StatisticsCallType.ITSI_ATTACH;        
        periodNode = getPeriodNode(period, currType, sourceStatistics);
        boolean hasItsiAtt = periodNode!=null;
        if(hasItsiAtt){
            hasStatistics = true;
            HashMap<Long, Node> currSourceRows = getAllPeriodRows(periodNode);            
            for(Long time : currSourceRows.keySet()){
                Node source = currSourceRows.get(time);
                Statistics row = rows.get(time);
                if(row==null){
                    row = new Statistics();
                    rows.put(time, row);
                }
                List<Node> srows = sourceRows.get(time);
                if(srows==null){
                    srows = new ArrayList<Node>();
                    sourceRows.put(time, srows);
                }
                srows.add(source);
                HashMap<StatisticsHeaders, Node> sourceCells = getAllRowCells(source, currType);                
                StatisticsHeaders currHeader = StatisticsHeaders.INH_ATT_ATTEMPT;
                Node cell = sourceCells.get(StatisticsHeaders.ATT_ATTEMPTS);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
                currHeader = StatisticsHeaders.INH_ATT_SUCCESS;
                cell = sourceCells.get(StatisticsHeaders.ATT_SUCCESS);
                row.updateHeaderWithCall(currHeader, currHeader.getStatisticsData(cell, null), cell);
            }            
        }
        
        if (hasStatistics) {
            
            if(rootNode == null){
                rootNode = createRootStatisticsNode(datasetNode, StatisticsCallType.AGGREGATION_STATISTICS);
            }
            Node statisticsNode = getStatisticsNode(rootNode, period);
            for (Long time : rows.keySet()) {
                List<Node> currSourceRows = sourceRows.get(time);
                Node srow = createAggrStatisticsRow(statisticsNode, time, currSourceRows, period);
                
                Statistics currStatistics = rows.get(time);
                
                StatisticsHeaders utilHeader;
                Object succCount;
                List<Node> succCalls;
                
                StatisticsHeaders savedHeader;
                List<Node> sourceCells;
                Object fValue;
                
                if (hasIndividual) {
                    utilHeader = StatisticsHeaders.SC_SUCC_SETUP_COUNT;
                    succCount = currStatistics.get(utilHeader);
                    succCalls = currStatistics.getAllAffectedCalls(utilHeader);
                    
                    savedHeader = StatisticsHeaders.SC1;
                    utilHeader = StatisticsHeaders.SC_ATTEMPT_COUNT;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    sourceCells.addAll(succCalls);
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, succCount, fValue), srow, sourceCells);
                    savedHeader = StatisticsHeaders.SC2_ZW2_AVG;
                    utilHeader = StatisticsHeaders.SC_SETUP_TIME_TOTAL;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    sourceCells.addAll(succCalls);
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, succCount), srow, sourceCells);
                    savedHeader = StatisticsHeaders.SC2_ZW2_MIN;
                    utilHeader = StatisticsHeaders.SC_SETUP_TIME_MIN;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    savedHeader = StatisticsHeaders.SC2_ZW2_MAX;
                    utilHeader = StatisticsHeaders.SC_SETUP_TIME_MAX;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    savedHeader = StatisticsHeaders.SC3;
                    utilHeader = StatisticsHeaders.SC_CALL_DISC_TIME;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    sourceCells.addAll(succCalls);
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, succCount), srow, sourceCells);
                    savedHeader = StatisticsHeaders.SC4;
                    utilHeader = StatisticsHeaders.SC_AUDIO_QUAL_SUCC;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    sourceCells.addAll(succCalls);
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, succCount), srow, sourceCells);
                    savedHeader = StatisticsHeaders.SC4_ZW2_AVG;
                    utilHeader = StatisticsHeaders.SC_AUDIO_QUAL_TOTAL;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    utilHeader = StatisticsHeaders.SC_AUDIO_QUAL_COUNT;
                    sourceCells.addAll(currStatistics.getAllAffectedCalls(utilHeader));
                    createAggrStatisticsCell(period, savedHeader,
                            getStatValue(savedHeader, fValue, currStatistics.get(utilHeader)), srow, sourceCells);
                    savedHeader = StatisticsHeaders.SC4_ZW2_MIN;
                    utilHeader = StatisticsHeaders.SC_AUDIO_QUAL_MIN;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    savedHeader = StatisticsHeaders.SC4_ZW2_MAX;
                    utilHeader = StatisticsHeaders.SC_AUDIO_QUAL_MAX;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    /*savedHeader = StatisticsHeaders.SC5_ZW1_AVG;
                    utilHeader = StatisticsHeaders.SC_DELAY_TOTAL;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    utilHeader = StatisticsHeaders.SC_DELAY_COUNT;
                    sourceCells.addAll(currStatistics.getAllAffectedCalls(utilHeader));
                    createAggrStatisticsCell(period, savedHeader,
                            getStatValue(savedHeader, fValue, currStatistics.get(utilHeader)), srow, sourceCells);
                    savedHeader = StatisticsHeaders.SC5_ZW1_MIN;
                    utilHeader = StatisticsHeaders.SC_DELAY_MIN;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    savedHeader = StatisticsHeaders.SC5_ZW1_MAX;
                    utilHeader = StatisticsHeaders.SC_DELAY_MAX;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));*/
                }
                
                if (hasGroup) {
                    utilHeader = StatisticsHeaders.GC_SUCC_SETUP_COUNT;
                    succCount = currStatistics.get(utilHeader);
                    succCalls = currStatistics.getAllAffectedCalls(utilHeader);
                    
                    savedHeader = StatisticsHeaders.GC1;
                    utilHeader = StatisticsHeaders.GC_ATTEMPT_COUNT;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    sourceCells.addAll(succCalls);
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, succCount, fValue), srow, sourceCells);
                    savedHeader = StatisticsHeaders.GC2_ZW2_AVG;
                    utilHeader = StatisticsHeaders.GC_SETUP_TIME_TOTAL;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    sourceCells.addAll(succCalls);
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, succCount), srow, sourceCells);
                    savedHeader = StatisticsHeaders.GC2_ZW2_MIN;
                    utilHeader = StatisticsHeaders.GC_SETUP_TIME_MIN;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    savedHeader = StatisticsHeaders.GC2_ZW2_MAX;
                    utilHeader = StatisticsHeaders.GC_SETUP_TIME_MAX;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    savedHeader = StatisticsHeaders.GC3;
                    utilHeader = StatisticsHeaders.GC_CALL_DISC_TIME;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    sourceCells.addAll(succCalls);
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, succCount), srow, sourceCells);
                    savedHeader = StatisticsHeaders.GC4;
                    utilHeader = StatisticsHeaders.GC_AUDIO_QUAL_SUCC;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    sourceCells.addAll(succCalls);
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, succCount), srow, sourceCells);
                    savedHeader = StatisticsHeaders.GC4_ZW2_AVG;
                    utilHeader = StatisticsHeaders.GC_AUDIO_QUAL_TOTAL;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    utilHeader = StatisticsHeaders.GC_AUDIO_QUAL_COUNT;
                    sourceCells.addAll(currStatistics.getAllAffectedCalls(utilHeader));
                    createAggrStatisticsCell(period, savedHeader,
                            getStatValue(savedHeader, fValue, currStatistics.get(utilHeader)), srow, sourceCells);
                    savedHeader = StatisticsHeaders.GC4_ZW2_MIN;
                    utilHeader = StatisticsHeaders.GC_AUDIO_QUAL_MIN;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    savedHeader = StatisticsHeaders.GC4_ZW2_MAX;
                    utilHeader = StatisticsHeaders.GC_AUDIO_QUAL_MAX;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    /*savedHeader = StatisticsHeaders.GC5_ZW1_AVG;
                    utilHeader = StatisticsHeaders.GC_DELAY_TOTAL;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    utilHeader = StatisticsHeaders.GC_DELAY_COUNT;
                    sourceCells.addAll(currStatistics.getAllAffectedCalls(utilHeader));
                    createAggrStatisticsCell(period, savedHeader,
                            getStatValue(savedHeader, fValue, currStatistics.get(utilHeader)), srow, sourceCells);
                    savedHeader = StatisticsHeaders.GC5_ZW1_MIN;
                    utilHeader = StatisticsHeaders.GC_DELAY_MIN;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));
                    savedHeader = StatisticsHeaders.GC5_ZW1_MAX;
                    utilHeader = StatisticsHeaders.GC_DELAY_MAX;
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, currStatistics.get(utilHeader)), srow,
                            currStatistics.getAllAffectedCalls(utilHeader));*/
                }
                
                if (hasItsiCc) {
                    savedHeader = StatisticsHeaders.INH_CC;
                    utilHeader = StatisticsHeaders.INH_CC_SUCCESS;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    utilHeader = StatisticsHeaders.INH_CC_ATTEMPT;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, currStatistics.get(utilHeader)), srow, sourceCells);
                }
                
                if (hasTsm) {
                    savedHeader = StatisticsHeaders.TSM;
                    utilHeader = StatisticsHeaders.TSM_SUCCESS;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    utilHeader = StatisticsHeaders.TSM_ATTEMPT;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, currStatistics.get(utilHeader)), srow, sourceCells);
                }
                
                if (hasSds) {
                    savedHeader = StatisticsHeaders.SDS;
                    utilHeader = StatisticsHeaders.SDS_SUCCESS;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    utilHeader = StatisticsHeaders.SDS_ATTEMPT;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, currStatistics.get(utilHeader)), srow, sourceCells);
                }
                
                if (hasItsiAtt) {
                    savedHeader = StatisticsHeaders.INH_AT;
                    utilHeader = StatisticsHeaders.INH_ATT_SUCCESS;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    fValue = currStatistics.get(utilHeader);
                    utilHeader = StatisticsHeaders.INH_ATT_ATTEMPT;
                    sourceCells = new ArrayList<Node>(currStatistics.getAllAffectedCalls(utilHeader));
                    createAggrStatisticsCell(period, savedHeader, getStatValue(savedHeader, fValue, currStatistics.get(utilHeader)), srow, sourceCells);
                }
            }
        }
        return rootNode;
    }
    
    private Node getPeriodNode(final CallTimePeriods period, StatisticsCallType callType, HashMap<StatisticsCallType, Node> sourceStatistics) {
        Node rootNode = sourceStatistics.get(callType);
        if (rootNode != null) {
            Iterator<Node> iterator = rootNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return period.getId().equalsIgnoreCase(NeoUtils.getNodeName(currentPos.currentNode(),neoService));
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            return iterator.hasNext() ? iterator.next() : null;
        }
        return null;
    }
    
    private Object getStatValue(StatisticsHeaders header, Object... sources){
        Object firstObj = sources[0];
        if(firstObj==null){
            return null;
        }
        StatisticsType type = header.getType();
        Float first = ((Number)firstObj).floatValue();
        if(type.equals(StatisticsType.AVERAGE)||type.equals(StatisticsType.PERCENT)){
            Object secObj = sources[0];
            if(secObj==null){
                return null;
            }
            Float second = ((Number)secObj).floatValue();
            return (first/second)*(type.equals(StatisticsType.PERCENT)?100:1);
        }
        return first;
    }
    
    private HashMap<Long, Node> getAllPeriodRows(Node periodNode){
        HashMap<Long, Node> result = new HashMap<Long, Node>();
        Iterator<Node> rows = NeoUtils.getChildTraverser(periodNode).iterator();
        while (rows.hasNext()) {
            Node row = rows.next();
            Long time = (Long)row.getProperty(INeoConstants.PROPERTY_TIME_NAME, 0L);
            result.put(time, row);
        }
        return result;
    }
    
    private HashMap<StatisticsHeaders, Node> getAllRowCells(Node row, StatisticsCallType callType){
        HashMap<StatisticsHeaders, Node> result = new HashMap<StatisticsHeaders, Node>();
        Iterator<Node> cells = NeoUtils.getChildTraverser(row).iterator();
        while (cells.hasNext()) {
            Node cell = cells.next();
            StatisticsHeaders header = callType.getHeaderByTitle(NeoUtils.getNodeName(cell));
            result.put(header, cell);
        }
        return result;
    }
    
    private Node createAggrStatisticsRow(Node root,Long start,List<Node> sources, CallTimePeriods period){
        Node result = neoService.createNode();
        String name = NeoUtils.getFormatDateString(start, period.addPeriod(start), "HH:mm");
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_ROW.getId());
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
        result.setProperty(INeoConstants.PROPERTY_TIME_NAME, start);
        
        for(Node source : sources){
            result.createRelationshipTo(source, GeoNeoRelationshipTypes.SOURCE);
        }
        Node previousNode = previousSRowNodes.get(period);
        if (previousNode == null) {
            root.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
        }
        else {
            previousNode.createRelationshipTo(result, GeoNeoRelationshipTypes.NEXT);
        }
        previousSRowNodes.put(period, result);
        
        return result;
    }
    
    private void createAggrStatisticsCell(CallTimePeriods period, StatisticsHeaders header, Object value, Node row, List<Node> sources){
        Node result = neoService.createNode();
        
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_CELL.getId());
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, header.getTitle());
        if (value == null) {
            switch (header.getType()) {
            case COUNT:
                value = new Integer(0);
                break;
            default:
                value = new Float(0);
                break;
            }                        
        }
        else {
            switch (header.getType()) {
            case PERCENT:
                value = (Float)value*100f;
                break;
            default:
                //do nothing
                break;
            }
            for (Node source : sources) {
                result.createRelationshipTo(source, GeoNeoRelationshipTypes.SOURCE);            
            }
        }
        result.setProperty(INeoConstants.PROPERTY_VALUE_NAME, value);
        
        Node previousNode = previousSCellNodes.get(period);
        if (previousNode == null) {
            row.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
        }
        else {
            previousNode.createRelationshipTo(result, GeoNeoRelationshipTypes.NEXT);
        }
        previousSCellNodes.put(period, result);
        
    }
    
    private CallTimePeriods getHighestPeriod(long minTime, long maxTime) {
        long delta = CallTimePeriods.DAILY.getFirstTime(maxTime) - CallTimePeriods.DAILY.getFirstTime(minTime);
        if (delta >= DAY) {
            return CallTimePeriods.MONTHLY;
        }
        delta = CallTimePeriods.HOURLY.getFirstTime(maxTime) - CallTimePeriods.HOURLY.getFirstTime(minTime);
        if (delta >= HOUR) {
            return CallTimePeriods.DAILY;
        }
        
        return CallTimePeriods.HOURLY;
    }
    
    private Pair<Long, Long> getTimeBounds(Node dataset) {
        Transaction transaction = neoService.beginTx();
        try {
            Node gisNode = NeoUtils.findGisNodeByChild(dataset,neoService);
            return NeoUtils.getMinMaxTimeOfDataset(gisNode, neoService);
        } finally {
            transaction.finish();
        }
    }
    
    private Long getSCellTime(Node sCell, Node probeNode) {
        Node sRow = NeoUtils.getParent(neoService, sCell);
        
        if (sRow.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return NeoUtils.isProbeNode(currentPos.currentNode());
            }
        }, GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING).iterator().next().equals(probeNode)) {        
            return (Long)sRow.getProperty(INeoConstants.PROPERTY_TIME_NAME);
        }
        else {
            return null;
        }
    }
    
    private Statistics getStatisticsFromDatabase(Node statisticsNode, StatisticsCallType callType, final long minDate, final long maxDate, final Node probeNode) {
        Iterator<Node> statisticsNodes = statisticsNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                if (NeoUtils.getNodeType(currentPos.currentNode()).equals(NodeTypes.S_CELL.getId())) {
                    Long sCellTime = getSCellTime(currentPos.currentNode(), probeNode);
                    if ((sCellTime != null) && (sCellTime >= minDate) && (sCellTime < maxDate)) {
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
            Object value = sCell.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
            StatisticsHeaders header = callType.getHeaderByTitle(headerName);
            
            statistics.updateHeaderWithCall(header, value, sCell);
        }
        
        if (hasInDb) {
            return statistics;
        }
        else {
            return null;
        }
    }
    
    private Statistics createStatistics(Node parentNode, Node highStatisticsNode, Node highLevelSRow, Node probeNode, MultiPropertyIndex<Long> timeIndex, CallTimePeriods period, StatisticsCallType callType, long startDate, long endDate) {
        Statistics statistics = new Statistics();
        
        long currentStartDate = period.getFirstTime(startDate);
        long nextStartDate = getNextStartDate(period, endDate, currentStartDate);
        
        if (startDate > currentStartDate) {
            currentStartDate = startDate;
        }
        
        Node statisticsNode = getStatisticsNode(parentNode, period);
        if (highStatisticsNode != null) {
            highStatisticsNode.createRelationshipTo(statisticsNode, GeoNeoRelationshipTypes.SOURCE);
        }
        
        do {
            Node sRow = createSRowNode(statisticsNode, new Date(period.getFirstTime(currentStartDate)), probeNode, highLevelSRow, period);
            
            Statistics periodStatitics = new Statistics();
            if (period == CallTimePeriods.HOURLY) {
                periodStatitics = getStatisticsByHour(timeIndex, callType, currentStartDate, nextStartDate);
            }
            else {
                periodStatitics = getStatisticsFromDatabase(statisticsNode, callType, currentStartDate, nextStartDate, probeNode);
                if (periodStatitics == null) {
                    periodStatitics = createStatistics(parentNode, statisticsNode, sRow, probeNode, timeIndex, period.getUnderlyingPeriod(), callType, currentStartDate, nextStartDate);
                }
            }
            
            for (StatisticsHeaders header : callType.getHeaders()) {                
                createSCellNode(sRow, periodStatitics, header, period);
            }           
            previousSCellNodes.put(period, null);
            
            
            updateStatistics(statistics, periodStatitics);            
            
            currentStartDate = nextStartDate;
            nextStartDate = getNextStartDate(period, endDate, currentStartDate);
        }
        while (currentStartDate < endDate);
        
        return statistics;
    }

    private long getNextStartDate(CallTimePeriods period, long endDate, long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if(!period.equals(CallTimePeriods.HOURLY)&&(nextStartDate > endDate)){
            nextStartDate = endDate;
        }
        return nextStartDate;
    }
    
    private Node getStatisticsNode(Node parent, final CallTimePeriods period) {
        Iterator<Node> nodes = parent.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return period.getId().equals(NeoUtils.getNodeName(currentPos.currentNode(),neoService));
            }
        }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
        
        if (nodes.hasNext()) {
            return nodes.next();
        }
        else {        
            Node result = neoService.createNode();
        
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, period.getId());
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.CALL_ANALYZIS.getId());
            parent.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
            
            return result;
        }
    }
    
    private Node createSRowNode(Node parent, Date startDate, Node probeNode, Node highLevelSRow, CallTimePeriods period) {
        Node result = neoService.createNode();
        String name = NeoUtils.getFormatDateString(startDate.getTime(), period.addPeriod(startDate.getTime()), "HH:mm");
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_ROW.getId());
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
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
        
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_CELL.getId());
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, header.getTitle());
        Object value = statistics.get(header);
        if (value == null) {
            switch (header.getType()) {
            case COUNT:
                value = new Integer(0);
                break;
            default:
                value = new Float(0);
                break;
            }                        
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
    
    private Statistics getStatisticsByHour(MultiPropertyIndex<Long> timeIndex, StatisticsCallType callType, long startTime, long endTime) throws IllegalArgumentException {
        Statistics statistics = new Statistics();
        
        Collection<Node> callNodes = timeIndex.find(new Long[] {startTime}, new Long[] {endTime});
        for (Node singleNode : callNodes) {
            if (singleNode.getProperty(CallProperties.CALL_TYPE.getId()).equals(callType.toString())) {
                updateCallStatistics(singleNode, statistics, callType);
            }
        }
        
        return statistics;
    }
    
    private void updateCallStatistics(Node callNode, Statistics statistics, StatisticsCallType callType) {
        ICallStatisticsConstants constants = statisticsConstants.get(callType);
        for(StatisticsHeaders header : callType.getHeaders()){
            statistics.updateHeaderWithCall(header, header.getStatisticsData(callNode, constants), callNode);
        }
    }

    /**
     * @return Returns the statisticNode.
     */
    public HashMap<StatisticsCallType, Node> getStatisticNode() {
        return statisticNode;
    }

    /**
     * @return Returns the highPeriod.
     */
    public CallTimePeriods getHighPeriod() {
        return highPeriod;
    }

    /**
     * @param periods
     */
    public Node getPeriodNode(final CallTimePeriods periods, StatisticsCallType callType) {
        Node rootNode = statisticNode.get(callType);
        if (rootNode != null) {
            Iterator<Node> iterator = rootNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return periods.getId().equalsIgnoreCase(NeoUtils.getNodeName(currentPos.currentNode(),neoService));
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            return iterator.hasNext() ? iterator.next() : null;
        }
        return null;
    }

}
