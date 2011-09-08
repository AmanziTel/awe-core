package org.amanzi.neo.services;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.services.CorrelationService.CorrelationNodeTypes;
import org.amanzi.neo.services.CorrelationService.Correlations;
import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class CorrelationServiceTest extends AbstractAWETest {

	private static Logger LOGGER = Logger
			.getLogger(CorrelationServiceTest.class);

	private Transaction tx;
	private static CorrelationService correlationServ;
	private static NewDatasetService dsServ;
	private static NewNetworkService nwServ;
	private static ProjectService prServ;
	private Node network;
	private Node dataset;
	private Node project;
	private static int count = 0;
	private static String filename = "." + File.separator + "file.txt";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();

		correlationServ = new CorrelationService(graphDatabaseService);
		dsServ = new NewDatasetService(graphDatabaseService);
		nwServ = new NewNetworkService(graphDatabaseService);
		prServ = new ProjectService(graphDatabaseService);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		clearDb();
	}

	@Before
	public final void before() {
		tx = graphDatabaseService.beginTx();
		try {
			count++;
			project = prServ.createProject("project" + count);
			network = dsServ.createDataset(project, "network",
					DatasetTypes.NETWORK);
			dataset = dsServ.createDataset(project, "dataset",
					DatasetTypes.DRIVE, DriveTypes.ROMES);
		} catch (AWEException e) {
			LOGGER.error("Could not create test nodes.", e);
		}
	}

	@After
	public final void after() {
		tx.success();
		tx.finish();
	}

	@Test
	public void testTest() {
		Assert.assertNotNull(project);
		Assert.assertNotNull(dataset);
		Assert.assertNotNull(network);
	}

	@Test
	public void testCreateCorrelation() {
		// create correlation
		Node correlation = correlationServ.createCorrelation(network, dataset);
		// node returned is not null
		Assert.assertNotNull(correlation);
		// relationship from network exists
		Assert.assertEquals(
				network,
				correlation.getSingleRelationship(Correlations.CORRELATION,
						Direction.INCOMING).getStartNode());
		// relationship from drive exists
		Assert.assertEquals(
				dataset,
				correlation
						.getSingleRelationship(Correlations.CORRELATED,
								Direction.OUTGOING)
						.getEndNode()
						.getSingleRelationship(Correlations.CORRELATION,
								Direction.INCOMING).getStartNode());
		// node type correct
		Assert.assertEquals(CorrelationNodeTypes.CORRELATION.getId(),
				correlation.getProperty(NewAbstractService.TYPE, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCorrelationNetworkNull() {
		// exception
		correlationServ.createCorrelation(null, dataset);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCorrelationDatasetNull() {
		// exception
		correlationServ.createCorrelation(network, null);
	}

	@Test
	public void testGetCorrelationRoot() {
		// root exists
		Node corRoot = correlationServ.createCorrelation(network, dataset);

		Node testNode = correlationServ.getCorrelationRoot(network);
		// node returned not null
		Assert.assertNotNull(testNode);
		// node correct
		Assert.assertEquals(corRoot, testNode);
	}

	@Test
	public void testGetCorrelationRootNoRoot() {
		// no root
		Node testnNode = correlationServ.getCorrelationRoot(network);
		// node returned is not null
		Assert.assertNotNull(testnNode);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCorrelationRootNetworkNull() {
		// exception
		correlationServ.getCorrelationRoot(null);
	}

	@Test
	public void testAddCorrelationNodes() {
		List<Node> ms = new ArrayList<Node>();
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			// create sector
			Node sector = nwServ.createSector(nwServ.createNetworkElement(
					network, "index", "site", NetworkElementNodeType.SITE),
					"index", "name", "ci", "lac");

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			dm.addFile(new File(filename));

			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < 6; i++) {
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = dm
						.addMeasurement(new File(filename).getName(), params);
				ms.add(m);

				// create correlation
				Node cr = correlationServ.addCorrelationNodes(network, sector,
						dataset, m);
				// node returned is not null
				Assert.assertNotNull(cr);
				// type is correct
				Assert.assertEquals(CorrelationNodeTypes.PROXY.getId(),
						cr.getProperty(NewAbstractService.TYPE, null));
				// relationship from sector exist
				Assert.assertEquals(
						sector,
						cr.getSingleRelationship(Correlations.CORRELATED,
								Direction.INCOMING).getStartNode());
				// relationship from measurement exists and property set correct
				Assert.assertEquals(
						cr,
						m.getSingleRelationship(Correlations.CORRELATED,
								Direction.INCOMING).getStartNode());
				// C-N-N relationship from correlation root exist
				Assert.assertTrue(chainExists(
						correlationServ.getCorrelationRoot(network), cr));

				// relationship property correct
				Assert.assertEquals(
						dataset.getId(),
						m.getSingleRelationship(Correlations.CORRELATED,
								Direction.INCOMING).getProperty(
								CorrelationService.DATASET_ID));
				Assert.assertEquals(
						network.getId(),
						m.getSingleRelationship(Correlations.CORRELATED,
								Direction.INCOMING).getProperty(
								CorrelationService.NETWORK_ID));

			}
		} catch (AWEException e) {
			LOGGER.error("Could not add correlation.", e);
			fail();
		}
	}

	@Test
	public void testGetCorrelatedSector() {
		Node m = null, sector = null;
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			// create sector
			sector = nwServ.createSector(nwServ.createNetworkElement(network,
					"index", "site", NetworkElementNodeType.SITE), "index",
					"name", "ci", "lac");

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			dm.addFile(new File(filename));

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("fake", "param");
			params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
			m = dm.addMeasurement(new File(filename).getName(), params);

			// create correlation
			correlationServ.addCorrelationNodes(network, sector, dataset, m);
		} catch (AWEException e) {
			LOGGER.error("Could not get correlated sector", e);
			fail();
		}

		Node testNode = correlationServ.getCorrelatedSector(m, network);
		// node returned is not null
		Assert.assertNotNull(testNode);
		// relationships exist
		Assert.assertEquals(
				m,
				testNode.getSingleRelationship(Correlations.CORRELATED,
						Direction.OUTGOING)
						.getEndNode()
						.getSingleRelationship(Correlations.CORRELATED,
								Direction.OUTGOING).getEndNode());
		// node returned is correct
		Assert.assertEquals(sector, testNode);
	}

	@Test
	public void testGetCorrelatedSectorNoCorrelation() {
		Node m = null;
		try {

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			dm.addFile(new File(filename));

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("fake", "param");
			params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
			m = dm.addMeasurement(new File(filename).getName(), params);
		} catch (AWEException e) {
			LOGGER.error("Could not get correlated sector", e);
			fail();
		}

		Node testNode = correlationServ.getCorrelatedSector(m, network);
		// node returned is not null
		Assert.assertNull(testNode);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCorrelatedSectorMeasurementNull() {
		correlationServ.getCorrelatedSector(null, network);
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCorrelatedSectorNetworkNull() {
		correlationServ.getCorrelatedSector(network, null);
		// exception
	}

	@Test
	public void testGetCorrelatedNodes() {
		List<Node> ms = new ArrayList<Node>();
		Node sector = null;
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			// create sector
			sector = nwServ.createSector(nwServ.createNetworkElement(network,
					"index", "site", NetworkElementNodeType.SITE), "index",
					"name", "ci", "lac");

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			dm.addFile(new File(filename));

			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < 6; i++) {
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = dm
						.addMeasurement(new File(filename).getName(), params);
				// create correlations
				correlationServ
						.addCorrelationNodes(network, sector, dataset, m);
				ms.add(m);
			}
		} catch (AWEException e) {
			LOGGER.error("Could not add correlation.", e);
			fail();
		}

		for (Node node : correlationServ.getCorrelatedNodes(network, sector,
				dataset)) {
			// node returned is not null
			Assert.assertNotNull(node);
			// relationships exist
			Assert.assertEquals(
					sector,
					node.getSingleRelationship(Correlations.CORRELATED,
							Direction.INCOMING)
							.getStartNode()
							.getSingleRelationship(Correlations.CORRELATED,
									Direction.INCOMING).getStartNode());
			// node correct
			Assert.assertTrue(ms.contains(node));
		}
	}

	@Test
	public void testGetCorrelatedNodesNoNodes() {
		Node sector = null;
		try {
			// create sector
			sector = nwServ.createSector(nwServ.createNetworkElement(network,
					"index", "site", NetworkElementNodeType.SITE), "index",
					"name", "ci", "lac");

		} catch (AWEException e) {
			LOGGER.error("Could not add correlation.", e);
			fail();
		}
		Iterable<Node> it = correlationServ.getCorrelatedNodes(network, sector,
				dataset);
		// object returned not null
		Assert.assertNotNull(it);
		// iterator empty
		Assert.assertFalse(it.iterator().hasNext());
		;

	}

	@Test
	public void testGetAllCorrelatedNodes() {
		List<Node> ms = new ArrayList<Node>();
		List<Node> mms = new ArrayList<Node>();
		Node sector = null, file = null;
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			// create sector
			sector = nwServ.createSector(nwServ.createNetworkElement(network,
					"index", "site", NetworkElementNodeType.SITE), "index",
					"name", "ci", "lac");

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			file = dm.addFile(new File(filename));

			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < 6; i++) {
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = dm
						.addMeasurement(new File(filename).getName(), params);
				// create correlations
				correlationServ
						.addCorrelationNodes(network, sector, dataset, m);
				ms.add(m);
			}
			for (int i = 0; i < 6; i++) {
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = dm
						.addMeasurement(new File(filename).getName(), params);
				// don't create correlations
				mms.add(m);
			}
		} catch (AWEException e) {
			LOGGER.error("Could not add correlation.", e);
			fail();
		}

		for (Node node : correlationServ
				.getAllCorrelatedNodes(network, dataset)) {
			// node returned is not null
			Assert.assertNotNull(node);
			// relationships exist
			Assert.assertEquals(
					sector,
					node.getSingleRelationship(Correlations.CORRELATED,
							Direction.INCOMING)
							.getStartNode()
							.getSingleRelationship(Correlations.CORRELATED,
									Direction.INCOMING).getStartNode());
			// node correct
			Assert.assertTrue(ms.contains(node));
			Assert.assertFalse(mms.contains(node));
			// chain exists
			Assert.assertTrue(chainExists(file, node));
		}
	}

	@Test
	public void testGetAllCorrelatedNodesNoNodes() {
		Iterable<Node> it = correlationServ.getAllCorrelatedNodes(network,
				dataset);
		// object returned is not null
		Assert.assertNotNull(it);
		// iterator is empty
		Assert.assertFalse(it.iterator().hasNext());
	}

	@Test
	public void testGetCorrelatedDatasets() {
		List<Node> dss = new ArrayList<Node>();
		for (int i = 0; i < 7; i++) {
			try {
				Node ds = dsServ.createDataset(project, "dataset" + i,
						DatasetTypes.DRIVE, DriveTypes.ROMES);

				// create correlations
				correlationServ.createCorrelation(network, ds);
				dss.add(ds);
				tx.success();
				tx.finish();
			} catch (AWEException e) {
				LOGGER.error("Could not create dataset.", e);
				fail();
			}

		}
		tx = graphDatabaseService.beginTx();
		// iterator returned
		Iterable<Node> it = correlationServ.getCorrelatedDatasets(network);
		Assert.assertNotNull(it);
		Assert.assertTrue(it.iterator().hasNext());
		// all nodes returned
		for (Node node : it) {
			Assert.assertNotNull(node);
			Assert.assertTrue("" + node.getId(), dss.contains(node));
		}
	}

	@Test
	public void testGetCorrelatedNetworks() {
		Node ds1 = null, ds2 = null;
		List<Node> nws = new ArrayList<Node>();
		try {
			ds1 = dsServ.createDataset(project, "dataset1", DatasetTypes.DRIVE,
					DriveTypes.ROMES);

			ds2 = dsServ.createDataset(project, "dataset2", DatasetTypes.DRIVE,
					DriveTypes.ROMES);

			// create correlations
			correlationServ.createCorrelation(network, ds1);
			correlationServ.createCorrelation(network, ds2);

			Node network1 = dsServ.createDataset(project, "network 1",
					DatasetTypes.NETWORK);
			// create correlations
			correlationServ.createCorrelation(network1, ds2);

			nws.add(network);
			nws.add(network1);

			tx.success();
			tx.finish();
		} catch (AWEException e) {
			LOGGER.error("Could not create dataset.", e);
			fail();
		}

		tx = graphDatabaseService.beginTx();
		// iterator returned
		Iterable<Node> it = correlationServ.getCorrelatedNetworks(ds1);
		Assert.assertNotNull(it);
		Assert.assertTrue(it.iterator().hasNext());
		// all nodes returned
		for (Node node : it) {
			Assert.assertNotNull(node);
			Assert.assertEquals(network, node);
			System.out.println(node.getId());
		}

		// iterator returned
		it = correlationServ.getCorrelatedNetworks(ds2);
		Assert.assertNotNull(it);
		Assert.assertTrue(it.iterator().hasNext());
		// all nodes returned
		for (Node node : it) {
			Assert.assertNotNull(node);
			Assert.assertTrue(nws.contains(node));
			System.out.println(node.getId());
		}
	}

	private boolean chainExists(Node parent, Node child) {
		Iterator<Relationship> it = parent.getRelationships(
				DatasetRelationTypes.CHILD, Direction.OUTGOING).iterator();

		Node prevNode = null, node = null;
		if (it.hasNext()) {
			// prevNode is set to first child
			prevNode = it.next().getOtherNode(parent);
		} else {
			return false;
		}

		while (true) {
			if (prevNode.equals(child)) {
				return true;
			}
			it = prevNode.getRelationships(DatasetRelationTypes.NEXT,
					Direction.OUTGOING).iterator();
			if (it.hasNext()) {
				node = it.next().getOtherNode(prevNode);
			} else {
				return false;
			}
			prevNode = node;
		}
	}

}
