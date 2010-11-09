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

package org.amanzi.neo.core.database.entity;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Probe wrapper
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class Probe extends Base {

    /** The root. */
    ProbeNetwork root;

    /** The is loaded root. */
    private boolean isLoadedRoot;

    /**
     * Sets the root.
     * 
     * @param root the new root
     */
    public void setRoot(ProbeNetwork root) {
        assert isLoadedRoot == false || root == null;
        this.root = root;
        isLoadedRoot = true;
    }

    /**
     * Instantiates a new probe.
     */
    public Probe() {
        super();
        isLoadedRoot = false;
        root = null;
    }

    /**
     * Instantiates a new probe.
     * 
     * @param node the node
     * @param service the service
     */
    public Probe(Node node, NeoDataService service) {
        super(node, service);
        isLoadedRoot = false;
        root = null;
    }

    /**
     * Load root.
     * 
     * @param service the service
     */
    void loadRoot(NeoDataService service) {
        assert isLoadedRoot == false;
        Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
        if (relation != null) {
            Node dataNode = relation.getOtherNode(node);
            ProbeNetwork root = (ProbeNetwork)service.getInstance(dataNode);
            setRoot(root);
        }
        isLoadedRoot = true;
    }

    @Override
    void save() {
        super.save();
        if (isLoadedRoot) {
            Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
            if (root == null) {
                if (relation != null) {
                    relation.delete();
                }
            } else if (root.node != null) {// links only if dataroot stored in database
                if (relation != null) {
                    Node dataNode = relation.getOtherNode(node);
                    if (!dataNode.equals(root.node)) {
                        relation.delete();
                        relation = null;
                    }
                }
                if (relation == null) {
                    root.node.createRelationshipTo(node, GeoNeoRelationshipTypes.CHILD);
                }
            }
        }
    }

    @Override
    void create(NeoDataService service) {
        super.create(service);
        setPropertyValue(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.PROBE.getId());
    }

}
