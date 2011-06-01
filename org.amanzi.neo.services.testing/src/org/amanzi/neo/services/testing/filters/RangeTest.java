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
import org.amanzi.neo.services.networkModel.IRange;
import org.amanzi.neo.services.networkModel.NumberRange;
import org.amanzi.neo.services.networkModel.StringRange;
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

public class RangeTest extends AbstractAWETest {

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
	 * Check corect String Range
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

			IRange strRange = new StringRange(checkNode.getProperty(
					INeoConstants.PROPERTY_NAME_NAME).toString());
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
	 * Check corect Number Range
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

	

}
