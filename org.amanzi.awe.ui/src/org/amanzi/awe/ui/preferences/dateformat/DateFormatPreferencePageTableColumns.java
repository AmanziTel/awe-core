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

package org.amanzi.awe.ui.preferences.dateformat;

import org.amanzi.awe.ui.internal.Messages;

/**
 * <p>
 * date types preference page columns
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum DateFormatPreferencePageTableColumns {

    FORMAT_COLUMN(Messages.dateTypesPreferencePageDateFormatColumnName), EXAMPLE_COLUMN(
            Messages.dateTypesPreferencePageExampleColumnName), IS_DEFAULT_COLUMN(Messages.dateTypesPreferencePageIsDefault);
    private String name;

    private DateFormatPreferencePageTableColumns(final String name) {
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

}
