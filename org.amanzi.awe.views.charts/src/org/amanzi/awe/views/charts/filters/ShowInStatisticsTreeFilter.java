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

package org.amanzi.awe.views.charts.filters;

import java.util.Collection;

import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.views.statistcstree.view.filter.container.IStatisticsTreeFilterContainer;
import org.amanzi.neo.core.period.Period;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ShowInStatisticsTreeFilter implements IStatisticsTreeFilterContainer {

    private Collection<String> groups;
    private IStatisticsRow row;
    private String cellName;
    private Period period;

    /**
     * @param groups
     * @param row
     * @param cellName
     * @param model
     */
    public ShowInStatisticsTreeFilter(Collection<String> groups, IStatisticsRow row, String cellName, Period period) {
        super();
        this.groups = groups;
        this.row = row;
        this.cellName = cellName;
        this.period = period;
    }

    @Override
    public long getRowStartTime() {
        return row.getStartDate();
    }

    @Override
    public Collection<String> getGroupNames() {
        return groups;
    }

    @Override
    public String getCellName() {
        return cellName;
    }


    @Override
    public Period getPeriod() {
        return period;
    }

}
