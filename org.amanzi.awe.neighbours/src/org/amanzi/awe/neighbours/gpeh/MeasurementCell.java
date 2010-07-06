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

import com.vividsolutions.jts.geom.Coordinate;

/**
 * <p>
 * Container for measurement cell
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class MeasurementCell {
    
    /** The cell. */
    private final Node cell;
    
    /** The measurement. */
    private RrcMeasurement measurement;
    
    /** The lat. */
    private Double lat;
    
    /** The lon. */
    private Double lon;

    /** The distance. */
    private Double distance;

    /**
     * Instantiates a new measurement cell.
     *
     * @param cell the cell
     */
    public MeasurementCell(Node cell) {
        this.cell = cell;
    }



    /**
     * Instantiates a new measurement cell.
     *
     * @param cell the cell
     * @param measurement the measurement
     */
    public MeasurementCell(Node cell, RrcMeasurement measurement) {
        this(cell);
        this.measurement = measurement;
    }



    /**
     * Gets the measurement.
     *
     * @return the measurement
     */
    public RrcMeasurement getMeasurement() {
        return measurement;
    }

    /**
     * Sets the measurement.
     *
     * @param measurement the new measurement
     */
    public void setMeasurement(RrcMeasurement measurement) {
        this.measurement = measurement;
    }

    /**
     * Gets the cell.
     *
     * @return the cell
     */
    public Node getCell() {
        return cell;
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
        result = prime * result + ((cell == null) ? 0 : cell.hashCode());
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
        MeasurementCell other = (MeasurementCell)obj;
        if (cell == null) {
            if (other.cell != null)
                return false;
        } else if (!cell.equals(other.cell))
            return false;
        return true;
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
     * Sets the lat.
     *
     * @param lat the new lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
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
     * Sets the lon.
     *
     * @param lon the new lon
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }




    /**
     * Sets the distance.
     *
     * @param distance the new distance
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }



    /**
     * Gets the distance.
     *
     * @return the distance
     */
    public Double getDistance() {
        return distance;
    }




    /**
     * Gets the coordinate.
     *
     * @return the coordinate
     */
    public Coordinate getCoordinate() {
        return new Coordinate(getLat(),getLon());
    }
  
}
