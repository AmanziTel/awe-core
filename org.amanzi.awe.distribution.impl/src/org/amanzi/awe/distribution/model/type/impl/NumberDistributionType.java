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

package org.amanzi.awe.distribution.model.type.impl;

import java.util.Set;

import org.amanzi.awe.distribution.model.type.impl.internal.AbstractDistributionType;
import org.amanzi.awe.distribution.model.type.impl.internal.SimpleRange;
import org.amanzi.awe.filters.impl.RangeFilter;
import org.amanzi.awe.filters.impl.RangeFilter.RangeFilterType;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.commons.lang3.Range;
import org.apache.commons.math3.util.Precision;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NumberDistributionType extends AbstractDistributionType<SimpleRange> {

    private static final int DOUBLE_SCALE = 4;

    private static final double PRECISION_DELTA = 0.00001;

    private final NumberDistributionRange numberDistributionRange;

    private Set<SimpleRange> ranges;

    /**
     * @param model
     * @param nodeType
     * @param propertyName
     * @param canChangeColors
     */
    public NumberDistributionType(final IPropertyStatisticalModel model, final INodeType nodeType, final String propertyName,
            final NumberDistributionRange numberDistributionRange) {
        super(model, nodeType, propertyName, true);
        this.numberDistributionRange = numberDistributionRange;
    }

    @Override
    public String getName() {
        return numberDistributionRange.toString();
    }

    @Override
    public Set<SimpleRange> getRanges() {
        if (ranges == null) {

            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;



            for (Object value : getModel().getPropertyStatistics().getValues(getNodeType(), getPropertyName())) {
                double currentValue = ((Number)value).doubleValue();

                min = Math.min(min, currentValue);
                max = Math.max(max, currentValue);
            }

            double step = getStep(min, max, numberDistributionRange.getDelta());

            while (!Precision.equals(max, min, PRECISION_DELTA)) {
                boolean includeMax = Precision.compareTo(max, min + step, PRECISION_DELTA) > 0;

                double curMax = includeMax ? min + step : max;
                RangeFilterType filterType = includeMax ? RangeFilterType.INCLUDE_START_AND_END : RangeFilterType.INCLUDE_START_EXCLUDE_END;

                Range range = Range.between(min, curMax);

                RangeFilter filter = new RangeFilter(getPropertyName(), range, filterType);
                ranges.add(new SimpleRange(getNumberDistributionRangeName(min, curMax), filter));

                min+= step;
            }
        }

        return ranges;
    }

    private double getStep(final double min, final double max, final int delta) {
        double res = (max - min) / delta;
        double scaleNum = Math.pow(10, DOUBLE_SCALE);
        res = Math.round(res * scaleNum) / scaleNum;
        return res;
    }

    private String getNumberDistributionRangeName(final double min, final double max) {
        StringBuilder sb = new StringBuilder();
        sb.append(min).append(" - ").append(max);
        return sb.toString();
    }

}
