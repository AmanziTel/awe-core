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

import org.neo4j.graphdb.Node;

/**
 * <p>
 * Best cell information wrapper
 * </p>.
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
    private Long lat;
    private Long lon;
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
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cellSector == null) ? 0 : cellSector.hashCode());
        return result;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
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
     *
     * @return
     */
    public boolean setupLocation() {
        return false;
    }

    public Long getLat() {
        return lat;
    }


    public Long getLon() {
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


    
    
}
