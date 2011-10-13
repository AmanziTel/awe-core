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

package org.amanzi.neo.services.model;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

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
    public void updateLocationBounds(double latitude, double longitude);

    public double getMinLatitude();

    public double getMaxLatitude();

    public double getMinLongitude();

    public double getMaxLongitude();

    public CRS getCRS();

    /**
     * TODO: test implementation
     * 
     * @return A <code>String</code> description for use of geo tools;
     */
    public String getDescription();

    /**
     * TODO: test implementation
     * 
     * @return An envelope, representing the coordinate bounds for the data in current model.
     */
    public ReferencedEnvelope getBounds();
}
