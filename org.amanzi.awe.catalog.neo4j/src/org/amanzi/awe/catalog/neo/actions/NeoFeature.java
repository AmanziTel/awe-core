package org.amanzi.awe.catalog.neo.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * This is the based class for all features that are genuinely build from JSON.
 * As such a JSONObject is passed to the constructor, and used to generate the
 * feature data required.
 * 
 * @author Dalibor
 */
class NeoFeature implements Feature {
	private static GeometryFactory geometryFactory = new GeometryFactory();

	@Override
	public Geometry createGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point[] getPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
