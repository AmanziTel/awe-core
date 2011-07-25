package org.amanzi.neo.services;

import java.util.List;

import junit.framework.Assert;

import org.amanzi.neo.services.DataService.DatasetRelationTypes;
import org.amanzi.neo.services.DataService.DatasetTypes;
import org.amanzi.neo.services.DataService.DriveTypes;
import org.amanzi.neo.services.exceptions.InvalidParameterException;
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
		tx = graphDatabaseService.beginTx();
		service = new DataService(graphDatabaseService);
		initProjectNode();
	}

	@After
	public final void after() {
		deleteProjectNode();
		tx.failure();
		tx.finish();
		tx = null;
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
		projectNode = graphDatabaseService.createNode();
		graphDatabaseService.getReferenceNode().createRelationshipTo(
				projectNode, DatasetRelationTypes.PROJECT);
		projectNode.setProperty(DataService.NAME, "project");
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
		Node datasetNode = graphDatabaseService.createNode();
		projectNode.createRelationshipTo(datasetNode,
				DatasetRelationTypes.DATASET);
		setPropertyToDatasetNode(datasetNode, name, type, driveType);
		return datasetNode;
	}

	/**
	 * delete PROJECT relation
	 */
	private void deleteProjectNode() {
		projectNode.getSingleRelationship(DatasetRelationTypes.PROJECT,
				Direction.INCOMING).delete();
	}

	/**
	 * get property of dataset node by key
	 * 
	 * @param key
	 * @return Object value
	 */
	private Object getDatasetProperty(String key) {
		return projectNode
				.getSingleRelationship(DatasetRelationTypes.DATASET,
						Direction.OUTGOING).getEndNode().getProperty(key);
	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when this dataset exists
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test
	public void findDatasetTest() throws InvalidParameterException,
			DatasetTypeParameterException {

		Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);

		Node checkNode = service.findDataset(NAME_1, DatasetTypes.NETWORK,
				projectNode);

		Assert.assertTrue("method findDataset() return wrong node",
				datasetNode.equals(checkNode));

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when this dataset doesn't exist
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test
	public void findDatasetNullTest() throws InvalidParameterException,
			DatasetTypeParameterException {

		initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);

		Node checkNode = service.findDataset(NAME_2, DatasetTypes.NETWORK,
				projectNode);

		Assert.assertNull("method findDataset() return wrong node", checkNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetExeptionNameTest() throws InvalidParameterException,
			DatasetTypeParameterException {
		service.findDataset(null, DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetExeptionTypeTest() throws InvalidParameterException,
			DatasetTypeParameterException {
		service.findDataset(NAME_1, null, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter projectNode = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetExeptionProjectNodeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.findDataset(NAME_1, DatasetTypes.NETWORK, null);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = ""
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetExeptionEmptyNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.findDataset("", DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type != NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void findDatasetNetworkTest() throws InvalidParameterException,
			DatasetTypeParameterException {
		service.findDataset(NAME_1, DatasetTypes.OTHER, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when this dataset exists
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test
	public void findDatasetWithDriveTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {

		Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1);

		Node checkNode = service.findDataset(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, projectNode);

		Assert.assertTrue("method findDataset() return wrong node",
				datasetNode.equals(checkNode));

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when this dataset doesn't exist
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test
	public void findDatasetWithDriveTypeNullTest()
			throws InvalidParameterException, DatasetTypeParameterException {

		Node checkNode = service.findDataset(NAME_2, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, projectNode);

		Assert.assertNull("method findDataset() return wrong node", checkNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void findDatasetWithDriveTypeNetworkTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.findDataset(NAME_1, DatasetTypes.NETWORK,
				DriveTypes.DRIVE_TYPE_1, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.findDataset(null, DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1,
				projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.findDataset(NAME_1, null, DriveTypes.DRIVE_TYPE_1, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter driveType = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionDriveTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.findDataset(NAME_1, DatasetTypes.OTHER, null, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter projectNode = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionProjectNodeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.findDataset(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, null);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = ""
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionEmptyNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.findDataset("", DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1,
				projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when all parameter != null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test
	public void createDatasetWithDriveTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {

		Node actualDataset = service.createDataset(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, projectNode);

		boolean hasRelation = actualDataset.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.INCOMING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) actualDataset
				.getProperty(DataService.NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(DataService.TYPE);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.OTHER.name(), actualType);

		String actualDriveType = (String) actualDataset
				.getProperty(DataService.DRIVE_TYPE);
		Assert.assertEquals("dataset has wrong driveType",
				DriveTypes.DRIVE_TYPE_1.name(), actualDriveType);
	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when parameter name = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetWithDriveTypeExeptionNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset(null, DatasetTypes.NETWORK,
				DriveTypes.DRIVE_TYPE_1, projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when parameter type == NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void createDatasetWithDriveTypeExeptionDatasetTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset(NAME_1, DatasetTypes.NETWORK,
				DriveTypes.DRIVE_TYPE_1, projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when parameter type = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetWithDriveTypeExeptionTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset(NAME_1, null, DriveTypes.DRIVE_TYPE_1,
				projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter projectNode = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetWithDriveTypeExeptionProjectNodeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, null);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = ""
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetWithDriveTypeExeptionEmptyNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset("", DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1,
				projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when all parameter != null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test
	public void createDatasetTest() throws InvalidParameterException,
			DatasetTypeParameterException {

		Node actualDataset = service.createDataset(NAME_1,
				DatasetTypes.NETWORK, projectNode);

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
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetExeptionNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset(null, DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type != NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void createDatasetExeptionDatasetTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset(NAME_1, DatasetTypes.OTHER, projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetExeptionTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset(NAME_1, null, projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter projectNode = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetExeptionProjectNodeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset(NAME_1, DatasetTypes.NETWORK, null);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = ""
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetExeptionEmptyNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.createDataset("", DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes, Node projectNode)
	 * when this dataset doesn't exist
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test
	public void getDatasetCreateTest() throws InvalidParameterException,
			DatasetTypeParameterException {
		Node actualDataset = service.getDataset(NAME_1, DatasetTypes.NETWORK,
				projectNode);

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
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test
	public void getDatasetFindTest() throws InvalidParameterException,
			DatasetTypeParameterException {

		Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);

		Node actualDataset = service.getDataset(NAME_1, DatasetTypes.NETWORK,
				projectNode);

		Assert.assertEquals("method getDataset return wrong node", datasetNode,
				actualDataset);
	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetExeptionNameTest() throws InvalidParameterException,
			DatasetTypeParameterException {
		service.getDataset(null, DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type != NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void getDatasetExeptionDatasetTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset(NAME_1, DatasetTypes.OTHER, projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetExeptionTypeTest() throws InvalidParameterException,
			DatasetTypeParameterException {
		service.getDataset(NAME_1, null, projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter projectNode = null
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetExeptionProjectNodeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset(NAME_1, DatasetTypes.NETWORK, null);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = ""
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetExeptionEmptyNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset("", DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when this dataset exists
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test
	public void getDatasetWithDriveTypeFindTest()
			throws InvalidParameterException, DatasetTypeParameterException {

		Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1);

		Node checkNode = service.getDataset(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, projectNode);

		Assert.assertTrue("method findDataset() return wrong node",
				datasetNode.equals(checkNode));

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveType
	 * driveType, Node projectNode) when dataset node doesn't exist
	 * 
	 * @throws InvalidParameterException
	 * @throws DatasetTypeParameterException
	 */
	@Test
	public void getDatasetWithDriveTypeCreateTest()
			throws InvalidParameterException, DatasetTypeParameterException {

		Node actualDataset = service.getDataset(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, projectNode);

		boolean hasRelation = actualDataset.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.INCOMING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) actualDataset
				.getProperty(DataService.NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		String actualType = (String) actualDataset
				.getProperty(DataService.TYPE);
		Assert.assertEquals("dataset has wrong type",
				DatasetTypes.OTHER.name(), actualType);

		String actualDriveType = (String) actualDataset
				.getProperty(DataService.DRIVE_TYPE);
		Assert.assertEquals("dataset has wrong driveType",
				DriveTypes.DRIVE_TYPE_1.name(), actualDriveType);
	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = NETWORK
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = DatasetTypeParameterException.class)
	public void getDatasetWithDriveTypeNetworkTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset(NAME_1, DatasetTypes.NETWORK,
				DriveTypes.DRIVE_TYPE_1, projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetWithDriveTypeExeptionNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset(null, DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1,
				projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetWithDriveTypeExeptionTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset(NAME_1, null, DriveTypes.DRIVE_TYPE_1, projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter driveType = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetWithDriveTypeExeptionDriveTypeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset(NAME_1, DatasetTypes.OTHER, null, projectNode);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter projectNode = null
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetWithDriveTypeExeptionProjectNodeTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset(NAME_1, DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1,
				null);

	}

	/**
	 * testing method getDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = ""
	 * 
	 * @throws DatasetTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void getDatasetWithDriveTypeExeptionEmptyNameTest()
			throws InvalidParameterException, DatasetTypeParameterException {
		service.getDataset("", DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1,
				projectNode);

	}

	/**
	 * testing method getAllDatasets(Node projectNode) when nobody datasets
	 * exist
	 * 
	 * @throws InvalidParameterException
	 */
	@Test
	public void findAllDatasetsEmptyTest() throws InvalidParameterException {
		List<Node> checkList = service.findAllDatasets(projectNode);
		Assert.assertTrue("method findAllDatasets return wrong nodes",
				checkList.isEmpty());
	}

	/**
	 * testing method getAllDatasets(Node projectNode) when some datasets exist
	 * 
	 * @throws InvalidParameterException
	 */
	@Test
	public void findAllDatasetsTest() throws InvalidParameterException {
		Node dataset_1 = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);
		Node dataset_2 = initDatasetNode(NAME_2, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1);
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
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findAllDatasetsExceptionTest() throws InvalidParameterException {
		service.findAllDatasets(null);
	}

	/**
	 * testing method getAllDatasetsByType(DatasetTypes type,Node projectNode)
	 * when nobody datasets by given type exist
	 * 
	 * @throws InvalidParameterException
	 */
	@Test
	public void findAllDatasetByTypesEmptyTest()
			throws InvalidParameterException {
		initDatasetNode(NAME_1, DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1);
		List<Node> checkList = service.findAllDatasetsByType(
				DatasetTypes.NETWORK, projectNode);
		Assert.assertTrue("method findAllDatasetsByType return wrong nodes",
				checkList.isEmpty());
	}

	/**
	 * testing method getAllDatasetsByType(DatasetTypes type, Node projectNode)
	 * when some datasets exist
	 * 
	 * @throws InvalidParameterException
	 */
	@Test
	public void findAllDatasetsByTypeTest() throws InvalidParameterException {
		Node dataset_1 = initDatasetNode(NAME_1, DatasetTypes.NETWORK, null);
		initDatasetNode(NAME_2, DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1);

		List<Node> checkList = service.findAllDatasetsByType(
				DatasetTypes.NETWORK, projectNode);

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
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findAllDatasetsByTypeExceptionProjectNodeTest()
			throws InvalidParameterException {
		service.findAllDatasetsByType(DatasetTypes.NETWORK, null);
	}

	/**
	 * testing method getAllDatasetsByType(DatasetTypes type,Node projectNode)
	 * when type == null
	 * 
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findAllDatasetsByTypeExceptionTypeTest()
			throws InvalidParameterException {
		service.findAllDatasetsByType(null, projectNode);
	}

}
