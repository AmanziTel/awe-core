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

package org.amanzi.awe.correlation.ui.view.table;

import org.amanzi.awe.correlation.ui.internal.CorrelationMessages;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum CorrelationTableColumns {
    NETWORK_COLUMN(CorrelationMessages.NETWORK_COLUMN_LABEL, 0), MEASUREMENT_COLUMN(CorrelationMessages.MEASUREMENT_COLUMN_LABEL, 1), PROXIES_COUNT_COLUMN(
            CorrelationMessages.PROXIES_COLUMN_LABEL, 2), TOTAL_SECTORS_COUNT(CorrelationMessages.SECTOR_COUNT_COLUMN_LABEL, 3), CORRELATED_M_COUNT(
            CorrelationMessages.CORRELATED_M_COUNT_COLUMN_LABEL, 4), TOTAL_M_COUNT(CorrelationMessages.TOTAL_M_COUNT_COLUMN_LABEL,
            5), START_TIME_COLUMN(CorrelationMessages.START_TIME_COLUMN_LABEL, 6), END_TIME_COLUMN(
            CorrelationMessages.END_TIME_COLUMN_LABEL, 7), DELETE(CorrelationMessages.DELETE_COLUMN_LABEL, 8);

    public static CorrelationTableColumns findByIndex(final int index) {
        for (CorrelationTableColumns column : CorrelationTableColumns.values()) {
            if (column.getIndex() == index) {
                return column;
            }
        }
        return null;
    }

    private String name;

    private int index;

    private CorrelationTableColumns(final String name, final int index) {
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
