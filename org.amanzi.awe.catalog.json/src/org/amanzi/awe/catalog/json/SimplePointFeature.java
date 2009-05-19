package org.amanzi.awe.catalog.json;

import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class SimplePointFeature implements Feature {
    private static GeometryFactory geometryFactory = new GeometryFactory();
    private Point point;
    private HashMap<String, Object> properties;

    public SimplePointFeature( final double x, final double y,
            final HashMap<String, Object> properties ) {
        Coordinate coordinate = new Coordinate(x, y);
        this.point = geometryFactory.createPoint(coordinate);
        this.properties = properties;
    }
    public final Point[] getPoints() {
        return new Point[]{this.point};
    }
    public final Map<String, Object> getProperties() {
        return properties;
    }
    public final String getType() {
        return "Point";
    }

    public final Geometry createGeometry() {
        return point;
    }

    public final String toString() {
        if (properties.containsKey("name")) {
            return properties.get("name").toString();
        } else {
            return point.toString();
        }
    }
}
