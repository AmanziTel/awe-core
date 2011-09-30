package org.amanzi.neo.services;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NewNetworkService.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

public class NewNetworkServiceTest extends AbstractNeoServiceTest {
    private static Logger LOGGER = Logger.getLogger(NewNetworkServiceTest.class);
    private static NewNetworkService networkService;
    private static final String databasePath = getDbLocation();
    private static Transaction tx;
    private static Node parent;

    private final static String DEFAULT_SELECTION_LIST_NAME = "Selection List";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();

        new LogStarter().earlyStartup();
        clearServices();

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

            String indexName = NewAbstractService.getIndexKey(parent, type);
            try {
                Node node = networkService.createNetworkElement(parent, indexName, type.getId(), type);

                // the node returned is not null
                Assert.assertNotNull(node);
                // the relationship from parent exists
                Assert.assertEquals(parent, node.getRelationships(DatasetRelationTypes.CHILD, Direction.INCOMING).iterator().next()
                        .getOtherNode(node));
                // all properties set
                Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.NAME, null));
                Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.TYPE, null));
                // the element is indexed
                Assert.assertEquals(node, graphDatabaseService.index().forNodes(indexName)
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
        try {
            networkService.createNetworkElement(parent, indexName, "name", null);
        } catch (AWEException e) {
            LOGGER.error("could not create network element", e);
            fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNETypeSector() throws AWEException {
        // exception
        INodeType type = NetworkElementNodeType.SECTOR;
        String indexName = NewAbstractService.getIndexKey(parent, type);
        try {
            networkService.createNetworkElement(parent, indexName, "name", type);
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
            String indexName = NewAbstractService.getIndexKey(parent, type);
            try {
                Node node = networkService.createNetworkElement(parent, indexName, type.getId(), type);
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
            String indexName = NewAbstractService.getIndexKey(parent, type);
            Node node = networkService.findNetworkElement(indexName, type.getId());

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
        String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.values()[0]);
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
        Node node = null;
        try {
            node = networkService.createNetworkElement(parent, indexName, type.getId(), type);
        } catch (AWEException e) {
            LOGGER.error("could not create network element", e);
            fail();
        }
        // element exists
        try {
            Node testNode = networkService.getNetworkElement(parent, indexName, type.getId(), type);
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
        Node node = null;
        try {
            node = networkService.createNetworkElement(parent, indexName, type.getId(), type);
        } catch (AWEException e) {
            LOGGER.error("could not create network element", e);
            fail();
        }
        // element exists, parent and type - null
        try {
            Node testNode = networkService.getNetworkElement(null, indexName, type.getId(), null);

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
        String indexName = NewAbstractService.getIndexKey(parent, type);
        // element does not exist
        try {
            Node node = networkService.getNetworkElement(parent, indexName, type.getId(), type);
            // node returned is not null
            Assert.assertNotNull(node);
            // name is right
            Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.NAME, null));
            // type is right
            Assert.assertEquals(type.getId(), node.getProperty(NewNetworkService.TYPE, null));
            // element is indexed
            Assert.assertEquals(node, graphDatabaseService.index().forNodes(indexName).get(NewNetworkService.NAME, type.getId())
                    .getSingle());
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
        // element does not exist, parent is null
        try {
            networkService.getNetworkElement(null, indexName, type.getId(), type);
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
        // element does not exist, type is null
        try {
            networkService.getNetworkElement(parent, indexName, type.getId(), null);
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
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
        String indexName = NewAbstractService.getIndexKey(parent, type);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, indexName, "name", "ci", "lac");

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
        Index<Node> index = graphDatabaseService.index().forNodes(
                NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR));
        Assert.assertEquals(sector, index.get(NewNetworkService.NAME, "name").getSingle());
        Assert.assertEquals(sector, index.get(NewNetworkService.LOCATION_AREA_CODE, "lac").getSingle());
        Assert.assertEquals(sector, index.get(NewNetworkService.CELL_INDEX, "ci").getSingle());
    }

    @Test
    public void testCreateSectorNameSet() {
        // only name is set, parent is correct
        Node sector = null;
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, indexName, "name", null, null);

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
        Index<Node> index = graphDatabaseService.index().forNodes(
                NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR));
        Assert.assertEquals(sector, index.get(NewNetworkService.NAME, "name").getSingle());
    }

    @Test
    public void testCreateSectorCILACSet() {
        // CILAC are set, parent is correct
        Node sector = null;
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, indexName, null, "ci", "lac");

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
        Index<Node> index = graphDatabaseService.index().forNodes(
                NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR));
        Assert.assertEquals(sector, index.get(NewNetworkService.LOCATION_AREA_CODE, "lac").getSingle());
        Assert.assertEquals(sector, index.get(NewNetworkService.CELL_INDEX, "ci").getSingle());
    }

    // -
    @Test(expected = IllegalNodeDataException.class)
    public void testCreateSectorNoneSet() throws IllegalNodeDataException {
        // properties are not set
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
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

    @Test(expected = IllegalArgumentException.class)
    public void testCreateSectorIndexEmpty() {
        // indexName ""
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, indexName, "name", "ci", "lac");

            tx.success();
        } catch (AWEException e) {
            LOGGER.error("could not create network element", e);
            fail();
        } finally {
            tx.finish();
        }

        Node testNode = networkService.findSector(indexName, "name", "ci", "lac");
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
        String indexName = null;
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, indexName, "name", "ci", "lac");

            tx.success();
        } catch (AWEException e) {
            LOGGER.error("could not create network element", e);
            fail();
        } finally {
            tx.finish();
        }

        Node testNode = networkService.findSector(indexName, "name", null, null);
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
        String indexName = null;
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, indexName, "name", "ci", "lac");

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
        String indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);

        Node testNode = networkService.findSector(indexName, "name", "ci", "lac");

        // node returned is null
        Assert.assertNull(testNode);
    }

    // -
    @Test(expected = IllegalArgumentException.class)
    public void testFindSectorNoneSet() {
        // sector exists, none set
        String indexName = null;
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
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
    }

    // +
    @Test
    public void testGetSector() {
        // sector exists
        Node sector = null;
        String indexName = null;
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, indexName, "name", "ci", "lac");

            tx.success();
        } catch (AWEException e) {
            LOGGER.error("could not create sector", e);
            fail();
        } finally {
            tx.finish();
        }

        try {
            Node testNode = networkService.getSector(parent, indexName, "name", "ci", "lac");

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
        String indexName = null;
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);
            sector = networkService.createSector(parent, indexName, "name", "ci", "lac");

            tx.success();
        } catch (AWEException e) {
            LOGGER.error("could not create sector", e);
            fail();
        } finally {
            tx.finish();
        }

        try {
            Node testNode = networkService.getSector(null, indexName, "name", "ci", "lac");

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
        String indexName = null;
        tx = graphDatabaseService.beginTx();
        try {
            parent = getNewNE();
            parent.setProperty(NewNetworkService.TYPE, NetworkElementNodeType.SITE.getId());
            indexName = NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR);

            tx.success();
        } finally {
            tx.finish();
        }

        try {
            Node testNode = networkService.getSector(parent, indexName, "name", "ci", "lac");

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
            Index<Node> index = graphDatabaseService.index().forNodes(
                    NewAbstractService.getIndexKey(parent, NetworkElementNodeType.SECTOR));
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
            for (int i = 0; i < 4; i++) {
                Node network = networkService.createNetworkElement(parent,
                        NewAbstractService.getIndexKey(parent, NetworkElementNodeType.NETWORK), "" + i,
                        NetworkElementNodeType.NETWORK);
                nodes.get(NetworkElementNodeType.NETWORK).add(network);
                for (int j = 0; j < 4; j++) {
                    Node bsc = networkService
                            .createNetworkElement(network, NewAbstractService.getIndexKey(network, NetworkElementNodeType.BSC), ""
                                    + j, NetworkElementNodeType.BSC);
                    nodes.get(NetworkElementNodeType.BSC).add(bsc);
                    for (int k = 0; k < 4; k++) {
                        Node site = networkService.createNetworkElement(bsc,
                                NewAbstractService.getIndexKey(bsc, NetworkElementNodeType.SITE), "" + k,
                                NetworkElementNodeType.SITE);
                        nodes.get(NetworkElementNodeType.SITE).add(site);
                        for (int l = 0; l < 4; l++) {
                            Node sector = networkService.createSector(site,
                                    NewAbstractService.getIndexKey(site, NetworkElementNodeType.SECTOR), "" + l, "" + l, "" + l);
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
    public void createSelectionModelWithoutName() throws IllegalArgumentException, AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        networkService.createSelectionList(networkNode, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSelectionModelWithEmptyName() throws IllegalArgumentException, AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        networkService.createSelectionList(networkNode, StringUtils.EMPTY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSelectionModelWithoutNetworkNode() throws IllegalArgumentException, AWEException {
        networkService.createSelectionList(null, DEFAULT_SELECTION_LIST_NAME);
    }

    @Test(expected = DuplicateNodeNameException.class)
    public void createSelectionModelWithAlreadyExistingName() throws AWEException, IllegalArgumentException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);
        networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);
    }

    @Test
    public void checkCreatedSelectionListNode() throws AWEException, IllegalArgumentException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        Node selectionNode = networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);

        Assert.assertNotNull("Created node should not be null", selectionNode);
    }

    @Test
    public void checkTypeOfCreatedSelectionListNode() throws AWEException, IllegalArgumentException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        Node selectionNode = networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);

        Assert.assertEquals("Incorrect Type of created Node", NetworkElementNodeType.SELECTION_LIST_ROOT.getId(),
                networkService.getNodeType(selectionNode));
    }

    @Test
    public void checkCountOfCreatedSelectionListNode() throws AWEException, IllegalArgumentException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        Node selectionNode = networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);

        Assert.assertEquals("Incorrect Count of created Node", 0, selectionNode.getProperty(NewNetworkService.SELECTED_NODES_COUNT));
    }

    @Test
    public void checkNameOfCreatedSelectionListNode() throws AWEException, IllegalArgumentException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        Node selectionNode = networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);

        Assert.assertEquals("Incorrect name of created Node", DEFAULT_SELECTION_LIST_NAME,
                selectionNode.getProperty(NewAbstractService.NAME));
    }

    @Test
    public void checkRelationshipsOfCreatedSelectionListNode() throws AWEException, IllegalArgumentException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        Node selectionNode = networkService.createSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);

        Iterable<Relationship> incomingRelationships = selectionNode.getRelationships(Direction.INCOMING);

        int count = 0;
        for (Relationship relationship : incomingRelationships) {
            count++;

            Node network = relationship.getOtherNode(selectionNode);
            Assert.assertEquals("Unexpected type of Relationship", NewNetworkService.NetworkRelationshipTypes.SELECTION_LIST,
                    relationship.getType());
            Assert.assertEquals("Unexpected start Node of Relationship", networkNode, network);
        }

        Assert.assertEquals("unexpected number of incoming relationships", 1, count);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSelectionModelWithoutNetworkNode() throws AWEException {
        networkService.findSelectionList(null, DEFAULT_SELECTION_LIST_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSelectionModelWithEmptyName() throws AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        networkService.findSelectionList(networkNode, StringUtils.EMPTY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSelectionModelWithoutName() throws AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();

        networkService.findSelectionList(networkNode, null);
    }

    @Test(expected = DuplicateNodeNameException.class)
    public void findDuplicatedSelecitonNodes() throws AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();

        createSelectionNode(networkNode, DEFAULT_SELECTION_LIST_NAME);
        createSelectionNode(networkNode, DEFAULT_SELECTION_LIST_NAME);

        tx.success();
        tx.finish();

        networkService.findSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME);
    }

    @Test
    public void checkFoundedSelectionNodes() throws AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();

        Node[] selectionNodes = new Node[5];
        for (int i = 0; i < 5; i++) {
            selectionNodes[i] = createSelectionNode(networkNode, DEFAULT_SELECTION_LIST_NAME + i);
        }

        tx.success();
        tx.finish();

        for (int i = 0; i < 5; i++) {
            Node foundedNode = networkService.findSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME + i);

            Assert.assertNotNull("Selection List Node <" + DEFAULT_SELECTION_LIST_NAME + i + "> was not found", foundedNode);
            Assert.assertEquals("Was found incorrect selection node", selectionNodes[i], foundedNode);
        }
    }

    @Test
    public void tryToFindNotExistingSelectionNode() throws AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();

        Node[] selectionNodes = new Node[5];
        for (int i = 0; i < 5; i++) {
            selectionNodes[i] = createSelectionNode(networkNode, DEFAULT_SELECTION_LIST_NAME + i);
        }

        tx.success();
        tx.finish();

        Node foundedNode = networkService.findSelectionList(networkNode, DEFAULT_SELECTION_LIST_NAME + 7);

        Assert.assertNull("Node should not be found", foundedNode);
    }

    private Node createSelectionNode(Node network, String name) {
        Node selectionNode = graphDatabaseService.createNode();
        selectionNode.setProperty(NewAbstractService.NAME, name);
        selectionNode.setProperty(NewAbstractService.TYPE, NetworkElementNodeType.SELECTION_LIST_ROOT.getId());

        network.createRelationshipTo(selectionNode, NetworkRelationshipTypes.SELECTION_LIST);

        return selectionNode;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToFindAllSelectionModelsWithoutNetworkNode() throws AWEException {
        networkService.getAllSelectionModelsOfNetwork(null);
    }
    
    @Test
    public void tryToFindAllSelectionModelsWithNetworkWithoutSelecdtionLists() throws AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode = graphDatabaseService.createNode();
        tx.success();
        tx.finish();
        
        Iterable<Node> result = networkService.getAllSelectionModelsOfNetwork(networkNode);
        
        Assert.assertNotNull("Result of search should not be null", result);
        Assert.assertFalse("Result should not contain any value", result.iterator().hasNext());
    }
    
    @Test
    public void checkNumberOfFoundedSelectionNodes() throws AWEException {
        Transaction tx = graphDatabaseService.beginTx();
        Node networkNode1 = graphDatabaseService.createNode();

        Node[] selectionNodesForNetwork1 = new Node[5];
        for (int i = 0; i < 5; i++) {
            selectionNodesForNetwork1[i] = createSelectionNode(networkNode1, DEFAULT_SELECTION_LIST_NAME + i);
        }
        
        Node networkNode2 = graphDatabaseService.createNode();
        
        Node[] selectionNodesForNetwork2 = new Node[10];
        for (int i = 0; i < 10; i++) {
            selectionNodesForNetwork2[i] = createSelectionNode(networkNode2, DEFAULT_SELECTION_LIST_NAME + i);
        }

        tx.success();
        tx.finish();

        Iterable<Node> selection1 = networkService.getAllSelectionModelsOfNetwork(networkNode1);
        ArrayList<Node> selectionList1 = new ArrayList<Node>();
        for (Node selectionNode : selection1) {
            selectionList1.add(selectionNode);
        }
        
        Iterable<Node> selection2 = networkService.getAllSelectionModelsOfNetwork(networkNode2);
        ArrayList<Node> selectionList2 = new ArrayList<Node>();
        for (Node selectionNode : selection2) {
            selectionList2.add(selectionNode);
        }
        
        Assert.assertEquals("Unexpected number of founded selection lists", 5, selectionList1.size());
        Assert.assertEquals("Unexpected number of founded selection lists", 10, selectionList2.size());
        
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue("Selection list <" + DEFAULT_SELECTION_LIST_NAME + i + "> not found", selectionList1.contains(selectionNodesForNetwork1[i]));
        }
        
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue("Selection list <" + DEFAULT_SELECTION_LIST_NAME + i + "> not found", selectionList2.contains(selectionNodesForNetwork2[i]));
        }
    }
}
