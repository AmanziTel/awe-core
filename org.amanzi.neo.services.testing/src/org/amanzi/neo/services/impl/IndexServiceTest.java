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

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.util.AbstractServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class IndexServiceTest extends AbstractServiceTest {
    private IndexService indexService = null;
    private static final String TEST_NODE_VALUE = "some value";

    private static final String NODE_NAME = "node_name";

    private static final String NAME_PARAM = "name";
    private static final String VALUE_PARAM = "value";
    private static final String TYPE_PARAM = "type";

    private enum TestNodeType implements INodeType {
        TEST_NODE_TYPE1;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        indexService = new IndexService(getService());

        setReadOnly(true);
    }

    @Test
    public void testCheckGetIndexIfNotExist() throws ServiceException {
        indexService = spy(indexService);
        Node node = getMockedNodeWithValues();
        @SuppressWarnings("unchecked")
        Index<Node> mockedIndex = mock(Index.class);
        doReturn(mockedIndex).when(indexService).createNodeIndex(any(String.class));
        Index<Node> index = indexService.getIndex(node, TestNodeType.TEST_NODE_TYPE1);
        verify(indexService).createNodeIndex(any(String.class));
        assertEquals("Unexpected index", mockedIndex, index);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckGetIndexIfExist() throws ServiceException {
        indexService = spy(indexService);
        Node node = getMockedNodeWithValues();
        Index<Node> mockedIndex = mock(Index.class);
        doReturn(mockedIndex).when(indexService).createNodeIndex(any(String.class));
        indexService.getIndex(node, TestNodeType.TEST_NODE_TYPE1);
        Index<Node> index = indexService.getIndex(node, TestNodeType.TEST_NODE_TYPE1);
        verify(indexService, times(1)).createNodeIndex(any(String.class));
        assertEquals("Unexpected index", mockedIndex, index);
    }

    @Test
    public void testCheckGetIndexKeyWithotuArray() {
        String key = Long.MIN_VALUE + "@test_node_type1";
        Node node = getNodeMock();
        doReturn(Long.MIN_VALUE).when(node).getId();
        String result = indexService.getIndexKey(node, TestNodeType.TEST_NODE_TYPE1);
        assertEquals("keys not equals", key, result);
    }

    @Test
    public void testCheckGetIndexKeyWithArray() {
        String key = Long.MIN_VALUE + "@test_node_type1|" + NODE_NAME + "|" + TEST_NODE_VALUE;
        Node node = getNodeMock();
        doReturn(Long.MIN_VALUE).when(node).getId();
        String result = indexService.getIndexKey(node, TestNodeType.TEST_NODE_TYPE1, NODE_NAME, TEST_NODE_VALUE);
        assertEquals("keys not equals", key, result);
    }

    private Node getMockedNodeWithValues() {
        Map<String, Object> values = getNodesValues();
        return getNodeMock(values);
    }

    /**
     * @return
     */
    private Map<String, Object> getNodesValues() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(NAME_PARAM, NODE_NAME);
        values.put(VALUE_PARAM, TEST_NODE_VALUE);
        values.put(TYPE_PARAM, TestNodeType.TEST_NODE_TYPE1.getId());
        return values;
    }
}
