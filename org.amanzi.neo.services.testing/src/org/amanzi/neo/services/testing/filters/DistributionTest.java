package org.amanzi.neo.services.testing.filters;

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

public class DistributionTest extends AbstractAWETest {

	private static Logger LOGGER = Logger.getLogger(DistributionTest.class);

	private static long startTimestamp;

	private static DatasetService datasetService;
	private static CommonConfigData config;
	private static ILoader<?, CommonConfigData> loader;
	private static Node rootNode;

	// private static AfpService afpService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		startTimestamp = System.currentTimeMillis();
		LOGGER.info("Set up Distribution Test");
		long before = 0;
		long after = 0;
		initializeDb();
		initPreferences();

		Transaction tx = graphDatabaseService.beginTx();
		try {

			config = new CommonConfigData();
			File file = new File("./files/afp_engine/germany/Network");
			config.setRoot(file);
			config.setDbRootName("rootNode");
			config.setProjectName("project");

			loader = getNetworkLoader();
			loader.setup(DatabaseAccessType.EMBEDDED, config);

			LOGGER.info("Loading file <" + file.getName() + "> to dataset <>");
			before = System.currentTimeMillis();
			loader.load();
			after = System.currentTimeMillis();

			datasetService = NeoServiceFactory.getInstance()
					.getDatasetService();
			rootNode = datasetService.findRoot("project", "rootNode");
			// afpService = AfpService.getService();
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * create NetworkModel test
	 */
	@Test
	public void creationOfNetworkModelTest() {
		LOGGER.info("< createNetworkModelTest begin >");
		Transaction tx = graphDatabaseService.beginTx();
		try {

			rootNode = datasetService.findRoot("project", "rootNode");

			IDistributionalModel nm = new NetworkModel(rootNode);

			Assert.assertEquals("Incorrect model name",
					rootNode.getProperty(INeoConstants.PROPERTY_NAME_NAME)
							.toString(), nm.getModelName());

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			tx.finish();
			LOGGER.info("< createNetworkModelTest end >");
		}

	}

	/**
	 * Create Distribution test if Statistic root not Exist
	 */
	@Test
	public void creationOfDistributionNotExistTest() {
		LOGGER.info("< creationOfDistributionNotExistTest begin >");
		Transaction tx = graphDatabaseService.beginTx();
		try {

			IDistributionalModel nm = new NetworkModel(rootNode);
			IDistributionModel sm = nm.getModel(
					INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY);
			LOGGER.info("< StringDistribution root " + sm.getRootNode().getId()
					+ " >");
			LOGGER.info("< RootNode NetworkModel " + rootNode.getId());
			Assert.assertTrue("Different  values excepted",
					!rootNode.equals(sm.getRootNode()));

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			tx.finish();
			LOGGER.info("< creationOfDistributionNotExistTest end >");
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

				if (statisticRoot
						.getProperty(INeoConstants.PROPERTY_NAME_NAME)
						.equals(rootNode
								.getProperty(INeoConstants.PROPERTY_NAME_NAME)
								+ "#"
								+ rootNode
										.getProperty(
												INeoConstants.PROPERTY_TYPE_NAME)
										.toString().toUpperCase())
						&& NodeTypes.STATISTICS_ROOT.checkNode(statisticRoot)) {
					statisticRoot.delete();
					relation.delete();
					LOGGER.info("< find and delete existing node >");
				}
			}

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.finish();
			LOGGER.info("< Prepare test finish >");
		}

	}
	/**
	 * Create Distribution test if Statistic root Exist
	 */
	@Test
	public void creationOfDistributionExistTest() {
		LOGGER.info("< creationOfDistributionExistTest begin >");
		prepareExistTest();
		Transaction tx = graphDatabaseService.beginTx();

		try {

			Node statisticNode = graphDatabaseService.createNode();
			statisticNode.setProperty(
					INeoConstants.PROPERTY_NAME_NAME,
					rootNode.getProperty(INeoConstants.PROPERTY_NAME_NAME)
							+ "#"
							+ rootNode
									.getProperty(
											INeoConstants.PROPERTY_TYPE_NAME)
									.toString().toUpperCase());
			NodeTypes.getEnumById(NodeTypes.STATISTICS_ROOT.getId())
					.setNodeType(statisticNode, graphDatabaseService);
			rootNode.createRelationshipTo(statisticNode,
					NetworkRelationshipTypes.CHILD);
			IDistributionalModel nm = new NetworkModel(rootNode);
			IDistributionModel sm = nm.getModel(
					INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY);

			LOGGER.info("< finded node id " + sm.getRootNode().getId() + " >");
			LOGGER.info("< Statistic Root id " + statisticNode.getId() + " >");

			Assert.assertEquals("Same values excepted", statisticNode,
					sm.getRootNode());
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			tx.finish();
			LOGGER.info("< creationOfDistributionNotExistTest end >");
		}

	}

}
