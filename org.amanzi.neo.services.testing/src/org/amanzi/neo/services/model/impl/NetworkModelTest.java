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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.AbstractService.FilterNodeType;
import org.amanzi.neo.services.CorrelationServiceTest;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.filters.ExpressionType;
import org.amanzi.neo.services.filters.FilterType;
import org.amanzi.neo.services.filters.INamedFilter;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.amanzi.neo.services.model.impl.RenderableModel.GisModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class NetworkModelTest extends AbstractNeoServiceTest {

    private static Logger LOGGER = Logger.getLogger(CorrelationServiceTest.class);

    private static DatasetService dsServ;
    private static ProjectService prServ;
    private Node network;
    private Node project;
    private static int count = 0;

    private NetworkModel model;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();

        dsServ = NeoServiceFactory.getInstance().getDatasetService();
        prServ = NeoServiceFactory.getInstance().getProjectService();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
    }

    @Before
    public final void before() {
        try {
            count++;
            project = prServ.createProject("project" + count);
            network = dsServ.createDataset(project, "network", DatasetTypes.NETWORK);
            model = new NetworkModel(network);
        } catch (AWEException e) {
            LOGGER.error("Could not create test nodes.", e);
        }
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
        assertEquals(min_lat, model.getMinLatitude(), DEFAULT_DOUBLE);
        assertEquals(max_lat, model.getMaxLatitude(), DEFAULT_DOUBLE);
        assertEquals(min_lon, model.getMinLongitude(), DEFAULT_DOUBLE);
        assertEquals(max_lon, model.getMaxLongitude(), DEFAULT_DOUBLE);
    }

    @Test
    public void testNetworkModelIDataElement() {

        String name = network.getProperty(AbstractService.NAME).toString();
        DataElement root = new DataElement(network);
        DataElement parent = new DataElement(project);

        NetworkModel nm;
        try {
            nm = new NetworkModel(parent, root, name, null);
        } catch (InvalidDatasetParameterException e) {
            // TODO Handle InvalidDatasetParameterException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (DatasetTypeParameterException e) {
            // TODO Handle DatasetTypeParameterException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (DuplicateNodeNameException e) {
            // TODO Handle DuplicateNodeNameException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        // object created not null
        assertNotNull(nm);
        // root node correct
        assertEquals(network, nm.getRootNode());
        // name correct
        assertEquals(name, nm.getName());
    }

    @Test
    public void testNetworkModelCRS() {
        String name = "network1";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("some", "param");
        DataElement root = new DataElement(params);
        DataElement parent = new DataElement(project);

        NetworkModel nm;
        try {
            nm = new NetworkModel(parent, root, name, "EPSG:4326");
            nm.finishUp();
        } catch (InvalidDatasetParameterException e) {
            // TODO Handle InvalidDatasetParameterException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (DatasetTypeParameterException e) {
            // TODO Handle DatasetTypeParameterException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (DuplicateNodeNameException e) {
            // TODO Handle DuplicateNodeNameException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        CoordinateReferenceSystem crs = nm.getCRS();

        NetworkModel testModel = null;
        try {
            testModel = new NetworkModel(nm.getRootNode());
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        Assert.assertEquals(crs, testModel.getCRS());
    }

    @Test
    public void testCreateElement() {
        IDataElement parentElement = new DataElement(network);
        for (INodeType type : NetworkElementNodeType.values()) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.TYPE, type.getId());
            params.put(AbstractService.NAME, type.getId());
            params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
            DataElement element = new DataElement(params);

            IDataElement testElement;
            try {
                testElement = model.createElement(parentElement, element);
            } catch (AWEException e) {
                // TODO Handle AWEException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            // object returned not null
            assertNotNull(testElement);
            // underlying node not null
            assertNotNull(((DataElement)testElement).getNode());
            // properties set
            for (String key : params.keySet()) {
                assertEquals(params.get(key), testElement.get(key));
            }
            parentElement = testElement;
        }
    }

    @Test
    public void testDeleteElement() {
        ArrayList<IDataElement> allDataElements = new ArrayList<IDataElement>();
        ArrayList<IDataElement> paramsDataElements = new ArrayList<IDataElement>();
        IDataElement parentElement = new DataElement(network);
        for (INodeType type : NetworkElementNodeType.values()) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.TYPE, type.getId());
            params.put(AbstractService.NAME, type.getId());
            params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
            DataElement element = new DataElement(params);
            paramsDataElements.add(element);

            IDataElement testElement;
            try {
                testElement = model.createElement(parentElement, element);
            } catch (AWEException e) {
                LOGGER.error("Erorr while trying to create element" + element, e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            // object returned not null
            assertNotNull(testElement);
            // underlying node not null
            assertNotNull(((DataElement)testElement).getNode());
            // properties set
            for (String key : params.keySet()) {
                assertEquals(params.get(key), testElement.get(key));
            }
            parentElement = testElement;
            allDataElements.add(parentElement);
        }
        int index = 2;
        IDataElement elementToDelete = allDataElements.get(index);
        try {
            model.deleteElement(elementToDelete);
        } catch (AWEException e) {
            LOGGER.error("Erorr while trying to delete element" + elementToDelete, e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        ArrayList<IDataElement> foundElements = new ArrayList<IDataElement>();
        for (IDataElement dataElement : paramsDataElements) {

            IDataElement foundElement;
            try {
                foundElement = model.findElement(((DataElement)dataElement));
            } catch (AWEException e) {
                LOGGER.error("Erorr while trying to found element" + dataElement, e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            if (foundElement != null) {
                foundElements.add(foundElement);
            }
        }

        assertEquals(foundElements.size(), index);
    }

    @Test
    public void testFindElement() {
        IDataElement parentElement = new DataElement(network);
        for (INodeType type : NetworkElementNodeType.values()) {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.TYPE, type.getId());
            params.put(AbstractService.NAME, type.getId());
            params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
            DataElement element = new DataElement(params);

            IDataElement newElement;
            try {
                newElement = model.createElement(parentElement, element);
            } catch (AWEException e) {
                LOGGER.error("Erorr while trying to Create element" + element, e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            parentElement = newElement;
        }

        for (INodeType type : NetworkElementNodeType.values()) {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.TYPE, type.getId());
            params.put(AbstractService.NAME, type.getId());
            IDataElement testElement;
            try {
                testElement = model.findElement(new DataElement(params));
            } catch (AWEException e) {
                LOGGER.error("Erorr while trying to found element", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            // object returned not null
            assertNotNull(testElement);
            // underlying node not null
            assertNotNull(((DataElement)testElement).getNode());

        }
    }

    @Test
    public void testGetElement() {
        IDataElement parentElement = new DataElement(network);
        for (INodeType type : NetworkElementNodeType.values()) {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.TYPE, type.getId());
            params.put(AbstractService.NAME, type.getId());
            params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
            DataElement element = new DataElement(params);

            IDataElement newElement;
            try {
                newElement = model.createElement(parentElement, element);
            } catch (AWEException e) {
                LOGGER.error("Erorr while trying to create element" + element, e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            parentElement = newElement;
        }

        parentElement = new DataElement(network);
        for (INodeType type : NetworkElementNodeType.values()) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.TYPE, type.getId());
            params.put(AbstractService.NAME, type.getId());
            IDataElement testElement;
            try {
                testElement = model.getNetworkElement(parentElement, new DataElement(params));
            } catch (AWEException e) {
                LOGGER.error("Erorr while trying to get element", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            // object returned not null
            assertNotNull(testElement);
            // underlying node not null
            assertNotNull(((DataElement)testElement).getNode());

            parentElement = testElement;
        }
    }

    @Test
    public void testGetElementNoElement() {
        IDataElement parentElement = new DataElement(network);
        for (INodeType type : NetworkElementNodeType.values()) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AbstractService.TYPE, type.getId());
            params.put(AbstractService.NAME, type.getId());
            IDataElement testElement;
            try {
                testElement = model.getNetworkElement(parentElement, new DataElement(params));
            } catch (AWEException e) {
                LOGGER.error("Erorr while trying to get element", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            // object returned not null
            assertNotNull(testElement);
            // underlying node not null
            assertNotNull(((DataElement)testElement).getNode());

            parentElement = testElement;
        }
    }

    @Test
    public void testGetCorrelationModels() {
        List<Node> datasets = new ArrayList<Node>();
        try {
            for (int i = 0; i < 4; i++) {
                Node dataset = dsServ.createDataset(project, "network" + i, DatasetTypes.DRIVE, DriveTypes.values()[0],
                        DriveNodeTypes.M);
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
                Node dsNode = ((DataElement)mod.getDataset()).getNode();
                Assert.assertNotNull(dsNode);
                Assert.assertTrue(datasets.contains(dsNode));

                Node nwNode = ((DataElement)mod.getNetwork()).getNode();
                Assert.assertNotNull(nwNode);
                Assert.assertEquals(network, nwNode);
            }
        } catch (AWEException e) {
            LOGGER.error("Could not get correlation models.", e);
            fail();
        }
    }

    @Test
    public void testGetN2NModels() {
        List<Node> n2ns = new ArrayList<Node>();
        for (int i = 0; i < 4; i++) {
            NodeToNodeRelationshipModel mdl;
            try {
                mdl = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name" + i,
                        NetworkElementNodeType.SECTOR);
            } catch (AWEException e) {
                // TODO Handle DatabaseException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            n2ns.add(mdl.getRootNode());
        }

        Iterable<INodeToNodeRelationsModel> it = null;
        try {
            it = model.getNodeToNodeModels();
        } catch (AWEException e) {
            LOGGER.error("Could not get correlation models.", e);
            fail();
        }
        Assert.assertNotNull(it);
        Assert.assertTrue(it.iterator().hasNext());
        for (INodeToNodeRelationsModel mod : it) {
            Node n2nRoot = ((NodeToNodeRelationshipModel)mod).getRootNode();
            Assert.assertTrue(n2ns.contains(n2nRoot));
        }
    }

    @Test
    public void testGetChildren() {
        Mockery context = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        final DatasetService dsS = context.mock(DatasetService.class);

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

        final NetworkService nwS = context.mock(NetworkService.class);

        for (final NetworkElementNodeType type : NetworkElementNodeType.values()) {
            // expectations
            context.checking(new Expectations() {
                {
                    atLeast(1).of(nwS).findAllNetworkElements(network, type);
                }
            });
        }

        // execute
        model.setNetworkService(nwS);

        for (NetworkElementNodeType type : NetworkElementNodeType.values()) {
            model.getAllElementsByType(type);
        }

        // verify
        context.assertIsSatisfied();
    }

    @Test
    public void testFindElementByPropertyValue() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        params.put(AbstractService.NAME, NetworkElementNodeType.SITE.getId());
        params.put("lat", 1d);
        params.put("lon", 2d);
        IDataElement parentElement;
        try {
            parentElement = model.createElement(new DataElement(network), params);
        } catch (AWEException e1) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }
        params.clear();
        params.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
        params.put(AbstractService.NAME, NetworkElementNodeType.SECTOR.getId());
        params.put(NetworkService.BCCH, 12);
        params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
        DataElement element = new DataElement(params);

        IDataElement newElement;
        try {
            newElement = model.createElement(parentElement, element);
        } catch (AWEException e) {
            LOGGER.error("Erorr while trying to Create element" + element, e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        parentElement = newElement;

        Set<IDataElement> testElement;
        try {
            testElement = model.findElementByPropertyValue(NetworkElementNodeType.SECTOR, NetworkService.BCCH, 12);
        } catch (AWEException e) {
            LOGGER.error("Erorr while trying to found element", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        // object returned not null
        assertNotNull(testElement);
        // underlying node not null
        assertNotNull(((DataElement)testElement.iterator().next()).getNode());

    }

    @Test
    public void testGetClosestElement() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        params.put(AbstractService.NAME, NetworkElementNodeType.SITE.getId());
        params.put("lat", 107d);
        params.put("lon", 24d);
        IDataElement site1;
        IDataElement site2;
        try {
            site1 = model.createElement(new DataElement(network), params);
            params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
            params.put(AbstractService.NAME, NetworkElementNodeType.SITE.getId());
            params.put("lat", 105d);
            params.put("lon", 34d);
            site2 = model.createElement(new DataElement(network), params);
        } catch (AWEException e1) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }
        Set<IDataElement> testSet = new HashSet<IDataElement>();
        testSet.add(site2);
        IDataElement finded = model.getClosestElement(site1, testSet, Integer.MAX_VALUE);
        // object returned not null
        assertNotNull(finded);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void checkSetStructureOnFinishUp() throws Exception {
        NetworkService service = mock(NetworkService.class);

        model.setNetworkService(service);
        model.finishUp();

        verify(service).setNetworkStructure(eq(model.getRootNode()), any(List.class));
    }

    @Test
    public void testGetProject() {
        IProjectModel pModel = model.getProject();
        Assert.assertTrue("Same nodes expected ", pModel.getRootNode().equals(project));
    }

    @Test
    public void testGetParentModel() {
        IModel pModel;
        try {
            pModel = model.getParentModel();
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        Assert.assertTrue("Same nodes expected ", pModel.getRootNode().equals(project));
    }

    @Test
    public void testSetCurrentNodeToNodeRelationshipModel() throws AWEException {
        NetworkService networkService = mock(NetworkService.class);

        model.setNetworkService(networkService);
        model.setCurrentNodeToNodeRelationshipModel(new NodeToNodeRelationshipModel(new DataElement(network),
                N2NRelTypes.NEIGHBOUR, "name", NetworkElementNodeType.SECTOR));

        verify(networkService).setCurrentNodeToNodeModelName(any(Node.class), any(String.class));
    }

    @Test
    public void testGetCurrentNodeToNodeRelationshipModel() throws Exception {
        INodeToNodeRelationsModel expectedModel = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR,
                "name", NetworkElementNodeType.SECTOR);

        NetworkModel networkModel = mock(NetworkModel.class);

        networkModel.setCurrentNodeToNodeRelationshipModel(expectedModel);
        INodeToNodeRelationsModel actualModel = networkModel.getCurrentNodeToNodeRelationshipModel();

        when(networkModel.getCurrentNodeToNodeRelationshipModel()).thenReturn(actualModel);

        Assert.assertNull(model.getCurrentNodeToNodeRelationshipModel());

        model.setCurrentNodeToNodeRelationshipModel(expectedModel);

        Assert.assertEquals(expectedModel.getName(), model.getCurrentNodeToNodeRelationshipModel().getName());

    }

    @Test
    public void testSetStarToolSelectedModel() throws AWEException {
        NetworkService service = mock(NetworkService.class);
        model.setNetworkService(service);
        model.setStarToolSelectedModel();
        verify(service).setStarToolSelectedModelName(any(Node.class), any(String.class));
    }

    @Test
    public void testGetStarToolSelectedModel() throws AWEException {
        Assert.assertNull(model.getStarToolSelectedModel());
        model.setStarToolSelectedModel();
        INetworkModel testModel = (INetworkModel)model.getStarToolSelectedModel();
        Assert.assertNotNull(testModel);

        Node testModelNode = dsServ.createDataset(project, "star_tool_network", DatasetTypes.NETWORK);

        INetworkModel expectedModel = new NetworkModel(testModelNode);
        expectedModel.setStarToolSelectedModel();
        testModel = (INetworkModel)expectedModel.getStarToolSelectedModel();
        Assert.assertNotNull(testModel);
    }

    @Test
    public void testRemoveStarToolSelectedModel() throws AWEException {
        Assert.assertNull(model.getStarToolSelectedModel());
        model.setStarToolSelectedModel();
        Assert.assertNotNull(model.getStarToolSelectedModel());
        model.removeStarToolSelectedModel();
        Assert.assertNull(model.getStarToolSelectedModel());
    }

    // TODO RenderableModel Tests
    @Test
    public void checkRenderableModelGetDescription() throws AWEException {
        NetworkModel model = new NetworkModel(network);
        Node node = mock(Node.class);
        String expected = "expectedDescription";
        when(node.getProperty(RenderableModel.DESCRIPTION, StringUtils.EMPTY)).thenReturn("");
        model.rootNode = node;
        Assert.assertTrue(model.getDescription().isEmpty());

        when(node.getProperty(RenderableModel.DESCRIPTION, StringUtils.EMPTY)).thenReturn(expected);

        Assert.assertEquals(expected, model.getDescription());
    }

    @Test
    public void checkGetBounds() throws AWEException {
        NetworkModel model = new NetworkModel(network);
        GisModel gisModel = mock(GisModel.class);
        ReferencedEnvelope envelope = new ReferencedEnvelope();
        when(gisModel.getBounds()).thenReturn(envelope);
        CoordinateReferenceSystem system = mock(CoordinateReferenceSystem.class);
        INamedFilter filter = mock(INamedFilter.class);
        Node node = mock(Node.class);
        gisModel.setCRS(system);
        when(gisModel.getCrs()).thenReturn(system);
        when(gisModel.getRootNode()).thenReturn(node);
        when(gisModel.getFilter()).thenReturn(filter);
        when(gisModel.getType()).thenReturn(DatasetTypes.GIS);
        when(gisModel.getBounds()).thenReturn(envelope);
        Assert.assertEquals(system, gisModel.getCrs());
        Assert.assertEquals(filter, gisModel.getFilter());
        Assert.assertEquals(node, gisModel.getRootNode());
        Assert.assertEquals(DatasetTypes.NETWORK, model.getType());
        Assert.assertEquals(envelope, gisModel.getBounds());
        Assert.assertNotNull(model.getBounds());
    }

    @Test
    public void checkSetSelectedElements() throws AWEException {
        NetworkModel model = new NetworkModel(network);
        IDataElement dataElement = mock(IDataElement.class);
        List<IDataElement> elements = new ArrayList<IDataElement>(2);
        elements.add(dataElement);
        elements.add(dataElement);
        model.setSelectedDataElementToList(dataElement);

        Assert.assertTrue(model.getSelectedElements().size() == 1);

        model.setSelectedDataElements(elements);

        Assert.assertTrue(model.getSelectedElements().size() == 3);

        for (IDataElement actualElement : model.getSelectedElements()) {
            Assert.assertEquals(dataElement, actualElement);
        }

        model.clearSelectedElements();

        Assert.assertTrue(model.getSelectedElements().isEmpty());
    }

    @Test
    public void checkDrawNeighbors() throws AWEException {
        NetworkModel model = new NetworkModel(network);
        model.setDrawNeighbors(true);
        Assert.assertTrue(model.isDrawNeighbors());
        model.setDrawNeighbors(false);
        Assert.assertFalse(model.isDrawNeighbors());
    }

    @Test
    public void checkGetCRS() throws AWEException {
        NetworkModel model = new NetworkModel(network);
        CoordinateReferenceSystem crs = mock(CoordinateReferenceSystem.class);
        model.setCRS(crs);
        Assert.assertNotNull(model.getCrs());
        Assert.assertEquals(crs, model.getCrs());

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void checkGetAllGisModels() throws AWEException {
        NetworkModel model = new NetworkModel(network);
        String name = "network";
        DatasetService service = mock(DatasetService.class);
        Node rootNode = mock(Node.class);
        List<Node> gisNodes = new ArrayList<Node>();
        gisNodes.add(rootNode);
        when(service.getAllGisByDataset(rootNode)).thenReturn(gisNodes);
        when(rootNode.getProperty(AbstractService.TYPE)).thenReturn("filter");
        when(rootNode.getProperty(DatasetService.NAME)).thenReturn(name);
        when(rootNode.getProperty(RenderableModel.CRS_NAME, StringUtils.EMPTY)).thenReturn("");
        Iterable iterable = mock(Iterable.class);
        Iterator iterator = mock(Iterator.class);
        when(service.loadFilters(rootNode)).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(iterator);
        Assert.assertNotNull(model.getAllGisModels());

        model.findGisByName(name);
        model.findGisByName("not_equals_name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkFindGisByName() throws DatabaseException {
        model.findGisByName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkFindGisModelByNameWithNull() throws DatabaseException {
        model.findGisByName(null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void checkAddLayer() throws AWEException {
        NetworkModel model = new NetworkModel(network);
        String name = "layer_name";
        Node gisRoot = mock(Node.class);
        DatasetService service = mock(DatasetService.class);
        Iterable iterable = mock(Iterable.class);
        Iterator iterator = mock(Iterator.class);
        when(gisRoot.getProperty(AbstractService.TYPE)).thenReturn("filter");
        when(gisRoot.getProperty(DatasetService.NAME)).thenReturn(name);
        when(gisRoot.getProperty(RenderableModel.CRS_NAME, StringUtils.EMPTY)).thenReturn("");
        when(service.loadFilters(gisRoot)).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(iterator);
        String type = (String)gisRoot.getProperty(AbstractService.TYPE);
        when(type.equals(FilterNodeType.FILTER.getId())).thenReturn(true);
        when(service.getGisNodeByDataset(model.rootNode, name)).thenReturn(gisRoot);
        INamedFilter filter = getFilterMock(name);
        model.addLayer(name, filter);
    }

    @Test
    public void checkGetFilter() {

    }

    @Test
    public void checkSetCRS() {

    }

    @Test
    public void checkGetType() {

    }

    /**
     * findAllFitlers Get INamedFilter mock object with specified logic
     * 
     * @param parameter parameter
     * @return INamedFilter mock
     */
    private INamedFilter getFilterMock(String parameter) {
        INamedFilter filter = mock(INamedFilter.class);
        when(filter.getExpressionType()).thenReturn(ExpressionType.AND);
        when(filter.getFilterType()).thenReturn(FilterType.MORE);
        when(filter.getPropertyName()).thenReturn(parameter);
        return filter;
    }
}
