package org.amanzi.awe.afp.models;

public class AfpFrequencyDomainModel extends AfpDomainModel {

	String band;
	String[] frequencies;
	
	
	
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
