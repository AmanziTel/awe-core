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

package org.amanzi.neo.core.period;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.amanzi.neo.core.internal.Messages;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public final class PeriodManager {

    private static final DateFormat HOUR_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    private static final DateFormat DAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final DateFormat DAY_YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final DateFormat WEEK_YEAR_DATE_FORMAT = new SimpleDateFormat("w, yyyy");

    private static final DateFormat MONTH_YEAR_DATE_FORMAT = new SimpleDateFormat("MMMM yyyy");

    private static final DateFormat YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy");

    private PeriodManager() {

    }

    public static String getPeriodName(final Period period, final long startTime, final long endTime) {
        return getPeriodName(period, new Date(startTime), new Date(endTime));
    }

    public static String getPeriodName(final Period period, final Date startDate, final Date endDate) {
        if (period != null) {
            switch (period) {
            case HOURLY:
                final boolean isSameDay = DateUtils.isSameDay(startDate, endDate);
                if (isSameDay) {
                    return MessageFormat.format(Messages.hourPeriodPattern, DAY_DATE_FORMAT.format(startDate),
                            HOUR_DATE_FORMAT.format(startDate), HOUR_DATE_FORMAT.format(endDate));
                }
                return MessageFormat.format(Messages.multiPeriodPattern, DAY_YEAR_DATE_FORMAT.format(startDate),
                        DAY_YEAR_DATE_FORMAT.format(endDate));
            case DAILY:
                return DAY_DATE_FORMAT.format(startDate);
            case WEEKLY:
                return MessageFormat.format(Messages.weekPeriodPattern, WEEK_YEAR_DATE_FORMAT.format(startDate));
            case MONTHLY:
                return MessageFormat.format(Messages.monthPeriodPattern, MONTH_YEAR_DATE_FORMAT.format(startDate));
            case YEARLY:
                return MessageFormat.format(Messages.yearPeriodPattern, YEAR_DATE_FORMAT.format(startDate));
            default:
                break;
            }
        }

        return StringUtils.EMPTY;
    }

    @Deprecated
    public static long getNextStartDate(final Period period, final long endDate, final long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if (!period.equals(Period.HOURLY) && (nextStartDate > endDate)) {
            nextStartDate = endDate;
        }
        return nextStartDate;
    }
}
