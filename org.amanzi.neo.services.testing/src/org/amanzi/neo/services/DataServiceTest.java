package org.amanzi.neo.services;

import junit.framework.Assert;

import org.amanzi.neo.services.DataService.DatasetRelationTypes;
import org.amanzi.neo.services.DataService.DatasetTypes;
import org.amanzi.neo.services.DataService.DriveTypes;
import org.amanzi.neo.services.exceptions.InvalidParameterException;
import org.amanzi.neo.services.exceptions.NetworkTypeParameterException;
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
		datasetNode.setProperty(DataService.TYPE, type);
		if (driveType != null)
			datasetNode.setProperty(DataService.DRIVE_TYPE, driveType);
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
	 */
	@Test
	public void findDatasetTest() throws InvalidParameterException {

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
	 */
	@Test
	public void findDatasetNullTest() throws InvalidParameterException {

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
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetExeptionNameTest() throws InvalidParameterException {
		service.findDataset(null, DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter type = null
	 * 
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetExeptionTypeTest() throws InvalidParameterException {
		service.findDataset(NAME_1, null, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter projectNode = null
	 * 
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetExeptionProjectNodeTest()
			throws InvalidParameterException {
		service.findDataset(NAME_1, DatasetTypes.NETWORK, null);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = ""
	 * 
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetExeptionEmptyNameTest()
			throws InvalidParameterException {
		service.findDataset("", DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when this dataset exists
	 * 
	 * @throws NetworkTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test
	public void findDatasetWithDriveTypeTest()
			throws InvalidParameterException, NetworkTypeParameterException {

		Node datasetNode = initDatasetNode(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1);

		Node checNode = service.findDataset(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, projectNode);

		Assert.assertTrue("method findDataset() return wrong node",
				datasetNode.equals(checNode));

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when this dataset doesn't exist
	 * 
	 * @throws NetworkTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test
	public void findDatasetWithDriveTypeNullTest()
			throws InvalidParameterException, NetworkTypeParameterException {

		Node checkNode = service.findDataset(NAME_2, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, projectNode);

		Assert.assertNull("method findDataset() return wrong node", checkNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = NETWORK
	 * 
	 * @throws NetworkTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = NetworkTypeParameterException.class)
	public void findDatasetWithDriveTypeNetworkTest()
			throws InvalidParameterException, NetworkTypeParameterException {
		service.findDataset(NAME_1, DatasetTypes.NETWORK,
				DriveTypes.DRIVE_TYPE_1, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = null
	 * 
	 * @throws NetworkTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionNameTest()
			throws InvalidParameterException, NetworkTypeParameterException {
		service.findDataset(null, DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1,
				projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter type = null
	 * 
	 * @throws NetworkTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionTypeTest()
			throws InvalidParameterException, NetworkTypeParameterException {
		service.findDataset(NAME_1, null, DriveTypes.DRIVE_TYPE_1, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter driveType = null
	 * 
	 * @throws NetworkTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionDriveTypeTest()
			throws InvalidParameterException, NetworkTypeParameterException {
		service.findDataset(NAME_1, DatasetTypes.OTHER, null, projectNode);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter projectNode = null
	 * 
	 * @throws NetworkTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionProjectNodeTest()
			throws InvalidParameterException, NetworkTypeParameterException {
		service.findDataset(NAME_1, DatasetTypes.OTHER,
				DriveTypes.DRIVE_TYPE_1, null);

	}

	/**
	 * testing method findDataset(String name, DatasetTypes type, DriveTypes
	 * driveType, Node projectNode) when parameter name = ""
	 * 
	 * @throws NetworkTypeParameterException
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void findDatasetWithDriveTypeExeptionEmptyNameTest()
			throws InvalidParameterException, NetworkTypeParameterException {
		service.findDataset("", DatasetTypes.OTHER, DriveTypes.DRIVE_TYPE_1,
				projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when all parameter != null
	 * 
	 * @throws InvalidParameterException
	 */
	@Test
	public void createDatasetTest() throws InvalidParameterException {

		service.createDataset(NAME_1, DatasetTypes.NETWORK, projectNode);

		boolean hasRelation = projectNode.hasRelationship(
				DatasetRelationTypes.DATASET, Direction.OUTGOING);
		Assert.assertTrue("not create DATASET relation", hasRelation);

		String actualName = (String) getDatasetProperty(DataService.NAME);
		Assert.assertEquals("dataset has wrong name", NAME_1, actualName);

		DatasetTypes actualType = (DatasetTypes) getDatasetProperty(DataService.TYPE);
		Assert.assertEquals("dataset has wrong type", DatasetTypes.NETWORK,
				actualType);
	}

	

	/**
	 * testing method createDataset(String name, DatasetTypes type, Node
	 * projectNode) when parameter name = null
	 * 
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetExeptionNameTest()
			throws InvalidParameterException {
		service.createDataset(null, DatasetTypes.NETWORK, projectNode);

	}

	/**
	 * testing method createDataset(String name, DatasetTypes type) when
	 * parameter type = null
	 * 
	 * @throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void createDatasetExeptionTypeTest()
			throws InvalidParameterException {
		service.createDataset(NAME_1, null, projectNode);

	}
}
