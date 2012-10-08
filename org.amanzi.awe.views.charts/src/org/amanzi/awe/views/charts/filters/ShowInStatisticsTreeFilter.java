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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((cellName == null) ? 0 : cellName.hashCode());
        result = (prime * result) + (int)(endDate ^ (endDate >>> 32));
        result = (prime * result) + ((groups == null) ? 0 : groups.hashCode());
        result = (prime * result) + ((period == null) ? 0 : period.hashCode());
        result = (prime * result) + (int)(startDate ^ (startDate >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ShowInStatisticsTreeFilter other = (ShowInStatisticsTreeFilter)obj;
        if (cellName == null) {
            if (other.cellName != null) {
                return false;
            }
        } else if (!cellName.equals(other.cellName)) {
            return false;
        }
        if (endDate != other.endDate) {
            return false;
        }
        if (groups == null) {
            if (other.groups != null) {
                return false;
            }
        } else if (!groups.equals(other.groups)) {
            return false;
        }
        if (period != other.period) {
            return false;
        }
        if (startDate != other.startDate) {
            return false;
        }
        return true;
    }

}
