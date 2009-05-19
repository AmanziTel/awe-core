package org.amanzi.awe.catalog.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * This is the based class for all features that are genuinely build from JSON. As such a JSONObject
 * is passed to the constructor, and used to generate the feature data required.
 * 
 * @author craig
 */
class JSONFeature implements Feature {
    private static GeometryFactory geometryFactory = new GeometryFactory();

    private String type;
    private JSONObject geometry;
    private JSONObject properties;
    private Point[] points;
    private HashMap<String, Object> propMap;
    private Geometry objGeometry;

    public JSONFeature( final JSONObject jsonObject ) {
        this.geometry = jsonObject.getJSONObject("geometry");
        this.properties = jsonObject.getJSONObject("properties");
        this.type = this.geometry.getString("type"); // We only care about the geometry type,
        // because the feature type is by
        // definition "Feature"
    }
    public final String getType() {
        return type;
    }
    public final Point[] getPoints() {
        if (points == null) {
            JSONArray coordinates = geometry.getJSONArray("coordinates");
            if (type.equals("Point")) {
                points = new Point[]{makePoint(coordinates)};
            } else {
                // Assume 2D array of points
                int countPoints = coordinates.size();
                points = new Point[countPoints];
                for( int i = 0; i < countPoints; i++ ) {
                    points[i] = makePoint(coordinates.getJSONArray(i));
                }
            }
        }
        return points;
    }
    private Point makePoint( final JSONArray jsonPoint ) {
        Coordinate coordinate = new Coordinate(jsonPoint.getDouble(0), jsonPoint.getDouble(1));
        return geometryFactory.createPoint(coordinate);
    }

    public final Geometry createGeometry() {
        if (objGeometry == null) {
            if (geometry != null) {
                final JSONGeoFeatureType featureType = JSONGeoFeatureType.fromCode(type);
                final JSONArray coordinates = geometry.getJSONArray("coordinates");
                switch( featureType ) {
                case POINT:
                    objGeometry = createPoint(coordinates);
                    break;
                case MULTI_POINT:
                    objGeometry = createMultiPoint(coordinates);
                    break;
                case LINE:
                    objGeometry = createLine(coordinates);
                    break;
                case MULTI_LINE_STRING:
                    objGeometry = createMultiLine(coordinates);
                    break;
                case POLYGON:
                    objGeometry = createPolygon(coordinates);
                    break;
                case MULTI_POLYGON:
                    objGeometry = createMultiPolygon(coordinates);
                default:
                    break;
                }
            }
        }
        return objGeometry;
    }

    /**
     * Creates {@link Point} object from json string.
     * 
     * @param coordinates {@link JSONArray} object
     * @return {@link Point} object
     */
    private Geometry createPoint( final JSONArray coordinates ) {
        return geometryFactory.createPoint(createCoordinate(coordinates));
    }

    /**
     * Creates {@link MultiPoint} object from json string.
     * 
     * @param jsonCoordinates json representation of MultiPoint.
     * @return {@link MultiPoint} object
     */
    private Geometry createMultiPoint( final JSONArray jsonCoordinates ) {
        return geometryFactory.createMultiPoint(createCoordinates(jsonCoordinates));
    }

    /**
     * Creates {@link LineString} object from json string.
     * 
     * @param jsonCoordinates json representation of LineString.
     * @return {@link LineString} object
     */
    private Geometry createLine( final JSONArray jsonCoordinates ) {
        return geometryFactory.createLineString(createCoordinates(jsonCoordinates));
    }

    /**
     * Creates {@link MultiLineString} object from json string.
     * 
     * @param jsonCoordinates json representation of MultiLineString.
     * @return {@link MultiLineString} object
     */
    private Geometry createMultiLine( final JSONArray jsonCoordinates ) {
        List<LineString> lineStringList = new ArrayList<LineString>();
        for( int i = 0; i < jsonCoordinates.size(); i++ ) {
            JSONArray jsonLine = jsonCoordinates.getJSONArray(i);
            lineStringList.add(geometryFactory.createLineString(createCoordinates(jsonLine)));
        }
        return geometryFactory.createMultiLineString(lineStringList
                .toArray(new LineString[lineStringList.size()]));
    }

    /**
     * Creates {@link Polygon} object from {@link JSONArray} object.
     * 
     * @param jsonCoordinates {@link JSONArray} object
     * @return {@link Polygon} object
     */
    private Polygon createPolygon( final JSONArray jsonCoordinates ) {
        LinearRing linearRing = null;

        final List<LinearRing> holeLinearRings = new ArrayList<LinearRing>();
        for( int i = 0; i < jsonCoordinates.size(); i++ ) {
            if (i == 0) {
                linearRing = createLinearRing(jsonCoordinates.getJSONArray(i));
            } else {
                holeLinearRings.add(createLinearRing(jsonCoordinates.getJSONArray(i)));
            }
        }
        return geometryFactory.createPolygon(linearRing, holeLinearRings
                .toArray(new LinearRing[holeLinearRings.size()]));
    }

    /**
     * Creates {@link MultiPolygon} object from json string.
     * 
     * @param jsonCoordinates json representation of MultiPolygon.
     * @return {@link MultiPolygon} object
     */
    private Geometry createMultiPolygon( final JSONArray jsonCoordinates ) {

        List<Polygon> polygons = new ArrayList<Polygon>();
        for( int i = 0; i < jsonCoordinates.size(); i++ ) {
            JSONArray jsonPolygon = jsonCoordinates.getJSONArray(i);
            polygons.add(createPolygon(jsonPolygon));
        }
        return geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
    }

    /**
     * Creates {@link LinearRing} object from given json.
     * 
     * @param jsonCoordinates {@link JSONArray} object
     * @return {@link LinearRing} object
     */
    private LinearRing createLinearRing( final JSONArray jsonCoordinates ) {
        final Coordinate[] coordinates = new Coordinate[jsonCoordinates.size()];
        for( int i = 0; i < coordinates.length; i++ ) {
            coordinates[i] = createCoordinate(jsonCoordinates.getJSONArray(i));
        }
        final CoordinateArraySequence sequence = new CoordinateArraySequence(coordinates);
        return new LinearRing(sequence, geometryFactory);
    }

    /**
     * Creates {@link Coordinate} array out of given {@link JSONArray} object.
     * 
     * @param jsonCoordinates {@link JSONArray} object that represents array of coordinates
     * @return array of {@link Coordinate} objects
     */
    private Coordinate[] createCoordinates( final JSONArray jsonCoordinates ) {
        Coordinate[] coordinates = new Coordinate[jsonCoordinates.size()];
        for( int i = 0; i < jsonCoordinates.size(); i++ ) {
            coordinates[i] = createCoordinate(jsonCoordinates.getJSONArray(i));
        }
        return coordinates;
    }

    /**
     * Creates {@link Coordinate} object out of given {@link JSONArray} object.
     * 
     * @param json {@link JSONArray} object
     * @return {@link Coordinate} object
     */
    private Coordinate createCoordinate( final JSONArray json ) {
        return new Coordinate(json.getDouble(0), json.getDouble(1));
    }

    public final Map<String, Object> getProperties() {
        if (propMap == null) {
            this.propMap = new HashMap<String, Object>();
            if (properties != null) {
                for( Object key : properties.keySet() ) {
                    propMap.put(key.toString(), properties.get(key));
                }
            }
        }
        return propMap;
    }
    public final String toString() {
        if (getProperties().containsKey("name")) {
            return getProperties().get("name").toString();
        } else {
            return points[0].toString();
        }
    }
}
