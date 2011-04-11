/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.afp.testing.engine;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import junit.framework.Assert;

import org.amanzi.awe.afp.executors.AfpProcessExecutor;
import org.amanzi.awe.afp.exporters.AfpExporter;
import org.amanzi.awe.afp.loaders.AfpOutputFileLoader;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.testing.engine.AfpModelFactory.AfpScenario;
import org.amanzi.awe.afp.testing.engine.TestDataLocator.DataType;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.loader.ui.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class AfpEngineTest {
    
    private static Logger LOGGER = Logger.getLogger(AfpEngineTest.class);
    
    private static GraphDatabaseService graphDatabaseService;
    
    private static ArrayList<IDataset> datasets = new ArrayList<IDataset>();
    
    private static long startTimestamp;
    
    private static HashMap<IDataset, HashMap<AfpScenario, AfpModel>> scenarios = new HashMap<IDataset, HashMap<AfpScenario, AfpModel>>();
    
    private static HashMap<AfpModel, AfpExporter> exporterMap = new HashMap<AfpModel, AfpExporter>();
    
    private static HashMap<AfpModel, AfpOutputFileLoader> loaderMap = new HashMap<AfpModel, AfpOutputFileLoader>();
    
    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        startTimestamp = System.currentTimeMillis();
        LOGGER.info("Set up AFP Engine Test");
        
        try {
            initEnvironment();
            loadDataset();
            exportInputFiles();
            runEngine();
            loadResults();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        NeoServiceProviderUi.getProvider().getIndexService().shutdown();
        graphDatabaseService.shutdown();
         
        clearDb();
        
        long duration = System.currentTimeMillis() - startTimestamp;
        int milliseconds = (int)(duration % 1000);
        int seconds = (int)(duration / 1000 % 60 );
        int minutes = (int)(duration / 1000 / 60 % 60);
        int hours = (int)(duration / 1000 / 60 / 60 % 24);
        LOGGER.info("Test finished. Test time - " + hours + " hours " + 
                                                    minutes + " minutes " + 
                                                    seconds + " seconds " +
                                                    milliseconds + " milliseconds");
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
    
    /**
     * Clears Database Directory
     */
    private static void clearDb() {
        deleteDirectory(new File(getDbLocation()));
    }
    
    private static void deleteDirectory(File directory) {
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                deleteDirectory(subFile);
            }            
            else {
                subFile.delete();
            }
        }
        directory.delete();
    }
    
    /**
     * Initialized Database on selected Directory
     */
    private static void initializeDb() {
        LOGGER.info("Initialize Database");
        graphDatabaseService = new EmbeddedGraphDatabase(getDbLocation());
        NeoServiceProviderUi.initProvider(graphDatabaseService, getDbLocation());
        DatabaseManager.setDatabaseAndIndexServices(graphDatabaseService, NeoServiceProviderUi.getProvider().getIndexService());
        LOGGER.info("Database was successfully initialized");
    }
    
    private static String getDbLocation() {
        return System.getProperty("user.home") + File.separator + ".amanzi" + File.separator + "afp_test";
    }
    
    private static void loadDataset() {
        LOGGER.info("Load Datasets");
        for (IDataset loader : datasets) {
            if (loader != null) {
                loader.run();
            }
        }
    }
    
    private static void initEnvironment() throws IOException {
        initializeDb();
        initPreferences();
        
        LOGGER.info("Initialize Test datasets");
        for (DataType singleType : DataType.values()) {
            datasets.add(getDatasetLoader(singleType));
        }
    }
    
    private static void initPreferences() {
        LOGGER.info("Load Preferences");
        DataLoadPreferenceInitializer initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
    }
    
    private static IDataset getDatasetLoader(DataType dataType) throws IOException {
        switch (dataType) {
        case ERICSSON:
            return null;//new LoadEricssonDataAction("project");
        case GENERAL_FORMAT:
            return null;
        case GERMANY:
            return new LoadGermanyDataAction("project");
        }
        
        return null;
    }
    
    private static void exportInputFiles() {
        LOGGER.info("Export input files for AFP Engine");
        for (IDataset dataset : datasets) {
            if (dataset == null) {
                continue;
            }
            for (AfpScenario scenario : AfpScenario.values()) {
                AfpModel model = dataset.getAfpModel(scenario);
                AfpExporter exporter = model.getExporter();
                
                AfpOutputFileLoader loader = new FakeAfpLoader(dataset.getRootNode(), model.getAfpNode(), exporter);
                loaderMap.put(model, loader);
                
                exporterMap.put(model, exporter);
                
                LOGGER.info("Writing files for Dataset <" + dataset.getName() + "> using " + scenario.name() + " scenario");
                long before = System.currentTimeMillis();
                exporter.run(null);
                long after = System.currentTimeMillis();
                LOGGER.info("Writing finished in " + (after - before) + " milliseconds");
                
                model.saveUserData();
                model.executeAfpEngine(null, exporter);
                
                if (!scenarios.containsKey(dataset)) {
                    scenarios.put(dataset, new HashMap<AfpScenario, AfpModel>());
                }
                scenarios.get(dataset).put(scenario, model);
            }
        }
    }
    
    private static void runEngine() {
        LOGGER.info("Running AFP Engine");
        
        for (IDataset dataset : scenarios.keySet()) {
            for (AfpScenario scenario : scenarios.get(dataset).keySet()) {
                AfpModel model = scenarios.get(dataset).get(scenario);
                
                AfpProcessExecutor executor = model.getExecutor();
                LOGGER.info("AFP Engine started for dataset <" + dataset.getName() + "> with " + scenario.name() + " scenario");
                long before = System.currentTimeMillis();
                executor.run(null);
                long after = System.currentTimeMillis();
                LOGGER.info("AFP Engine finished. Spent time - " + toHourTime(after - before));
            }
        }
    }
    
    private static String toHourTime(long milliseconds) {
        int seconds = (int)(milliseconds / 1000 % 60 );
        int minutes = (int)(milliseconds / 1000 / 60 % 60);
        int hours = (int)(milliseconds / 1000 / 60 / 60 % 24);
        return hours + " hours " + 
               minutes + " minutes " + 
               seconds + " seconds";
        
    }
    
    private static void loadResults() throws IOException {
        LOGGER.info("Loading generated Frequency Plans back to Database");
        for (IDataset dataset : datasets) {
            if (dataset == null) {
                continue;
            }
            for (AfpScenario scenario : scenarios.get(dataset).keySet()) {
                AfpModel model = scenarios.get(dataset).get(scenario);
                AfpOutputFileLoader loader = loaderMap.get(model);
                
                long before = System.currentTimeMillis();
                loader.runAfpLoader(null);
                long after = System.currentTimeMillis();
                LOGGER.info("Generated Frequency Plan for datasets <" + dataset.getName() + "> and scenario <" + scenario.name() + "> was loaded in " + (after - before) + " milliseconds");
            }
        }
    }
    
    @Test
    public void test1() {
        
    }
    
    @Test
    public void checkOutputFiles() {
        LOGGER.info("Check generation of Output Files");
        
        ArrayList<String> errors = new ArrayList<String>();
        
        for (IDataset dataset : scenarios.keySet()) {
            for (AfpScenario scenario : scenarios.get(dataset).keySet()) {
                AfpModel model = scenarios.get(dataset).get(scenario);
                
                AfpExporter exporter = exporterMap.get(model);
                
                for (String domainDir : exporter.domainDirPaths) {
                    File outputFile = new File(domainDir + File.separator + exporter.outputFileName);
                    
                    if (!outputFile.exists()) {
                        errors.add("<" + dataset.getName() + "> on " + scenario.name() + 
                                   " missing OutputFile for domain " + new File(domainDir).getName());
                    }
                }
            }
        }
        
        if (!errors.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (String error : errors) {
                errorMessage.append(error + "\n");
            }
            
            fail(errorMessage.toString());
        }
    }
    
    @Test
    public void CorrectStructure(){
    	
    	LinkedList<String> structureNode = new LinkedList<String>();
    	structureNode.add(0, NodeTypes.NETWORK.getId());
    	structureNode.add(1, NodeTypes.BSC.getId());
    	structureNode.add(2, NodeTypes.CITY.getId());
    	structureNode.add(3, NodeTypes.SITE.getId());
    	structureNode.add(4, NodeTypes.SECTOR.getId());
    	structureNode.add(5, NodeTypes.TRX.getId());
    	structureNode.add(6, NodeTypes.FREQUENCY_PLAN.getId());
    	
    	LinkedList<RelationshipType> structureRelation = new LinkedList<RelationshipType>();
    	structureRelation.add(0, NetworkRelationshipTypes.CHILD);
    	structureRelation.add(1, NetworkRelationshipTypes.CHILD);
    	structureRelation.add(2, NetworkRelationshipTypes.CHILD);
    	structureRelation.add(3, NetworkRelationshipTypes.CHILD);
    	structureRelation.add(4, NetworkRelationshipTypes.CHILD);
    	structureRelation.add(5, DatasetRelationshipTypes.PLAN_ENTRY);
    	structureRelation.add(6, null);
    	
    	for (IDataset dataset : datasets) {
    		if (dataset == null){
    			continue;
    		}
    		Iterator<String> iterNode = structureNode.iterator();
    		Iterator<RelationshipType> iterRelation = structureRelation.iterator();
    		Node node = dataset.getRootNode();
    		String nodeType;
    		RelationshipType relationType;
    		while (iterNode.hasNext() && iterRelation.hasNext()){
    			nodeType = iterNode.next();
    			relationType = iterRelation.next();
    			isCorrect(node, nodeType, relationType);
    			if (relationType != null)
    				node = node.getRelationships(relationType, Direction.OUTGOING).iterator().next().getEndNode();
    		}
    		
    				
    			
    		   			
    	}
    }
    
    private void isCorrect(Node node, String nodeType, RelationshipType relationType ){
    	Assert.assertEquals(
    			node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null)+"unequal"+nodeType,
				nodeType,
				node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null));
    	if (relationType != null)
    		Assert.assertTrue(nodeType+" hasn't "+relationType+" relation",node.hasRelationship(relationType, Direction.OUTGOING));
    	
    	
    }

    @Test
    public void checkFrequencyPlanCoSiteCoSectorSeparations() {
        for (IDataset dataset : datasets) {
            NetworkModel network = new NetworkModel(dataset.getRootNode());
            
            
        }
    }
}
