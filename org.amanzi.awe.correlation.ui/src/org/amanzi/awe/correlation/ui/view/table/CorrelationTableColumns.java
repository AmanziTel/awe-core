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

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum CorrelationTableColumns {
    NETWORK_COLUMN("Network", 0), MEASUREMENT_COLUMN("Measurement", 1), CORRELATION_PROPERTY("Correlation property", 2), CORRELATED_PROPERTY(
            "Correlated property", 3), PROXIES_COUNT_COLUMN("Proxies", 4), START_TIME_COLUMN("Start time", 5), END_TIME_COLUMN(
            "End time", 6);

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
