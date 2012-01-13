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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class NodeToNodeRelationshipModelTest extends AbstractNeoServiceTest {

    private static final Logger LOGGER = Logger.getLogger(NodeToNodeRelationshipModelTest.class);

    private Transaction tx;
    private static int count = 0;

    private static DatasetService dsServ;
    private static ProjectService prServ;
    private Node network;
    private static Node project;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();

        new LogStarter().earlyStartup();

        dsServ = NeoServiceFactory.getInstance().getDatasetService();
        prServ = NeoServiceFactory.getInstance().getProjectService();
        project = prServ.createProject("project");
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
            network = dsServ.createDataset(project, "network" + count, DatasetTypes.NETWORK);
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
    public void testNode2NodeRelationshipModel() {
        // create new model
        NodeToNodeRelationshipModel model;
        try {
            model = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name",
                    NetworkElementNodeType.SECTOR);
        } catch (AWEException e) {
            LOGGER.error("Error while trying to create element", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        // object returned is not null
        Assert.assertNotNull(model);
        // relation type is correct
        Assert.assertEquals(N2NRelTypes.NEIGHBOUR, model.getNodeToNodeRelationsType());
    }

    @Test
    public void testNode2NodeRelationshipModelNotRecreated() {
        try {
            new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name", NetworkElementNodeType.SECTOR);
        } catch (AWEException e) {
            LOGGER.error("Error while trying to create element", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        // create new model with the same parameters
        NodeToNodeRelationshipModel model;
        try {
            model = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name",
                    NetworkElementNodeType.SECTOR);
        } catch (AWEException e) {
            LOGGER.error("Error while trying to create element", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        // object returned is not null
        Assert.assertNotNull(model);
        // n2n root not recreated
        Assert.assertNotNull(network.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.OUTGOING));
    }

    @Test
    public void testLinkNode() throws Exception {
        // create network structure
        NetworkModel nm = new NetworkModel(network);
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        params.put(AbstractService.NAME, NetworkElementNodeType.SITE.getId());
        IDataElement site;
        try {
            site = nm.createElement(new DataElement(nm.getRootNode()), new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        params.put(AbstractService.NAME, "sector1");
        params.put(NetworkService.CELL_INDEX, "ci1");
        params.put(NetworkService.LOCATION_AREA_CODE, "lac1");
        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        IDataElement sector1;
        try {
            sector1 = nm.createElement(site, new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        params.put(AbstractService.NAME, "sector2");
        params.put(NetworkService.CELL_INDEX, "ci2");
        params.put(NetworkService.LOCATION_AREA_CODE, "lac2");
        IDataElement sector2;
        try {
            sector2 = nm.createElement(site, new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        NodeToNodeRelationshipModel model;
        try {
            model = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name",
                    NetworkElementNodeType.SECTOR);

            params = new HashMap<String, Object>();
            params.put(AbstractService.NAME, "neighbour");
            params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
            model.linkNode(sector1, sector2, params);

        } catch (AWEException e) {
            LOGGER.error("Error in testLinkNode ", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }// proxies created
        Node s1 = ((DataElement)sector1).getNode();
        Node s2 = ((DataElement)sector2).getNode();
        Node p1 = s1.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.BOTH).getOtherNode(s1);
        Node p2 = s2.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.BOTH).getOtherNode(s2);
        Assert.assertNotNull(p1);
        Assert.assertNotNull(p2);
        // relationship created
        Relationship rel = p1.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.OUTGOING);
        Assert.assertNotNull(rel);
        Assert.assertEquals(p2, rel.getOtherNode(p1));
        // properties set
        for (String key : params.keySet()) {
            Assert.assertEquals(params.get(key), rel.getProperty(key, null));
        }
        // chain exists
        Assert.assertTrue(chainExists(model.getRootNode(), p1));
        Assert.assertTrue(chainExists(model.getRootNode(), p2));
    }

    @Test
    public void testLinkFewNodes() throws Exception {
        // create network structure
        NetworkModel nm = new NetworkModel(network);
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        params.put(AbstractService.NAME, NetworkElementNodeType.SITE.getId());
        IDataElement site;
        try {
            site = nm.createElement(new DataElement(nm.getRootNode()), new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        params.put(AbstractService.NAME, "sector1");
        params.put(NetworkService.CELL_INDEX, "ci1");
        params.put(NetworkService.LOCATION_AREA_CODE, "lac1");
        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        IDataElement sector1;
        try {
            sector1 = nm.createElement(site, new DataElement(params));
        } catch (AWEException e1) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        params.put(AbstractService.NAME, "sector2");
        params.put(NetworkService.CELL_INDEX, "ci2");
        params.put(NetworkService.LOCATION_AREA_CODE, "lac2");
        IDataElement sector2;
        try {
            sector2 = nm.createElement(site, new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        params.put(AbstractService.NAME, "sector3");
        params.put(NetworkService.CELL_INDEX, "ci3");
        params.put(NetworkService.LOCATION_AREA_CODE, "lac3");
        IDataElement sector3;
        try {
            sector3 = nm.createElement(site, new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        NodeToNodeRelationshipModel model;
        try {
            model = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name",
                    NetworkElementNodeType.SECTOR);

            params = new HashMap<String, Object>();
            params.put(AbstractService.NAME, "neighbour");
            params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
            model.linkNode(sector1, sector2, params);

            params.put(DriveModel.TIMESTAMP, System.currentTimeMillis());
            model.linkNode(sector1, sector3, params);
        } catch (AWEException e) {
            LOGGER.error("Error in testLinkFewNodes ", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        // proxies created
        Node s1 = ((DataElement)sector1).getNode();
        Node s2 = ((DataElement)sector2).getNode();
        Node s3 = ((DataElement)sector3).getNode();

        Node p1 = s1.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.BOTH).getOtherNode(s1);
        Node p2 = s2.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.BOTH).getOtherNode(s2);
        Node p3 = s3.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.BOTH).getOtherNode(s3);

        Assert.assertNotNull(p1);
        Assert.assertNotNull(p2);
        Assert.assertNotNull(p3);
        // proxy not recreated
        Relationship rel2 = s1.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.OUTGOING);
        Relationship rel3 = s3.getSingleRelationship(N2NRelTypes.NEIGHBOUR, Direction.OUTGOING);
        boolean isExist = false;
        for (Relationship r : p3.getRelationships(N2NRelTypes.NEIGHBOUR)) {
            if (r.getOtherNode(p3) != null && r.getOtherNode(p3).equals(rel2.getEndNode())) {
                isExist = true;
            }
        }
        Assert.assertEquals(rel3.getEndNode(), p3);
        Assert.assertTrue(isExist);
        Assert.assertTrue(chainExists(model.getRootNode(), p1));
        Assert.assertTrue(chainExists(model.getRootNode(), p2));
        Assert.assertTrue(chainExists(model.getRootNode(), p3));

    }

    @Test
    public void testGetN2NRelatedElements() throws Exception {
        List<Relationship> relations = new ArrayList<Relationship>();
        final int relCount = 5;
        // create network structure

        NodeToNodeRelationshipModel model;
        try {
            model = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name",
                    NetworkElementNodeType.SECTOR);
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create model", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        NetworkModel nm = new NetworkModel(network);
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        params.put(AbstractService.NAME, NetworkElementNodeType.SITE.getId());
        IDataElement site;
        try {
            site = nm.createElement(new DataElement(nm.getRootNode()), new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        params.put(AbstractService.NAME, "sector");
        params.put(NetworkService.CELL_INDEX, "ci");
        params.put(NetworkService.LOCATION_AREA_CODE, "lac");
        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        IDataElement sector;
        try {
            sector = nm.createElement(site, new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        for (int i = 0; i < relCount; i++) {
            params = new HashMap<String, Object>();
            params.put(AbstractService.NAME, "sector" + i);
            params.put(NetworkService.CELL_INDEX, "ci" + i);
            params.put(NetworkService.LOCATION_AREA_CODE, "lac" + i);
            params.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
            IDataElement sect;
            try {
                sect = nm.createElement(site, new DataElement(params));
            } catch (AWEException e1) {
                LOGGER.error("Error while trying to create element", e1);
                throw (RuntimeException)new RuntimeException().initCause(e1);
            }

            Relationship rel = dsServ.createRelationship(model.getProxy(((DataElement)sector).getNode()),
                    model.getProxy(((DataElement)sect).getNode()), N2NRelTypes.NEIGHBOUR);

            relations.add(rel);

        }

        // all elements are returned
        Iterable<IDataElement> elements = model.getN2NRelatedElements(sector);
        int resCount = 0;
        for (IDataElement element : elements) {
            resCount++;
            Assert.assertTrue(relations.contains(((DataElement)element).getRelationship()));
        }
        Assert.assertTrue("Incorrect Relations Count: " + resCount, resCount == relCount);
    }

    @Test
    public void testGetServiceByProxy() throws Exception {
        List<Relationship> relations = new ArrayList<Relationship>();
        final int relCount = 5;
        // create network structure

        NodeToNodeRelationshipModel model;
        try {
            model = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name",
                    NetworkElementNodeType.SECTOR);
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create model", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        NetworkModel nm = new NetworkModel(network);
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(AbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        params.put(AbstractService.NAME, NetworkElementNodeType.SITE.getId());
        IDataElement site;
        try {
            site = nm.createElement(new DataElement(nm.getRootNode()), new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        params.put(AbstractService.NAME, "sector");
        params.put(NetworkService.CELL_INDEX, "ci");
        params.put(NetworkService.LOCATION_AREA_CODE, "lac");
        params.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
        IDataElement sector;
        try {
            sector = nm.createElement(site, new DataElement(params));
        } catch (AWEException e1) {
            LOGGER.error("Error while trying to create element", e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }

        for (int i = 0; i < relCount; i++) {
            params = new HashMap<String, Object>();
            params.put(AbstractService.NAME, "sector" + i);
            params.put(NetworkService.CELL_INDEX, "ci" + i);
            params.put(NetworkService.LOCATION_AREA_CODE, "lac" + i);
            params.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
            IDataElement sect;
            try {
                sect = nm.createElement(site, new DataElement(params));
                model.linkNode(sector, sect, params);
            } catch (AWEException e1) {
                LOGGER.error("Error while trying to create element", e1);
                throw (RuntimeException)new RuntimeException().initCause(e1);
            }

            Relationship rel = dsServ.createRelationship(model.getProxy(((DataElement)sector).getNode()),
                    model.getProxy(((DataElement)sect).getNode()), N2NRelTypes.NEIGHBOUR);

            relations.add(rel);

        }
        Node proxy = model.getProxy(((DataElement)sector).getNode());
        IDataElement findedService = model.getServiceElementByProxy(new DataElement(proxy));
        Assert.assertEquals("Unexpected element", sector, findedService);
        // Assert.assertTrue("Incorrect Relations Count: " + resCount, resCount == relCount);
    }

    @Test
    public void testConstructorRootNode() {
        // create n2n model
        NodeToNodeRelationshipModel model;
        try {
            model = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name",
                    NetworkElementNodeType.SECTOR);
        } catch (AWEException e) {
            // TODO Handle DatabaseException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        Node n2nRoot = model.getRootNode();
        Assert.assertNotNull(n2nRoot);

        // create n2n model based on existing root
        NodeToNodeRelationshipModel testModel;
        try {
            testModel = new NodeToNodeRelationshipModel(n2nRoot);
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        Assert.assertNotNull(testModel);
        // properties correct
        Assert.assertEquals(model.getName(), testModel.getName());
        Assert.assertEquals(model.getNodeToNodeRelationsType(), testModel.getNodeToNodeRelationsType());
        Assert.assertEquals(model.getRootNode(), testModel.getRootNode());
        Assert.assertEquals(model.getType(), testModel.getType());
    }

    @Test
    public void testGetParentModel() {
        // create n2n model
        NodeToNodeRelationshipModel model;
        IModel pModel;
        try {
            model = new NodeToNodeRelationshipModel(new DataElement(network), N2NRelTypes.NEIGHBOUR, "name",
                    NetworkElementNodeType.SECTOR);
            pModel = model.getParentModel();
        } catch (AWEException e) {
            // TODO Handle DatabaseException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        Assert.assertTrue("Same nodes expected ", pModel.getRootNode().equals(network));
    }

    private boolean chainExists(Node parent, Node child) {
        Iterator<Relationship> it = parent.getRelationships(DatasetRelationTypes.CHILD, Direction.OUTGOING).iterator();

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
            it = prevNode.getRelationships(DatasetRelationTypes.NEXT, Direction.OUTGOING).iterator();
            if (it.hasNext()) {
                node = it.next().getOtherNode(prevNode);
            } else {
                return false;
            }
            prevNode = node;
        }
    }

}
