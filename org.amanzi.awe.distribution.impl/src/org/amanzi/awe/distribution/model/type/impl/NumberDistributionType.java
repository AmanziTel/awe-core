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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.awe.distribution.model.type.impl.internal.AbstractDistributionType;
import org.amanzi.awe.distribution.model.type.impl.internal.SimpleRange;
import org.amanzi.awe.filters.impl.RangeFilter;
import org.amanzi.awe.filters.impl.RangeFilter.RangeFilterType;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.math3.util.Precision;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NumberDistributionType extends AbstractDistributionType<SimpleRange> {

    private static final int DOUBLE_SCALE = 4;

    private static final double PRECISION_DELTA = 0.00001;

    private static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    private final NumberDistributionRange numberDistributionRange;

    private Set<SimpleRange> ranges;

    /**
     * @param model
     * @param nodeType
     * @param propertyName
     * @param canChangeColors
     */
    public NumberDistributionType(final IPropertyStatisticalModel model, final INodeType nodeType, final String propertyName,
            final NumberDistributionRange numberDistributionRange, final Select select) {
        super(model, nodeType, propertyName, select);
        this.numberDistributionRange = numberDistributionRange;
    }

    @Override
    public String getName() {
        return numberDistributionRange.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Set<SimpleRange> getRanges() {
        if (ranges == null) {

            ranges = new LinkedHashSet<SimpleRange>();

            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;

            for (final Object value : getModel().getPropertyStatistics().getValues(getNodeType(), getPropertyName())) {
                final double currentValue = ((Number)value).doubleValue();

                min = Math.min(min, currentValue);
                max = Math.max(max, currentValue);
            }

            final double step = getStep(min, max, numberDistributionRange.getDelta());

            while (Precision.compareTo(max, min, PRECISION_DELTA) > 0) {
                final boolean includeMax = Precision.compareTo(max, min + step, PRECISION_DELTA) > 0;

                final double curMax = includeMax ? increaseMinimum(min, step) : max;
                final RangeFilterType filterType = includeMax ? RangeFilterType.INCLUDE_START_AND_END
                        : RangeFilterType.INCLUDE_START_EXCLUDE_END;

                final Range range = Range.between(min, curMax);

                final RangeFilter filter = new RangeFilter(getPropertyName(), range, filterType);
                ranges.add(new SimpleRange(getNumberDistributionRangeName(min, curMax), filter));

                min = increaseMinimum(min, step);
            }
        }

        return ranges;
    }

    protected double increaseMinimum(final double previousMinimum, final double step) {
        return previousMinimum + step;
    }

    private double getStep(final double min, final double max, final int delta) {
        double res = (max - min) / delta;
        final double scaleNum = Math.pow(10, DOUBLE_SCALE);
        res = Math.round(res * scaleNum) / scaleNum;
        return res;
    }

    protected String getNumberDistributionRangeName(final double min, final double max) {
        final StringBuilder sb = new StringBuilder();

        sb.append(DECIMAL_FORMAT.format(min)).append(" - ").append(DECIMAL_FORMAT.format(max));
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof NumberDistributionType) {
            final NumberDistributionType type = (NumberDistributionType)o;
            return super.equals(type) && ObjectUtils.equals(numberDistributionRange, type.numberDistributionRange);
        }

        return false;
    }
}
