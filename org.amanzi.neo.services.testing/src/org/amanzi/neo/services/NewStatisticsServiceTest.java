package org.amanzi.neo.services;

import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewStatisticsService.StatisticsNodeTypes;
import org.amanzi.neo.services.NewStatisticsService.StatisticsRelationships;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateStatisticsException;
import org.amanzi.neo.services.exceptions.InvalidPropertyStatisticsNodeException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.exceptions.LoadVaultException;
import org.amanzi.neo.services.statistic.IVault;
import org.amanzi.neo.services.statistic.StatisticsVault;
import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class NewStatisticsServiceTest extends AbstractNeoServiceTest {

	private final static String PROPERTIES = "PROPERTIES";
	private final static String NEIGHBOURS = "Neighbours";
	private final static String NETWORK = "Network";
	private final static String SECTOR = "Sector";

	private static Logger LOGGER = Logger
			.getLogger(NewDatasetServiceTest.class);

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
	private NewStatisticsService service = new NewStatisticsService();
	private Node referenceNode = graphDatabaseService.getReferenceNode();

	

	@After
	public final void after() {

		cleanReferenceNode();
	}

	/**
	 * delete all referenceNode relationships
	 */
	private void cleanReferenceNode() {
		tx = graphDatabaseService.beginTx();
		try {
			Iterator<Relationship> iter = referenceNode.getRelationships()
					.iterator();
			while (iter.hasNext()) {
				iter.next().delete();
			}
			tx.success();
		} finally {
			tx.finish();
		}
	}

	/**
	 * this method checks vault for compliance with the expected values
	 * 
	 * @param vault
	 * @param expectedClass
	 * @param expectedCount
	 * @param expectedType
	 * @param expectedCountSubVault
	 */
	private void checkVault(IVault vault, Class<?> expectedClass,
			int expectedCount, String expectedType, int expectedCountSubVault) {
		Class<?> actualClass = vault.getClass();
		Assert.assertEquals("load wrong IVault object", expectedClass,
				actualClass);

		int actualCount = vault.getCount();
		Assert.assertEquals("vault has wrong count", expectedCount, actualCount);

		String actualType = vault.getType();
		Assert.assertEquals("vault has wrong type", expectedType, actualType);

		int count = vault.getSubVaults().size();
		Assert.assertEquals("vault has wrong count of subVault",
				expectedCountSubVault, count);
	}

	/**
	 * this method checks vault node for compliance with the expected values
	 * 
	 * @param vaultNode
	 * @param expectedName
	 * @param expectedClass
	 * @param expectedCount
	 * @param expectedCountSubVault
	 */
	private void checkVaultNode(Node vaultNode, String expectedName,
			Class<?> expectedClass, int expectedCount, int expectedCountSubVault) {
		String nodeType = (String) vaultNode.getProperty(
				NewAbstractService.TYPE, "");
		Assert.assertEquals("Vault node has not VAULT type",
				StatisticsNodeTypes.VAULT.getId(), nodeType);

		String nodeName = (String) vaultNode.getProperty(
				NewStatisticsService.NAME, "");
		Assert.assertEquals("", expectedName, nodeName);

		String klass = (String) vaultNode.getProperty(
				NewStatisticsService.CLASS, null);
		Assert.assertNotNull("Vault node has not property CLASS", klass);
		Assert.assertEquals("Vault node property CLASS is wrong value",
				expectedClass.getCanonicalName(), klass);

		int count = (Integer) vaultNode.getProperty(NewStatisticsService.COUNT,
				null);
		Assert.assertEquals("Vault node has wrong count", expectedCount, count);

		int countSubVault = 0;
		for (@SuppressWarnings("unused")
		Node subVaultNode : service.getSubVaultNodes(vaultNode)) {
			countSubVault++;
		}

		Assert.assertEquals(
				"Vault node has wrong count of CHILD relationships",
				expectedCountSubVault, countSubVault);
	}

	/**
	 * testing method saveVault(Node rootNode, IVault vault)
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 */
	@Test
	public void saveVaultPositiveTest() throws DatabaseException,
			InvalidStatisticsParameterException, DuplicateStatisticsException {
		LOGGER.debug("start saveVaultPositiveTest()");
		StatisticsVault propVault = new StatisticsVault(PROPERTIES);
		StatisticsVault neighboursSubVault = new StatisticsVault(NEIGHBOURS);
		StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
		propVault.addSubVault(neighboursSubVault);
		neighboursSubVault.addSubVault(networkSubVault);
		// create PropertyStatistics
		NewPropertyStatistics propStat = new NewPropertyStatistics("Counter",
				Integer.class);
		propStat.updatePropertyMap(1, 1);
		propStat.updatePropertyMap(2, 1);
		propStat.updatePropertyMap(1, 1);
		// add PropertyStatistics to propertyVault
		propVault.addPropertyStatistics(propStat);
		// create spy object
		NewStatisticsService mockService = Mockito.spy(service);

		mockService.saveVault(referenceNode, propVault);
		// check whether the method savePropertyStatistics() offered
		Mockito.verify(mockService, Mockito.times(1)).savePropertyStatistics(
				(NewPropertyStatistics) Mockito.any(), (Node) Mockito.any());

		boolean hasStatisticsRelationships = referenceNode.hasRelationship(
				StatisticsRelationships.STATISTICS, Direction.OUTGOING);
		Assert.assertTrue("not create StatisticsRelationships.STATISTICS",
				hasStatisticsRelationships);

		Node propVaultNode = referenceNode.getSingleRelationship(
				StatisticsRelationships.STATISTICS, Direction.OUTGOING)
				.getEndNode();
		checkVaultNode(propVaultNode, PROPERTIES, StatisticsVault.class, 0, 1);

		Node neighbourtsVaultNode = service.getSubVaultNodes(propVaultNode)
				.iterator().next();
		checkVaultNode(neighbourtsVaultNode, NEIGHBOURS, StatisticsVault.class,
				0, 1);
		Node networkVaultNode = service.getSubVaultNodes(neighbourtsVaultNode)
				.iterator().next();
		checkVaultNode(networkVaultNode, NETWORK, StatisticsVault.class, 0, 0);
		LOGGER.debug("finish saveVaultPositiveTest()");

	}

	/**
	 * testing method saveVault(Node rootNode, IVault vault) when parameter
	 * rootNode == null
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 */
	@Test(expected = InvalidStatisticsParameterException.class)
	public void saveVaultNullParameterRootNodeNegativeTest()
			throws DatabaseException, InvalidStatisticsParameterException,
			DuplicateStatisticsException {
		LOGGER.debug("start saveVaultNullParameterRootNodeNegativeTest()");
		StatisticsVault propVault = new StatisticsVault(PROPERTIES);
		service.saveVault(null, propVault);
		LOGGER.debug("finish saveVaultNullParameterRootNodeNegativeTest()");

	}

	/**
	 * testing method saveVault(Node rootNode, IVault vault) when parameter
	 * vault == null
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 */
	@Test(expected = InvalidStatisticsParameterException.class)
	public void saveVaultNullParameterVaultNegativeTest()
			throws DatabaseException, InvalidStatisticsParameterException,
			DuplicateStatisticsException {
		LOGGER.debug("start saveVaultNullParameterVaultNegativeTest()");
		service.saveVault(referenceNode, null);
		LOGGER.debug("finish saveVaultNullParameterVaultNegativeTest()");

	}

	/**
	 * testing method saveVault(Node rootNode, IVault vault) when rootNode
	 * already exists statistics
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 */
	@Test(expected = DuplicateStatisticsException.class)
	public void saveVaultDuplicateStatisticsNegativeTest()
			throws DatabaseException, InvalidStatisticsParameterException,
			DuplicateStatisticsException {
		LOGGER.debug("start saveVaultDuplicateStatisticsNegativeTest()");
		StatisticsVault propVault = new StatisticsVault(PROPERTIES);
		tx = graphDatabaseService.beginTx();
		try {
			Node statNode = graphDatabaseService.createNode();
			referenceNode.createRelationshipTo(statNode,
					StatisticsRelationships.STATISTICS);
			tx.success();
		} finally {
			tx.finish();
		}
		service.saveVault(referenceNode, propVault);
		LOGGER.debug("finish saveVaultDuplicateStatisticsNegativeTest()");

	}
	
	/**
	 * testing method saveVault(Node rootNode, IVault vault)
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 */
	@Test
	public void doubleSaveVaultPositiveTest() throws DatabaseException,
			InvalidStatisticsParameterException, DuplicateStatisticsException {
		LOGGER.debug("start doubleSaveVaultPositiveTest()");
		StatisticsVault propVault = new StatisticsVault(PROPERTIES);
		StatisticsVault neighboursSubVault = new StatisticsVault(NEIGHBOURS);
		StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
		StatisticsVault sectorSubVault = new StatisticsVault(SECTOR);
		
		propVault.addSubVault(neighboursSubVault);
		propVault.addSubVault(networkSubVault);
		networkSubVault.addSubVault(sectorSubVault);
		
		// create PropertyStatistics
		NewPropertyStatistics propStat = new NewPropertyStatistics("Counter",
				Integer.class);
		propStat.updatePropertyMap(1, 1);
		propStat.updatePropertyMap(2, 1);
		propStat.updatePropertyMap(1, 1);
		// add PropertyStatistics to propertyVault
		propVault.addPropertyStatistics(propStat);
		
		NewPropertyStatistics sectorStat = new NewPropertyStatistics("Sector counter", 
				Integer.class);
		sectorStat.updatePropertyMap(3, 5);
		sectorStat.updatePropertyMap(4, 10);
		// add PropertyStatistics to sectorVault
		sectorSubVault.addPropertyStatistics(sectorStat);
		
		NewPropertyStatistics neighbourStat = new NewPropertyStatistics("Neighbours counter",
				Integer.class);
		neighbourStat.updatePropertyMap(2, 3);
		neighbourStat.updatePropertyMap(3, 2);
		// add PropertyStatistics to neighboursVault
		neighboursSubVault.addPropertyStatistics(neighbourStat);
		
		// create spy object
		NewStatisticsService mockService = Mockito.spy(service);

		mockService.saveVault(referenceNode, propVault);
		propVault.setIsStatisticsChanged(true);
		mockService.saveVault(referenceNode, propVault);
		// check whether the method savePropertyStatistics() offered
		Mockito.verify(mockService, Mockito.times(6)).savePropertyStatistics(
				(NewPropertyStatistics) Mockito.any(), (Node) Mockito.any());

		boolean hasStatisticsRelationships = referenceNode.hasRelationship(
				StatisticsRelationships.STATISTICS, Direction.OUTGOING);
		Assert.assertTrue("not create StatisticsRelationships.STATISTICS",
				hasStatisticsRelationships);

		Node propVaultNode = referenceNode.getSingleRelationship(
				StatisticsRelationships.STATISTICS, Direction.OUTGOING)
				.getEndNode();
		checkVaultNode(propVaultNode, PROPERTIES, StatisticsVault.class, 0, 2);

		for (Node subVaultNode : service.getSubVaultNodes(propVaultNode)) {
			if (subVaultNode.getProperty(NewStatisticsService.NAME).toString().equals(NEIGHBOURS)) {
				checkVaultNode(subVaultNode, NEIGHBOURS, StatisticsVault.class, 0, 0);
			}
			else {
				checkVaultNode(subVaultNode, NETWORK, StatisticsVault.class, 0, 1);
			}
 		}
		LOGGER.debug("finish doubleSaveVaultPositiveTest()");

	}

	/**
	 * testing method saveVault(Node rootNode, IVault vault)
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 */
	@Test
	public void deleteVaultPositiveTest() throws DatabaseException,
			InvalidStatisticsParameterException, DuplicateStatisticsException {
		LOGGER.debug("start doubleSaveVaultPositiveTest()");
		StatisticsVault propVault = new StatisticsVault(PROPERTIES);
		StatisticsVault neighboursSubVault = new StatisticsVault(NEIGHBOURS);
		StatisticsVault networkSubVault = new StatisticsVault(NETWORK);
		StatisticsVault sectorSubVault = new StatisticsVault(SECTOR);
		
		propVault.addSubVault(neighboursSubVault);
		propVault.addSubVault(networkSubVault);
		networkSubVault.addSubVault(sectorSubVault);
		
		// create PropertyStatistics
		NewPropertyStatistics propStat = new NewPropertyStatistics("Counter",
				Integer.class);
		propStat.updatePropertyMap(1, 1);
		propStat.updatePropertyMap(2, 1);
		propStat.updatePropertyMap(1, 1);
		// add PropertyStatistics to propertyVault
		propVault.addPropertyStatistics(propStat);
		
		NewPropertyStatistics sectorStat = new NewPropertyStatistics("Sector counter", 
				Integer.class);
		sectorStat.updatePropertyMap(3, 5);
		sectorStat.updatePropertyMap(4, 10);
		// add PropertyStatistics to sectorVault
		sectorSubVault.addPropertyStatistics(sectorStat);
		
		NewPropertyStatistics neighbourStat = new NewPropertyStatistics("Neighbours counter",
				Integer.class);
		neighbourStat.updatePropertyMap(2, 3);
		neighbourStat.updatePropertyMap(3, 2);
		// add PropertyStatistics to neighboursVault
		neighboursSubVault.addPropertyStatistics(neighbourStat);
		
		// create spy object
		NewStatisticsService mockService = Mockito.spy(service);

		mockService.saveVault(referenceNode, propVault);
		mockService.deleteVault(referenceNode);
		// check whether the method savePropertyStatistics() offered
		Mockito.verify(mockService, Mockito.times(3)).savePropertyStatistics(
				(NewPropertyStatistics) Mockito.any(), (Node) Mockito.any());

		boolean hasStatisticsRelationships = referenceNode.hasRelationship(
				StatisticsRelationships.STATISTICS, Direction.OUTGOING);
		Assert.assertFalse("not delete StatisticsRelationships.STATISTICS",
				hasStatisticsRelationships);
	}
	
	/**
	 * testing method deleteVault(Node rootNode) when parameter
	 * rootNode == null
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 */
	@Test(expected = InvalidStatisticsParameterException.class)
	public void deleteVaultNullParameterRootNodeNegativeTest()
			throws DatabaseException, InvalidStatisticsParameterException,
			DuplicateStatisticsException {
		LOGGER.debug("start deleteVaultNullParameterRootNodeNegativeTest()");
		StatisticsVault propVault = new StatisticsVault(PROPERTIES);
		service.saveVault(referenceNode, propVault);
		service.deleteVault(null);
		LOGGER.debug("finish deleteVaultNullParameterRootNodeNegativeTest()");
	}

	/**
	 * testing method loadVault(Node rootNode)
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 * @throws LoadVaultException
	 * @throws InvalidPropertyStatisticsNodeException
	 */
	@Test
	public void loadVaultPositiveTest() throws DatabaseException,
			InvalidStatisticsParameterException, DuplicateStatisticsException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, LoadVaultException,
			InvalidPropertyStatisticsNodeException {
		LOGGER.debug("start loadVaultPositiveTest()");
		StatisticsVault propVault = new StatisticsVault(PROPERTIES);
		StatisticsVault neighboursSubVault = new StatisticsVault(NEIGHBOURS);
		StatisticsVault networkSubVault = new StatisticsVault(NETWORK);

		propVault.addSubVault(neighboursSubVault);
		neighboursSubVault.addSubVault(networkSubVault);

		// create PropertyStatistics
		NewPropertyStatistics propStat = new NewPropertyStatistics("Counter",
				Integer.class);
		propStat.updatePropertyMap(1, 1);
		propStat.updatePropertyMap(2, 1);
		propStat.updatePropertyMap(1, 1);
		// add PropertyStatistics to propertyVault
		propVault.addPropertyStatistics(propStat);

		service.saveVault(referenceNode, propVault);
		// create spy object
		NewStatisticsService mockService = Mockito.spy(service);

		IVault vault = mockService.loadVault(referenceNode);
		// check whether the method loadPropertyStatistics(Node
		// propertyStatisticsNode) offered
		Mockito.verify(mockService, Mockito.times(1)).loadPropertyStatistics(
				(Node) Mockito.any());

		checkVault(vault, StatisticsVault.class, 0, PROPERTIES, 1);
		IVault subVault = vault.getSubVaults().get(NEIGHBOURS);
		checkVault(subVault, StatisticsVault.class, 0, NEIGHBOURS, 1);
		subVault = subVault.getSubVaults().get(NETWORK);
		checkVault(subVault, StatisticsVault.class, 0, NETWORK, 0);
		LOGGER.debug("finish loadVaultPositiveTest()");

	}

	/**
	 * testing method loadVault(Node rootNode) when parameter rootNode = null
	 * 
	 * @throws InvalidStatisticsParameterException
	 * @throws LoadVaultException
	 */
	@Test(expected = InvalidStatisticsParameterException.class)
	public void loadVaultNullParameterNegativeTest()
			throws InvalidStatisticsParameterException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, LoadVaultException {
		LOGGER.debug("start loadVaultNullParameterNegativeTest()");
		service.loadVault(null);
		LOGGER.debug("finish loadVaultNullParameterNegativeTest()");

	}

	/**
	 * testing method loadVault(Node rootNode) when rootNode has not statistics
	 * vault
	 * 
	 * @throws InvalidStatisticsParameterException
	 * @throws LoadVaultException
	 */
	@Test
	public void loadVaultEmptyPositiveTest()
			throws InvalidStatisticsParameterException, LoadVaultException {
		LOGGER.debug("start loadVaultEmptyPositiveTest()");
		IVault vault = service.loadVault(referenceNode);
		checkVault(vault, StatisticsVault.class, 0, "", 0);
		LOGGER.debug("finish loadVaultEmptyPositiveTest()");
	}

	/**
	 * testing method loadVault(Node rootNode) when vaultNode has wrong CLASS
	 * property
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws DuplicateStatisticsException
	 * @throws LoadVaultException
	 */
	@Test(expected = LoadVaultException.class)
	public void loadVaultExceptionNegativeTest() throws DatabaseException,
			InvalidStatisticsParameterException, DuplicateStatisticsException,
			LoadVaultException {
		LOGGER.debug("start loadVaultExceptionNegativeTest()");
		StatisticsVault propVault = new StatisticsVault(PROPERTIES);
		service.saveVault(referenceNode, propVault);
		Node vaultNode = referenceNode.getSingleRelationship(
				StatisticsRelationships.STATISTICS, Direction.OUTGOING)
				.getEndNode();
		tx = graphDatabaseService.beginTx();
		try {
			vaultNode.setProperty(NewStatisticsService.CLASS, "wrong_class");
			tx.success();
		} finally {
			tx.finish();
		}
		service.loadVault(referenceNode);
		LOGGER.debug("finish loadVaultExceptionNegativeTest()");
	}

	/**
	 * testing method savePropertyStatistics(NewPropertryStatistics propStat,
	 * Node vaultNode)
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 */
	@Test
	public void savePropertyStatisticsPositiveTest() throws DatabaseException,
			InvalidStatisticsParameterException {
		LOGGER.debug("start savePropertyStatisticsPositiveTest()");
		NewPropertyStatistics propStat = new NewPropertyStatistics("Counter",
				Integer.class);
		propStat.updatePropertyMap(1, 1);
		propStat.updatePropertyMap(2, 1);
		propStat.updatePropertyMap(1, 1);

		service.savePropertyStatistics(propStat, referenceNode);

		Node propStatNode = referenceNode.getSingleRelationship(
				DatasetRelationTypes.CHILD, Direction.OUTGOING).getEndNode();

		boolean hasChildRelationship = propStatNode.hasRelationship(
				DatasetRelationTypes.CHILD, Direction.INCOMING);
		Assert.assertTrue("not create StatisticsRelationships.CHILD",
				hasChildRelationship);

		String name = (String) propStatNode.getProperty(
				NewAbstractService.NAME, "");
		Assert.assertEquals("propertyStatistics node has wrong name",
				"Counter", name);

		int number = (Integer) propStatNode.getProperty(
				NewStatisticsService.NUMBER, null);
		Assert.assertEquals("propertyStatistics node has wrong number", 2,
				number);

		int v1 = (Integer) propStatNode.getProperty("v1", null);
		int v2 = (Integer) propStatNode.getProperty("v2", null);
		int c1 = (Integer) propStatNode.getProperty("c1", null);
		int c2 = (Integer) propStatNode.getProperty("c2", null);
		Assert.assertEquals("propertyStatistics node has wrong v1", 1, v1);
		Assert.assertEquals("propertyStatistics node has wrong v2", 2, v2);
		Assert.assertEquals("propertyStatistics node has wrong c1", 2, c1);
		Assert.assertEquals("propertyStatistics node has wrong c2", 1, c2);

		String className = (String) propStatNode.getProperty(
				NewStatisticsService.CLASS, "");
		Assert.assertEquals("propertyStatistics node has wrong className",
				Integer.class.getCanonicalName(), className);
		LOGGER.debug("finish savePropertyStatisticsPositiveTest()");
	}

	/**
	 * testing method savePropertyStatistics(NewPropertryStatistics propStat,
	 * Node vaultNode) when parameter propStat == null
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 */
	@Test(expected = InvalidStatisticsParameterException.class)
	public void savePropertyStatisticsNullParameterPropStatNegativeTest()
			throws DatabaseException, InvalidStatisticsParameterException {
		service.savePropertyStatistics(null, referenceNode);
	}

	/**
	 * testing method savePropertyStatistics(NewPropertryStatistics propStat,
	 * Node vaultNode) when parameter vaultNode == null
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 */
	@Test(expected = InvalidStatisticsParameterException.class)
	public void savePropertyStatisticsNullParameterVaultNodeNegativeTest()
			throws DatabaseException, InvalidStatisticsParameterException {
		service.savePropertyStatistics(new NewPropertyStatistics("name",
				String.class), null);
	}

	/**
	 * testing method loadPropertyStatistics(Node propertyStatisticsNode)
	 * 
	 * @throws DatabaseException
	 * @throws InvalidStatisticsParameterException
	 * @throws InvalidPropertyStatisticsNodeException
	 * @throws LoadVaultException
	 */
	@Test
	public void loadPropertyStatisticsPositiveTest() throws DatabaseException,
			InvalidStatisticsParameterException,
			InvalidPropertyStatisticsNodeException, LoadVaultException {
		NewPropertyStatistics propStat = new NewPropertyStatistics("Counter",
				Integer.class);
		propStat.updatePropertyMap(1, 1);
		propStat.updatePropertyMap(2, 1);
		propStat.updatePropertyMap(1, 1);
		service.savePropertyStatistics(propStat, referenceNode);
		Node propertyStatisticsNode = service
				.getPropertyStatisticsNodes(referenceNode).iterator().next();
		NewPropertyStatistics actualPropStat = service
				.loadPropertyStatistics(propertyStatisticsNode);

		String actualName = actualPropStat.getName();
		Class<?> actualClass = actualPropStat.getKlass();
		Map<Object, Integer> actualPropertyMap = actualPropStat
				.getPropertyMap();

		Assert.assertEquals("property map has wrong name", "Counter",
				actualName);
		Assert.assertEquals("property map has wrong className", Integer.class,
				actualClass);
		Assert.assertTrue(
				"property map has not contain expected value",
				actualPropertyMap.containsKey(1)
						&& actualPropertyMap.containsValue(2));
		Assert.assertTrue(
				"property map has not contain expected value",
				actualPropertyMap.containsKey(2)
						&& actualPropertyMap.containsValue(1));
		Assert.assertEquals("property map has wrong size", 2,
				actualPropertyMap.size());
	}

	/**
	 * testing method loadPropertyStatistics(Node propertyStatisticsNode) when
	 * propertyStatisticsNode has not className property
	 * 
	 * @throws InvalidPropertyStatisticsNodeException
	 * @throws LoadVaultException
	 * @throws InvalidStatisticsParameterException
	 */
	@Test(expected = InvalidPropertyStatisticsNodeException.class)
	public void loadPropetyStatisticsNotClassPropertyNegativeTest()
			throws InvalidPropertyStatisticsNodeException, LoadVaultException,
			InvalidStatisticsParameterException {
		tx = graphDatabaseService.beginTx();
		try {
			Node invalidPropStatNode = graphDatabaseService.createNode();
			invalidPropStatNode.setProperty(NewStatisticsService.NAME, "name");
			service.loadPropertyStatistics(invalidPropStatNode);

			tx.success();
		} finally {
			tx.finish();
		}
	}

	/**
	 * testing method loadPropertyStatistics(Node propertyStatisticsNode) when
	 * propertyStatisticsNode has not name property
	 * 
	 * @throws InvalidPropertyStatisticsNodeException
	 * @throws LoadVaultException
	 * @throws InvalidStatisticsParameterException
	 */
	@Test(expected = InvalidPropertyStatisticsNodeException.class)
	public void loadPropetyStatisticsNotNamePropertyNegativeTest()
			throws InvalidPropertyStatisticsNodeException, LoadVaultException,
			InvalidStatisticsParameterException {
		tx = graphDatabaseService.beginTx();
		try {
			Node invalidPropStatNode = graphDatabaseService.createNode();
			invalidPropStatNode.setProperty(NewStatisticsService.CLASS,
					Integer.class.getCanonicalName());
			service.loadPropertyStatistics(invalidPropStatNode);

			tx.success();
		} finally {
			tx.finish();
		}
	}

	/**
	 * testing method loadPropertyStatistics(Node propertyStatisticsNode) when
	 * propertyStatisticsNode has empty name property
	 * 
	 * @throws InvalidPropertyStatisticsNodeException
	 * @throws LoadVaultException
	 * @throws InvalidStatisticsParameterException
	 */
	@Test(expected = InvalidPropertyStatisticsNodeException.class)
	public void loadPropetyStatisticsEmptyNamePropertyNegativeTest()
			throws InvalidPropertyStatisticsNodeException, LoadVaultException,
			InvalidStatisticsParameterException {
		tx = graphDatabaseService.beginTx();
		try {
			Node invalidPropStatNode = graphDatabaseService.createNode();
			invalidPropStatNode.setProperty(NewStatisticsService.CLASS,
					Integer.class.getCanonicalName());
			invalidPropStatNode.setProperty(NewStatisticsService.NAME, "");
			service.loadPropertyStatistics(invalidPropStatNode);

			tx.success();
		} finally {
			tx.finish();
		}
	}

	/**
	 * testing method loadPropertyStatistics(Node propertyStatisticsNode) when
	 * parameter propertyStatisticsNode is null
	 * 
	 * @throws InvalidPropertyStatisticsNodeException
	 * @throws LoadVaultException
	 * @throws InvalidStatisticsParameterException
	 */
	@Test(expected = InvalidStatisticsParameterException.class)
	public void loadPropetyStatisticsNullParameterNegativeTest()
			throws InvalidPropertyStatisticsNodeException, LoadVaultException,
			InvalidStatisticsParameterException {
		service.loadPropertyStatistics(null);
	}
}
