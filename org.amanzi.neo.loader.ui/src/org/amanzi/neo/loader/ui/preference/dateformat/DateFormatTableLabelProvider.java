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

package org.amanzi.neo.loader.ui.preference.dateformat;

import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.ui.icons.IconManager;
import org.amanzi.neo.dateformat.DateFormatManager;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DateFormatTableLabelProvider extends ColumnLabelProvider {
  
    private static final Date SAMPLE_DATE = Calendar.getInstance().getTime();
    private static final Image CHECKED = IconManager.getInstance().getImage("checked");
    private static final Image UNCHECKED = IconManager.getInstance().getImage("unchecked");

    private DateFormatPreferencePageTableColumns columnType;
    private FormatTableViewer viewer;

    /**
     * @param integer
     */
    public DateFormatTableLabelProvider(DateFormatPreferencePageTableColumns columnType) {
        this.columnType = columnType;
    }

    @Override
    public String getText(Object element) {
        switch (columnType) {
        case EXAMPLE_COLUMN:
            return DateFormatManager.getInstance().parseDateToString(SAMPLE_DATE, element.toString());
        case FORMAT_COLUMN:
            return element.toString();
        default:
            break;
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getImage(Object element) {
        switch (columnType) {
        case IS_DEFAULT_COLUMN:
            if (element.equals(viewer.getDefaultFormat())) {
                return CHECKED;
            }
            return UNCHECKED;
        default:
            break;
        }
        return null;

    }

    public String getColumnName() {
        return columnType.getName();
    }

    /**
     * initialize label provider
     * 
     * @param viewer
     */
    protected void initViewer(FormatTableViewer viewer) {
        this.viewer = viewer;
    }
}
