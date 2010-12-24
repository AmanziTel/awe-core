package org.amanzi.awe.afp.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.amanzi.awe.afp.wizards.AfpLoadNetworkPage;
import org.amanzi.awe.afp.wizards.AfpWizardUtils;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

public class AfpModel {

	protected Node datasetNode;
	protected Node afpNode;
	private final GraphDatabaseService service = NeoServiceProviderUi.getProvider().getService();
	private static final String AFP_NODE_NAME = "afp-dataset";

	private HashMap<String, Node> networkNodes;
	private HashMap<String, Node> afpNodes;

	boolean optimizeFrequency = true;
	boolean optimizeBSIC = true;
	boolean optimizeHSN = true;
	boolean optimizeMAIO = true;
	
	public static final String[] BAND_NAMES = { "900", "1800", "850", "1900" };
	public static final int BAND_900=0;
	public static final int BAND_1800=1;
	public static final int BAND_850=2;
	public static final int BAND_1900=3;
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
	String availableFreq[] = new String[4];
	int availableNCCs = 0xff;
	int availableBCCs = 0xff;
	
	//Page 3 params
	Vector<AfpFrequencyDomainModel> freqDomains = new Vector<AfpFrequencyDomainModel>();
	Set TRXDomain;
	
	//Page 4 params
	Vector<AfpHoppingMALDomainModel> malDomains= new Vector<AfpHoppingMALDomainModel>();
	
	//Page 5 params
	Vector<AfpSeparationDomainModel> siteSeparationDomains= new Vector<AfpSeparationDomainModel>();
	Vector<AfpSeparationDomainModel> sectorSeparationDomains= new Vector<AfpSeparationDomainModel>();
	
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


	public void setSelectNetworkDataSetName(String datasetName) {
    	datasetNode = networkNodes.get(datasetName);
    	if(datasetNode != null) {
    		loadAfpDataSet();
    	}
	}

    public String[] getNetworkDatasets() {
        networkNodes = new HashMap<String, Node>();
        for (Node root : NeoUtils.getAllRootTraverser(service, null)) {
        	
            if (NodeTypes.NETWORK.checkNode(root)) {
                networkNodes.put(NeoUtils.getNodeName(root, service), root);
            }
        }
        return networkNodes.keySet().toArray(new String[0]);
    }
	/**
     * Gets the networ datasets.
     * 
     * @return the network datasets
     */
    
    public String[] getAfpDatasets(Node networkNode) {
        afpNodes = new HashMap<String, Node>();
        Transaction tx = service.beginTx();
        try {
        	Traverser traverser = networkNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){

				@Override
				public boolean isReturnableNode(TraversalPosition currentPos) {
					if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.AFP.getId()))
						return true;
					return false;
				}
        		
        	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
            for (Node afpNode : traverser) {
            	
                if (NodeTypes.AFP.checkNode(afpNode)) {
                    afpNodes.put(NeoUtils.getNodeName(afpNode, service), afpNode);
                }
            }
        } finally {
            tx.finish();
        }
        return afpNodes.keySet().toArray(new String[0]);
    }
    public boolean hasValidNetworkDataset() {
	    if (datasetNode == null) {
	            return false;
	    }
	    return  true;
    }

	/**
	 * @param afpNode the afpNode to set
	 */
	public void loadAfpDataSet() {
		getAfpDatasets(datasetNode);

        if(afpNodes != null) {
        	afpNode = afpNodes.get(AFP_NODE_NAME);
        }
		loadUserData();
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


	public boolean isFrequencyBandAvaliable(int band) {
		if(band >=0 && band <=4) {
			return frequencyBands[band];
		}		
		return false;
	}



	/**
	 * @param frequencyBands the frequencyBands to set
	 */
	public void setFrequencyBands(boolean[] frequencyBands) {
		this.frequencyBands = frequencyBands;
		for(int i=0;i<4;i++) {
			if(!this.frequencyBands[i]) {
				this.availableFreq[i] ="";
			}
		}
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
	public String getAvailableFreq(int band) {
		if(band >=0 && band <=4) {
			if(availableFreq[band] != null) {
				return availableFreq[band];
			}
		}
		return "";
	}





	/**
	 * @param availableFreq900 the availableFreq900 to set
	 */
	public void setAvailableFreq(int band, String freq) {
		if(band >=0 && band <=4) {
			availableFreq[band] = freq;
		}
	}


	/**
	 * @return the availableNCCs
	 */
	public boolean[] getAvailableNCCs() {
		boolean ret[] = new boolean[] { true,true,true,true,true,true,true,true};
		for(int i=0; i<8;i++) {
			ret[i] = ((availableNCCs & (1 << i)) >0);
		}
		return ret;
	}





	/**
	 * @param availableNCCs the availableNCCs to set
	 */
	public void setAvailableNCCs(boolean[] availableNCCs) {
		int n =0;
		for(int i=0; i< availableNCCs.length;i++) {
			if(availableNCCs[i]) {
				n = n | (1 << i);
			}
		}
		this.availableNCCs = n;
	}





	/**
	 * @return the availableBCCs
	 */
	public boolean[] getAvailableBCCs() {
		boolean ret[] = new boolean[] { true,true,true,true,true,true,true,true};
		for(int i=0; i<8;i++) {
			ret[i] = ((availableBCCs & (1 << i)) >0);
		}
		return ret;
	}





	/**
	 * @param availableBCCs the availableBCCs to set
	 */
	public void setAvailableBCCs(boolean[] availableBCCs) {
		int n =0;
		for(int i=0; i< availableBCCs.length;i++) {
			if(availableBCCs[i]) {
				n = n | (1 << i);
			}
		}
		this.availableBCCs = n;
	}

	private void addFreeFrequencyDomain() {
		if(freqDomains.size() !=0) {
			// remove free domains
			for(int i=0;i<freqDomains.size();i++) {
				AfpFrequencyDomainModel d = freqDomains.elementAt(i);
				if(d.isFree()) {
					freqDomains.remove(i);
				}
			}
		}
		for(int i=0; i< frequencyBands.length;i++) {
			if(frequencyBands[i]) {
				// add free domains
				AfpFrequencyDomainModel d = new AfpFrequencyDomainModel();
				d.setName("Free " + BAND_NAMES[i]);
				d.setBand(BAND_NAMES[i]);
				d.setFree(true);
				String f[] = new String[1];
				f[0] = "1";
				d.setFrequencies(f);
				if(freqDomains.size() > i) {
					freqDomains.insertElementAt(d, i);
				} else {
					freqDomains.add(d);
				}
			}
		}
	}

	/**
	 * @return the freqDomains
	 */
	public Vector<AfpFrequencyDomainModel> getFreqDomains() {
		addFreeFrequencyDomain();
		return freqDomains;
	}





	/**
	 * @param freqDomains the freqDomains to set
	 */
	private void setFreqDomains(Vector<AfpFrequencyDomainModel> freqDomains) {
		this.freqDomains = freqDomains;
	}
	
	private void addDefaultMalDomains() {
		boolean add = true;
		if(malDomains.size() !=0) {
			// remove free domains
			for(int i=0;i<malDomains.size();i++) {
				AfpHoppingMALDomainModel d = malDomains.elementAt(i);
				if(d.isFree()) {
					add = false;
					break;
				} else if("Default MAL".compareTo(d.getName())==0) {
					d.setFree(true);
					add= false;
				}
			}
		}
		if(add) {
			// add free domains
			AfpHoppingMALDomainModel d = new AfpHoppingMALDomainModel();
			d.setName("Default MAL");
			d.setFree(true);
			malDomains.insertElementAt(d, 0);
		}
	}
	
	/**
	 * @return the malDomains
	 */
	public Vector<AfpHoppingMALDomainModel> getMalDomains() {
		addDefaultMalDomains();
		return malDomains;
	}


	private void addDefaultSiteSeparationDomains() {
		boolean add = true;
		if(siteSeparationDomains.size() !=0) {
			// remove free domains
			for(int i=0;i<siteSeparationDomains.size();i++) {
				AfpSeparationDomainModel d = siteSeparationDomains.elementAt(i);
				if(d.isFree()) {
					add = false;
					break;
				} else if("Default Separations".compareTo(d.getName())==0) {
					d.setFree(true);
					add = false;
					break;
				}
			}
		}
		if(add) {
			// add free domains
			AfpSeparationDomainModel d = new AfpSeparationDomainModel();
			d.setName("Default Separations");
			d.setFree(true);
			siteSeparationDomains.insertElementAt(d, 0);
		}
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
	private void setSiteSeparationDomains(Vector<AfpSeparationDomainModel> siteSeparationDomains) {
		this.siteSeparationDomains = siteSeparationDomains;
	}


	private void addDefaultSectorSeparationDomains() {
		boolean add = true;
		if(sectorSeparationDomains.size() !=0) {
			// remove free domains
			for(int i=0;i<sectorSeparationDomains.size();i++) {
				AfpSeparationDomainModel d = sectorSeparationDomains.elementAt(i);
				if(d.isFree()) {
					add = false;
					break;
				} else if("Default Separations".compareTo(d.getName()) ==0 ) {
					d.setFree(true);
					add = false;
					break;
				}
			}
		}
		if(add) {
			// add free domains
			AfpSeparationDomainModel d = new AfpSeparationDomainModel();
			d.setName("Default Separations");
			d.setFree(true);
			sectorSeparationDomains.insertElementAt(d, 0);
		}
	}
	


	/**
	 * @return the sectorSeparationDomains
	 */
	public Vector<AfpSeparationDomainModel> getSectorSeparationDomains() {
		addDefaultSectorSeparationDomains();
		return sectorSeparationDomains;
	}





	/**
	 * @param sectorSeparationDomains the sectorSeparationDomains to set
	 */
	private void setSectorSeparationDomains(
			Vector<AfpSeparationDomainModel> sectorSeparationDomains) {
		this.sectorSeparationDomains = sectorSeparationDomains;
	}





	/**
	 * @param malDomains the malDomains to set
	 */
	private void setMalDomains(Vector<AfpHoppingMALDomainModel> malDomains) {
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
			return;
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
	
	
	/**
	 * Write all user selected data to database
	 */
	public void saveUserData() {
		
		Transaction tx = this.service.beginTx();
		try {
			if (afpNode == null) {
				afpNode = service.createNode();
				NodeTypes.AFP.setNodeType(afpNode, service);
				NeoUtils.setNodeName(afpNode, AFP_NODE_NAME , service);
				datasetNode.createRelationshipTo(afpNode, NetworkRelationshipTypes.CHILD);
			}
			afpNode.setProperty(INeoConstants.AFP_OPTIMIZATION_PARAMETERS, getOptimizationParameters());
			afpNode.setProperty(INeoConstants.AFP_FREQUENCY_BAND, getFrequencyBands());
			afpNode.setProperty(INeoConstants.AFP_CHANNEL_TYPE, getChanneltypes());
			afpNode.setProperty(INeoConstants.AFP_ANALYZE_CURRENT, isAnalyzeCurrentFreqAllocation());
			if (getAvailableFreq(BAND_900) != null)
				afpNode.setProperty( INeoConstants.AFP_AVAILABLE_FREQUENCIES_900, getAvailableFreq(BAND_900));
			if (getAvailableFreq(BAND_1800) != null)
				afpNode.setProperty(INeoConstants.AFP_AVAILABLE_FREQUENCIES_1800, getAvailableFreq(BAND_1800));
			
			if (getAvailableFreq(BAND_850) != null)
				afpNode.setProperty( INeoConstants.AFP_AVAILABLE_FREQUENCIES_850, getAvailableFreq(BAND_850));
			if (getAvailableFreq(BAND_1900) != null)
				afpNode.setProperty( INeoConstants.AFP_AVAILABLE_FREQUENCIES_1900, getAvailableFreq(BAND_1900));
			afpNode.setProperty(INeoConstants.AFP_AVAILABLE_BCCS, this.availableBCCs);
			afpNode.setProperty(INeoConstants.AFP_AVAILABLE_NCCS, this.availableNCCs);

			afpNode.setProperty(INeoConstants.AFP_SECTOR_SCALING_RULES, getSectorSeparation());
			afpNode.setProperty(INeoConstants.AFP_SITE_SCALING_RULES, getSiteSeparation());
			afpNode.setProperty(INeoConstants.AFP_CO_INTERFERENCE_VALUES, getCoInterference());
			afpNode.setProperty(INeoConstants.AFP_ADJ_INTERFERENCE_VALUES,getAdjInterference());
			afpNode.setProperty(INeoConstants.AFP_CO_NEIGHBOR_VALUES, getCoNeighbor());
			afpNode.setProperty(INeoConstants.AFP_ADJ_NEIGHBOR_VALUES, getAdjNeighbor());
			afpNode.setProperty(INeoConstants.AFP_CO_TRIANGULATION_VALUES, getCoTriangulation());
			afpNode.setProperty(INeoConstants.AFP_ADJ_TRIANGULATION_VALUES, getAdjTriangulation());
			afpNode.setProperty(INeoConstants.AFP_CO_SHADOWING_VALUES, getCoShadowing());
			afpNode.setProperty(INeoConstants.AFP_ADJ_SHADOWING_VALUES, getAdjShadowing());
			
			// remove all chid nodes before storing new ones
			Traverser traverser = afpNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){
				@Override
				public boolean isReturnableNode(TraversalPosition currentPos) {
					if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.AFP_DOMAIN.getId())) 
						return true;
					return false;
				}
	    		
	    	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
			

			for (Node n : traverser) {
				Iterable<Relationship> relationsI = n.getRelationships();
				if(relationsI != null) {
					Iterator<Relationship> reations = relationsI.iterator();
					while(reations.hasNext()) {
						Relationship r = reations.next();
						r.delete();
					}
				}
				n.delete();
			}

			for (AfpFrequencyDomainModel frequencyModel : getFreqDomains()) {
				if(!frequencyModel.isFree())
					createFrequencyDomainNode(afpNode, frequencyModel, service);
			}

			for (AfpHoppingMALDomainModel malModel : getMalDomains()) {
				createHoppingMALDomainNode(afpNode, malModel,
						service);
			}

			for (AfpSeparationDomainModel separationsModel : getSectorSeparationDomains()) {
				createSectorSeparationDomainNode(afpNode,
						separationsModel, service);
			}

			for (AfpSeparationDomainModel separationsModel : getSiteSeparationDomains()) {
				createSiteSeparationDomainNode(afpNode,
						separationsModel, service);
			}

		} catch (Exception e) {
			AweConsolePlugin.exception(e);
		} finally {
			tx.finish();
		}
	}
	
	private void loadUserData() {
		
		try {
			if (afpNode == null) {
				return;
			}
			try {
				boolean opt[] = (boolean[])afpNode.getProperty(INeoConstants.AFP_OPTIMIZATION_PARAMETERS);
				if(opt != null) {
					if(opt.length >=4) {
						this.optimizeFrequency = opt[0];
						this.optimizeBSIC = opt[1];
						this.optimizeHSN = opt[2];
						this.optimizeMAIO = opt[3];
					}
				}
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setFrequencyBands((boolean[])afpNode.getProperty(INeoConstants.AFP_FREQUENCY_BAND));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setChanneltypes((boolean[])afpNode.getProperty(INeoConstants.AFP_CHANNEL_TYPE));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAnalyzeCurrentFreqAllocation((Boolean)afpNode.getProperty(INeoConstants.AFP_ANALYZE_CURRENT));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAvailableFreq(BAND_900, (String)afpNode.getProperty(INeoConstants.AFP_AVAILABLE_FREQUENCIES_900));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAvailableFreq(BAND_1800,(String)afpNode.getProperty(INeoConstants.AFP_AVAILABLE_FREQUENCIES_1800));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAvailableFreq(BAND_850,(String)afpNode.getProperty(INeoConstants.AFP_AVAILABLE_FREQUENCIES_850));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAvailableFreq(BAND_1900,(String)afpNode.getProperty(INeoConstants.AFP_AVAILABLE_FREQUENCIES_1900));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				Integer n = (Integer)afpNode.getProperty(INeoConstants.AFP_AVAILABLE_BCCS);
				this.availableBCCs =n.intValue();				
			} catch(Exception e) {
				// no property sent 
			}
			try {
				Integer n = (Integer)afpNode.getProperty(INeoConstants.AFP_AVAILABLE_NCCS);
				this.availableNCCs =n.intValue();				
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setSectorSeparation((float[])afpNode.getProperty(INeoConstants.AFP_SECTOR_SCALING_RULES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setSiteSeparation((float[])afpNode.getProperty(INeoConstants.AFP_SITE_SCALING_RULES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setCoInterference((float[])afpNode.getProperty(INeoConstants.AFP_CO_INTERFERENCE_VALUES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAdjInterference((float[])afpNode.getProperty(INeoConstants.AFP_ADJ_INTERFERENCE_VALUES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setCoNeighbor((float[])afpNode.getProperty(INeoConstants.AFP_CO_NEIGHBOR_VALUES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAdjNeighbor((float[])afpNode.getProperty(INeoConstants.AFP_ADJ_NEIGHBOR_VALUES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setCoTriangulation((float[])afpNode.getProperty(INeoConstants.AFP_CO_TRIANGULATION_VALUES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAdjTriangulation((float[])afpNode.getProperty(INeoConstants.AFP_ADJ_TRIANGULATION_VALUES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setCoShadowing((float[])afpNode.getProperty(INeoConstants.AFP_CO_SHADOWING_VALUES));
			} catch(Exception e) {
				// no property sent 
			}
			try {
				setAdjShadowing((float[])afpNode.getProperty(INeoConstants.AFP_ADJ_SHADOWING_VALUES));
			} catch(Exception e) {
				// no property sent 
			}
			
			loadDomainNode(afpNode);
/*
			for (AfpHoppingMALDomainModel malModel : getMalDomains()) {
				createHoppingMALDomainNode(afpNode, malModel,
						service);
			}

			for (AfpSeparationDomainModel separationsModel : getSectorSeparationDomains()) {
				createSectorSeparationDomainNode(afpNode,
						separationsModel, service);
			}

			for (AfpSeparationDomainModel separationsModel : getSiteSeparationDomains()) {
				createSiteSeparationDomainNode(afpNode,
						separationsModel, service);
			}*/

		} catch (Exception e) {
			AweConsolePlugin.exception(e);
		} finally {
		}
	}
	
	public void createFrequencyDomainNode(Node afpNode, AfpFrequencyDomainModel domainModel, GraphDatabaseService service){
		Node frequencyNode = findOrCreateDomainNode(afpNode, INeoConstants.AFP_DOMAIN_NAME_FREQUENCY, domainModel.getName(), service);
        
        frequencyNode.setProperty(INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, domainModel.getBand());
        frequencyNode.setProperty(INeoConstants.AFP_PROPERTY_FREQUENCIES_NAME, domainModel.getFrequencies());
	}
	
	public void createHoppingMALDomainNode(Node afpNode, AfpHoppingMALDomainModel domainModel, GraphDatabaseService service){
		Node malNode = findOrCreateDomainNode(afpNode, INeoConstants.AFP_DOMAIN_NAME_MAL, domainModel.getName(), service);
		malNode.setProperty(INeoConstants.AFP_PROPERTY_MAL_SIZE_NAME, domainModel.getMALSize());
	}
	
	public void createSectorSeparationDomainNode(Node afpNode, AfpSeparationDomainModel domainModel, GraphDatabaseService service){
		Node separationNode = findOrCreateDomainNode(afpNode, INeoConstants.AFP_DOMAIN_NAME_SECTOR_SEPARATION, domainModel.getName(), service);
		separationNode.setProperty(INeoConstants.AFP_PROPERTY_SEPARATIONS_NAME, domainModel.getSeparations());
	}
	
	public void createSiteSeparationDomainNode(Node afpNode, AfpSeparationDomainModel domainModel, GraphDatabaseService service){
		Node separationNode = findOrCreateDomainNode(afpNode, INeoConstants.AFP_DOMAIN_NAME_SITE_SEPARATION, domainModel.getName(), service);
		separationNode.setProperty(INeoConstants.AFP_PROPERTY_SEPARATIONS_NAME, domainModel.getSeparations());
	}
	
	public void deleteDomainNode(Node afpNode, String domain, String name, GraphDatabaseService service){
		//TODO implement this method
	}

	public Node findOrCreateDomainNode(Node afpNode, String domain, String name, GraphDatabaseService service){
		Node domainNode = null;
		
		Traverser traverser = afpNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){

			@Override
			public boolean isReturnableNode(TraversalPosition currentPos) {
				if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.AFP_DOMAIN.getId()))
					return true;
				return false;
			}
    		
    	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
		
		for (Node node : traverser) {
        	if (node.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(name) &&
        			node.getProperty(INeoConstants.AFP_PROPERTY_DOMAIN_NAME).equals(domain))
        		domainNode = node;
        }
		
		if (domainNode == null){
			domainNode = service.createNode();
    		NodeTypes.AFP_DOMAIN.setNodeType(domainNode, service);
            NeoUtils.setNodeName(domainNode, name, service);
            domainNode.setProperty(INeoConstants.AFP_PROPERTY_DOMAIN_NAME, domain);
            afpNode.createRelationshipTo(domainNode, NetworkRelationshipTypes.CHILD);
		}
		
		return domainNode;

	}

	private void loadDomainNode(Node afpNode){
		Node domainNode = null;
		
		Traverser traverser = afpNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){

			@Override
			public boolean isReturnableNode(TraversalPosition currentPos) {
				if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.AFP_DOMAIN.getId())) 
					return true;
				return false;
			}
    		
    	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
		

		for (Node node : traverser) {
        	if (node.getProperty(INeoConstants.AFP_PROPERTY_DOMAIN_NAME,"").equals(INeoConstants.AFP_DOMAIN_NAME_FREQUENCY)) {
        		// frequency type domain
        		AfpFrequencyDomainModel m = AfpFrequencyDomainModel.getModel(node);
        		if(m != null) {
        			freqDomains.add(m);
        		}
        	} else  if (node.getProperty(INeoConstants.AFP_PROPERTY_DOMAIN_NAME,"").equals(INeoConstants.AFP_DOMAIN_NAME_MAL)) {
        		// frequency type domain
        		AfpHoppingMALDomainModel m = AfpHoppingMALDomainModel.getModel(node);
        		if(m != null) {
        			malDomains.add(m);
        		}
        	} else  if (node.getProperty(INeoConstants.AFP_PROPERTY_DOMAIN_NAME,"").equals(INeoConstants.AFP_DOMAIN_NAME_SECTOR_SEPARATION)) {
        		// frequency type domain
        		AfpSeparationDomainModel m = AfpSeparationDomainModel.getModel(node, INeoConstants.AFP_PROPERTY_SEPARATIONS_NAME);
        		if(m != null) {
        			sectorSeparationDomains.add(m);
        		}
        	} else  if (node.getProperty(INeoConstants.AFP_PROPERTY_DOMAIN_NAME,"").equals(INeoConstants.AFP_DOMAIN_NAME_SITE_SEPARATION)) {
        		// frequency type domain
        		AfpSeparationDomainModel m = AfpSeparationDomainModel.getModel(node,INeoConstants.AFP_PROPERTY_SEPARATIONS_NAME);
        		if(m != null) {
        			siteSeparationDomains.add(m);
        		}
        	}  
        }
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
