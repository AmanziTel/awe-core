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
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.models.IDataModel;
import org.amanzi.neo.models.exceptions.DataInconsistencyException;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
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
public class AbstractDataModelTest extends AbstractMockitoTest {

    private static class TestDataModel extends AbstractDataModel {

        /**
         * @param nodeService
         */
        public TestDataModel(final INodeService nodeService) {
            super(nodeService, null);
        }

        @Override
        public void finishUp() throws ModelException {
        }

    }

    private INodeService nodeService;

    private IDataModel dataModel;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        nodeService = mock(INodeService.class);

        dataModel = new TestDataModel(nodeService);
    }

    @Test
    public void testCheckServiceActivityOnGetNodeParent() throws Exception {
        Node childNode = getNodeMock();
        IDataElement child = new DataElement(childNode);

        Node parentNode = getNodeMock();

        when(nodeService.getParent(childNode, NodeServiceRelationshipType.CHILD)).thenReturn(parentNode);

        dataModel.getParentElement(child);

        verify(nodeService).getParent(childNode, NodeServiceRelationshipType.CHILD);
    }

    @Test
    public void testCheckResultOfGetNodeParent() throws Exception {
        Node childNode = getNodeMock();
        IDataElement child = new DataElement(childNode);

        Node parentNode = getNodeMock();
        IDataElement parent = new DataElement(parentNode);

        when(nodeService.getParent(childNode, NodeServiceRelationshipType.CHILD)).thenReturn(parentNode);

        IDataElement result = dataModel.getParentElement(child);

        assertEquals("Unexpected Parent Node", parent, result);
    }

    @Test(expected = DataInconsistencyException.class)
    public void testCheckResultOfChildWithoutParentProperty() throws Exception {
        Node childNode = getNodeMock();
        IDataElement child = new DataElement(childNode);

        doThrow(new PropertyNotFoundException("parent", childNode)).when(nodeService).getParent(childNode,
                NodeServiceRelationshipType.CHILD);

        dataModel.getParentElement(child);
    }

    @Test(expected = FatalException.class)
    public void testCheckDatabaseExceptionOnGetParent() throws Exception {
        Node childNode = getNodeMock();
        IDataElement child = new DataElement(childNode);

        when(nodeService.getParent(childNode, NodeServiceRelationshipType.CHILD)).thenThrow(
                new DatabaseException(new IllegalArgumentException()));

        dataModel.getParentElement(child);
    }
}
