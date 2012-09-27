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
    private String cellName;
    private Period period;
    private long startDate;
    private long endDate;

    /**
     * @param groups
     * @param row
     * @param cellName
     * @param model
     */
    public ShowInStatisticsTreeFilter(Collection<String> groups, long startDate, long endDate, String cellName, Period period) {
        super();
        this.groups = groups;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cellName = cellName;
        this.period = period;
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

    @Override
    public long getStartTime() {
        return startDate;
    }

    @Override
    public long getEndTime() {
        return endDate;
    }

}
