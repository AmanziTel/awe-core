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
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractDataseModelTest extends AbstractMockitoTest {

    private static class TestDatasetModel extends AbstractDatasetModel {

        /**
         * @param nodeService
         * @param generalNodeProperties
         */
        public TestDatasetModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
            super(nodeService, generalNodeProperties);
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

    }

    private IIndexModel indexModel;

    private IPropertyStatisticsModel statisticsModel;

    private AbstractDatasetModel model;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        indexModel = mock(IIndexModel.class);
        statisticsModel = mock(IPropertyStatisticsModel.class);

        model = new TestDatasetModel(null, null);

        model.setIndexModel(indexModel);
        model.setPropertyStatisticsModel(statisticsModel);
    }

    @Test
    public void testCheckActivityOnFinishUp() throws Exception {
        model.finishUp();

        verify(indexModel).finishUp();
        verify(statisticsModel).finishUp();
    }

}
