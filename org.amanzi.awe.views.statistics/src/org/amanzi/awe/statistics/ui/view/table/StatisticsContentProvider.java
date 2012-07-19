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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.amanzi.awe.statistics.entities.impl.AggregatedStatistics;
import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * content provider for StatisticsTable
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsContentProvider implements IStructuredContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof AggregatedStatistics) {
            List<StatisticsCell[]> elements = new ArrayList<StatisticsCell[]>();
            AggregatedStatistics statistics = (AggregatedStatistics)inputElement;
            for (StatisticsGroup group : statistics.getAllChild()) {
                Iterable<StatisticsRow> rows = group.getAllChild();
                for (StatisticsRow row : rows) {
                    Collection<StatisticsCell> cells = row.getAllChild();
                    StatisticsCell[] cellsAsArray = cells.toArray(new StatisticsCell[cells.size()]);
                    if (cellsAsArray.length != 0) {
                        elements.add(cellsAsArray);
                    }
                }
            }
            return elements.toArray();
        }
        return null;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

}
