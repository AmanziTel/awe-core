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

package org.amanzi.neo.loader.ui.page.preference.dateformat;

import org.amanzi.neo.loader.ui.page.preference.dateformat.enumeration.DateFormatPreferencePageTableColumns;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DateFormatTableColumnProvider extends ColumnLabelProvider {
    private DateFormatPreferencePageTableColumns columnType;

    /**
     * @param integer
     */
    public DateFormatTableColumnProvider(DateFormatPreferencePageTableColumns columnType) {
        this.columnType = columnType;
    }

    @Override
    public String getText(Object element) {
        switch (columnType) {
        case EXAMPLE_COLUMN:
            return element.toString();
        default:
            break;
        }
        return StringUtils.EMPTY;
    }

    public String getColumnName() {
        return columnType.getName();
    }
}
