package org.amanzi.awe.statistics.ui.view.table;

import java.util.Collection;

import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filter by aggregation
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsAggregationFilter extends ViewerFilter {
    private Collection<String> values;

    /**
     * @param start
     * @param end
     */
    public StatisticsAggregationFilter(Collection<String> selection) {
        this.values = selection;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        StatisticsCell[] cells = (StatisticsCell[])element;
        StatisticsRow row = cells[0].getParent();
        StatisticsGroup group = row.getParent();
        return values.contains(group.getName());
    }

}
