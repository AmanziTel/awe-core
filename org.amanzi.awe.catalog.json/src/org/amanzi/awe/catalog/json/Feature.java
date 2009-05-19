package org.amanzi.awe.catalog.json;

import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * A generic interface for all features, whether made from JSON or from some other source of data.
 * This is to allow for more compact data formats like CSV, or hex streams. It is modeled however on
 * the types of data found in GeoJSON features.
 * 
 * @author craig
 */
public interface Feature {
    /**
     * get the type, as specified in the GeoJSON spec, eg. Point, MultiPoint, Polygon, etc.
     * 
     * @return type
     */
    String getType();
    /**
     * get the set of points representing this feature, or array of length 1 for Point type.
     * 
     * @return array of {@link Point} objects
     */
    Point[] getPoints();
    /**
     * get the map of additional properties for this data type, eg. domain specific data
     * 
     * @return {@link Map} object
     */
    Map<String, Object> getProperties();
    /**
     * Creates geometry object for this feature.
     * 
     * @return {@link Geometry} object
     */
    Geometry createGeometry();
}
