package org.amanzi.neo.loader.core.saver.impl.testing;

import static org.junit.Assert.fail;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.impl.DriveModelSaver;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.DriveModel;
import org.amanzi.testing.AbstractAWETest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;

public class DriveModelSaverTest extends AbstractAWETest {

	private static DriveModelSaver<BaseTransferData> saver;
	private static BaseTransferData data;
	private static Node project;
	private static Node rootDataset;
	private static String projectName = "project";
	private static String rootName = "root";
	private static String filename = "c:\\file.txt";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clearDb();
		initializeDb();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopDb();
		clearDb();
	}

	@Before
	public void setUp() throws Exception {
		data = new BaseTransferData();
		saver = new DriveModelSaver<BaseTransferData>();
		project = NeoServiceFactory.getInstance().getNewProjectService()
				.getProject(projectName);
		rootDataset = NeoServiceFactory
				.getInstance()
				.getNewDatasetService()
				.getDataset(project, rootName, DatasetTypes.DRIVE,
						DriveTypes.ROMES);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() {
		// preset
		data.setProjectName(projectName);
		data.setFileName(filename);
		data.setRootName(rootName);
		data.put(DriveModel.DRIVE_TYPE, DriveTypes.ROMES.getId());

		saver.init(data);
		DriveModel dm = saver.getDriveModel();
		// drive model created root and project correct
		Assert.assertNotNull(dm);
		Assert.assertEquals(rootDataset, dm.getRootNode());
		Assert.assertEquals(rootName, dm.getName());
	}

	@Test
	public void testInitNoData() {
		// preset
		data.setProjectName("newProject");
		data.setFileName(filename);
		data.setRootName("newRoot");
		data.put(DriveModel.DRIVE_TYPE, DriveTypes.ROMES.getId());

		saver.init(data);
		DriveModel dm = saver.getDriveModel();
		// drive model created root and project correct
		Assert.assertNotNull(dm);
		// project is created
		try {
			project = NeoServiceFactory.getInstance().getNewProjectService()
					.findProject("newProject");
		} catch (AWEException e) {
			// TODO: log
			fail();
		}
		Assert.assertNotNull(project);
		// root created
		try {
			rootDataset = NeoServiceFactory
					.getInstance()
					.getNewDatasetService()
					.findDataset(project, "newRoot", DatasetTypes.DRIVE,
							DriveTypes.ROMES);
		} catch (AWEException e) {
			// TODO: log
			fail();
		}
		Assert.assertNotNull(rootDataset);
		//
		Assert.assertEquals("newRoot", dm.getName());
	}

	@Test
	public void testSave() {
		// preset
		// for each line a measurement is created
		fail("Not yet implemented");
	}

	@Test
	public void testGetMetaData() {
		// metadata not null
		// metadata correct
		fail("Not yet implemented");
	}

}
