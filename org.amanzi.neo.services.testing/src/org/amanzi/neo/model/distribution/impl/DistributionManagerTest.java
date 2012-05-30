/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.neo.model.distribution.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistribution.ChartType;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.impl.DistributionManager.DistributionManagerException;
import org.amanzi.neo.model.distribution.types.impl.EnumeratedDistribution;
import org.amanzi.neo.model.distribution.types.impl.NumberDistribution;
import org.amanzi.neo.model.distribution.types.impl.NumberDistributionType;
import org.amanzi.neo.model.distribution.xml.DistributionXmlParser;
import org.amanzi.neo.model.distribution.xml.DistributionXmlParsingException;
import org.amanzi.neo.model.distribution.xml.schema.Bar;
import org.amanzi.neo.model.distribution.xml.schema.Bars;
import org.amanzi.neo.model.distribution.xml.schema.Data;
import org.amanzi.neo.model.distribution.xml.schema.Distribution;
import org.amanzi.neo.model.distribution.xml.schema.Filter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.DistributionService;
import org.amanzi.neo.services.DistributionService.DistributionNodeTypes;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Plugin;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Tests on Distribution Manager
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class DistributionManagerTest extends AbstractNeoServiceTest {

	private static final INodeType DEFAULT_NODE_TYPE = DistributionNodeTypes.AGGREGATION_BAR;

	private static final String DEFAULT_PROPERTY_NAME = "default_property_name";

	private static final int STRING_DISTRIBUTIONS_NUMBER = 1;

	private static final String DEFAULT_MODEL_NAME = "model_name";

	private static DistributionManager manager;

	private static Node rootNode;

	private static final ChartType[] NON_NUMBERIC_CHARTS = new ChartType[] {
			ChartType.COUNTS, ChartType.LOGARITHMIC, ChartType.PERCENTS };

	private static final ChartType[] NUMBERIC_CHARTS = new ChartType[] {
			ChartType.COUNTS, ChartType.LOGARITHMIC, ChartType.PERCENTS,
			ChartType.CDF };

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();
		new LogStarter().earlyStartup();
		rootNode = graphDatabaseService.getReferenceNode();
		manager = DistributionManager.getManager();
		// used enums registering
		DistributionService.DistributionNodeTypes.values();
		DatasetTypes.values();
		NetworkElementNodeType.values();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		clearDb();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		cleanUpReferenceNode();
	}

	@Test(expected = DistributionManagerException.class)
	public void tryToCreateCDFChartForString() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);

		manager.getDistributions(model, DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME, ChartType.CDF);
	}

	@Test(expected = DistributionManagerException.class)
	public void tryToCreateCDFChartForBoolean() throws Exception {
		IDistributionalModel model = getDistributionalModel(Boolean.class);

		manager.getDistributions(model, DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME, ChartType.CDF);
	}

	@Test
	public void checkSizeOfDistributionsForString() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);

		List<IDistribution<?>> result = manager.getDistributions(model,
				DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
				ChartType.getDefault(), false);

		assertEquals(
				"Unexpected size of Distributions created for String type",
				STRING_DISTRIBUTIONS_NUMBER, result.size());
	}

	@Test
	public void checkStringDistributionsSize() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);

		List<IDistribution<?>> result = manager.getDistributions(model,
				DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
				ChartType.getDefault(), false);

		IDistribution<?> stringDistribution = result.get(0);

		assertEquals("Unexpected type of Distribution for String type",
				EnumeratedDistribution.class, stringDistribution.getClass());
	}

	@Test
	public void checkCacheForStringDistribution() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);

		List<IDistribution<?>> result = manager.getDistributions(model,
				DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
				ChartType.getDefault(), false);
		IDistribution<?> firstDistribution = result.get(0);

		result = manager.getDistributions(model, DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME, ChartType.getDefault(), false);
		IDistribution<?> secondDistribution = result.get(0);

		assertSame("Distributions should be same", firstDistribution,
				secondDistribution);
	}

	@Test
	public void checkSizeOfDistributionsForNumber() throws Exception {
		IDistributionalModel model = getDistributionalModel(Number.class);

		List<IDistribution<?>> result = manager.getDistributions(model,
				DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
				ChartType.getDefault(), false);

		// NumberDistributionType.values().length + 1, because must include one
		// String Distribution
		assertEquals(
				"Unexpected size of Distributions created for Number type",
				NumberDistributionType.values().length + 1, result.size());
	}

	@Test
	public void checkNumberDistributionsType() throws Exception {
		IDistributionalModel model = getDistributionalModel(Number.class);

		List<IDistribution<?>> result = manager.getDistributions(model,
				DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
				ChartType.getDefault(), false);

		int cntEnumDistr = 0;
		for (IDistribution<?> distribution : result) {
			if (distribution instanceof EnumeratedDistribution) {
				cntEnumDistr++;
			} else {
				assertEquals("Unexpected type of Distribution for Number type",
						NumberDistribution.class, distribution.getClass());
			}
		}
		assertTrue("Unexpected type of Distribution for Number type",
				cntEnumDistr == 1);
	}

	@Test
	public void checkCacheForNumberDistribution() throws Exception {
		IDistributionalModel model = getDistributionalModel(Number.class);

		List<IDistribution<?>> result = manager.getDistributions(model,
				DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
				ChartType.getDefault(), false);
		IDistribution<?> firstDistribution = result.get(0);

		result = manager.getDistributions(model, DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME, ChartType.getDefault(), false);
		IDistribution<?> secondDistribution = result.get(0);

		assertSame("Distributions should be same", firstDistribution,
				secondDistribution);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetDistributionWithoutModel() throws Exception {
		manager.getDistributions(null, DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME, ChartType.getDefault());
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetDistributionWithoutNodeType() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);

		manager.getDistributions(model, null, DEFAULT_PROPERTY_NAME,
				ChartType.getDefault());
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetDistributionWithoutPropertyName() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);

		manager.getDistributions(model, DEFAULT_NODE_TYPE, null,
				ChartType.getDefault());
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetDistributionWithEmptyPropertyName() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);

		manager.getDistributions(model, DEFAULT_NODE_TYPE, StringUtils.EMPTY,
				ChartType.getDefault());
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetDistributionWithoutChartType() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);

		manager.getDistributions(model, DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetChartsWithoutModel() throws Exception {
		manager.getPossibleChartTypes(null, DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetChartsWithoutNodeType() throws Exception {
		manager.getPossibleChartTypes(getDistributionalModel(String.class),
				null, DEFAULT_PROPERTY_NAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetChartsWithoutPropertyName() throws Exception {
		manager.getPossibleChartTypes(getDistributionalModel(String.class),
				DEFAULT_NODE_TYPE, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tryToGetChartsWithEmptyPropertyName() throws Exception {
		manager.getPossibleChartTypes(getDistributionalModel(String.class),
				DEFAULT_NODE_TYPE, StringUtils.EMPTY);
	}

	@Test
	public void checkChartsForString() throws Exception {
		ChartType[] chartType = manager.getPossibleChartTypes(
				getDistributionalModel(String.class), DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME);

		for (ChartType nonNumericChart : NON_NUMBERIC_CHARTS) {
			assertTrue("chart for string didn't contains <" + nonNumericChart
					+ ">", ArrayUtils.contains(chartType, nonNumericChart));

			chartType = (ChartType[]) ArrayUtils.removeElement(chartType,
					nonNumericChart);
		}

		assertTrue("array of chart types should be empty",
				chartType.length == 0);
	}

	@Test
	public void checkChartsForBoolean() throws Exception {
		ChartType[] chartType = manager.getPossibleChartTypes(
				getDistributionalModel(Boolean.class), DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME);

		for (ChartType nonNumericChart : NON_NUMBERIC_CHARTS) {
			assertTrue("chart for boolean didn't contains <" + nonNumericChart
					+ ">", ArrayUtils.contains(chartType, nonNumericChart));

			chartType = (ChartType[]) ArrayUtils.removeElement(chartType,
					nonNumericChart);
		}

		assertTrue("array of chart types should be empty",
				chartType.length == 0);
	}

	@Test
	public void checkChartsForLong() throws Exception {
		ChartType[] chartType = manager.getPossibleChartTypes(
				getDistributionalModel(Long.class), DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME);

		for (ChartType numericChart : NUMBERIC_CHARTS) {
			assertTrue("chart for long didn't contains <" + numericChart + ">",
					ArrayUtils.contains(chartType, numericChart));

			chartType = (ChartType[]) ArrayUtils.removeElement(chartType,
					numericChart);
		}

		assertTrue("array of chart types should be empty",
				chartType.length == 0);
	}

	@Test
	public void checkChartsForDouble() throws Exception {
		ChartType[] chartType = manager.getPossibleChartTypes(
				getDistributionalModel(Double.class), DEFAULT_NODE_TYPE,
				DEFAULT_PROPERTY_NAME);

		for (ChartType numericChart : NUMBERIC_CHARTS) {
			assertTrue("chart for double didn't contains <" + numericChart
					+ ">", ArrayUtils.contains(chartType, numericChart));

			chartType = (ChartType[]) ArrayUtils.removeElement(chartType,
					numericChart);
		}

		assertTrue("array of chart types should be empty",
				chartType.length == 0);
	}

	@Test
	public void checkGetCurrent() throws DatabaseException {
		IDistributionalModel model = getDistributionalModel(Node.class);
		DistributionService service = mock(DistributionService.class);
		Node node = getNode();
		manager.distributionService = service;
		when(service.getCurrentDistribution(model.getRootNode())).thenReturn(
				node);
		Assert.assertNotNull(manager.getCurrent(model));

		when(service.getCurrentDistribution(model.getRootNode())).thenReturn(
				null);

		Assert.assertNull(manager.getCurrent(model));
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkGetCurrentWithNullModel() {
		manager.getCurrent(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkGetCurrentWithNullRootNode() {
		IDistributionalModel model = getDistributionalModel(String.class);
		when(model.getRootNode()).thenReturn(null);
		manager.getCurrent(model);
	}

	@Test
	public void checkGetCurrentCatchNullReturning() throws DatabaseException {
		IDistributionalModel model = getDistributionalModel(String.class);
		when(
				manager.distributionService.getCurrentDistribution(model
						.getRootNode())).thenThrow(new DatabaseException(""));
		Assert.assertNull(manager.getCurrent(model));
	}

	@Test
	public void checkGetAllDistributionModels() {
		IDistributionalModel model = getDistributionalModel(Node.class);
		DistributionService service = mock(DistributionService.class);
		Node node = getNode();
		manager.distributionService = service;
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(node);

		when(service.findRootAggregationNodes(model.getRootNode())).thenReturn(
				nodes);
		try {
			Assert.assertTrue(!manager.getAllDistributionModels(model)
					.isEmpty());
		} catch (AWEException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void checkGetDistributionsWithIncludeUserDefined() throws Exception {
		IDistributionalModel model = getDistributionalModel(String.class);
		ReuseTestPlugin plugin = new ReuseTestPlugin();
		// for final plugin.getBundle()
		Bundle bundle = mock(Bundle.class);
		BundleContext context = mock(BundleContext.class);
		when(context.getBundle()).thenReturn(bundle);

		plugin.start(context);
		manager.registerReusePlugin(plugin);
		Enumeration<URL> enumeration = getEnumeration();
		URL url = enumeration.nextElement();
		List<Distribution> distributions = getInitializedDistributionsList(url
				.getFile());
		when(
				bundle.findEntries(DistributionManager.DISTRIBUTION_XML_PATH,
						"*.xml", false)).thenReturn(enumeration);
		when(
				manager.getUserDefinedDistribution(model,
						NetworkElementNodeType.SECTOR, "antenne",
						url.getFile(), new DistributionXmlParser(url)))
				.thenReturn(null);
		when(manager.distributionService.findUserDefinedDistributions())
				.thenReturn(distributions);

		when(model.getName()).thenReturn(DEFAULT_MODEL_NAME);
		try {
			List<IDistribution<?>> result = manager.getDistributions(model,
					NetworkElementNodeType.SECTOR, "antenne",
					ChartType.getDefault(), true);
			Assert.assertTrue(!result.isEmpty());
			Assert.assertEquals(3, result.size());
			Assert.assertNotNull(manager.getDistributions(model,
					NetworkElementNodeType.SECTOR, "antenne",
					ChartType.getDefault()));

		} catch (DistributionManagerException e) {
			e.printStackTrace();
		}

		plugin.stop(context);

	}

	@Test
	public void checkCreateDistributionFromFile() throws FileNotFoundException,
			DistributionXmlParsingException, IOException, DatabaseException {
		String path = "../org.amanzi.awe.views.reuse/distributions/test_distributions.xml";
		DistributionXmlParser parser = new DistributionXmlParser(
				new FileInputStream(path), path);
		Node node = mock(Node.class);
		when(
				manager.distributionService
						.createUserDefinedDistribution(parser.getXmlDistr()))
				.thenReturn(node);

		manager.createDistributionFromFile(path);
	}

	/**
	 * Creates mocked DistributionalModel
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IDistributionalModel getDistributionalModel(Class clazz) {
		IDistributionalModel model = mock(IDistributionalModel.class);
		when(model.getPropertyClass(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME))
				.thenReturn(clazz);
		when(model.getName()).thenReturn(DEFAULT_MODEL_NAME);
		when(model.getRootNode()).thenReturn(rootNode);
		when(model.getType()).thenReturn(DatasetTypes.NETWORK);
		when(model.getPropertyClass(NetworkElementNodeType.SECTOR, "antenne"))
				.thenReturn(clazz);
		return model;
	}

	/**
	 * Get initialized Node mock
	 * 
	 * @return node
	 */
	private Node getNode() {
		Node node = mock(Node.class);
		when(node.getProperty(NetworkService.TYPE, StringUtils.EMPTY))
				.thenReturn("MOCK_TYPE");
		when(node.getProperty(DistributionService.COUNT, 0)).thenReturn(1);
		return node;
	}

	@SuppressWarnings("deprecation")
	private Enumeration<URL> getEnumeration() throws IOException {
		Vector<URL> urls = new Vector<URL>(1);
		File xmlDistributions = new File(
				"../org.amanzi.awe.views.reuse/distributions/test_distributions.xml");
		urls.add(xmlDistributions.toURL());
		urls.add(xmlDistributions.toURL());
		return urls.elements();
	}

	/**
	 * Initialize Distribution mock
	 * 
	 * @param dataName
	 *            for data.getName()
	 * @return distribution mock
	 */
	private List<Distribution> getInitializedDistributionsList(String dataName) {
		Distribution distribution = mock(Distribution.class);
		Data data = mock(Data.class);
		Bars barsMock = mock(Bars.class);
		Bar bar = mock(Bar.class);
		Filter filter = mock(Filter.class);
		List<Bar> barsList = Arrays.asList(bar);

		when(distribution.getData()).thenReturn(data);
		when(data.getName()).thenReturn(dataName);
		when(distribution.getBars()).thenReturn(barsMock);
		when(data.getDataType()).thenReturn(DatasetTypes.NETWORK.toString());
		when(data.getNodeType()).thenReturn(
				NetworkElementNodeType.SECTOR.toString());
		when(barsMock.getBar()).thenReturn(barsList);
		when(bar.getName()).thenReturn("");
		when(bar.getColor()).thenReturn(null);
		when(bar.getFilter()).thenReturn(filter);
		when(filter.getNodeType()).thenReturn(
				NetworkElementNodeType.SECTOR.toString());
		when(filter.getPropertyName()).thenReturn("antenne");

		return Arrays.asList(distribution);
	}

	/**
	 * Hack ReusePlugin class for using mock in final methods
	 * 
	 * @author Bondoronok_P
	 */
	public class ReuseTestPlugin extends Plugin {

		@Override
		public void start(BundleContext context) throws Exception {
			super.start(context);
		}

		@Override
		public void stop(BundleContext context) throws Exception {
			super.stop(context);
		}
	}
}
