package org.amanzi.neo.services.model;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NewNetworkServiceTest;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.DriveModel.DriveRelationshipTypes;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class DriveModelTest extends AbstractAWETest {

	private static Logger LOGGER = Logger
			.getLogger(NewNetworkServiceTest.class);
	private static final String databasePath = getDbLocation();
	private static Transaction tx;
	private static ProjectService prServ;
	private static NewDatasetService dsServ;
	private static NewNetworkService nwServ;
	private static Node project, dataset;
	private static String dsName;
	private static int count = 0;
	private static String filename = "c:\\dev\\file.txt";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDatabaseService = new EmbeddedGraphDatabase(databasePath);
		LOGGER.info("Database created in folder " + databasePath);
		prServ = new ProjectService(graphDatabaseService);
		dsServ = new NewDatasetService(graphDatabaseService);
		nwServ = new NewNetworkService(graphDatabaseService);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (graphDatabaseService != null) {
			graphDatabaseService.shutdown();
			LOGGER.info("Database shut down");
		}
		clearDb();
	}

	@Before
	public void newProject() {
		try {
			project = prServ.createProject("project" + count++);
			dsName = "dataset" + count;
			dataset = dsServ.createDataset(project, dsName, DatasetTypes.DRIVE,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
		}
	}

	@Test
	public final void testGetName() {
		// create a drive model
		DriveModel dm = new DriveModel(project, dataset, dsName);
		// check that getName returns the right name
		Assert.assertEquals(dsName, dm.getName());
	}

	@Test
	public final void testGetRootNode() {
		// create a drive model
		DriveModel dm = new DriveModel(project, dataset, dsName);
		// check that getName returns the right root node
		Assert.assertEquals(dataset, dm.getRootNode());
	}

	@Test
	public final void testConstructor() {
		// all params set
		DriveModel dm = new DriveModel(project, dataset, dsName);
		Assert.assertNotNull(dm);
		Assert.assertEquals(dataset, dm.getRootNode());
		Assert.assertEquals(dsName, dm.getName());
	}

	@Test
	public final void testConstructorRootNull() {
		String name = "drive_model";
		// root is null
		DriveModel dm = new DriveModel(project, null, name);
		Assert.assertNotNull(dm);
		Assert.assertEquals(name, dm.getName());
		Assert.assertEquals(name,
				dm.getRootNode().getProperty(NewAbstractService.NAME, null));
	}

	@Test
	public final void testAddVirtualDataset() {
		DriveModel dm = new DriveModel(project, dataset, dsName);
		// add virtual dataset
		DriveModel virtual = dm.addVirtualDataset("name",
				DriveTypes.values()[0]);
		// object returned is not null
		Assert.assertNotNull(virtual);
		// name is correct
		Assert.assertEquals("name", virtual.getName());
		// root node is correct
		Assert.assertNotNull(virtual.getRootNode());
		// root node type is correct
		Assert.assertEquals(DatasetTypes.DRIVE, virtual.getRootNode()
				.getProperty(NewAbstractService.TYPE, null));
		Assert.assertEquals(DriveTypes.values()[0], virtual.getRootNode()
				.getProperty(DriveModel.DRIVE_TYPE, null));
	}

	@Test
	public final void testGetVirtualDatasets() {
		// add virtual datasets
		DriveModel dm = new DriveModel(project, dataset, dsName);
		List<DriveModel> dss = new ArrayList<DriveModel>();
		for (int i = 0; i < 4; i++) {
			dss.add(dm.addVirtualDataset("" + i, DriveTypes.values()[0]));
		}
		Iterable<DriveModel> it = dm.getVirtualDatasets();
		// traverser is not null
		Assert.assertNotNull(it);
		// check that all virtual datasets are returned
		for (DriveModel drm : it) {
			Assert.assertTrue(dss.contains(drm));
		}
	}

	@Test
	public final void testGetVirtualDatasetsNoDatasets() {
		// no virtual datasets
		DriveModel dm = new DriveModel(project, dataset, dsName);
		Iterable<DriveModel> it = dm.getVirtualDatasets();
		// traverser is not null
		Assert.assertNotNull(it);
		// no nodes retured
		Assert.assertFalse(it.iterator().hasNext());
	}

	@Test
	public final void testAddFile() {
		// add file
		DriveModel dm = new DriveModel(project, dataset, dsName);
		File f = new File(filename);
		Node fileNode = dm.addFile(f);
		// node returned is not null
		Assert.assertNotNull(fileNode);
		// name correct
		Assert.assertEquals("file.txt",
				fileNode.getProperty(NewAbstractService.NAME, null));
		// path correct
		Assert.assertEquals("c:\\dev",
				fileNode.getProperty(DriveModel.PATH, null));
		// type correct
		Assert.assertEquals(DriveNodeTypes.FILE.getId(),
				fileNode.getProperty(NewAbstractService.TYPE));
		// chain exists
		Assert.assertTrue(chainExists(dataset, fileNode));
		// node indexed
		Assert.assertTrue(isIndexed(dataset, fileNode, NewAbstractService.NAME,
				fileNode.getProperty(NewAbstractService.NAME, "")));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddFileNull() {
		// add file null
		DriveModel dm = new DriveModel(project, dataset, dsName);
		dm.addFile(null);
		// exception
	}

	@Test
	public final void testAddMeasurement() {
		// add measurement with some parameters
		DriveModel dm = new DriveModel(project, dataset, dsName);
		Node f = dm.addFile(new File(filename));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fake", "param");
		params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
		Node m = dm.addMeasurement(filename, params);
		// node returned is not null
		Assert.assertNotNull(m);
		// all params are set
		for (String key : params.keySet()) {
			Assert.assertEquals(params.get(key), m.getProperty(key, null));
		}
		// type correct
		Assert.assertEquals(DriveNodeTypes.M.getId(),
				m.getProperty(NewAbstractService.TYPE, null));
		// chain exists
		Assert.assertTrue(chainExists(f, m));
		// root primary_type set
		Assert.assertEquals(DriveNodeTypes.M.getId(),
				dataset.getProperty(DriveModel.PRIMARY_TYPE, null));
		// root min|max timestamp set
		Assert.assertEquals(params.get(DriveModel.MIN_TIMESTAMP),
				dataset.getProperty(DriveModel.TIMESTAMP, 0L));
		Assert.assertEquals(params.get(DriveModel.MAX_TIMESTAMP),
				dataset.getProperty(DriveModel.TIMESTAMP, 0L));
		// root count updated
		Assert.assertEquals(1, dataset.getProperty(DriveModel.COUNT, -1));
	}

	@Test
	public final void testAddMeasurementRootParams() {
		// add few measurements with some parameters
		DriveModel dm = new DriveModel(project, dataset, dsName);
		dm.addFile(new File(filename));

		Map<Node, Map<String, Object>> ms = new HashMap<Node, Map<String, Object>>();
		long min_tst = Long.MAX_VALUE, max_tst = 0;
		for (int i = 0; i < 4; i++) {
			Map<String, Object> m = new HashMap<String, Object>();
			long tst = (long) (Math.random() * Long.MAX_VALUE);
			if (tst < min_tst)
				min_tst = tst;
			if (tst > max_tst)
				max_tst = tst;
			m.put(DriveModel.TIMESTAMP, tst);
			ms.put(dm.addMeasurement(filename, m), m);
		}

		// root params updated correctly
		Node root = dm.getRootNode();
		Assert.assertEquals(4, root.getProperty(DriveModel.COUNT, 0));
		Assert.assertEquals(min_tst,
				root.getProperty(DriveModel.MIN_TIMESTAMP, 0L));
		Assert.assertEquals(max_tst,
				root.getProperty(DriveModel.MAX_TIMESTAMP, 0L));
		Assert.assertEquals(
				ms.keySet().iterator().next()
						.getProperty(NewAbstractService.TYPE, null),
				root.getProperty(DriveModel.PRIMARY_TYPE));
	}

	@Test
	public final void testAddMeasurementLatLon() {
		// add measurement with some parameters
		DriveModel dm = new DriveModel(project, dataset, dsName);
		dm.addFile(new File(filename));

		Map<String, Object> params = new HashMap<String, Object>();
		long lat = (long) (Math.random() * Long.MAX_VALUE);
		long lon = (long) (Math.random() * Long.MAX_VALUE);
		params.put(DriveModel.LATITUDE, lat);
		params.put(DriveModel.LONGITUDE, lon);
		params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
		Node m = dm.addMeasurement(filename, params);

		Node l = dm.getLocation(m);
		// location node created
		Assert.assertNotNull(l);
		// location node properties correct
		Assert.assertEquals(lat, l.getProperty(DriveModel.LATITUDE, 0L));
		Assert.assertEquals(lon, l.getProperty(DriveModel.LONGITUDE, 0L));
		// chain exists
		Assert.assertEquals(
				m,
				l.getRelationships(DriveRelationshipTypes.LOCATION,
						Direction.INCOMING).iterator().next().getOtherNode(l));
	}

	@Test
	public final void testAddMeasurementLatLonNull() {
		// add measurement with some parameters
		DriveModel dm = new DriveModel(project, dataset, dsName);
		dm.addFile(new File(filename));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(DriveModel.LATITUDE, null);
		params.put(DriveModel.LONGITUDE, null);
		params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
		Node m = dm.addMeasurement(filename, params);

		Node l = dm.getLocation(m);
		// location node not created
		Assert.assertNull(l);
	}

	@Test
	public final void testAddMeasurementLatLonEmpty() {
		// add measurement with some parameters
		DriveModel dm = new DriveModel(project, dataset, dsName);
		dm.addFile(new File(filename));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(DriveModel.LATITUDE, 0);
		params.put(DriveModel.LONGITUDE, 0);
		params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
		Node m = dm.addMeasurement(filename, params);

		Node l = dm.getLocation(m);
		// location node not created
		Assert.assertNull(l);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddMeasurementFilenameNull() {
		// add measurement filename null
		(new DriveModel(project, dataset, dsName)).addMeasurement(null,
				new HashMap<String, Object>());
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddMeasurementFilenameEmpty() {
		// add measurement filename ""
		(new DriveModel(project, dataset, dsName)).addMeasurement("",
				new HashMap<String, Object>());
		// exception
	}

	private boolean chainExists(Node parent, Node child) {
		Iterator<Relationship> it = parent.getRelationships(
				DatasetRelationTypes.CHILD, Direction.OUTGOING).iterator();

		Node prevNode = null, node = null;
		if (it.hasNext()) {
			// prevNode is set to first child
			prevNode = it.next().getOtherNode(parent);
		} else {
			return false;
		}

		while (true) {
			if (prevNode.equals(child)) {
				return true;
			}
			it = prevNode.getRelationships(DatasetRelationTypes.NEXT,
					Direction.OUTGOING).iterator();
			if (it.hasNext()) {
				node = it.next().getOtherNode(prevNode);
			} else {
				return false;
			}
			prevNode = node;
		}
	}

	private boolean isIndexed(Node parent, Node node, String name, Object value) {
		Node n = parent
				.getGraphDatabase()
				.index()
				.forNodes(
						dsServ.getIndexKey(parent,
								NetworkElementNodeType.valueOf(node
										.getProperty(NewAbstractService.TYPE,
												"").toString())))
				.get(name, value).getSingle();
		return n.equals(node);

	}

}
