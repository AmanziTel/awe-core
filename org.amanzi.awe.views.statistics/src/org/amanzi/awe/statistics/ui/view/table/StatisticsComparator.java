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

package org.amanzi.awe.statistics.ui.view.table;

import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * <p>
 * statistics table value comparator;
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsComparator extends ViewerComparator {
    private int direction = SWT.UP;
    private int column = 0;
    private boolean showAdditionalColumn;

    public void update(int column, int direction, boolean showAdditionalColumn) {
        this.column = column;
        this.direction = direction;
        this.showAdditionalColumn = showAdditionalColumn;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        int result = 0;
        StatisticsCell[] cells1 = (StatisticsCell[])e1;
        StatisticsCell[] cells2 = (StatisticsCell[])e2;
        StatisticsRow row1 = cells1[0].getParent();
        StatisticsRow row2 = cells2[0].getParent();
        StatisticsGroup group1 = row1.getParent();
        StatisticsGroup group2 = row2.getParent();
        switch (column) {
        case 0:
            if (showAdditionalColumn) {
                // TODO KV: implement this case;
            } else {
                if (row1 != null && row2 != null) {
                    result = group1.getName().compareTo(group2.getName());
                }
            }
            break;
        case 1:
            if (showAdditionalColumn) {
                result = group1.getName().compareTo(group2.getName());
            } else {
                result = comparePeriods(result, row1, row2);
            }
            break;
        case 2:
            if (showAdditionalColumn) {
                result = comparePeriods(result, row1, row2);
            } else {
                result = compareValues(cells1, cells2, column - (showAdditionalColumn ? 3 : 2));
            }
            break;
        default:
            result = compareValues(cells1, cells2, column - (showAdditionalColumn ? 3 : 2));
        }
        if (direction == SWT.DOWN) {
            result = -result;
        }
        return result;
    }

    /**
     * @param result
     * @param row1
     * @param row2
     * @return
     */
    private int comparePeriods(int result, StatisticsRow row1, StatisticsRow row2) {
        if (row1.isSummaryNode()) {
            if (!row2.isSummaryNode()) {
                result = 1;
            }
        } else {
            if (!row2.isSummaryNode()) {
                Long period1 = row1.getTimestamp();
                Long period2 = row2.getTimestamp();
                result = period1 == period2 ? 0 : period1 < period2 ? -1 : 1;
            } else {
                result = -1;
            }
        }
        return result;
    }

    /**
     * @param cells1 a first array of cells to be compared
     * @param cells2
     * @return
     */
    private int compareValues(StatisticsCell[] cells1, StatisticsCell[] cells2, int index) {
        int result;
        Number val1 = cells1[index].getValue();
        Number val2 = cells2[index].getValue();
        if (val1 == null) {
            result = val2 == null ? 0 : -1;
        } else {
            if (val2 == null) {
                result = 1;
            } else {
                double value1 = val1.doubleValue();
                double value2 = val2.doubleValue();
                result = value1 == value2 ? 0 : value1 < value2 ? -1 : 1;
            }

        }
        return result;
    }
}
