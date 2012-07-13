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

package org.amanzi.neo.providers.impl.internal;

import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.impl.internal.AbstractDatasetModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.IIndexModelProvider;
import org.amanzi.neo.providers.IPropertyStatisticsModelProvider;
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
public class AbstractDatasetModelProviderTest extends AbstractMockitoTest {

    public static class TestDatasetModelProvider extends AbstractDatasetModelProvider<IModel, IModel, AbstractDatasetModel> {

        /**
         * @param nodeService
         * @param generalNodeProperties
         * @param indexModelProvider
         * @param propertyStatisticsModelProvider
         */
        protected TestDatasetModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
                final IIndexModelProvider indexModelProvider, final IPropertyStatisticsModelProvider propertyStatisticsModelProvider) {
            super(nodeService, generalNodeProperties, indexModelProvider, propertyStatisticsModelProvider, null);
        }

        @Override
        protected INodeType getModelType() {
            return null;
        }

        @Override
        protected AbstractDatasetModel createInstance() {
            return null;
        }

        @Override
        protected Class< ? extends IModel> getModelClass() {
            return null;
        }

    }

    private AbstractDatasetModelProvider<IModel, IModel, AbstractDatasetModel> provider;

    private IIndexModelProvider indexModelProvider;

    private IPropertyStatisticsModelProvider propertyStatisticsModelProvider;

    private AbstractDatasetModel model;

    private IIndexModel indexModel;

    private IPropertyStatisticsModel statisitcsModel;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        indexModelProvider = mock(IIndexModelProvider.class);
        propertyStatisticsModelProvider = mock(IPropertyStatisticsModelProvider.class);
        model = mock(AbstractDatasetModel.class);

        indexModel = mock(IIndexModel.class);
        statisitcsModel = mock(IPropertyStatisticsModel.class);

        provider = new TestDatasetModelProvider(null, null, indexModelProvider, propertyStatisticsModelProvider);
    }

    @Test
    public void testCheckProviderActivityOnPostInitialization() throws Exception {
        when(indexModelProvider.getIndexModel(model)).thenReturn(indexModel);
        when(propertyStatisticsModelProvider.getPropertyStatistics(model)).thenReturn(statisitcsModel);

        provider.postInitialize(model);

        verify(indexModelProvider).getIndexModel(model);
        verify(propertyStatisticsModelProvider).getPropertyStatistics(model);
    }

    @Test
    public void testCheckModelActivityOnPostInitialization() throws Exception {
        when(indexModelProvider.getIndexModel(model)).thenReturn(indexModel);
        when(propertyStatisticsModelProvider.getPropertyStatistics(model)).thenReturn(statisitcsModel);

        provider.postInitialize(model);

        verify(model).setIndexModel(indexModel);
        verify(model).setPropertyStatisticsModel(statisitcsModel);
    }

    @Test
    public void testCheckIndexesInitializedOnPostInitialization() throws Exception {
        provider.postInitialize(model);

        verify(model).initializeIndexes();
    }
}
