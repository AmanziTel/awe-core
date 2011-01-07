package org.amanzi.awe.afp.models;

import org.amanzi.neo.services.INeoConstants;
import org.neo4j.graphdb.Node;

public class AfpDomainModel {
	String name;
	boolean free=false;
	String filters ="";
	int numTRX;

	public AfpDomainModel() {
		
	}
	
	public AfpDomainModel(AfpDomainModel c) {
		this.setName(c.getName());
		this.setFree(c.isFree());
		this.setFilters(c.getFilters());
		this.setNumTRX(c.getNumTRX());
	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFree() {
		return free;
	}
	public void setFree(boolean free) {
		this.free = free;
	}
	public String getFilters() {
		if(filters != null) {
			if(filters.trim().length() == 0) {
				return null;
			}
		}
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}
	
	public int getNumTRX() {
		return numTRX;
	}

	public void setNumTRX(int numTRX) {
		this.numTRX = numTRX;
	}

	protected static AfpDomainModel getModel(AfpDomainModel model, Node n) {

		try {
			String name = (String) n.getProperty(INeoConstants.PROPERTY_NAME_NAME);
			model.setName(name);

			try {
				String filter = (String) n.getProperty(INeoConstants.AFP_PROPERTY_FILTERS_NAME);
				int trxCount = (Integer) n.getProperty(INeoConstants.AFP_PROPERTY_TRX_COUNT_NAME);
				model.setFilters(filter);
				model.setNumTRX(trxCount);
			} catch(Exception e) {
				// filter not set
			}


			return model;
		} catch (Exception e) {
			return null;
		}
	}
	
}
