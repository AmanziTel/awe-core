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
import org.amanzi.neo.services.enums.INodeType;
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
public class StringRange extends DefaultRange {
    private String strRangeValue;
    private static Filter filter;
    private static INodeType type;

    protected static Filter init(String range,INodeType type) {
        filter = new Filter(FilterType.EQUALS);
        filter.setExpression(type, INeoConstants.PROPERTY_NAME_NAME, range);
        return filter;
    }

    public StringRange(String rangeValue,INodeType type) {
        super(init(rangeValue,type));
        strRangeValue = rangeValue;
    }

}
