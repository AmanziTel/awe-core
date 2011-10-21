package org.amanzi.neo.services.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.indexes.PropertyIndex;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.ProjectModel.DistributionItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class ProjectModelTest extends AbstractNeoServiceTest {

	private static Logger LOGGER = Logger.getLogger(ProjectModelTest.class);
	
	private final static String[] NETWORK_STRUCTURE_NODE_TYPES = new String[] {
        NetworkElementNodeType.BSC.getId(),
        NetworkElementNodeType.CITY.getId(),
        NetworkElementNodeType.SITE.getId(),
        NetworkElementNodeType.SECTOR.getId()
    };

	private static int count = 0;

	private ProjectModel model;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();
		
		new LogStarter().earlyStartup();
        clearServices();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		clearDb();
	}

	@Before
	public final void before() {
		count++;
		model = new ProjectModel("project" + count);
	}

	@Test
	public void testProjectModel() {
		LOGGER.debug("start testProjectModel()");
		model = new ProjectModel("project");
		// object returned not null
		Assert.assertNotNull(model);
		// root node correct
		Assert.assertNotNull(model.getRootNode());
		Assert.assertEquals("project",
				model.getRootNode().getProperty(NewAbstractService.NAME, null));
		// name correct
		Assert.assertEquals("project", model.getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProjectModelNameNull() {
		LOGGER.debug("start testProjectModelNameNull()");
		String nl = null;
		model = new ProjectModel(nl);
		// exception
	}

	@Test
	public void testCreateDatasetStringIDriveType() {
		LOGGER.debug("start testCreateDatasetStringIDriveType()");

		IDriveModel dm = model.createDataset("dataset", DriveTypes.values()[0]);

		// object returned not null
		Assert.assertNotNull(dm);
		Assert.assertNotNull(dm.getRootNode());
		// name correct
		Assert.assertEquals("dataset", dm.getName());
		// drive type correct
		Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
	}

	@Test
	public void testCreateDatasetStringIDriveTypeINodeType() {
		LOGGER.debug("start testCreateDatasetStringIDriveTypeINodeType()");

		IDriveModel dm = model.createDataset("dataset", DriveTypes.values()[0],
				NetworkElementNodeType.values()[0]);

		// object returned not null
		Assert.assertNotNull(dm);
		Assert.assertNotNull(dm.getRootNode());
		// name correct
		Assert.assertEquals("dataset", dm.getName());
		// drive type correct
		Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
		// type correct
		Assert.assertEquals(NetworkElementNodeType.values()[0], dm.getType());
	}

	@Test
	public void testFindDataset() {
		LOGGER.debug("start testFindDataset()");
		model.createDataset("dataset", DriveTypes.values()[0]);

		IDriveModel dm = model.findDataset("dataset", DriveTypes.values()[0]);
		// object returned not null
		Assert.assertNotNull(dm);
		Assert.assertNotNull(dm.getRootNode());
		// name correct
		Assert.assertEquals("dataset", dm.getName());
		// drive type correct
		Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
	}

	@Test
	public void testFindDatasetNoDataset() {
		LOGGER.debug("start testFindDatasetNoDataset()");
		IDriveModel dm = model.findDataset("dataset", DriveTypes.values()[0]);
		// object returned is null
		Assert.assertNull(dm);
	}

	@Test
	public void testGetDatasetStringIDriveType() {
		LOGGER.debug("start testGetDatasetStringIDriveType()");
		// dataset exists
		model.createDataset("dataset", DriveTypes.values()[0]);

		IDriveModel dm = model.getDataset("dataset", DriveTypes.values()[0]);
		// object returned not null
		Assert.assertNotNull(dm);
		Assert.assertNotNull(dm.getRootNode());
		// name correct
		Assert.assertEquals("dataset", dm.getName());
		// drive type correct
		Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
	}

	@Test
	public void testGetDatasetStringIDriveTypeNoDataset() {
		LOGGER.debug("start testGetDatasetStringIDriveTypeNoDataset()");
		// dataset !exists

		IDriveModel dm = model.getDataset("dataset", DriveTypes.values()[0]);
		// object returned not null
		Assert.assertNotNull(dm);
		Assert.assertNotNull(dm.getRootNode());
		// name correct
		Assert.assertEquals("dataset", dm.getName());
		// drive type correct
		Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
	}

	@Test
	public void testGetDatasetStringIDriveTypeINodeType() {
		LOGGER.debug("start testGetDatasetStringIDriveTypeINodeType()");
		// dataset exists
		model.createDataset("dataset", DriveTypes.values()[0],
				NetworkElementNodeType.values()[0]);

		IDriveModel dm = model.getDataset("dataset", DriveTypes.values()[0],
				NetworkElementNodeType.values()[0]);
		// object returned not null
		Assert.assertNotNull(dm);
		Assert.assertNotNull(dm.getRootNode());
		// name correct
		Assert.assertEquals("dataset", dm.getName());
		// drive type correct
		Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
		// primary type correct
		Assert.assertEquals(NetworkElementNodeType.values()[0], dm.getType());
	}

	@Test
	public void testGetDatasetStringIDriveTypeINodeTypeNoDataset() {
		LOGGER.debug("start testGetDatasetStringIDriveTypeINodeTypeNoDataset()");
		// dataset !exists

		IDriveModel dm = model.getDataset("dataset", DriveTypes.values()[0],
				NetworkElementNodeType.values()[0]);
		// object returned not null
		Assert.assertNotNull(dm);
		Assert.assertNotNull(dm.getRootNode());
		// name correct
		Assert.assertEquals("dataset", dm.getName());
		// drive type correct
		Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
		// primary type correct
		Assert.assertEquals(NetworkElementNodeType.values()[0], dm.getType());
	}

	@Test
	public void testCreateNetwork() throws Exception {
		LOGGER.debug("start testCreateNetwork()");
		INetworkModel nm = model.createNetwork("network");

		// object returned not null
		Assert.assertNotNull(nm);
		Assert.assertNotNull(nm.getRootNode());
		// name correct
		Assert.assertEquals("network", nm.getName());
	}

	@Test
	public void testFindNetwork() throws Exception {
		LOGGER.debug("start testFindNetwork()");
		model.createNetwork("network");

		INetworkModel nm = model.findNetwork("network");
		// object returned not null
		Assert.assertNotNull(nm);
		Assert.assertNotNull(nm.getRootNode());
		// name correct
		Assert.assertEquals("network", nm.getName());
	}

	@Test
	public void testFindNetworkNoNetwork() throws Exception {
		LOGGER.debug("start testFindNetworkNoNetwork()");
		INetworkModel nm = model.findNetwork("network");
		// object returned is null
		Assert.assertNull(nm);
	}

	@Test
	public void testGetNetwork() throws Exception {
		LOGGER.debug("start testGetNetwork()");
		// network exists
		model.createNetwork("network");

		INetworkModel nm = model.getNetwork("network");
		// object returned not null
		Assert.assertNotNull(nm);
		Assert.assertNotNull(nm.getRootNode());
		// name correct
		Assert.assertEquals("network", nm.getName());
	}

	@Test
	public void testGetNetworkNoNetwork() throws Exception {
		LOGGER.debug("start testGetNetworkNoNetwork()");
		// network !exists
		INetworkModel nm = model.getNetwork("network");
		// object returned not null
		Assert.assertNotNull(nm);
		Assert.assertNotNull(nm.getRootNode());
		// name correct
		Assert.assertEquals("network", nm.getName());
	}
	
	@Test
	public void checkEmptyDistributionItemList() throws Exception {
	    assertTrue("List of distributions should be empty", model.getAllDistributionalModels().isEmpty());
	}
	
	@Test
	public void checkNotNullDistributionItemList() throws Exception{
	    assertNotNull("List of distributions should not be null", model.getAllDistributionalModels());
	}
	
	@Test
	public void checkDistributionItemsForNetworkNotEmpty() throws Exception {
	    model.dsServ = getDatasetService(model.getRootNode(), getNetworkNodes(1));
	    
	    List<DistributionItem> items = model.getAllDistributionalModels();
	    assertFalse("DistributionItems list cannot be empty", items.isEmpty());
	}
	
	@Test
	public void checkDistributionItemsSizeForNetwork() throws Exception {
	    model.dsServ = getDatasetService(model.getRootNode(), getNetworkNodes(1));
        
        List<DistributionItem> items = model.getAllDistributionalModels();
        assertEquals("Unexpected size of Distribution Items list", NETWORK_STRUCTURE_NODE_TYPES.length, items.size());
	}
	
	@Test
	public void checkDistributionModelOfAllItems() throws Exception {
	    model.dsServ = getDatasetService(model.getRootNode(), getNetworkNodes(1));
        
        List<DistributionItem> items = model.getAllDistributionalModels();
        DistributionItem firstItem = items.get(0);
        IDistributionalModel model = firstItem.getModel();
        
        for (DistributionItem item : items) {
            assertEquals("Unexpected DistributionalModel for Item", model, item.getModel());
        }   
	}
	
	@Test
	public void checkTypesOfAllItems() throws Exception {
	    model.dsServ = getDatasetService(model.getRootNode(), getNetworkNodes(1));
        
        List<DistributionItem> items = model.getAllDistributionalModels();
        
        for (int i = 0; i < NETWORK_STRUCTURE_NODE_TYPES.length; i++) {
            String expectedNodeType = NETWORK_STRUCTURE_NODE_TYPES[i];
            String actualNodeType = items.get(i).getNodeType().getId();
            
            assertEquals("Unexepcted NodeType of Item", expectedNodeType, actualNodeType);
        }
	}
	
	private NewDatasetService getDatasetService(Node projectNode, List<Node> networkNodes) throws Exception {
	    NewDatasetService result = mock(NewDatasetService.class);
	    
	    when(result.findAllDatasetsByType(projectNode, DatasetTypes.NETWORK)).thenReturn(networkNodes);
	    
	    return result;
	}
	
	private List<Node> getNetworkNodes(Integer count) {
	    List<Node> result = new ArrayList<Node>();
	    
	    for (int i = 0; i < count; i++) {
	        Node networkNode = mock(Node.class);
	        
	        when(networkNode.getProperty(NewNetworkService.NETWORK_STRUCTURE, null)).thenReturn(NETWORK_STRUCTURE_NODE_TYPES);
	        when(networkNode.getProperty(NewNetworkService.TYPE, null)).thenReturn(NetworkElementNodeType.NETWORK.getId());
	        when(networkNode.getProperty(NewNetworkService.NAME, StringUtils.EMPTY)).thenReturn(count.toString());
	        when(networkNode.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.INDEX, Direction.OUTGOING)).thenReturn(new ArrayList<Relationship>());
	        
	        result.add(networkNode);
	    }
	    
	    return result;
	}

}
