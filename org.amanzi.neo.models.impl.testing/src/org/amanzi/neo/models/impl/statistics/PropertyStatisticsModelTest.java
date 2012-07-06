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

package org.amanzi.neo.models.impl.statistics;

import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.IPropertyStatisticsService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicatedNodeException;
import org.amanzi.neo.services.impl.PropertyStatisticsNodeType;
import org.amanzi.neo.services.impl.statistics.IPropertyStatistics;
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
public class PropertyStatisticsModelTest extends AbstractMockitoTest {

    private static final INodeType TEST_NODE_TYPE = PropertyStatisticsNodeType.PROPERTY_STATISTICS;

    private static final String TEST_PROPERTY_NAME = "property";

    private static final String TEST_PROPERTY_VALUE = "value";

    private static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = new GeneralNodeProperties();

    private IPropertyStatisticsService statisticsService;

    private IPropertyStatisticsModel model;

    private IPropertyStatistics vault;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        statisticsService = mock(IPropertyStatisticsService.class);

        model = new PropertyStatisticsModel(GENERAL_NODE_PROPERTIES, statisticsService);
    }

    @Test
    public void testCheckActivityOnFinishUp() throws Exception {
        initializeStatistics();

        model.finishUp();

        verify(statisticsService).saveStatistics(any(Node.class), any(IPropertyStatistics.class));
    }

    @Test
    public void testCheckActivityOnInitialize() throws Exception {
        Node rootNode = initializeStatistics();

        verify(statisticsService).loadStatistics(rootNode);
    }

    @Test(expected = DuplicatedModelException.class)
    public void testCheckDataInconsistencyExceptionOnInitialize() throws Exception {
        doThrow(new DuplicatedNodeException("statistics", "statistics")).when(statisticsService).loadStatistics(any(Node.class));

        ((PropertyStatisticsModel)model).initialize(getNodeMock());
    }

    @Test(expected = DuplicatedModelException.class)
    public void testCheckDataInconsistencyExceptionOnFinishUp() throws Exception {
        initializeStatistics();

        doThrow(new DuplicatedNodeException("statistics", "statistics")).when(statisticsService).saveStatistics(any(Node.class),
                any(IPropertyStatistics.class));

        model.finishUp();
    }

    @Test(expected = FatalException.class)
    public void testCheckDatabaseExceptionOnInitialize() throws Exception {
        doThrow(new DatabaseException(new IllegalArgumentException())).when(statisticsService).loadStatistics(any(Node.class));

        ((PropertyStatisticsModel)model).initialize(getNodeMock());
    }

    @Test(expected = FatalException.class)
    public void testCheckDatabaseExceptionOnFinishUp() throws Exception {
        initializeStatistics();

        doThrow(new DatabaseException(new IllegalArgumentException())).when(statisticsService).saveStatistics(any(Node.class),
                any(IPropertyStatistics.class));

        model.finishUp();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCheckExceptionOnInitializationFromString() throws Exception {
        ((PropertyStatisticsModel)model).initialize(null, null, null);
    }

    @Test
    public void testCheckActivityOnGetCount() throws Exception {
        initializeStatistics();

        model.getCount();

        verify(vault).getCount();
    }

    @Test
    public void testCheckActivityOnGetCountWithNodeType() throws Exception {
        initializeStatistics();

        model.getCount(TEST_NODE_TYPE);

        verify(vault).getCount(TEST_NODE_TYPE);
    }

    @Test
    public void testCheckActivityOnGetPropertyCount() throws Exception {
        initializeStatistics();

        model.getValueCount(TEST_NODE_TYPE, TEST_PROPERTY_NAME, TEST_PROPERTY_VALUE);

        verify(vault).getValueCount(TEST_NODE_TYPE, TEST_PROPERTY_NAME, TEST_PROPERTY_VALUE);
    }

    @Test
    public void testCheckActivityOnGetPropertyNames() throws Exception {
        initializeStatistics();

        model.getPropertyNames();

        verify(vault).getPropertyNames();
    }

    @Test
    public void testCheckActivityOnGetPropertyNamesWithNodeTypes() throws Exception {
        initializeStatistics();

        model.getPropertyNames(TEST_NODE_TYPE);

        verify(vault).getPropertyNames(TEST_NODE_TYPE);
    }

    @Test
    public void testCheckActivityOnGetValues() throws Exception {
        initializeStatistics();

        model.getValues(TEST_NODE_TYPE, TEST_PROPERTY_NAME);

        verify(vault).getValues(TEST_NODE_TYPE, TEST_PROPERTY_NAME);
    }

    @Test
    public void testCheckActivityOnIndex() throws Exception {
        initializeStatistics();

        model.indexProperty(TEST_NODE_TYPE, TEST_PROPERTY_NAME, TEST_PROPERTY_VALUE);

        verify(vault).indexProperty(TEST_NODE_TYPE, TEST_PROPERTY_NAME, TEST_PROPERTY_VALUE);
    }

    private Node initializeStatistics() throws Exception {
        Node rootNode = getNodeMock();
        mockStatistics(rootNode);

        ((PropertyStatisticsModel)model).initialize(rootNode);

        return rootNode;
    }

    private void mockStatistics(final Node node) throws Exception {
        vault = mock(IPropertyStatistics.class);

        when(statisticsService.loadStatistics(node)).thenReturn(vault);
    }
}
