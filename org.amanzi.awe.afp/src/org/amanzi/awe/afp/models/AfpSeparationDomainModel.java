package org.amanzi.awe.afp.models;

import org.amanzi.neo.services.INeoConstants;
import org.neo4j.graphdb.Node;

public class AfpSeparationDomainModel extends AfpDomainModel {
	
	/**
	 * Array: 0- BCCH-BCCH
	 * 1- BCCH-Non/BB TCH
	 * 2- BCCH-SY TCH
	 * 3- Non/BB TCH-BCCH
	 * 4- Non/BB TCH-Non/BB TCH
	 * 5- Non/BB TCH-SY TCH
	 * 6- SY TCH-BCCJ
	 * 7- SY TCH-Non/BB TCH
	 * 8- SY TCH-SY TCH-
	 */
	private String[] separations = new String[]{"2", "2", "2", "2", "2", "2", "2", "2", "1"};

	public AfpSeparationDomainModel() {
		
	}
	public AfpSeparationDomainModel(AfpSeparationDomainModel c) {
		this.setFree(c.isFree());
		this.setName(c.getName());
		this.setSeparations(c.getSeparations());
	}
	/**
	 * @return the separations array
	 */
	public String[] getSeparations() {
		return separations;
	}

	/**
	 * @param separations the separations array to set
	 */
	public void setSeparations(String[] separations) {
		this.separations = separations;
	}
	
	public String getSeparation(int index) {
		if(index < this.separations.length)
			return separations[index];
		return "";
	}
	public void setSeparation(int index, String separation) {
		if(index < this.separations.length)
			this.separations[index] = separation;
	}
	/**
	 * @return the separations array 0 element
	 */
	public String getSeparation0() {
		return separations[0];
	}

	/**
	 * @param separation the separations array 0 element to set
	 */
	public void setSeparation0(String separation) {
		this.separations[0] = separation;
	}
	
	
	/**
	 * @return the separations array 1 element
	 */
	public String getSeparation1() {
		return separations[1];
	}

	/**
	 * @param separation the separations array 1 element to set
	 */
	public void setSeparation1(String separation) {
		this.separations[1] = separation;
	}
	
	
	/**
	 * @return the separations array 2 element
	 */
	public String getSeparation2() {
		return separations[2];
	}

	/**
	 * @param separation the separations array 2 element to set
	 */
	public void setSeparation2(String separation) {
		this.separations[2] = separation;
	}
	
	
	/**
	 * @return the separations array 3 element
	 */
	public String getSeparation3() {
		return separations[3];
	}

	/**
	 * @param separation the separations array 3 element to set
	 */
	public void setSeparation3(String separation) {
		this.separations[3] = separation;
	}
	
	
	/**
	 * @return the separations array 4 element
	 */
	public String getSeparation4() {
		return separations[4];
	}

	/**
	 * @param separation the separations array 4 element to set
	 */
	public void setSeparation4(String separation) {
		this.separations[4] = separation;
	}
	
	
	/**
	 * @return the separations array 5 element
	 */
	public String getSeparation5() {
		return separations[5];
	}

	/**
	 * @param separation the separations array 5 element to set
	 */
	public void setSeparation5(String separation) {
		this.separations[5] = separation;
	}
	
	
	/**
	 * @return the separations array 6 element
	 */
	public String getSeparation6() {
		return separations[6];
	}

	/**
	 * @param separation the separations array 6 element to set
	 */
	public void setSeparation6(String separation) {
		this.separations[6] = separation;
	}
	
	
	/**
	 * @return the separations array 7 element
	 */
	public String getSeparation7() {
		return separations[7];
	}

	/**
	 * @param separation the separations array 7 element to set
	 */
	public void setSeparation7(String separation) {
		this.separations[7] = separation;
	}
	
	
	/**
	 * @return the separations array 8 element
	 */
	public String getSeparation8() {
		return separations[8];
	}

	/**
	 * @param separation the separations array 8 element to set
	 */
	public void setSeparation8(String separation) {
		this.separations[8] = separation;
	}
	
	public static AfpSeparationDomainModel getModel(Node n, String type) {

		try {
			String name = (String) n.getProperty(INeoConstants.PROPERTY_NAME_NAME);
			String[] separations = (String[]) n.getProperty(type);
			
			AfpSeparationDomainModel model = new AfpSeparationDomainModel();

			model.setName(name);
			model.setSeparations(separations);
		
			return model;
		} catch (Exception e) {
			return null;
		}
	}
}
