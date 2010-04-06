package org.amanzi.neo.loader.correlate.testing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.GeneratedCallsData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.AMSLoader;
import org.amanzi.neo.loader.NemoLoader;
import org.amanzi.neo.loader.correlate.AMSCorrellator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * Class for testing {@link org.amanzi.neo.loader.correlate.AMSCorrellator}
 * 
 * @author zhuhrou_a
 */
public class AMSCorrellatorTest {
    
    /** name of ams datset  */
    private static final String DRIVE_DATASET_NAME = "drive_data";
    
    /** name of nemo dataset */
    private static final String PROBE_DATASET_NAME = "probe_data";
    
    /** name of test network */
    private static final String TEST_NETWORK_NAME = "test network";
    
    /** name of test project node */
    private static final String TEST_PROJECT_NAME = "project";

    /** neo4j database name */
    private static final String DATABASE_NAME = "neo_test";

    /** name of nemo data directory */
    private static final String NEMO_DATA_DIR_NAME = "nemo_data";
    
    /** name of test data file that store nemo data */
    private static final String NEMO_DATA_FILE_NAME = "Nemo_3G_Scanner.nmf.1.nmf";

    /** name of ams data directory */
    private static final String AMS_DATA_DIR_NAME = "ams_data";

    /** user home directory property name */
    private static final String USER_HOME_PARAMETER_NAME = "user.home";

    /** test base working directory name */
    private static final String AMANZI_PROJECT_WORKING_DIRECTORY_NAME = ".amanzi";

    /** test main subfolder directory name */
    private static final String MAIN_DIRECTORY_NAME = "correlate_test";

    /** Prefix to file names that contains probe data. */
    private static final String PROBE_NAME_PREFIX = "PROBE";

    /** path to main wprking directory */
    private static String mainDirectoryPath;

    /** neo4j database */
    private static GraphDatabaseService neoDatabase;
    
    /** probe dataset */
    private Node probeDataset;
    
    /** drive dataset */
    private Node driveDataset;
    
    /**
    * Prepare operations before execute test.
    */
    @Before
    public void prepareTests(){
        prepareMainDirectory();
        initProjectService();
    }    

    /**
     * Initialize project service.
     */
    protected static void initProjectService() {
        NeoCorePlugin.getDefault().initProjectService(getNeo());
    }

    /**
     * Create new empty main directory instead old one.
     */
    protected static void prepareMainDirectory() {
        clearMainDirectory();
        initEmptyMainDirectory();
    }

    /**
     * Delete main directory.
     */
    protected static void clearMainDirectory() {
        File dir = new File(getUserHome());
        if (dir.exists() && dir.isDirectory()) {
            dir = new File(dir, AMANZI_PROJECT_WORKING_DIRECTORY_NAME);
            if (dir.exists() && dir.isDirectory()) {
                dir = new File(dir, MAIN_DIRECTORY_NAME);
                if (dir.exists()) {
                    if (dir.isDirectory()) {
                        clearDirectory(dir);
                    }
                    dir.delete();
                }
            }
        }
    }

    /**
     * Clear directory.
     * 
     * @param directory File (for clear)
     */
    protected static void clearDirectory(File directory) {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    clearDirectory(file);
                }
                file.delete();
            }
        }
    }
    
    /**
     * Create new main directory.
     */
    protected static void initEmptyMainDirectory(){
        File dir = new File(getUserHome());
        if(!dir.exists()){
            dir.mkdir();
        }
        dir = new File(dir,AMANZI_PROJECT_WORKING_DIRECTORY_NAME);
        if(!dir.exists()){
            dir.mkdir();    
        }
        dir = new File(dir,MAIN_DIRECTORY_NAME);
        if(!dir.exists()){
            dir.mkdir();    
        }
        mainDirectoryPath = dir.getPath();
    }

    /**
     * Gets neo service.
     * 
     * @return EmbeddedGraphDatabase
     */
    public static GraphDatabaseService getNeo() {
        if (neoDatabase == null) {
            neoDatabase = new EmbeddedGraphDatabase(getDbDirectoryName());
        }
        return neoDatabase;
    }

    /**
     * Get name of %USER_HOME% directory.
     * 
     * @return String
     */
    private static String getUserHome() {
        return System.getProperty(USER_HOME_PARAMETER_NAME);
    }
    
    /**
     * Returns name of directory for save generated data.
     *
     * @return String
     */
    private String getAsmDataDirectoryPath(){
        return mainDirectoryPath+ File.separator + AMS_DATA_DIR_NAME;
    }
    
    private String getNemoDataDirectoryPath()
    {
        return mainDirectoryPath + File.separator + NEMO_DATA_DIR_NAME;
    }
    
    /**
     * Shutdown database service.
     */
    protected void shutdownNeoDatabase() {
        if(neoDatabase!=null){
            neoDatabase.shutdown();
            neoDatabase = null;
        }
    }

    /**
     * Get name of data base directory. (Create directory if it not exists)
     * 
     * @return String
     */
    private static String getDbDirectoryName() {
        File dir = new File(mainDirectoryPath, DATABASE_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getPath();
    }
    
    
    /**
     * Generate data for gets statistics.
     *
     * @param aHours Integer (hours count)
     * @param aDrift Integer (drift for first hour from 00:00)
     * @param aCallsPerHour Integer (count of calls per hour)
     * @param aCallPerHourVariance Integer (variance of count of calls per hour)
     * @param aProbes Integer (count of probes)
     * @param dataDir String (directory for save data)
     * @return List<CallGroup> (generated ams data)
     */
    private List<CallGroup> generateAmsData(Integer aHours, Integer aDrift, 
            Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes, String dataDir) {
        IDataGenerator generator = DataGenerateManager.getIndividualAmsGenerator(dataDir, aHours,aDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
        List<CallGroup> generated = ((GeneratedCallsData)generator.generate()).getData();
        return generated;
    }
    
    /**
     * Generate nemo data and update ams data with nemo information
     * @param amsData List<CallGroup> (generated ams data)
     * @return
     */
    private List<CallGroup> generateNemoData(final List<CallGroup> amsData, String nemoDataDirectoryPath)
    {
        IDataGenerator generator =  DataGenerateManager.getNemoDataGenerator(new GeneratedCallsData(amsData) , nemoDataDirectoryPath  , NEMO_DATA_FILE_NAME );
        List<CallGroup> generated = ((GeneratedCallsData) generator.generate() ).getData();
        return generated;
    }
    
     
    /**
    * Test work of correlation method in {@link AMSCorrellator#correlate(String, String)}
    */
    @Test
    public void testCorrelateData() throws Exception
    {
        String amsDataDirectoryPath = getAsmDataDirectoryPath();
        String nemoDataDirectoryPath = getNemoDataDirectoryPath();
        List<CallGroup> amsData = generateAmsData(1 , 5 , 10 , 5 , 6 , amsDataDirectoryPath);       
        List<CallGroup> nemoData = generateNemoData(amsData, nemoDataDirectoryPath);
        //than we need load generated data to neo4j
        loadProbeData(amsDataDirectoryPath);
        loadDriveData(nemoDataDirectoryPath + File.separator + NEMO_DATA_FILE_NAME);
        addProjectNode();
        correlateData();
    }
    
    /** 
     * add project node to database. This is necessary because {@link AMSCorrellator}
     * is used it to locate dataset nodes.
     */
    public void addProjectNode()
    {
        Transaction tx = neoDatabase.beginTx();
        NeoUtils.addTransactionLog(tx, Thread.currentThread(), "create project node");
        try
        {
            Node referenceNode = neoDatabase.getReferenceNode();
            Node aweProjectNode = neoDatabase.createNode();
            aweProjectNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, TEST_PROJECT_NAME);
            aweProjectNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME , NodeTypes.AWE_PROJECT.getId() );
            //add relationship to reference node
            referenceNode.createRelationshipTo( aweProjectNode , SplashRelationshipTypes.AWE_PROJECT );
            aweProjectNode.createRelationshipTo( probeDataset , NetworkRelationshipTypes.CHILD );
            aweProjectNode.createRelationshipTo( driveDataset , NetworkRelationshipTypes.CHILD );
            tx.success();
        }
        finally
        {
           tx.finish();
        }
    }
    
    /**
     * Correlate probe and drive data
     */
    private void correlateData()
    {
         AMSCorrellator amsCorrellator = new AMSCorrellator(getNeo());
         amsCorrellator.correlate( PROBE_DATASET_NAME , DRIVE_DATASET_NAME );
    }    
    
    
    /**
     * Load generated probe data.
     *
     * @param dataDir String (directory for get data)
     * @throws IOException (problem in data generation)
     */
    private void loadProbeData(final String dataDir) throws IOException {
        AMSLoader loader = new AMSLoader(dataDir, PROBE_DATASET_NAME , TEST_NETWORK_NAME , getNeo());
        loader.setLimit(5000);
        loader.run(new NullProgressMonitor());
        probeDataset = loader.getDatasetNode();   
    }
    
    /**
     * Load generated drive data.
     * @param filePath String (file path to get data)
     * @throws IOException (problems in data generation)
     */
    private void loadDriveData(final String filePath) throws IOException
    {
       NemoLoader loader = new NemoLoader(null, filePath , DRIVE_DATASET_NAME , getNeo() );
       loader.setLimit(5000);
       loader.run(new NullProgressMonitor());
       driveDataset = loader.getDatasetNode();
    }
    
    /**
     * For every asm node we check that we have corresponding location informarion.
     */
    private void checkCorrelation()
    {
        
    }
    
    
    /**
     * Finish test.
     */
    @After
    public void finish(){
        shutdownNeoDatabase();
    }
    
    /**
     * Finish all tests.
     */
    //@AfterClass !!TODO uncomment it (we need this because - we need to test how our test generate test data)
    public static void finishAll(){
        clearMainDirectory();
    }
    
    

}