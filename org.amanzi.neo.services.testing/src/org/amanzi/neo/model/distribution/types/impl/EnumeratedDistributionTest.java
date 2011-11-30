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

package org.amanzi.neo.model.distribution.types.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistribution.Select;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.types.ranges.impl.SimpleRange;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.DistributionService;
import org.amanzi.neo.services.DistributionService.DistributionNodeTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.filters.ISimpleFilter;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * Tests on String Distribution
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class EnumeratedDistributionTest extends AbstractNeoServiceTest {

    private static final INodeType DEFAULT_NODE_TYPE = DistributionNodeTypes.AGGREGATION_BAR;

    private static final String DEFAULT_PROPERTY_NAME = "default_property_name";

    private static final int DEFAULT_NUMBER_OF_PROPERTIES = 100;

    private static final String[] DEFAULT_PROPERTY_NAMES = new String[] {"property1", "property2", "property3"};

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new LogStarter().earlyStartup();
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithoutModel() throws Exception {
        new EnumeratedDistribution(null, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithoutNodeType() throws Exception {
        IDistributionalModel model = getDistributionalModel();
        
        new EnumeratedDistribution(model, null, DEFAULT_PROPERTY_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithoutPropertyName() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithEmptyPropertyName() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, StringUtils.EMPTY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithTooManyPropertyValues() throws Exception {
        IDistributionalModel model = mock(IDistributionalModel.class);
        String[] values = new String[EnumeratedDistribution.MAX_PROPERTY_VALUE_COUNT + 1];
        for (int i = 0; i <= EnumeratedDistribution.MAX_PROPERTY_VALUE_COUNT; i ++) {
            values[i] = String.valueOf(i);
        }
        when(model.getPropertyValues(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME)).thenReturn(values);
        
        new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME).init();
    }

    @Test
    public void checkResultsOfCreation() throws Exception {
        IDistributionalModel model = getDistributionalModel();
        IDistribution<SimpleRange> distribution = new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        assertEquals("Unexpected name of Distribution", EnumeratedDistribution.STRING_DISTRIBUTION_NAME, distribution.getName());
        assertEquals("Unexpected NodeType of Distribution", DEFAULT_NODE_TYPE, distribution.getNodeType());
        assertNotNull("Initially Range of Distribution should not be null", distribution.getRanges());
        assertTrue("Initially ranges of Distribution should be empty", distribution.getRanges().isEmpty());
        assertTrue("Initially count of Distribution should be zero", distribution.getCount() == 0);
    }

    @Test
    public void checkCountAfterDistributionInitialization() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        IDistribution<SimpleRange> distribution = new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        distribution.init();

        assertEquals("Unexpected number of values to Analyze", DEFAULT_NUMBER_OF_PROPERTIES, distribution.getCount());
    }

    @Test
    public void checkNumberOfRangesAfterInitialization() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        IDistribution<SimpleRange> distribution = new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        distribution.init();

        assertEquals("Unexpected number of ranges in Distribution", DEFAULT_PROPERTY_NAMES.length, distribution.getRanges().size());
    }

    @Test
    public void checkRangeNamesAfterInitialization() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        IDistribution<SimpleRange> distribution = new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        distribution.init();

        for (int i = 0; i < DEFAULT_PROPERTY_NAMES.length; i++) {
            assertEquals("Unexpected name of Range", DEFAULT_PROPERTY_NAMES[i], distribution.getRanges().get(i).getName());
        }
    }

    @Test
    public void checkRangeColorsAfterInitialization() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        IDistribution<SimpleRange> distribution = new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        distribution.init();

        for (int i = 0; i < DEFAULT_PROPERTY_NAMES.length; i++) {
            assertEquals("Unexpected color of Range", null, distribution.getRanges().get(i).getColor());
        }
    }

    @Test
    public void checkRangeFiltersAfterInitialization() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        IDistribution<SimpleRange> distribution = new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        distribution.init();

        IDataElement[] nodesToCheck = getMockedNodesForFilter();

        for (int i = 0; i < DEFAULT_PROPERTY_NAMES.length; i++) {
            ISimpleFilter filter = distribution.getRanges().get(i).getFilter();

            assertTrue("Filter should accept current node", filter.check(nodesToCheck[i]));
        }
    }

    @Test
    public void checkActionsOfModelOnInitialization() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        IDistribution<SimpleRange> distribution = new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        distribution.init();

        verify(model).getPropertyCount(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        verify(model).getPropertyValues(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        verifyNoMoreInteractions(model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetIllegalSelect() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        IDistribution<SimpleRange> distribution = new EnumeratedDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME);
        distribution.setSelect(Select.MAX);
    }

    /**
     * Returns array of mocked nodes to check filter
     * 
     * @return
     */
    private IDataElement[] getMockedNodesForFilter() {
        List<IDataElement> result = new ArrayList<IDataElement>();
        for (int i = 0; i < DEFAULT_PROPERTY_NAMES.length; i++) {
            Node node = mock(Node.class);

            when(node.hasProperty(DistributionService.TYPE)).thenReturn(true);
            when(node.hasProperty(DEFAULT_PROPERTY_NAME)).thenReturn(true);
            when(node.getProperty(DistributionService.TYPE, StringUtils.EMPTY)).thenReturn(DEFAULT_NODE_TYPE.getId());
            when(node.getProperty(DEFAULT_PROPERTY_NAME)).thenReturn(DEFAULT_PROPERTY_NAMES[i]);

            result.add(new DataElement(node));
        }

        return result.toArray(new IDataElement[DEFAULT_PROPERTY_NAMES.length]);
    }

    /**
     * Creates mocked DistributionalModel
     * 
     * @param clazz
     * @return
     */
    private IDistributionalModel getDistributionalModel() {
        IDistributionalModel model = mock(IDistributionalModel.class);

        when(model.getPropertyCount(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME)).thenReturn(DEFAULT_NUMBER_OF_PROPERTIES);
        when(model.getPropertyValues(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME)).thenReturn(DEFAULT_PROPERTY_NAMES);

        return model;
    }

}
