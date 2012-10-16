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

package org.amanzi.awe.statistics.ui.filter.impl;

import java.util.Collection;

import org.amanzi.awe.statistics.filter.IStatisticsFilter;
import org.amanzi.neo.core.period.Period;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * event data container
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsFilter implements IStatisticsFilter {

    private final Period period;

    private final Long startTime;

    private final Long endTime;

    private Collection<String> groupNames;

    private String cellName;

    public StatisticsFilter(Period period, Long startTime, Long endTime) {
        this.period = period;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public StatisticsFilter(Period period, Long startTime, Long endTime, Collection<String> groupNames, String cellName) {
        this(period, startTime, endTime);

        this.groupNames = groupNames;
        this.cellName = cellName;
    }

    @Override
    public Period getPeriod() {
        return period;
    }

    @Override
    public Long getStartTime() {
        return startTime;
    }

    @Override
    public Long getEndTime() {
        return endTime;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

    @Override
    public String getCellName() {
        return cellName;
    }

    @Override
    public Collection<String> getGroupNames() {
        return groupNames;
    }
}
