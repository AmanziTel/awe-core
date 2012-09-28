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

package org.amanzi.awe.filters.impl;

import org.amanzi.awe.filters.impl.internal.AbstractFilter;
import org.amanzi.neo.dto.IDataElement;
import org.apache.commons.lang3.Range;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class RangeFilter extends AbstractFilter<Number> {

    public enum RangeFilterType {
        INCLUDE_START_AND_END,
        INCLUDE_START_EXCLUDE_END,
        EXCLUDE_START_INCLUDE_END,
        EXCLUDE_START_AND_END;
    }

    private final Range<Number> range;

    private final RangeFilterType filterType;

    /**
     * @param propertyName
     */
    public RangeFilter(final String propertyName, final Range<Number> range, final RangeFilterType filterType) {
        super(propertyName);
        this.range = range;
        this.filterType = filterType;
    }

    @Override
    public boolean matches(final IDataElement element) {
        Number value = getElementValue(element);
        if (value != null) {
            double dValue = value.doubleValue();
            boolean contains = range.contains(dValue);

            if (contains) {
                switch (filterType) {
                case EXCLUDE_START_AND_END:
                    return contains && !range.isStartedBy(dValue) && !range.isStartedBy(dValue);
                case EXCLUDE_START_INCLUDE_END:
                    return contains && !range.isStartedBy(dValue);
                case INCLUDE_START_AND_END:
                    return contains;
                case INCLUDE_START_EXCLUDE_END:
                    return contains && !range.isStartedBy(dValue);
                }
            }
        }

        return false;
    }

}
