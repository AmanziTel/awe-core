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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistribution;
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
 * <p>
 * Tests for EnumeratedDistribution
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class NumberDistributionTest extends AbstractNeoServiceTest {

    private static final String DEFAULT_PROPERTY_NAME = "property_name";
    private static final INodeType DEFAULT_NODE_TYPE = DistributionNodeTypes.AGGREGATION_BAR;
    private static final Double[] DEFAULT_VALUES = new Double[] {5.2, 0.97, 1.13, 3.91, 12.2332};

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new LogStarter().earlyStartup();
        clearServices();
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateWithoutNumberDistributionType() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        new NumberDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME, null);
    }

    private IDistributionalModel getDistributionalModel() {
        IDistributionalModel model = mock(IDistributionalModel.class);

        when(model.getPropertyCount(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME)).thenReturn(DEFAULT_VALUES.length);
        when(model.getPropertyValues(DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME)).thenReturn(DEFAULT_VALUES);

        return model;
    }

    @Test
    public void checkResultsOfCreation() throws Exception {
        IDistributionalModel model = getDistributionalModel();
        NumberDistributionType distrType = NumberDistributionType.I10;

        IDistribution<SimpleRange> distribution = new NumberDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME, distrType);

        assertEquals("Unexpected name of Distribution", NumberDistribution.STRING_DISTRIBUTION_NAME, distribution.getName());
        assertEquals("Unexpected NodeType of Distribution", DEFAULT_NODE_TYPE, distribution.getNodeType());
        assertNotNull("Initially Range of Distribution should not be null", distribution.getRanges());
        assertTrue("Initially ranges of Distribution should be empty", distribution.getRanges().isEmpty());
        assertTrue("Initially count of Distribution should be zero", distribution.getCount() == 0);
    }

    @Test
    public void checkNumberOfRangesAfterInit() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        for (NumberDistributionType distrType : NumberDistributionType.values()) {
            IDistribution<SimpleRange> distribution = new NumberDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
                    distrType);
            distribution.init();

            double max = AbstractDistribution.getMaxValue(DEFAULT_VALUES);
            double min = AbstractDistribution.getMinValue(DEFAULT_VALUES);
            int rangeCnt = getRangeCnt(min, max, distrType);
            assertEquals("Unexpected number of ranges in Distribution", rangeCnt, distribution.getRanges().size());
        }

    }

    @Test
    public void checkRangeNamesAfterInitialization() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        for (NumberDistributionType distrType : NumberDistributionType.values()) {
            IDistribution<SimpleRange> distribution = new NumberDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
                    distrType);
            distribution.init();

            double min = AbstractDistribution.getMinValue(DEFAULT_VALUES);
            double max = AbstractDistribution.getMaxValue(DEFAULT_VALUES);
            double step = AbstractDistribution.getStep(min, max, distrType.getDelta());
            int rangeCnt = getRangeCnt(min, max, distrType);

            for (int i = 0; i < rangeCnt; i++) {
                String name = AbstractDistribution.getNumberDistributionRangeName(min, (min + step) < max ? min + step : max);
                assertEquals("Unexpected name of Range", name, distribution.getRanges().get(i).getName());
                min += step;
            }
        }
    }

    @Test
    public void checkRangeFiltersAfterInitialization() throws Exception {
        IDistributionalModel model = getDistributionalModel();

        for (NumberDistributionType distrType : NumberDistributionType.values()) {
            IDistribution<SimpleRange> distribution = new NumberDistribution(model, DEFAULT_NODE_TYPE, DEFAULT_PROPERTY_NAME,
                    distrType);
            distribution.init();

            double min = AbstractDistribution.getMinValue(DEFAULT_VALUES);
            double max = AbstractDistribution.getMaxValue(DEFAULT_VALUES);
            double step = AbstractDistribution.getStep(min, max, distrType.getDelta());
            int rangeCnt = getRangeCnt(min, max, distrType);

            for (int i = 0; i < rangeCnt; i++) {
                double curMax = (min + step) < max ? min + step : max + AbstractDistribution.EPS;
                for (int j = 0; j < DEFAULT_VALUES.length; j++) {
                    ISimpleFilter filter = distribution.getRanges().get(i).getFilter();
                    if (DEFAULT_VALUES[j] >= min && DEFAULT_VALUES[j] < curMax) {
                        assertTrue("Filter should accept current node", filter.check(getMockedNodeForFilter(DEFAULT_VALUES[j])));
                    } else {
                        assertFalse("Filter should not accept current node",
                                filter.check(getMockedNodeForFilter(DEFAULT_VALUES[j])));
                    }
                }
                min += step;
            }
        }
    }

    private int getRangeCnt(double min, double max, NumberDistributionType distrType) {
        double step = AbstractDistribution.getStep(min, max, distrType.getDelta());
        int rangeCnt = 0;
        while (max - min >= AbstractDistribution.EPS) {
            rangeCnt++;
            min += step;
        }
        return rangeCnt;
    }

    private IDataElement getMockedNodeForFilter(double value) {
        Node node = mock(Node.class);

        when(node.hasProperty(DistributionService.TYPE)).thenReturn(true);
        when(node.hasProperty(DEFAULT_PROPERTY_NAME)).thenReturn(true);
        when(node.getProperty(DistributionService.TYPE, StringUtils.EMPTY)).thenReturn(DEFAULT_NODE_TYPE.getId());
        when(node.getProperty(DEFAULT_PROPERTY_NAME)).thenReturn(value);

        return new DataElement(node);
    }
}
