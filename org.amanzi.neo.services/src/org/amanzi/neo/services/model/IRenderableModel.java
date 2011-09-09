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

import org.geotools.referencing.CRS;

/**
 * <p>
 * Describes methods specific for models, that can be displayed in GUI.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IRenderableModel {

    /**
     * Update the minimum and maximum values of latitude and longitude.
     * 
     * @param latitude
     * @param longitude
     */
    public void updateBounds(double latitude, double longitude);

    public double getMinLatitude();

    public double getMaxLatitude();

    public double getMinLongitude();

    public double getMaxLongitude();

    public CRS getCRS();
}
