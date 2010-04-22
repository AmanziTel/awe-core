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

import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.GpehReportUtil.ReportsRelations;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * Gpeh report model
 * </p>
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

        /**
         * Instantiates a new intrafrequency matrix.
         *
         * @param mainNode the main node
         */
        IntraFrequencyICDM(Node mainNode) {
            super(mainNode);
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
