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
package org.amanzi.neo.nodetypes;

import org.amanzi.neo.nodetypes.NodeTypeManager.NodeTypeNotExistsException;
import org.amanzi.neo.nodetypes.internal.TestNodeTypes;
import org.amanzi.testing.AbstractTest;
import org.junit.Before;
import org.junit.Test;

public class NodeTypeManagerTest extends AbstractTest {

    /** String UNEXPECTED_NODE_TYPE field */
    private static final String UNEXPECTED_NODE_TYPE = "unexpected node type";

    private enum NodeType implements INodeType {
        TYPE1, TYPE2, TYPE3;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    private enum AnotherNodeTypes implements INodeType {
        TYPE4, TYPE5, TYPE6;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    private NodeTypeManager manager;

    @Before
    public void setUp() {
        manager = new NodeTypeManager();
    }

    @Test
    public void testGetType() throws Exception {
        manager.registerNodeType(NodeType.class);
        for (NodeType nodeType : NodeType.values()) {
            INodeType t = manager.getType(nodeType.getId());
            assertEquals(UNEXPECTED_NODE_TYPE, nodeType, t);
        }
    }

    @Test
    public void testGetTypeMultipleTypes() throws Exception {
        manager.registerNodeType(AnotherNodeTypes.class);
        for (AnotherNodeTypes nodeType : AnotherNodeTypes.values()) {
            INodeType t = manager.getType(nodeType.getId());
            assertEquals(UNEXPECTED_NODE_TYPE, nodeType, t);
        }
    }

    @Test
    public void testRegisterNodeTypeTwice() {
        manager.registerNodeType(AnotherNodeTypes.class);
        manager.registerNodeType(AnotherNodeTypes.class);
    }

    @Test
    public void testCheckNodeTypesFromExtensionPoint() {
        assertTrue("node type not found in extensions", manager.getRegisteredNodeTypes().contains(TestNodeTypes.class));
    }

    @Test
    public void testCheckNodeTypesIds() throws Exception {
        for (TestNodeTypes t : TestNodeTypes.values()) {
            INodeType type = manager.getType(NodeTypeUtils.getTypeName(t.getId()));
            assertEquals("Unexpected NodeType", t.getId(), type.getId());
        }
    }

    @Test(expected = NodeTypeNotExistsException.class)
    public void testCheckNodeTypeNotExistsException() throws Exception {
        manager.getType("blablabla");
    }
}
