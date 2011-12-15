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

package org.amanzi.neo.services.filters;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * <p>
 *Filters type describe behavior with next filter
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public enum FilterType{
    EQUALS, LIKE, MORE, LESS, MORE_OR_EQUALS, LESS_OR_EQUALS, EMPTY, NOT_EMPTY;
    
    public static FilterType getByName(String name) {
        if (StringUtils.isEmpty(name))
            return null;
        for (FilterType filterType : FilterType.values()) {
            if (filterType.name().toLowerCase().equals(name.toLowerCase())) {
                return filterType;
            }
        }
        return null;
    }
}