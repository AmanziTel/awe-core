package org.amanzi.neo.services.model.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.DriveModel.DriveRelationshipTypes;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class DriveModelTest extends AbstractAWETest {

	private static Logger LOGGER = Logger.getLogger(DriveModelTest.class);
	private static final String databasePath = getDbLocation();
	private static ProjectService prServ;
	private static NewDatasetService dsServ;
	private static Node project, dataset;
	private static String dsName;
	private static int count = 0;
	private static String filename = "." + File.separator + "file.txt";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();
		
		LOGGER.info("Database created in folder " + databasePath);
		prServ = NeoServiceFactory.getInstance().getNewProjectService();
		dsServ = NeoServiceFactory.getInstance().getNewDatasetService();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
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
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// check that getName returns the right name
		Assert.assertEquals(dsName, dm.getName());
	}

	@Test
	public final void testGetRootNode() {
		// create a drive model
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// check that getName returns the right root node
		Assert.assertEquals(dataset, dm.getRootNode());
	}

	@Test
	public final void testConstructor() {
		// all params set
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		Assert.assertNotNull(dm);
		Assert.assertEquals(dataset, dm.getRootNode());
		Assert.assertEquals(dsName, dm.getName());
	}

	@Test
	public final void testConstructorRootNull() {
		String name = "drive_model";
		// root is null
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, null, name, DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		Assert.assertNotNull(dm);
		Assert.assertEquals(name, dm.getName());
		Assert.assertEquals(name,
				dm.getRootNode().getProperty(NewAbstractService.NAME, null));
	}

	@Test
	public final void testAddVirtualDataset() {
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// add virtual dataset
		DriveModel virtual = null;
		try {
			virtual = dm.addVirtualDataset("name", DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not add virtual dataset", e);
			fail();
		}
		// object returned is not null
		Assert.assertNotNull(virtual);
		// name is correct
		Assert.assertEquals("name", virtual.getName());
		// root node is correct
		Assert.assertNotNull(virtual.getRootNode());
		// root node type is correct
		Assert.assertEquals(DatasetTypes.DRIVE.getId(), virtual.getRootNode()
				.getProperty(NewAbstractService.TYPE, null));
		Assert.assertEquals(DriveTypes.values()[0].getId(), virtual
				.getRootNode().getProperty(DriveModel.DRIVE_TYPE, null));
	}

	@Test(expected = DuplicateNodeNameException.class)
	public final void testAddVirtualDatasetSameName() throws AWEException {
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// add first virtual dataset
		try {
			dm.addVirtualDataset("name", DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not add virtual dataset", e);
			fail();
		}
		// exception
		dm.addVirtualDataset("name", DriveTypes.values()[0]);
	}

	@Test(expected = IllegalNodeDataException.class)
	public final void testAddVirtualDatasetNameNull() throws AWEException {
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// add virtual dataset
		dm.addVirtualDataset(null, DriveTypes.values()[0]);
		// exception
	}

	@Test(expected = IllegalNodeDataException.class)
	public final void testAddVirtualDatasetNameEmpty() throws AWEException {
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// add virtual dataset
		dm.addVirtualDataset("", DriveTypes.values()[0]);
		// exception
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddVirtualDatasetTypeNull() {
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// add virtual dataset
		try {
			dm.addVirtualDataset("name", null);
		} catch (AWEException e) {
			LOGGER.error("Could not add virtual dataset", e);
			fail();
		}
		// exception
	}

	@Test
	public final void testFindVirtualDataset() {
		// dataset exists
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// add virtual dataset
		try {
			dm.addVirtualDataset("name", DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not add virtual dataset", e);
			fail();
		}

		IDriveModel virtual = dm.findVirtualDataset("name");
		// DM returned not null
		Assert.assertNotNull(virtual);
		// name is correct
		Assert.assertEquals("name", virtual.getName());
		// root node is correct
		Assert.assertEquals(
				dm.getRootNode(),
				virtual.getRootNode()
						.getRelationships(
								DriveRelationshipTypes.VIRTUAL_DATASET,
								Direction.INCOMING).iterator().next()
						.getOtherNode(virtual.getRootNode()));
	}

	@Test
	public final void testFindVirtualDatasetNoDataset() {
		// no dataset
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}

		IDriveModel virtual = dm.findVirtualDataset("name");
		// DM returned null
		Assert.assertNull(virtual);
	}

	@Test
	public final void testGetDataset() {
		// dataset exists
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		// add virtual dataset
		try {
			dm.addVirtualDataset("name", DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not add virtual dataset", e);
			fail();
		}

		IDriveModel virtual = null;
		try {
			virtual = dm.getVirtualDataset("name", DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not add virtual dataset", e);
			fail();
		}
		// DM returned not null
		Assert.assertNotNull(virtual);
		// name is correct
		Assert.assertEquals("name", virtual.getName());
		// root node is correct
		Assert.assertEquals(
				dm.getRootNode(),
				virtual.getRootNode()
						.getRelationships(
								DriveRelationshipTypes.VIRTUAL_DATASET,
								Direction.INCOMING).iterator().next()
						.getOtherNode(virtual.getRootNode()));
	}

	@Test
	public final void testGetDatasetNoDataset() {
		// no dataset
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}

		IDriveModel virtual = null;
		try {
			virtual = dm.getVirtualDataset("name", DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not add virtual dataset", e);
			fail();
		}
		// DM returned not null
		Assert.assertNotNull(virtual);
		// name is correct
		Assert.assertEquals("name", virtual.getName());
		// root node is correct
		Assert.assertEquals(
				dm.getRootNode(),
				virtual.getRootNode()
						.getRelationships(
								DriveRelationshipTypes.VIRTUAL_DATASET,
								Direction.INCOMING).iterator().next()
						.getOtherNode(virtual.getRootNode()));
	}

	@Test
	public final void testGetVirtualDatasets() {
		// add virtual datasets
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		List<Node> dss = new ArrayList<Node>();
		for (int i = 0; i < 4; i++) {
			try {
				dss.add(dm.addVirtualDataset("" + i, DriveTypes.values()[0])
						.getRootNode());
			} catch (AWEException e) {
				LOGGER.error("Could not add virtual dataset", e);
				fail();
			}
		}
		Iterable<IDriveModel> it = dm.getVirtualDatasets();
		// traverser is not null
		Assert.assertNotNull(it);
		// check that all virtual datasets are returned
		for (IDriveModel drm : it) {
			Assert.assertTrue(dss.contains(drm.getRootNode()));
		}
	}

	@Test
	public final void testGetVirtualDatasetsNoDatasets() {
		// no virtual datasets
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		Iterable<IDriveModel> it = dm.getVirtualDatasets();
		// traverser is not null
		Assert.assertNotNull(it);
		// no nodes retured
		Assert.assertFalse(it.iterator().hasNext());
	}

	@Test
	public final void testAddFile() {
		// add file
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		File f = new File(filename);
		Node fileNode = null;
		try {
			fileNode = ((DataElement) dm.addFile(f)).getNode();
		} catch (DatabaseException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		} catch (DuplicateNodeNameException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		}
		// node returned is not null
		Assert.assertNotNull(fileNode);
		// name correct
		Assert.assertEquals("file.txt",
				fileNode.getProperty(NewAbstractService.NAME, null));
		// path correct
		Assert.assertEquals(filename,
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
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		try {
			dm.addFile(null);
		} catch (DatabaseException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		} catch (DuplicateNodeNameException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		}
		// exception
	}

	@Test
	public final void testAddMeasurement() {
		// add measurement with some parameters
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		Node f = null;
		try {
			f = ((DataElement) dm.addFile(new File(filename))).getNode();
		} catch (DatabaseException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		} catch (DuplicateNodeNameException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fake", "param");
		params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
		Node m = null;
		try {
			m = ((DataElement) dm.addMeasurement(
					filename.substring(filename.lastIndexOf('\\') + 1), params))
					.getNode();
			dm.finishUp();
		} catch (DatabaseException e) {
			LOGGER.error("Could not add measurement", e);
			fail();
		} catch (AWEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// node returned is not null
		Assert.assertNotNull(m);
		// all params are set
		for (String key : params.keySet()) {
			Assert.assertEquals(params.get(key), m.getProperty(key, null));
		}
		// dataset id set
		Assert.assertEquals(dsName,
				m.getProperty(NewAbstractService.DATASET_ID, null));
		// type correct
		Assert.assertEquals(DriveNodeTypes.M.getId(),
				m.getProperty(NewAbstractService.TYPE, null));
		// chain exists
		Assert.assertTrue(chainExists(f, m));
		// root primary_type set
		Assert.assertEquals(DriveNodeTypes.M.getId(),
				dataset.getProperty(DriveModel.PRIMARY_TYPE, null));
		// root min|max timestamp set
		Assert.assertEquals(params.get(DriveModel.TIMESTAMP),
				dataset.getProperty(DriveModel.MIN_TIMESTAMP, 0L));
		Assert.assertEquals(params.get(DriveModel.TIMESTAMP),
				dataset.getProperty(DriveModel.MAX_TIMESTAMP, 0L));
		// root count updated
		Assert.assertEquals(1, dataset.getProperty(DriveModel.COUNT, -1));
	}

	@Test
	public final void testAddMeasurementRootParams() {
		// add few measurements with some parameters
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		try {
			dm.addFile(new File(filename));
		} catch (DatabaseException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		} catch (DuplicateNodeNameException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		}

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
			Node me = null;
			try {
				me = ((DataElement) dm.addMeasurement(
						filename.substring(filename.lastIndexOf('\\') + 1), m))
						.getNode();
			} catch (DatabaseException e) {
				LOGGER.error("Could not add measurement", e);
				fail();
			} catch (AWEException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ms.put(me, m);
		}
		dm.finishUp();

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
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		try {
			dm.addFile(new File(filename));
		} catch (DatabaseException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		} catch (DuplicateNodeNameException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		}

		Map<String, Object> params = new HashMap<String, Object>();
		long lat = (long) (Math.random() * Long.MAX_VALUE);
		long lon = (long) (Math.random() * Long.MAX_VALUE);
		params.put(DriveModel.LATITUDE, lat);
		params.put(DriveModel.LONGITUDE, lon);
		params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
		Node m = null;
		try {
			m = ((DataElement) dm.addMeasurement(
					filename.substring(filename.lastIndexOf('\\') + 1), params))
					.getNode();
		} catch (DatabaseException e) {
			LOGGER.error("Could not add measurement", e);
			fail();
		} catch (AWEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Node l = ((DataElement) dm.getLocation(m)).getNode();
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
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		try {
			dm.addFile(new File(filename));
		} catch (DatabaseException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		} catch (DuplicateNodeNameException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(DriveModel.LATITUDE, null);
		params.put(DriveModel.LONGITUDE, null);
		params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
		Node m = null;
		try {
			m = ((DataElement) dm.addMeasurement(
					filename.substring(filename.lastIndexOf('\\') + 1), params))
					.getNode();
		} catch (DatabaseException e) {
			LOGGER.error("Could not add measurement", e);
			fail();
		} catch (AWEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IDataElement l = dm.getLocation(m);
		// location node not created
		Assert.assertNull(l);
	}

	@Test
	public final void testAddMeasurementLatLonEmpty() {
		// add measurement with some parameters
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		try {
			dm.addFile(new File(filename));
		} catch (DatabaseException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		} catch (DuplicateNodeNameException e) {
			LOGGER.error("Could not add file node", e);
			fail();
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(DriveModel.LATITUDE, 0L);
		params.put(DriveModel.LONGITUDE, 0L);
		params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
		Node m = null;
		try {
			m = ((DataElement) dm.addMeasurement(
					filename.substring(filename.lastIndexOf('\\') + 1), params))
					.getNode();
		} catch (DatabaseException e) {
			LOGGER.error("Could not add measurement", e);
			fail();
		} catch (AWEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IDataElement l = dm.getLocation(m);
		// location node not created
		Assert.assertNull(l);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddMeasurementFilenameNull() {
		// add measurement filename null
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		try {
			dm.addMeasurement(null, new HashMap<String, Object>());
		} catch (DatabaseException e) {
			LOGGER.error("Could not add measurement", e);
			fail();
		}
		// exception
		catch (AWEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddMeasurementFilenameEmpty() {
		// add measurement filename ""
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		try {
			dm.addMeasurement("", new HashMap<String, Object>());
		} catch (DatabaseException e) {
			LOGGER.error("Could not add measurement", e);
			fail();
		} catch (AWEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		// exception
	}

	@Test
	public void testGetCorrelatedModels() throws AWEException {
		DriveModel dm = null;
		List<Node> networks = new ArrayList<Node>();
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
			for (int i = 0; i < 4; i++) {
				Node network = dsServ.createDataset(project, "network" + i,
						DatasetTypes.NETWORK);
				networks.add(network);
				new CorrelationModel(network, dataset);
			}
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}

		Iterable<ICorrelationModel> it = dm.getCorrelatedModels();
		Assert.assertNotNull(it);
		Assert.assertTrue(it.iterator().hasNext());
		for (ICorrelationModel model : dm.getCorrelatedModels()) {
			Assert.assertTrue(networks.contains(model.getNetwork()));
			Assert.assertEquals(dataset, model.getDataset());
		}
	}

	@Test
	public void testGetCorrelatedModel() throws AWEException {
		DriveModel dm = null;
		List<Node> networks = new ArrayList<Node>();
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
			for (int i = 0; i < 4; i++) {
				Node network = dsServ.createDataset(project, "network" + i,
						DatasetTypes.NETWORK);
				networks.add(network);
				new CorrelationModel(network, dataset);
			}
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}

		for (int i = 0; i < networks.size(); i++) {
			ICorrelationModel cm = dm.getCorrelatedModel("network" + i);
			Assert.assertEquals(dataset, cm.getDataset());
			Assert.assertEquals(networks.get(i), cm.getNetwork());
		}
	}

	@Test
	public void testGetFiles() {
		List<Node> fileNodes = new ArrayList<Node>();
		// add file
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		for (int i = 0; i < 7; i++) {

			try {
				fileNodes
						.add(((DataElement) dm.addFile(new File(filename + i)))
								.getNode());
			} catch (DatabaseException e) {
				LOGGER.error("Could not add file node", e);
				fail();
			} catch (DuplicateNodeNameException e) {
				LOGGER.error("Could not add file node", e);
				fail();
			}
		}

		Iterable<IDataElement> it = dm.getFiles();
		// the object returned is not null
		Assert.assertNotNull(it);
		// iterator has next
		Assert.assertTrue(it.iterator().hasNext());
		for (IDataElement node : it) {
			// node correct
			Assert.assertTrue(fileNodes.contains(((DataElement) node).getNode()));
		}
	}

	@Test
	public void testGetMeasurements() {
		Map<String, List<Node>> fms = new HashMap<String, List<Node>>();
		// add measurement with some parameters
		DriveModel dm = null;
		try {
			dm = new DriveModel(project, dataset, dsName,
					DriveTypes.values()[0]);
		} catch (AWEException e) {
			LOGGER.error("Could not create drive model", e);
			fail();
		}
		for (int i = 0; i < 4; i++) {
			String fname = filename + i;
			fms.put(fname, new ArrayList<Node>());
			try {
				dm.addFile(new File(fname));
			} catch (DatabaseException e) {
				LOGGER.error("Could not add file node", e);
				fail();
			} catch (DuplicateNodeNameException e) {
				LOGGER.error("Could not add file node", e);
				fail();
			}
			for (int j = 0; j < 3; j++) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("fake", "param");
				params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());

				try {
					fms.get(fname).add(
							((DataElement) dm.addMeasurement(fname, params))
									.getNode());
				} catch (DatabaseException e) {
					LOGGER.error("Could not add measurement", e);
					fail();
				} catch (AWEException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (String fname : fms.keySet()) {
			Iterable<IDataElement> it = dm.getMeasurements(fname);
			// object not null
			Assert.assertNotNull(it);
			// has next
			Assert.assertTrue(it.iterator().hasNext());
			for (IDataElement node : it) {
				// node correct
				Assert.assertTrue(fms.get(fname).contains(
						((DataElement) node).getNode()));
			}
		}
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

	@SuppressWarnings("static-access")
	private boolean isIndexed(Node parent, Node node, String name, Object value) {
		Node n = parent
				.getGraphDatabase()
				.index()
				.forNodes(
						dsServ.getIndexKey(parent, NodeTypeManager.getType(node
								.getProperty(NewAbstractService.TYPE, "")
								.toString()))).get(name, value).getSingle();
		return n.equals(node);

	}

}
