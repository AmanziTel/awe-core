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

package org.amanzi.awe.nem.ui.properties.table;

import org.amanzi.awe.nem.ui.messages.NEMMessages;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum PropertyColumns {

    NAME(NEMMessages.COLUMN_NAME_LABEL), TYPE(NEMMessages.COLUMN_TYPE_LABEL), DEFAULT_VALUE(NEMMessages.COLUMN_DEFAULT_VALUE_LABEL);

    private String name;

    /**
     * 
     */
    private PropertyColumns(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
