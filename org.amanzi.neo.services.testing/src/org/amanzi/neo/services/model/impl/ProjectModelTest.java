package org.amanzi.neo.services.model.impl;

import junit.framework.Assert;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

public class ProjectModelTest extends AbstractNeoServiceTest {

	private static Logger LOGGER = Logger.getLogger(ProjectModelTest.class);

	private Transaction tx;
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
		tx = graphDatabaseService.beginTx();
		count++;
		model = new ProjectModel("project" + count);
	}

	@After
	public final void after() {
		tx.success();
		tx.finish();
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
	public void testCreateNetwork() {
		LOGGER.debug("start testCreateNetwork()");
		INetworkModel nm = model.createNetwork("network");

		// object returned not null
		Assert.assertNotNull(nm);
		Assert.assertNotNull(nm.getRootNode());
		// name correct
		Assert.assertEquals("network", nm.getName());
	}

	@Test
	public void testFindNetwork() {
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
	public void testFindNetworkNoNetwork() {
		LOGGER.debug("start testFindNetworkNoNetwork()");
		INetworkModel nm = model.findNetwork("network");
		// object returned is null
		Assert.assertNull(nm);
	}

	@Test
	public void testGetNetwork() {
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
	public void testGetNetworkNoNetwork() {
		LOGGER.debug("start testGetNetworkNoNetwork()");
		// network !exists
		INetworkModel nm = model.getNetwork("network");
		// object returned not null
		Assert.assertNotNull(nm);
		Assert.assertNotNull(nm.getRootNode());
		// name correct
		Assert.assertEquals("network", nm.getName());
	}

}
