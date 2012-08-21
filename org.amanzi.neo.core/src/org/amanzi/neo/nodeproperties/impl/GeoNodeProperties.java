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

package org.amanzi.neo.nodeproperties.impl;

import org.amanzi.neo.nodeproperties.IGeoNodeProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class GeoNodeProperties implements IGeoNodeProperties {

    private static final String LATITUDE_PROPERTY = "lat";

    private static final String LONGITUDE_PROPERTY = "lon";

    private static final String MIN_LATTIUDE = "min_lat";

    private static final String MAX_LATITUDE = "max_lat";

    private static final String MIN_LONGITUDE = "min_lon";

    private static final String MAX_LONGITUDE = "max_lon";

    private static final String CRS = "crs";

    private static final String CAN_RENDER_PROPERTY = "can_render";

    @Override
    public String getLatitudeProperty() {
        return LATITUDE_PROPERTY;
    }

    @Override
    public String getLongitudeProperty() {
        return LONGITUDE_PROPERTY;
    }

    @Override
    public String getMinLatitudeProperty() {
        return MIN_LATTIUDE;
    }

    @Override
    public String getMaxLatitudeProperty() {
        return MAX_LATITUDE;
    }

    @Override
    public String getMinLongitudeProperty() {
        return MIN_LONGITUDE;
    }

    @Override
    public String getMaxLongitudeProperty() {
        return MAX_LONGITUDE;
    }

    @Override
    public String getCRSProperty() {
        return CRS;
    }

    @Override
    public String getCanRenderProperty() {
        return CAN_RENDER_PROPERTY;
    }

}
