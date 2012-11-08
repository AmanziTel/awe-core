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
public enum SynonymsTableColumns {

    TYPE(NEMMessages.SYNONYM_TABLE_TYPE_COLUMN, 0), PROPERTY(NEMMessages.SYNONYM_TABLE_PROPERTY_COLUMN, 1), HEADER(
            NEMMessages.SYNONYM_TABLE_HEADER_COLUMN, 2);

    public static SynonymsTableColumns findByIndex(final int i) {
        for (SynonymsTableColumns column : SynonymsTableColumns.values()) {
            if (column.getIndex() == i) {
                return column;
            }
        }
        return null;
    }

    private String name;

    private int index;

    private SynonymsTableColumns(final String name, final int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
}
