package org.amanzi.neo.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NewNetworkService.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService.NodeToNodeRelationshipTypes;
import org.amanzi.testing.AbstractAWETest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class NewNetworkServiceTest extends AbstractAWETest {
    private static Logger LOGGER = Logger.getLogger(NewNetworkServiceTest.class);
    private static NewNetworkService networkService;
    private static NewDatasetService datasetService;
    private static final String databasePath = getDbLocation();
    private static Node parent;
    private static final String NAME_VALUE = "default name";
    private static final String NEW_NAME_VALUE = "new name value";
    private static final String FIRST_PROPERTY = "first property";
    private static final String SECOND_PROPERTY = "second property";
    private final static String DEFAULT_SELECTION_LIST_NAME = "Selection List";
    private int indexCount = 0;

    private final static List<INodeType> DEFAULT_NETWORK_STRUCTURE = new ArrayList<INodeType>();

    private final static INodeType[] NETWORK_STRUCTURE_NODE_TYPES = new INodeType[] {NetworkElementNodeType.NETWORK,
            NetworkElementNodeType.BSC, NetworkElementNodeType.CITY, NetworkElementNodeType.SITE, NetworkElementNodeType.SECTOR};

    static {
        // initialize default network structure
        for (INodeType nodeType : NETWORK_STRUCTURE_NODE_TYPES) {
            DEFAULT_NETWORK_STRUCTURE.add(nodeType);
        }
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();
        LOGGER.info("Database created in folder " + databasePath);
        networkService = NeoServiceFactory.getInstance().getNewNetworkService();
        datasetService = NeoServiceFactory.getInstance().getNewDatasetService();
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
            Assert.assertEquals(String.valueOf(parent.getId()) + "@" + type.getId(), key);
        }
    }

    // -
    @Test(expected = IllegalArgumentException.class)
    public void testGetIndexKeyRootNull() {
        // exception
        NewAbstractService.getIndexKey(null, NetworkElementNodeType.values()[0]);
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
                Node node = networkService.createNetworkElement(parent, index, type.getId(), type);

                // the node returned is not null
                Assert.assertNotNull(node);
                // the relationship from parent exists
                Assert.assertEquals(parent, node.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                        .getOtherNode(node));
                // all properties set
                Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.NAME, null));
                Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.TYPE, null));
                // the element is indexed
                Assert.assertEquals(node, index.get(NewNetworkService.NAME, type.getId()).getSingle());
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
                Node node = networkService.createNetworkElement(parent, index, type.getId(), type);
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
            Assert.assertEquals(type.getId(), node.getProperty(NewAbstractService.NAME, null));
            // the relationship from parent exists
            Assert.assertEquals(parent, node.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
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
            index = networkService.getIndex(parent, NetworkElementNodeType.values()[0]);
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
            index = networkService.getIndex(parent, NetworkElementNodeType.values()[0]);
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
            index = networkService.getIndex(parent, NetworkElementNodeType.values()[0]);
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
            node = networkService.createNetworkElement(parent, index, type.getId(), type);
        } catch (AWEException e) {
            LOGGER.error("could not create network element", e);
            fail();
        }
        // element exists
        try {
            Node testNode = networkService.getNetworkElement(parent, index, type.getId(), type);
            // node returned is not null
            Assert.assertNotNull(testNode);
            // name is right
            Assert.assertEquals(type.getId(), testNode.getProperty(NewNetworkService.NAME, null));
            // the element is not recreated
            Assert.assertEquals(node, testNode);
            // the relationship from parent exists
            Assert.assertEquals(parent, testNode.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
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
            node = networkService.createNetworkElement(parent, index, type.getId(), type);
        } catch (AWEException e) {
            LOGGER.error("could not create network element", e);
            fail();
        }
        // element exists, parent and type - null
        try {
            Node testNode = networkService.getNetworkElement(null, index, type.getId(), null);

            // node returned is not null
            Assert.assertNotNull(testNode);
            // name is right
            Assert.assertEquals(type.getId(), testNode.getProperty(NewNetworkService.NAME, null));
            // the element is not recreated
            Assert.assertEquals(node, testNode);
            // the relationship from parent exists
            Assert.assertEquals(parent, node.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
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
            Node node = networkService.getNetworkElement(parent, index, type.getId(), type);
            // node returned is not null
            Assert.assertNotNull(node);
            // name is right
            Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.NAME, null));
            // type is right
            Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.TYPE, null));
            // element is indexed
            Assert.assertEquals(node, index.get(NewNetworkService.NAME, type.getId()).getSingle());
            // the relationship from parent exists
            Assert.assertEquals(parent, node.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, index, "name", "ci", "lac");

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
        Assert.assertEquals("name", sector.getProperty(NewNetworkService.NAME, null));
        Assert.assertEquals("ci", sector.getProperty(NewNetworkService.CELL_INDEX, null));
        Assert.assertEquals("lac", sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
        Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), sector.getProperty(NewNetworkService.TYPE, null));
        // relation from parent is created
        Assert.assertEquals(parent, sector.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                .getOtherNode(sector));
        // sector is indexed
        index = graphDatabaseService.index().forNodes(NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR));
        Assert.assertEquals(sector, index.get(NewNetworkService.NAME, "name").getSingle());
        Assert.assertEquals(sector, index.get(NewNetworkService.LOCATION_AREA_CODE, "lac").getSingle());
        Assert.assertEquals(sector, index.get(NewNetworkService.CELL_INDEX, "ci").getSingle());
    }

    @Test
    public void testCreateSectorNameSet() {
        // only name is set, parent is correct
        Node sector = null;
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, index, "name", null, null);

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
        Assert.assertEquals("name", sector.getProperty(NewNetworkService.NAME, null));
        Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), sector.getProperty(NewNetworkService.TYPE, null));
        // relation from parent is created
        Assert.assertEquals(parent, sector.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                .getOtherNode(sector));
        // sector is indexed
        Assert.assertEquals(sector, index.get(NewNetworkService.NAME, "name").getSingle());
    }

    @Test
    public void testCreateSectorCILACSet() {
        // CILAC are set, parent is correct
        Node sector = null;
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, index, null, "ci", "lac");

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
        Assert.assertEquals("ci", sector.getProperty(NewNetworkService.CELL_INDEX, null));
        Assert.assertEquals("lac", sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
        Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), sector.getProperty(NewNetworkService.TYPE, null));
        // relation from parent is created
        Assert.assertEquals(parent, sector.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                .getOtherNode(sector));
        // sector is indexed
        Assert.assertEquals(sector, index.get(NewNetworkService.LOCATION_AREA_CODE, "lac").getSingle());
        Assert.assertEquals(sector, index.get(NewNetworkService.CELL_INDEX, "ci").getSingle());
    }

    // -
    @Test(expected = IllegalNodeDataException.class)
    public void testCreateSectorNoneSet() throws IllegalNodeDataException {
        // properties are not set
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, index, "name", "ci", "lac");

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
        Assert.assertEquals("name", sector.getProperty(NewNetworkService.NAME, null));
        Assert.assertEquals("ci", sector.getProperty(NewNetworkService.CELL_INDEX, null));
        Assert.assertEquals("lac", sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
        Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), sector.getProperty(NewNetworkService.TYPE, null));
        // relation from parent is created
        Assert.assertEquals(parent, sector.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                .getOtherNode(sector));
    }

    @Test
    public void testFindSectorNameSet() {
        // sector exists, name set
        Node sector = null;
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, index, "name", "ci", "lac");

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
        Assert.assertEquals("name", sector.getProperty(NewNetworkService.NAME, null));
        Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), sector.getProperty(NewNetworkService.TYPE, null));
        // relation from parent is created
        Assert.assertEquals(parent, sector.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                .getOtherNode(sector));
    }

    @Test
    public void testFindSectorCILACSet() {
        // sector exists, CI+LAC set
        Node sector = null;
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, index, "name", "ci", "lac");

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
        Assert.assertEquals("ci", sector.getProperty(NewNetworkService.CELL_INDEX, null));
        Assert.assertEquals("lac", sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
        Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), sector.getProperty(NewNetworkService.TYPE, null));
        // relation from parent is created
        Assert.assertEquals(parent, sector.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                .getOtherNode(sector));
    }

    @Test
    public void testFindSectorNotFound() {
        // sector is not there, all set
        parent = getNewNE();
        Index<Node> index = null;
        try {
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        Index<Node> index = null;
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
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
        Transaction tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, index, "name", "ci", "lac");

            tx.success();
        } catch (AWEException e) {
            LOGGER.error("could not create sector", e);
            fail();
        } finally {
            tx.finish();
        }

        try {
            Node testNode = networkService.getSector(parent, index, "name", "ci", "lac");

            // sector not recreated
            Assert.assertEquals(sector, testNode);
            // all params correct
            Assert.assertEquals("name", sector.getProperty(NewNetworkService.NAME, null));
            Assert.assertEquals("ci", sector.getProperty(NewNetworkService.CELL_INDEX, null));
            Assert.assertEquals("lac", sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
            Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), sector.getProperty(NewNetworkService.TYPE, null));
            // relation from parent is created
            Assert.assertEquals(parent, sector.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
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
        Transaction tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, index, "name", "ci", "lac");

            tx.success();
        } catch (AWEException e) {
            LOGGER.error("could not create sector", e);
            fail();
        } finally {
            tx.finish();
        }

        try {
            Node testNode = networkService.getSector(null, index, "name", "ci", "lac");

            // sector not recreated
            Assert.assertEquals(sector, testNode);
            // all params correct
            Assert.assertEquals("name", sector.getProperty(NewNetworkService.NAME, null));
            Assert.assertEquals("ci", sector.getProperty(NewNetworkService.CELL_INDEX, null));
            Assert.assertEquals("lac", sector.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
            Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), sector.getProperty(NewNetworkService.TYPE, null));
            // relation from parent is created
            Assert.assertEquals(parent, sector.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
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
        Transaction tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            index = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);

            tx.success();
        } catch (DatabaseException e) {
            LOGGER.error("Could not create index.", e);
        } finally {
            tx.finish();
        }

        try {
            Node testNode = networkService.getSector(parent, index, "name", "ci", "lac");

            // sector created
            Assert.assertNotNull(testNode);
            // all params correct
            Assert.assertEquals("name", testNode.getProperty(NewNetworkService.NAME, null));
            Assert.assertEquals("ci", testNode.getProperty(NewNetworkService.CELL_INDEX, null));
            Assert.assertEquals("lac", testNode.getProperty(NewNetworkService.LOCATION_AREA_CODE, null));
            Assert.assertEquals(NetworkElementNodeType.SECTOR.getId(), testNode.getProperty(NewNetworkService.TYPE, null));
            // relation from parent is created
            Assert.assertEquals(parent, testNode.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                    .getOtherNode(testNode));
            // sector is indexed
            Assert.assertEquals(testNode, index.get(NewNetworkService.NAME, "name").getSingle());
            Assert.assertEquals(testNode, index.get(NewNetworkService.LOCATION_AREA_CODE, "lac").getSingle());
            Assert.assertEquals(testNode, index.get(NewNetworkService.CELL_INDEX, "ci").getSingle());
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
            Index<Node> index = networkService.getIndex(parent, NetworkElementNodeType.NETWORK);
            Index<Node> indexBSC = networkService.getIndex(parent, NetworkElementNodeType.BSC);
            Index<Node> indexSITE = networkService.getIndex(parent, NetworkElementNodeType.SITE);
            Index<Node> indexSECTOR = networkService.getIndex(parent, NetworkElementNodeType.SECTOR);
            for (int i = 0; i < 4; i++) {
                Node network = networkService.createNetworkElement(parent, index, "" + i, NetworkElementNodeType.NETWORK);
                nodes.get(NetworkElementNodeType.NETWORK).add(network);
                for (int j = 0; j < 4; j++) {
                    Node bsc = networkService.createNetworkElement(network, indexBSC, "" + j, NetworkElementNodeType.BSC);
                    nodes.get(NetworkElementNodeType.BSC).add(bsc);
                    for (int k = 0; k < 4; k++) {
                        Node site = networkService.createNetworkElement(bsc, indexSITE, "" + k, NetworkElementNodeType.SITE);
                        nodes.get(NetworkElementNodeType.SITE).add(site);
                        for (int l = 0; l < 4; l++) {
                            Node sector = networkService.createSector(site, indexSECTOR, "" + l, "" + l, "" + l);
                            nodes.get(NetworkElementNodeType.SECTOR).add(sector);
                        }
                    }
                }
            }
        } catch (AWEException e) {
            LOGGER.error("could not create network element", e);
            fail();
        }

        for (INodeType type : NetworkElementNodeType.values()) {
            Iterable<Node> it = networkService.findAllNetworkElements(parent, type);
            for (Node node : it) {
                // type is valid
                Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.TYPE, null));
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
    public void createSelectionModelWithoutName() throws IllegalArgumentException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        try {
            networkService.createSelectionList(networkNode, null);
        } catch (AWEException e) {
            LOGGER.error("Could not create selection list.", e);
            fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSelectionModelWithEmptyName() throws IllegalArgumentException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        try {
            networkService.createSelectionList(networkNode, StringUtils.EMPTY);
        } catch (AWEException e) {
            LOGGER.error("Could not create selection list.", e);
            fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSelectionModelWithoutNetworkNode() throws IllegalArgumentException {
        try {
            networkService.createSelectionList(null, DEFAULT_SELECTION_LIST_NAME);
        } catch (AWEException e) {
            LOGGER.error("Could not create selection list.", e);
            fail();
        }
    }

    @Test(expected = DuplicateNodeNameException.class)
    public void createSelectionModelWithAlreadyExistingName() throws DuplicateNodeNameException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        try {
            networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);
            networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);
        } catch (DatabaseException e) {
            LOGGER.error("Could not create selection list.", e);
            fail();
        }

    }

    @Test
    public void checkCreatedSelectionListNode() {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        Node selectionNode = null;
        try {
            selectionNode = networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);
        } catch (AWEException e) {
            LOGGER.error("Could not create selection list.", e);
            fail();
        }

        Assert.assertNotNull(selectionNode);
    }

    @Test
    public void checkTypeOfCreatedSelectionListNode() {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        Node selectionNode = null;
        try {
            selectionNode = networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);
        } catch (AWEException e) {
            LOGGER.error("Could not create selection list.", e);
            fail();
        }

        Assert.assertEquals(NetworkElementNodeType.SELECTION_LIST_ROOT.getId(), NewNetworkService.getNodeType(selectionNode));
    }

    @Test
    public void checkNameOfCreatedSelectionListNode() {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        Node selectionNode = null;
        try {
            selectionNode = networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);
        } catch (AWEException e) {
            LOGGER.error("Could not create selection list.", e);
            fail();
        }

        Assert.assertEquals(DEFAULT_SELECTION_LIST_NAME, selectionNode.getProperty(NewAbstractService.NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToLinkSelectionNodeWithoutSelectionRootNode() throws Exception {
        Node sector = getSectorForSelection();

        networkService.createSelectionLink(null, sector, getLinkIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToLinkSelectionNodeWithoutNode() throws Exception {
        Node rootSelection = getRootForSelection();

        networkService.createSelectionLink(rootSelection, null, getLinkIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToLinkWihoutIndexes() throws Exception {
        Node rootSelection = getRootForSelection();
        Node sector = getSectorForSelection();

        networkService.createSelectionLink(rootSelection, sector, null);
    }

    @Test(expected = DatabaseException.class)
    public void tryToDoubleLinkNode() throws Exception {
        Node rootSelection = getRootForSelection();
        Node sector = getSectorForSelection();
        Index<Relationship> index = getLinkIndex();

        networkService.createSelectionLink(rootSelection, sector, index);
        networkService.createSelectionLink(rootSelection, sector, index);
    }

    @Test
    public void checkCreatedLink() throws Exception {
        Node rootSelection = getRootForSelection();
        Node sector = getSectorForSelection();

        networkService.createSelectionLink(rootSelection, sector, getLinkIndex());

        Iterable<Relationship> relationships = rootSelection.getRelationships(Direction.OUTGOING);

        assertNotNull("Result should not be null", relationships);

        Iterator<Relationship> relationshipIterator = relationships.iterator();

        assertTrue("It should be at least one Relationship", relationshipIterator.hasNext());

        relationshipIterator.next();

        assertFalse("It should not be more than one Relationship", relationshipIterator.hasNext());
    }

    @Test
    public void checkTypeOfCreatedLink() throws Exception {
        Node rootSelection = getRootForSelection();
        Node sector = getSectorForSelection();

        networkService.createSelectionLink(rootSelection, sector, getLinkIndex());

        Iterator<Relationship> relationshipIterator = rootSelection.getRelationships(Direction.OUTGOING).iterator();

        RelationshipType linkType = relationshipIterator.next().getType();
        assertEquals("Incorrect type of Relationship", NetworkRelationshipTypes.SELECTED, linkType);
    }

    @Test
    public void checkLinkedNode() throws Exception {
        Node rootSelection = getRootForSelection();
        Node sector = getSectorForSelection();

        networkService.createSelectionLink(rootSelection, sector, getLinkIndex());

        Iterator<Relationship> relationshipIterator = rootSelection.getRelationships(Direction.OUTGOING).iterator();

        Node otherNode = relationshipIterator.next().getOtherNode(rootSelection);
        assertEquals("Incorrect Node on another end of Link", sector, otherNode);
    }

    @Test
    public void checkReplaceRelationship() throws Exception {
        Node root = getNewNE();
        Node childNode = getNewNE();
        networkService.createRelationship(root, childNode, org.amanzi.neo.services.enums.NetworkRelationshipTypes.CHILD);
        Node newRootNode = getNewNE();
        networkService.replaceRelationship(newRootNode, childNode, org.amanzi.neo.services.enums.NetworkRelationshipTypes.CHILD,
                Direction.INCOMING);
        assertNull(root + " still has relationships",
                root.getSingleRelationship(org.amanzi.neo.services.enums.NetworkRelationshipTypes.CHILD, Direction.OUTGOING));
        assertNotNull(newRootNode + "still hasn't relationships",
                newRootNode.getSingleRelationship(org.amanzi.neo.services.enums.NetworkRelationshipTypes.CHILD, Direction.OUTGOING));
    }

    @Test
    public void checkCompletePropertiesWithoutReplacement() {
        Node rootNode = getNewNE();
        addedNodeProperties(rootNode, FIRST_PROPERTY, NAME_VALUE);
        Map<String, Object> valuesMap = new HashMap<String, Object>();
        valuesMap.put(SECOND_PROPERTY, NEW_NAME_VALUE);
        try {
            networkService.completeProperties(rootNode, new DataElement(valuesMap), false);
        } catch (DatabaseException e) {
            Assert.fail("End with exception");
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        assertTrue("Missing property: " + SECOND_PROPERTY, rootNode.hasProperty(SECOND_PROPERTY));
        assertTrue(SECOND_PROPERTY + "isn't equals" + NEW_NAME_VALUE, rootNode.getProperty(SECOND_PROPERTY).equals(NEW_NAME_VALUE));
        assertTrue("Missing property: " + FIRST_PROPERTY, rootNode.hasProperty(FIRST_PROPERTY));
        assertTrue(SECOND_PROPERTY + "isn't equals" + NAME_VALUE, rootNode.getProperty(FIRST_PROPERTY).equals(NAME_VALUE));
    }

    @Test
    public void checkCompletePropertiesWithReplacement() {
        Node rootNode = getNewNE();
        addedNodeProperties(rootNode, FIRST_PROPERTY, NAME_VALUE);
        Map<String, Object> valuesMap = new HashMap<String, Object>();
        valuesMap.put(SECOND_PROPERTY, NEW_NAME_VALUE);
        valuesMap.put(FIRST_PROPERTY, NEW_NAME_VALUE);
        try {
            networkService.completeProperties(rootNode, new DataElement(valuesMap), true);
        } catch (DatabaseException e) {
            Assert.fail("End with exception");
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        assertTrue("Missing property: " + SECOND_PROPERTY, rootNode.hasProperty(SECOND_PROPERTY));
        assertTrue(SECOND_PROPERTY + "isn't equals" + NEW_NAME_VALUE, rootNode.getProperty(SECOND_PROPERTY).equals(NEW_NAME_VALUE));
        assertTrue("Missing property: " + FIRST_PROPERTY, rootNode.hasProperty(FIRST_PROPERTY));
        assertTrue(FIRST_PROPERTY + "isn't equals" + NEW_NAME_VALUE, rootNode.getProperty(FIRST_PROPERTY).equals(NEW_NAME_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetNetworkStructureWithoutNode() throws Exception {
        networkService.setNetworkStructure(null, DEFAULT_NETWORK_STRUCTURE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetNetworkStructureWithoutList() throws Exception {
        networkService.setNetworkStructure(getNewNE(), null);
    }

    @Test
    public void checkSavedNetworkStructureInNode() throws Exception {
        Node network = getNewNE();

        networkService.setNetworkStructure(network, DEFAULT_NETWORK_STRUCTURE);

        assertTrue("No network structure in node", network.hasProperty(NewNetworkService.NETWORK_STRUCTURE));
    }

    @Test
    public void checkContentOfNetworkStructureInNode() throws Exception {
        Node network = getNewNE();

        networkService.setNetworkStructure(network, DEFAULT_NETWORK_STRUCTURE);

        String[] elementNames = new String[NETWORK_STRUCTURE_NODE_TYPES.length];
        int i = 0;
        for (INodeType nodeType : NETWORK_STRUCTURE_NODE_TYPES) {
            elementNames[i++] = nodeType.getId();
        }

        String[] structureFromNode = (String[])network.getProperty(NewNetworkService.NETWORK_STRUCTURE);

        assertTrue("Incorrect Network Structure in Node", Arrays.equals(elementNames, structureFromNode));
    }

    private final static String TEST_NAME = "test_name";

    /**
     * createProxy(Node sourceNode, Node rootNode), check N2N_REL relation
     * @throws Exception 
     */
    @Test
    public void checkCreateProxyRelation() throws DatabaseException {
        Node sourceNode = getNewNE();
        Node rootNode = getNewNE();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(NewAbstractService.NAME, TEST_NAME);
        datasetService.setProperties(sourceNode, props);

        Node result = networkService.createProxy(sourceNode, rootNode, N2NRelTypes.NEIGHBOUR);
        
        assertTrue("No N2N relation", result.hasRelationship(N2NRelTypes.NEIGHBOUR, Direction.INCOMING));
    }

    /**
     * createProxy(Node sourceNode, Node rootNode), check N2N_REL relation
     * @throws Exception 
     */
    @Test
    public void checkCreateProxyChildRelation() throws DatabaseException {
        Node sourceNode = getNewNE();
        Node rootNode = getNewNE();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(NewAbstractService.NAME, TEST_NAME);
        datasetService.setProperties(sourceNode, props);

        Node result = networkService.createProxy(sourceNode, rootNode, N2NRelTypes.NEIGHBOUR);

        assertTrue("No CHILD OR NEXT relation", result.hasRelationship(DatasetRelationTypes.CHILD, Direction.INCOMING)
                || sourceNode.hasRelationship(DatasetRelationTypes.NEXT, Direction.INCOMING));
    }

    /**
     * createProxy(Node sourceNode, Node rootNode), check SOURCE_NAME property
     * @throws Exception 
     */
    @Test
    public void checkCreateProxyProperty() throws DatabaseException {
        Node sourceNode = getNewNE();
        Node rootNode = getNewNE();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(NewAbstractService.NAME, TEST_NAME);
        datasetService.setProperties(sourceNode, props);

        Node result = networkService.createProxy(sourceNode, rootNode, NodeToNodeRelationshipTypes.PROXYS);

        assertTrue("Proxy node doesn't have " + NewNetworkService.SOURCE_NAME + " property",
                result.hasProperty(NewNetworkService.SOURCE_NAME));

        assertTrue(NewNetworkService.SOURCE_NAME + " property of proxy node contains incorrect data",
                TEST_NAME.equals(result.getProperty(NewNetworkService.SOURCE_NAME)));

    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCreateProxyNullRoot() throws DatabaseException {
        Node sourceNode = getNewNE();

        networkService.createProxy(sourceNode, null, N2NRelTypes.NEIGHBOUR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCreateProxyNullSource() throws DatabaseException {
        Node rootNode = getNewNE();

        networkService.createProxy(null, rootNode, N2NRelTypes.NEIGHBOUR);
    }

    /**
     * added properties to node
     * 
     * @param node
     * @param key
     * @param value
     */
    private void addedNodeProperties(Node node, String key, Object value) {
        Transaction tx = graphDatabaseService.beginTx();
        node.setProperty(key, value);
        tx.success();
        tx.finish();

    }

    /**
     * Creates Root node for Selection Structure
     * 
     * @return
     */
    private Node getRootForSelection() {
        Transaction tx = graphDatabaseService.beginTx();

        Node result = graphDatabaseService.createNode();

        tx.success();
        tx.finish();

        return result;
    }

    /**
     * Creates Sector node to add in Selection Structure
     * 
     * @return
     */
    private Node getSectorForSelection() {
        Transaction tx = graphDatabaseService.beginTx();

        Node result = graphDatabaseService.createNode();

        tx.success();
        tx.finish();

        return result;
    }

    private Index<Relationship> getLinkIndex() {
        Transaction tx = graphDatabaseService.beginTx();

        Index<Relationship> result = graphDatabaseService.index().forRelationships("" + indexCount++);

        tx.success();
        tx.finish();

        return result;
    }

}
