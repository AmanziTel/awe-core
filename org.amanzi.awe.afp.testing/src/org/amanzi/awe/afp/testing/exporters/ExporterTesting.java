
/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2010-2011, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.afp.testing.exporters;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.amanzi.awe.afp.exporters.AfpExporter;
import org.amanzi.awe.afp.filters.AfpColumnFilter;
import org.amanzi.awe.afp.filters.AfpRowFilter;
import org.amanzi.awe.afp.models.AfpDomainModel;
import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.amanzi.neo.loader.NeighbourLoader;
import org.amanzi.neo.loader.NetworkLoader;
import org.amanzi.testing.CommonTestUtil;
import org.amanzi.testing.LongRunning;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;


/**
 * <p>
 *Test case for AFP exporter 
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ExporterTesting {
    private static final String DATABASE_NAME = "afp_test";
    private static final String MAIN_DIR = "test";
    static CommonTestUtil util;
	private static AfpModel model= null;
	private static Node rootNode = null;
    @BeforeClass
    public static void init() throws IOException{
        util=new CommonTestUtil(DATABASE_NAME, MAIN_DIR);
        
        NeoServiceProviderUi.initProvider(util.getNeo());
        model = initalizeAfpModel();
    }

    private static AfpModel initalizeAfpModel() throws IOException {
    	if(model != null) 
    		return model;
    	
        CommonTestUtil.clearDirectory(new File(AfpExporter.tmpAfpFolder));
        
    	// import the test data
    	rootNode = loadNetwork("../org.amanzi.awe.afp.testing/files/exporter/Network.txt");

    	model = new AfpModel();
    	model.setDatasetNode(rootNode);
    	model.loadAfpDataSet();
    	// set goals
		model.setOptimizeFrequency(true);
		model.setOptimizeBSIC(false);
		model.setOptimizeHSN(false);
		model.setOptimizeMAIO(false);
		// avaliable bands
		model.setFrequencyBands(new boolean[]{true, false, false, false});
		model.setChanneltypes(new boolean[]{true, true, false});
		model.setAnalyzeCurrentFreqAllocation(true);
    	// avaliable resorces
		model.setAvailableNCCs(new boolean[] {true,true,true,true,true,true,true,true });
		model.setAvailableBCCs(new boolean[] {true,true,true,true,true,true,true,true });
		model.setAvailableFreq(AfpModel.BAND_900, "1-9");
		
		// add Frequency domains
		AfpFrequencyDomainModel fd = new AfpFrequencyDomainModel();
		fd.setName("Band_900");
		fd.setFree(false);
		fd.setBand("900");
		fd.setFrequencies(new String[] {"1-9"});
		AfpRowFilter rowFilter = new AfpRowFilter();
		AfpColumnFilter colFilter = new AfpColumnFilter("band", NodeTypes.TRX.getId());
		colFilter.addValue("900");
		rowFilter.addColumn(colFilter);
		colFilter = new AfpColumnFilter("bcch", NodeTypes.TRX.getId());
		colFilter.addValue("true");
		//rowFilter.addColumn(colFilter);
		fd.setFilters(rowFilter.toString());
		HashMap<String,AfpFrequencyDomainModel> freqDomains = new HashMap<String,AfpFrequencyDomainModel>();

		freqDomains.put("900", fd);
		model.setFreqDomains(freqDomains);
		
		model.saveUserData();

		return model;
    	
    }
    
    private static AfpModel add1800DomainToModel() throws IOException {
    	if(model == null) 
    		initalizeAfpModel();
    	
		// avaliable bands
		model.setFrequencyBands(new boolean[]{true, true, false, false});
    	
		// avaliable resorces
		model.setAvailableFreq(AfpModel.BAND_1800, "512-520");
		
		// add Frequency domains
		AfpFrequencyDomainModel fd = new AfpFrequencyDomainModel();
		fd.setName("Band_1800");
		fd.setFree(false);
		fd.setBand("1800");
		fd.setFrequencies(new String[] {"512-520"});
		AfpRowFilter rowFilter = new AfpRowFilter();
		AfpColumnFilter colFilter = new AfpColumnFilter("band", NodeTypes.TRX.getId());
		colFilter.addValue("1800");
		rowFilter.addColumn(colFilter);
		colFilter = new AfpColumnFilter("bcch", NodeTypes.TRX.getId());
		colFilter.addValue("true");
		//rowFilter.addColumn(colFilter);
		fd.setFilters(rowFilter.toString());
		HashMap<String,AfpFrequencyDomainModel> freqDomains = new HashMap<String,AfpFrequencyDomainModel>();
		for (AfpFrequencyDomainModel fdModels : model.getFreqDomains(false))
			freqDomains.put(fdModels.getBand(), fdModels);

		freqDomains.put("1800", fd);
		model.setFreqDomains(freqDomains);
		
		model.saveUserData();

		return model;
    	
    }
    
    private int getLineCnt(String filePath, String matchwith) throws Exception {
    	BufferedReader reader = new BufferedReader(new FileReader(filePath));
    	int lineCnt =0;
    	String line;
    	while((line = reader.readLine()) != null) {
    		if(matchwith != null) {
    			if(line.matches(matchwith)) {
        			lineCnt++;
    			}
    		} else {
    			lineCnt++;
    		}
    	}
    	//System.out.println("Line Count " + lineCnt);
    	return lineCnt;
    }
    private int getTrxCountForBand(Node root, String band, boolean filterInterferers) throws Exception {
    	
		Traverser traverser = root.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator(){

			@Override
			public boolean isReturnableNode(TraversalPosition currentPos) {
				if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.SECTOR.getId()) ||
						currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.SITE.getId()) ||
						currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.TRX.getId())){
					return true;
				}
				return false;
			}
    	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
		
		int cnt =0;
		for (Node node: traverser) {
			// add to unique properties
			if(node.getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.TRX.getId())){
				String b = (String)node.getProperty("band", "");
				if (b.contains(band)){
					if(filterInterferers) {
						// find the sector node
						Node sector = node.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
						// check if the sector has proxy sectors
						if(sector != null) {
							String secName = (String)sector.getProperty(INeoConstants.PROPERTY_NAME_NAME, "");

							Traverser tr = sector.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){

								@Override
								public boolean isReturnableNode(TraversalPosition currentPos) {
									if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.SECTOR_SECTOR_RELATIONS.getId())){
										return true;
									}
									return false;
								}
					    	}, NetworkRelationshipTypes.NEIGHBOURS, Direction.OUTGOING,NetworkRelationshipTypes.INTERFERENCE, Direction.OUTGOING);
							
							for(Node pSector:tr) {
								
								if(pSector.hasRelationship(Direction.OUTGOING)) {
//									System.out.println(secName + "-"+node.getId());
									cnt++;
									break;
								}
							}
						}
					} else {
						cnt++;
					}
				}
			}
		}
    	return cnt;
    }
    private void validateCELLFile(Node root,String domain, String band) throws Exception {
    	int lineCnt = getLineCnt(AfpExporter.tmpAfpFolder + "/" + domain + "/InputCellFile.awe", null);
    	int trxCnt = getTrxCountForBand(root, band, false);
    	// check count of TRX
    	assertTrue("Cell File TRX Count do not match <"+lineCnt+","+trxCnt+ ">",(lineCnt == trxCnt));
    }
    
    private void validateINTFile(Node root,String domain, String band) throws Exception {
    	int lineCnt = getLineCnt(AfpExporter.tmpAfpFolder + "/" + domain + "/InputInterferenceFile.awe", "^SUBCELL.*$");
    	int trxCnt = getTrxCountForBand(root, band, true);
    	// check count of TRX
    	assertTrue("Int File SUBCELL TRX Count do not match <"+lineCnt+","+trxCnt+ ">",(lineCnt == trxCnt));
    }
    
    // tests
    // domain folders are created
    // cell and interference files are created
    // single domain 
    //  - cell file, no interference lists
    //  - cell file + 1 neighbor list
    //  - cell file + 1 interference list
    //  - cell file + 1 triangulation list
    //  - cell file + 1 shadow list
    //  - cell file + 1 nbr + 1 int list
    //  - cell file + 2 nbr + 2 int list
    //  - cell file + 1 nbr + 1 int + 1 tri + 1 sha
    
    // 2 or more doamins 
    
    // test bcch only
    // test bcch + non hopping
    // test bcch + SY-HOPPING
    // test multipe bands
    
    // combined domains
    // overlaping domains.
    
    // validate cell files
    //	- count number of TRXs
    // validate interference files
    
    
    @Test
    public void testCellFileExport() throws Exception{
    	// run exporter
    	AfpExporter exporter = new AfpExporter(this.rootNode, model.getAfpNode(), model);
    	
    	exporter.run(new NullProgressMonitor());
    	
    	// validate CELL File
    	validateCELLFile(this.rootNode,"Band_900", "900");
    	//validate that interference file should not be generated
    	int lineCnt = getLineCnt(AfpExporter.tmpAfpFolder + "/Band_900/InputInterferenceFile.awe", "SUBCELL*");
    	assertTrue("Int File SUBCELL TRX Count do not match <"+lineCnt+","+0+ ">",(lineCnt == 0));
    	
    }

    @Test
    @LongRunning
    public void testNbrFileExport() throws Exception{
    	// import the test data
    	loadNeighbors(this.rootNode,"../org.amanzi.awe.afp.testing/files/exporter/Neighbours.txt");
    	// run exporter
    	AfpExporter exporter = new AfpExporter(this.rootNode, model.getAfpNode(), model);
    	
    	exporter.run(new NullProgressMonitor());
    	
    	// validate CELL File
    	validateCELLFile(this.rootNode,"Band_900", "900");
    	//validate interference file
    	validateINTFile(this.rootNode,"Band_900", "900");
    }
    
    @Test
    @LongRunning
    public void testIntFileExport() throws Exception{
    	// import the test data
    	loadInterference(this.rootNode,"../org.amanzi.awe.afp.testing/files/exporter/distance2.txt");
    	// run exporter
    	AfpExporter exporter = new AfpExporter(this.rootNode, model.getAfpNode(), model);
    	
    	exporter.run(new NullProgressMonitor());
    	
    	// validate CELL File
    	validateCELLFile(this.rootNode,"Band_900", "900");
    	//validate interference file
    	validateINTFile(this.rootNode,"Band_900", "900");
    }
    
    @Test
    @LongRunning
    public void testNbrAndIntFileExport() throws Exception{
    	// import the test data
    	loadNeighbors(this.rootNode,"../org.amanzi.awe.afp.testing/files/exporter/Neighbours.txt");
    	loadInterference(this.rootNode,"../org.amanzi.awe.afp.testing/files/exporter/distance2.txt");
    	// run exporter
    	AfpExporter exporter = new AfpExporter(this.rootNode, model.getAfpNode(), model);
    	
    	exporter.run(new NullProgressMonitor());
    	
    	// validate CELL File
    	validateCELLFile(this.rootNode,"Band_900", "900");
    	//validate interference file
    	validateINTFile(this.rootNode,"Band_900", "900");
    }
    
    @Test
    public void testCellFileExportTwoDomains() throws Exception{
    	add1800DomainToModel();
    	// run exporter
    	AfpExporter exporter = new AfpExporter(this.rootNode, model.getAfpNode(), model);
    	
    	exporter.run(new NullProgressMonitor());
    	
    	// validate CELL File
    	validateCELLFile(this.rootNode,"Band_900", "900");
    	validateCELLFile(this.rootNode,"Band_1800", "1800");
    	//validate that interference file should not be generated
    	int lineCnt = getLineCnt(AfpExporter.tmpAfpFolder + "/Band_900/InputInterferenceFile.awe", "SUBCELL*");
    	assertTrue("Int File SUBCELL TRX Count do not match <"+lineCnt+","+0+ ">",(lineCnt == 0));
    	
    	lineCnt = getLineCnt(AfpExporter.tmpAfpFolder + "/Band_1800/InputInterferenceFile.awe", "SUBCELL*");
    	assertTrue("Int File SUBCELL TRX Count do not match <"+lineCnt+","+0+ ">",(lineCnt == 0));
    	
    }
    
    @Test
    @LongRunning
    public void testNbrFileExportTwoDomains() throws Exception{
    	add1800DomainToModel();
    	// import the test data
    	loadNeighbors(this.rootNode,"../org.amanzi.awe.afp.testing/files/exporter/Neighbours.txt");
    	// run exporter
    	AfpExporter exporter = new AfpExporter(this.rootNode, model.getAfpNode(), model);
    	
    	exporter.run(new NullProgressMonitor());
    	
    	// validate CELL File
    	validateCELLFile(this.rootNode,"Band_900", "900");
    	validateCELLFile(this.rootNode,"Band_1800", "1800");
    	//validate interference file
    	validateINTFile(this.rootNode,"Band_900", "900");
    	validateINTFile(this.rootNode,"Band_1800", "1800");
    	
    }
    
    @Test
    @LongRunning
    public void testIntFileExportTwoDomains() throws Exception{
    	add1800DomainToModel();
    	// import the test data
    	loadNeighbors(this.rootNode,"../org.amanzi.awe.afp.testing/files/exporter/distance2.txt");
    	// run exporter
    	AfpExporter exporter = new AfpExporter(this.rootNode, model.getAfpNode(), model);
    	
    	exporter.run(new NullProgressMonitor());
    	
    	// validate CELL File
    	validateCELLFile(this.rootNode,"Band_900", "900");
    	validateCELLFile(this.rootNode,"Band_1800", "1800");
    	//validate interference file
    	validateINTFile(this.rootNode,"Band_900", "900");
    	validateINTFile(this.rootNode,"Band_1800", "1800");
    	
    }
    
    @Test
    @LongRunning
    public void testNbrAndIntFileExportTwoDomains() throws Exception{
    	add1800DomainToModel();
    	// import the test data
    	loadNeighbors(this.rootNode,"../org.amanzi.awe.afp.testing/files/exporter/Neighbours.txt");
    	loadNeighbors(this.rootNode,"../org.amanzi.awe.afp.testing/files/exporter/distance2.txt");
    	// run exporter
    	AfpExporter exporter = new AfpExporter(this.rootNode, model.getAfpNode(), model);
    	
    	exporter.run(new NullProgressMonitor());
    	
    	// validate CELL File
    	validateCELLFile(this.rootNode,"Band_900", "900");
    	validateCELLFile(this.rootNode,"Band_1800", "1800");
    	//validate interference file
    	validateINTFile(this.rootNode,"Band_900", "900");
    	validateINTFile(this.rootNode,"Band_1800", "1800");
    	
    }


    /**
     * Creates the structure.
     *
     * @return the node
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static Node loadNetwork(String fileName) throws IOException {
        NetworkLoader loader = new NetworkLoader(util.getNeo(), fileName, util.getIndex());
        loader.setup();
        loader.run(new NullProgressMonitor());
        return loader.getNetworkNode();
    }
    private static void loadNeighbors(Node networkNode, String fileName) throws Exception {
    	
    	NeighbourLoader loader = new NeighbourLoader(networkNode, fileName, util.getIndex(), false, true);
        loader.run(new NullProgressMonitor());
        return;
    }
    private static void loadInterference(Node networkNode, String fileName) throws Exception {
    	
    	NeighbourLoader loader = new NeighbourLoader(networkNode, fileName, util.getIndex(), true, true);
        loader.run(new NullProgressMonitor());
        return;
    }
    /**
     * Finish all tests.
     */
    @AfterClass
    public static void finishAll(){
        util.shutdownNeo();
        CommonTestUtil.clearDirectory(new File(util.getMainDirectory(false)));
    }
}
