package org.amanzi.neo.services.testing.distribution;

//import org.amanzi.awe.afp.services.AfpService;
import java.io.File;
import java.util.Iterator;

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

public class DistributionTests extends AbstractAWETest {

	private static Logger LOGGER = Logger.getLogger(DistributionTests.class);

	private static long startTimestamp;

	private static DatasetService datasetService;
	private static CommonConfigData config;
	private static ILoader<?, CommonConfigData> loader;
	private static Node rootNode;
	private static String ROOT_KEY = "rootNode";
	private static String ROOT_NODE_NAME="rootNode";
	private static String PROJECT_NAME="project";
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

		Transaction tx = graphDatabaseService.beginTx();
		try {

			config = new CommonConfigData();
			File file = new File("./files/afp_engine/germany/Network");
			config.setRoot(file);
			config.setDbRootName(ROOT_KEY);
			config.setProjectName(PROJECT_NAME);

			loader = getNetworkLoader();
			loader.setup(DatabaseAccessType.EMBEDDED, config);

			LOGGER.info("Loading file <" + file.getName() + "> to dataset <>");
			before = System.currentTimeMillis();
			loader.load();
			after = System.currentTimeMillis();

			datasetService = NeoServiceFactory.getInstance()
					.getDatasetService();
			rootNode = datasetService.findRoot(PROJECT_NAME, ROOT_NODE_NAME);

			tx.success();
		} catch (Exception e) {
 
		} finally {
			tx.finish();
			LOGGER.info("Loading finished in " + (after - before)
					+ " milliseconds");
		}
	}

	private static ILoader<?, CommonConfigData> getNetworkLoader() {
		Loader<BaseTransferData, CommonConfigData> loader = new Loader<BaseTransferData, CommonConfigData>();

		loader.setParser(new CSVParser());
		loader.setSaver(new NetworkSaver());

		return loader;
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
	 * create NetworkModel test
	 */
	@Test
	public void creationOfNetworkModelTest() {
		LOGGER.info("< create NetworkModel test begin >");
		Transaction tx = graphDatabaseService.beginTx();
		try {

			rootNode = datasetService.findRoot(PROJECT_NAME, ROOT_NODE_NAME);

			IDistributionalModel nm = new NetworkModel(rootNode);

			Assert.assertEquals("Incorrect model name",
					rootNode.getProperty(INeoConstants.PROPERTY_NAME_NAME)
							.toString(), nm.getName());

			tx.success();
		} catch (Exception e) {

			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());

		} finally {
			tx.finish();
			LOGGER.info("< create NetworkModel test end >");
		}

	}

	/**
	 *  Distribution test if Statistic root not Exist
	 */
	@Test
	public void creationOfDistributionNotExistTest() {
		LOGGER.info("< Distribution test if Statistic root not Exist begin >");
		Transaction tx = graphDatabaseService.beginTx();
		try {

			IDistributionalModel nm = new NetworkModel(rootNode);
			IDistributionModel sm = nm.getModel(
					INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY);

			LOGGER.info("< finded node id " + sm.getRootNode().getId() + " >");
			LOGGER.info("< Statistic Root id " + sm.getRootNode().getId()
					+ " >");
			Assert.assertTrue("Different  values expected",
					!rootNode.equals(sm.getRootNode()));

			tx.success();
		} catch (Exception e) {
 			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());
		} finally {
			tx.finish();
			LOGGER.info("< Distribution test if Statistic root not Exist end >");
		}

	}

	public void prepareExistTest() {

		LOGGER.info("< Prepare test begin >");
		Transaction tx = graphDatabaseService.beginTx();
		try {

			Iterable<Relationship> iter = rootNode.getRelationships(
					NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
			Iterator<Relationship> iterR = iter.iterator();
			Node statisticRoot;
			Relationship relation;
			while (iterR.hasNext()) {
				relation = iterR.next();
				statisticRoot = relation.getEndNode();

				if (statisticRoot.getProperty(INeoConstants.PROPERTY_NAME_NAME)
						.equals(rootNode
								.getProperty(INeoConstants.PROPERTY_NAME_NAME))
						&& NodeTypes.STATISTICS_ROOT.checkNode(statisticRoot)) {
					statisticRoot.delete();
					relation.delete();
					LOGGER.info("< find and delete existing node >");
				}
			}

			tx.success();
		} catch (Exception e) {
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());
		} finally {
			tx.finish();
			LOGGER.info("< Prepare test finish >");
		}

	}

	/**
	 * Distribution test if Statistic root Exist
	 */
	@Test
	public void creationOfDistributionExistTest() {
		LOGGER.info("< Distribution test if Statistic root Exist begin >");
		prepareExistTest();
		Transaction tx = graphDatabaseService.beginTx();

		try {

			Node statisticNode = graphDatabaseService.createNode();
			statisticNode.setProperty(INeoConstants.PROPERTY_NAME_NAME,
					rootNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
			NodeTypes.getEnumById(NodeTypes.STATISTICS_ROOT.getId())
					.setNodeType(statisticNode, graphDatabaseService);
			rootNode.createRelationshipTo(statisticNode,
					NetworkRelationshipTypes.CHILD);

			IDistributionalModel nm = new NetworkModel(rootNode);
			IDistributionModel sm = nm.getModel(
					INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY);

			LOGGER.info("< finded node id " + sm.getRootNode().getId() + " >");
			LOGGER.info("< Statistic Root id " + statisticNode.getId() + " >");

			Assert.assertEquals("Same values expected", statisticNode,
					sm.getRootNode());

			tx.success();
		} catch (Exception e) {
 
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());

		} finally {
			tx.finish();
			LOGGER.info("< Distribution test if Statistic root Exist end >");
		}

	}

	/**
	 * Check correct return if property value doesn't String
	 */
	@Test
	public void checkStringPropertyValue() {
		LOGGER.info("< Check correct return if property value doesn't String begin >");

		Transaction tx = graphDatabaseService.beginTx();

		try {

			IDistributionalModel nm = new NetworkModel(rootNode);
			IDistributionModel sm = nm.getModel(
					INeoConstants.PROPERTY_LON_NAME, NodeTypes.SITE);

			Assert.assertNull("Null value expected", sm);

			tx.success();
		} catch (Exception e) {
 
			LOGGER.info("Exception " + e.getLocalizedMessage());
			Assert.fail("Exception " + e.getLocalizedMessage());

		} finally {
			tx.finish();
			LOGGER.info("< Check correct return if property value doesn't String end >");
		}

	}



	

}
