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
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class GpehReportModel {
    private final String networkName;
    private final String gpehEventsName;
    private final GraphDatabaseService service;
    private IntraFrequencyICDM intraFrequencyICDM;
    private IProgressMonitor monitor;
    private final Node network;
    private final Node gpeh;
    private Node root;

    GpehReportModel(Node network, Node gpeh, GraphDatabaseService service) {
        this.network = network;
        this.gpeh = gpeh;
        networkName = NeoUtils.getNodeName(network, service);
        gpehEventsName = NeoUtils.getNodeName(gpeh, service);
        this.service = service;
        intraFrequencyICDM = null;
        init();
    }

    private void init() {
        Transaction tx = service.beginTx();
        try {
            findRootNode();
            findIntraFrequencyICDM();
        } finally {
            tx.finish();
        }
    }

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

    public Node getRoot() {
        return root;
    }

    public IntraFrequencyICDM getIntraFrequencyICDM() {
        return intraFrequencyICDM;
    }

    private abstract class AbstractICDM {
        protected final Node mainNode;

        AbstractICDM(Node mainNode) {
            this.mainNode = mainNode;
        }

        public Node getMainNode() {
            return root;
        }
    }

    public class IntraFrequencyICDM extends AbstractICDM {

        /**
         * @param mainNode
         */
        IntraFrequencyICDM(Node mainNode) {
            super(mainNode);
        }

    }

    public Node getNetwork() {
        return network;
    }

    public Node getGpeh() {
        return gpeh;
    }

    public String getNetworkName() {
        return networkName;
    }

    public String getGpehEventsName() {
        return gpehEventsName;
    }

}
