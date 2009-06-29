package org.amanzi.awe.catalog.neo.shapes;

import com.vividsolutions.jts.geom.Coordinate;

public class NeoMultiLine extends Object
{
    NeoLineCoords[] nCoords;
	
	public NeoMultiLine(NeoLineCoords[] nCoords)
	{
		this.nCoords=nCoords;
		
	}
	
	public Coordinate[] getCoords()
	{
		Coordinate[] coords=new Coordinate[nCoords.length*2];
		for(int i=0;i<nCoords.length;i++)
		{
			coords[2*i]=nCoords[i].getStartCoordinate();
			coords[2*i+1]=nCoords[i].getEndCoordinate();
		}
		return coords;
	}

	public NeoLineCoords[] getNCoords() {
		return nCoords;
	}
	
	
	
}
