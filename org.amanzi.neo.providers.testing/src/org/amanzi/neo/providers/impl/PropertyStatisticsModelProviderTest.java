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

package org.amanzi.neo.providers.impl;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.impl.statistics.PropertyStatisticsModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
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
public class PropertyStatisticsModelProviderTest extends AbstractMockitoTest {

    public static class TestStatisticalModel extends AbstractModel implements IPropertyStatisticalModel {

        /**
         * @param nodeService
         * @param generalNodeProperties
         */
        public TestStatisticalModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
            super(nodeService, generalNodeProperties);
            // TODO Auto-generated constructor stub
        }

        @Override
        public IDataElement getParentElement(final IDataElement childElement) throws ModelException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void finishUp() throws ModelException {
            // TODO Auto-generated method stub

        }

        @Override
        public Iterable<IDataElement> getChildren(final IDataElement parentElement) throws ModelException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public IPropertyStatisticsModel getPropertyStatistics() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Iterable<IDataElement> getAllElementsByType(final INodeType nodeType) throws ModelException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void deleteElement(IDataElement element) throws ModelException {
            // TODO Auto-generated method stub

        }

    }

    private PropertyStatisticsModel model;

    private PropertyStatisticsModelProvider provider;

    private TestStatisticalModel parent;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        model = mock(PropertyStatisticsModel.class);
        parent = mock(TestStatisticalModel.class);

        provider = new PropertyStatisticsModelProvider(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckExceptionWhenParentIsEmpty() throws Exception {
        provider.getPropertyStatistics(null);
    }

    @Test
    public void testCheckModelActivity() throws Exception {
        spyStatisticsProvider();

        Node parentNode = getNodeMock();
        when(parent.getRootNode()).thenReturn(parentNode);

        provider.getPropertyStatistics(parent);

        verify(parent).getRootNode();
        verify(model).initialize(parentNode);
    }

    @Test
    public void testCheckCache() throws Exception {
        spyStatisticsProvider();

        Node parentNode = getNodeMock();
        when(parent.getRootNode()).thenReturn(parentNode);

        provider.getPropertyStatistics(parent);
        provider.getPropertyStatistics(parent);

        verify(parent, times(2)).getRootNode();
        verify(model).initialize(parentNode);
    }

    @Test(expected = ModelException.class)
    public void testCheckUnderlyingException() throws Exception {
        spyStatisticsProvider();

        Node parentNode = getNodeMock();
        when(parent.getRootNode()).thenReturn(parentNode);
        doThrow(new FatalException(new IllegalArgumentException())).when(model).initialize(parentNode);

        provider.getPropertyStatistics(parent);
    }

    @Test
    public void testCheckResult() throws Exception {
        spyStatisticsProvider();

        Node parentNode = getNodeMock();
        when(parent.getRootNode()).thenReturn(parentNode);

        IPropertyStatisticsModel result = provider.getPropertyStatistics(parent);

        assertEquals("Unexpected property statistics model", model, result);

    }

    private void spyStatisticsProvider() {
        provider = spy(provider);
        when(provider.createInstance()).thenReturn(model);
    }

}
