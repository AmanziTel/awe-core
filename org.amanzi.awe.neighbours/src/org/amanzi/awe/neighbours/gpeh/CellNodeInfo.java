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

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Best cell information wrapper
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CellNodeInfo {

    /** The best cell. */
    private Node cellSector;
    /** The distance. */
    private double distance;
    /** The cell sector info. */
    private Node cellSectorInfo;
    private Integer uarfcnDl;
    private Double lat;
    private Double lon;

    /**
     * Instantiates a new cell node info.
     * 
     * @param cellSector the cell sector
     * @param cellSectorInfo the cell sector info
     */
    public CellNodeInfo(Node cellSector, Node cellSectorInfo) {
        super();
        this.cellSector = cellSector;
        this.cellSectorInfo = cellSectorInfo;
    }

    /**
     * Gets the uarfcn dl.
     * 
     * @return the uarfcn dl
     */
    public Integer getUarfcnDl() {
        return uarfcnDl;
    }

    /**
     * Sets the uarfcn dl.
     * 
     * @param uarfcnDl the new uarfcn dl
     */
    public void setUarfcnDl(Integer uarfcnDl) {
        this.uarfcnDl = uarfcnDl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cellSector == null) ? 0 : cellSector.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CellNodeInfo other = (CellNodeInfo)obj;
        if (cellSector == null) {
            if (other.cellSector != null)
                return false;
        } else if (!cellSector.equals(other.cellSector))
            return false;
        return true;
    }

    /**
     * Gets the cell sector.
     * 
     * @return the cell sector
     */
    public Node getCellSector() {
        return cellSector;
    }

    /**
     * Gets the cell sector info.
     * 
     * @return the cell sector info
     */
    public Node getCellSectorInfo() {
        return cellSectorInfo;
    }

    /**
     * Setup location.
     * 
     * @return true, if successful
     */
    public boolean setupLocation() {
        if (lat != null && lon != null) {
            return true;
        }
        // define location
        Relationship rel = cellSector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
        if (rel != null) {
            Node site = rel.getOtherNode(cellSector);
            Double lat = (Double)site.getProperty(INeoConstants.PROPERTY_LAT_NAME, null);
            Double lon = (Double)site.getProperty(INeoConstants.PROPERTY_LON_NAME, null);
            this.lat = lat;
            this.lon = lon;
            return lat != null && lon != null;
        }
        return false;
    }

    /**
     * Gets the lat.
     * 
     * @return the lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Gets the lon.
     * 
     * @return the lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     * Gets the distance.
     * 
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the distance.
     * 
     * @param distance the new distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Define uarfcn dl.
     * 
     * @return the integer
     */
    public Integer defineUarfcnDl() {
        if (uarfcnDl == null) {
            uarfcnDl = (Integer)cellSector.getProperty("uarfcnDl", null);
        }
        return uarfcnDl;
    }

}
