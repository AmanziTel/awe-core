package org.amanzi.neo.services;

import java.util.List;

import junit.framework.Assert;

import org.amanzi.neo.services.DataService.DatasetRelationTypes;
import org.amanzi.neo.services.DataService.DatasetTypes;
import org.amanzi.neo.services.DataService.DriveTypes;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.exceptions.DublicateDatasetException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.testing.AbstractAWETest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Test for org.amanzi.neo.services.DataService
 * 
 * @author kruglik_a
 * 
 */
public class DataServiceTest extends AbstractAWETest {

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
	private DataService service;

	@Before
	public final void before() {
		service = new DataService(graphDatabaseService);
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
		datasetNode.setProperty(DataService.NAME, name);
		datasetNode.setProperty(DataService.TYPE, type.name());
		if (driveType != null)
			datasetNode.setProperty(DataService.DRIVE_TYPE, driveType.name());
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
			projectNode.setProperty(DataService.NAME, "project");
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
	 */
	@Test
	public void findDatasetTest() throws InvalidDatasetParameterException,
			DatasetTypeParameterException {

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
	 */
	@Test
	public void findDatasetNullTest() throws InvalidDatasetParameterException,
			DatasetTypeParameterException {

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
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetExeptionNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, null, DatasetTypes.NETWORK);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetExeptionTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, NAME_1, null);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter projectNode = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetExeptionProjectNodeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(null, NAME_1, DatasetTypes.NETWORK);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = ""
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetExeptionEmptyNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, "", DatasetTypes.NETWORK);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type != NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void findDatasetNetworkTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, NAME_1, DatasetTypes.DRIVE);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when this dataset exists
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 */
	@Test
	public void findDatasetWithDriveTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {

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
	 */
	@Test
	public void findDatasetWithDriveTypeNullTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {

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
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void findDatasetWithDriveTypeNetworkTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, NAME_1, DatasetTypes.NETWORK,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetWithDriveTypeExeptionNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, null, DatasetTypes.DRIVE,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetWithDriveTypeExeptionTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, NAME_1, null, DriveTypes.NEMO_V1);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter driveType = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetWithDriveTypeExeptionDriveTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, NAME_1, DatasetTypes.DRIVE, null);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter projectNode = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetWithDriveTypeExeptionProjectNodeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(null, NAME_1, DatasetTypes.DRIVE,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = ""
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void findDatasetWithDriveTypeExeptionEmptyNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException {
		service.findDataset(projectNode, "", DatasetTypes.DRIVE,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when all parameter != null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test
	public void createDatasetWithDriveTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {

		Node actualDataset = service.createDataset(projectNode, NAME_1,
				DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

		boolean hasRelation = actualDataset.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.INCOMING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) actualDataset
				.getProperty(DataService.NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(DataService.TYPE);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.DRIVE.name(), actualType);

		String actualDriveType = (String) actualDataset
				.getProperty(DataService.DRIVE_TYPE);
		Assert.assertEquals("dataset has wrong driveType",
				DriveTypes.NEMO_V1.name(), actualDriveType);
	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when parameter name = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void createDatasetWithDriveTypeExeptionNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(projectNode, null, DatasetTypes.NETWORK,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when parameter type == NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void createDatasetWithDriveTypeExeptionDatasetTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(projectNode, NAME_1, DatasetTypes.NETWORK,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when parameter type = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void createDatasetWithDriveTypeExeptionTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(projectNode, NAME_1, null, DriveTypes.NEMO_V1);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter projectNode = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void createDatasetWithDriveTypeExeptionProjectNodeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(null, NAME_1, DatasetTypes.DRIVE,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = ""
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void createDatasetWithDriveTypeExeptionEmptyNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(projectNode, "", DatasetTypes.DRIVE,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when all parameter != null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test
	public void createDatasetTest() throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {

		Node actualDataset = service.createDataset(projectNode, NAME_1,
				DatasetTypes.NETWORK);

		boolean hasRelation = actualDataset.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.INCOMING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) actualDataset
				.getProperty(DataService.NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(DataService.TYPE);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.NETWORK.name(), actualType);
	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void createDatasetExeptionNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(projectNode, null, DatasetTypes.NETWORK);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type != NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void createDatasetExeptionDatasetTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(projectNode, NAME_1, DatasetTypes.DRIVE);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void createDatasetExeptionTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(projectNode, NAME_1, null);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter projectNode = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void createDatasetExeptionProjectNodeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(null, NAME_1, DatasetTypes.NETWORK);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = ""
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void createDatasetExeptionEmptyNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.createDataset(projectNode, "", DatasetTypes.NETWORK);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes, Node projectNode)
	 * when this dataset doesn't exist
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test
	public void getDatasetCreateTest() throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		Node actualDataset = service.getDataset(projectNode, NAME_1,
				DatasetTypes.NETWORK);

		boolean hasRelation = actualDataset.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.INCOMING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) actualDataset
				.getProperty(DataService.NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(DataService.TYPE);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.NETWORK.name(), actualType);
	}

	/**
	 * testing method getDataset(String name, DatasetTypes, Node projectNode)
	 * when this dataset exists
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test
	public void getDatasetFindTest() throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {

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
	 * @throws DublicateDatasetException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetExeptionNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(projectNode, null, DatasetTypes.NETWORK);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type != NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void getDatasetExeptionDatasetTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(projectNode, NAME_1, DatasetTypes.DRIVE);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetExeptionTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(projectNode, NAME_1, null);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter projectNode = null
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetExeptionProjectNodeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(null, NAME_1, DatasetTypes.NETWORK);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = ""
	 * 
	 * @throws InvalidDatasetParameterException
	 * @throws DatasetTypeParameterException
	 * @throws DublicateDatasetException
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetExeptionEmptyNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(projectNode, "", DatasetTypes.NETWORK);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when this dataset exists
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test
	public void getDatasetWithDriveTypeFindTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {

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
	 * @throws DublicateDatasetException 
	 */
	@Test
	public void getDatasetWithDriveTypeCreateTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {

		Node actualDataset = service.getDataset(projectNode, NAME_1,
				DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

		boolean hasRelation = actualDataset.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.INCOMING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) actualDataset
				.getProperty(DataService.NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(DataService.TYPE);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.DRIVE.name(), actualType);

		String actualDriveType = (String) actualDataset
				.getProperty(DataService.DRIVE_TYPE);
		Assert.assertEquals("dataset has wrong driveType",
				DriveTypes.NEMO_V1.name(), actualDriveType);
	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void getDatasetWithDriveTypeNetworkTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(projectNode, NAME_1, DatasetTypes.NETWORK,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetWithDriveTypeExeptionNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(projectNode, null, DatasetTypes.DRIVE,
				DriveTypes.NEMO_V1);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetWithDriveTypeExeptionTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(projectNode, NAME_1, null, DriveTypes.NEMO_V1);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter driveType = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetWithDriveTypeExeptionDriveTypeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(projectNode, NAME_1, DatasetTypes.DRIVE, null);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter projectNode = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetWithDriveTypeExeptionProjectNodeTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
		service.getDataset(null, NAME_1, DatasetTypes.DRIVE, DriveTypes.NEMO_V1);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = ""
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidDatasetParameterException
	 * @throws DublicateDatasetException 
	 */
	@Test(expected = InvalidDatasetParameterException.class)
	public void getDatasetWithDriveTypeExeptionEmptyNameTest()
			throws InvalidDatasetParameterException,
			DatasetTypeParameterException, DublicateDatasetException {
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

}
