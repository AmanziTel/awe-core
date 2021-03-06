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
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
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

    /** long TEST_LONG_PROPERTY field */
    private static final long TEST_LONG_PROPERTY = 123l;

    /** String NEW_TEST_NODE_PROPERTY field */
    private static final String NEW_TEST_NODE_PROPERTY = "new property";

    /** String TEST_NODE_VALUE field */
    private static final String TEST_NODE_VALUE = "some value";

    private static final String NODE_NAME = "node_name";

    private static final String NODE_TYPE_ID = TestNodeType.TEST1.getId();

    private static final String TEST_NODE_PROPERTY = "property";

    private final IGeneralNodeProperties generalNodeProperties = new GeneralNodeProperties();

    private NodeService nodeService = null;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        nodeService = new NodeService(getService(), generalNodeProperties);

        setReadOnly(true);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckDatabaseExceptionOnGetNodeName() throws Exception {
        setMethodFailure();

        Node node = getNodeMock();
        when(node.hasProperty(generalNodeProperties.getNodeNameProperty())).thenThrow(new IllegalArgumentException());

        nodeService.getNodeName(node);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckDatabaseExceptionOnGetNodeType() throws Exception {
        setMethodFailure();

        Node node = getNodeMock();
        when(node.hasProperty(generalNodeProperties.getNodeTypeProperty())).thenThrow(new IllegalArgumentException());

        nodeService.getNodeType(node);
    }

    @Test
    public void testCheckGetNodeNameActivity() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeNameProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(generalNodeProperties.getNodeNameProperty(), null)).thenReturn(NODE_NAME);

        nodeService.getNodeName(node);

        verify(node).hasProperty(generalNodeProperties.getNodeNameProperty());
        verify(node).getProperty(generalNodeProperties.getNodeNameProperty());
        verifyNoMoreInteractions(node);
    }

    @Test
    public void testCheckGetNodeNameResult() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeNameProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(generalNodeProperties.getNodeNameProperty())).thenReturn(NODE_NAME);

        String result = nodeService.getNodeName(node);

        assertEquals("Unexpected Name of Node", NODE_NAME, result);
    }

    @Test
    public void testCheckGetNodeTypeActivity() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeTypeProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(generalNodeProperties.getNodeTypeProperty())).thenReturn(NODE_TYPE_ID);

        nodeService.getNodeType(node);

        verify(node).hasProperty(generalNodeProperties.getNodeTypeProperty());
        verify(node).getProperty(generalNodeProperties.getNodeTypeProperty());
        verifyNoMoreInteractions(node);
    }

    @Test
    public void testCheckGetNodeTypeResult() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeTypeProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(generalNodeProperties.getNodeTypeProperty())).thenReturn(NODE_TYPE_ID);

        INodeType result = nodeService.getNodeType(node);

        assertEquals("Unepected Type of Node", TestNodeType.TEST1, result);
    }

    @Test(expected = PropertyNotFoundException.class)
    public void testCheckPropertyNotFoundExceptionOnGetNodeName() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeNameProperty())).thenReturn(false);

        nodeService.getNodeName(node);
    }

    @Test(expected = PropertyNotFoundException.class)
    public void testCheckPropertyNotFoundExceptionOnGetNodeType() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeTypeProperty())).thenReturn(false);

        nodeService.getNodeType(node);
    }

    @Test(expected = DatabaseException.class)
    public void testGetAllChildrentOfNode() throws Exception {
        setChildTraversalToNull();

        nodeService.getChildren(getNodeMock(), TestNodeType.TEST1);
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

        nodeService.getParent(node, NodeService.NodeServiceRelationshipType.CHILD);

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

        Node result = nodeService.getParent(node, NodeService.NodeServiceRelationshipType.CHILD);

        assertEquals("unexpected parent", parent, result);
    }

    @Test
    public void testCheckWithoutParent() throws Exception {
        Node node = getNodeMock();

        when(node.getSingleRelationship(NodeService.NodeServiceRelationshipType.CHILD, Direction.INCOMING)).thenReturn(null);

        Node result = nodeService.getParent(node, NodeService.NodeServiceRelationshipType.CHILD);

        assertNull("there cannot be a parent", result);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckDatabaseExceptiononGetParent() throws Exception {
        Node node = getNodeMock();

        when(node.getSingleRelationship(NodeService.NodeServiceRelationshipType.CHILD, Direction.INCOMING)).thenThrow(
                new IllegalArgumentException());

        nodeService.getParent(node, NodeService.NodeServiceRelationshipType.CHILD);
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

    @Test
    public void testCheckUpdateNodePropertyWithoutProperty() throws Exception {
        setReadOnly(false);
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(false);

        nodeService.updateProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE);

        verifyNodeProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE, false, false);
    }

    @Test
    public void testCheckUpdateNodePropertyWithEqualProperty() throws Exception {
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(true);
        when(node.getProperty(TEST_NODE_PROPERTY)).thenReturn(TEST_NODE_VALUE);

        nodeService.updateProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE);

        verifyNodeProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE, true, true);
    }

    @Test
    public void testCheckUpdateNodePropertyWithNotEqualProperty() throws Exception {
        setReadOnly(false);
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(true);
        when(node.getProperty(TEST_NODE_PROPERTY)).thenReturn(TEST_NODE_VALUE + 1);

        nodeService.updateProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE);

        verifyNodeProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE, true, false);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckExceptionOnUpdateProperty() throws Exception {
        setReadOnly(false);
        setMethodFailure();
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(true);
        when(node.getProperty(TEST_NODE_PROPERTY)).thenReturn(TEST_NODE_VALUE + 1);

        doThrow(new IllegalArgumentException()).when(node).setProperty(TEST_NODE_PROPERTY, TEST_NODE_VALUE);

        nodeService.updateProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckExceptionOnGetSingleChild() throws Exception {
        NodeService nodeServiceImpl = spy(new NodeService(getService(), generalNodeProperties));

        doThrow(new IllegalArgumentException()).when(nodeServiceImpl).getDownlinkTraversal();

        nodeServiceImpl.getSingleChild(getNodeMock(), TestNodeType.TEST1, TestRelationshipTypes.TEST_RELATION);
    }

    @Test
    public void testCheckGetNodePropertiesWithDefaultValueAndExistingProperty() throws Exception {
        Node node = getNodeMock();

        when(node.getProperty(TEST_NODE_PROPERTY, TEST_NODE_VALUE)).thenReturn(TEST_NODE_VALUE);

        Object result = nodeService.getNodeProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE, false);

        verify(node).hasProperty(TEST_NODE_PROPERTY);
        verify(node).getProperty(TEST_NODE_PROPERTY, TEST_NODE_VALUE);

        assertEquals("unexpected property", TEST_NODE_VALUE, result);
    }

    @Test
    public void testCheckGetNodePropertiesWithDefaultValueAndExistingPropertyThrowException() throws Exception {
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(true);
        when(node.getProperty(TEST_NODE_PROPERTY)).thenReturn(TEST_NODE_VALUE);

        Object result = nodeService.getNodeProperty(node, TEST_NODE_PROPERTY, null, true);

        verify(node).hasProperty(TEST_NODE_PROPERTY);
        verify(node).getProperty(TEST_NODE_PROPERTY);

        assertEquals("unexpected property", TEST_NODE_VALUE, result);
    }

    @Test
    public void testCheckGetNodePropertiesWithDefaultValueAndNotExistingProperty() throws Exception {
        Node node = getNodeMock();

        when(node.getProperty(TEST_NODE_PROPERTY, TEST_NODE_VALUE)).thenReturn(TEST_NODE_VALUE);

        Object result = nodeService.getNodeProperty(node, TEST_NODE_PROPERTY, TEST_NODE_VALUE, false);

        verify(node).hasProperty(TEST_NODE_PROPERTY);
        verify(node).getProperty(TEST_NODE_PROPERTY, TEST_NODE_VALUE);

        assertEquals("unexpected property", TEST_NODE_VALUE, result);
    }

    @Test(expected = PropertyNotFoundException.class)
    public void testCheckGetNodePropertiesWithDefaultValueAndNotExistingPropertyThrowException() throws Exception {
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(false);

        nodeService.getNodeProperty(node, TEST_NODE_PROPERTY, null, true);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckExceptionOnRemoveProperty() throws Exception {
        setReadOnly(false);
        setMethodFailure();
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(true);
        when(node.removeProperty(TEST_NODE_PROPERTY)).thenThrow(new IllegalArgumentException());

        nodeService.removeNodeProperty(node, TEST_NODE_PROPERTY, false);
    }

    @Test(expected = PropertyNotFoundException.class)
    public void testCheckExceptionOnRemoveNotExistingProperty() throws Exception {
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(false);

        nodeService.removeNodeProperty(node, TEST_NODE_PROPERTY, true);
    }

    @Test
    public void testCheckActivityOnRemoveExistingProperty() throws Exception {
        setReadOnly(false);

        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(true);

        nodeService.removeNodeProperty(node, TEST_NODE_PROPERTY, true);

        verify(node).hasProperty(TEST_NODE_PROPERTY);
        verify(node).removeProperty(TEST_NODE_PROPERTY);
    }

    @Test
    public void testCheckActivityOnRemoveNotExistingProperty() throws Exception {
        Node node = getNodeMock();

        when(node.hasProperty(TEST_NODE_PROPERTY)).thenReturn(false);

        nodeService.removeNodeProperty(node, TEST_NODE_PROPERTY, false);

        verify(node).hasProperty(TEST_NODE_PROPERTY);
        verify(node, never()).removeProperty(TEST_NODE_PROPERTY);
    }

    @Test
    public void testCheckActivityOnRenamePropertyWithThrow() throws Exception {
        Node node = getNodeMock();
        nodeService = spy(nodeService);

        doReturn(TEST_NODE_VALUE).when(nodeService).getNodeProperty(node, TEST_NODE_PROPERTY, null, false);
        doNothing().when(nodeService).removeNodeProperty(node, TEST_NODE_PROPERTY, true);
        doNothing().when(nodeService).updateProperty(node, NEW_TEST_NODE_PROPERTY, TEST_NODE_VALUE);

        nodeService.renameNodeProperty(node, TEST_NODE_PROPERTY, NEW_TEST_NODE_PROPERTY, true);

        verify(nodeService).removeNodeProperty(node, TEST_NODE_PROPERTY, false);
        verify(nodeService).getNodeProperty(node, TEST_NODE_PROPERTY, null, false);
        verify(nodeService).updateProperty(node, NEW_TEST_NODE_PROPERTY, TEST_NODE_VALUE);
    }

    @Test
    public void testCheckActivityOnRenamePropertyWithoutThrow() throws Exception {
        Node node = getNodeMock();
        nodeService = spy(nodeService);

        doReturn(TEST_NODE_VALUE).when(nodeService).getNodeProperty(node, TEST_NODE_PROPERTY, null, false);
        doNothing().when(nodeService).removeNodeProperty(node, TEST_NODE_PROPERTY, false);
        doNothing().when(nodeService).updateProperty(node, NEW_TEST_NODE_PROPERTY, TEST_NODE_VALUE);

        nodeService.renameNodeProperty(node, TEST_NODE_PROPERTY, NEW_TEST_NODE_PROPERTY, false);

        verify(nodeService).removeNodeProperty(node, TEST_NODE_PROPERTY, false);
        verify(nodeService).getNodeProperty(node, TEST_NODE_PROPERTY, null, false);
        verify(nodeService).updateProperty(node, NEW_TEST_NODE_PROPERTY, TEST_NODE_VALUE);
    }

    @Test
    public void testCheckActivityOnRenamePropertyWithNotExistingProperty() throws Exception {
        Node node = getNodeMock();
        nodeService = spy(nodeService);

        doReturn(null).when(nodeService).getNodeProperty(node, TEST_NODE_PROPERTY, null, true);

        nodeService.renameNodeProperty(node, TEST_NODE_PROPERTY, NEW_TEST_NODE_PROPERTY, false);

        verify(nodeService, never()).removeNodeProperty(node, TEST_NODE_PROPERTY, false);
        verify(nodeService).getNodeProperty(node, TEST_NODE_PROPERTY, null, false);
        verify(nodeService, never()).updateProperty(node, NEW_TEST_NODE_PROPERTY, TEST_NODE_VALUE);
    }

    @Test(expected = PropertyNotFoundException.class)
    public void testCheckActivityOnRenamePropertyWithNotExistingPropertyWithThrow() throws Exception {
        Node node = getNodeMock();
        nodeService = spy(nodeService);

        doReturn(null).when(nodeService).getNodeProperty(node, TEST_NODE_PROPERTY, null, true);

        nodeService.renameNodeProperty(node, TEST_NODE_PROPERTY, NEW_TEST_NODE_PROPERTY, true);
    }

    private void verifyNodeProperty(final Node node, final String name, final Object value, final boolean exists,
            final boolean equal) {
        verify(node).hasProperty(name);

        if (exists) {
            verify(node).getProperty(name);
        } else {
            verify(node, never()).getProperty(name);
        }

        if (exists && equal) {
            verify(node, never()).setProperty(name, value);
        } else {
            verify(node).setProperty(name, value);
        }

        verifyNoMoreInteractions(node);
    }

    private Map<String, Object> getNodeProperties() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("string", "string");
        result.put("long", TEST_LONG_PROPERTY);

        return result;
    }

    private void setReferencedNode(final Node node) {
        GraphDatabaseService service = getService();

        if (node == null) {
            when(service.getReferenceNode()).thenThrow(new IllegalArgumentException());
        } else {
            when(service.getReferenceNode()).thenReturn(node);
        }
    }

    private void setChildTraversalToNull() {
        NodeService spyService = spy(new NodeService(getService(), generalNodeProperties));

        when(spyService.getChildrenTraversal(TestNodeType.TEST1, NodeServiceRelationshipType.CHILD)).thenReturn(null);

        nodeService = spyService;
    }
}
