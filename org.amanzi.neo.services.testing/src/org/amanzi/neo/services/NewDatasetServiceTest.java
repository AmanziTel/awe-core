package org.amanzi.neo.services;

import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
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
import org.neo4j.shell.kernel.apps.Dbinfo;

/**
 * Test for org.amanzi.neo.services.DataService
 * 
 * @author kruglik_a
 * 
 */
public class NewDatasetServiceTest extends AbstractAWETest {

	private static Logger LOGGER = Logger
			.getLogger(NewDatasetServiceTest.class);

	private Node parent;

	@BeforeClass
	public static final void beforeClass() {
		clearDb();
		initializeDb();
	}

	@AfterClass
	public static final void afterClass() {
		stopDb();
		clearDb();
	}

	private Transaction tx;
	private NewDatasetService service;

	@Before
	public final void before() {
		service = new NewDatasetService(graphDatabaseService);
		initProjectNode();
	}

	@After
	public final void after() {
		deleteProjectNode();

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
		datasetNode.setProperty(NewDatasetService.PROPERTY_NAME_NAME, name);
		datasetNode.setProperty(NewDatasetService.PROPERTY_TYPE_NAME,
				type.name());
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
			projectNode.setProperty(NewDatasetService.PROPERTY_NAME_NAME,
					"project");
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
	 * delete PROJECT relation
	 */
	private void deleteProjectNode() {
		tx = graphDatabaseService.beginTx();
		try {
			projectNode.getSingleRelationship(DatasetRelationTypes.PROJECT,
					Direction.INCOMING).delete();
			tx.failure();

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
			DatasetTypeParameterException, DuplicateNodeNameException {

		Node actualDataset = service.createDataset(projectNode, NAME_1,
				DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

		boolean hasRelation = actualDataset.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.INCOMING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) actualDataset
				.getProperty(NewDatasetService.PROPERTY_NAME_NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(NewDatasetService.PROPERTY_TYPE_NAME);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.DRIVE.name(), actualType);

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
			DatasetTypeParameterException, DuplicateNodeNameException {
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
			DatasetTypeParameterException, DuplicateNodeNameException {
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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
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
				.getProperty(NewDatasetService.PROPERTY_NAME_NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(NewDatasetService.PROPERTY_TYPE_NAME);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.NETWORK.name(), actualType);
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
				.getProperty(NewDatasetService.PROPERTY_NAME_NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(NewDatasetService.PROPERTY_TYPE_NAME);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.NETWORK.name(), actualType);
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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DuplicateNodeNameException {

		Node actualDataset = service.getDataset(projectNode, NAME_1,
				DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

		boolean hasRelation = actualDataset.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.INCOMING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) actualDataset
				.getProperty(NewDatasetService.PROPERTY_NAME_NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(NewDatasetService.PROPERTY_TYPE_NAME);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.DRIVE.name(), actualType);

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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
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
			throws InvalidDatasetParameterException,
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

	// +
	@Test
	public void testAddChildNoChildren() {

		tx = graphDatabaseService.beginTx();
		Node parent = null, child = null;
		try {
			parent = graphDatabaseService.createNode();
			child = graphDatabaseService.createNode();
		} catch (Exception e) {
			LOGGER.error("Could not create node", e);
		} finally {
			tx.success();
			tx.finish();
		}
		// parent has no nodes, no last_child_id, lastChild = null:
		try {
			service.addChild(parent, child, null);
		} catch (Exception e) {
			LOGGER.error("Could not add child", e);
			Assert.fail();
		}

		// last_child_id is updated,
		Assert.assertEquals(child.getId(),
				parent.getProperty(NewDatasetService.LAST_CHILD_ID, ""));
		// parent_id set
		Assert.assertEquals(parent.getId(),
				child.getProperty(NewDatasetService.PARENT_ID, ""));
		// valid chain exists
		Assert.assertTrue(chainExists(parent, child));

	}

	@Test
	public void testAddChildLastChildId() {

		tx = graphDatabaseService.beginTx();
		Node child = null;
		try {
			child = graphDatabaseService.createNode();
		} catch (Exception e) {
			LOGGER.error("Could not create node", e);
		} finally {
			tx.success();
			tx.finish();
		}

		// parent has one node, last child id, lastChild = null:
		try {
			service.addChild(parent, child, null);
		} catch (Exception e) {
			LOGGER.error("Could not add child", e);
			Assert.fail();
		}

		// last_child_id updated,
		Assert.assertEquals(child.getId(),
				parent.getProperty(NewDatasetService.LAST_CHILD_ID, ""));
		// parent_id updated,
		Assert.assertEquals(parent.getId(),
				child.getProperty(NewDatasetService.PARENT_ID, ""));
		// there is a chain from parent to the child
		Assert.assertTrue(chainExists(parent, child));
	}

	@Test
	public void testAddChildLastChild() {

		// parent has nodes, last_child_id not set, lastChild set:
		// relationship,last_child_id updated,parent_id updated, chain exists
	}

	@Test
	public void testAddChildNoPreset() {
		// parent has nodes, last_child_id not set, lastChild not set:
		// relationship, last_child_id updated, parent_id updated, chain exists
	}

	@Test
	public void testAddChildLCIdWrong() {
		// parent has nodes, last_child_id is wrong, last child set
		// relationship, last_child_id updated, parent_id updated, chain exists
	}

	@Test
	public void testAddChildPresetWrong() {
		// parent has nodes, last_child_id is wrong, last child set wrong
		// relationship, last_child_id updated, parent_id updated, chain exists
	}

	// -
	@Test(expected = NullPointerException.class)
	public void testAddChildParentNull() {
		// parent is null: exception
	}

	@Test(expected = NullPointerException.class)
	public void testAddChildChildNull() {

		// child is null: exception
	}

	// +
	@Test
	public void testGetParentWithId() {

		// parent_id set: parent returned, chain exists
	}

	public void testGetParentNoId() {

		// parent_id not set: parent returned, chain exists, parent_id updated
	}

	// -
	@Test
	public void testGetParentWrongId() {
		// parent_id set wrong: parent returned, chain exists, parent_id updated
	}

	@Test
	public void testGetParentNoParent() {
		// child has no parent: return null
	}

	@Test(expected = NullPointerException.class)
	public void testGetParentNull() {

		// child is null: exception
	}

	// +
	@Test
	public void testGetLastChildWithId() {

		// last_child_id set: last child returned, chain exists
	}

	@Test
	public void testGetLastChildNoId() {

		// last_child_id not set: last_child returned, chain exists,
		// last_child_id updated
	}

	// -
	@Test
	public void testGetLastChildWrongId() {
		// last_child_id set wrong: last_chld returned, chain exists,
		// last_child_id updated
	}

	@Test
	public void testGetLastChildNoChildren() {
		// parent has no children: return null
	}

	@Test
	public void testGetLastChildNull() {
		// parent is null: exception
	}

	// +
	@Test
	public void testGetChildrenChainTraverser() {

		// valid chain exists: traverser not null, first relationship is child,
		// all others - next, children appear in the order they are linked
	}

	@Test
	public void testGetChildrenChainTraverserNoChildren() {
		// project has no children: traverser not null, !iterator.hasNext()
	}

	// -
	@Test(expected = NullPointerException.class)
	public void testGetChildrenChainTraverserNull() {
		// project is null: exception

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
