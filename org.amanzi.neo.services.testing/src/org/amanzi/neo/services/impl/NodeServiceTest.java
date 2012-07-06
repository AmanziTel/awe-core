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

package org.amanzi.neo.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.util.AbstractServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NodeServiceTest extends AbstractServiceTest {

    private static final String NODE_NAME = "node_name";

    private static final String NODE_TYPE_ID = TestNodeType.TEST1.getId();

    private final IGeneralNodeProperties generalNodeProperties = new GeneralNodeProperties();

    private INodeService nodeService = null;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        this.nodeService = new NodeService(getService(), this.generalNodeProperties);

        setReadOnly(true);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckDatabaseExceptionOnGetNodeName() throws Exception {
        setMethodFailure();

        Node node = getNodeMock();
        when(node.hasProperty(this.generalNodeProperties.getNodeNameProperty())).thenThrow(new IllegalArgumentException());

        this.nodeService.getNodeName(node);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckDatabaseExceptionOnGetNodeType() throws Exception {
        setMethodFailure();

        Node node = getNodeMock();
        when(node.hasProperty(this.generalNodeProperties.getNodeTypeProperty())).thenThrow(new IllegalArgumentException());

        this.nodeService.getNodeType(node);
    }

    @Test
    public void testCheckGetNodeNameActivity() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeNameProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(this.generalNodeProperties.getNodeNameProperty(), null)).thenReturn(NODE_NAME);

        this.nodeService.getNodeName(node);

        verify(node).hasProperty(this.generalNodeProperties.getNodeNameProperty());
        verify(node).getProperty(this.generalNodeProperties.getNodeNameProperty());
        verifyNoMoreInteractions(node);
    }

    @Test
    public void testCheckGetNodeNameResult() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeNameProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(this.generalNodeProperties.getNodeNameProperty())).thenReturn(NODE_NAME);

        String result = this.nodeService.getNodeName(node);

        assertEquals("Unexpected Name of Node", NODE_NAME, result);
    }

    @Test
    public void testCheckGetNodeTypeActivity() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeTypeProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(this.generalNodeProperties.getNodeTypeProperty())).thenReturn(NODE_TYPE_ID);

        this.nodeService.getNodeType(node);

        verify(node).hasProperty(this.generalNodeProperties.getNodeTypeProperty());
        verify(node).getProperty(this.generalNodeProperties.getNodeTypeProperty());
        verifyNoMoreInteractions(node);
    }

    @Test
    public void testCheckGetNodeTypeResult() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeTypeProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(this.generalNodeProperties.getNodeTypeProperty())).thenReturn(NODE_TYPE_ID);

        INodeType result = this.nodeService.getNodeType(node);

        assertEquals("Unepected Type of Node", TestNodeType.TEST1, result);
    }

    @Test(expected = PropertyNotFoundException.class)
    public void testCheckPropertyNotFoundExceptionOnGetNodeName() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeNameProperty())).thenReturn(false);

        this.nodeService.getNodeName(node);
    }

    @Test(expected = PropertyNotFoundException.class)
    public void testCheckPropertyNotFoundExceptionOnGetNodeType() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeTypeProperty())).thenReturn(false);

        this.nodeService.getNodeType(node);
    }

    @Test(expected = DatabaseException.class)
    public void testGetAllChildrentOfNode() throws Exception {
        setChildTraversalToNull();

        nodeService.getChildren(getNodeMock());
    }

    @Test(expected = DatabaseException.class)
    public void testGetAllChildByName() throws Exception {
        setChildTraversalToNull();

        nodeService.getChildByName(getNodeMock(), "some name", TestNodeType.TEST1);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckExceptionOnGetReferencedNode() throws Exception {
        setReferencedNode(null);

        nodeService.getReferencedNode();
    }

    @Test
    public void testCheckDbActivityOnGetReferencedNode() throws Exception {
        Node node = getNodeMock();
        setReferencedNode(node);

        nodeService.getReferencedNode();

        GraphDatabaseService service = getService();
        verify(service).getReferenceNode();
    }

    @Test
    public void testCheckResultOfGetReferencedNode() throws Exception {
        Node node = getNodeMock();
        setReferencedNode(node);

        Node result = nodeService.getReferencedNode();

        assertEquals("Unexpected referenced node", node, result);
    }

    @Test
    public void testCheckActivityOnGetParent() throws Exception {
        Node node = getNodeMock();
        Node parent = getNodeMock();
        Relationship relToParent = mock(Relationship.class);

        when(relToParent.getStartNode()).thenReturn(parent);
        when(node.getSingleRelationship(NodeService.NodeServiceRelationshipType.CHILD, Direction.INCOMING)).thenReturn(relToParent);

        nodeService.getParent(node);

        verify(node).getSingleRelationship(NodeService.NodeServiceRelationshipType.CHILD, Direction.INCOMING);
        verify(relToParent).getStartNode();
    }

    @Test
    public void testCheckResultOnGetParent() throws Exception {
        Node node = getNodeMock();
        Node parent = getNodeMock();
        Relationship relToParent = mock(Relationship.class);

        when(relToParent.getStartNode()).thenReturn(parent);
        when(node.getSingleRelationship(NodeService.NodeServiceRelationshipType.CHILD, Direction.INCOMING)).thenReturn(relToParent);

        Node result = nodeService.getParent(node);

        assertEquals("unexpected parent", parent, result);
    }

    @Test
    public void testCheckWithoutParent() throws Exception {
        Node node = getNodeMock();

        when(node.getSingleRelationship(NodeService.NodeServiceRelationshipType.CHILD, Direction.INCOMING)).thenReturn(null);

        Node result = nodeService.getParent(node);

        assertNull("there cannot be a parent", result);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckDatabaseExceptiononGetParent() throws Exception {
        Node node = getNodeMock();

        when(node.getSingleRelationship(NodeService.NodeServiceRelationshipType.CHILD, Direction.INCOMING)).thenThrow(
                new IllegalArgumentException());

        nodeService.getParent(node);
    }

    @Test
    public void testCheckCreateNodeCalledByCreateNodeWithOnlyType() throws Exception {
        nodeService = spy(nodeService);
        Node parentNode = getNodeMock();
        Map<String, Object> properties = new HashMap<String, Object>();

        doReturn(null).when(nodeService)
                .createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, properties);

        nodeService.createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION);

        verify(nodeService).createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, properties);
    }

    @Test
    public void testCheckCreateNodeCalledByCreateNodeWithTypeAndString() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(generalNodeProperties.getNodeNameProperty(), NODE_NAME);

        nodeService = spy(nodeService);
        Node parentNode = getNodeMock();

        doReturn(null).when(nodeService)
                .createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, properties);

        nodeService.createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, NODE_NAME);

        verify(nodeService).createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, properties);
    }

    @Test
    public void testCheckCreateNodeCalledByCreateNodeWithTypeAndStringAndMap() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("some property", NODE_NAME);

        Map<String, Object> rawProperties = new HashMap<String, Object>(properties);
        rawProperties.put(generalNodeProperties.getNodeNameProperty(), NODE_NAME);

        nodeService = spy(nodeService);
        Node parentNode = getNodeMock();

        doReturn(null).when(nodeService).createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION,
                rawProperties);

        nodeService.createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, NODE_NAME, properties);

        verify(nodeService).createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, rawProperties);
    }

    @Test
    public void testCheckCreateNodeActivity() throws Exception {
        setReadOnly(false);
        Node parentNode = getNodeMock();
        Node createdNode = getNodeMock();

        GraphDatabaseService service = getService();

        when(service.createNode()).thenReturn(createdNode);

        Map<String, Object> properties = getNodeProperties();

        nodeService.createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, properties);

        verify(service).createNode();
        verify(parentNode).createRelationshipTo(createdNode, TestRelationshipTypes.TEST_RELATION);
        verify(createdNode).setProperty(generalNodeProperties.getNodeTypeProperty(), TestNodeType.TEST1.getId());
        for (Entry<String, Object> entry : properties.entrySet()) {
            verify(createdNode).setProperty(entry.getKey(), entry.getValue());
        }
    }

    @Test(expected = DatabaseException.class)
    public void testCheckDatabaseExceptionOnCreatingNewNode() throws Exception {
        setReadOnly(false);
        setMethodFailure();

        Node parentNode = getNodeMock();

        GraphDatabaseService service = getService();

        doThrow(new IllegalArgumentException()).when(service).createNode();

        Map<String, Object> properties = getNodeProperties();

        nodeService.createNode(parentNode, TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION, properties);
    }

    private Map<String, Object> getNodeProperties() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("string", "string");
        result.put("long", 123l);

        return result;
    }

    private void setReferencedNode(Node node) {
        GraphDatabaseService service = getService();

        if (node == null) {
            when(service.getReferenceNode()).thenThrow(new IllegalArgumentException());
        } else {
            when(service.getReferenceNode()).thenReturn(node);
        }
    }

    private void setChildTraversalToNull() {
        NodeService spyService = spy(new NodeService(getService(), generalNodeProperties));

        when(spyService.getChildrenTraversal()).thenReturn(null);

        nodeService = spyService;
    }
}
