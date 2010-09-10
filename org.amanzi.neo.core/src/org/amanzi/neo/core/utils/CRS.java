package org.amanzi.neo.core.utils;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This class handles the CRS specification. Currently it is hard coded to return WGS84
 * (EPSG:4326) for data that looks like lat/long and RT90 2.5 gon V (EPSG:3021) for data that
 * looks like it is in meters and no hints are given. If the user passes a hint, the following
 * are considered:
 * 
 * @author craig
 */
public class CRS {
    protected String type = null;
    protected String epsg = null;
    protected String wkt = null;

    private CRS() {
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return epsg;
    }

    public static CRS fromLocation(Float lat, Float lon, String hint) {
        return fromLocation(lat.doubleValue(),lon.doubleValue(),hint);
    }
    public static CRS fromLocation(double lat, double lon, String hint) {
        CRS crs = new CRS();
        crs.wkt=null;
        crs.type = "geographic";
        crs.epsg = "EPSG:4326";
        if ((lat > 90 || lat < -90) && (lon > 180 || lon < -180)) {
            crs.type = "projected";
            if (hint != null && hint.toLowerCase().startsWith("germany")) {
                crs.epsg = "EPSG:31467";
            } else {
                crs.epsg = "EPSG:3021";
            }
        }
        return crs;
    }

    public static CRS fromCRS(String crsType, String crsName) {
        CRS crs = new CRS();
        crs.wkt=null;
        crs.type = crsType;
        crs.epsg = crsName;
        return crs;
    }

    /**
     * @param crs
     */
    public static CRS fromCRS(CoordinateReferenceSystem crs) {
        CRS result = new CRS();
        result.wkt=crs.toWKT(); 
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