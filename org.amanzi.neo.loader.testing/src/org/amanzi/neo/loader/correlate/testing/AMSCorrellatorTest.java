package org.amanzi.neo.loader.correlate.testing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CommandRow;
import org.amanzi.neo.data_generator.data.calls.GeneratedCallsData;
import org.amanzi.neo.data_generator.data.calls.ProbeData;
import org.amanzi.neo.data_generator.data.nemo.PointData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.AMSLoader;
import org.amanzi.neo.loader.NemoLoader;
import org.amanzi.neo.loader.OldNemoVersionLoader;
import org.amanzi.neo.loader.correlate.AMSCorrellator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
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

    /** path to main wprking directory */
    private static String mainDirectoryPath;

    /** neo4j database */
    private static GraphDatabaseService neoDatabase;
    
    /** probe dataset */
    private Node probeDataset;
    
    /** drive dataset */
    private Node driveDataset;
    
    /** create index for probe data based on timestamp */
    private Map<Long,CommandRow> probeIndex;
    
    /** contains statistics for results of correlation opereation  */
    private Map<CorrelationResult,Integer> statistics;
    
    
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
        String amsDataDirectoryPath = "d:\\AWE documentation\\test data\\correlation test\\2009-08-04 part 1"; //getAsmDataDirectoryPath();
        String nemoDataDirectoryPath = "D:\\AWE documentation\\test data\\correlation test\\NA45_S-Bahn + Nord_09Aug04.dt1";//getNemoDataDirectoryPath();
        //List<CallGroup> amsData = generateAmsData( 1 , 0 , 1 , 1 , 2 , amsDataDirectoryPath);       
        //createProbeIndex(amsData);
        //generateNemoData(amsData, nemoDataDirectoryPath);
        //than we need load generated data to neo4j
        loadProbeData(amsDataDirectoryPath);
        loadDriveData(nemoDataDirectoryPath);
        addProjectNode();
        correlateData();
        //see correlation result statistics
        checkCorrelation();
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
        loader.setLimit(500000);
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
       NemoLoader loader = new OldNemoVersionLoader(null, filePath , DRIVE_DATASET_NAME , getNeo() );
       loader.setLimit(50000);
       loader.run(new NullProgressMonitor());
       driveDataset = loader.getDatasetNode();
    }
    
    /**
     * For every asm node we check that we have corresponding location informarion.
     */
    @SuppressWarnings({"null"})
    private void checkCorrelation()
    {
        statistics = new HashMap<CorrelationResult, Integer>();
        //start from dataset node and transverse
        Transaction transaction = null; 
         try
         {
             neoDatabase.beginTx();
             List<Node> files = getAllDirectoryNodes();
             //then i need to get all command 
             for(Node file: files)
             {
                 List<Node> commands = getAllCommandInFile(file);
                 for(Node command : commands)
                 {
                     checkCommandCorrelation(command);
                 }
             }
             transaction.success();
         }
         catch(Exception e)
         {
             NeoCorePlugin.error(null, e);
             if(transaction != null) transaction.failure();
         }
         finally
         {
             if(transaction != null) transaction.finish();
         }
    }
    
    /** 
     * Check that node have corresponding location information
     */
    private void checkCommandCorrelation(Node command)
    {
        Iterator<Node> locationIterator = command.traverse( 
                Order.DEPTH_FIRST , 
                new StopEvaluator() {
                    
                    @Override
                    public boolean isStopNode(TraversalPosition currentPos) {
                        return false;
                    }
                }, 
                new ReturnableEvaluator() {
                    
                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        Node node = currentPos.currentNode();
                        return node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.MP.getId());
                    }
                }, GeoNeoRelationshipTypes.LOCATION , Direction.OUTGOING ).iterator();
        if(locationIterator.hasNext())
        {
            //check if we have correct location information
            Node location = locationIterator.next();
            Float lat = (Float) location.getProperty(INeoConstants.PROPERTY_LAT_NAME);
            Float lon = (Float) location.getProperty(INeoConstants.PROPERTY_LON_NAME);
            Long timestamp = (Long) command.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME);
            //then reference to initial constant based on timestamp property
           // CommandRow rCommand = probeIndex.get(timestamp);
            Assert.fail("Couldn't find command node. Problems with transferring data in our system");
           // PointData pointData  =  rCommand.getPointData();
           addToStatistics(CorrelationResult.CORRALATED);
            /** if(lat.equals(pointData.getLatitude()))
            {
                if(lon.equals(pointData.getLongitude()))
                {
                    addToStatistics(CorrelationResult.CORRALATED);
                }
                else
                {
                    addToStatistics(CorrelationResult.MISSEDLOCATION);
                }
            }
            else
            {
                addToStatistics(CorrelationResult.MISSEDLOCATION);
            } */
        }
        else
        {
            //report that we missed location information
            addToStatistics(CorrelationResult.INCORECTLOCATION);
        }
    }    
    
    /** 
     * Add new data to statistics..
     * TODO we need this method because we already knows that we have wrong correlation process 
     * and we need some statistics to find out where troubles is located
     * @param correlationResult CorrelationResult (result of correlation operation)
     */
    private void addToStatistics(CorrelationResult correlationResult)
    {
        //refresh statistics data
        if(statistics.get(correlationResult) == null)
        {
            statistics.put(correlationResult, 1);
        }
        else
        {
            Integer oldStatData = statistics.get(correlationResult);
            statistics.put(correlationResult, oldStatData + 1 );
        }
    }
    
    
    /** build index for quick access finding command based on probe data.
     * Used command timestamp as index value 
     * @param probeData List (generated ams data)
     */
    private void createProbeIndex(List<CallGroup> probeData)
    {
        probeIndex = new HashMap<Long, CommandRow>();
        for(CallGroup callGroup : probeData)
        {
            List<CallData> callData = callGroup.getData();
            for(CallData call : callData)
            {
                List<ProbeData> probesData = call.getReceiverProbes();
                for(ProbeData data : probesData)
                {
                    List<CommandRow> commands = data.getCommands();
                    for(CommandRow command : commands)
                    {
                        probeIndex.put(command.getTime().getTime(), command );
                    }
                }
            }
        }
    }
    
    
    
    /**
    * Returns dataset node by it's name
    *
    * @param datasetName name of dataset
    * @return dataset node
    */
    private Node getDatasetNode(final String datasetName) {
        Node root = neoDatabase.getReferenceNode();
        Iterator<Node> datasetIterator = root.traverse(Order.DEPTH_FIRST, new StopEvaluator() {

            @Override
            public boolean isStopNode(TraversalPosition currentPos) {
                return currentPos.depth() > 3;
            }
        }, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.DATASET.getId()) &&
                       node.hasProperty(INeoConstants.PROPERTY_NAME_NAME) &&
                       node.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(datasetName);
            }
        }, SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
        
        if (datasetIterator.hasNext()) {
            return datasetIterator.next();
        }
        return null;
    }
    
    /** 
     * Get all list of command nodes 
     * @return list of files nodes
     */
    private List<Node> getAllDirectoryNodes()
    {
        Node datasetNode = getDatasetNode( PROBE_DATASET_NAME );        
        
        //then we create iterator that return all root level directories
        Iterator<Node> directoryIterator = 
            datasetNode.traverse( Order.DEPTH_FIRST , 
                    new StopEvaluator() {
                        
                        @Override
                        public boolean isStopNode(TraversalPosition currentPos) {
                            return false;
                        }
                    }, 
                    new ReturnableEvaluator() {

                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            Node node = currentPos.currentNode();
                            return node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) 
                                    && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.FILE.getId());
                        }
                    } , NetworkRelationshipTypes.CHILD , Direction.OUTGOING , GeoNeoRelationshipTypes.NEXT , Direction.OUTGOING ).iterator();        
        List<Node> fileNodes = new LinkedList<Node>();
        while(directoryIterator.hasNext())
        {
            fileNodes.add(directoryIterator.next());
        }
        
        return fileNodes;
    }
    
    
    private List<Node> getAllCommandInFile(Node file)
    {
      //then we create iterator that return all root level directories
      Iterator<Node> directoryIterator = 
            file.traverse( Order.DEPTH_FIRST , 
                    new StopEvaluator() {
                        
                        @Override
                        public boolean isStopNode(TraversalPosition currentPos) {
                            return false;
                        }
                    }, 
                    new ReturnableEvaluator() {

                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            Node node = currentPos.currentNode();
                            return node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) 
                                    && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.M.getId());
                        }
                    } , NetworkRelationshipTypes.CHILD , Direction.OUTGOING , GeoNeoRelationshipTypes.NEXT , Direction.OUTGOING ).iterator();        
        List<Node> mNodes = new LinkedList<Node>();
        while(directoryIterator.hasNext())
        {
            mNodes.add(directoryIterator.next());
        }
        
        return mNodes;
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
    
    /** Possible result of correlation operation */
    private enum CorrelationResult
    {
        CORRALATED("correlated"),
        MISSEDLOCATION("missedlocation"),
        INCORECTLOCATION("incorectlocation");
        
        /** identifier */
        private final String name;
        
        /** 
         * Default constructor
         * @param name String (identifier)
         */
        private CorrelationResult(String name)
        {
            this.name = name;
        }
        
        public String getName()
        {
            return name;
        }
        
    }
}