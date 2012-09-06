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

package org.amanzi.awe.statistics.dto.impl;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.neo.impl.dto.DataElement;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.neo4j.graphdb.Node;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsRow extends DataElement implements IStatisticsRow {

    private long startDate;

    private long endDate;

    private List<IStatisticsCell> statisticsCells = new ArrayList<IStatisticsCell>();

    private boolean summury = false;

    private IStatisticsGroup statisticsGroup;

    public StatisticsRow(final Node node) {
        super(node);
    }

    /**
     * @return Returns the startDate.
     */
    @Override
    public long getStartDate() {
        return startDate;
    }

    /**
     * @param startDate The startDate to set.
     */
    public void setStartDate(final long startDate) {
        this.startDate = startDate;
    }

    /**
     * @return Returns the endDate.
     */
    @Override
    public long getEndDate() {
        return endDate;
    }

    /**
     * @param endDate The endDate to set.
     */
    public void setEndDate(final long endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        String startDateString = DateFormatUtils.ISO_DATETIME_FORMAT.format(startDate);
        String endDateString = DateFormatUtils.ISO_DATETIME_FORMAT.format(endDate);

        StringBuilder builder = new StringBuilder("StatisticsRow from <").append(startDateString).append("> to <")
                .append(endDateString).append(">.");

        return builder.toString();
    }

    /**
     * @return Returns the statisticsCells.
     */
    @Override
    public Iterable<IStatisticsCell> getStatisticsCells() {
        return statisticsCells;
    }

    /**
     * @param statisticsCells The statisticsCells to set.
     */
    public void setStatisticsCells(final Iterable<IStatisticsCell> statisticsCells) {
        // TODO KV: make sure about this way solution
        this.statisticsCells.clear();
        Iterables.addAll(this.statisticsCells, statisticsCells);
    }

    /**
     * @return Returns the statisticsGroup.
     */
    public IStatisticsGroup getStatisticsGroup() {
        return statisticsGroup;
    }

    /**
     * @param statisticsGroup The statisticsGroup to set.
     */
    public void setStatisticsGroup(final IStatisticsGroup statisticsGroup) {
        this.statisticsGroup = statisticsGroup;
    }

    /**
     * @return Returns the summury.
     */
    public boolean isSummury() {
        return summury;
    }

    /**
     * @param summury The summury to set.
     */
    public void setSummury(boolean summury) {
        this.summury = summury;
    }
}
