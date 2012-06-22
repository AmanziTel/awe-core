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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.amanzi.neo.models.exceptions.DataInconsistencyException;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.exceptions.enums.ExceptionSeverity;
import org.amanzi.neo.services.nodetypes.INodeType;
import org.amanzi.testing.AbstractTest;
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
public class AbstractModelTest extends AbstractTest {

    private static final String TEST_NODE_NAME = "test name";

    private static final INodeType TEST_NODE_TYPE = new INodeType() {

        @Override
        public String getId() {
            return "test id";
        }
    };

    private INodeService nodeService;

    private AbstractModel model;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        nodeService = mock(INodeService.class);

        model = new AbstractModel(nodeService) {

            @Override
            public void finishUp() throws ModelException {
                // do nothing
            }
        };

    }

    @Test
    public void testServiceActivityOnInitialize() throws Exception {
        Node rootNode = getNodeMock();

        model.initialize(rootNode);

        verify(nodeService).getNodeName(eq(rootNode));
        verify(nodeService).getNodeType(eq(rootNode));
    }

    @Test
    public void testCheckModelFieldsOnInitialize() throws Exception {
        Node rootNode = getNodeMock();

        when(nodeService.getNodeName(eq(rootNode))).thenReturn(TEST_NODE_NAME);
        when(nodeService.getNodeType(eq(rootNode))).thenReturn(TEST_NODE_TYPE);

        model.initialize(rootNode);

        assertEquals("Unexpected initialized name", TEST_NODE_NAME, model.getName());
        assertEquals("Unexpected initialized type", TEST_NODE_TYPE, model.getType());
    }

    @Test(expected = FatalException.class)
    public void testCheckFatalExceptionOnInitialize() throws Exception {
        Node rootNode = getNodeMock();

        when(nodeService.getNodeName(eq(rootNode))).thenThrow(new DatabaseException(new NullPointerException()));

        model.initialize(rootNode);
    }

    @Test(expected = DataInconsistencyException.class)
    public void testCheckInconsistencyExceptionForNameOnInitialize() throws Exception {
        Node rootNode = getNodeMock();

        doThrow(new PropertyNotFoundException(ExceptionSeverity.FATAL, "Name", rootNode)).when(nodeService).getNodeName(rootNode);

        model.initialize(rootNode);
    }

    @Test(expected = DataInconsistencyException.class)
    public void testCheckInconsistencyExceptionForTypeOnInitialize() throws Exception {
        Node rootNode = getNodeMock();

        doThrow(new PropertyNotFoundException(ExceptionSeverity.FATAL, "Name", rootNode)).when(nodeService).getNodeName(rootNode);

        model.initialize(rootNode);
    }

}
