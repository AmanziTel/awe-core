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
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsRowFilter extends ViewerFilter {
    private long start;
    private long end;

    /**
     * @param start
     * @param end
     */
    public StatisticsRowFilter(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        StatisticsCell[] cells = (StatisticsCell[])element;
        StatisticsRow row = cells[0].getParent();
        if (row.isSummaryNode()) {
            return true;
        }
        Long period = row.getTimestamp();
        return (period >= start) && (period <= end);
    }

}