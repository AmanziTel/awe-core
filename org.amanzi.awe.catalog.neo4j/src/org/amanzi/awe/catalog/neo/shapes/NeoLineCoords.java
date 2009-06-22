package org.amanzi.awe.catalog.neo.shapes;

import com.vividsolutions.jts.geom.Coordinate;

public class NeoLineCoords 
{
	
	private double startX;
	private double startY;
	private double endX;
	private double endY;

	public NeoLineCoords(double xStart,double yStart,double xEnd,double yEnd)
	{
		this.startX=xStart;
		this.startY=yStart;
		this.endX=xEnd;
		this.endY=yEnd;
	}

	public double getStartX() {
		return startX;
	}

	public void setStartX(double startX) {
		this.startX = startX;
	}

	public double getStartY() {
		return startY;
	}

	public void setStartY(double startY) {
		this.startY = startY;
	}

	public double getEndX() {
		return endX;
	}

	public void setEndX(double endX) {
		this.endX = endX;
	}

	public double getEndY() {
		return endY;
	}

	public void setEndY(double endY) {
		this.endY = endY;
	}
	
	public Coordinate getStartCoordinate()
	{
		return new Coordinate(startX,startY);
	}
	
	public Coordinate getEndCoordinate()
	{
		return new Coordinate(endX,endY);
	}
	
	public Coordinate[] getCoordArray()
	{
	Coordinate[] coords=new Coordinate[2];
	coords[0]=getStartCoordinate();
	coords[1]=getEndCoordinate();
		
		return coords;
	}
	
}
