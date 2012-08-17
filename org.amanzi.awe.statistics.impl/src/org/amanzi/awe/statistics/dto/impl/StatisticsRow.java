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

import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.neo.impl.dto.DataElement;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.neo4j.graphdb.Node;

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

}
