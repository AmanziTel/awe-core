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

package org.amanzi.neo.core.utils;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.utils.CRS;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class GisProperties {
    private final Node gis;
    private CRS crs;
    private double[] bbox;
    long savedData;

    public GisProperties(Node gis) {
        this.gis = gis;
        bbox = (double[])gis.getProperty(INeoConstants.PROPERTY_BBOX_NAME, null);
        savedData = (Long)gis.getProperty(INeoConstants.COUNT_TYPE_NAME, 0L);
    }

    /**
     *inc saved;
     */
    public void incSaved() {
        savedData++;
    }

    public final void checkCRS(float lat, float lon, String hint) {
        if (crs == null) {
            // TODO move CRS class and update CRS in amanzi.neo.core
            crs = CRS.fromLocation(lat, lon, hint);
            saveCRS();
        }
    }

    /**
     * initCRS
     */
    public void initCRS() {
        if (gis.hasProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME) && gis.hasProperty(INeoConstants.PROPERTY_CRS_NAME)) {
            crs = CRS.fromCRS((String)gis.getProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME), (String)gis.getProperty(INeoConstants.PROPERTY_CRS_NAME));
        }
    }

    /**
     * ubdate bbox
     * 
     * @param lat - latitude
     * @param lon - longitude
     */
    public final void updateBBox(double lat, double lon) {
        if (bbox == null) {
            bbox = new double[] {lon, lon, lat, lat};
        } else {
            if (bbox[0] > lon)
                bbox[0] = lon;
            if (bbox[1] < lon)
                bbox[1] = lon;
            if (bbox[2] > lat)
                bbox[2] = lat;
            if (bbox[3] < lat)
                bbox[3] = lat;
        }
    }

    /**
     * @return Returns the gis.
     */
    public Node getGis() {
        return gis;
    }

    /**
     * @return Returns the bbox.
     */
    public double[] getBbox() {
        return bbox;
    }

    /**
     * @param crs The crs to set.
     */
    public void setCrs(CRS crs) {
        this.crs = crs;
    }

    /**
     * @return
     */
    public CRS getCrs() {
        return crs;
    }

    /**
     *save bbox to gis node
     */
    public void saveBBox() {
        if (getBbox() != null) {
            gis.setProperty(INeoConstants.PROPERTY_BBOX_NAME, getBbox());
        }
    }

    /**
     *save CRS to gis node
     */
    public void saveCRS() {
        if (getCrs() != null) {
            if (crs.getWkt() != null) {
                gis.setProperty(INeoConstants.PROPERTY_WKT_CRS, crs.getWkt());
            }
            gis.setProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, crs.getType());// TODO remove?
            // - not used
            // in GeoNeo
            gis.setProperty(INeoConstants.PROPERTY_CRS_NAME, crs.toString());
        }
    }

    /**
     *save CRS
     * 
     * @param crs -CoordinateReferenceSystem
     */
    public void setCrs(CoordinateReferenceSystem crs) {
        setCrs(CRS.fromCRS(crs));
    }

    /**
     * @param bbox The bbox to set.
     */
    public void setBbox(double[] bbox) {
        this.bbox = bbox;
    }
    
    public long getSavedData() {
        return savedData;
    }
}