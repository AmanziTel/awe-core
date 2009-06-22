package org.amanzi.awe.catalog.neo.shapes;

import com.vividsolutions.jts.geom.Coordinate;

public class NeoPoint extends Object
{

	Coordinate coords;

	
	public NeoPoint(NeoCoords nCoords)
	{
		
		this.coords=createCoordinate(nCoords);
		
	}
	
	
	
	
	private Coordinate createCoordinate(NeoCoords ncoords)
	{
		return new Coordinate(ncoords.getX(),ncoords.getY());
		
	}




	public Coordinate getCoords() {
		return coords;
	}




	public void setCoords(Coordinate coords) {
		this.coords = coords;
	}


	
	
	
	
	
}
