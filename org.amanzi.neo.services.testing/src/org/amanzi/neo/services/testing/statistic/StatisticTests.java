package org.amanzi.neo.services.testing.statistic;

//import org.amanzi.awe.afp.services.AfpService;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.db.manager.DatabaseManager.DatabaseAccessType;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.parser.CSVParser;
import org.amanzi.neo.loader.core.saver.impl.NetworkSaver;
import org.amanzi.neo.loader.ui.loaders.Loader;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
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
import org.amanzi.neo.services.statistic.internal.StatisticHandler;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class StatisticTests extends AbstractAWETest {

	private static Logger LOGGER = Logger.getLogger(DistributionTests.class);

	private static long startTimestamp;

	private static DatasetService datasetService;
	private static CommonConfigData config;
	private static ILoader<?, CommonConfigData> loader;
	private static Node rootNode;
	private static String rootKey = "";
	
	// private static AfpService afpService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		startTimestamp = System.currentTimeMillis();
		LOGGER.info("Set up Distribution Test");
		long before = 0;
		long after = 0;
		clearDb();
		initializeDb();
		initPreferences();
		rootKey = "rootKey";

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		// clearDb();

		long duration = System.currentTimeMillis() - startTimestamp;
		int milliseconds = (int) (duration % 1000);
		int seconds = (int) (duration / 1000 % 60);
		int minutes = (int) (duration / 1000 / 60 % 60);
		int hours = (int) (duration / 1000 / 60 / 60 % 24);
		LOGGER.info("Test finished. Test time - " + hours + " hours " + minutes
				+ " minutes " + seconds + " seconds " + milliseconds
				+ " milliseconds");
	}
	@Test
	public void clearStatisticTest(){
		StatisticHandler statisticHandler = new StatisticHandler();
		
	}
	/**
	 * Creation of rootNode
	 */
	@Test
	public void createRootTest() {
		LOGGER.info("<  Creation of rootNode begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			Node rootNode = graphDatabaseService.createNode();
			rootNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "root");
			NodeTypes.getEnumById(NodeTypes.NETWORK.getId()).setNodeType(
					rootNode, graphDatabaseService);

			// IRange strRange = new StringRange(rootNode.getProperty(
			// INeoConstants.PROPERTY_NAME_NAME).toString());
			Assert.assertTrue("Node doesn't equals ", graphDatabaseService
					.getNodeById(1).equals(rootNode));

			rootNode.delete();
			tx.success();

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception  " + e.getMessage());

		} finally {
			tx.finish();
			LOGGER.info("<  Creation of rootNode end >");
		}

	}

	/**
	 * Test statistic creation
	 */
	@Test
	public void creationOfStatisticTest() {
		LOGGER.info("<  Test statistic creation begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			Node rootNode = graphDatabaseService.createNode();
			rootNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "rootNODE");
			NodeTypes.getEnumById(NodeTypes.NETWORK.getId()).setNodeType(
					rootNode, graphDatabaseService);

			// childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "");
			// IRange strRange = new StringRange(rootNode.getProperty(
			// INeoConstants.PROPERTY_NAME_NAME).toString());
			IStatistic stat = StatisticManager.getStatistic(rootNode);

			stat.indexValue(rootKey, NodeTypes.NETWORK.toString(),
					"String value", NodeTypes.NETWORK.toString());
			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(rootKey, NodeTypes.NETWORK.toString(),
					"String value", NodeTypes.NETWORK.toString());

			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(rootKey, NodeTypes.NETWORK.toString(),
					"Double value", 1);
			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(rootKey, NodeTypes.NETWORK.toString(),
					"Double value", 2);
			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(rootKey, NodeTypes.NETWORK.toString(),
					"Double value", 3);
			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.save();

			Assert.assertTrue(
					"Lost values ",
					stat.getTotalCount(rootKey, NodeTypes.NETWORK.toString()) == 5);
			tx.success();

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
	
			Node rootNode = graphDatabaseService.createNode();
			rootNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "rootNODE");
			NodeTypes.getEnumById(NodeTypes.NETWORK.getId()).setNodeType(
					rootNode, graphDatabaseService);
			
			Node childNode=graphDatabaseService.createNode();
			childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "");
			NodeTypes.CITY.setNodeType(childNode, graphDatabaseService);
			 childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "child node name");
			IStatistic stat = StatisticManager.getStatistic(rootNode);
			
			rootNode.createRelationshipTo(childNode, NetworkRelationshipTypes.CHILD);
			stat.indexValue(rootKey, NodeTypes.CITY.toString(),
					"String value", NodeTypes.CITY.toString());
			stat.updateTypeCount(rootKey, NodeTypes.CITY.toString(), 1);
			stat.indexValue(rootKey, NodeTypes.CITY.toString(),
					"String value", NodeTypes.CITY.toString());
			
			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(rootKey, NodeTypes.NETWORK.toString(),
					"Double value", 1);
			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(rootKey, NodeTypes.NETWORK.toString(),
					"Double value", 2);
			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.indexValue(rootKey, NodeTypes.NETWORK.toString(),
					"Double value", 3);
			stat.updateTypeCount(rootKey, NodeTypes.NETWORK.toString(), 1);
			stat.save();
		
//			IDistributionalModel networkModel=new NetworkModel(rootNode);
//			IDistributionModel stringDistrib=networkModel.getModel(INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY);
//			
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
