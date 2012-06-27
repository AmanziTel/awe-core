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

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.util.AbstractServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

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

        setReadOnly();
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
        verify(node).getProperty(this.generalNodeProperties.getNodeNameProperty(), null);
        verifyNoMoreInteractions(node);
    }

    @Test
    public void testCheckGetNodeNameResult() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeNameProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(this.generalNodeProperties.getNodeNameProperty(), null)).thenReturn(NODE_NAME);

        String result = this.nodeService.getNodeName(node);

        assertEquals("Unexpected Name of Node", NODE_NAME, result);
    }

    @Test
    public void testCheckGetNodeTypeActivity() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeTypeProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(this.generalNodeProperties.getNodeTypeProperty(), null)).thenReturn(NODE_TYPE_ID);

        this.nodeService.getNodeType(node);

        verify(node).hasProperty(this.generalNodeProperties.getNodeTypeProperty());
        verify(node).getProperty(this.generalNodeProperties.getNodeTypeProperty(), null);
        verifyNoMoreInteractions(node);
    }

    @Test
    public void testCheckGetNodeTypeResult() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(this.generalNodeProperties.getNodeTypeProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(this.generalNodeProperties.getNodeTypeProperty(), null)).thenReturn(NODE_TYPE_ID);

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

        nodeService.getChildByName(getNodeMock(), "some name");
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
