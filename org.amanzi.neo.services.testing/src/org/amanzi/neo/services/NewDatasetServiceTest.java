package org.amanzi.neo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
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

/**
 * Test for org.amanzi.neo.services.DataService
 * 
 * @author kruglik_a
 * 
 */
public class NewDatasetServiceTest extends AbstractNeoServiceTest {

	private static Logger LOGGER = Logger
			.getLogger(NewDatasetServiceTest.class);

    private static Node parent;

    @BeforeClass
    public static final void beforeClass() {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
        clearServices();
    }

    @AfterClass
    public static final void afterClass() {
        stopDb();
        clearDb();
    }

    private Transaction tx;
    private NewDatasetService service;

    @Before
    public void before() {
        service = new NewDatasetService(graphDatabaseService);
        initProjectNode();
    }

    @After
    public final void after() {
        cleanReferenceNode();

    }

    /**
     * constants for test dataset names
     */
    private final String NAME_1 = "dataset_1";
    private final String NAME_2 = "dataset_2";

    /**
     * this methot inits properties for dataset node
     * 
     * @param datasetNode
     * @param name
     * @param type
     * @param driveType
     */
    private void setPropertyToDatasetNode(Node datasetNode, String name,
            DatasetTypes type, DriveTypes driveType) {
        datasetNode.setProperty(NewDatasetService.NAME, name);
        datasetNode.setProperty(NewDatasetService.TYPE, type.getId());
        if (driveType != null)
            datasetNode.setProperty(NewDatasetService.DRIVE_TYPE,
                    driveType.name());
    }

    /**
     * Project node
     */
    private Node projectNode;

    /**
     * create Project node in database
     */
    private void initProjectNode() {
        tx = graphDatabaseService.beginTx();
        try {
            projectNode = graphDatabaseService.createNode();
            graphDatabaseService.getReferenceNode().createRelationshipTo(
                    projectNode, DatasetRelationTypes.PROJECT);
            projectNode.setProperty(NewDatasetService.NAME, "project");
            tx.success();
        } finally {
            tx.finish();
            tx = null;
        }
    }

    /**
     * create Dataset node in database
     * 
     * @param name
     * @param type
     * @param driveType
     * @return datasetNode
     */
    private Node initDatasetNode(String name, DatasetTypes type,
            DriveTypes driveType) {
        Node datasetNode = null;
        tx = graphDatabaseService.beginTx();
        try {
            datasetNode = graphDatabaseService.createNode();
            projectNode.createRelationshipTo(datasetNode,
                    DatasetRelationTypes.DATASET);
            setPropertyToDatasetNode(datasetNode, name, type, driveType);

            tx.success();

        } finally {
            tx.finish();
            tx = null;
        }
        return datasetNode;
    }

    /**
     * removes reference node relationships
     */
    private void cleanReferenceNode() {
        tx = graphDatabaseService.beginTx();
        try {

            Iterator<Relationship> iter = graphDatabaseService
                    .getReferenceNode().getRelationships().iterator();
            while (iter.hasNext()) {
                iter.next().delete();
            }
            tx.success();

        } finally {
            tx.finish();
            tx = null;
        }
    }

    /**
     * testing method findDataset(String name, DatasetTypes type, Node
     * projectNode) when this dataset exists
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test
    public void findDatasetTest() throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {

        Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);

        Node checkNode = service.findDataset(projectNode, NAME_1,
                DatasetTypes.NETWORK);

        Assert.assertTrue("method findDataset() return wrong node",
                datasetNode.equals(checkNode));

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, Node
     * projectNode) when this dataset doesn't exist
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test
    public void findDatasetNullTest() throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {

        initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);

        Node checkNode = service.findDataset(projectNode, NAME_2,
                DatasetTypes.NETWORK);

        Assert.assertNull("method findDataset() return wrong node", checkNode);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter name = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetExeptionNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, null, DatasetTypes.NETWORK);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter type = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetExeptionTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, NAME_1, null);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter projectNode = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetExeptionProjectNodeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(null, NAME_1, DatasetTypes.NETWORK);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter name = ""
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetExeptionEmptyNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, "", DatasetTypes.NETWORK);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter type != NETWORK
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = DatasetTypeParameterException.class)
    public void findDatasetNetworkTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, NAME_1, DatasetTypes.DRIVE);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when this dataset exists
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test
    public void findDatasetWithDriveTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {

        Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

        Node checkNode = service.findDataset(projectNode, NAME_1,
                DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

        Assert.assertTrue("method findDataset() return wrong node",
                datasetNode.equals(checkNode));

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when this dataset doesn't exist
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test
    public void findDatasetWithDriveTypeNullTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {

        Node checkNode = service.findDataset(projectNode, NAME_2,
                DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

        Assert.assertNull("method findDataset() return wrong node", checkNode);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter type = NETWORK
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = DatasetTypeParameterException.class)
    public void findDatasetWithDriveTypeNetworkTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, NAME_1, DatasetTypes.NETWORK,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter name = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetWithDriveTypeExeptionNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, null, DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter type = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetWithDriveTypeExeptionTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, NAME_1, null, DriveTypes.NEMO_V1);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter driveType = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetWithDriveTypeExeptionDriveTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, NAME_1, DatasetTypes.DRIVE, null);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter projectNode = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetWithDriveTypeExeptionProjectNodeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(null, NAME_1, DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method findDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter name = ""
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findDatasetWithDriveTypeExeptionEmptyNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.findDataset(projectNode, "", DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, DriveType
     * driveType, Node projectNode) when all parameter != null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test
    public void createDatasetWithDriveTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException, DatabaseException {

        Node actualDataset = service.createDataset(projectNode, NAME_1,
                DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

        boolean hasRelation = actualDataset.hasRelationship(
                DatasetRelationTypes.DATASET, Direction.INCOMING);
        Assert.assertTrue("not create DATASET relation", hasRelation);

        String actualName = (String) actualDataset
                .getProperty(NewDatasetService.NAME);
        Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

        String actualType = (String) actualDataset
                .getProperty(NewDatasetService.TYPE);
        Assert.assertEquals("dataset has wrong type",
                DatasetTypes.DRIVE.getId(), actualType);

        String actualDriveType = (String) actualDataset
                .getProperty(NewDatasetService.DRIVE_TYPE);
        Assert.assertEquals("dataset has wrong driveType",
                DriveTypes.NEMO_V1.name(), actualDriveType);
    }

    /**
     * testing method createDataset(String name, DatasetTypes type, DriveType
     * driveType, Node projectNode) when parameter name = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void createDatasetWithDriveTypeExeptionNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException, DatabaseException {
        service.createDataset(projectNode, null, DatasetTypes.NETWORK,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, DriveType
     * driveType, Node projectNode) when parameter type == NETWORK
     * 
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = DatasetTypeParameterException.class)
    public void createDatasetWithDriveTypeExeptionDatasetTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException, DatabaseException {
        service.createDataset(projectNode, NAME_1, DatasetTypes.NETWORK,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, DriveType
     * driveType, Node projectNode) when parameter type = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void createDatasetWithDriveTypeExeptionTypeTest()
            throws InvalidDatasetParameterException, DatabaseException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.createDataset(projectNode, NAME_1, null, DriveTypes.NEMO_V1);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter projectNode = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void createDatasetWithDriveTypeExeptionProjectNodeTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.createDataset(null, NAME_1, DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter name = ""
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void createDatasetWithDriveTypeExeptionEmptyNameTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.createDataset(projectNode, "", DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter name duplicate name existing
     * dataset node
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = DuplicateNodeNameException.class)
    public void createDatasetWithDriveTypeExeptionDublicateNameTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {

        initDatasetNode(NAME_1, DatasetTypes.DRIVE, DriveTypes.ROMES);
        service.createDataset(projectNode, NAME_1, DatasetTypes.DRIVE,
                DriveTypes.ROMES);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, Node
     * projectNode) when all parameter != null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test
    public void createDatasetTest() throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {

        Node actualDataset = service.createDataset(projectNode, NAME_1,
                DatasetTypes.NETWORK);

        boolean hasRelation = actualDataset.hasRelationship(
                DatasetRelationTypes.DATASET, Direction.INCOMING);
        Assert.assertTrue("not create DATASET relation", hasRelation);

        String actualName = (String) actualDataset
                .getProperty(NewDatasetService.NAME);
        Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

        String actualType = (String) actualDataset
                .getProperty(NewDatasetService.TYPE);
        Assert.assertEquals("dataset has wrong type",
                DatasetTypes.NETWORK.getId(), actualType);
    }

    /**
     * testing method createDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter name = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void createDatasetExeptionNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.createDataset(projectNode, null, DatasetTypes.NETWORK);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter type != NETWORK
     * 
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = DatasetTypeParameterException.class)
    public void createDatasetExeptionDatasetTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.createDataset(projectNode, NAME_1, DatasetTypes.DRIVE);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter type = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void createDatasetExeptionTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.createDataset(projectNode, NAME_1, null);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter projectNode = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void createDatasetExeptionProjectNodeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.createDataset(null, NAME_1, DatasetTypes.NETWORK);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter name = ""
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void createDatasetExeptionEmptyNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.createDataset(projectNode, "", DatasetTypes.NETWORK);

    }

    /**
     * testing method createDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter name dublicate name existing dataset node
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = DuplicateNodeNameException.class)
    public void createDatasetExeptionDublicateNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {

        initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);
        service.createDataset(projectNode, NAME_1, DatasetTypes.NETWORK);

    }

    /**
     * testing method getDataset(String name, DatasetTypes, Node projectNode)
     * when this dataset doesn't exist
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test
    public void getDatasetCreateTest() throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        Node actualDataset = service.getDataset(projectNode, NAME_1,
                DatasetTypes.NETWORK);

        boolean hasRelation = actualDataset.hasRelationship(
                DatasetRelationTypes.DATASET, Direction.INCOMING);
        Assert.assertTrue("not create DATASET relation", hasRelation);

        String actualName = (String) actualDataset
                .getProperty(NewDatasetService.NAME);
        Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

        String actualType = (String) actualDataset
                .getProperty(NewDatasetService.TYPE);
        Assert.assertEquals("dataset has wrong type",
                DatasetTypes.NETWORK.getId(), actualType);
    }

    /**
     * testing method getDataset(String name, DatasetTypes, Node projectNode)
     * when this dataset exists
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test
    public void getDatasetFindTest() throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {

        Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);

        Node actualDataset = service.getDataset(projectNode, NAME_1,
                DatasetTypes.NETWORK);

        Assert.assertEquals("method getDataset return wrong node", datasetNode,
                actualDataset);
    }

    /**
     * testing method getDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter name = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetExeptionNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.getDataset(projectNode, null, DatasetTypes.NETWORK);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter type != NETWORK
     * 
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = DatasetTypeParameterException.class)
    public void getDatasetExeptionDatasetTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.getDataset(projectNode, NAME_1, DatasetTypes.DRIVE);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter type = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetExeptionTypeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.getDataset(projectNode, NAME_1, null);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter projectNode = null
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetExeptionProjectNodeTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.getDataset(null, NAME_1, DatasetTypes.NETWORK);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, Node
     * projectNode) when parameter name = ""
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetExeptionEmptyNameTest()
            throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException,
            DatabaseException {
        service.getDataset(projectNode, "", DatasetTypes.NETWORK);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when this dataset exists
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test
    public void getDatasetWithDriveTypeFindTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {

        Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

        Node checkNode = service.getDataset(projectNode, NAME_1,
                DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

        Assert.assertTrue("method findDataset() return wrong node",
                datasetNode.equals(checkNode));

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, DriveType
     * driveType, Node projectNode) when dataset node doesn't exist
     * 
     * @throws InvalidDatasetParameterException
     * @throws DatasetTypeParameterException
     * @throws DuplicateNodeNameException
     */
    @Test
    public void getDatasetWithDriveTypeCreateTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {

        Node actualDataset = service.getDataset(projectNode, NAME_1,
                DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

        boolean hasRelation = actualDataset.hasRelationship(
                DatasetRelationTypes.DATASET, Direction.INCOMING);
        Assert.assertTrue("not create DATASET relation", hasRelation);

        String actualName = (String) actualDataset
                .getProperty(NewDatasetService.NAME);
        Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

        String actualType = (String) actualDataset
                .getProperty(NewDatasetService.TYPE);
        Assert.assertEquals("dataset has wrong type",
                DatasetTypes.DRIVE.getId(), actualType);

        String actualDriveType = (String) actualDataset
                .getProperty(NewDatasetService.DRIVE_TYPE);
        Assert.assertEquals("dataset has wrong driveType",
                DriveTypes.NEMO_V1.name(), actualDriveType);
    }

    /**
     * testing method getDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter type = NETWORK
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = DatasetTypeParameterException.class)
    public void getDatasetWithDriveTypeNetworkTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.getDataset(projectNode, NAME_1, DatasetTypes.NETWORK,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter name = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetWithDriveTypeExeptionNameTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.getDataset(projectNode, null, DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter type = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetWithDriveTypeExeptionTypeTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.getDataset(projectNode, NAME_1, null, DriveTypes.NEMO_V1);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter driveType = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetWithDriveTypeExeptionDriveTypeTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.getDataset(projectNode, NAME_1, DatasetTypes.DRIVE, null);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter projectNode = null
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetWithDriveTypeExeptionProjectNodeTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.getDataset(null, NAME_1, DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

    }

    /**
     * testing method getDataset(String name, DatasetTypes type, DriveTypes
     * driveType, Node projectNode) when parameter name = ""
     * 
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     * @throws DuplicateNodeNameException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void getDatasetWithDriveTypeExeptionEmptyNameTest()
            throws InvalidDatasetParameterException, DatabaseException, 
            DatasetTypeParameterException, DuplicateNodeNameException {
        service.getDataset(projectNode, "", DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);

    }

    /**
     * testing method getAllDatasets(Node projectNode) when nobody datasets
     * exist
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test
    public void findAllDatasetsEmptyTest()
            throws InvalidDatasetParameterException {
        List<Node> checkList = service.findAllDatasets(projectNode);
        Assert.assertTrue("method findAllDatasets return wrong nodes",
                checkList.isEmpty());
    }

    /**
     * testing method getAllDatasets(Node projectNode) when some datasets exist
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test
    public void findAllDatasetsTest() throws InvalidDatasetParameterException {
        Node dataset_1 = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);
        Node dataset_2 = initDatasetNode(NAME_2, DatasetTypes.DRIVE,
                DriveTypes.NEMO_V1);
        List<Node> checkList = service.findAllDatasets(projectNode);

        Assert.assertTrue("method findAllDatasets didn't return dataset_1",
                checkList.contains(dataset_1));
        Assert.assertTrue("method findAllDatasets didn't return dataset_2",
                checkList.contains(dataset_2));

        checkList.remove(dataset_1);
        checkList.remove(dataset_2);
        Assert.assertTrue("method findAllDatasets return superfluouts nodes",
                checkList.isEmpty());
    }

    /**
     * testing method getAllDatasets(Node projectNode) when projectNode == null
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findAllDatasetsExceptionTest()
            throws InvalidDatasetParameterException {
        service.findAllDatasets(null);
    }

    /**
     * testing method getAllDatasetsByType(DatasetTypes type,Node projectNode)
     * when nobody datasets by given type exist
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test
    public void findAllDatasetByTypesEmptyTest()
            throws InvalidDatasetParameterException {
        initDatasetNode(NAME_1, DatasetTypes.DRIVE, DriveTypes.NEMO_V1);
        List<Node> checkList = service.findAllDatasetsByType(projectNode,
                DatasetTypes.NETWORK);
        Assert.assertTrue("method findAllDatasetsByType return wrong nodes",
                checkList.isEmpty());
    }

    /**
     * testing method getAllDatasetsByType(DatasetTypes type, Node projectNode)
     * when some datasets exist
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test
    public void findAllDatasetsByTypeTest()
            throws InvalidDatasetParameterException {
        Node dataset_1 = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);
        initDatasetNode(NAME_2, DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

        List<Node> checkList = service.findAllDatasetsByType(projectNode,
                DatasetTypes.NETWORK);

        Assert.assertTrue("method findAllDatasets didn't return dataset_1",
                checkList.contains(dataset_1));

        checkList.remove(dataset_1);
        Assert.assertTrue("method findAllDatasets return superfluouts nodes",
                checkList.isEmpty());
    }

    /**
     * testing method getAllDatasetsByType(DatasetTypes type,Node projectNode)
     * when projectNode == null
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findAllDatasetsByTypeExceptionProjectNodeTest()
            throws InvalidDatasetParameterException {
        service.findAllDatasetsByType(null, DatasetTypes.NETWORK);
    }

    /**
     * testing method getAllDatasetsByType(DatasetTypes type,Node projectNode)
     * when type == null
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findAllDatasetsByTypeExceptionTypeTest()
            throws InvalidDatasetParameterException {
        service.findAllDatasetsByType(projectNode, null);
    }

    /**
     * testing method findAllDatasets() when some datasets exist
     */
    @Test
    public void findAllDatasetsInAllProjectsTest() {

        Node dataset_1 = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);
        initProjectNode();
        Node dataset_2 = initDatasetNode(NAME_2, DatasetTypes.DRIVE,
                DriveTypes.TEMS);
        List<Node> checkList = service.findAllDatasets();
        Assert.assertTrue("method findAllDatasets didn't return dataset_1",
                checkList.contains(dataset_1));
        Assert.assertTrue("method findAllDatasets didn't return dataset_2",
                checkList.contains(dataset_2));
        checkList.remove(dataset_1);
        checkList.remove(dataset_2);
        Assert.assertTrue("method findAllDatasets return superfluouts nodes",
                checkList.isEmpty());
    }

    /**
     * testing method findAllDatasets() when nobody datasets exist
     */
    @Test
    public void findAllDatasetsInAllProjectsEmptyTest() {

        List<Node> checkList = service.findAllDatasets();

        Assert.assertTrue("method findAllDatasets return superfluouts nodes",
                checkList.isEmpty());
    }

    /**
     * testing method findAllDatasetsByType(DatasetTypes type) when some
     * datasets exist
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test
    public void findAllDatasetsByTypeInAllProjectsTest()
            throws InvalidDatasetParameterException {

        Node dataset_1 = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);
        initProjectNode();
        Node dataset_2 = initDatasetNode(NAME_2, DatasetTypes.NETWORK, null);
        initDatasetNode(NAME_1, DatasetTypes.COUNTERS, DriveTypes.NEMO_V1);
        List<Node> checkList = service
                .findAllDatasetsByType(DatasetTypes.NETWORK);
        Assert.assertTrue(
                "method findAllDatasetsByType didn't return dataset_1",
                checkList.contains(dataset_1));
        Assert.assertTrue(
                "method findAllDatasetsByType didn't return dataset_2",
                checkList.contains(dataset_2));
        checkList.remove(dataset_1);
        checkList.remove(dataset_2);
        Assert.assertTrue(
                "method findAllDatasetsByType return superfluouts nodes",
                checkList.isEmpty());
    }

    /**
     * testing method findAllDatasetsByType(DatasetTypes type) when nobody
     * datasets exist
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test
    public void findAllDatasetsByTypeInAllProjectsEmptyTest()
            throws InvalidDatasetParameterException {

        initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);

        List<Node> checkList = service
                .findAllDatasetsByType(DatasetTypes.COUNTERS);

        Assert.assertTrue(
                "method findAllDatasetsByType return superfluouts nodes",
                checkList.isEmpty());
    }

    /**
     * testing method findAllDatasetsByType(DatasetTypes type) when type = null
     * 
     * @throws InvalidDatasetParameterException
     */
    @Test(expected = InvalidDatasetParameterException.class)
    public void findAllDatasetsByTypeInAllProjectsExceptionTest()
            throws InvalidDatasetParameterException {
        service.findAllDatasetsByType(null);
    }

    // +
    @Test
    public void testAddChildNoChildren() {

        parent = getNewChild();
        Node child = getNewChild();

        // parent has no nodes, no last_child_id, lastChild = null:
        try {
            service.addChild(parent, child, null);
        } catch (Exception e) {
            LOGGER.error("Could not add child", e);
            Assert.fail();
        }

        // last_child_id is updated,
        Assert.assertEquals(child.getId(),
                parent.getProperty(NewDatasetService.LAST_CHILD_ID, 0L));
        // parent_id set
        Assert.assertEquals(parent.getId(),
                child.getProperty(NewDatasetService.PARENT_ID, 0L));
        // valid chain exists
        Assert.assertTrue(chainExists(parent, child));

    }

    @Test
    public void testAddChildLastChildId() {

        Node child = getNewChild();

        // parent has one node, last child id, lastChild = null:
        try {
            service.addChild(parent, child, null);
        } catch (Exception e) {
            LOGGER.error("Could not add child", e);
            Assert.fail();
        }

        // last_child_id updated,
        Assert.assertEquals(child.getId(),
                parent.getProperty(NewDatasetService.LAST_CHILD_ID, 0L));
        // parent_id updated,
        Assert.assertEquals(parent.getId(),
                child.getProperty(NewDatasetService.PARENT_ID, 0L));
        // there is a chain from parent to the child
        Assert.assertTrue(chainExists(parent, child));
    }

    @Test
    public void testAddChildLastChild() {
        Node lastChild = getNewChild();
        try {
            service.addChild(parent, lastChild, null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
        // remove last_child_id from parent
        tx = graphDatabaseService.beginTx();
        try {
            parent.removeProperty(NewDatasetService.LAST_CHILD_ID);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not remove property", e);
        } finally {
            tx.finish();
        }

        Node child = getNewChild();

        // parent has nodes, last_child_id not set, lastChild set:
        try {
            service.addChild(parent, child, lastChild);
        } catch (Exception e) {
            LOGGER.error("Could not add child", e);
            Assert.fail();
        }

        // last_child_id updated,
        Assert.assertEquals(child.getId(),
                parent.getProperty(NewDatasetService.LAST_CHILD_ID, 0L));
        // parent_id updated,
        Assert.assertEquals(parent.getId(),
                child.getProperty(NewDatasetService.PARENT_ID, 0L));
        // chain exists
        Assert.assertTrue(chainExists(parent, child));
    }

    @Test
    public void testAddChildNoPreset() {
        // remove last_child_id from parent
        tx = graphDatabaseService.beginTx();
        try {
            parent.removeProperty(NewDatasetService.LAST_CHILD_ID);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not remove property", e);
            Assert.fail();
        } finally {
            tx.finish();
        }

        Node child = getNewChild();

        // parent has nodes, last_child_id not set, lastChild not set:
        try {
            service.addChild(parent, child, null);
        } catch (Exception e) {
            LOGGER.error("Could not add child", e);
            Assert.fail();
        }
        // last_child_id updated,
        Assert.assertEquals(child.getId(),
                parent.getProperty(NewDatasetService.LAST_CHILD_ID, 0L));
        // parent_id updated,
        Assert.assertEquals(parent.getId(),
                child.getProperty(NewDatasetService.PARENT_ID, 0L));
        // chain exists
        Assert.assertTrue(chainExists(parent, child));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddChildLCIdWrong() {
        Node lastChild = getNewChild();
        try {
            service.addChild(parent, lastChild, null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
        // reset last_child_id
        tx = graphDatabaseService.beginTx();
        try {
            parent.setProperty(NewDatasetService.LAST_CHILD_ID, parent.getId());
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set property", e);
            Assert.fail();
        } finally {
            tx.finish();
        }

        Node child = getNewChild();

        // parent has nodes, last_child_id is wrong, last child set
        try {
            service.addChild(parent, child, lastChild);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
    }

    // -
    @Test(expected = IllegalArgumentException.class)
    public void testAddChildParentNull() {
        // parent is null: exception
        try {
            service.addChild(null, getNewChild(), null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddChildChildNull() {
        // child is null: exception
        try {
            service.addChild(parent, null, null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
    }

    // +
    @Test
    public void testGetParentWithIdOneChild() {
        parent = getNewChild();
        Node child = null;
        try {
            child = service.addChild(parent, getNewChild(), null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
        // parent_id set:
        Node node = null;
        try {
            node = service.getParent(child, true);
        } catch (DatabaseException e) {
            LOGGER.error("could not get parent", e);
            Assert.fail();
        }
        // parent returned,
        Assert.assertEquals(parent, node);
        // chain exists
        Assert.assertTrue(chainExists(node, child));
    }

    @Test
    public void testGetParentWithIdMoreChildren() {
        Node child = null;
        try {
            child = service.addChild(parent, getNewChild(), null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
        // parent_id set:
        Node node = null;
        try {
            node = service.getParent(child, true);
        } catch (DatabaseException e) {
            LOGGER.error("could not get parent", e);
            Assert.fail();
        }
        // parent returned,
        Assert.assertEquals(parent, node);
        // chain exists
        Assert.assertTrue(chainExists(node, child));
    }

    @Test
    public void testGetParentNoIdOneChild() {
        parent = getNewChild();
        Node child = null;
        try {
            child = service.addChild(parent, getNewChild(), null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
        // remove parent_id
        tx = graphDatabaseService.beginTx();
        try {
            child.removeProperty(NewDatasetService.PARENT_ID);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set property", e);
            Assert.fail();
        } finally {
            tx.finish();
        }
        // parent_id not set:
        Node node = null;
        try {
            node = service.getParent(child, true);
        } catch (DatabaseException e) {
            LOGGER.error("could not get parent", e);
            Assert.fail();
        }
        // parent returned,
        Assert.assertEquals(parent, node);
        // chain exists
        Assert.assertTrue(chainExists(node, child));
        // parent_id updated
        Assert.assertEquals(parent.getId(),
                child.getProperty(NewDatasetService.PARENT_ID, 0L));
    }

    @Test
    public void testGetParentNoIdMoreChildren() {
        Node child = null;
        try {
            child = service.addChild(parent, getNewChild(), null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
        // remove parent_id
        tx = graphDatabaseService.beginTx();
        try {
            child.removeProperty(NewDatasetService.PARENT_ID);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set property", e);
            Assert.fail();
        } finally {
            tx.finish();
        }
        // parent_id not set:
        Node node = null;
        try {
            node = service.getParent(child, true);
        } catch (DatabaseException e) {
            LOGGER.error("could not get parent", e);
            Assert.fail();
        }
        // parent returned,
        Assert.assertEquals(parent, node);
        // chain exists
        Assert.assertTrue(chainExists(node, child));
        // parent_id updated
        Assert.assertEquals(parent.getId(),
                child.getProperty(NewDatasetService.PARENT_ID, 0L));
    }

    @Test
    public void testGetParentChildInMiddle() {
        parent = getNewChild();
        List<Node> children = new ArrayList<Node>();
        for (int i = 0; i < 10; i++) {
            Node child = getNewChild();
            try {
                children.add(service.addChild(parent, child, null));
            } catch (DatabaseException e) {
                LOGGER.error("could not add child.", e);
            }
        }

        Node node = null;
        for (Node testChild : children) {
            // testChild in the middle of a chain
            try {
                node = service.getParent(testChild, true);
            } catch (DatabaseException e) {
                LOGGER.error("could not get parent", e);
                Assert.fail();
            }
            // parent returned,
            Assert.assertEquals(parent, node);
            // chain exists
            Assert.assertTrue(chainExists(node, testChild));
        }
    }

    // -
    @Test
    public void testGetParentNoParent() {
        Node child = getNewChild();
        // child has no parent: return null
        Node node = null;
        try {
            node = service.getParent(child, true);
        } catch (DatabaseException e) {
            LOGGER.error("could not get parent", e);
            Assert.fail();
        }
        Assert.assertNull(node);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetParentNull() {
        // child is null: exception
        try {
            service.getParent(null, true);
        } catch (DatabaseException e) {
            LOGGER.error("could not get parent", e);
            Assert.fail();
        }
    }

    // +
    @Test
    public void testGetLastChildWithId() {

        Node child = getNewChild();
        try {
            service.addChild(parent, child, null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
        // last_child_id set:
        Node lastChild = null;
        try {
            lastChild = service.getLastChild(parent);
        } catch (DatabaseException e) {
            LOGGER.error("could not get last child", e);
            Assert.fail();
        }
        // last child returned,
        Assert.assertEquals(child, lastChild);
        // chain exists
        Assert.assertTrue(chainExists(parent, lastChild));
    }

    @Test
    public void testGetLastChildNoId() {

        Node child = getNewChild();
        try {
            service.addChild(parent, child, null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
        }
        // remove last_child_id from parent
        tx = graphDatabaseService.beginTx();
        try {
            parent.removeProperty(NewDatasetService.LAST_CHILD_ID);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set property", e);
            Assert.fail();
        } finally {
            tx.finish();
        }

        // last_child_id not set:
        Node lastChild = null;
        try {
            lastChild = service.getLastChild(parent);
        } catch (DatabaseException e) {
            LOGGER.error("could not get last child", e);
            Assert.fail();
        }
        // last_child returned,
        Assert.assertEquals(child, lastChild);
        // chain exists,
        Assert.assertTrue(chainExists(parent, lastChild));
        // last_child_id updated
        Assert.assertEquals(lastChild.getId(),
                parent.getProperty(NewDatasetService.LAST_CHILD_ID, 0L));
    }

    // -
    @Test
    public void testGetLastChildNoChildren() {
        Node node = getNewChild();
        // parent has no children: return null
        Node lastChild = null;
        try {
            lastChild = service.getLastChild(node);
        } catch (DatabaseException e) {
            LOGGER.error("could not get last child", e);
            Assert.fail();
        }
        Assert.assertNull(lastChild);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLastChildNull() {
        // parent is null: exception
        try {
            service.getLastChild(null);
        } catch (DatabaseException e) {
            LOGGER.error("could not get last child", e);
            Assert.fail();
        }
    }

    // +
    @Test
    public void testGetChildrenChainTraverser() {

        Iterable<Node> traverser = service.getChildrenChainTraverser(parent);
        // traverser not null,
        Assert.assertNotNull(traverser);
        Assert.assertTrue(traverser.iterator().hasNext());
        Node prevNode = null;
        for (Node node : traverser) {

            if (prevNode != null) {
                // all relationships - next,
                // children appear in the order they are linked
                Assert.assertEquals(
                        node,
                        prevNode.getRelationships(DatasetRelationTypes.NEXT,
                                Direction.OUTGOING).iterator().next()
                                .getOtherNode(prevNode));
            }
            // check that node is in the chain
            Assert.assertTrue(chainExists(parent, node));
            prevNode = node;
        }
    }

    @Test
    public void testGetChildrenChainTraverserNoChildren() {
        Node node = getNewChild();
        // project has no children:
        Iterable<Node> traverser = service.getChildrenChainTraverser(node);
        // traverser not null,
        Assert.assertNotNull(traverser);
        // !iterator.hasNext()
        Assert.assertFalse(traverser.iterator().hasNext());
    }

    @Test
    public void testGetChildrenChainTraverserNoChildrenComplexChain() {
        parent = getNewChild();
        Node[][] nodes = getComplexChain(parent);

        // check on nodes that have no children has no children:
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 1; j < nodes[i].length; j++) {
                Iterable<Node> traverser = service
                        .getChildrenChainTraverser(nodes[i][j]);
                // traverser not null,
                Assert.assertNotNull(traverser);
                // !iterator.hasNext()
                Iterator<Node> it = traverser.iterator();
                Assert.assertFalse(it.hasNext());
            }
        }
    }

    // -
    @Test(expected = IllegalArgumentException.class)
    public void testGetChildrenChainTraverserNull() {
        // project is null: exception
        service.getChildrenChainTraverser(null);
    }

    // +
    @Test
    public void testGetParentComplexChain() {
        parent = getNewChild();
        Node[][] nodes = getComplexChain(parent);

        // check that valid parent is returned
        for (int i = 0; i < nodes.length; i++) {
            try {
                Assert.assertEquals(parent,
                        service.getParent(nodes[i][0], true));
                Assert.assertTrue(chainExists(parent, nodes[i][0]));
                for (int j = 1; j < nodes[i].length; j++) {
                    Assert.assertEquals(nodes[i][0],
                            service.getParent(nodes[i][j], true));
                    Assert.assertTrue(chainExists(nodes[i][0], nodes[i][j]));
                }
            } catch (DatabaseException e) {
                LOGGER.error("could not get parent", e);
                Assert.fail();
            }
        }
    }

    @Test
    public void testGetLastChildComplexChain() {
        parent = getNewChild();
        Node[][] nodes = getComplexChain(parent);

        // check that valid last child is returned
        try {
            Node lastChild = service.getLastChild(parent);
            Assert.assertEquals(nodes[nodes.length - 1][0], lastChild);
            Assert.assertTrue(chainExists(parent, lastChild));
            for (int i = 0; i < nodes.length; i++) {
                lastChild = service.getLastChild(nodes[i][0]);
                Assert.assertEquals(nodes[i][nodes[i].length - 1], lastChild);
                Assert.assertTrue(chainExists(nodes[i][0], lastChild));
            }
        } catch (DatabaseException e) {
            LOGGER.error("could not get last child", e);
            Assert.fail();
        }
    }

    @Test
    public void testGetChainTraverserComplexChain() {
        parent = getNewChild();
        Node[][] nodes = getComplexChain(parent);

        // test traverser
        Iterable<Node> traverser = service.getChildrenChainTraverser(parent);
        // traverser not null,
        Assert.assertNotNull(traverser);
        Node prevNode = null;
        for (Node node : traverser) {
            if (prevNode != null) {
                // all relationships - next,
                // children appear in the order they are linked
                Assert.assertEquals(
                        node,
                        prevNode.getRelationships(DatasetRelationTypes.NEXT,
                                Direction.OUTGOING).iterator().next()
                                .getOtherNode(prevNode));
            }
            prevNode = node;
            // check that node is in the chain
            Assert.assertTrue(chainExists(parent, node));
        }

        // test traverser on every high level child
        for (int i = 0; i < nodes.length; i++) {
            traverser = service.getChildrenChainTraverser(nodes[i][0]);
            // traverser not null,
            Assert.assertNotNull(traverser);
            prevNode = null;
            for (Node node : traverser) {
                if (prevNode != null) {
                    // all relationships - next,
                    // children appear in the order they are linked
                    Assert.assertEquals(
                            node,
                            prevNode.getRelationships(
                                    DatasetRelationTypes.NEXT,
                                    Direction.OUTGOING).iterator().next()
                                    .getOtherNode(prevNode));
                }
                prevNode = node;
                // check that node is in the chain
                Assert.assertTrue(chainExists(nodes[i][0], node));
            }
        }
    }

    // +
    @Test
    public void testAddChildNoChain() {
        parent = getNewChild();
        List<Node> children = new ArrayList<Node>();
        // add child to parent
        for (int i = 0; i < 4; i++) {
            try {
                children.add(service.addChild(parent, getNewChild()));
            } catch (DatabaseException e) {
                LOGGER.error("could not add child", e);
                Assert.fail();
            }
        }
        // check that a relationship is created
        for (Relationship rel : parent.getRelationships(
                DatasetRelationshipTypes.CHILD, Direction.OUTGOING)) {
            Assert.assertTrue(children.contains(rel.getOtherNode(parent)));
        }
    }

    // -
    @Test(expected = IllegalArgumentException.class)
    public void testAddChildNoChainParentNull() {
        // parent is null: exception
        try {
            service.addChild(null, getNewChild());
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddChildNoChainChildNull() {
        // child is null: exception
        try {
            service.addChild(parent, null);
        } catch (DatabaseException e) {
            LOGGER.error("could not add child", e);
            Assert.fail();
        }
    }

    // +
    @Test
    public void testGetChildrenTraverser() {
        Iterable<Node> traverser = service.getChildrenTraverser(parent);
        // traverser not null,
        Assert.assertNotNull(traverser);
        for (Node node : traverser) {
            // all children are lined to parent
            for (Relationship rel : node.getRelationships(
                    DatasetRelationTypes.CHILD, Direction.INCOMING)) {
                Assert.assertEquals(parent, rel.getOtherNode(node));
            }
        }
    }

    @Test
    public void testGetChildrenTraverserNoChildren() {
        // parent has no children
        parent = getNewChild();
        Iterable<Node> traverser = service.getChildrenChainTraverser(parent);
        // traverser not null,
        Assert.assertNotNull(traverser);
        // check that no children are returned
        Assert.assertFalse(traverser.iterator().hasNext());
    }

    // -
    @Test(expected = IllegalArgumentException.class)
    public void testGetChildrenTraverserNull() {
        // parent is null: exception
        service.getChildrenChainTraverser(null);
    }

    // +
    @Test
    public void testGetParentNoChain() {
        // children are added via addChild(Node, Node) method
        parent = getNewChild();
        List<Node> children = new ArrayList<Node>();
        // add child to parent
        for (int i = 0; i < 4; i++) {
            try {
                children.add(service.addChild(parent, getNewChild()));
            } catch (DatabaseException e) {
                LOGGER.error("could not add child", e);
                Assert.fail();
            }
        }
        // check that a relationship is created the valid parent is returned
        for (Node node : children) {
            try {
                Assert.assertEquals(parent, service.getParent(node, false));
            } catch (DatabaseException e) {
                LOGGER.error("could not get parent", e);
                Assert.fail();
            }
        }
    }

    // +
    @Test
    public void testGetElementByType() {
        parent = getNewChild();
        Node[][] nodes = getComplexChain(parent);

        List<Node> files = new ArrayList<Node>();
        List<Node> ms = new ArrayList<Node>();
        List<Node> mps = new ArrayList<Node>();

        tx = graphDatabaseService.beginTx();
        try {
            for (Node[] nodds : nodes) {
                for (Node node : nodds) {
                    DriveNodeTypes type = getRandomType();
                    node.setProperty(NewAbstractService.TYPE, type.getId());
                    switch (type) {
                    case FILE:
                        files.add(node);
                        break;
                    case M:
                        ms.add(node);
                        break;
                    case MP:
                        mps.add(node);
                        break;
                    }
                }
            }
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set node types.", e);
        } finally {
            tx.finish();
        }

        DriveNodeTypes[] possibleDriveNodeTypes = new DriveNodeTypes[] 
                {DriveNodeTypes.FILE, DriveNodeTypes.M, DriveNodeTypes.MP};
        
        for (DriveNodeTypes type : possibleDriveNodeTypes) {

            Iterable<Node> traverser = service.findAllDatasetElements(parent,
                    type);

            // traverser not null,
            Assert.assertNotNull(traverser);
            Assert.assertTrue(traverser.iterator().hasNext());

            for (Node node : traverser) {
                DriveNodeTypes testType = DriveNodeTypes.findById(node
                        .getProperty(NewAbstractService.TYPE, null).toString());
                // type correct
                Assert.assertEquals(type, testType);
                // node correct
                switch (testType) {
                case FILE:
                    Assert.assertTrue(files.contains(node));
                    break;
                case M:
                    Assert.assertTrue(ms.contains(node));
                    break;
                case MP:
                    Assert.assertTrue(mps.contains(node));
                    break;
                default:
                    Assert.fail("A node of a wrong type returned");
                    break;
                }

            }
        }
    }

    private DriveNodeTypes getRandomType() {
        int sw = (int) (Math.random() * 3);

        switch (sw) {
        case 0:
            return DriveNodeTypes.FILE;
        case 1:
            return DriveNodeTypes.MP;
        default:
            return DriveNodeTypes.M;
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

    private Node getNewChild() {
        tx = graphDatabaseService.beginTx();
        Node child = null;
        try {
            child = graphDatabaseService.createNode();
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create node", e);
        } finally {
            tx.finish();
        }
        return child;
    }

    private Node[][] getComplexChain(Node parentNode) {

        Node[][] nodes = new Node[10][5];

        // create a complex chain of nodes
        for (int i = 0; i < nodes.length; i++) {
            try {
                nodes[i][0] = service.addChild(parentNode, getNewChild(), null);
                for (int j = 1; j < nodes[i].length; j++) {
                    nodes[i][j] = service.addChild(nodes[i][0], getNewChild(),
                            null);
                }
            } catch (DatabaseException e) {
                LOGGER.error("could not add child", e);
            }
        }
        return nodes;
    }

}
