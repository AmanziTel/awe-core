package org.amanzi.neo.loader.core.newsaver;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.loader.core.saver.impl.RomesDriveModelSaver;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.testing.AbstractAWETest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;

public class RomesDriveModelSaverTest extends AbstractAWETest {

    private static RomesDriveModelSaver<BaseTransferData> saver;
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
        saver = new RomesDriveModelSaver<BaseTransferData>();
        project = NeoServiceFactory.getInstance().getNewProjectService().getProject(projectName);
        rootDataset = NeoServiceFactory.getInstance().getNewDatasetService()
                .getDataset(project, rootName, DatasetTypes.DRIVE, DriveTypes.ROMES, DriveNodeTypes.M);
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

        saver.init(data);
        DriveModel dm = saver.getDriveModel();
        // drive model created root and project correct
        Assert.assertNotNull(dm);
        Assert.assertEquals(rootDataset, dm.getRootNode());
        Assert.assertEquals(rootName, dm.getName());
        // file node created
        Assert.assertNotNull(graphDatabaseService.index()
                .forNodes(NeoServiceFactory.getInstance().getNewDatasetService().getIndexKey(rootDataset, DriveNodeTypes.FILE))
                .get(NewAbstractService.NAME, filename));
    }

    @Test
    public void testInitNoData() {
        // preset
        data.setProjectName("newProject");
        data.setFileName(filename);
        data.setRootName("newRoot");

        saver.init(data);
        DriveModel dm = saver.getDriveModel();
        // drive model created root and project correct
        Assert.assertNotNull(dm);
        // project is created
        try {
            project = NeoServiceFactory.getInstance().getNewProjectService().findProject("newProject");
        } catch (AWEException e) {
            // TODO: log
            fail();
        }
        Assert.assertNotNull(project);
        // root created
        try {
            rootDataset = NeoServiceFactory.getInstance().getNewDatasetService()
                    .findDataset(project, "newRoot", DatasetTypes.DRIVE, DriveTypes.ROMES);
        } catch (AWEException e) {
            // TODO: log
            fail();
        }
        Assert.assertNotNull(rootDataset);
        //
        Assert.assertEquals("newRoot", dm.getName());
        // file node created
        Assert.assertNotNull(graphDatabaseService.index()
                .forNodes(NewAbstractService.getIndexKey(rootDataset, DriveNodeTypes.FILE)).get(NewAbstractService.NAME, filename));
    }

    @Test
    public void testSave() {
        // preset
        data.setProjectName(projectName);
        data.setFileName(filename);
        data.setRootName(rootName);
        saver.init(data);

        data = new BaseTransferData();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DriveModel.LATITUDE, (long)(Math.random() * Integer.MAX_VALUE));
        map.put(DriveModel.LONGITUDE, (long)(Math.random() * Integer.MAX_VALUE));
        map.put(DriveModel.TIMESTAMP, (long)(Math.random() * Integer.MAX_VALUE));
        map.put("some", "fake");
        map.put("para", "meters");
        for (String key : map.keySet()) {
            data.put(key, String.valueOf(map.get(key)));
        }

        saver.save(data);
        // a measurement is created
        DriveModel dm = saver.getDriveModel();
        Iterable<IDataElement> ms = dm.getMeasurements(filename);
        Node m = ((DataElement)ms.iterator().next()).getNode();
        // node not null
        Assert.assertNotNull(m);
        // properties set correctly
        Assert.assertEquals(map.get(DriveModel.TIMESTAMP), m.getProperty(DriveModel.TIMESTAMP, null));
        Assert.assertEquals(map.get("some"), m.getProperty("some", null));
        Assert.assertEquals(map.get("para"), m.getProperty("para", null));
        // Location node created
        Node l = ((DataElement)dm.getLocation(new DataElement(m))).getNode();
        Assert.assertNotNull(l);
        Assert.assertEquals(map.get(DriveModel.LATITUDE), l.getProperty(DriveModel.LATITUDE, null));
        Assert.assertEquals(map.get(DriveModel.LONGITUDE), l.getProperty(DriveModel.LONGITUDE, null));
        Assert.assertEquals(map.get(DriveModel.TIMESTAMP), l.getProperty(DriveModel.TIMESTAMP, null));
    }

    @Test
    public void testGetMetaData() {
        Iterable<MetaData> it = saver.getMetaData();
        MetaData md = it.iterator().next();
        // metadata not null
        Assert.assertNotNull(md);
        // metadata correct
        Assert.assertEquals(DriveTypes.ROMES.name().toLowerCase(), md.getPropertyValues(MetaData.SUB_TYPE));
        Assert.assertEquals("dataset", md.getType());
    }

}
