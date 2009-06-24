package org.amanzi.awe.catalog.neo.beans;

public class Sector extends Object
{
     double azimuth;
     double beamwidth;
     String name;
     
     public Sector()
     {
    	 
     }

	public Sector(double azimuth, double beamwidth, String name) {
		super();
		this.azimuth = azimuth;
		this.beamwidth = beamwidth;
		this.name = name;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

	public double getBeamwidth() {
		return beamwidth;
	}

	public void setBeamwidth(double beamwidth) {
		this.beamwidth = beamwidth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
     
    
}
