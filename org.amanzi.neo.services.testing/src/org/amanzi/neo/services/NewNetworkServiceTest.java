package org.amanzi.neo.services;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class NewNetworkServiceTest extends AbstractAWETest {
	private static Logger LOGGER = Logger
			.getLogger(NewNetworkServiceTest.class);
	private static NewNetworkService networkService;
	private static GraphDatabaseService graphDb;
	private static final String databasePath = getDbLocation();
	private static Transaction tx;
	private static Node parent;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDb = new EmbeddedGraphDatabase(databasePath);
		LOGGER.info("Database created in folder " + databasePath);
		networkService = new NewNetworkService(graphDb);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (graphDb != null) {
			graphDb.shutdown();
			LOGGER.info("Database shut down");
		}
		clearDb();
	}

	// tests for methods of NewAbstractService class
	// +
	@Test
	public void testGetIndexKey() {
		parent = getNewNE();
		// the string returned is valid for every type of network element
		for (INodeType type : NetworkElementNodeType.values()) {
			String key = networkService.getIndexKey(parent, type);
			Assert.assertEquals(
					String.valueOf(parent.getId()) + "@" + type.getId(), key);
		}
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testGetIndexKeyRootNull() {
		// exception
		networkService.getIndexKey(null, NetworkElementNodeType.values()[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetIndexKeyTypeNull() {
		// exception
		networkService.getIndexKey(parent, null);
	}

	// // tests for methods of NewNetworkService class
	// +
	@Test
	public void testCreateNetworkElement() {
		parent = getNewNE();
		for (INodeType type : NetworkElementNodeType.values()) {
			if (type.equals(NetworkElementNodeType.SECTOR)) {
				continue;
			}

			String indexName = networkService.getIndexKey(parent, type);
			try {
				Node node = networkService.createNetworkElement(parent,
						indexName, type.getId(), type);

				// the node returned is not null
				Assert.assertNotNull(node);
				// the relationship from parent exists
				Assert.assertEquals(
						parent,
						node.getRelationships(DatasetRelationTypes.CHILD,
								Direction.INCOMING).iterator().next()
								.getOtherNode(node));
				// all properties set
				Assert.assertEquals(type.getId(),
						node.getProperty(NewNetworkService.NAME, null));
				Assert.assertEquals(type.getId(),
						node.getProperty(NewNetworkService.TYPE, null));
				// the element is indexed
				Assert.assertEquals(node, graphDb.index().forNodes(indexName)
						.get(NewNetworkService.NAME, type.getId()).getSingle());
			} catch (DatabaseException e) {
				LOGGER.error("could not create network element", e);
				fail();
			} catch (IllegalNodeDataException e) {
				LOGGER.error("could not create network element", e);
				fail();
			}
		}
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testCreateNEParentNull() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		try {
			networkService.createNetworkElement(null, indexName, "name", type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNEIndexNameNull() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		try {
			networkService.createNetworkElement(parent, null, "name", type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNEIndexNameEmpty() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		try {
			networkService.createNetworkElement(parent, "", "name", type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testCreateNENameNull() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		try {
			networkService.createNetworkElement(parent, indexName, null, type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testCreateNENameEmpty() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		try {
			networkService.createNetworkElement(parent, indexName, "", type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNETypeNull() {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		try {
			networkService
					.createNetworkElement(parent, indexName, "name", null);
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNETypeSector() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.SECTOR;
		String indexName = networkService.getIndexKey(parent, type);
		try {
			networkService
					.createNetworkElement(parent, indexName, "name", type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	// +
	@Test
	public void testFindNetworkElement() {
		parent = getNewNE();
		List<Node> nodes = new ArrayList<Node>();
		for (INodeType type : NetworkElementNodeType.values()) {
			if (type.equals(NetworkElementNodeType.SECTOR)) {
				continue;
			}
			String indexName = networkService.getIndexKey(parent, type);
			try {
				Node node = networkService.createNetworkElement(parent,
						indexName, type.getId(), type);
				nodes.add(node);
			} catch (AWEException e) {
				LOGGER.error("could not create network element", e);
				fail();
			}
		}
		// the element exists
		for (INodeType type : NetworkElementNodeType.values()) {
			if (type.equals(NetworkElementNodeType.SECTOR)) {
				continue;
			}
			String indexName = networkService.getIndexKey(parent, type);
			Node node = networkService.findNetworkElement(indexName,
					type.getId());

			// the node returned is not null
			Assert.assertNotNull(node);
			// the element name property is right
			Assert.assertEquals(type.getId(),
					node.getProperty(NewAbstractService.NAME, null));
			// the relationship from parent exists
			Assert.assertEquals(
					parent,
					node.getRelationships(DatasetRelationTypes.CHILD,
							Direction.INCOMING).iterator().next()
							.getOtherNode(node));
			// node is correct
			Assert.assertTrue(nodes.contains(node));
		}
	}

	@Test
	public void testFindNetworkElementNotFound() {
		parent = getNewNE();
		String indexName = networkService.getIndexKey(parent,
				NetworkElementNodeType.values()[0]);
		String name = "elementNOTfound";
		// the element does not exist
		Node node = networkService.findNetworkElement(indexName, name);
		// the node returned is null
		Assert.assertNull(node);
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testFindNEIndexNameNull() {
		// exception
		networkService.findNetworkElement(null, "name");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindNEIndexNameEmpty() {
		// exception
		networkService.findNetworkElement("", "name");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindNENameEmpty() {
		// exception
		networkService.findNetworkElement("index", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindNENameNull() {
		// exception
		networkService.findNetworkElement("index", null);
	}

	// +
	@Test
	public void testGetNetworkElement() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		Node node = null;
		try {
			node = networkService.createNetworkElement(parent, indexName,
					type.getId(), type);
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
		// element exists
		try {
			Node testNode = networkService.getNetworkElement(parent, indexName,
					type.getId(), type);
			// node returned is not null
			Assert.assertNotNull(testNode);
			// name is right
			Assert.assertEquals(type.getId(),
					testNode.getProperty(NewNetworkService.NAME, null));
			// the element is not recreated
			Assert.assertEquals(node, testNode);
			// the relationship from parent exists
			Assert.assertEquals(
					parent,
					testNode.getRelationships(DatasetRelationTypes.CHILD,
							Direction.INCOMING).iterator().next()
							.getOtherNode(node));

		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test
	public void testGetNetworkElementParamsNull() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		Node node = null;
		try {
			node = networkService.createNetworkElement(parent, indexName,
					type.getId(), type);
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
		// element exists, parent and type - null
		try {
			Node testNode = networkService.getNetworkElement(null, indexName,
					type.getId(), null);

			// node returned is not null
			Assert.assertNotNull(testNode);
			// name is right
			Assert.assertEquals(type.getId(),
					testNode.getProperty(NewNetworkService.NAME, null));
			// the element is not recreated
			Assert.assertEquals(node, testNode);
			// the relationship from parent exists
			Assert.assertEquals(
					parent,
					node.getRelationships(DatasetRelationTypes.CHILD,
							Direction.INCOMING).iterator().next()
							.getOtherNode(node));
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test
	public void testGetNetworkElementNoElement() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		// element does not exist
		try {
			Node node = networkService.getNetworkElement(parent, indexName,
					type.getId(), type);
			// node returned is not null
			Assert.assertNotNull(node);
			// name is right
			Assert.assertEquals(type.getId(),
					node.getProperty(NewNetworkService.NAME, null));
			// type is right
			Assert.assertEquals(type.getId(),
					node.getProperty(NewNetworkService.TYPE, null));
			// element is indexed
			Assert.assertEquals(
					node,
					graphDb.index().forNodes(indexName)
							.get(NewNetworkService.NAME, type.getId())
							.getSingle());
			// the relationship from parent exists
			Assert.assertEquals(
					parent,
					node.getRelationships(DatasetRelationTypes.CHILD,
							Direction.INCOMING).iterator().next()
							.getOtherNode(node));
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testGetNEParentNull() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		// element does not exist, parent is null
		try {
			networkService.getNetworkElement(null, indexName, type.getId(),
					type);
			// exception
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNETypeNull() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		// element does not exist, type is null
		try {
			networkService.getNetworkElement(parent, indexName, type.getId(),
					null);
			// exception
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNENameNull() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		// name is null
		try {
			networkService.getNetworkElement(parent, indexName, null, type);
			// exception
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNEIndexNameNull() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		// indexName is null
		try {
			networkService.getNetworkElement(parent, null, type.getId(), type);
			// exception
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNENameEmpty() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		String indexName = networkService.getIndexKey(parent, type);
		// name is ""
		try {
			networkService.getNetworkElement(parent, indexName, "", type);
			// exception
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNEIndexNameNEmpty() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		// indexName is ""
		try {
			networkService.getNetworkElement(parent, "", type.getId(), type);
			// exception
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	// +
	@Test
	public void testCreateSector() {

		// all parameters are set, parent is correct
		Node sector = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			String indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, indexName, "name",
					"ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// sector is not null
		Assert.assertNotNull(sector);
		// sector has valid properties
		Assert.assertEquals("name",
				sector.getProperty(NewNetworkService.NAME, null));
		Assert.assertEquals("ci",
				sector.getProperty(NewNetworkService.CELL_INDEX, null));
		Assert.assertEquals("lac",
				sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
		Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
				sector.getProperty(NewNetworkService.TYPE, null));
		// relation from parent is created
		Assert.assertEquals(
				parent,
				sector.getRelationships(DatasetRelationTypes.CHILD,
						Direction.INCOMING).iterator().next()
						.getOtherNode(sector));
		// sector is indexed
		Index<Node> index = graphDb.index().forNodes(
				networkService.getIndexKey(parent,
						NetworkElementNodeType.SECTOR));
		Assert.assertEquals(sector, index.get(NewNetworkService.NAME, "name")
				.getSingle());
		Assert.assertEquals(sector,
				index.get(NewNetworkService.LOCATION_AREA_CODE, "lac")
						.getSingle());
		Assert.assertEquals(sector,
				index.get(NewNetworkService.CELL_INDEX, "ci").getSingle());
	}

	@Test
	public void testCreateSectorNameSet() {
		// only name is set, parent is correct
		Node sector = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			String indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, indexName, "name",
					null, null);

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// sector is not null
		Assert.assertNotNull(sector);
		// sector has valid properties
		Assert.assertEquals("name",
				sector.getProperty(NewNetworkService.NAME, null));
		Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
				sector.getProperty(NewNetworkService.TYPE, null));
		// relation from parent is created
		Assert.assertEquals(
				parent,
				sector.getRelationships(DatasetRelationTypes.CHILD,
						Direction.INCOMING).iterator().next()
						.getOtherNode(sector));
		// sector is indexed
		Index<Node> index = graphDb.index().forNodes(
				networkService.getIndexKey(parent,
						NetworkElementNodeType.SECTOR));
		Assert.assertEquals(sector, index.get(NewNetworkService.NAME, "name")
				.getSingle());
	}

	@Test
	public void testCreateSectorCILACSet() {
		// CILAC are set, parent is correct
		Node sector = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			String indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, indexName, null, "ci",
					"lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// sector is not null
		Assert.assertNotNull(sector);
		// sector has valid properties
		Assert.assertEquals("ci",
				sector.getProperty(NewNetworkService.CELL_INDEX, null));
		Assert.assertEquals("lac",
				sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
		Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
				sector.getProperty(NewNetworkService.TYPE, null));
		// relation from parent is created
		Assert.assertEquals(
				parent,
				sector.getRelationships(DatasetRelationTypes.CHILD,
						Direction.INCOMING).iterator().next()
						.getOtherNode(sector));
		// sector is indexed
		Index<Node> index = graphDb.index().forNodes(
				networkService.getIndexKey(parent,
						NetworkElementNodeType.SECTOR));
		Assert.assertEquals(sector,
				index.get(NewNetworkService.LOCATION_AREA_CODE, "lac")
						.getSingle());
		Assert.assertEquals(sector,
				index.get(NewNetworkService.CELL_INDEX, "ci").getSingle());
	}

	// -
	@Test(expected = IllegalNodeDataException.class)
	public void testCreateSectorNoneSet() throws IllegalNodeDataException {
		// properties are not set
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			String indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, null, null, null);

			tx.success();
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// exception
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testCreateSectorCISet() throws IllegalNodeDataException {
		// only CI set
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			String indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, null, "ci", null);

			tx.success();
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// exception
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testCreateSectorLACSet() throws IllegalNodeDataException {
		// only LAC set
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			String indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, null, null, "lac");

			tx.success();
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSectorParentWrong() {
		// parent is not SITE
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			String indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSectorParentNull() {
		// parent is null
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			String indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(null, indexName, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSectorIndexNull() {
		// indexName null
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			networkService.createSector(parent, null, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSectorIndexEmpty() {
		// indexName ""
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			networkService.createSector(parent, "", "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}
		// exception
	}

	// +
	@Test
	public void testFindSector() {
		// sector exists, all params set
		Node sector = null;
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, indexName, "name",
					"ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		Node testNode = networkService.findSector(indexName, "name", "ci",
				"lac");
		// correct node is found
		Assert.assertEquals(sector, testNode);
		// sector has valid properties
		Assert.assertEquals("name",
				sector.getProperty(NewNetworkService.NAME, null));
		Assert.assertEquals("ci",
				sector.getProperty(NewNetworkService.CELL_INDEX, null));
		Assert.assertEquals("lac",
				sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
		Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
				sector.getProperty(NewNetworkService.TYPE, null));
		// relation from parent is created
		Assert.assertEquals(
				parent,
				sector.getRelationships(DatasetRelationTypes.CHILD,
						Direction.INCOMING).iterator().next()
						.getOtherNode(sector));
	}

	@Test
	public void testFindSectorNameSet() {
		// sector exists, name set
		Node sector = null;
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, indexName, "name",
					"ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		Node testNode = networkService
				.findSector(indexName, "name", null, null);
		// correct node is found
		Assert.assertEquals(sector, testNode);
		// sector has valid properties
		Assert.assertEquals("name",
				sector.getProperty(NewNetworkService.NAME, null));
		Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
				sector.getProperty(NewNetworkService.TYPE, null));
		// relation from parent is created
		Assert.assertEquals(
				parent,
				sector.getRelationships(DatasetRelationTypes.CHILD,
						Direction.INCOMING).iterator().next()
						.getOtherNode(sector));
	}

	@Test
	public void testFindSectorCILACSet() {
		// sector exists, CI+LAC set
		Node sector = null;
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, indexName, "name",
					"ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		Node testNode = networkService.findSector(indexName, null, "ci", "lac");
		// correct node is found
		Assert.assertEquals(sector, testNode);
		// sector has valid properties
		Assert.assertEquals("ci",
				sector.getProperty(NewNetworkService.CELL_INDEX, null));
		Assert.assertEquals("lac",
				sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
		Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
				sector.getProperty(NewNetworkService.TYPE, null));
		// relation from parent is created
		Assert.assertEquals(
				parent,
				sector.getRelationships(DatasetRelationTypes.CHILD,
						Direction.INCOMING).iterator().next()
						.getOtherNode(sector));
	}

	@Test
	public void testFindSectorNotFound() {
		// sector is not there, all set
		parent = getNewNE();
		String indexName = networkService.getIndexKey(parent,
				NetworkElementNodeType.SECTOR);

		Node testNode = networkService.findSector(indexName, "name", "ci",
				"lac");

		// node returned is null
		Assert.assertNull(testNode);
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorNoneSet() {
		// sector exists, none set
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		networkService.findSector(indexName, null, null, null);
		// exception

	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorCISet() {
		// sector exists, CI set
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		networkService.findSector(indexName, null, "ci", null);
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorLACSet() {
		// sector exists, LAC set
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		networkService.findSector(indexName, null, null, "lac");
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorIndexNull() {
		// sector exists, all set, indexName null
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		networkService.findSector(null, "name", "ci", "lac");
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorIndexEmpty() {
		// sector exists, all set, indexName ""
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, indexName, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		networkService.findSector("", "name", "ci", "lac");
		// exception
		fail("Not yet implemented"); // TODO
	}

	// +
	@Test
	public void testGetSector() {
		// sector exists
		Node sector = null;
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, indexName, "name",
					"ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create sector", e);
			fail();
		} finally {
			tx.finish();
		}

		try {
			Node testNode = networkService.getSector(parent, indexName, "name",
					"ci", "lac");

			// sector not recreated
			Assert.assertEquals(sector, testNode);
			// all params correct
			Assert.assertEquals("name",
					sector.getProperty(NewNetworkService.NAME, null));
			Assert.assertEquals("ci",
					sector.getProperty(NewNetworkService.CELL_INDEX, null));
			Assert.assertEquals("lac", sector.getProperty(
					NewNetworkService.LOCATION_AREA_CODE, null));
			Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
					sector.getProperty(NewNetworkService.TYPE, null));
			// relation from parent is created
			Assert.assertEquals(
					parent,
					sector.getRelationships(DatasetRelationTypes.CHILD,
							Direction.INCOMING).iterator().next()
							.getOtherNode(sector));
		} catch (AWEException e) {
			LOGGER.error("could not create sector", e);
			fail();
		}
	}

	@Test
	public void testGetSectorParentNull() {
		// sector exists
		Node sector = null;
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, indexName, "name",
					"ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create sector", e);
			fail();
		} finally {
			tx.finish();
		}

		try {
			Node testNode = networkService.getSector(null, indexName, "name",
					"ci", "lac");

			// sector not recreated
			Assert.assertEquals(sector, testNode);
			// all params correct
			Assert.assertEquals("name",
					sector.getProperty(NewNetworkService.NAME, null));
			Assert.assertEquals("ci",
					sector.getProperty(NewNetworkService.CELL_INDEX, null));
			Assert.assertEquals("lac", sector.getProperty(
					NewNetworkService.LOCATION_AREA_CODE, null));
			Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
					sector.getProperty(NewNetworkService.TYPE, null));
			// relation from parent is created
			Assert.assertEquals(
					parent,
					sector.getRelationships(DatasetRelationTypes.CHILD,
							Direction.INCOMING).iterator().next()
							.getOtherNode(sector));
		} catch (AWEException e) {
			LOGGER.error("could not create sector", e);
			fail();
		}
	}

	@Test
	public void testGetSectorNoSector() {
		// sector !exists
		String indexName = null;
		tx = graphDb.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			indexName = networkService.getIndexKey(parent,
					NetworkElementNodeType.SECTOR);

			tx.success();
		} finally {
			tx.finish();
		}

		try {
			Node testNode = networkService.getSector(parent, indexName, "name",
					"ci", "lac");

			// sector created
			Assert.assertNotNull(testNode);
			// all params correct
			Assert.assertEquals("name",
					testNode.getProperty(NewNetworkService.NAME, null));
			Assert.assertEquals("ci",
					testNode.getProperty(NewNetworkService.CELL_INDEX, null));
			Assert.assertEquals("lac", testNode.getProperty(
					NewNetworkService.LOCATION_AREA_CODE, null));
			Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(),
					testNode.getProperty(NewNetworkService.TYPE, null));
			// relation from parent is created
			Assert.assertEquals(
					parent,
					testNode.getRelationships(DatasetRelationTypes.CHILD,
							Direction.INCOMING).iterator().next()
							.getOtherNode(testNode));
			// sector is indexed
			Index<Node> index = graphDb.index().forNodes(
					networkService.getIndexKey(parent,
							NetworkElementNodeType.SECTOR));
			Assert.assertEquals(testNode,
					index.get(NewNetworkService.NAME, "name").getSingle());
			Assert.assertEquals(testNode,
					index.get(NewNetworkService.LOCATION_AREA_CODE, "lac")
							.getSingle());
			Assert.assertEquals(testNode,
					index.get(NewNetworkService.CELL_INDEX, "ci").getSingle());
		} catch (AWEException e) {
			LOGGER.error("could not create sector", e);
			fail();
		}
	}

	@Test
	public void testFindAllNetworkElements() {
		parent = getNewNE();
		Map<INodeType, List<Node>> nodes = new HashMap<INodeType, List<Node>>();
		for (INodeType type : NetworkElementNodeType.values()) {
			nodes.put(type, new ArrayList<Node>());
		}
		try {
			for (int i = 0; i < 4; i++) {
				Node network = networkService.createNetworkElement(parent,
						networkService.getIndexKey(parent,
								NetworkElementNodeType.NETWORK), "" + i,
						NetworkElementNodeType.NETWORK);
				nodes.get(NetworkElementNodeType.NETWORK).add(network);
				for (int j = 0; j < 4; j++) {
					Node bsc = networkService.createNetworkElement(network,
							networkService.getIndexKey(network,
									NetworkElementNodeType.BSC), "" + j,
							NetworkElementNodeType.BSC);
					nodes.get(NetworkElementNodeType.BSC).add(bsc);
					for (int k = 0; k < 4; k++) {
						Node site = networkService.createNetworkElement(bsc,
								networkService.getIndexKey(bsc,
										NetworkElementNodeType.SITE), "" + k,
								NetworkElementNodeType.SITE);
						nodes.get(NetworkElementNodeType.SITE).add(site);
						for (int l = 0; l < 4; l++) {
							Node sector = networkService.createSector(site,
									networkService.getIndexKey(site,
											NetworkElementNodeType.SECTOR), ""
											+ l, "" + l, "" + l);
							nodes.get(NetworkElementNodeType.SECTOR)
									.add(sector);
						}
					}
				}
			}
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}

		for (INodeType type : NetworkElementNodeType.values()) {
			Iterable<Node> it = networkService.findAllNetworkElements(parent,
					type);
			for (Node node : it) {
				// type is valid
				Assert.assertEquals(type.getId(),
						node.getProperty(NewNetworkService.TYPE, null));
				// all elements are found
				Assert.assertTrue(nodes.get(type).contains(node));
			}
		}
	}

	private Node getNewNE() {
		Transaction tx = graphDb.beginTx();
		Node node = null;
		try {
			node = graphDb.createNode();
			tx.success();
		} catch (Exception e) {
			LOGGER.error("Could not create node", e);
		} finally {
			tx.finish();
		}
		return node;
	}

}
