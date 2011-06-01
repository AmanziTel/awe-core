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

package org.amanzi.neo.services.networkModel;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.FilterType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class NumberRange extends DefaultRange {
    private static Number minimum;
    private static Number maximum;
    private static Filter filter;
    private static Filter filterAddition;

    protected static Filter init(Number min, Number max) {
        filter = new Filter(FilterType.MORE_OR_EQUALS);
        filter.setExpression(NodeTypes.SITE, INeoConstants.PROPERTY_NAME_MIN_VALUE, min);
        filterAddition = new Filter(FilterType.LESS_OR_EQUALS);
        filterAddition.setExpression(NodeTypes.SITE, INeoConstants.PROPERTY_NAME_MAX_VALUE, max);
        filter.addFilter(filterAddition);
        return filter;
    }

    public NumberRange(Number min, Number max) {
        super(init(min, max));
        minimum = min;
        maximum = max;

    }

}
