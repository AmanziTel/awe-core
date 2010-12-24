package org.amanzi.awe.afp.models;

import org.amanzi.neo.services.INeoConstants;
import org.neo4j.graphdb.Node;

public class AfpFrequencyDomainModel extends AfpDomainModel {

	String band;
	String[] frequencies;
	
	public static AfpFrequencyDomainModel getModel(Node n) {

		try {
			String name = (String) n.getProperty(INeoConstants.PROPERTY_NAME_NAME);
			String band = (String) n.getProperty(INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME);
			String[] frequencies = (String[]) n.getProperty(INeoConstants.AFP_PROPERTY_FREQUENCIES_NAME);
			
			AfpFrequencyDomainModel model = new AfpFrequencyDomainModel();

			model.setName(name);
			model.setBand(band);
			model.setFrequencies(frequencies);
		
			return model;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * @return the band
	 */
	public String getBand() {
		return band;
	}
	/**
	 * @param band the band to set
	 */
	public void setBand(String band) {
		this.band = band;
	}
	/**
	 * @return the frequencies
	 */
	public String[] getFrequencies() {
		return frequencies;
	}
	/**
	 * @param frequencies the frequencies to set
	 */
	public void setFrequencies(String[] frequencies) {
		this.frequencies = frequencies;
	}


	
}
