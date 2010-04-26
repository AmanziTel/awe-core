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

package org.amanzi.awe.filters.experimental;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of composite filters or filters
 * 
 * @see
 * <li>org.amanzi.awe.filters.experimental.CompositeFilter
 * <li> org.amanzi.awe.filters.experimental.Filter
 * @author Pechko_E
 * @since 1.0.0
 */
public class GroupFilter {
    private String property;
    private List<IFilter> filters = new ArrayList<IFilter>(0);

    public GroupFilter(String property) {
        super();
        this.property = property;
    }

    /**
     * @return Returns the filters.
     */
    public List<IFilter> getFilters() {
        return filters;
    }

    /**
     * @return Returns the property.
     */
    public String getProperty() {
        return property;
    }
    

}
