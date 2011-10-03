package org.amanzi.neo.services.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.CorrelationServiceTest;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.IDataElement;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class NetworkModelTest extends AbstractNeoServiceTest {

	private static Logger LOGGER = Logger
			.getLogger(CorrelationServiceTest.class);

	private Transaction tx;
	private static NewDatasetService dsServ;
	private static ProjectService prServ;
	private Node network;
	private Node project;
	private static int count = 0;
	
	private NetworkModel model;
	
	private final static NetworkElementNodeType[] POSSIBLE_ELEMENT_TYPES = new NetworkElementNodeType[]
	        {NetworkElementNodeType.BSC,
	         NetworkElementNodeType.MSC,
	         NetworkElementNodeType.CITY,
	         NetworkElementNodeType.SITE,
	         NetworkElementNodeType.SECTOR
	        };

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();
		
		new LogStarter().earlyStartup();
        clearServices();

		dsServ = new NewDatasetService(graphDatabaseService);
		prServ = new ProjectService(graphDatabaseService);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		clearDb();
	}

	@Before
	public final void before() {
		tx = graphDatabaseService.beginTx();
		try {
			count++;
			project = prServ.createProject("project" + count);
			network = dsServ.createDataset(project, "network",
					DatasetTypes.NETWORK);
			model = new NetworkModel(network);
		} catch (AWEException e) {
			LOGGER.error("Could not create test nodes.", e);
		}
	}

	@After
	public final void after() {
		tx.success();
		tx.finish();
	}

	@Test
	public void testUpdateBounds() {
		double min_lat = Double.MAX_VALUE;
		double max_lat = 0;
		double min_lon = Double.MAX_VALUE;
		double max_lon = 0;

		for (int i = 0; i < 10; i++) {
			double lat = Math.random() * Double.MAX_VALUE;
			if (lat < min_lat) {
				min_lat = lat;
			}
			if (lat > max_lat) {
				max_lat = lat;
			}
			double lon = Math.random() * Double.MAX_VALUE;
			if (lon < min_lon) {
				min_lon = lon;
			}
			if (lon > max_lon) {
				max_lon = lon;
			}
			model.updateLocationBounds(lat, lon);
		}

		// min and max values are valid
		assertEquals(min_lat, model.getMinLatitude());
		assertEquals(max_lat, model.getMaxLatitude());
		assertEquals(min_lon, model.getMinLongitude());
		assertEquals(max_lon, model.getMinLongitude());
	}

	@Test
	public void testNetworkModelIDataElement() {

		DataElement root = new DataElement(network);

		NetworkModel nm = new NetworkModel(root);

		// object created not null
		assertNotNull(nm);
		// root node correct
		assertEquals(network, nm.getRootNode());
		// name correct
		assertEquals(network.getProperty(NewAbstractService.NAME, null),
				nm.getName());
	}

	@Test
	public void testCreateElement() {
		IDataElement parentElement = new DataElement(network);
		for (INodeType type : POSSIBLE_ELEMENT_TYPES) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(NewAbstractService.TYPE, type.getId());
			params.put(NewAbstractService.NAME, type.getId());
			params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
			DataElement element = new DataElement(params);

			IDataElement testElement = model.createElement(parentElement,
					element);
			// object returned not null
			assertNotNull(testElement);
			// underlying node not null
			assertNotNull(((DataElement) testElement).getNode());
			// properties set
			for (String key : params.keySet()) {
				assertEquals(params.get(key), testElement.get(key));
			}
			parentElement = testElement;
		}
	}

	@Test
	public void testFindElement() {
		IDataElement parentElement = new DataElement(network);
		for (INodeType type : POSSIBLE_ELEMENT_TYPES) {

			Map<String, Object> params = new HashMap<String, Object>();
			params.put(NewAbstractService.TYPE, type.getId());
			params.put(NewAbstractService.NAME, type.getId());
			params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
			DataElement element = new DataElement(params);

			IDataElement newElement = model.createElement(parentElement,
					element);
			parentElement = newElement;
		}

		for (INodeType type : POSSIBLE_ELEMENT_TYPES) {

			Map<String, Object> params = new HashMap<String, Object>();
			params.put(NewAbstractService.TYPE, type.getId());
			params.put(NewAbstractService.NAME, type.getId());
			IDataElement testElement = model
					.findElement(new DataElement(params));
			// object returned not null
			assertNotNull(testElement);
			// underlying node not null
			assertNotNull(((DataElement) testElement).getNode());

		}
	}

	@Test
	public void testGetElement() {
		IDataElement parentElement = new DataElement(network);
		for (INodeType type : POSSIBLE_ELEMENT_TYPES) {

			Map<String, Object> params = new HashMap<String, Object>();
			params.put(NewAbstractService.TYPE, type.getId());
			params.put(NewAbstractService.NAME, type.getId());
			params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
			DataElement element = new DataElement(params);

			IDataElement newElement = model.createElement(parentElement,
					element);
			parentElement = newElement;
		}

		parentElement = new DataElement(network);
		for (INodeType type : NetworkElementNodeType.values()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(NewAbstractService.TYPE, type.getId());
			params.put(NewAbstractService.NAME, type.getId());
			IDataElement testElement = model.getElement(parentElement,
					new DataElement(params));
			// object returned not null
			assertNotNull(testElement);
			// underlying node not null
			assertNotNull(((DataElement) testElement).getNode());

			parentElement = testElement;
		}
	}

	@Test
	public void testGetElementNoElement() {
		IDataElement parentElement = new DataElement(network);
		for (INodeType type : POSSIBLE_ELEMENT_TYPES) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(NewAbstractService.TYPE, type.getId());
			params.put(NewAbstractService.NAME, type.getId());
			IDataElement testElement = model.getElement(parentElement,
					new DataElement(params));
			// object returned not null
			assertNotNull(testElement);
			// underlying node not null
			assertNotNull(((DataElement) testElement).getNode());

			parentElement = testElement;
		}
	}

	@Test
	public void testGetCorrelationModels() throws AWEException {
		List<Node> datasets = new ArrayList<Node>();
		try {
			for (int i = 0; i < 4; i++) {
				Node dataset = dsServ.createDataset(project, "network" + i,
						DatasetTypes.DRIVE, DriveTypes.values()[0]);
				datasets.add(dataset);
				new CorrelationModel(network, dataset);
			}
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}

		Iterable<ICorrelationModel> it = null;
		try {
			it = model.getCorrelationModels();
		} catch (AWEException e) {
			LOGGER.error("Could not get correlation models.", e);
			fail();
		}
		Assert.assertNotNull(it);
		Assert.assertTrue(it.iterator().hasNext());
		try {
			for (ICorrelationModel mod : model.getCorrelationModels()) {
				Assert.assertTrue(datasets.contains(mod.getDataset()));
				Assert.assertEquals(network, mod.getNetwork());
			}
		} catch (AWEException e) {
			LOGGER.error("Could not get correlation models.", e);
			fail();
		}
	}

	@Test
	public void testGetChildren() {
		Mockery context = new Mockery() {
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};

		final NewDatasetService dsS = context.mock(NewDatasetService.class);

		// expectations
		context.checking(new Expectations() {
			{
				atLeast(1).of(dsS).getChildrenTraverser(network);
			}
		});

		// execute
		model.setDatasetService(dsS);
		DataElement de = new DataElement(network);
		model.getChildren(de);

		// verify
		context.assertIsSatisfied();
	}

	@Test
	public void testGetAllElementsByType() {
		Mockery context = new Mockery() {
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};

		final NewNetworkService nwS = context.mock(NewNetworkService.class);

		for (final NetworkElementNodeType type : POSSIBLE_ELEMENT_TYPES) {
			// expectations
			context.checking(new Expectations() {
				{
					atLeast(1).of(nwS).findAllNetworkElements(network, type);
				}
			});
		}

		// execute
		model.setNetworkService(nwS);

		for (NetworkElementNodeType type : POSSIBLE_ELEMENT_TYPES) {
			model.getAllElementsByType(type);
		}

		// verify
		context.assertIsSatisfied();
	}
}
