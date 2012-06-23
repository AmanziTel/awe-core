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

package org.amanzi.neo.services.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.ProjectModel.DistributionItem;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProjectModelTest extends AbstractNeoServiceTest {

    private static Logger LOGGER = Logger.getLogger(ProjectModelTest.class);

    private static final String[] NETWORK_STRUCTURE_NODE_TYPES = new String[] {NetworkElementNodeType.BSC.getId(),
            NetworkElementNodeType.CITY.getId(), NetworkElementNodeType.SITE.getId(), NetworkElementNodeType.SECTOR.getId()};

    private static int count = 0;

    private ProjectModel model;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();

        new LogStarter().earlyStartup();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
    }

    @Before
    public final void before() {
        count++;
        model = new ProjectModel("project" + count);
    }

    @Test
    public void testProjectModel() {
        LOGGER.debug("start testProjectModel()");
        model = new ProjectModel("project");
        // object returned not null
        Assert.assertNotNull(model);
        // root node correct
        Assert.assertNotNull(model.getRootNode());
        Assert.assertEquals("project", model.getRootNode().getProperty(AbstractService.NAME, null));
        // name correct
        Assert.assertEquals("project", model.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProjectModelNameNull() {
        LOGGER.debug("start testProjectModelNameNull()");
        String nl = null;
        model = new ProjectModel(nl);
        // exception
    }

    @Test
    public void testCreateDatasetStringIDriveType() {
        LOGGER.debug("start testCreateDatasetStringIDriveType()");

        IDriveModel dm = model.createDriveModel("dataset", DriveTypes.values()[0]);

        // object returned not null
        Assert.assertNotNull(dm);
        Assert.assertNotNull(dm.getRootNode());
        // name correct
        Assert.assertEquals("dataset", dm.getName());
        // drive type correct
        Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
    }

    @Test
    public void testCreateDatasetStringIDriveTypeINodeType() {
        LOGGER.debug("start testCreateDatasetStringIDriveTypeINodeType()");

        IDriveModel dm;
        try {
            dm = model.createDriveModel("dataset", DriveTypes.values()[0], DriveNodeTypes.values()[0]);

            // object returned not null
            Assert.assertNotNull(dm);
            Assert.assertNotNull(dm.getRootNode());
            // name correct
            Assert.assertEquals("dataset", dm.getName());
            // drive type correct
            Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
            // primary type correct
            Assert.assertEquals(DriveNodeTypes.values()[0], dm.getPrimaryType());
        } catch (AWEException e) {
            Assert.fail("testCreateDatasetStringIDriveTypeINodeType exception while create driveModel ");
        }
    }

    @Test
    public void testFindDataset() {
        LOGGER.debug("start testFindDataset()");
        model.createDriveModel("dataset", DriveTypes.values()[0]);

        IDriveModel dm = model.findDriveModel("dataset", DriveTypes.values()[0]);
        // object returned not null
        Assert.assertNotNull(dm);
        Assert.assertNotNull(dm.getRootNode());
        // name correct
        Assert.assertEquals("dataset", dm.getName());
        // drive type correct
        Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
    }

    @Test
    public void testFindDatasetNoDataset() {
        LOGGER.debug("start testFindDatasetNoDataset()");
        IDriveModel dm = model.findDriveModel("dataset", DriveTypes.values()[0]);
        // object returned is null
        Assert.assertNull(dm);
    }

    @Test
    public void testGetDatasetStringIDriveType() {
        LOGGER.debug("start testGetDatasetStringIDriveType()");
        // dataset exists
        model.createDriveModel("dataset", DriveTypes.values()[0]);

        IDriveModel dm = model.getDriveModel("dataset", DriveTypes.values()[0]);
        // object returned not null
        Assert.assertNotNull(dm);
        Assert.assertNotNull(dm.getRootNode());
        // name correct
        Assert.assertEquals("dataset", dm.getName());
        // drive type correct
        Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
    }

    @Test
    public void testGetDatasetStringIDriveTypeNoDataset() {
        LOGGER.debug("start testGetDatasetStringIDriveTypeNoDataset()");
        // dataset !exists

        IDriveModel dm = model.getDriveModel("dataset", DriveTypes.values()[0]);
        // object returned not null
        Assert.assertNotNull(dm);
        Assert.assertNotNull(dm.getRootNode());
        // name correct
        Assert.assertEquals("dataset", dm.getName());
        // drive type correct
        Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
    }

    @Test
    public void testGetDatasetStringIDriveTypeINodeType() {
        LOGGER.debug("start testGetDatasetStringIDriveTypeINodeType()");
        // dataset exists
        try {
            model.createDriveModel("dataset", DriveTypes.values()[0], DriveNodeTypes.values()[0]);

            IDriveModel dm = model.getDrive("dataset", DriveTypes.values()[0], DriveNodeTypes.values()[0]);
            // object returned not null
            Assert.assertNotNull(dm);
            Assert.assertNotNull(dm.getRootNode());
            // name correct
            Assert.assertEquals("dataset", dm.getName());
            // drive type correct
            Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
            // type correct
            Assert.assertEquals(DatasetTypes.DRIVE, dm.getType());
            // primary type correct
            Assert.assertEquals(DriveNodeTypes.values()[0], dm.getPrimaryType());
        } catch (AWEException e) {
            Assert.fail("testGetDatasetStringIDriveTypeINodeType exception  ");
        }
    }

    @Test
    public void testGetDatasetStringIDriveTypeINodeTypeNoDataset() {
        LOGGER.debug("start testGetDatasetStringIDriveTypeINodeTypeNoDataset()");
        // dataset !exists

        IDriveModel dm;
        try {
            dm = model.getDrive("dataset", DriveTypes.values()[0], DriveNodeTypes.values()[0]);

            // object returned not null
            Assert.assertNotNull(dm);
            Assert.assertNotNull(dm.getRootNode());
            // name correct
            Assert.assertEquals("dataset", dm.getName());
            // drive type correct
            Assert.assertEquals(DriveTypes.values()[0], dm.getDriveType());
            // primary type correct
            Assert.assertEquals(DriveNodeTypes.values()[0], dm.getPrimaryType());
        } catch (AWEException e) {
            // TODO Handle AWEException
            Assert.fail("testGetDatasetStringIDriveTypeINodeType exception while try to get model  ");
        }
    }

    @Test
    public void testCreateNetwork() throws Exception {
        LOGGER.debug("start testCreateNetwork()");
        INetworkModel nm = model.createNetwork("network");

        // object returned not null
        Assert.assertNotNull(nm);
        Assert.assertNotNull(nm.getRootNode());
        // name correct
        Assert.assertEquals("network", nm.getName());
    }

    @Test
    public void testFindNetwork() throws Exception {
        LOGGER.debug("start testFindNetwork()");
        model.createNetwork("network");

        INetworkModel nm = model.findNetwork("network");
        // object returned not null
        Assert.assertNotNull(nm);
        Assert.assertNotNull(nm.getRootNode());
        // name correct
        Assert.assertEquals("network", nm.getName());
    }

    @Test
    public void testFindNetworkNoNetwork() throws Exception {
        LOGGER.debug("start testFindNetworkNoNetwork()");
        INetworkModel nm = model.findNetwork("network");
        // object returned is null
        Assert.assertNull(nm);
    }

    @Test
    public void testGetNetwork() throws Exception {
        LOGGER.debug("start testGetNetwork()");
        // network exists
        model.createNetwork("network");

        INetworkModel nm = model.getNetwork("network");
        // object returned not null
        Assert.assertNotNull(nm);
        Assert.assertNotNull(nm.getRootNode());
        // name correct
        Assert.assertEquals("network", nm.getName());
    }

    @Test
    public void testGetNetworkNoNetwork() throws Exception {
        LOGGER.debug("start testGetNetworkNoNetwork()");
        // network !exists
        INetworkModel nm = model.getNetwork("network");
        // object returned not null
        Assert.assertNotNull(nm);
        Assert.assertNotNull(nm.getRootNode());
        // name correct
        Assert.assertEquals("network", nm.getName());
    }

    @Test
    public void checkEmptyDistributionItemList() throws Exception {
        assertTrue("List of distributions should be empty", model.getAllDistributionalModels().isEmpty());
    }

    @Test
    public void checkNotNullDistributionItemList() throws Exception {
        assertNotNull("List of distributions should not be null", model.getAllDistributionalModels());
    }

    private void createNetwork() throws Exception {
        NetworkModel nm = (NetworkModel)model.createNetwork("test");
        List<INodeType> networkStructure = new LinkedList<INodeType>();
        for (String nodeType : NETWORK_STRUCTURE_NODE_TYPES) {
            networkStructure.add(NodeTypeManager.getType(nodeType));
        }
        nm.setCurrentNetworkStructure(networkStructure);
        nm.finishUp();
    }

    @Test
    public void checkDistributionItemsForNetworkNotEmpty() throws Exception {
        createNetwork();

        List<DistributionItem> items = model.getAllDistributionalModels();
        assertFalse("DistributionItems list cannot be empty", items.isEmpty());
    }

    @Test
    public void checkDistributionItemsSizeForNetwork() throws Exception {
        createNetwork();

        List<DistributionItem> items = model.getAllDistributionalModels();
        assertEquals("Unexpected size of Distribution Items list", NETWORK_STRUCTURE_NODE_TYPES.length, items.size());
    }

    @Test
    public void checkDistributionModelOfAllItems() throws Exception {
        createNetwork();

        List<DistributionItem> items = model.getAllDistributionalModels();
        DistributionItem firstItem = items.get(0);
        IDistributionalModel model = firstItem.getModel();

        for (DistributionItem item : items) {
            assertEquals("Unexpected DistributionalModel for Item", model, item.getModel());
        }
    }

    @Test
    public void checkTypesOfAllItems() throws Exception {
        createNetwork();

        List<DistributionItem> items = model.getAllDistributionalModels();

        for (int i = 0; i < NETWORK_STRUCTURE_NODE_TYPES.length; i++) {
            String expectedNodeType = NETWORK_STRUCTURE_NODE_TYPES[i];
            String actualNodeType = items.get(i).getNodeType().getId();

            assertEquals("Unexepcted NodeType of Item", expectedNodeType, actualNodeType);
        }
    }

}
