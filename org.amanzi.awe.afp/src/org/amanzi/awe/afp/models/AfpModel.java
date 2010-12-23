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
	boolean[] availableNCCs = new boolean[8];
	boolean[] availableBCCs = new boolean[8];
	
	//Page 3 params
	Vector<AfpFrequencyDomainModel> freqDomains;
	Set TRXDomain;
	
	//Page 4 params
	Vector<AfpHoppingMALDomainModel> malDomains;
	
	//Page 5 params
	Vector<AfpSeparationDomainModel> siteSeparationDomains;
	Vector<AfpSeparationDomainModel> sectorSeparationDomains;
	
	//Page 6 params
	//Constants defining the index of Serving-Interfering pair in the scaling rules arrays
	//NHBB: Non/BB TCH
	//SFH: SY TCH
	public final static int BCCHBCCH = 0;
	public final static int BCCHNHBB = 1;
	public final static int BCCHSFH = 2;
	public final static int NHBBBCCH = 3;
	public final static int NHBBNHBB = 4;
	public final static int NHBBSFH = 5;
	public final static int SFHBCCH = 6;
	public final static int SFHNHBB = 7;
	public final static int SFHSFH = 8;
	
	//scaling rules arrays with default values
	float[] sectorSeparation = new float[]{100, 100, 100, 100, 100, 100, 100, 100, 100};
	float[] siteSeparation = new float[]{100, 70, 50, 70, 50, 30, 70, 50, 20};
	float[] coInterference = new float[]{1, 0.7f, 0.5f, 0.7f, 0.5f, 0.3f, 0.7f, 0.3f, 0.2f};
	float[] adjInterference = new float[]{1, 0.7f, 0.5f, 0.7f, 0.5f, 0.3f, 0.7f, 0.3f, 0.2f};
	float[] coNeighbor = new float[]{1, 0.3f, 0.2f, 0, 0, 0, 0, 0, 0};
	float[] adjNeighbor = new float[]{1, 0.1f, 0, 0, 0, 0, 0, 0, 0};
	float[] coTriangulation = new float[]{1, 0, 0, 0, 0, 0, 0, 0, 0};
	float[] adjTriangulation = new float[]{1, 0, 0, 0, 0, 0, 0, 0, 0};
	float[] coShadowing = new float[]{1, 0, 0, 0, 0, 0, 0, 0, 0};
	float[] adjShadowing = new float[]{1, 0, 0, 0, 0, 0, 0, 0, 0};
	
	
	public AfpModel() {
		for(int i=0;i<this.availableBCCs.length;i++) {
			this.availableBCCs[i] = true;
		}
		for(int i=0;i<this.availableNCCs.length;i++) {
			this.availableNCCs[i] = true;
		}
	}





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
	public Vector<AfpFrequencyDomainModel> getFreqDomains() {
		return freqDomains;
	}





	/**
	 * @param freqDomains the freqDomains to set
	 */
	public void setFreqDomains(Vector<AfpFrequencyDomainModel> freqDomains) {
		this.freqDomains = freqDomains;
	}
	
	/**
	 * @return the malDomains
	 */
	public Vector<AfpHoppingMALDomainModel> getMalDomains() {
		return malDomains;
	}





	/**
	 * @return the siteSeparationDomains
	 */
	public Vector<AfpSeparationDomainModel> getSiteSeparationDomains() {
		return siteSeparationDomains;
	}





	/**
	 * @param siteSeparationDomains the siteSeparationDomains to set
	 */
	public void setSiteSeparationDomains(
			Vector<AfpSeparationDomainModel> siteSeparationDomains) {
		this.siteSeparationDomains = siteSeparationDomains;
	}





	/**
	 * @return the sectorSeparationDomains
	 */
	public Vector<AfpSeparationDomainModel> getSectorSeparationDomains() {
		return sectorSeparationDomains;
	}





	/**
	 * @param sectorSeparationDomains the sectorSeparationDomains to set
	 */
	public void setSectorSeparationDomains(
			Vector<AfpSeparationDomainModel> sectorSeparationDomains) {
		this.sectorSeparationDomains = sectorSeparationDomains;
	}





	/**
	 * @param malDomains the malDomains to set
	 */
	public void setMalDomains(Vector<AfpHoppingMALDomainModel> malDomains) {
		this.malDomains = malDomains;
	}





	/**
	 * @return the sectorSeparation
	 */
	public float[] getSectorSeparation() {
		return sectorSeparation;
	}





	/**
	 * @param sectorSeparation the sectorSeparation to set
	 */
	public void setSectorSeparation(float[] sectorSeparation) {
		this.sectorSeparation = sectorSeparation;
	}





	/**
	 * @return the siteSeparation
	 */
	public float[] getSiteSeparation() {
		return siteSeparation;
	}





	/**
	 * @param siteSeparation the siteSeparation to set
	 */
	public void setSiteSeparation(float[] siteSeparation) {
		this.siteSeparation = siteSeparation;
	}





	/**
	 * @return the coInterference
	 */
	public float[] getCoInterference() {
		return coInterference;
	}





	/**
	 * @param coInterference the coInterference to set
	 */
	public void setCoInterference(float[] coInterference) {
		this.coInterference = coInterference;
	}





	/**
	 * @return the adjInterference
	 */
	public float[] getAdjInterference() {
		return adjInterference;
	}





	/**
	 * @param adjInterference the adjInterference to set
	 */
	public void setAdjInterference(float[] adjInterference) {
		this.adjInterference = adjInterference;
	}





	/**
	 * @return the coNeighbor
	 */
	public float[] getCoNeighbor() {
		return coNeighbor;
	}





	/**
	 * @param coNeighbor the coNeighbor to set
	 */
	public void setCoNeighbor(float[] coNeighbor) {
		this.coNeighbor = coNeighbor;
	}





	/**
	 * @return the adjNeighbor
	 */
	public float[] getAdjNeighbor() {
		return adjNeighbor;
	}





	/**
	 * @param adjNeighbor the adjNeighbor to set
	 */
	public void setAdjNeighbor(float[] adjNeighbor) {
		this.adjNeighbor = adjNeighbor;
	}





	/**
	 * @return the coTriangulation
	 */
	public float[] getCoTriangulation() {
		return coTriangulation;
	}





	/**
	 * @param coTriangulation the coTriangulation to set
	 */
	public void setCoTriangulation(float[] coTriangulation) {
		this.coTriangulation = coTriangulation;
	}





	/**
	 * @return the adjTriangulation
	 */
	public float[] getAdjTriangulation() {
		return adjTriangulation;
	}





	/**
	 * @param adjTriangulation the adjTriangulation to set
	 */
	public void setAdjTriangulation(float[] adjTriangulation) {
		this.adjTriangulation = adjTriangulation;
	}





	/**
	 * @return the coShadowing
	 */
	public float[] getCoShadowing() {
		return coShadowing;
	}





	/**
	 * @param coShadowing the coShadowing to set
	 */
	public void setCoShadowing(float[] coShadowing) {
		this.coShadowing = coShadowing;
	}





	/**
	 * @return the adjShadowing
	 */
	public float[] getAdjShadowing() {
		return adjShadowing;
	}





	/**
	 * @param adjShadowing the adjShadowing to set
	 */
	public void setAdjShadowing(float[] adjShadowing) {
		this.adjShadowing = adjShadowing;
	}





	public void addFreqDomain(AfpFrequencyDomainModel freqDomain){
		if (freqDomains == null){
			freqDomains = new Vector<AfpFrequencyDomainModel>();
		}
		freqDomains.add(freqDomain);
	}
	
	public void deleteFreqDomain(AfpFrequencyDomainModel freqDomain){
		if (freqDomains == null){
			return;
		}
		freqDomains.remove(freqDomain);
	}
	
	public AfpFrequencyDomainModel findFreqDomain(String domainName){
		for(AfpFrequencyDomainModel freqDomain : freqDomains){
			if (freqDomain.getName().equals(domainName))
				return freqDomain;
		}
		return null;
	}
	
	public String[] getAllFrequencyDomainNames(){
		String[] names = new String[freqDomains.size()];
		int i = 0;
		for(AfpFrequencyDomainModel freqDomain : freqDomains){
			names[i] = freqDomain.getName();
			i++;	
		}
		return names;
	}
	
	public String[] getAvailableBands(){
		int length = 0;
		for (boolean isEnabled : frequencyBands){
			if (isEnabled)
				length++;
		}
		String[] bands = new String[length];
		
		int i = 0;
		if (frequencyBands[0]) {
			bands[i] = "900";
			i++;
		}
		if (frequencyBands[1]) {
			bands[i] = "1800";
			i++;
		}
		if (frequencyBands[2]) {
			bands[i] = "850";
			i++;
		}
		if (frequencyBands[3]) {
			bands[i] = "1900";
		}
		
		return bands;
	}
	
	public void addMALDomain(AfpHoppingMALDomainModel malDomain){
		if (malDomains == null)
			malDomains = new Vector<AfpHoppingMALDomainModel>();
		malDomains.add(malDomain);
	}
	
	public void deleteMALDomain(AfpHoppingMALDomainModel malDomain){
		if (malDomains == null){
			//TODO error handling
		}
			
		malDomains.remove(malDomain);
	}
	
	public AfpHoppingMALDomainModel findMALDomain(String domainName){
		for(AfpHoppingMALDomainModel malDomain : malDomains){
			if (malDomain.getName().equals(domainName))
				return malDomain;
		}
		return null;
	}
	
	public String[] getAllMALDomainNames(){
		String[] names = new String[malDomains.size()];
		int i = 0;
		for(AfpHoppingMALDomainModel malDomain : malDomains){
			names[i] = malDomain.getName();
			i++;	
		}
		return names;
	}
	
	public void addSiteSeparationDomain(AfpSeparationDomainModel separationDomain){
		if (siteSeparationDomains == null)
			siteSeparationDomains = new Vector<AfpSeparationDomainModel>();
		siteSeparationDomains.add(separationDomain);
	}
	
	public void deleteSiteSeparationDomain(AfpSeparationDomainModel separationDomain){
		if (separationDomain == null){
			//TODO error handling
		}
			
		siteSeparationDomains.remove(separationDomain);
	}
	
	public AfpSeparationDomainModel findSiteSeparationDomain(String domainName){
		for(AfpSeparationDomainModel separationDomain : siteSeparationDomains){
			if (separationDomain.getName().equals(domainName))
				return separationDomain;
		}
		return null;
	}
	
	public String[] getAllSiteSeparationDomainNames(){
		String[] names = new String[siteSeparationDomains.size()];
		int i = 0;
		for(AfpSeparationDomainModel separationDomain : siteSeparationDomains){
			names[i] = separationDomain.getName();
			i++;	
		}
		return names;
	}
	
	
	public void addSectorSeparationDomain(AfpSeparationDomainModel separationDomain){
		if (sectorSeparationDomains == null)
			sectorSeparationDomains = new Vector<AfpSeparationDomainModel>();
		sectorSeparationDomains.add(separationDomain);
	}
	
	public void deleteSectorSeparationDomain(AfpSeparationDomainModel separationDomain){
		if (separationDomain == null){
			//TODO error handling
		}
			
		sectorSeparationDomains.remove(separationDomain);
	}
	
	public AfpSeparationDomainModel findSectorSeparationDomain(String domainName){
		for(AfpSeparationDomainModel separationDomain : sectorSeparationDomains){
			if (separationDomain.getName().equals(domainName))
				return separationDomain;
		}
		return null;
	}
	
	public String[] getAllSectorSeparationDomainNames(){
		String[] names = new String[sectorSeparationDomains.size()];
		int i = 0;
		for(AfpSeparationDomainModel separationDomain : sectorSeparationDomains){
			names[i] = separationDomain.getName();
			i++;	
		}
		return names;
	}
	
	public void setInterferenceMatrixArrays(float[][] array){
		coInterference = array[0];
		adjInterference = array[1];
		coNeighbor = array[2];
		adjNeighbor = array[3];
		coTriangulation = array[4];
		adjTriangulation = array[5];
		coShadowing = array[6];
		adjShadowing = array[7];
	}
	
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Optimization Parameters:\n" );
		sb.append("  Frequencies: ");
		sb.append(isOptimizeFrequency() ? "Yes\n" : "No\n");
		
		for (float value : coNeighbor){
			sb.append(Float.toString(value) + " ");
		}
		
		return sb.toString();
	}
 

}
