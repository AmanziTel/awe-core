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

package org.amanzi.awe.views.statistics.table;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * Comparer for sorting table columns and values
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsViewerComparator extends ViewerComparator {
    private int direction = SWT.UP;
    private int column = 0;

    public void update(int column, int direction) {
        this.column = column;
        this.direction = direction;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        int result = 0;
        IStatisticsRow row1 = (IStatisticsRow)e1;
        IStatisticsRow row2 = (IStatisticsRow)e2;
        IStatisticsGroup group1 = row1.getStatisticsGroup();
        IStatisticsGroup group2 = row2.getStatisticsGroup();
        switch (column) {
        case 0:
            if (row1 != null && row2 != null) {
                result = group1.getPropertyValue().compareTo(group2.getPropertyValue());
            }
            break;
        case 1:
            result = comparePeriods(result, row1, row2);
            break;
        default:
            result = compareValues(row1.getStatisticsCells(), row2.getStatisticsCells(), column - 2);
            break;
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
    private int comparePeriods(int result, IStatisticsRow row1, IStatisticsRow row2) {
        Long period1 = row1.getStartDate();
        Long period2 = row2.getStartDate();
        result = period1 == period2 ? 0 : period1 < period2 ? -1 : 1;
        return result;
    }

    /**
     * @param cells1 a first array of cells to be compared
     * @param cells2
     * @return
     */
    private int compareValues(Iterable<IStatisticsCell> cells1, Iterable<IStatisticsCell> cells2, int index) {
        int result;
        Number val1 = getCellValueByIndex(cells1, index);
        Number val2 = getCellValueByIndex(cells2, index);
        if (val1 == null) {
            result = val2 == null ? 0 : -1;
        } else {
            if (val2 == null) {
                result = 1;
            } else {
                Double value1 = val1.doubleValue();
                Double value2 = val2.doubleValue();
                result = value1.compareTo(value2);
            }

        }
        return result;
    }

    /**
     * @param cells1
     * @param index
     * @return
     */
    private Number getCellValueByIndex(Iterable<IStatisticsCell> cells, int index) {
        int counter = 0;
        for (IStatisticsCell cell : cells) {
            if (counter == index) {
                return cell.getValue();
            }
            counter++;
        }
        return null;
    }
}
