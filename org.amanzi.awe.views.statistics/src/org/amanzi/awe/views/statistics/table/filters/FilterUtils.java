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

package org.amanzi.awe.views.statistics.table.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common actions for statistics filtering
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FilterUtils {

    private static class FilterUtilsConstatntHolder {
        private static final FilterUtils FILTERS_UTILS = new FilterUtils();
    }

    public static final FilterUtils getInstatnce() {
        return FilterUtilsConstatntHolder.FILTERS_UTILS;
    }

    private static final String COMPARER_PATTERN = ".*%s.*";

    private FilterUtils() {

    }

    public boolean match(String filterText, String secondString) {
        Pattern pattern = Pattern.compile(String.format(COMPARER_PATTERN, filterText), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(secondString);
        return matcher.matches();
    }
}
