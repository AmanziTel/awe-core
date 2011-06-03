package org.amanzi.neo.services.testing.distribution;

//import org.amanzi.awe.afp.services.AfpService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.networkModel.IDistributionModel;
import org.amanzi.neo.services.networkModel.IDistributionalModel;
import org.amanzi.neo.services.networkModel.IRange;
import org.amanzi.neo.services.networkModel.NumberRange;
import org.amanzi.neo.services.networkModel.StringRange;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class RangeTests extends AbstractAWETest {

	private static Logger LOGGER = Logger.getLogger(DistributionTests.class);

	private static long startTimestamp;
	private static Node rootNode;
	private static String ROOT_KEY = "rootNode";
	private static String ROOT_NODE_NAME = "rootNode";
	private static Double NODE_MIN_VALUE = 5.0;
	private static Double NODE_MAX_VALUE = 12.0;
	private static String NODE_NAME_VALUE = "value 1";
	private static String NODE_NAME_CITY1 = "City 1";
	private static String NODE_NAME_CITY2 = "City 2";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		startTimestamp = System.currentTimeMillis();
		LOGGER.info("Set up Distribution Test");

		clearDb();
		initializeDb();
		initPreferences();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		//

		long duration = System.currentTimeMillis() - startTimestamp;
		int milliseconds = (int) (duration % 1000);
		int seconds = (int) (duration / 1000 % 60);
		int minutes = (int) (duration / 1000 / 60 % 60);
		int hours = (int) (duration / 1000 / 60 / 60 % 24);
		LOGGER.info("Test finished. Test time - " + hours + " hours " + minutes
				+ " minutes " + seconds + " seconds " + milliseconds
				+ " milliseconds");
	}

	private void rootNodeCreation() {
		rootNode = graphDatabaseService.createNode();
		rootNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, ROOT_NODE_NAME);
		NodeTypes.getEnumById(NodeTypes.NETWORK.getId()).setNodeType(rootNode,
				graphDatabaseService);
	}

	/**
	 * Check correct String Range
	 */
	@Test
	public void checkCorrectStringRange() {
		LOGGER.info("< Check correct return if property value doesn't String begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			Node checkNode = graphDatabaseService.createNode();
			checkNode.setProperty(INeoConstants.PROPERTY_NAME_NAME,
					NODE_NAME_VALUE);
			NodeTypes.getEnumById(NodeTypes.SITE.getId()).setNodeType(
					checkNode, graphDatabaseService);

			IRange strRange = new StringRange(NODE_NAME_VALUE, NodeTypes.SITE,INeoConstants.PROPERTY_NAME_NAME);
			Assert.assertTrue("Expected correct range or node property ",
					strRange.includes(checkNode));

			tx.failure();
		} catch (Exception e) {
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());

		} finally {
			tx.finish();
			LOGGER.info("< Check correct return if property value doesn't String end >");
		}

	}

	/**
	 * Check correct Number Range
	 */
	@Test
	public void checkCorrectNumberRange() {
		LOGGER.info("< Check corect Number Range begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			Node checkNode = graphDatabaseService.createNode();
			checkNode.setProperty(INeoConstants.PROPERTY_NAME_MAX_VALUE,
					NODE_MAX_VALUE);
			checkNode.setProperty(INeoConstants.PROPERTY_NAME_MIN_VALUE,
					NODE_MIN_VALUE);
			NodeTypes.SITE.setNodeType(checkNode, graphDatabaseService);

			IRange numRange = new NumberRange(5.0, 13.0, NodeTypes.SITE);
			Assert.assertTrue("Expected correct range or node property ",
					numRange.includes(checkNode));

			tx.failure();
		} catch (Exception e) {
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());

		} finally {
			tx.finish();
			LOGGER.info("< Check corect Number Range end >");
		}

	}

	/**
	 * Check incorrect Number Range
	 */
	@Test
	public void checkIncorrectNumberRange() {
		LOGGER.info("< Check Incorrect Number Range begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			Node checkNode = graphDatabaseService.createNode();
			checkNode.setProperty(INeoConstants.PROPERTY_NAME_MAX_VALUE,
					NODE_MAX_VALUE);
			checkNode.setProperty(INeoConstants.PROPERTY_NAME_MIN_VALUE,
					NODE_MIN_VALUE);
			NodeTypes.SITE.setNodeType(checkNode, graphDatabaseService);

			IRange numRange = new NumberRange(7.0, 8.0, NodeTypes.SITE);
			Assert.assertFalse("Expected Incorrect range or node property ",
					numRange.includes(checkNode));

			tx.failure();
		} catch (Exception e) {
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());

		} finally {
			tx.finish();
			LOGGER.info("< Check Incorrect Number Range end >");
		}

	}

	@Test
	public void creationOfStatisticTest() {
		LOGGER.info("<  Test statistic creation begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			rootNodeCreation();

			IStatistic stat = StatisticManager.getStatistic(rootNode);

			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.PROPERTY_NAME_NAME, NODE_NAME_CITY1);
			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.PROPERTY_NAME_NAME, NODE_NAME_CITY2);

			stat.indexValue(ROOT_KEY, NodeTypes.NETWORK.getId(),
					INeoConstants.PROPERTY_NAME_NAME, 1);

			stat.indexValue(ROOT_KEY, NodeTypes.NETWORK.getId(),
					INeoConstants.PROPERTY_NAME_NAME, 2);

			stat.indexValue(ROOT_KEY, NodeTypes.NETWORK.getId(),
					INeoConstants.PROPERTY_NAME_NAME, 3);

			stat.updateTypeCount(ROOT_KEY, NodeTypes.NETWORK.getId(), 3);
			stat.updateTypeCount(ROOT_KEY, NodeTypes.CITY.getId(), 2);
			stat.save();

			Assert.assertTrue(
					"Expected count 5 ",
					stat.getTotalCount(ROOT_KEY, NodeTypes.NETWORK.getId()) == 3
							&& stat.getTotalCount(ROOT_KEY,
									NodeTypes.CITY.getId()) == 2);
			tx.success();

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());

		} finally {
			tx.finish();
			LOGGER.info("<  Test statistic creation end >");
		}

	}

	/**
	 * Test correct String Range distribution
	 */
	@Test
	public void correctStringRangeDistributionTest() {
		LOGGER.info("<   Test correct String range distribution begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			rootNodeCreation();
			Node childNode = graphDatabaseService.createNode();
			childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "");
			NodeTypes.CITY.setNodeType(childNode, graphDatabaseService);
			childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME,
					NODE_NAME_CITY1);
			rootNode.createRelationshipTo(childNode,
					NetworkRelationshipTypes.CHILD);

			IStatistic stat = StatisticManager.getStatistic(rootNode);

			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.PROPERTY_NAME_NAME, NODE_NAME_CITY1);
			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.PROPERTY_NAME_NAME, NODE_NAME_CITY2);

			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 1);
			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 2);
			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 3);

			stat.updateTypeCount(ROOT_KEY, NodeTypes.CITY.getId(), 5);

			stat.save();

			IDistributionalModel networkModel = new NetworkModel(rootNode);

			IDistributionModel stringDistrib = networkModel.getModel(
					INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY);
			LOGGER.info("RanageList size "
					+ stringDistrib.getRangeList().size());

			Assert.assertEquals("Expected RangeList size 2 ", 2, stringDistrib
					.getRangeList().size());
			Assert.assertTrue("Expected correct node value for filter",
					stringDistrib.getRangeList().get(0).includes(childNode));

			tx.failure();

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());
			
		} finally {
			tx.finish();
			LOGGER.info("<   Test correct String range distribution end >");
		}

	}
	
	/**
	 * Test incorrect String Range distribution
	 */
	@Test
	public void incorrectStringRangeDistributionTest() {
		LOGGER.info("<   Test incorrect String range distribution begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			rootNodeCreation();
			Node childNode = graphDatabaseService.createNode();
			childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "");
			NodeTypes.CITY.setNodeType(childNode, graphDatabaseService);
			childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME,
					NODE_NAME_CITY1);
			rootNode.createRelationshipTo(childNode,
					NetworkRelationshipTypes.CHILD);

			IStatistic stat = StatisticManager.getStatistic(rootNode);

			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.PROPERTY_NAME_NAME, NODE_NAME_CITY1);
			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.PROPERTY_NAME_NAME, NODE_NAME_CITY2);

			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 1);
			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 2);
			stat.indexValue(ROOT_KEY, NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 3);

			stat.updateTypeCount(ROOT_KEY, NodeTypes.CITY.getId(), 5);

			stat.save();

			IDistributionalModel networkModel = new NetworkModel(rootNode);

			IDistributionModel stringDistrib = networkModel.getModel(
					INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY);

			Assert.assertEquals("Expected RangeList size 2 ", 2, stringDistrib
					.getRangeList().size());
			Assert.assertFalse("Expected incorrect node value for filter",
					stringDistrib.getRangeList().get(1).includes(childNode));

			tx.failure();

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());

		} finally {
			tx.finish();
			LOGGER.info("<Test incorrect String range distribution end >");
		}

	}

}
