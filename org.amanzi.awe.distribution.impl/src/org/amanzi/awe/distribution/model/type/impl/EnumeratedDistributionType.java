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

import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.awe.distribution.model.type.impl.internal.AbstractDistributionType;
import org.amanzi.awe.distribution.model.type.impl.internal.SimpleRange;
import org.amanzi.awe.filters.IFilter;
import org.amanzi.awe.filters.impl.EqualsFilter;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class EnumeratedDistributionType extends AbstractDistributionType<SimpleRange> {

    /*
     * Maximum count of property values, if count > max that exception will be thrown
     */
    protected static final int MAX_PROPERTY_VALUE_COUNT = 100;

    protected static final String ENUMERATED_DISTRIBUTION_NAME = "auto";

    private Set<SimpleRange> ranges;

    /**
     * @param model
     * @param nodeType
     * @param propertyName
     * @param canChangeColors
     */
    public EnumeratedDistributionType(final IPropertyStatisticalModel model, final INodeType nodeType, final String propertyName) {
        super(model, nodeType, propertyName, true);
    }

    @Override
    public String getName() {
        return ENUMERATED_DISTRIBUTION_NAME;
    }

    @Override
    public Set<SimpleRange> getRanges() {
        if (ranges == null) {
            ranges = new LinkedHashSet<SimpleRange>();

            Set<Object> propertyValues = getModel().getPropertyStatistics().getValues(getNodeType(), getPropertyName());

            if (propertyValues.size() > MAX_PROPERTY_VALUE_COUNT) {
                //TODO: LN: throw an error
            }

            for (Object value : propertyValues) {
                String sValue = value.toString();

                IFilter filter = new EqualsFilter<Object>(getPropertyName(), sValue);
                ranges.add(new SimpleRange(sValue, filter));
            }
        }
        return ranges;
    }

}
