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
package org.amanzi.neo.core.nodetypes;

import junit.framework.Assert;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class NodeTypeManagerTest {

    private enum NodeType implements INodeType {
        TYPE1, TYPE2, TYPE3;

        @Override
        public String getId() {
            return name().toLowerCase();
        }
    }

    private enum AnotherNodeTypes implements INodeType {
        TYPE4, TYPE5, TYPE6;

        @Override
        public String getId() {
            return name().toLowerCase();
        }
    }

    @BeforeClass
    public static void beforeTest() throws Exception {
        NodeTypeManager.registerNodeType(NodeType.class);
    }

    @Test
    public void testGetType() {
        for (NodeType nodeType : NodeType.values()) {
            INodeType t = NodeTypeManager.getType(nodeType.getId());
            Assert.assertEquals(nodeType, t);
        }
    }

    @Test
    public void testGetTypeMultipleTypes() {
        NodeTypeManager.registerNodeType(AnotherNodeTypes.class);
        for (AnotherNodeTypes nodeType : AnotherNodeTypes.values()) {
            INodeType t = NodeTypeManager.getType(nodeType.getId());
            Assert.assertEquals(nodeType, t);
        }
    }

    @Test
    public void testRegisterNodeTypeTwice() {
        NodeTypeManager.registerNodeType(AnotherNodeTypes.class);
        NodeTypeManager.registerNodeType(AnotherNodeTypes.class);
    }

}
