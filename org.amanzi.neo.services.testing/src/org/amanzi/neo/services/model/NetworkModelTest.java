package org.amanzi.neo.services.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.amanzi.neo.services.CorrelationService;
import org.amanzi.neo.services.CorrelationServiceTest;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class NetworkModelTest extends AbstractAWETest {

	private static Logger LOGGER = Logger
			.getLogger(CorrelationServiceTest.class);

	private Transaction tx;
	private static CorrelationService correlationServ;
	private static NewDatasetService dsServ;
	private static NewNetworkService nwServ;
	private static ProjectService prServ;
	private Node network;
	private Node dataset;
	private Node project;
	private static int count = 0;
	private static String filename = "." + File.separator + "file.txt";

	private NetworkModel model;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();

		correlationServ = new CorrelationService(graphDatabaseService);
		dsServ = new NewDatasetService(graphDatabaseService);
		nwServ = new NewNetworkService(graphDatabaseService);
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
	public void testGetMinLatitude() {
		double min = Double.MAX_VALUE;

		for (int i = 0; i < 10; i++) {
			double lat = Math.random() * Double.MAX_VALUE;
			if (lat < min) {
				min = lat;
			}
			model.updateBounds(lat, 0);
		}

		assertEquals(min, model.getMinLatitude());
	}

	@Test
	public void testGetMaxLatitude() {
		double max = 0;

		for (int i = 0; i < 10; i++) {
			double lat = Math.random() * Double.MAX_VALUE;
			if (lat > max) {
				max = lat;
			}
			model.updateBounds(lat, 0);
		}

		assertEquals(max, model.getMaxLatitude());
	}

	@Test
	public void testGetMinLongitude() {
		double min = Double.MAX_VALUE;

		for (int i = 0; i < 10; i++) {
			double lon = Math.random() * Double.MAX_VALUE;
			if (lon < min) {
				min = lon;
			}
			model.updateBounds(0, lon);
		}

		assertEquals(min, model.getMinLongitude());
	}

	@Test
	public void testGetMaxLongitude() {
		double max = 0;

		for (int i = 0; i < 10; i++) {
			double lon = Math.random() * Double.MAX_VALUE;
			if (lon > max) {
				max = lon;
			}
			model.updateBounds(0, lon);
		}

		assertEquals(max, model.getMaxLongitude());
	}

	@Test
	public void testNetworkModelIDataElement() {

		DataElement root = new DataElement(network);

		NetworkModel nm = new NetworkModel(root);

		// object created not null
		// root node correct
		// name correct
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testCreateElement() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testFindElement() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetElement() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testUpdateBounds() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	@Test
	public void testGetCRS() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	@Test
	public void testGetNetworkType() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetCorrelationModels() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetChildren() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetAllElementsByType() {
		fail("Not yet implemented"); // TODO
	}
}
