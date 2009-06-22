package org.amanzi.awe.catalog.neo.shapes;

import java.util.Vector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;



public class NeoMultiPolygon extends Object
{

	NeoPolygon[] polygons;
	
	public NeoMultiPolygon(NeoPolygon[] nPolygons)
	{
		this.polygons=nPolygons;
	}
	
	public NeoPolygon[] getPolygons()
	{
		return polygons;
	}
	
	public Coordinate[] getPoints()
	{
		Vector<Coordinate> pointVector=new Vector<Coordinate>();
		int pointNum=0;
		for(int i=0;i<polygons.length;i++)
		{
			pointNum=+polygons[i].getCoords().length;
			for(int j=0;j<polygons[i].getCoords().length;j++)
			{
				pointVector.add(polygons[i].getCoords()[j]);
			}
		}
         return pointVector.toArray(new Coordinate[pointNum]);
	}
}
