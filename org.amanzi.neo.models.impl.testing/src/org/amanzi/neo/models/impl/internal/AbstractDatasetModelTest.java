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
import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex;
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
public class AbstractDatasetModelTest extends AbstractMockitoTest {

    private static final String PROPERTY_NAME = "propertyName";

    private static final INodeType TYPE_1 = new INodeType() {

        @Override
        public String getId() {
            return "type 1";
        }
    };

    private static final INodeType TYPE_2 = new INodeType() {

        @Override
        public String getId() {
            return "type 2";
        }
    };

    public static class TestDatasetModel extends AbstractDatasetModel {

        /**
         * @param nodeService
         * @param generalNodeProperties
         */
        public TestDatasetModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
                final IGeoNodeProperties geoNodeProperties) {
            super(nodeService, generalNodeProperties, geoNodeProperties);
        }

        @Override
        protected INodeType getModelType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public IDataElement getParentElement(final IDataElement childElement) throws ModelException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void initializeIndexes() {
            // TODO Auto-generated method stub

        }

    }

    private IIndexModel indexModel;

    private IPropertyStatisticsModel statisticsModel;

    private AbstractDatasetModel model;

    private Node rootNode;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        indexModel = mock(IIndexModel.class);
        statisticsModel = mock(IPropertyStatisticsModel.class);
        rootNode = mock(Node.class);

        model = spy(new TestDatasetModel(null, null, null));

        model.setIndexModel(indexModel);
        model.setPropertyStatisticsModel(statisticsModel);

        doReturn(rootNode).when(model).getRootNode();
    }

    @Test
    public void testCheckActivityOnFinishUp() throws Exception {
        model.finishUp();

        verify(indexModel).finishUp();
        verify(statisticsModel).finishUp();
    }

    @Test
    public void testCheckActivityOnRegisteringMultiPropertyIndexes() throws Exception {
        model.registerMultiPropertyIndexes(TYPE_1, PROPERTY_NAME);

        verify(indexModel).getMultiPropertyIndex(TYPE_1, rootNode, PROPERTY_NAME);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnFinishUpForIndexes() throws Exception {
        MultiPropertyIndex<Object> index = mock(MultiPropertyIndex.class);
        when(indexModel.getMultiPropertyIndex(TYPE_1, rootNode, PROPERTY_NAME)).thenReturn(index);

        model.registerMultiPropertyIndexes(TYPE_1, PROPERTY_NAME);
        model.finishUp();

        verify(index).finishUp();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnIndexNode() throws Exception {
        MultiPropertyIndex<Object> index = mock(MultiPropertyIndex.class);
        when(indexModel.getMultiPropertyIndex(TYPE_1, rootNode, PROPERTY_NAME)).thenReturn(index);

        model.registerMultiPropertyIndexes(TYPE_1, PROPERTY_NAME);

        Node node = getNodeMock();

        model.index(TYPE_1, node);

        verify(index).add(node);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnIndexNodeWithWrongType() throws Exception {
        MultiPropertyIndex<Object> index = mock(MultiPropertyIndex.class);
        when(indexModel.getMultiPropertyIndex(TYPE_1, rootNode, PROPERTY_NAME)).thenReturn(index);

        model.registerMultiPropertyIndexes(TYPE_2, PROPERTY_NAME);

        Node node = getNodeMock();

        model.index(TYPE_1, node);

        verifyNoMoreInteractions(index);
    }
}
