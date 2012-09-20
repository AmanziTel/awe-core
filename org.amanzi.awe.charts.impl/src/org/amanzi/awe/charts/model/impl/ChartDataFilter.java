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

package org.amanzi.awe.charts.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amanzi.awe.charts.model.IChartDataFilter;
import org.amanzi.awe.statistics.dto.IStatisticsRow;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChartDataFilter implements IChartDataFilter {

    private List<String> groups = new ArrayList<String>();
    private Long minRowPeriod = Long.MIN_VALUE;
    private Long maxRowPeriod = Long.MAX_VALUE;

    /**
     * create filter to check appropriation between periods and groups
     * 
     * @param minRowPeriod
     * @param maxRowPeriod
     * @param groups
     */
    public ChartDataFilter(long minRowPeriod, long maxRowPeriod, Iterable<String> groups) {
        this.minRowPeriod = minRowPeriod;
        this.maxRowPeriod = maxRowPeriod;
        Iterables.addAll(this.groups, groups);
    }

    /**
     * create filter to check appropriation between groups only
     * 
     * @param groups
     */
    public ChartDataFilter(Iterable<String> groups) {
        this(Long.MIN_VALUE, Long.MAX_VALUE, groups);
    }

    @SuppressWarnings("unchecked")
    public ChartDataFilter(long minRowPeriod, long maxRowPeriod) {
        this(minRowPeriod, maxRowPeriod, Collections.EMPTY_LIST);
    }

    @Override
    public Iterable<String> getChartGroups() {
        return groups;
    }

    @Override
    public Long getMinRowPeriod() {
        return minRowPeriod;
    }

    @Override
    public Long getMaxRowPeriod() {
        return maxRowPeriod;
    }

    @Override
    public boolean check(IStatisticsRow row, boolean isSummuryNeed) {
        if (row.isSummury()) {
            return false;
        } else {
            return row.getStartDate() >= minRowPeriod && row.getStartDate() <= maxRowPeriod
                    && groups.contains(row.getStatisticsGroup().getPropertyValue());
        }
    }
}
