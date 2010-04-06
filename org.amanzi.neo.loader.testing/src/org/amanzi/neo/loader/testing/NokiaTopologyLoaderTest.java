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

package org.amanzi.neo.loader.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.nokia.BSCData;
import org.amanzi.neo.data_generator.data.nokia.ExternalCellData;
import org.amanzi.neo.data_generator.data.nokia.NokiaTopologyData;
import org.amanzi.neo.data_generator.data.nokia.SectorData;
import org.amanzi.neo.data_generator.data.nokia.SiteData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.NokiaTopologyLoader;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Tests for NokiaTopologyLoader
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class NokiaTopologyLoaderTest extends AbstractLoaderTest{
    
    private static final String NETWORK_NAME = "nokia_network";
    private static final String DATA_SAVER_DIR = "nokia_data";
    
    private String dataDirectory;
    private NokiaTopologyData genData;
    private String fileName;
    
    /**
     * Create new main directory.
     */
    private void initEmptyDataDirectory(){
        File dir = new File(getUserHome());
        if(!dir.exists()){
            dir.mkdir();
        }
        dir = new File(dir,AMANZI_STR);
        if(!dir.exists()){
            dir.mkdir();    
        }
        dir = new File(dir,DATA_SAVER_DIR);
        if(!dir.exists()){
            dir.mkdir();    
        }
        dataDirectory = dir.getPath();
    }
    
    /**
     * initialize test
     * 
     * @throws IOException
     */
    @Before
    public void init() throws IOException {
        clearDbDirectory();
        initEmptyDataDirectory();
    }

    
    
    /**
     * Tests load empty data base.
     */
    @Test
    public void testEmptyLoading()throws IOException{
        NokiaTopologyLoader loader = initDataBase(BUNDLE_KEY_EMPTY);
        assertLoader(loader);
        assertLoadedData(BUNDLE_KEY_EMPTY);
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectLoading()throws IOException{
        NokiaTopologyLoader loader = initDataBase(BUNDLE_KEY_CORRECT);
        assertLoader(loader);
        assertLoadedData(BUNDLE_KEY_CORRECT);
    }
    
    /**
     * Initialize loader.
     * @param aTestKey String (key for test)
     * @throws IOException (loading problem)
     */
    private NokiaTopologyLoader initDataBase(String aTestKey) throws IOException {
        String dbName = getDbName(aTestKey);
        generateDataFiles(aTestKey,dbName);
        initProjectService();        
        fileName = new File(dataDirectory,dbName).getPath();
        NokiaTopologyLoader loader = new NokiaTopologyLoader(fileName, NETWORK_NAME, null,initIndex(),getNeo());
        loader.setLimit(5000);
        loader.run(new NullProgressMonitor());
        return loader;
    }
    
    /**
     * Generate data for gets statistics.
     *
     * @param aTestKey
     * @param aFileName
     * @throws IOException (problem in data generation)
     */
    private void generateDataFiles(String aTestKey, String aFileName) throws IOException {
        List<Integer> params = parceStringToIntegerList(getProperty("test_loader.gen_params."+aTestKey));
        List<Float> paramsLoc = parceStringToFloatList(getProperty("test_loader.gen_params.loc."+aTestKey));
        IDataGenerator generator = DataGenerateManager.getNokiaTopologyGenerator(dataDirectory, aFileName, params.get(0), params.get(1), params.get(2), params.get(3),
                new Float[]{paramsLoc.get(0),paramsLoc.get(1)}, new Float[]{paramsLoc.get(2),paramsLoc.get(3)});
        genData = (NokiaTopologyData)generator.generate();
    }
    
    /**
     * Execute after even test. 
     * Clear data base.
     */
    @After
    public void finishOne(){
        doFinish();
        clearDataDirectory();
    }

    private void clearDataDirectory() {
        File dir = new File(dataDirectory);
        if(dir.exists()){
            clearDirectory(dir);
        }
        dir.delete();
    }   

    /**
     *finish 
     */
    @AfterClass
    public static void finish() {
        doFinish();
    }

    /**
     * Assert loaded data by generated.
     *
     * @param aTestKey String (key for test)
     */
    private void assertLoadedData(String aTestKey){
        Transaction tx = getNeo().beginTx();
        try {
            Node gis = NeoUtils.findGisNode(NETWORK_NAME, getNeo());
            Node network = NeoUtils.findOrCreateNetworkNode(gis, NETWORK_NAME, fileName, getNeo());
            assertBSCs(aTestKey, network);
            assertExternals(aTestKey, network);
        } finally {
            tx.finish();
        }
    }
    
    /**
     * Assert BSC nodes.
     *
     * @param aTestKey  String (key for test)
     * @param network Node
     */
    private void assertBSCs(String aTestKey, Node network){
        List<BSCData> bscDataList = genData.getBscList();
        List<Node> bscNodes = getListOfNodes(network, NodeTypes.BSC, false);
        assertEquals("Wrong count of BSC nodes by key <"+aTestKey+">.",bscDataList.size(), bscNodes.size());
        for(BSCData bscData : bscDataList){
            String name = bscData.getProperties().get("name");
            Node bsc = getNodeByName(bscNodes, name);
            assertFalse("Not find BSC by name <"+name+"> (key <"+aTestKey+">).",bsc==null);
            assertSites(aTestKey, name, bsc, bscData.getSites());
        }
    }
    
    /**
     * Assert External UMTS nodes.
     *
     * @param aTestKey  String (key for test)
     * @param network Node
     */
    private void assertExternals(String aTestKey, Node network){
        ExternalCellData cellData = genData.getExternalCell();
        Node realCell = getRealExternalCell(network);
        if(cellData==null){
            assertTrue("Network by key <"+aTestKey+"> must have no external cells",realCell==null);
        }
        else{
            assertTrue("Network by key <"+aTestKey+"> must have an external cells",realCell!=null);
            assertSectors("External UMTS cell by key <"+aTestKey+">.", getListOfNodes(realCell, NodeTypes.SECTOR, false), cellData.getSectors(), true);
        }
    }
    
    private Node getRealExternalCell(Node network){
        for(Relationship link : network.getRelationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING)){
            Node child = link.getEndNode();
            NodeTypes type = NodeTypes.getNodeType(child, getNeo());
            if(type!=null && type.equals(NodeTypes.SITE)){
                return child;
            }
        }
        return null;
    }
    
    /**
     * Assert site nodes.
     *
     * @param aTestKey  String (key for test)
     * @param bscName String (BSC node name)
     * @param bsc Node (root BSC)
     * @param data list of SiteData.
     */
    private void assertSites(String aTestKey, String bscName, Node bsc, List<SiteData> data){
        List<Node> sites = getListOfNodes(bsc, NodeTypes.SITE, false);
        assertEquals("Wrong count of Site nodes by BSC <"+bscName+"> key <"+aTestKey+">.",data.size(), sites.size());
        for(SiteData siteData : data){
            String name = siteData.getProperties().get("name");
            Node site = getNodeByName(sites, name);
            assertFalse("Not find Site by name <"+name+"> (BSC name <"+bscName+">, key <"+aTestKey+">).",site==null);
            Float etalon = siteData.getLatitude();
            Double real = (Double)site.getProperty(INeoConstants.PROPERTY_LAT_NAME,0.0);
            assertEquals("Wrong latitude in Site <"+name+"> (BSC name <"+bscName+">, key <"+aTestKey+">).",etalon, real.floatValue());
            etalon = siteData.getLongitude();
            real = (Double)site.getProperty(INeoConstants.PROPERTY_LON_NAME,0.0);
            assertEquals("Wrong longitude in Site <"+name+"> (BSC name <"+bscName+">, key <"+aTestKey+">).",etalon, real.floatValue());
            List<Node> sectors = getListOfNodes(site, NodeTypes.SECTOR, false);
            assertSectors("Site name <"+name+">, BSC name <"+bscName+">, key <"+aTestKey+">", sectors, siteData.getSectors(), false);
        }
    }
    
    /**
     * Assert site nodes.
     *
     * @param assertKey String (key for assert messages)
     * @param sectors list of Nodes 
     * @param data list of SectorData
     * @param onlyCheckName boolean (not need check other data)
     */
    private void assertSectors(String assertKey, List<Node> sectors, List<SectorData> data, boolean onlyCheckName){
        assertEquals("Wrong count of Secctor nodes by "+assertKey+".",data.size(), sectors.size());
        for(SectorData sectorData : data){
            String name = sectorData.getProperties().get("name");
            Node sector = getNodeByName(sectors, name);
            assertFalse("Not find Sector by name <"+name+"> ("+assertKey+").",sector==null);
            if(!onlyCheckName){
                Integer etalon = sectorData.getAzimuth();
                Integer real = (Integer)sector.getProperty("azimuth", null);
                assertEquals("Wrong azimuth in Sector <"+name+"> ("+assertKey+").",etalon, real);
                etalon = sectorData.getBeamwidth();
                real = (Integer)sector.getProperty("beamwidth", null);
                assertEquals("Wrong beamwidth in Sector <"+name+"> ("+assertKey+").",etalon, real);
                assertSectors("neighbor for Sector <"+name+">, "+assertKey, 
                        getListOfNodes(sector, NodeTypes.SECTOR, true), sectorData.getNeighbors(), true);
            }
        }
    }
    
    /**
     * Returns list of nodes by type. 
     *
     * @param root Node
     * @param type NodeTypes
     * @param isNeighbors boolean (search by neighbor links)
     * @return list of nodes
     */
    private List<Node> getListOfNodes(final Node root, final NodeTypes type, boolean isNeighbors){
        ReturnableEvaluator returnableEvaluator = new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node curr = currentPos.currentNode();
                NodeTypes currType = NodeTypes.getNodeType(curr, getNeo());
                return !curr.equals(root)&&currType!=null && currType.equals(type);
            }
        };
        Traverser traverser;
        if (isNeighbors) {
            traverser = root.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, returnableEvaluator,
                    NetworkRelationshipTypes.NEIGHBOUR, Direction.OUTGOING);
        }else{
            traverser = root.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, returnableEvaluator,
                    GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
        }
        return new ArrayList<Node>(traverser.getAllNodes());
    }
    
    /**
     * Gets node from list by name.
     *
     * @param nodes list of nodes
     * @param name String 
     * @return Node
     */
    private Node getNodeByName(List<Node> nodes, String name){
        for(Node node : nodes){
            String currName = NeoUtils.getNodeName(node, getNeo());
            if(name.equals(currName)){
                return node;
            }
            if(currName.startsWith(name)&& currName.contains("external")){
                return node;
            }
        }
        return null;
    }
}
