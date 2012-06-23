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

package org.amanzi.neo.models.render;

import java.util.List;

import org.amanzi.awe.filters.IFilter;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * <p>
 * Describes methods specific for models, that can be displayed in GUI.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IRenderableModel extends IModel {

    /**
     * Update the minimum and maximum values of latitude and longitude.
     * 
     * @param latitude
     * @param longitude
     */
    void updateLocationBounds(double latitude, double longitude);

    /**
     * set selected data element to list
     */
    void setSelectedDataElementToList(IDataElement dataElement);

    /**
     * set list of selected elements to selected data elements
     */
    void setSelectedDataElements(List<IDataElement> dataElements);

    /**
     * get selected elements list
     */
    List<IDataElement> getSelectedElements();

    /**
     * clear selected elements list
     */
    void clearSelectedElements();

    /**
     * update crs by crs code
     * 
     * @param crsCode
     * @return
     */
    CoordinateReferenceSystem updateCRS(String crsCode);

    /**
     * set crs to current gis
     * 
     * @param crs
     */
    void setCRS(CoordinateReferenceSystem crs);

    /**
     * @return minimal latitude
     */
    double getMinLatitude();

    /**
     * @return maximal latitude
     */
    double getMaxLatitude();

    /**
     * @return minimal longitude
     */
    double getMinLongitude();

    /**
     * @return maximal longitude
     */
    double getMaxLongitude();

    /**
     * get crs from current gis;
     * 
     * @return
     */
    CoordinateReferenceSystem getCRS();

    /**
     * TODO: test implementation
     * 
     * @return A <code>String</code> description for use of geo tools;
     */
    String getDescription();

    /**
     * TODO: test implementation
     * 
     * @return An envelope, representing the coordinate bounds for the data in current model.
     */
    ReferencedEnvelope getBounds();

    /**
     * Gets all the model elements in the defined bounds.
     * 
     * @param bounds
     * @return
     */
    Iterable<IDataElement> getElements(Envelope bounds) throws ModelException;

    /**
     * Find out coordinates from the defined element.
     * 
     * @param element a not null element with an underlying node or properties set.
     * @return
     */
    Coordinate getCoordinate(IDataElement element);

    /**
     * add new layer to catalog.
     * <p>
     * Layer is actually a gis elements which store some information for renderer
     * </p>
     * 
     * @param name -name of new layer
     * @param filter- filter to currentLayer
     */
    void addLayer(String name, IFilter filter);

    /**
     * Draw neighbors relationships
     * 
     * @return true or false
     */
    boolean shouldDrawNeighbors();

    /**
     * draw neighbors or not
     * 
     * @param drawNeighbors true or false
     */
    void setDrawNeighbors(boolean drawNeighbors);

}
