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

package org.amanzi.awe.neighbours.gpeh;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.statistic.StatisticByPeriodStructure;
import org.amanzi.awe.statistic.StatisticNeoService;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.GpehReportUtil.CellReportsProperties;
import org.amanzi.neo.core.utils.GpehReportUtil.MatrixProperties;
import org.amanzi.neo.core.utils.GpehReportUtil.ReportsRelations;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Gpeh report model
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class GpehReportModel {

    /** The network name. */
    private final String networkName;

    /** The gpeh events name. */
    private final String gpehEventsName;

    /** The service. */
    private final GraphDatabaseService service;

    /** The intra frequency icdm. */
    private IntraFrequencyICDM intraFrequencyICDM;
    
    /** The inter frequency icdm. */
    private InterFrequencyICDM interFrequencyICDM;
    
    /** The cell rscp analisis. */
    private final Map<CallTimePeriods, CellRscpAnalisis> cellRscpAnalisis = new HashMap<CallTimePeriods, CellRscpAnalisis>();
    private final Map<CallTimePeriods, CellEcNoAnalisis> cellEcNoAnalisis = new HashMap<CallTimePeriods, CellEcNoAnalisis>();

    /** The network. */
    private final Node network;

    /** The gpeh. */
    private final Node gpeh;

    /** The root. */
    private Node root;

    /** The crs. */
    private final CoordinateReferenceSystem crs;

    /**
     * Instantiates a new gpeh report model.
     * 
     * @param network the network
     * @param gpeh the gpeh
     * @param service the service
     */
    GpehReportModel(Node network, Node gpeh, GraphDatabaseService service) {
        this.network = network;
        this.gpeh = gpeh;

        networkName = NeoUtils.getNodeName(network, service);
        crs = NeoUtils.getCRS(NeoUtils.findGisNodeByChild(network, service), service);
        gpehEventsName = NeoUtils.getNodeName(gpeh, service);
        this.service = service;
        intraFrequencyICDM = null;
        init();
    }

    /**
     * Inits the.
     */
    private void init() {
        Transaction tx = service.beginTx();
        try {
            findRootNode();
            findMatrixNodes();
            findCellAnalysis();
        } finally {
            tx.finish();
        }
    }


    /**
     * Find cell analysis.
     */
    public void findCellAnalysis() {
        for (CallTimePeriods period:new CallTimePeriods[]{CallTimePeriods.HOURLY,CallTimePeriods.DAILY,CallTimePeriods.ALL}){
            findCellRscpAnalisis(period);
        }
    }

    /**
     * Find matrix nodes.
     */
    public void findMatrixNodes() {
        findIntraFrequencyICDM();
        findInterFrequencyICDM();
    }

    /**
     * Find inter frequency icdm.
     *
     * @return the inter frequency icdm
     */
    public InterFrequencyICDM findInterFrequencyICDM() {
        if (getRoot() == null) {
            return null;
        }
        if (interFrequencyICDM != null) {
            return interFrequencyICDM;
        }
        Relationship rel = getRoot().getSingleRelationship(ReportsRelations.ICDM_INTER_FR, Direction.OUTGOING);
        interFrequencyICDM = rel == null ? null : new InterFrequencyICDM(rel.getOtherNode(getRoot()));
        return interFrequencyICDM;
    }

    /**
     * Find root node.
     * 
     * @return the node
     */
    public Node findRootNode() {
        if (root == null) {
            for (Relationship relation : network.getRelationships(ReportsRelations.REPORTS)) {
                Node reportNode = relation.getOtherNode(network);
                if (!NeoUtils.getRelations(gpeh, reportNode, ReportsRelations.REPORTS, service).isEmpty()) {
                    root = reportNode;
                    break;
                }
            }
        }
        return root;
    }

    /**
     * Find intra frequency icdm.
     * 
     * @return the intra frequency icdm
     */
    public IntraFrequencyICDM findIntraFrequencyICDM() {
        if (getRoot() == null) {
            return null;
        }
        if (intraFrequencyICDM != null) {
            return intraFrequencyICDM;
        }
        Relationship rel = getRoot().getSingleRelationship(ReportsRelations.ICDM_INTRA_FR, Direction.OUTGOING);
        intraFrequencyICDM = rel == null ? null : new IntraFrequencyICDM(rel.getOtherNode(getRoot()));
        return intraFrequencyICDM;
    }
    public CellEcNoAnalisis findCellEcNoAnalisis(final CallTimePeriods periods){
        if (getRoot() == null) {
            return null;
        }
        CellEcNoAnalisis analys = cellEcNoAnalisis.get(periods);
        if (analys != null) {
            return analys;
        }

        Relationship rel = getRoot().getSingleRelationship(periods.getPeriodRelation(CellEcNoAnalisis.ECNO_PRFIX), Direction.OUTGOING);
        if (rel!=null){
            Node mainNode=rel.getOtherNode(getRoot());
            analys = new CellEcNoAnalisis(mainNode, periods);
            cellEcNoAnalisis.put(periods, analys);
            return analys;
        }
        return null;   
    }
    /**
     * Find cell rscp analisis.
     *
     * @param periods the periods
     * @return the cell rscp analisis
     */
    public CellRscpAnalisis findCellRscpAnalisis(final CallTimePeriods periods) {
        if (getRoot() == null) {
            return null;
        }
        CellRscpAnalisis analys = cellRscpAnalisis.get(periods);
        if (analys != null) {
            return analys;
        }

        Relationship rel = getRoot().getSingleRelationship(periods.getPeriodRelation(CellRscpAnalisis.RSCP_PRFIX), Direction.OUTGOING);
        if (rel!=null){
            Node mainNode=rel.getOtherNode(getRoot());
            analys = new CellRscpAnalisis(mainNode, periods);
            cellRscpAnalisis.put(periods, analys);
            return analys;
        }
        return null;
//        Iterator<Node> iter = getRoot().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//
//            @Override
//            public boolean isReturnableNode(TraversalPosition currentPos) {
//                return currentPos.notStartNode()&&currentPos.currentNode().getProperty(CellReportsProperties.PERIOD_ID,"").equals(periods.getId());
//            }
//        }, ReportsRelations.CELL_RSCP_ANALYSYS, Direction.OUTGOING).iterator();
//        
//        Node mainNode = iter.hasNext() ? iter.next() : null;
//        if (mainNode != null) {
//            analys = new CellRscpAnalisis(mainNode, periods);
//            cellRscpAnalisis.put(periods, analys);
//            return analys;
//        } else {
//            return null;
//        }
    }

    /**
     * Gets the root.
     * 
     * @return the root
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Gets the intra frequency icdm.
     * 
     * @return the intra frequency icdm
     */
    public IntraFrequencyICDM getIntraFrequencyICDM() {
        return intraFrequencyICDM;
    }

    /**
     * Gets the inter frequency icdm.
     * 
     * @return the inter frequency icdm
     */
    public InterFrequencyICDM getInterFrequencyICDM() {
        return interFrequencyICDM;
    }

    /**
     * Gets the cell rscp analisis.
     *
     * @param periods the periods
     * @return the cell rscp analisis
     */
    public CellRscpAnalisis getCellRscpAnalisis(CallTimePeriods periods) {
        return cellRscpAnalisis.get(periods);
    }

    /**
     * The Class AbstractICDM.
     */
    private abstract class AbstractICDM {
        /** The id name. */
        protected final String idName;
        /** The main node. */
        protected final Node mainNode;
        
        /** The best cell num cashe. */
        protected final Map<Node, Integer> bestCellNumCashe = new HashMap<Node, Integer>();

        /**
         * Instantiates a new abstract icdm.
         *
         * @param mainNode the main node
         * @param idName the id name
         */
        AbstractICDM(Node mainNode, String idName) {
            this.mainNode = mainNode;
            this.idName = idName;
            bestCellNumCashe.clear();
        }

        /**
         * Gets the num mr for best cell.
         * 
         * @param row the row
         * @return the num mr for best cell
         */
        public Integer getNumMRForBestCell(Node row) {

            // using cache //TODO store cache values like properties in node - it more faster
            int count = 0;
            Node bestCell = getBestCell(row);
            Integer result = bestCellNumCashe.get(bestCell);
            if (result != null) {
                return result;
            }
            Iterable<Relationship> rel = bestCell.getRelationships(ReportsRelations.BEST_CELL, Direction.INCOMING);
            for (Relationship relationship : rel) {
                if (relationship.getProperty(GpehReportUtil.REPORTS_ID, "").equals(idName)) {
                    Node tableNode = relationship.getOtherNode(bestCell);
                    for (Relationship rel2 : tableNode.getRelationships(ReportsRelations.SOURCE_MATRIX_EVENT, Direction.OUTGOING)) {
                        count++;
                    }
                }
            }
            bestCellNumCashe.put(bestCell, count);
            return count;
        }

        /**
         * Gets the num mr for interfering cell.
         * 
         * @param row the row
         * @return the num mr for interfering cell
         */
        public Integer getNumMRForInterferingCell(Node row) {
            // ODO store cache values like properties in node - it more faster
            int count = 0;
            for (Relationship rel2 : row.getRelationships(ReportsRelations.SOURCE_MATRIX_EVENT, Direction.OUTGOING)) {
                count++;
            }
            // Iterable<Relationship> rel =
            // getInterferingCell(row).getRelationships(ReportsRelations.SECOND_SELL,
            // Direction.INCOMING);
            // for (Relationship relationship : rel) {
            // if (relationship.getProperty(GpehReportUtil.REPORTS_ID, "").equals(idName)) {
            // count++;
            // }
            // }
            return count;
        }

        /**
         * Gets the distance.
         * 
         * @param tblRow the tbl row
         * @return the distance
         */
        public Double getDistance(Node tblRow) {
            return (Double)tblRow.getProperty(MatrixProperties.DISTANCE, null);
        }

        /**
         * Gets the best cell.
         * 
         * @param row the row
         * @return the best cell
         */
        public Node getBestCell(Node row) {
            return row.getSingleRelationship(ReportsRelations.BEST_CELL, Direction.OUTGOING).getOtherNode(row);
        }

        /**
         * Gets the best cell psc.
         * 
         * @param row the row
         * @return the best cell psc
         */
        public String getBestCellPSC(Node row) {
            return (String)getBestCell(row).getProperty(GpehReportUtil.PRIMARY_SCR_CODE, "");
        }

        /**
         * Gets the main node.
         * 
         * @return the main node
         */
        public Node getMainNode() {
            return mainNode;
        }

        /**
         * Gets the row traverser.
         * 
         * @return the row traverser
         */
        public Traverser getRowTraverser() {
            return getMainNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD,
                    Direction.OUTGOING);
        }

        /**
         * Gets the best cell name.
         * 
         * @param row the row
         * @return the best cell name
         */
        public String getBestCellName(Node row) {
            Node bestCell = getBestCell(row);
            String result = (String)bestCell.getProperty("userLabel", "");
            if (StringUtil.isEmpty(result)) {
                result = NeoUtils.getNodeName(bestCell);
            }
            return result;
        }

        /**
         * Gets the interfering cell psc.
         * 
         * @param row the row
         * @return the interfering cell psc
         */
        public String getInterferingCellPSC(Node row) {
            return (String)getInterferingCell(row).getProperty(GpehReportUtil.PRIMARY_SCR_CODE, "");
        }

        /**
         * Gets the interfering cell name.
         * 
         * @param row the row
         * @return the interfering cell name
         */
        public String getInterferingCellName(Node row) {
            Node cell = getInterferingCell(row);
            String result = (String)cell.getProperty("userLabel", "");
            if (StringUtil.isEmpty(result)) {
                result = NeoUtils.getNodeName(cell);
            }
            return result;
        }

        /**
         * Gets the interfering cell.
         * 
         * @param row the row
         * @return the interfering cell
         */
        public Node getInterferingCell(Node row) {
            return row.getSingleRelationship(ReportsRelations.SECOND_SELL, Direction.OUTGOING).getOtherNode(row);
        }

        /**
         * Checks if is defined nbr.
         * 
         * @param tblRow the tbl row
         * @return the boolean
         */
        public Boolean isDefinedNbr(Node tblRow) {
            return (Boolean)tblRow.getProperty(MatrixProperties.DEFINED_NBR, false);
        }
    }

    /**
     * The Class InterFrequencyICDM.
     */
    public class InterFrequencyICDM extends AbstractICDM {

        /**
         * Instantiates a new inter frequency icdm.
         *
         * @param mainNode the main node
         */
        InterFrequencyICDM(Node mainNode) {
            super(mainNode, GpehReportUtil.getMatrixLuceneIndexName(getNetworkName(), getGpehEventsName(), GpehReportUtil.MR_TYPE_INTERF));
        }

        /**
         * Gets the ec no.
         *
         * @param prefix the prefix
         * @param tblRow the tbl row
         * @return the ec no
         */
        public Integer getEcNo(int prefix, Node tblRow) {
            return (Integer)tblRow.getProperty(MatrixProperties.EC_NO_PREFIX + prefix, 0);
        }

        /**
         * Gets the rSCP.
         *
         * @param prfx1 the prfx1
         * @param prfx2 the prfx2
         * @param tblRow the tbl row
         * @return the rSCP
         */
        public Integer getRSCP(int prfx1, int prfx2, Node tblRow) {
            return (Integer)tblRow.getProperty(MatrixProperties.getRSCPECNOPropertyName(prfx1, prfx2), 0);
        }

        /**
         * Gets the best cell uarfcn.
         *
         * @param tblRow the tbl row
         * @return the best cell uarfcn
         */
        public String getBestCellUARFCN(Node tblRow) {
            return getBestCell(tblRow).getProperty("uarfcnDl", "").toString();
        }

        /**
         * Gets the interfering cell uarfcn.
         *
         * @param tblRow the tbl row
         * @return the interfering cell uarfcn
         */
        public String getInterferingCellUARFCN(Node tblRow) {
            return getInterferingCell(tblRow).getProperty("uarfcnDl", "").toString();
        }

    }

    /**
     * The Class IntraFrequencyICDM.
     */
    public class IntraFrequencyICDM extends AbstractICDM {

        /**
         * Instantiates a new intrafrequency matrix.
         * 
         * @param mainNode the main node
         */
        IntraFrequencyICDM(Node mainNode) {
            super(mainNode, GpehReportUtil.getMatrixLuceneIndexName(getNetworkName(), getGpehEventsName(), GpehReportUtil.MR_TYPE_INTRAF));
        }

        /**
         * Gets the deltaecno.
         * 
         * @param i the number
         * @param row the row
         * @return the delta ecno
         */
        public Integer getDeltaEcNo(int i, Node row) {
            assert i >= 1 && i <= 5 && row != null;
            return (Integer)row.getProperty(MatrixProperties.EC_NO_DELTA_PREFIX + i, 0);
        }

        /**
         * Gets the delta rscp.
         * 
         * @param i the i
         * @param row the row
         * @return the delta rscp
         */
        public Integer getDeltaRSCP(int i, Node row) {
            assert i >= 1 && i <= 5 && row != null;
            return (Integer)row.getProperty(MatrixProperties.RSCP_DELTA_PREFIX + i, 0);
        }

        /**
         * Gets the position.
         * 
         * @param i the i
         * @param row the row
         * @return the position
         */
        public Integer getPosition(int i, Node row) {
            assert i >= 1 && i <= 5 && row != null;
            return (Integer)row.getProperty(MatrixProperties.POSITION_PREFIX + i, 0);
        }

    }

    /**
     * Gets the network.
     * 
     * @return the network
     */
    public Node getNetwork() {
        return network;
    }

    /**
     * Gets the gpeh.
     * 
     * @return the gpeh
     */
    public Node getGpeh() {
        return gpeh;
    }

    /**
     * Gets the network name.
     * 
     * @return the network name
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * Gets the gpeh events name.
     * 
     * @return the gpeh events name
     */
    public String getGpehEventsName() {
        return gpehEventsName;
    }

    /**
     * Gets the crs.
     * 
     * @return the crs
     */
    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    /**
     * The Class AnalysisByPeriods.
     */
    private abstract class AnalysisByPeriods {

        /** The period. */
        protected final CallTimePeriods period;
        
        /** The use cache. */
        protected  boolean useCache;
        
        /** The cache. */
        protected  HashMap<String,StatisticByPeriodStructure> cache=new HashMap<String, StatisticByPeriodStructure>();
        
        /** The main node. */
        protected final Node mainNode;
        
        /** The id name. */
        protected String idName;
        
        /** The prefix. */
        protected final String prefix;

        /**
         * Instantiates a new analysis by periods.
         *
         * @param mainNode the main node
         * @param period the period
         * @param prefix the prefix
         */
        public AnalysisByPeriods(Node mainNode, CallTimePeriods period,String prefix) {
            this.period = period;
            this.mainNode = mainNode;
            this.prefix = prefix;
            useCache=false;
        }
        
        /**
         * Gets the statistic structure.
         *
         * @param structureId the structure id
         * @return the statistic structure
         */
        public StatisticByPeriodStructure getStatisticStructure(String structureId){
            if (useCache){
                StatisticByPeriodStructure result = cache.get(structureId);
                if (result!=null){
                    return result;
                }
                
            }
           Node rootNode = StatisticNeoService.findRootNode(mainNode, structureId);
           if (rootNode!=null){
               StatisticByPeriodStructure result = new StatisticByPeriodStructure(rootNode, service);
               if (isUseCache()){
                   result.setUseCache(true);
                   cache.put(structureId, result);
               }
               return result; 
           }else{
               return null;
           }
        }
        /**
         * Gets the period.
         *
         * @return the period
         */
        public CallTimePeriods getPeriod() {
            return period;
        }

        /**
         * Gets the main node.
         *
         * @return the main node
         */
        public Node getMainNode() {
            return mainNode;
        }

        /**
         * Checks if is use cache.
         *
         * @return true, if is use cache
         */
        public boolean isUseCache() {
            return useCache;
        }

        /**
         * Sets the use cache.
         *
         * @param useCache the new use cache
         */
        public void setUseCache(boolean useCache) {
            this.useCache = useCache;
            if (!this.useCache){
                cache.clear();
            }
        }

    }

    /**
     * The Class CellRscpAnalisis.
     */
    public class CellRscpAnalisis extends AnalysisByPeriods {
        
        /** The Constant RSCP_PRFIX. */
        public static final String RSCP_PRFIX = "RSCP";
        
        /**
         * Instantiates a new cell rscp analisis.
         *
         * @param mainNode the main node
         * @param period the period
         */
        public CellRscpAnalisis(Node mainNode, CallTimePeriods period) {
            super(mainNode, period,RSCP_PRFIX);
        }
        
        
        /**
         * Gets the rscp property.
         *
         * @param node the node
         * @return the rscp property
         */
        public int[] getRscpProperty(Node node) {
            int[] result = (int[])node.getProperty(CellReportsProperties.RNSP_ARRAY,null);
            return result==null?new int[92]:result;
        }
        
    }
    public class CellEcNoAnalisis extends AnalysisByPeriods {

        /** The Constant RSCP_PRFIX. */
        public static final String ECNO_PRFIX = "ECNO";

        /**
         * Instantiates a new cell rscp analisis.
         *
         * @param mainNode the main node
         * @param period the period
         */
        public CellEcNoAnalisis(Node mainNode, CallTimePeriods period) {
            super(mainNode, period,ECNO_PRFIX);
        }


        /**
         * Gets the rscp property.
         *
         * @param node the node
         * @return the rscp property
         */
        public int[] getEcNoProperty(Node node) {
            int[] result = (int[])node.getProperty(CellReportsProperties.ECNO_ARRAY,null);
            return result==null?new int[50]:result;
        }

    }

}
