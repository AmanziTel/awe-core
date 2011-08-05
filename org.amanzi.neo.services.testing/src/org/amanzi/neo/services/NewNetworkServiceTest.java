package org.amanzi.neo.services;

import static org.junit.Assert.fail;
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

	// // +
	// @Test
	// public void testSetNameProperty() {
	// Node node = getNewNE();
	// String name = "test";
	// try {
	// networkService.setNameProperty(node, name);
	// } catch (DatabaseException e) {
	// LOGGER.error("Could not set name property", e);
	// fail();
	// } catch (IllegalNodeDataException e) {
	// LOGGER.error("Could not set name property", e);
	// fail();
	// }
	// // name property is set correctly
	// Assert.assertEquals(name,
	// node.getProperty(NewNetworkService.NAME, null));
	// }
	//
	// @Test(expected = IllegalArgumentException.class)
	// public void testsetNamePropertyNodeNull() throws AWEException {
	// // exception
	// networkService.setNameProperty(null, "name");
	//
	// }
	//
	// @Test(expected = IllegalNodeDataException.class)
	// public void testsetNamePropertyNameNull() throws AWEException {
	// // exception
	// networkService.setNameProperty(getNewNE(), null);
	// }
	//
	// @Test(expected = IllegalNodeDataException.class)
	// public void testsetNamePropertyNameEmpty() throws AWEException {
	// // exception
	// networkService.setNameProperty(getNewNE(), "");
	// }
	//
	// // +
	// @Test
	// public void testAddNodeToIndex() {
	// Node node = getNewNE();
	// String indexName = networkService.getIndexKey(parent,
	// NetworkElementNodeType.NETWORK);
	// String name = "test";
	// try {
	// networkService.addNodeToIndex(node, indexName,
	// NewNetworkService.NAME, name);
	// } catch (DatabaseException e) {
	// LOGGER.error("coud not index node", e);
	// fail();
	// }
	// // node is indexed
	// Assert.assertEquals(
	// node,
	// graphDb.index().forNodes(indexName)
	// .get(NewNetworkService.NAME, name).getSingle());
	// }
	//
	// // tests for methods of NewNetworkService class
	// +
	@Test
	public void testCreateNetworkElement() {
		parent = getNewNE();
		for (INodeType type : NetworkElementNodeType.values()) {
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
				LOGGER.error("coud not create network element", e);
				fail();
			} catch (IllegalNodeDataException e) {
				LOGGER.error("coud not create network element", e);
				fail();
			}
		}
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testCreateNEParentNull() {
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNEIndexNameNull() {
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNEIndexNameEmpty() {
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNENameNull() {
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNENameEmpty() {
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNETypeNull() {
		// exception
	}

	// +
	@Test
	public void testFindNetworkElement() {
		// the element exists
		// the node returned is not null
		// the element name property is right
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testFindNetworkElementNotFound() {
		// the element does not exist
		// the node returned is null
		fail("Not yet implemented"); // TODO
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testFindNEIndexNameNull() {
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindNEIndexNameEmpty() {
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindNENameEmpty() {
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindNENameNull() {
		// exception
	}

	// +
	@Test
	public void testGetNetworkElement() {
		// element exists
		// node returned is not null
		// name is right
		// the element is not recreated
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetNetworkElementParamsNull() {
		// element exists, parent and type - null
		// node returned is not null
		// name is right
		// the element is not recreated
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetNetworkElementNoElement() {
		// element does not exist
		// node returned is not null
		// name is right
		// type is right
		// element is indexed
		fail("Not yet implemented"); // TODO
	}

	// -
	@Test(expected = IllegalArgumentException.class)
	public void testGetNEParentNull() {
		// element does not exist, parent is null
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNETypeNull() {
		// element does not exist, type is null
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNENameNull() {
		// name is null
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNEIndexNameNull() {
		// indexName is null
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNENameEmpty() {
		// name is ""
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNEIndexNameNEmpty() {
		// indexName is ""
		// exception
	}

	@Test
	public void testCreateSector() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testFindSector() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetSector() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testFindAllNetworkElements() {
		// type is valid
		// all elements are found
		fail("Not yet implemented"); // TODO
	}

	private Node getNewNE() {
		tx = graphDb.beginTx();
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
