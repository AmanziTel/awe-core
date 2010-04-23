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

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.GpehReportUtil.MatrixProperties;
import org.amanzi.neo.core.utils.GpehReportUtil.ReportsRelations;
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
 * </p>.
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
        crs=NeoUtils.getCRS(NeoUtils.findGisNodeByChild(network, service),service);
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
            findIntraFrequencyICDM();
        } finally {
            tx.finish();
        }
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
     * The Class AbstractICDM.
     */
    private abstract class AbstractICDM {
        
        /** The main node. */
        protected final Node mainNode;

        /**
         * Instantiates a new abstract icdm.
         *
         * @param mainNode the main node
         */
        AbstractICDM(Node mainNode) {
            this.mainNode = mainNode;
        }

        /**
         * Gets the main node.
         *
         * @return the main node
         */
        public Node getMainNode() {
            return root;
        }
    }

    /**
     * The Class IntraFrequencyICDM.
     */
    public class IntraFrequencyICDM extends AbstractICDM {

        private final String idName;


        /**
         * Instantiates a new intrafrequency matrix.
         *
         * @param mainNode the main node
         */
        IntraFrequencyICDM(Node mainNode) {
            super(mainNode);
            idName=GpehReportUtil.getMatrixLuceneIndexName(getNetworkName(), getGpehEventsName(), GpehReportUtil.MR_TYPE_INTRAF);
        }


        /**
         * Gets the row traverser.
         *
         * @return the row traverser
         */
        public Traverser getRowTraverser() {
            return getMainNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING);
        }
        /**
         * Gets the best cell name.
         *
         * @param row the row
         * @return the best cell name
         */
        public String getBestCellName(Node row) {
            String result=NeoUtils.getNodeName(getBestCell(row));
            return result;
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
            return (String)getBestCell(row).getProperty(GpehReportUtil.PRIMARY_SCR_CODE,"");
        }
        
        /**
         * Gets the interfering cell psc.
         *
         * @param row the row
         * @return the interfering cell psc
         */
        public String getInterferingCellPSC(Node row) {
            return (String)getInterferingCell(row).getProperty(GpehReportUtil.PRIMARY_SCR_CODE,"");
        }

        /**
         * Gets the interfering cell name.
         *
         * @param row the row
         * @return the interfering cell name
         */
        public String getInterferingCellName(Node row) {
            String result=NeoUtils.getNodeName(getInterferingCell(row));
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
         * Gets the num mr for best cell.
         *
         * @param row the row
         * @return the num mr for best cell
         */
        public Integer getNumMRForBestCell(Node row) {
            //TODO add cache if necessary - it will be not large
            int count=0;
            Iterable<Relationship> rel = getBestCell(row).getRelationships(ReportsRelations.BEST_CELL,Direction.INCOMING);
            for (Relationship relationship : rel) {
                if (relationship.getProperty(GpehReportUtil.REPORTS_ID,"").equals(idName)){
                    count++;
                }
            }
            return count;
        }



        /**
         * Gets the num mr for interfering cell.
         *
         * @param row the row
         * @return the num mr for interfering cell
         */
        public Integer getNumMRForInterferingCell(Node row) {
            //TODO add cache if necessary - it will be not large
            int count=0;
            Iterable<Relationship> rel = getInterferingCell(row).getRelationships(ReportsRelations.SECOND_SELL,Direction.INCOMING);
            for (Relationship relationship : rel) {
                if (relationship.getProperty(GpehReportUtil.REPORTS_ID,"").equals(idName)){
                    count++;
                }
            }
            return count;
        }



        /**
         * Gets the deltaecno.
         *
         * @param i the number
         * @param row the row
         * @return the delta ecno
         */
        public Integer getDeltaEcNo(int i, Node row) {
            assert i>=1&&i<=5&&row!=null;
            return (Integer)row.getProperty(MatrixProperties.EC_NO_DELTA_PREFIX+i,0);
        }



        public Integer getDeltaRSCP(int i, Node row) {
            assert i>=1&&i<=5&&row!=null;
            return (Integer)row.getProperty(MatrixProperties.RSCP_DELTA_PREFIX+i,0);
        }
        public Integer getPosition(int i, Node row) {
            assert i>=1&&i<=5&&row!=null;
            return (Integer)row.getProperty(MatrixProperties.POSITION_PREFIX+i,0);
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




}
