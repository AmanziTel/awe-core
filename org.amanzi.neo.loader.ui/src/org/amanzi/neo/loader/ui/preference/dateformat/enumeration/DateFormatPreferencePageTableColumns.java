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

package org.amanzi.neo.loader.ui.preference.dateformat.enumeration;

import org.amanzi.neo.loader.ui.internal.Messages;

/**
 * <p>
 * date types preference page columns
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum DateFormatPreferencePageTableColumns {

    FORMAT_COLUMN(0, Messages.dateTypesPreferencePageDateFormatColumnName), EXAMPLE_COLUMN(1,
            Messages.dateTypesPreferencePageExampleColumnName);
    //TODO: LN: 09.08.2012, why not use ordinal() ?
    private int columnIndex;
    private String name;

    private DateFormatPreferencePageTableColumns(final int columnIndex, final String name) {
        this.columnIndex = columnIndex;
        this.name = name;
    }

    /**
     * @return Returns the columnIndex.
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

}
