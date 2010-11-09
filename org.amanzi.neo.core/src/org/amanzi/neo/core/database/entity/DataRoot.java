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

import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * DataRoot entity
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class DataRoot extends Base {

    /** The is loaded gis. */
    private boolean isLoadedGis;

    /** The gis. */
    Gis gis;

    /**
     * Instantiates a new data root.
     */
    DataRoot() {
        super();
        gis = null;
        isLoadedGis = false;
    }

    /**
     * Instantiates a new data root.
     * 
     * @param node the node
     * @param service the service
     */
    public DataRoot(Node node, NeoDataService service) {
        super(node, service);
        gis = null;
        isLoadedGis = false;
    }

    /**
     * Gets the gis.
     * 
     * @return Returns the gis.
     */
    public Gis getGis() {
        assert isLoadedGis == true;
        return gis;
    }

    /**
     * Sets the gis.
     * 
     * @param gis The gis to set.
     */
    public void setGis(Gis gis) {
        assert isLoadedGis == false || gis == null;
        isLoadedGis = true;
        this.gis = gis;
    }

    /**
     * Load gis.
     * 
     * @param service the service
     */
    void loadGis(NeoDataService service) {
        assert isLoadedGis == false;
        Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING);
        if (relation != null) {
            Node dataNode = relation.getOtherNode(node);
            gis = (Gis)service.getInstance(dataNode);
            gis.setDataroot(this);
        }
        isLoadedGis = true;
    }

    /**
     * Save.
     */
    @Override
    void save() {
        super.save();
        if (isLoadedGis) {
            Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING);
            if (gis == null) {
                if (relation != null) {
                    relation.delete();
                }
            } else if (gis.node != null) {// links only if dataroot stored in database
                if (relation != null) {
                    Node dataNode = relation.getOtherNode(node);
                    if (!dataNode.equals(gis.node)) {
                        relation.delete();
                        relation = null;
                    }
                }
                if (relation == null) {
                    gis.node.createRelationshipTo(node, GeoNeoRelationshipTypes.NEXT);
                }
            }
        }
    }
}
