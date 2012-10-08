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
package org.amanzi.neo.models.impl.internal.util;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This class handles the CRS specification. Currently it is hard coded to return WGS84 (EPSG:4326)
 * for data that looks like lat/long and RT90 2.5 gon V (EPSG:3021) for data that looks like it is
 * in meters and no hints are given. If the user passes a hint, the following are considered:
 * 
 * @author craig
 */
public class CRSWrapper {

    protected String type = null;
    protected String epsg = null;
    protected String wkt = null;

    private CRSWrapper() {
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return epsg;
    }

    public static CRSWrapper fromLocation(Float lat, Float lon, String hint) {
        return fromLocation(lat.doubleValue(), lon.doubleValue(), hint);
    }

    public static CRSWrapper fromLocation(double lat, double lon, String hint) {
        CRSWrapper crs = new CRSWrapper();
        crs.wkt = null;
        crs.type = "geographic";
        crs.epsg = "EPSG:4326";
        if (((lat > 90) || (lat < -90)) && ((lon > 180) || (lon < -180))) {
            crs.type = "projected";
            if ((hint != null) && hint.toLowerCase().contains("germany")) {
                crs.epsg = "EPSG:31467";
            } else {
                crs.epsg = "EPSG:3021";
            }
        }
        return crs;
    }

    public static CRSWrapper fromCRS(String crsType, String crsName) {
        CRSWrapper crs = new CRSWrapper();
        crs.wkt = null;
        crs.type = crsType;
        crs.epsg = crsName;
        return crs;
    }

    /**
     * @param crs
     */
    public static CRSWrapper fromCRS(CoordinateReferenceSystem crs) {
        CRSWrapper result = new CRSWrapper();
        result.wkt = crs.toWKT();
        result.type = "geographic";
        result.epsg = crs.getIdentifiers().iterator().next().toString();
        return result;
    }

    public String getWkt() {
        return wkt;
    }

    public String getEpsg() {
        return epsg;
    }
}