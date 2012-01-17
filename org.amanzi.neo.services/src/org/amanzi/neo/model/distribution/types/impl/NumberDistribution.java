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

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.types.ranges.impl.SimpleRange;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.FilterType;
import org.apache.log4j.Logger;

/**
 * <p>
 * Distribution for Number properties Creates range for each number value of property
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class NumberDistribution extends AbstractDistribution<SimpleRange> {
    private static final Logger LOGGER = Logger.getLogger(NumberDistribution.class);

    private NumberDistributionType distrType;
    private int count = 0;

    public NumberDistribution(IDistributionalModel model, INodeType nodeType, String propertyName, NumberDistributionType distrType) {
        super(model, nodeType, propertyName);
        if (distrType == null) {
            LOGGER.error("NumberDistributionType cannot be null");
            throw new IllegalArgumentException("NumberDistributionType cannot be null");
        }
        this.distrType = distrType;
    }

    @Override
    public String getName() {
        return distrType.toString();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Select[] getPossibleSelects() {
        return new Select[] {Select.EXISTS};
    }

    @Override
    protected void createRanges() {
        LOGGER.debug("start createRange()");

        // initialize count of all properties
        count = model.getPropertyCount(nodeType, propertyName);

        Number[] values = new Number[count];
        int i = 0;
        // initialize ranges
        for (Object value : model.getPropertyValues(nodeType, propertyName)) {
            // we are sure that it's a Number
            values[i++] = (Number)value;
        }
        double min = AbstractDistribution.getMinValue(values);
        double max = AbstractDistribution.getMaxValue(values);
        double step = AbstractDistribution.getStep(min, max, distrType.getDelta());

        while (max - min >= EPS) {
            Filter filter = new Filter(FilterType.MORE_OR_EQUALS);
            filter.setExpression(nodeType, propertyName, min);
            Filter subFilter;
            if (max - min - step >= EPS) {
                subFilter = new Filter(FilterType.LESS);
            } else {
                subFilter = new Filter(FilterType.LESS_OR_EQUALS);
            }
            double curMax = max - (min + step) >= EPS ? min + step : max;
            subFilter.setExpression(nodeType, propertyName, curMax);
            filter.addFilter(subFilter);
            ranges.add(new SimpleRange(AbstractDistribution.getNumberDistributionRangeName(min, curMax), filter));

            min += step;
        }

        LOGGER.debug("finish createRange()");
    }

    @Override
    protected Select getDefaultSelect() {
        return null;
    }

    public NumberDistributionType getDistrType() {
        return distrType;
    }
}