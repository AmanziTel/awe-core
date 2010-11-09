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
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Gis entity
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class Gis extends Base {

    /** The gis_type. */
    String gis_type;

    /** The dataroot. */
    DataRoot dataroot;

    /** The is loaded dataroot. */
    private boolean isLoadedDataroot;

    /**
     * Instantiates a new gis.
     */
    public Gis() {
        super();
        dataroot = null;
        isLoadedDataroot = false;
    }

    /**
     * Instantiates a new gis.
     * 
     * @param node the node
     * @param service the service
     */
    Gis(Node node, NeoDataService service) {
        super(node, service);
        dataroot = null;
        isLoadedDataroot = false;
    }

    /**
     * Load dataroot.
     * 
     * @param service the service
     */
    void loadDataroot(NeoDataService service) {
        assert isLoadedDataroot == false;
        Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
        if (relation != null) {
            Node dataNode = relation.getOtherNode(node);
            dataroot = (DataRoot)service.getInstance(dataNode);
            dataroot.setGis(this);
        }
        isLoadedDataroot = true;
    }

    /**
     * Gets the gis type.
     * 
     * @return Returns the gis_type.
     */
    public GisTypes getGisType() {

        return GisTypes.findGisTypeByHeader(getStringPropertyValue(INeoConstants.PROPERTY_GIS_TYPE_NAME));
    }

    /**
     * Sets the gis type.
     * 
     * @param gisType The gis_type to set.
     */
    public void setGisType(GisTypes gisType) {
        setPropertyValue(INeoConstants.PROPERTY_GIS_TYPE_NAME, gisType.getHeader());
    }

    /**
     * Gets the dataroot.
     * 
     * @return Returns the dataroot.
     */
    public DataRoot getDataroot() {
        assert isLoadedDataroot == true;
        return dataroot;
    }

    /**
     * Sets the dataroot.
     * 
     * @param dataroot The dataroot to set.
     */
    public void setDataroot(DataRoot dataroot) {
        assert isLoadedDataroot == false || dataroot == null;
        isLoadedDataroot = true;
        this.dataroot = dataroot;
    }

    /**
     * Save.
     */
    @Override
    void save() {
        super.save();
        if (isLoadedDataroot) {
            Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
            if (dataroot == null) {
                if (relation != null) {
                    relation.delete();
                }
            } else if (dataroot.node != null) {// links only if dataroot stored in database
                if (relation != null) {
                    Node dataNode = relation.getOtherNode(node);
                    if (!dataNode.equals(dataroot.node)) {
                        relation.delete();
                        relation = null;
                    }
                }
                if (relation == null) {
                    node.createRelationshipTo(dataroot.node, GeoNeoRelationshipTypes.NEXT);
                }
            }
        }
    }

    /**
     * Creates the.
     * 
     * @param service the service
     */
    @Override
    void create(NeoDataService service) {
        super.create(service);
        setPropertyValue(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.GIS.getId());
        service.getReferenceNode().createRelationshipTo(node, GeoNeoRelationshipTypes.CHILD);
    }
}
