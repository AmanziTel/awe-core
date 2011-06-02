package org.amanzi.neo.services.testing.statistic;

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
	private static String ROOTKEY = "rootNode";
	private static String ROOT_NODE_NAME="rootNode";


	// private static AfpService afpService;

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
	/**
	 * Creation of rootNode
	 */
	private void rootNodeCreation(){
		rootNode = graphDatabaseService.createNode();
		rootNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, ROOT_NODE_NAME);
		NodeTypes.getEnumById(NodeTypes.NETWORK.getId()).setNodeType(
				rootNode, graphDatabaseService);
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
			checkNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "value 1");
			NodeTypes.getEnumById(NodeTypes.SITE.getId()).setNodeType(
					checkNode, graphDatabaseService);

			IRange strRange = new StringRange("value 1",NodeTypes.SITE);
			Assert.assertTrue(strRange.includes(checkNode));
			checkNode.delete();
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception " + e.getMessage());

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
			checkNode.setProperty(INeoConstants.PROPERTY_NAME_MAX_VALUE, 12.0);
			checkNode.setProperty(INeoConstants.PROPERTY_NAME_MIN_VALUE, 5.0);
			NodeTypes.getEnumById(NodeTypes.SITE.getId()).setNodeType(
					checkNode, graphDatabaseService);

			IRange numRange = new NumberRange(5.0, 13.0);
			Assert.assertTrue(numRange.includes(checkNode));
			checkNode.delete();
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception " + e.getMessage());

		} finally {
			tx.finish();
			LOGGER.info("< Check corect Number Range end >");
		}

	}
	@Test
	public void creationOfStatisticTest() {
		LOGGER.info("<  Test statistic creation begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			rootNodeCreation();

			IStatistic stat = StatisticManager.getStatistic(rootNode);

			stat.indexValue(ROOTKEY, NodeTypes.NETWORK.toString(),
					"String value", NodeTypes.NETWORK.toString());
			stat.updateTypeCount(ROOTKEY, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(ROOTKEY, NodeTypes.NETWORK.toString(),
					"String value", NodeTypes.NETWORK.toString());

			stat.updateTypeCount(ROOTKEY, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(ROOTKEY, NodeTypes.NETWORK.toString(),
					"Double value", 1);
			stat.updateTypeCount(ROOTKEY, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(ROOTKEY, NodeTypes.NETWORK.toString(),
					"Double value", 2);
			stat.updateTypeCount(ROOTKEY, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(ROOTKEY, NodeTypes.NETWORK.toString(),
					"Double value", 3);
			stat.updateTypeCount(ROOTKEY, NodeTypes.NETWORK.toString(), 1);
			stat.save();

			Assert.assertTrue(
					"Lost values ",
					stat.getTotalCount(ROOTKEY, NodeTypes.NETWORK.toString()) == 5);
			tx.failure();

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception  " + e.getMessage());

		} finally {
			tx.finish();
			LOGGER.info("<  Test statistic creation end >");
		}

	}

	/**
	 * Test range distribution
	 */
	@Test
	public void rangeDistributionTest() {
		LOGGER.info("<   Test range distribution begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			rootNodeCreation();
			Node childNode = graphDatabaseService.createNode();
			childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "");
			NodeTypes.CITY.setNodeType(childNode, graphDatabaseService);
			childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME,
					"City 1");
			rootNode.createRelationshipTo(childNode,
					NetworkRelationshipTypes.CHILD);
			
			IStatistic stat = StatisticManager.getStatistic(rootNode);

			stat.indexValue(ROOTKEY, NodeTypes.CITY.getId(),
					INeoConstants.PROPERTY_NAME_NAME, "City 2");
			stat.indexValue(ROOTKEY, NodeTypes.CITY.getId(),
					INeoConstants.PROPERTY_NAME_NAME, "City 1");

			
			stat.indexValue(ROOTKEY,NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 1);
			stat.indexValue(ROOTKEY,NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 2);
			stat.indexValue(ROOTKEY,NodeTypes.CITY.getId(),
					INeoConstants.COUNT_TYPE_NAME, 3);
	
			stat.updateTypeCount(ROOTKEY, NodeTypes.NETWORK.getId(), 5);

			
			stat.save();

			IDistributionalModel networkModel = new NetworkModel(rootNode);
			
			IDistributionModel stringDistrib = networkModel.getModel(
					INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY);
			LOGGER.info("RanageList size "+stringDistrib.getRangeList().size());
			

			Assert.assertEquals("Incorrect expected value",2,stringDistrib.getRangeList().size());
			Assert.assertTrue(stringDistrib.getRangeList().get(0).includes(childNode));
			
			tx.success();

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception  " + e.getMessage());

		} finally {
			tx.finish();
			LOGGER.info("<   Test range distributionend >");
		}

	}
	
}
