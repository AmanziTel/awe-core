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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.amanzi.neo.core.internal.Messages;
import org.apache.commons.lang3.ArrayUtils;

/**
 * <p>
 * Time periods
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum Period {
    // 1 hour
    HOURLY(Messages.ctrHourly, null, -1) {
        @Override
        public Long addPeriod(final Long time) {
            return addOnePeriod(time, Calendar.HOUR_OF_DAY);
        }

        @Override
        public Long getStartTime(final Long time) {
            final GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }

    },
    // 1 day
    DAILY(Messages.ctrDaily, HOURLY, Calendar.HOUR_OF_DAY) {
        @Override
        public Long addPeriod(final Long time) {
            return addOnePeriod(time, Calendar.DAY_OF_MONTH);
        }

        @Override
        public Long getStartTime(final Long time) {
            final GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.HOUR_OF_DAY, 0);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }
    },
    // 1 week
    WEEKLY(Messages.ctrWeekly, DAILY, Calendar.DAY_OF_YEAR) {
        @Override
        public Long addPeriod(final Long time) {
            return addOnePeriod(time, Calendar.WEEK_OF_YEAR);
        }

        @Override
        public Long getStartTime(final Long time) {
            final GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.DAY_OF_WEEK, cl.getFirstDayOfWeek());
            cl.set(Calendar.HOUR_OF_DAY, 0);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }

    },
    // 1 month
    MONTHLY(Messages.ctrMonthly, WEEKLY, Calendar.WEEK_OF_YEAR) {
        @Override
        public Long addPeriod(final Long time) {
            return addOnePeriod(time, Calendar.MONTH);
        }

        @Override
        public Long getStartTime(final Long time) {
            final GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            final int dayInMonth = cl.get(Calendar.DAY_OF_MONTH);
            cl.add(Calendar.DAY_OF_YEAR, 1 - dayInMonth);
            cl.set(Calendar.HOUR_OF_DAY, 0);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }

    },
    YEARLY(Messages.ctrYearly, MONTHLY, Calendar.MONTH) {
        @Override
        public Long addPeriod(final Long time) {
            return addOnePeriod(time, Calendar.YEAR);
        }

        @Override
        public Long getStartTime(final Long time) {
            final GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.MONTH, Calendar.JANUARY);
            cl.set(Calendar.DAY_OF_MONTH, 1);
            cl.set(Calendar.HOUR_OF_DAY, 0);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }

    },
    ALL(Messages.ctrTotal, YEARLY, Calendar.YEAR) {

        @Override
        public Long addPeriod(final Long time) {
            return Long.MAX_VALUE;
        }

        @Override
        public Long getStartTime(final Long time) {
            return time;
        }

        @Override
        public Long getEndTime(final Long time) {
            return time;
        }

    };

    private final String id;
    private static final Period[] SORTED_PERIODS;

    static {
        SORTED_PERIODS = Period.values();
        ArrayUtils.reverse(SORTED_PERIODS);
    }

    private Period underlyingPeriod;

    private int underlyingPeriodCalendarField;

    /**
     * Constructor.
     * 
     * @param id String
     * @param underlyingPeriod Period
     */
    Period(final String id, final Period underlyingPeriod, final int underlyingPeriodCalendarField) {
        this.id = id;
        this.underlyingPeriod = underlyingPeriod;
        this.underlyingPeriodCalendarField = underlyingPeriodCalendarField;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Returns the underlyingPeriod.
     */
    public Period getUnderlyingPeriod() {
        return underlyingPeriod;
    }

    /**
     * Finds enum by id
     * 
     * @param periodId type id
     * @return enum or null
     */
    public static Period findById(final String periodId) {
        if (periodId == null) {
            return null;
        }
        for (final Period period : Period.values()) {
            if (period.getId().equals(periodId)) {
                return period;
            }
        }
        return null;
    }

    /**
     * gets first time of period
     * 
     * @param time - timestamp
     */
    public abstract Long getStartTime(Long time);

    /**
     * gets time+period Length
     * 
     * @param time- timestamp
     */
    public abstract Long addPeriod(Long time);

    /**
     * get last ms in current period
     * 
     * @param time - timestamp
     * @return last ms
     */
    public Long getEndTime(final Long time) {
        return addPeriod(getStartTime(time));
    }

    public static Period getHighestPeriod(final long minTime, final long maxTime) {
        final Calendar minDate = Calendar.getInstance();
        minDate.setTimeInMillis(minTime);

        final Calendar maxDate = Calendar.getInstance();
        maxDate.setTimeInMillis(maxTime);

        // starting with highest period
        Period highestPeriod;
        Period result = null;
        for (int i = 0; (i < SORTED_PERIODS.length) && (result == null); i++) {
            highestPeriod = SORTED_PERIODS[i];

            if (highestPeriod == Period.HOURLY) {
                result = highestPeriod;
            } else {
                final int minDatePeriod = minDate.get(highestPeriod.getUnderlyingPeriodCalendarField());
                final int maxDatePeriod = maxDate.get(highestPeriod.getUnderlyingPeriodCalendarField());

                if (minDatePeriod < maxDatePeriod) {
                    result = highestPeriod;
                }
            }
        }

        return result;
    }

    /**
     * add one period
     * 
     * @param time - timestamp
     * @param period - period @see Calendar
     * @return timestamp+ 1 period
     */
    private static Long addOnePeriod(final Long time, final int period) {
        final GregorianCalendar cl = new GregorianCalendar();
        cl.setTimeInMillis(time);
        cl.add(period, 1);
        return cl.getTimeInMillis();
    }

    /**
     * @return Returns the underlyingPeriodCalendarField.
     */
    public int getUnderlyingPeriodCalendarField() {
        return underlyingPeriodCalendarField;
    }

    /**
     * get available periods for current time range
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<Period> getAvailablePeriods(final long startTime, final long endTime) {
        // TODO: LN: 09.08.2012, remove magic numbers
        // TODO: LN: 09.08.2012, algorithm of detecting available periods is in correct since it
        // depends on numbers (at least in month we can have more or less than 30 days)

        final List<Period> periods = new ArrayList<Period>();
        long time = (endTime - startTime) / (1000 * 60);

        if ((time = time / 60) >= 0) {
            periods.add(HOURLY);
            if ((time = time / 24) >= 1) {
                periods.add(DAILY);
                if ((time / 7) >= 1) {
                    periods.add(WEEKLY);
                }
                if ((time = time / 30) >= 1) {
                    periods.add(MONTHLY);
                }
            }
        }
        return periods;
    }

    /**
     * @return Returns the sortedPeriods.
     */
    public static Period[] getSortedPeriods() {
        return SORTED_PERIODS;
    }

}
