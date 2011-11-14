package org.amanzi.neo.services.model.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.CorrelationService;
import org.amanzi.neo.services.CorrelationService.Correlations;
import org.amanzi.neo.services.CorrelationServiceTest;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
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
import org.neo4j.graphdb.index.Index;

public class CorrelationModelTest extends AbstractNeoServiceTest {

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
	public void testCorrelationModel() {
		CorrelationModel cm = null;
		try {
			cm = new CorrelationModel(network, dataset);
		} catch (AWEException e) {
			LOGGER.error("Could not create correlation model.", e);
			fail(e.getMessage());
		}
		// object returned is not null
		Assert.assertNotNull(cm);
		// relationship to network exists
		Assert.assertEquals(
				network,
				cm.getRootNode()
						.getSingleRelationship(Correlations.CORRELATION,
								Direction.INCOMING).getStartNode());
		// relationship to dataset exists
		Assert.assertEquals(
				dataset,
				cm.getRootNode()
						.getSingleRelationship(Correlations.CORRELATED,
								Direction.OUTGOING)
						.getEndNode()
						.getSingleRelationship(Correlations.CORRELATION,
								Direction.INCOMING).getStartNode());
	}

	@Test
	public void testConstructorNoRecreate() {
		// correlation already exists
		Node corRoot = null;
		try {
			corRoot = correlationServ.createCorrelation(network, dataset);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create correlation.", e);
			fail();
		}

		CorrelationModel cm = null;
		try {
			cm = new CorrelationModel(network, dataset);
		} catch (AWEException e) {
			LOGGER.error("Could not create correlation model.", e);
			fail();
		}
		// object returned is not null
		Assert.assertNotNull(cm);
		// correlation root is not recreated
		Assert.assertEquals(corRoot, cm.getRootNode());
		// relationship to network
		Assert.assertEquals(
				network,
				cm.getRootNode()
						.getSingleRelationship(Correlations.CORRELATION,
								Direction.INCOMING).getStartNode());
		// relationship to dataset exists
		Assert.assertEquals(
				dataset,
				cm.getRootNode()
						.getSingleRelationship(Correlations.CORRELATED,
								Direction.OUTGOING)
						.getEndNode()
						.getSingleRelationship(Correlations.CORRELATION,
								Direction.INCOMING).getStartNode());
	}

	@Test
	public void testGetNetwork() {
		CorrelationModel cm = null;
		try {
			cm = new CorrelationModel(network, dataset);
		} catch (AWEException e) {
			LOGGER.error("Could not create correlation model.", e);
			fail(e.getMessage());
		}

		IDataElement nw = cm.getNetwork();
		// element returned is not null
		Assert.assertNotNull(nw);
		Node nwNode = ((DataElement) nw).getNode();
		// node is correct
		Assert.assertNotNull(nwNode);
		Assert.assertEquals(network, nwNode);
	}

	@Test
	public void testGetDataset() {
		CorrelationModel cm = null;
		try {
			cm = new CorrelationModel(network, dataset);
		} catch (AWEException e) {
			LOGGER.error("Could not create correlation model.", e);
			fail(e.getMessage());
		}

		IDataElement ds = cm.getDataset();
		// element returned is not null
		Assert.assertNotNull(ds);
		Node dsNode = ((DataElement) ds).getNode();
		// node is correct
		Assert.assertNotNull(dsNode);
		Assert.assertEquals(dataset, dsNode);
	}

	@Test
	public void testGetSectors() {
		List<Node> cor_sect = new ArrayList<Node>();
		List<Node> not_cor_sect = new ArrayList<Node>();
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			for (int i = 0; i < 5; i++) {
				// create sector
				Index<Node> index = graphDatabaseService.index().forNodes(
						"index");
				Node sector = nwServ.createSector(nwServ.getNetworkElement(
						network, index, "site", NetworkElementNodeType.SITE),
						index, "name" + i, "ci" + i, "lac" + i);

				// create measurement
				DriveModel dm = new DriveModel(null, dataset, null, null);
				dm.getFile(filename);

				for (int j = 0; j < 3; j++) {
					Map<String, Object> params = new HashMap<String, Object>();

					params.put("fake", "param");
					params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
					Node m = ((DataElement) dm.addMeasurement(
							new File(filename).getName(), params)).getNode();

					// create correlation
					correlationServ.addCorrelationNodes(network, sector,
							dataset, m);
				}
				cor_sect.add(sector);
			}
			for (int i = 0; i < 5; i++) {
				// create sector
				Index<Node> index = graphDatabaseService.index().forNodes(
						"index");
				Node sector = nwServ.createSector(nwServ.getNetworkElement(
						network, index, "site", NetworkElementNodeType.SITE),
						index, "name" + 10 + i, "ci" + 10 + i, "lac" + 10 + i);

				not_cor_sect.add(sector);
			}
		} catch (AWEException e) {
			LOGGER.error("Could not create correlated structure", e);
			fail(e.getMessage());
		}

		CorrelationModel cm = null;
		try {
			cm = new CorrelationModel(network, dataset);
		} catch (AWEException e) {
			LOGGER.error("Could not create correlation model.", e);
			fail();
		}
		// all the sectors returned
		try {
			for (IDataElement sElement : cm.getSectors()) {
				Node sector = ((DataElement) sElement).getNode();
				Assert.assertNotNull(sector);
				// sector is correct
				Assert.assertTrue(cor_sect.contains(sector));
				Assert.assertFalse(not_cor_sect.contains(sector));
				// sector has a proxy relationship
				Assert.assertTrue(sector.hasRelationship(
						Correlations.CORRELATED, Direction.OUTGOING));
				for (Relationship rel : sector
						.getSingleRelationship(Correlations.CORRELATED,
								Direction.OUTGOING)
						.getEndNode()
						.getRelationships(Correlations.CORRELATED,
								Direction.OUTGOING)) {
					// relationship properties are correct
					Assert.assertEquals(network.getId(),
							rel.getProperty(CorrelationService.NETWORK_ID, 0L));
					Assert.assertEquals(dataset.getId(),
							rel.getProperty(CorrelationService.DATASET_ID, 0L));
				}
			}
		} catch (AWEException e) {
			LOGGER.error("Could not get sectors.", e);
			fail();
		}
	}

	@Test
	public void testGetMeasurements() {
		List<Node> cor_ms = new ArrayList<Node>();
		List<Node> not_cor_ms = new ArrayList<Node>();
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			for (int i = 0; i < 5; i++) {
				// create sector
				Index<Node> index = graphDatabaseService.index().forNodes(
						"index");
				Node sector = nwServ.createSector(nwServ.getNetworkElement(
						network, index, "site", NetworkElementNodeType.SITE),
						index, "name" + i, "ci" + i, "lac" + i);

				// create measurement
				DriveModel dm = new DriveModel(null, dataset, null, null);
				dm.getFile(filename);

				for (int j = 0; j < 3; j++) {
					Map<String, Object> params = new HashMap<String, Object>();

					params.put("fake", "param");
					params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
					Node m = ((DataElement) dm.addMeasurement(
							new File(filename).getName(), params)).getNode();

					// create correlation
					correlationServ.addCorrelationNodes(network, sector,
							dataset, m);
					cor_ms.add(m);
				}

			}
			// create another network structure
			Index<Node> index = graphDatabaseService.index().forNodes("index");
			Node n = dsServ.createDataset(project, "nw", DatasetTypes.NETWORK);
			Node s = nwServ.createSector(nwServ.getNetworkElement(n, index,
					"site", NetworkElementNodeType.SITE), index, "name", "ci",
					"lac");

			DriveModel dm = new DriveModel(null, dataset, null, null);
			dm.getFile(filename);
			// create measurements
			for (int j = 0; j < 3; j++) {
				Map<String, Object> params = new HashMap<String, Object>();

				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = ((DataElement) dm.addMeasurement(
						new File(filename).getName(), params)).getNode();
				// create correlation
				correlationServ.addCorrelationNodes(n, s, dataset, m);
				not_cor_ms.add(m);
				// create correlation
				correlationServ.addCorrelationNodes(n, s, dataset,
						cor_ms.get(j));
			}
		} catch (AWEException e) {
			LOGGER.error("Could not create correlated structure", e);
			fail(e.getMessage());
		}

		CorrelationModel cm = null;
		try {
			cm = new CorrelationModel(network, dataset);
		} catch (AWEException e) {
			LOGGER.error("Could not create correlation model.", e);
			fail();
		}
		// all the measurements returned
		try {
			for (IDataElement mElement : cm.getMeasurements()) {
				Node m = ((DataElement) mElement).getNode();
				Assert.assertNotNull(m);
				// node correct
				Assert.assertNotNull(m);
				Assert.assertTrue(cor_ms.contains(m));
				Assert.assertFalse(not_cor_ms.contains(m));
				Relationship testRel = null;
				for (Relationship rel : m.getRelationships(
						Correlations.CORRELATED, Direction.INCOMING)) {
					if (rel.getProperty(CorrelationService.NETWORK_ID, 0L)
							.equals(network.getId())) {
						testRel = rel;
					}
				}
				// relationship is not null
				Assert.assertNotNull(testRel);
				// relationship properties are correct
				Assert.assertEquals(network.getId(),
						testRel.getProperty(CorrelationService.NETWORK_ID, 0L));
				Assert.assertEquals(dataset.getId(),
						testRel.getProperty(CorrelationService.DATASET_ID, 0L));

			}
		} catch (AWEException e) {
			LOGGER.error("Could not get measurements.", e);
			fail();
		}
	}

	@Test
	public void testGetCorrelatedNodes() {
		List<Node> sectors = new ArrayList<Node>();
		List<Node> not_cor_ms = new ArrayList<Node>();
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			for (int i = 0; i < 5; i++) {
				// create sector
				Index<Node> index = graphDatabaseService.index().forNodes(
						"index");
				Node sector = nwServ.createSector(nwServ.getNetworkElement(
						network, index, "site", NetworkElementNodeType.SITE),
						index, "name" + i, "ci" + i, "lac" + i);

				// create measurement
				DriveModel dm = new DriveModel(null, dataset, null, null);
				dm.getFile(filename);

				for (int j = 0; j < 3; j++) {
					Map<String, Object> params = new HashMap<String, Object>();

					params.put("fake", "param");
					params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
					Node m = ((DataElement) dm.addMeasurement(
							new File(filename).getName(), params)).getNode();

					// create correlation
					correlationServ.addCorrelationNodes(network, sector,
							dataset, m);
					sectors.add(sector);
				}

			}
			// create another dataset structure
			Node d = dsServ.createDataset(project, "ds", DatasetTypes.DRIVE,
					DriveTypes.ROMES);

			DriveModel dm = new DriveModel(null, d, null, null);
			dm.getFile(filename);
			// create measurements
			for (int j = 0; j < sectors.size(); j++) {
				Map<String, Object> params = new HashMap<String, Object>();

				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
				Node m = ((DataElement) dm.addMeasurement(
						new File(filename).getName(), params)).getNode();
				// create correlation
				correlationServ.addCorrelationNodes(network, sectors.get(j), d,
						m);
				not_cor_ms.add(m);
			}
		} catch (AWEException e) {
			LOGGER.error("Could not create correlated structure", e);
			fail(e.getMessage());
		}

		CorrelationModel cm = null;
		try {
			cm = new CorrelationModel(network, dataset);
		} catch (AWEException e) {
			LOGGER.error("Could not create correlation model.", e);
			fail();
		}
		for (Node sector : sectors) {
			// all nodes returned
			for (IDataElement nElement : cm.getCorrelatedNodes(sector)) {
				Node n = ((DataElement) nElement).getNode();
				Assert.assertNotNull(n);
				// node returned correct
				Assert.assertFalse(not_cor_ms.contains(n));
				// relationship to sector exists
				Assert.assertEquals(
						sector,
						n.getSingleRelationship(Correlations.CORRELATED,
								Direction.INCOMING)
								.getStartNode()
								.getSingleRelationship(Correlations.CORRELATED,
										Direction.INCOMING).getStartNode());
				Relationship rel = n.getSingleRelationship(
						Correlations.CORRELATED, Direction.INCOMING);
				// relationship not null
				Assert.assertNotNull(rel);
				// relationship properties correct
				Assert.assertEquals(network.getId(),
						rel.getProperty(CorrelationService.NETWORK_ID, 0L));
				Assert.assertEquals(dataset.getId(),
						rel.getProperty(CorrelationService.DATASET_ID, 0L));
			}
		}
	}

	@Test
	public void testGetCorrelatedSector() {
		List<Node> ms = new ArrayList<Node>();
		List<Node> not_cor_sectors = new ArrayList<Node>();
		try {
			// correlate
			correlationServ.createCorrelation(network, dataset);
			for (int i = 0; i < 5; i++) {
				// create sector
				Index<Node> index = graphDatabaseService.index().forNodes(
						"index");
				Node sector = nwServ.createSector(nwServ.getNetworkElement(
						network, index, "site", NetworkElementNodeType.SITE),
						index, "name" + i, "ci" + i, "lac" + i);

				// create measurement
				DriveModel dm = new DriveModel(null, dataset, null, null);
				dm.getFile(filename);

				for (int j = 0; j < 3; j++) {
					Map<String, Object> params = new HashMap<String, Object>();

					params.put("fake", "param");
					params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
					Node m = ((DataElement) dm.addMeasurement(
							new File(filename).getName(), params)).getNode();

					// create correlation
					correlationServ.addCorrelationNodes(network, sector,
							dataset, m);
					ms.add(m);
				}

			}
			// create another network structure
			Index<Node> index = graphDatabaseService.index().forNodes("index");
			for (int j = 0; j < 3; j++) {
				not_cor_sectors.add(nwServ.createSector(nwServ
						.getNetworkElement(network, index, "site",
								NetworkElementNodeType.SITE), index, "name" + j
						+ 10, "ci", "lac"));

			}
		} catch (AWEException e) {
			LOGGER.error("Could not create correlated structure", e);
			fail(e.getMessage());
		}

		CorrelationModel cm = null;
		try {
			cm = new CorrelationModel(network, dataset);
		} catch (AWEException e) {
			LOGGER.error("Could not create correlation model.", e);
			fail();
		}
		for (Node m : ms) {
			IDataElement sectEl = cm.getCorrelatedSector(m);

			Node sect = ((DataElement) sectEl).getNode();

			// the valid sector returned
			Assert.assertNotNull(sect);
			Assert.assertFalse(not_cor_sectors.contains(sect));
			// relationship exists
			Assert.assertEquals(
					sect,
					m.getSingleRelationship(Correlations.CORRELATED,
							Direction.INCOMING)
							.getStartNode()
							.getSingleRelationship(Correlations.CORRELATED,
									Direction.INCOMING).getStartNode());
		}
	}

}
