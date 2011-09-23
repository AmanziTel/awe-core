package org.amanzi.neo.services.model.impl;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

public class ProjectModelTest extends AbstractAWETest {

	private static Logger LOGGER = Logger.getLogger(ProjectModelTest.class);

	private Transaction tx;
	// private static CorrelationService correlationServ;
	// private static NewDatasetService dsServ;
	// private static NewNetworkService nwServ;
	// private static ProjectService prServ;
	private static int count = 0;

	private ProjectModel model;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();

		// correlationServ = new CorrelationService(graphDatabaseService);
		// dsServ = new NewDatasetService(graphDatabaseService);
		// nwServ = new NewNetworkService(graphDatabaseService);
		// prServ = new ProjectService(graphDatabaseService);
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
		model = new ProjectModel(null);
		// exception
	}

	@Test
	public void testCreateDatasetStringIDriveType() {

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
		IDriveModel dm = model.findDataset("dataset", DriveTypes.values()[0]);
		// object returned is null
		Assert.assertNull(dm);
	}

	@Test
	public void testGetDatasetStringIDriveType() {
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
		INetworkModel nm = model.createNetwork("network");

		// object returned not null
		Assert.assertNotNull(nm);
		Assert.assertNotNull(nm.getRootNode());
		// name correct
		Assert.assertEquals("network", nm.getName());
	}

	@Test
	public void testFindNetwork() {
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
		INetworkModel nm = model.findNetwork("network");
		// object returned is null
		Assert.assertNull(nm);
	}

	@Test
	public void testGetNetwork() {
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
		// network !exists
		INetworkModel nm = model.getNetwork("network");
		// object returned not null
		Assert.assertNotNull(nm);
		Assert.assertNotNull(nm.getRootNode());
		// name correct
		Assert.assertEquals("network", nm.getName());
	}

}
