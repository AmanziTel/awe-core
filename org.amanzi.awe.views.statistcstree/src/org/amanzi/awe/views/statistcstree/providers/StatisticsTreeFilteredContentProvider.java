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

package org.amanzi.awe.views.statistcstree.providers;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.views.statistcstree.view.filter.container.IStatisticsTreeFilterContainer;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsTreeFilteredContentProvider extends StatisticsTreeContentProvider {

    private IStatisticsTreeFilterContainer filter;
    private IStatisticsModel model;

    public StatisticsTreeFilteredContentProvider(DimensionType type, IStatisticsModel model, IStatisticsTreeFilterContainer filter) {
        super(type);
        this.model = model;
        this.filter = filter;
    }

    @Override
    protected Iterable< ? extends IDataElement> getStatisticsGroups(IStatisticsModel model, DimensionType type, String levelName)
            throws ModelException {
        List<IStatisticsGroup> groups = new ArrayList<IStatisticsGroup>();
        for (IStatisticsGroup group : model.getAllStatisticsGroups(DimensionType.TIME, levelName)) {
            if (filter.getGroupNames().contains(group.getPropertyValue())) {
                groups.add(group);
            }
        }
        return groups;
    }

    @Override
    protected Iterable<Object> getStatisticsLevels(IStatisticsModel model) throws ModelException {
        List<Object> levels = new ArrayList<Object>();
        for (IDataElement level : model.findAllStatisticsLevels(getType())) {
            if (level.getName().equals(filter.getPeriod().getId())) {
                levels.add(level);
            }
        }
        return levels;
    }

    @Override
    protected List<IStatisticsModel> getRootElements() throws ModelException {
        List<IStatisticsModel> statisticsModels = new ArrayList<IStatisticsModel>();
        statisticsModels.add(model);
        return statisticsModels;
    }

    @Override
    protected Iterable<IStatisticsRow> getRows(IStatisticsModel model, String period) throws ModelException {
        return model.getStatisticsRowsInTimeRange(period, filter.getStartTime(), filter.getEndTime());
    }

    @Override
    protected Iterable<Object> getRowsChildren(IStatisticsRow row) throws ModelException {
        List<Object> cells = new ArrayList<Object>();
        for (IStatisticsCell cell : row.getStatisticsCells()) {
            if (cell.getName().equals(filter.getCellName())) {
                cells.add(cell);
            }
        }
        return cells;
    }

    public IStatisticsTreeFilterContainer getFilter() {
        return filter;
    }
}
