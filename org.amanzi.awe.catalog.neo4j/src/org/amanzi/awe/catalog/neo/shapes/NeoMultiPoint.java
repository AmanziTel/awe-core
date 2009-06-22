package org.amanzi.awe.catalog.neo.shapes;

import com.vividsolutions.jts.geom.Coordinate;




public class NeoMultiPoint 
{

	Coordinate[] coords;
    NeoCoords[] nCoords;
	
	public NeoMultiPoint(NeoCoords[] nCoords)
	{
		this.nCoords=nCoords;
		this.coords=createCoordinates(nCoords);
		
	}
	
	
	private Coordinate[] createCoordinates(NeoCoords[] ncoords)
	{
		Coordinate[] coordinates=new Coordinate[ncoords.length];
		for(int i=0;i<ncoords.length;i++)
		{
			coordinates[i]=createCoordinate(ncoords[i]);
		}
		return coordinates;
	} 
	
	
	private Coordinate createCoordinate(NeoCoords ncoords)
	{
		return new Coordinate(ncoords.getX(),ncoords.getY());
		
	}


	public Coordinate[] getCoords() {
		return coords;
	}


	public void setCoords(Coordinate[] coords) {
		this.coords = coords;
	}


	public NeoCoords[] getNCoords() {
		return nCoords;
	}


	public void setNCoords(NeoCoords[] coords) {
		nCoords = coords;
	}
	
	
	
	
	
}
