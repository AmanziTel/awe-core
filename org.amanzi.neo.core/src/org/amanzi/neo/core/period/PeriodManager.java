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
import java.util.Calendar;
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
public class PeriodManager {

    private static final DateFormat HOUR_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    private static final DateFormat DAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final DateFormat DAY_YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final DateFormat WEEK_DATE_FORMAT = new SimpleDateFormat("w");

    private static final DateFormat WEEK_YEAR_DATE_FORMAT = new SimpleDateFormat("w, yyyy");

    private static final DateFormat MONTH_YEAR_DATE_FORMAT = new SimpleDateFormat("MMMM yyyy");

    private static final DateFormat YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy");

    private static class InstanceHolder {
        private static final PeriodManager INSTANCE = new PeriodManager();

    }

    public static PeriodManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private PeriodManager() {

    }

    public String getPeriodName(Period period, Date startDate, Date endDate) {
        if (period != null) {
            switch (period) {
            case HOURLY:
                boolean isSameDay = DateUtils.isSameDay(startDate, endDate);
                String pattern = isSameDay ? Messages.singlePeriodPattern : Messages.multiPeriodPattern;
                DateFormat dayDateFormat = isSameDay ? HOUR_DATE_FORMAT : DAY_YEAR_DATE_FORMAT;

                return MessageFormat.format(pattern, dayDateFormat.format(startDate), dayDateFormat.format(endDate));
            case DAILY:
                return DAY_DATE_FORMAT.format(startDate);
            case WEEKLY:
                DateFormat weekDateFormat = isNeedYear(startDate, endDate) ? WEEK_DATE_FORMAT : WEEK_YEAR_DATE_FORMAT;

                return MessageFormat.format(Messages.weekPeriodPattern, weekDateFormat.format(startDate));
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

    private boolean isNeedYear(final Date startDate, final Date endDate) {
        Calendar startDateCalendar = DateUtils.toCalendar(startDate);
        Calendar endDateCalendar = DateUtils.toCalendar(endDate);
        Calendar currentDateCalendar = Calendar.getInstance();

        int startYear = startDateCalendar.get(Calendar.YEAR);
        int endYear = endDateCalendar.get(Calendar.YEAR);
        int currentYear = currentDateCalendar.get(Calendar.YEAR);

        return !((startYear == currentYear) || (startYear == endYear));
    }

    public long getNextStartDate(final Period period, final long endDate, final long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if (!period.equals(Period.HOURLY) && (nextStartDate > endDate)) {
            nextStartDate = endDate;
        }
        return nextStartDate;
    }
}
