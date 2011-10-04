package org.amanzi.neo.services;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.testing.AbstractAWETest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

public class NewNetworkServiceTest extends AbstractAWETest {
	private static Logger LOGGER = Logger
			.getLogger(NewNetworkServiceTest.class);
	private static NewNetworkService networkService;
	private static final String databasePath = getDbLocation();
	private static Transaction tx;
	private static Node parent;

	private final static String DEFAULT_SELECTION_LIST_NAME = "Selection List";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();
		LOGGER.info("Database created in folder " + databasePath);
		networkService = NeoServiceFactory.getInstance().getNewNetworkService();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		clearDb();
	}

	// tests for methods of NewAbstractService class
	// +
	@Test
	public void testGetIndexKey() {
		parent = getNewNE();
		// the string returned is valid for every type of network element
		for (INodeType type : NetworkElementNodeType.values()) {
			String key = NewAbstractService.getIndexKey(parent, type);
			Assert.assertEquals(
					String.valueOf(parent.getId()) + "@" + type.getId(), key);
		}
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testGetIndexKeyRootNull() {
		// exception
		NewAbstractService
				.getIndexKey(null, NetworkElementNodeType.values()[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetIndexKeyTypeNull() {
		// exception
		NewAbstractService.getIndexKey(parent, null);
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

			Index<Node> index = null;
			try {
				index = networkService.getIndex(parent, type);
			} catch (DatabaseException e1) {
				LOGGER.error("Could not create index.", e1);
				fail();
			}
			try {
				Node node = networkService.createNetworkElement(parent, index,
						type.getId(), type);

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
				Assert.assertEquals(node,
						index.get(NewNetworkService.NAME, type.getId())
								.getSingle());
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
	public void testCreateNEParentNull() {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		try {

			Index<Node> index = networkService.getIndex(parent, type);
			networkService.createNetworkElement(null, index, "name", type);
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNEIndexNull() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		try {
			networkService.createNetworkElement(parent, null, "name", type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testCreateNENameNull() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		try {
			Index<Node> index = networkService.getIndex(parent, type);
			networkService.createNetworkElement(parent, index, null, type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testCreateNENameEmpty() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		try {
			Index<Node> index = networkService.getIndex(parent, type);
			networkService.createNetworkElement(parent, index, "", type);
		} catch (DatabaseException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNETypeNull() {
		// exception
		INodeType type = NetworkElementNodeType.values()[0];
		try {
			Index<Node> index = networkService.getIndex(parent, type);
			networkService.createNetworkElement(parent, index, "name", null);
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNETypeSector() throws AWEException {
		// exception
		INodeType type = NetworkElementNodeType.SECTOR;
		try {
			Index<Node> index = networkService.getIndex(parent, type);
			networkService.createNetworkElement(parent, index, "name", type);
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
			try {
				Index<Node> index = networkService.getIndex(parent, type);
				Node node = networkService.createNetworkElement(parent, index,
						type.getId(), type);
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
			Index<Node> index = null;
			try {
				index = networkService.getIndex(parent, type);
			} catch (DatabaseException e) {
				LOGGER.error("Could not create index.", e);
				fail();
			}
			Node node = networkService.findNetworkElement(index, type.getId());

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
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent,
					NetworkElementNodeType.values()[0]);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create index.", e);
			fail();
		}
		String name = "elementNOTfound";
		// the element does not exist
		Node node = networkService.findNetworkElement(index, name);
		// the node returned is null
		Assert.assertNull(node);
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testFindNEIndexNull() {
		// exception
		networkService.findNetworkElement(null, "name");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindNENameEmpty() {
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent,
					NetworkElementNodeType.values()[0]);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create index.", e);
			fail();
		}
		// exception
		networkService.findNetworkElement(index, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindNENameNull() {
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent,
					NetworkElementNodeType.values()[0]);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create index.", e);
			fail();
		}
		// exception
		networkService.findNetworkElement(index, null);
	}

	// +
	@Test
	public void testGetNetworkElement() {
		parent = getNewNE();
		INodeType type = NetworkElementNodeType.values()[0];
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent, type);
		} catch (DatabaseException e1) {
			LOGGER.error("Could not create index.", e1);
			fail();
		}
		Node node = null;
		try {
			node = networkService.createNetworkElement(parent, index,
					type.getId(), type);
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
		// element exists
		try {
			Node testNode = networkService.getNetworkElement(parent, index,
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
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent, type);
		} catch (DatabaseException e1) {
			LOGGER.error("Could not create index.", e1);
			fail();
		}
		Node node = null;
		try {
			node = networkService.createNetworkElement(parent, index,
					type.getId(), type);
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
		// element exists, parent and type - null
		try {
			Node testNode = networkService.getNetworkElement(null, index,
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
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent, type);
		} catch (DatabaseException e1) {
			LOGGER.error("Could not create index.", e1);
			fail();
		}
		// element does not exist
		try {
			Node node = networkService.getNetworkElement(parent, index,
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
			Assert.assertEquals(node,
					index.get(NewNetworkService.NAME, type.getId()).getSingle());
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
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent, type);
		} catch (DatabaseException e1) {
			LOGGER.error("Could not create index.", e1);
			fail();
		}
		// element does not exist, parent is null
		try {
			networkService.getNetworkElement(null, index, type.getId(), type);
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
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent, type);
		} catch (DatabaseException e1) {
			LOGGER.error("Could not create index.", e1);
			fail();
		}
		// element does not exist, type is null
		try {
			networkService.getNetworkElement(parent, index, type.getId(), null);
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
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent, type);
		} catch (DatabaseException e1) {
			LOGGER.error("Could not create index.", e1);
			fail();
		}
		// name is null
		try {
			networkService.getNetworkElement(parent, index, null, type);
			// exception
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNEIndexNull() {
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
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent, type);
		} catch (DatabaseException e1) {
			LOGGER.error("Could not create index.", e1);
			fail();
		}
		// name is ""
		try {
			networkService.getNetworkElement(parent, index, "", type);
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
			networkService.getNetworkElement(parent, null, type.getId(), type);
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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, index, "name", "ci",
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
		index = graphDatabaseService.index().forNodes(
				NewAbstractService.getIndexKey(parent,
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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, index, "name", null,
					null);

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
		Assert.assertEquals(sector, index.get(NewNetworkService.NAME, "name")
				.getSingle());
	}

	@Test
	public void testCreateSectorCILACSet() {
		// CILAC are set, parent is correct
		Node sector = null;
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, index, null, "ci",
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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, index, null, null, null);

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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, index, null, "ci", null);

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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, index, null, null, "lac");

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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, index, "name", "ci", "lac");

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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(null, index, "name", "ci", "lac");

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
		tx = graphDatabaseService.beginTx();
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

	// +
	@Test
	public void testFindSector() {
		// sector exists, all params set
		Node sector = null;
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, index, "name", "ci",
					"lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		Node testNode = networkService.findSector(index, "name", "ci", "lac");
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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, index, "name", "ci",
					"lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		Node testNode = networkService.findSector(index, "name", null, null);
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
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, index, "name", "ci",
					"lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		Node testNode = networkService.findSector(index, null, "ci", "lac");
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
		Index<Node> index = null;
		try {
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
		} catch (DatabaseException e1) {
			LOGGER.error("Could not create index.", e1);
			fail();
		}

		Node testNode = networkService.findSector(index, "name", "ci", "lac");

		// node returned is null
		Assert.assertNull(testNode);
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorNoneSet() {
		// sector exists, none set
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, index, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		networkService.findSector(index, null, null, null);
		// exception

	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorCISet() {
		// sector exists, CI set
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, index, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		networkService.findSector(index, null, "ci", null);
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorLACSet() {
		// sector exists, LAC set
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, index, "name", "ci", "lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create network element", e);
			fail();
		} finally {
			tx.finish();
		}

		networkService.findSector(index, null, null, "lac");
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindSectorIndexNull() {
		// sector exists, all set, index null
		tx = graphDatabaseService.beginTx();
		Index<Node> index = null;
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			networkService.createSector(parent, index, "name", "ci", "lac");

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

	// +
	@Test
	public void testGetSector() {
		// sector exists
		Node sector = null;
		Index<Node> index = null;
		tx = graphDatabaseService.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, index, "name", "ci",
					"lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create sector", e);
			fail();
		} finally {
			tx.finish();
		}

		try {
			Node testNode = networkService.getSector(parent, index, "name",
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
		Index<Node> index = null;
		tx = graphDatabaseService.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			sector = networkService.createSector(parent, index, "name", "ci",
					"lac");

			tx.success();
		} catch (AWEException e) {
			LOGGER.error("could not create sector", e);
			fail();
		} finally {
			tx.finish();
		}

		try {
			Node testNode = networkService.getSector(null, index, "name", "ci",
					"lac");

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
		Index<Node> index = null;
		tx = graphDatabaseService.beginTx();
		try {
			parent = getNewNE();
			parent.setProperty(NewNetworkService.TYPE,
					NetworkElementNodeType.SITE.getId());
			index = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);

			tx.success();
		} catch (DatabaseException e) {
			LOGGER.error("Could not create index.", e);
		} finally {
			tx.finish();
		}

		try {
			Node testNode = networkService.getSector(parent, index, "name",
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
			Index<Node> index = networkService.getIndex(parent,
					NetworkElementNodeType.NETWORK);
			Index<Node> indexBSC = networkService.getIndex(parent,
					NetworkElementNodeType.BSC);
			Index<Node> indexSITE = networkService.getIndex(parent,
					NetworkElementNodeType.SITE);
			Index<Node> indexSECTOR = networkService.getIndex(parent,
					NetworkElementNodeType.SECTOR);
			for (int i = 0; i < 4; i++) {
				Node network = networkService.createNetworkElement(parent,
						index, "" + i, NetworkElementNodeType.NETWORK);
				nodes.get(NetworkElementNodeType.NETWORK).add(network);
				for (int j = 0; j < 4; j++) {
					Node bsc = networkService.createNetworkElement(network,
							indexBSC, "" + j, NetworkElementNodeType.BSC);
					nodes.get(NetworkElementNodeType.BSC).add(bsc);
					for (int k = 0; k < 4; k++) {
						Node site = networkService.createNetworkElement(bsc,
								indexSITE, "" + k, NetworkElementNodeType.SITE);
						nodes.get(NetworkElementNodeType.SITE).add(site);
						for (int l = 0; l < 4; l++) {
							Node sector = networkService.createSector(site,
									indexSECTOR, "" + l, "" + l, "" + l);
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
		Transaction tx = graphDatabaseService.beginTx();
		Node node = null;
		try {
			node = graphDatabaseService.createNode();
			tx.success();
		} catch (Exception e) {
			LOGGER.error("Could not create node", e);
		} finally {
			tx.finish();
		}
		return node;
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSelectionModelWithoutName()
			throws IllegalArgumentException {
		Node networkNode = graphDatabaseService.createNode();

		try {
			networkService.createSelectionList(networkNode, null);
		} catch (AWEException e) {
			LOGGER.error("Could not create selection list.", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSelectionModelWithEmptyName()
			throws IllegalArgumentException {
		Node networkNode = graphDatabaseService.createNode();

		try {
			networkService.createSelectionList(networkNode, StringUtils.EMPTY);
		} catch (AWEException e) {
			LOGGER.error("Could not create selection list.", e);
			fail();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSelectionModelWithoutNetworkNode()
			throws IllegalArgumentException {
		try {
			networkService.createSelectionList(null,
					DEFAULT_SELECTION_LIST_NAME);
		} catch (AWEException e) {
			LOGGER.error("Could not create selection list.", e);
			fail();
		}
	}

	@Test(expected = DuplicateNodeNameException.class)
	public void createSelectionModelWithAlreadyExistingName()
			throws DuplicateNodeNameException {
		Node networkNode = graphDatabaseService.createNode();

		try {
			networkService.createSelectionList(networkNode,
					DEFAULT_SELECTION_LIST_NAME);
			networkService.createSelectionList(networkNode,
					DEFAULT_SELECTION_LIST_NAME);
		} catch (DatabaseException e) {
			LOGGER.error("Could not create selection list.", e);
			fail();
		}

	}

	@Test
	public void checkCreatedSelectionListNode() {
		Node networkNode = graphDatabaseService.createNode();

		Node selectionNode = null;
		try {
			selectionNode = networkService.createSelectionList(networkNode,
					DEFAULT_SELECTION_LIST_NAME);
		} catch (AWEException e) {
			LOGGER.error("Could not create selection list.", e);
			fail();
		}

		Assert.assertNotNull(selectionNode);
	}

	@Test
	public void checkTypeOfCreatedSelectionListNode() {
		Node networkNode = graphDatabaseService.createNode();

		Node selectionNode = null;
		try {
			selectionNode = networkService.createSelectionList(networkNode,
					DEFAULT_SELECTION_LIST_NAME);
		} catch (AWEException e) {
			LOGGER.error("Could not create selection list.", e);
			fail();
		}

		Assert.assertEquals(NetworkElementNodeType.SELECTION_LIST_ROOT,
				networkService.getNodeType(selectionNode));
	}

	@Test
	public void checkNameOfCreatedSelectionListNode() {
		Node networkNode = graphDatabaseService.createNode();

		Node selectionNode = null;
		try {
			selectionNode = networkService.createSelectionList(networkNode,
					DEFAULT_SELECTION_LIST_NAME);
		} catch (AWEException e) {
			LOGGER.error("Could not create selection list.", e);
			fail();
		}

		Assert.assertEquals(DEFAULT_SELECTION_LIST_NAME,
				selectionNode.getProperty(NewAbstractService.NAME));
	}

}
