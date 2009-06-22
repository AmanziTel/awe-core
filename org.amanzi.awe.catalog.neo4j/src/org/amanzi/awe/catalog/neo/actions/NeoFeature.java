package org.amanzi.awe.catalog.neo.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;




import org.amanzi.awe.catalog.neo.shapes.NeoCoords;
import org.amanzi.awe.catalog.neo.shapes.NeoLine;
import org.amanzi.awe.catalog.neo.shapes.NeoLineCoords;
import org.amanzi.awe.catalog.neo.shapes.NeoMultiLine;
import org.amanzi.awe.catalog.neo.shapes.NeoMultiPoint;
import org.amanzi.awe.catalog.neo.shapes.NeoMultiPolygon;
import org.amanzi.awe.catalog.neo.shapes.NeoPoint;
import org.amanzi.awe.catalog.neo.shapes.NeoPolygon;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.Node;

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
class NeoFeature implements Feature 
{
	String FeatureType;
	private Point[] points;
	private Node neoNode;
	private Geometry objGeometry;
	
	
	private static GeometryFactory geometryFactory = new GeometryFactory();

	
	public NeoFeature(EmbeddedNeo neo,Node node)
	{
		this.neoNode=node;
	}
	
	@Override
	 public final Geometry createGeometry() {
        if (objGeometry == null) 
        {
        	if(neoNode.getProperty("geometry") instanceof NeoPoint)
        	{
        		objGeometry=geometryFactory.createPoint(((NeoPoint)neoNode.getProperty("geometry")).getCoords());
        	}
        	else if(neoNode.getProperty("geometry") instanceof NeoMultiPoint)
        	{
        		objGeometry=geometryFactory.createMultiPoint(((NeoMultiPoint)neoNode.getProperty("geometry")).getCoords());
        	}
        	else if(neoNode.getProperty("geometry") instanceof NeoLine)
        	{
        		objGeometry=geometryFactory.createLineString(((NeoLine)neoNode.getProperty("geometry")).getCoords());
        	}
        	else if(neoNode.getProperty("geometry") instanceof NeoMultiLine)
        	{
        		  List<LineString> lineStringList = new ArrayList<LineString>();
        		  for( int i = 0; i <((NeoMultiLine) neoNode.getProperty("geometry")).getNCoords().length; i++ ) {
        	           
        	            lineStringList.add(geometryFactory.createLineString(((NeoMultiLine) neoNode.getProperty("geometry")).getNCoords()[i].getCoordArray()));
        	        }
        		  objGeometry=geometryFactory.createMultiLineString(lineStringList
        	                .toArray(new LineString[lineStringList.size()]));
        	}
        	else if(neoNode.getProperty("geometry") instanceof NeoPolygon)
        	{
        		LinearRing linearRing = null;

                final List<LinearRing> holeLinearRings = new ArrayList<LinearRing>();
                for(  int i = 0; i <((NeoPolygon) neoNode.getProperty("geometry")).getNCoords().length; i++ ) {
                    if (i == 0) {
                        linearRing = createLinearRing(((NeoPolygon) neoNode.getProperty("geometry")).getNCoords()[i].getCoordArray());
                    } else {
                        holeLinearRings.add(createLinearRing(((NeoPolygon) neoNode.getProperty("geometry")).getNCoords()[i].getCoordArray()));
                    }
                }
                objGeometry=geometryFactory.createPolygon(linearRing, holeLinearRings
                        .toArray(new LinearRing[holeLinearRings.size()]));
        	}
        	else if(neoNode.getProperty("geometry") instanceof NeoMultiPolygon)
        	{
        		List<Polygon> polygons = new ArrayList<Polygon>();
        		for(int j=0;j<((NeoMultiPolygon)neoNode.getProperty("geometry")).getPolygons().length;j++)
        		{
        			polygons.add(createPolygon(((NeoMultiPolygon)neoNode.getProperty("geometry")).getPolygons()[j]));
        		}
        		
        		
        		objGeometry=geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
        	}
        }
        return objGeometry;
    }
	
	
	private Polygon createPolygon(NeoPolygon nPolygon)
	{
		LinearRing linearRing = null;

        final List<LinearRing> holeLinearRings = new ArrayList<LinearRing>();
        for(  int i = 0; i <nPolygon.getNCoords().length; i++ ) {
            if (i == 0) {
                linearRing = createLinearRing((nPolygon.getNCoords()[i].getCoordArray()));
            } else {
                holeLinearRings.add(createLinearRing(nPolygon.getNCoords()[i].getCoordArray()));
            }
        }
        return geometryFactory.createPolygon(linearRing, holeLinearRings
                .toArray(new LinearRing[holeLinearRings.size()]));
	}
	
	
	 private LinearRing createLinearRing( Coordinate[] Coords ) {
	        
	        final CoordinateArraySequence sequence = new CoordinateArraySequence(Coords);
	        return new LinearRing(sequence, geometryFactory);
	    }
	
	
	
	@Override
	public Point[] getPoints() {
		// TODO Auto-generated method stub
		if(points==null)
		{
			if(neoNode.getProperty("geometry") instanceof NeoPoint)
			{
				points=new Point[]{geometryFactory.createPoint(((NeoPoint)neoNode.getProperty("geometry")).getCoords())};
			}
			else if(neoNode.getProperty("geometry") instanceof NeoMultiPoint)
			{
				NeoMultiPoint nPoint=((NeoMultiPoint)neoNode.getProperty("geometry"));
				
				points=new Point[nPoint.getCoords().length];
				
				for(int j=0;j<nPoint.getCoords().length;j++)
				{
					points[j]=geometryFactory.createPoint(nPoint.getCoords()[j]);
				}
				
			}
			else if(neoNode.getProperty("geometry") instanceof NeoLine)
			{
				NeoLine nPoint=((NeoLine)neoNode.getProperty("geometry"));
				
				points=new Point[nPoint.getCoords().length];
				
				for(int j=0;j<nPoint.getCoords().length;j++)
				{
					points[j]=geometryFactory.createPoint(nPoint.getCoords()[j]);
				}
			}
			else if(neoNode.getProperty("geometry") instanceof NeoMultiLine)
			{
				NeoMultiLine nPoint=((NeoMultiLine)neoNode.getProperty("geometry"));
				
				points=new Point[nPoint.getCoords().length];
				
				for(int j=0;j<nPoint.getCoords().length;j++)
				{
					points[j]=geometryFactory.createPoint(nPoint.getCoords()[j]);
				}
			}
			else if(neoNode.getProperty("geometry") instanceof NeoPolygon)
			{			
				NeoPolygon nPoint=((NeoPolygon)neoNode.getProperty("geometry"));
				
				points=new Point[nPoint.getCoords().length];
				
				for(int j=0;j<nPoint.getCoords().length;j++)
				{
					points[j]=geometryFactory.createPoint(nPoint.getCoords()[j]);
				}
			}
			else if(neoNode.getProperty("geometry") instanceof NeoMultiPolygon)
			{
				NeoMultiPolygon nPoint=((NeoMultiPolygon)neoNode.getProperty("geometry"));
				
				points=new Point[nPoint.getPoints().length];
				
				for(int j=0;j<nPoint.getPoints().length;j++)
				{
					points[j]=geometryFactory.createPoint(nPoint.getPoints()[j]);
				}
			}
		}
		return points;
	}

		
	@Override
	public Map<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		if(neoNode.getProperty("geometry") instanceof NeoPoint)
    	{
    		
    	}
    	else if(neoNode.getProperty("geometry") instanceof NeoMultiPoint)
    	{
    	
    	}
    	else if(neoNode.getProperty("geometry") instanceof NeoLine)
    	{
    		
    	}
    	else if(neoNode.getProperty("geometry") instanceof NeoMultiLine)
    	{
    		
    	}
    	else if(neoNode.getProperty("geometry") instanceof NeoMultiPoint)
    	{
    		
    	}
    	else if(neoNode.getProperty("geometry") instanceof NeoMultiPoint)
    	{
    		return "NeoMultiPoint";
    	}
		return null;
	}

}
