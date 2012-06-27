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

import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.testing.AbstractIntegrationTest;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NodeServiceIntegrationTest extends AbstractIntegrationTest {

    private enum TestRelatinshipType implements RelationshipType {
        TEST_REL;
    }

    private static final String[] CHILDREN_NAMES = new String[] {"child1", "child2", "child3"};

    private static final IGeneralNodeProperties nodeProperties = new GeneralNodeProperties();

    private INodeService nodeService;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        nodeService = new NodeService(getGraphDatabaseService(), nodeProperties);
    }

    @Test
    public void testCheckGetAllNodes() throws Exception {
        Node parent = createNode();
        createChildren(parent, NodeService.NodeServiceRelationshipType.CHILD);

        Iterator<Node> result = nodeService.getChildren(parent);

        assertNotNull("result of search should not be null", result);

        @SuppressWarnings("unchecked")
        List<Node> resultList = IteratorUtils.toList(result);

        assertEquals("Unexpected size of childrent", CHILDREN_NAMES.length, resultList.size());

        for (Node node : resultList) {
            String name = (String)node.getProperty(nodeProperties.getNodeNameProperty());

            assertTrue("name should exists in original children names", ArrayUtils.contains(CHILDREN_NAMES, name));
        }
    }

    @Test
    public void testCheckGetAllNodesWhenEmpty() throws Exception {
        Node parent = createNode();

        Iterator<Node> result = nodeService.getChildren(parent);

        assertFalse("It should not be any children", result.hasNext());
    }

    @Test
    public void testCheckGetAllChildrenWithOtherRelationship() throws Exception {
        Node parent = createNode();
        createChildren(parent, TestRelatinshipType.TEST_REL);

        Iterator<Node> result = nodeService.getChildren(parent);

        assertFalse("It should not be any children", result.hasNext());
    }

    @Test
    public void testCheckGetAllNodesWithMixedRelTypes() throws Exception {
        Node parent = createNode();
        createChildren(parent, NodeService.NodeServiceRelationshipType.CHILD);
        createChildren(parent, TestRelatinshipType.TEST_REL);

        Iterator<Node> result = nodeService.getChildren(parent);

        assertNotNull("result of search should not be null", result);

        @SuppressWarnings("unchecked")
        List<Node> resultList = IteratorUtils.toList(result);

        assertEquals("Unexpected size of childrent", CHILDREN_NAMES.length, resultList.size());

        for (Node node : resultList) {
            String name = (String)node.getProperty(nodeProperties.getNodeNameProperty());

            assertTrue("name should exists in original children names", ArrayUtils.contains(CHILDREN_NAMES, name));
        }
    }

    @Test
    public void testCheckGetReferencedNode() throws Exception {
        assertEquals("unexpected referenced node", getGraphDatabaseService().getReferenceNode(), nodeService.getReferencedNode());
    }

    private void createChildren(Node parent, RelationshipType relType) {
        Transaction tx = getGraphDatabaseService().beginTx();

        for (String name : CHILDREN_NAMES) {
            Node child = getGraphDatabaseService().createNode();
            child.setProperty(nodeProperties.getNodeNameProperty(), name);

            parent.createRelationshipTo(child, relType);
        }

        tx.success();
        tx.finish();
    }

}
