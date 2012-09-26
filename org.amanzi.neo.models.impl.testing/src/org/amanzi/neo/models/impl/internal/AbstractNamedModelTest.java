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

package org.amanzi.neo.models.impl.internal;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
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
public class AbstractNamedModelTest extends AbstractMockitoTest {

    public static class TestNamedModel extends AbstractNamedModel {

        /**
         * @param nodeService
         * @param generalNodeProperties
         */
        public TestNamedModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
            super(nodeService, generalNodeProperties);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void finishUp() throws ModelException {
            // TODO Auto-generated method stub

        }

        @Override
        protected INodeType getModelType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Iterable<IDataElement> getAllElementsByType(final INodeType nodeType) throws ModelException {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private static enum TestNodeTypes implements INodeType {
        TEST1;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }

    }

    private static final String MODEL_NAME = "name";

    private INodeService nodeService;

    private AbstractNamedModel model;

    @Before
    public void setUp() {
        nodeService = mock(INodeService.class);

        model = spy(new TestNamedModel(nodeService, new GeneralNodeProperties()));
        doReturn(TestNodeTypes.TEST1).when(model).getModelType();
    }

    @Test
    public void testInitializationFromName() throws Exception {
        Node parentNode = getNodeMock();

        model.initialize(parentNode, MODEL_NAME);

        verify(nodeService).createNode(parentNode, TestNodeTypes.TEST1, NodeService.NodeServiceRelationshipType.CHILD, MODEL_NAME);
    }
}
