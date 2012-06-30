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

package org.amanzi.neo.models.impl;

import org.amanzi.neo.models.exceptions.DataInconsistencyException;
import org.amanzi.neo.models.impl.ProjectModel.ProjectModelNodeType;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.impl.NodeService;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProjectModelTest extends AbstractMockitoTest {

    private enum TestNodeType implements INodeType {
        TEST_TYPE;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    private static final String PROJECT_NAME = "project";

    private static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = new GeneralNodeProperties();

    private INodeService nodeService;

    private ProjectModel model;

    private Node node;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        nodeService = mock(INodeService.class);
        node = getNodeMock();

        model = new ProjectModel(nodeService, GENERAL_NODE_PROPERTIES);

    }

    @Test
    public void testInitializationFromNode() throws Exception {
        // mock node properties
        Node referencedNode = getNodeMock();

        when(nodeService.getReferencedNode()).thenReturn(referencedNode);
        when(nodeService.getParent(node)).thenReturn(referencedNode);
        when(nodeService.getNodeName(node)).thenReturn(PROJECT_NAME);
        when(nodeService.getNodeType(node)).thenReturn(ProjectModel.ProjectModelNodeType.PROJECT);

        model.initialize(node);

        verify(nodeService).getReferencedNode();
        verify(nodeService).getParent(node);
        verify(nodeService).getNodeName(node);
        verify(nodeService).getNodeType(node);
    }

    @Test(expected = DataInconsistencyException.class)
    public void testInitializatinoFromNodeWithIncorrectParent() throws Exception {
        // mock node properties
        Node referencedNode = getNodeMock();
        Node parentNode = getNodeMock();

        when(nodeService.getReferencedNode()).thenReturn(referencedNode);
        when(nodeService.getParent(node)).thenReturn(parentNode);
        when(nodeService.getNodeName(node)).thenReturn(PROJECT_NAME);
        when(nodeService.getNodeType(node)).thenReturn(ProjectModel.ProjectModelNodeType.PROJECT);

        model.initialize(node);
    }

    @Test(expected = DataInconsistencyException.class)
    public void testInitializationFromNodeWithoutParent() throws Exception {
        // mock node properties
        Node referencedNode = getNodeMock();

        when(nodeService.getReferencedNode()).thenReturn(referencedNode);
        when(nodeService.getParent(node)).thenReturn(null);
        when(nodeService.getNodeName(node)).thenReturn(PROJECT_NAME);
        when(nodeService.getNodeType(node)).thenReturn(ProjectModel.ProjectModelNodeType.PROJECT);

        model.initialize(node);
    }

    @Test(expected = DataInconsistencyException.class)
    public void testInitializionFromNodeWithIncorrectType() throws Exception {
        // mock node properties
        Node referencedNode = getNodeMock();

        when(nodeService.getReferencedNode()).thenReturn(referencedNode);
        when(nodeService.getParent(node)).thenReturn(referencedNode);
        when(nodeService.getNodeName(node)).thenReturn(PROJECT_NAME);
        when(nodeService.getNodeType(node)).thenReturn(TestNodeType.TEST_TYPE);

        model.initialize(node);
    }

    @Test
    public void testInitializationFromProjectName() throws Exception {
        Node referencedNode = getNodeMock();
        Node rootNode = getNodeMock();
        when(nodeService.getReferencedNode()).thenReturn(referencedNode);
        when(
                nodeService.createNode(referencedNode, ProjectModelNodeType.PROJECT, NodeService.NodeServiceRelationshipType.CHILD,
                        PROJECT_NAME)).thenReturn(rootNode);

        model.initialize(PROJECT_NAME);

        verify(nodeService).getReferencedNode();
        verify(nodeService).createNode(referencedNode, ProjectModelNodeType.PROJECT, NodeService.NodeServiceRelationshipType.CHILD,
                PROJECT_NAME);
    }
}
