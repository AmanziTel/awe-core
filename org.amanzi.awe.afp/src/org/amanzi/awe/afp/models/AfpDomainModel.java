package org.amanzi.awe.afp.models;

import org.amanzi.neo.services.INeoConstants;
import org.neo4j.graphdb.Node;

public class AfpDomainModel {
	String name;
	boolean free=false;

	
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
	
}
