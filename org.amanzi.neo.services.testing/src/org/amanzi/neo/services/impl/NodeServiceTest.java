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
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.impl.internal.AbstractServiceTest;
import org.amanzi.neo.services.nodetypes.INodeType;
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
public class NodeServiceTest extends AbstractServiceTest {

    private static final String NODE_NAME = "node_name";

    private static final String NODE_TYPE_ID = "node_type";

    private static final INodeType NODE_TYPE = new INodeType() {

        @Override
        public String getId() {
            return NODE_TYPE_ID;
        }
    };

    private INodeService nodeService;

    private IGeneralNodeProperties generalNodeProperties;

    @Before
    public void setUp() {
        super.setUp();

        generalNodeProperties = new GeneralNodeProperties();

        nodeService = new NodeService(service, generalNodeProperties);

        setReadOnly();
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
        verify(node).getProperty(generalNodeProperties.getNodeNameProperty(), null);
        verifyNoMoreInteractions(node);
    }

    @Test
    public void testCheckGetNodeTypeActivity() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeTypeProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(generalNodeProperties.getNodeTypeProperty(), null)).thenReturn(NODE_TYPE_ID);

        nodeService.getNodeType(node);

        verify(node).hasProperty(generalNodeProperties.getNodeTypeProperty());
        verify(node).getProperty(generalNodeProperties.getNodeTypeProperty(), null);
        verifyNoMoreInteractions(node);
    }

    @Test
    public void testCheckGetNodeNameResult() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeNameProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(generalNodeProperties.getNodeNameProperty(), null)).thenReturn(NODE_NAME);

        String result = nodeService.getNodeName(node);

        assertEquals("Unexpected Name of Node", NODE_NAME, result);
    }

    @Test
    public void testCheckGetNodeTypeResult() throws Exception {
        Node node = getNodeMock();

        // property exists
        when(node.hasProperty(generalNodeProperties.getNodeTypeProperty())).thenReturn(true);
        // return this property
        when(node.getProperty(generalNodeProperties.getNodeTypeProperty(), null)).thenReturn(NODE_TYPE_ID);

        INodeType result = nodeService.getNodeType(node);

        assertEquals("Unepected Type of Node", NODE_TYPE, result);
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
    public void testCheckDatabaseExceptionOnGetNodeName() throws Exception {
        setMethodFailure();

        Node node = getNodeMock();
        when(node.hasProperty(generalNodeProperties.getNodeNameProperty())).thenThrow(new NullPointerException());

        nodeService.getNodeName(node);
    }

    @Test(expected = DatabaseException.class)
    public void testCheckDatabaseExceptionOnGetNodeType() throws Exception {
        setMethodFailure();

        Node node = getNodeMock();
        when(node.hasProperty(generalNodeProperties.getNodeTypeProperty())).thenThrow(new NullPointerException());

        nodeService.getNodeType(node);
    }

}
