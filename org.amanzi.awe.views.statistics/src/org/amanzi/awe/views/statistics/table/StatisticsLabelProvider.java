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

package org.amanzi.awe.views.statistics.table;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.period.Period;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsLabelProvider implements ITableLabelProvider {

    private static final DateFormat HOUR_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    private static final DateFormat DAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final DateFormat YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final String SINGLE_PERIOD_PATTERN = "{0} - {1}";

    private static final String MULTI_PERIOD_PATTERN = "{0} to {1}";

    private IStatisticsRow previousRow = null;

    private final List<IStatisticsCell> cellList = new ArrayList<IStatisticsCell>();

    private Period period;

    @Override
    public void addListener(final ILabelProviderListener listener) {

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {

    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        if (element instanceof IStatisticsRow) {
            IStatisticsRow statisticsRow = (IStatisticsRow)element;

            if (!statisticsRow.equals(previousRow)) {
                initializeCellList(statisticsRow);

                previousRow = statisticsRow;
            }

            switch (columnIndex) {
            case 0:
                return statisticsRow.getStatisticsGroup().getPropertyValue();
            case 1:
                return getStatisticsRowName(statisticsRow);
            default:
                Number value = cellList.get(columnIndex - 2).getValue();

                return value == null ? "N/A" : value.toString();
            }
        }

        return StringUtils.EMPTY;
    }

    private void initializeCellList(final IStatisticsRow statisticsRow) {
        cellList.clear();
        Iterables.addAll(cellList, statisticsRow.getStatisticsCells());
    }

    private String getStatisticsRowName(final IStatisticsRow row) {
        Date startDate = new Date(row.getStartDate());
        Date endDate = new Date(row.getEndDate());

        if (period != null) {
            switch (period) {
            case HOURLY:
                boolean isSameDay = DateUtils.isSameDay(startDate, endDate);
                String pattern = isSameDay ? SINGLE_PERIOD_PATTERN : MULTI_PERIOD_PATTERN;
                DateFormat dateFormat = isSameDay ? HOUR_DATE_FORMAT : YEAR_DATE_FORMAT;

                return MessageFormat.format(pattern, dateFormat.format(startDate), dateFormat.format(endDate));
            case DAILY:
                return DAY_DATE_FORMAT.format(startDate);
            }
        }

        return StringUtils.EMPTY;
    }

    public void setPeriod(final Period period) {
        this.period = period;
    }

}
