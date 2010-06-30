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

package org.amanzi.awe.afp;

import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Neighbour sub time;
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public enum AfpNeighbourSubType {
    EXCEPTION("excep");
    public static final String PROPERTY_NAME = "afp_type";
    private final String id;

    /**
     * constructor
     * 
     * @param id node type ID
     * @param nonEditableProperties list of not editable properties
     */
    private AfpNeighbourSubType(String id) {
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns NetworkTypes by its ID
     * 
     * @param enumId id of Node Type
     * @return NodeTypes or null
     */
    public static AfpNeighbourSubType getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (AfpNeighbourSubType call : AfpNeighbourSubType.values()) {
            if (call.getId().equals(enumId)) {
                return call;
            }
        }
        return null;
    }

    /**
     * returns type of node
     * 
     * @param container PropertyContainer
     * @param service NeoService
     * @return type of node
     */
    public static AfpNeighbourSubType getNodeType(PropertyContainer networkGis, GraphDatabaseService service) {
        Transaction tx = service == null ? null : service.beginTx();
        try {
            return getEnumById((String)networkGis.getProperty(PROPERTY_NAME, null));
        } finally {
            if (service != null) {
                tx.finish();
            }
        }
    }

    /**
     * Set network type to current node
     * 
     * @param node - node
     * @param service NeoService - neo service, if null then transaction do not created
     */
    public void setTypeToNode(Node node, GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            node.setProperty(PROPERTY_NAME, getId());
            NeoUtils.successTx(tx);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * Check type.
     * 
     * @param node the node
     * @param service the service
     * @return true, if successful
     */
    public boolean checkType(Node node, GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            return node == null ? false : getId().equals(node.getProperty(PROPERTY_NAME, ""));
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

}
