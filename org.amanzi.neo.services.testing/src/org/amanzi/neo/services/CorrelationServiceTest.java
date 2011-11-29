package org.amanzi.neo.services;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.CorrelationService.CorrelationNodeTypes;
import org.amanzi.neo.services.CorrelationService.Correlations;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

public class CorrelationServiceTest extends AbstractNeoServiceTest {

	private static Logger LOGGER = Logger
			.getLogger(CorrelationServiceTest.class);

	private static CorrelationService correlationServ;
	private static DatasetService dsServ;
	private static NetworkService nwServ;
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

		new LogStarter().earlyStartup();
		correlationServ = NeoServiceFactory.getInstance().getCorrelationService();
		dsServ = NeoServiceFactory.getInstance().getDatasetService();
		nwServ = NeoServiceFactory.getInstance().getNetworkService();
		prServ = NeoServiceFactory.getInstance().getProjectService();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		clearDb();
	}

	@Before
	public final void before() {
		try {
			count++;
			project = prServ.createProject("project" + count);
			network = dsServ.createDataset(project, "network",
					DatasetTypes.NETWORK);
			dataset = dsServ.createDataset(project, "dataset",
					DatasetTypes.DRIVE, DriveTypes.ROMES, DriveNodeTypes.M);
		} catch (AWEException e) {
			LOGGER.error("Could not create test nodes.", e);
		}
	}

	@Test
	public void testTest() {
		Assert.assertNotNull(project);
		Assert.assertNotNull(dataset);
		Assert.assertNotNull(network);
	}

	@Test
	public void testCreateCorrelation() throws AWEException {
		// create correlation
		Node correlation = null;
		try {
			correlation = correlationServ.createCorrelation(network, dataset);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create orrelation", e);
		}
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
				correlation.getProperty(AbstractService.TYPE, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCorrelationNetworkNull() throws AWEException {
		// exception
		try {
			correlationServ.createCorrelation(null, dataset);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create correlation", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCorrelationDatasetNull() throws AWEException {
		// exception
		try {
			correlationServ.createCorrelation(network, null);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create correlation", e);
			fail();
		}
	}

	@Test
	public void testGetCorrelationRoot() throws AWEException {
		// root exists
		Node corRoot = null;
		try {
			corRoot = correlationServ.createCorrelation(network, dataset);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create correlation", e);
			fail();
		}

		Node testNode = null;
		try {
			testNode = correlationServ.getCorrelationRoot(network);
		} catch (DatabaseException e) {
			LOGGER.error("Could not get correlation root", e);
			fail();
		}
		// node returned not null
		Assert.assertNotNull(testNode);
		// node correct
		Assert.assertEquals(corRoot, testNode);
	}

	@Test
	public void testGetCorrelationRootNoRoot() throws AWEException {
		// no root
		Node testNode = null;
		try {
			testNode = correlationServ.getCorrelationRoot(network);
		} catch (DatabaseException e) {
			LOGGER.error("Could not get correlation root.", e);
			fail();
		}
		// node returned is not null
		Assert.assertNotNull(testNode);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCorrelationRootNetworkNull() throws AWEException {
		// exception
		try {
			correlationServ.getCorrelationRoot(null);
		} catch (DatabaseException e) {
			LOGGER.error("Could not get correation root.", e);
			fail();
		}
	}

	@Test
	public void testAddCorrelationNodes() {
		List<Node> ms = new ArrayList<Node>();
		try {
			Index<Node> index = graphDatabaseService.index().forNodes("index");
			// correlate
			correlationServ.createCorrelation(network, dataset);
			// create sector
			Node sector = nwServ.createSector(nwServ.createNetworkElement(
					network, index, "site", NetworkElementNodeType.SITE),
					index, "name", "ci", "lac");

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			dm.addFile(new File(filename));

			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < 6; i++) {
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = ((DataElement) dm.addMeasurement(
						new File(filename).getName(), params)).getNode();
				ms.add(m);

				// create correlation
				Node cr = correlationServ.addCorrelationNodes(network, sector,
						dataset, m);
				// node returned is not null
				Assert.assertNotNull(cr);
				// type is correct
				Assert.assertEquals(CorrelationNodeTypes.PROXY.getId(),
						cr.getProperty(AbstractService.TYPE, null));
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
			Index<Node> index = graphDatabaseService.index().forNodes("index");
			// correlate
			correlationServ.createCorrelation(network, dataset);
			// create sector
			sector = nwServ.createSector(nwServ.createNetworkElement(network,
					index, "site", NetworkElementNodeType.SITE), index, "name",
					"ci", "lac");

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			dm.addFile(new File(filename));

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("fake", "param");
			params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
			m = ((DataElement) dm.addMeasurement(new File(filename).getName(),
					params)).getNode();

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
			m = ((DataElement) dm.addMeasurement(new File(filename).getName(),
					params)).getNode();
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
			Index<Node> index = graphDatabaseService.index().forNodes("index");
			sector = nwServ.createSector(nwServ.createNetworkElement(network,
					index, "site", NetworkElementNodeType.SITE), index, "name",
					"ci", "lac");

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			dm.addFile(new File(filename));

			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < 6; i++) {
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = ((DataElement) dm.addMeasurement(
						new File(filename).getName(), params)).getNode();
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
			Index<Node> index = graphDatabaseService.index().forNodes("index");
			sector = nwServ.createSector(nwServ.createNetworkElement(network,
					index, "site", NetworkElementNodeType.SITE), index, "name",
					"ci", "lac");

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
	public void testGetAllCorrelatedNodes() throws AWEException {
		List<Node> ms = new ArrayList<Node>();
		List<Node> mms = new ArrayList<Node>();
		Node sector = null, file = null;
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			// create sector
			Index<Node> index = graphDatabaseService.index().forNodes("index");
			sector = nwServ.createSector(nwServ.createNetworkElement(network,
					index, "site", NetworkElementNodeType.SITE), index, "name",
					"ci", "lac");

			// create measurement
			DriveModel dm = new DriveModel(null, dataset, null, null);
			file = ((DataElement) dm.addFile(new File(filename))).getNode();

			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < 6; i++) {
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = ((DataElement) dm.addMeasurement(
						new File(filename).getName(), params)).getNode();
				// create correlations
				correlationServ
						.addCorrelationNodes(network, sector, dataset, m);
				ms.add(m);
			}
			for (int i = 0; i < 6; i++) {
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = ((DataElement) dm.addMeasurement(
						new File(filename).getName(), params)).getNode();
				// don't create correlations
				mms.add(m);
			}
		} catch (AWEException e) {
			LOGGER.error("Could not add correlation.", e);
			fail();
		}

		try {
			for (Node node : correlationServ.getAllCorrelatedNodes(network,
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
				Assert.assertFalse(mms.contains(node));
				// chain exists
				Assert.assertTrue(chainExists(file, node));
			}
		} catch (DatabaseException e) {
			LOGGER.error("Could not get correlated nodes.", e);
			fail();
		}
	}

	@Test
	public void testGetAllCorrelatedNodesNoNodes() {
		Iterable<Node> it = null;
		try {
			it = correlationServ.getAllCorrelatedNodes(network, dataset);
		} catch (DatabaseException e) {
			LOGGER.error("Could not get correlated nodes.", e);
			fail();
		}
		// object returned is not null
		Assert.assertNotNull(it);
		// iterator is empty
		Assert.assertFalse(it.iterator().hasNext());
	}

	@Test
	public void testGetCorrelatedDatasets() throws AWEException {
		List<Node> dss = new ArrayList<Node>();
		for (int i = 0; i < 7; i++) {
			try {
				Node ds = dsServ.createDataset(project, "dataset" + i,
						DatasetTypes.DRIVE, DriveTypes.ROMES, DriveNodeTypes.M);

				// create correlations
				correlationServ.createCorrelation(network, ds);
				dss.add(ds);
			} catch (AWEException e) {
				LOGGER.error("Could not create dataset.", e);
				fail();
			}

		}
		// iterator returned
		Iterable<Node> it = null;
		try {
			it = correlationServ.getCorrelatedDatasets(network);
		} catch (DatabaseException e) {
			LOGGER.error("Could not get correlated datasets.", e);
			fail();
		}
		Assert.assertNotNull(it);
		Assert.assertTrue(it.iterator().hasNext());
		// all nodes returned
		for (Node node : it) {
			Assert.assertNotNull(node);
			Assert.assertTrue("" + node.getId(), dss.contains(node));
		}
	}

	@Test
	public void testGetCorrelatedNetworks() throws AWEException {
		Node ds1 = null, ds2 = null;
		List<Node> nws = new ArrayList<Node>();
		try {
			ds1 = dsServ.createDataset(project, "dataset1", DatasetTypes.DRIVE,
					DriveTypes.ROMES, DriveNodeTypes.M);

			ds2 = dsServ.createDataset(project, "dataset2", DatasetTypes.DRIVE,
					DriveTypes.ROMES, DriveNodeTypes.M);

			// create correlations
			correlationServ.createCorrelation(network, ds1);
			correlationServ.createCorrelation(network, ds2);

			Node network1 = dsServ.createDataset(project, "network 1",
					DatasetTypes.NETWORK);
			// create correlations
			correlationServ.createCorrelation(network1, ds2);

			nws.add(network);
			nws.add(network1);			
		} catch (AWEException e) {
			LOGGER.error("Could not create dataset.", e);
			fail();
		}

		// iterator returned
		Iterable<Node> it = null;
		try {
			it = correlationServ.getCorrelatedNetworks(ds1);
		} catch (DatabaseException e) {
			LOGGER.error("Could not get correlated networks.", e);
			fail();
		}
		Assert.assertNotNull(it);
		Assert.assertTrue(it.iterator().hasNext());
		// all nodes returned
		for (Node node : it) {
			Assert.assertNotNull(node);
			Assert.assertEquals(network, node);
			System.out.println(node.getId());
		}

		// iterator returned
		try {
			it = correlationServ.getCorrelatedNetworks(ds2);
		} catch (DatabaseException e) {
			LOGGER.error("Could not get correlated networks.", e);
			fail();
		}
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
