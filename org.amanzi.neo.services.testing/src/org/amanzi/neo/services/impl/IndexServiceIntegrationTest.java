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

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.testing.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class IndexServiceIntegrationTest extends AbstractIntegrationTest {
    private IndexService indexService;

    private static final String NODE_NAME = "node_name_1";
    private static final String NODE_NAME_2 = "node_name_2";
    private static final String PROPERTY_NAME = "property_name";
    private static final String PROPERTY_VALUE = "value";

    private static final String NAME_PARAM = "name";
    private String KEY_FORMAT = "%s@%s";

    private enum TestNodeType implements INodeType {
        TEST_NODE_TYPE1;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        indexService = new IndexService(getGraphDatabaseService());
    }

    @Test(expected = ServiceException.class)
    public void testCheckCreateNodeIndexIsNull() throws ServiceException {
        indexService.createNodeIndex(null);
    }

    @Test
    public void testCheckCreateNodeIndex() throws ServiceException {
        String key = String.format(KEY_FORMAT, NODE_NAME, TestNodeType.TEST_NODE_TYPE1.getId());

        Index<Node> index = indexService.createNodeIndex(key);

        assertEquals("index names are not equals", key, index.getName());
    }

    @Test
    public void testCheckGetIndex() throws ServiceException {
        Node node = createNode(NAME_PARAM, NODE_NAME);

        String key = String.format(KEY_FORMAT, node.getId(), TestNodeType.TEST_NODE_TYPE1.getId());
        Index<Node> index = indexService.getIndex(node, TestNodeType.TEST_NODE_TYPE1);

        assertEquals("index names are not equals", key, index.getName());
    }

    @Test
    public void testCheckAddToIndex() throws ServiceException {
        Node node = createNode(NAME_PARAM, NODE_NAME);
        Node node2 = createNode(NAME_PARAM, NODE_NAME_2);
        String key = String.format(KEY_FORMAT, node.getId(), TestNodeType.TEST_NODE_TYPE1.getId());

        indexService.addToIndex(node, TestNodeType.TEST_NODE_TYPE1, node2, PROPERTY_NAME, PROPERTY_VALUE);

        Index<Node> result;
        Transaction tx = getGraphDatabaseService().beginTx();
        result = getGraphDatabaseService().index().forNodes(key);
        tx.success();
        tx.finish();

        assertEquals("unexpected property", node2, result.get(PROPERTY_NAME, PROPERTY_VALUE).getSingle());

    }

    @Test(expected = ServiceException.class)
    public void testCheckAddToIndexIfSecondNodeIsNull() throws ServiceException {
        Node node = createNode(NAME_PARAM, NODE_NAME);
        indexService.addToIndex(node, TestNodeType.TEST_NODE_TYPE1, null, PROPERTY_NAME, PROPERTY_VALUE);
    }
}
