package org.amanzi.awe.afp.models;

import java.util.Set;
import java.util.Vector;

import org.neo4j.graphdb.Node;

public class AfpModel {
	
	//Page 0 params
	Node afpNode;
	
	//Page 1 params
	boolean optimizeFrequency = true;
	boolean optimizeBSIC = true;
	boolean optimizeHSN = true;
	boolean optimizeMAIO = true;
	
	/**
	 * 0- 900
	 * 1- 1800
	 * 2- 850
	 * 3- 1900
	 */
	boolean[] frequencyBands = new boolean[] {true, true, true, true};
	
	/**
	 * 0- BCCH
	 * 1- TCH Non/BB Hopping
	 * 2- TCH SY Hopping
	 */
	boolean[] channeltypes = new boolean[] {true, true, true};
	boolean analyzeCurrentFreqAllocation = true;

	//Page 2 params
	String availableFreq900;
	String availableFreq1800;
	String availableFreq850;
	String availableFreq1900;
	boolean[] availableNCCs;
	boolean[] availableBCCs;
	
	//Page 3 params
	Vector freqDomains;
	Set TRXDomain;
	
	
	
	/**
	 * Array rows: 
	 * 0-Selected sectors
	 * 1-Selected TRXs
	 * 2- BCCH TRXs
	 * 3- TCH Non/BB Hopping TRXs
	 * 4- TCH SY Hopping TRXs
	 * 
	 * Array columns:
	 * 0- 900
	 * 1- 1800
	 * 2- 850
	 * 3- 1900
	 * @return return an array
	 */
	public int[][] getSelectedCount(){
		//TODO: get the count from database based on the selected node and also update frequency band values based on that
		int[][] selectedArray = {{150,150,0,0},
							   {320,330,0,0},
							   {150,150,0,0},
							   {150,0,0,0},
							   {0,200,0,0}
		};
		return selectedArray;
	}





	/**
	 * @return the afpNode
	 */
	public Node getAfpNode() {
		return afpNode;
	}





	/**
	 * @param afpNode the afpNode to set
	 */
	public void setAfpNode(Node afpNode) {
		this.afpNode = afpNode;
	}





	/**
	 * @return the optimizeFrequency
	 */
	public boolean isOptimizeFrequency() {
		return optimizeFrequency;
	}





	/**
	 * @param optimizeFrequency the optimizeFrequency to set
	 */
	public void setOptimizeFrequency(boolean optimizeFrequency) {
		this.optimizeFrequency = optimizeFrequency;
	}





	/**
	 * @return the optimizeBSIC
	 */
	public boolean isOptimizeBSIC() {
		return optimizeBSIC;
	}





	/**
	 * @param optimizeBSIC the optimizeBSIC to set
	 */
	public void setOptimizeBSIC(boolean optimizeBSIC) {
		this.optimizeBSIC = optimizeBSIC;
	}





	/**
	 * @return the optimizeHsn
	 */
	public boolean isOptimizeHSN() {
		return optimizeHSN;
	}





	/**
	 * @param optimizeHsn the optimizeHsn to set
	 */
	public void setOptimizeHSN(boolean optimizeHSN) {
		this.optimizeHSN = optimizeHSN;
	}





	/**
	 * @return the optimizeMaio
	 */
	public boolean isOptimizeMAIO() {
		return optimizeMAIO;
	}





	/**
	 * @param optimizeMaio the optimizeMaio to set
	 */
	public void setOptimizeMAIO(boolean optimizeMAIO) {
		this.optimizeMAIO = optimizeMAIO;
	}

	
	/**
	 * Array: 
	 * 0- Frequencies
	 * 1- BSIC
	 * 2- HSN
	 * 3- MAIO
	 * @return boolean array containing all optimization params
	 */
	public boolean[] getOptimizationParameters(){
		return new boolean[]{isOptimizeFrequency(), isOptimizeBSIC(), isOptimizeHSN(), isOptimizeMAIO()}; 
	}



	/**
	 * @return the frequencyBands
	 */
	public boolean[] getFrequencyBands() {
		return frequencyBands;
	}





	/**
	 * @param frequencyBands the frequencyBands to set
	 */
	public void setFrequencyBands(boolean[] frequencyBands) {
		this.frequencyBands = frequencyBands;
	}





	/**
	 * @return the channeltypes
	 */
	public boolean[] getChanneltypes() {
		return channeltypes;
	}





	/**
	 * @param channeltypes the channeltypes to set
	 */
	public void setChanneltypes(boolean[] channeltypes) {
		this.channeltypes = channeltypes;
	}





	/**
	 * @return the analyzeCurrentFreqAllocation
	 */
	public boolean isAnalyzeCurrentFreqAllocation() {
		return analyzeCurrentFreqAllocation;
	}





	/**
	 * @param analyzeCurrentFreqAllocation the analyzeCurrentFreqAllocation to set
	 */
	public void setAnalyzeCurrentFreqAllocation(boolean analyzeCurrentFreqAllocation) {
		this.analyzeCurrentFreqAllocation = analyzeCurrentFreqAllocation;
	}





	/**
	 * @return the availableFreq900
	 */
	public String getAvailableFreq900() {
		return availableFreq900;
	}





	/**
	 * @param availableFreq900 the availableFreq900 to set
	 */
	public void setAvailableFreq900(String availableFreq900) {
		this.availableFreq900 = availableFreq900;
	}





	/**
	 * @return the availableFreq1800
	 */
	public String getAvailableFreq1800() {
		return availableFreq1800;
	}





	/**
	 * @param availableFreq1800 the availableFreq1800 to set
	 */
	public void setAvailableFreq1800(String availableFreq1800) {
		this.availableFreq1800 = availableFreq1800;
	}





	/**
	 * @return the availableFreq850
	 */
	public String getAvailableFreq850() {
		return availableFreq850;
	}





	/**
	 * @param availableFreq850 the availableFreq850 to set
	 */
	public void setAvailableFreq850(String availableFreq850) {
		this.availableFreq850 = availableFreq850;
	}





	/**
	 * @return the availableFreq1900
	 */
	public String getAvailableFreq1900() {
		return availableFreq1900;
	}





	/**
	 * @param availableFreq1900 the availableFreq1900 to set
	 */
	public void setAvailableFreq1900(String availableFreq1900) {
		this.availableFreq1900 = availableFreq1900;
	}





	/**
	 * @return the availableNCCs
	 */
	public boolean[] getAvailableNCCs() {
		return availableNCCs;
	}





	/**
	 * @param availableNCCs the availableNCCs to set
	 */
	public void setAvailableNCCs(boolean[] availableNCCs) {
		this.availableNCCs = availableNCCs;
	}





	/**
	 * @return the availableBCCs
	 */
	public boolean[] getAvailableBCCs() {
		return availableBCCs;
	}





	/**
	 * @param availableBCCs the availableBCCs to set
	 */
	public void setAvailableBCCs(boolean[] availableBCCs) {
		this.availableBCCs = availableBCCs;
	}





	/**
	 * @return the freqDomains
	 */
	public Vector getFreqDomains() {
		return freqDomains;
	}





	/**
	 * @param freqDomains the freqDomains to set
	 */
	public void setFreqDomains(Vector freqDomains) {
		this.freqDomains = freqDomains;
	}





	/**
	 * @return the tRXDomain
	 */
	public Set getTRXDomain() {
		return TRXDomain;
	}





	/**
	 * @param domain the tRXDomain to set
	 */
	public void setTRXDomain(Set domain) {
		TRXDomain = domain;
	}
 

}
