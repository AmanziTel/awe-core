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

package org.amanzi.awe.statistics.enumeration;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
    HOURLY(Messages.CTP_HOURLY, null, -1) {
        @Override
        public Long addPeriod(Long time) {
            return addOnePeriod(time, Calendar.HOUR_OF_DAY);
        }

        @Override
        public Long getStartTime(Long time) {
            GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }

    },
    // 1 day
    DAILY(Messages.CTP_DAILY, HOURLY, Calendar.HOUR_OF_DAY) {
        @Override
        public Long addPeriod(Long time) {
            return addOnePeriod(time, Calendar.DAY_OF_MONTH);
        }

        @Override
        public Long getStartTime(Long time) {
            GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.HOUR_OF_DAY, 0);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }
    },
    // 1 week
    WEEKLY(Messages.CTP_WEEKLY, DAILY, Calendar.DAY_OF_YEAR) {
        @Override
        public Long addPeriod(Long time) {
            return addOnePeriod(time, Calendar.WEEK_OF_YEAR);
        }

        @Override
        public Long getStartTime(Long time) {
            GregorianCalendar cl = new GregorianCalendar();
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
    MONTHLY(Messages.CTP_MONTHLY, WEEKLY, Calendar.WEEK_OF_YEAR) {
        @Override
        public Long addPeriod(Long time) {
            return addOnePeriod(time, Calendar.MONTH);
        }

        @Override
        public Long getStartTime(Long time) {
            GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            int dayInMonth = cl.get(Calendar.DAY_OF_MONTH);
            cl.add(Calendar.DAY_OF_YEAR, 1 - dayInMonth);
            cl.set(Calendar.HOUR_OF_DAY, 0);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }

    },
    YEARLY(Messages.CTP_YEARLY, MONTHLY, Calendar.MONTH) {
        @Override
        public Long addPeriod(Long time) {
            return addOnePeriod(time, Calendar.YEAR);
        }

        @Override
        public Long getStartTime(Long time) {
            GregorianCalendar cl = new GregorianCalendar();
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
    ALL(Messages.CTP_TOTAL, YEARLY, Calendar.YEAR) {

        @Override
        public Long addPeriod(Long time) {
            return Long.MAX_VALUE;
        }

        @Override
        public Long getStartTime(Long time) {
            return time;
        }

        @Override
        public Long getEndTime(Long time) {
            return time;
        }

    };

    private final String id;
    private static final Period[] PERIODS = new Period[] {Period.ALL, Period.YEARLY, Period.MONTHLY, WEEKLY, Period.DAILY,
            Period.HOURLY};
    private Period underlyingPeriod;

    private int underlyingPeriodCalendarField;

    /**
     * Constructor.
     * 
     * @param id String
     * @param underlyingPeriod Period
     */
    Period(String id, Period underlyingPeriod, int underlyingPeriodCalendarField) {
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
    public static Period findById(String periodId) {
        if (periodId == null) {
            return null;
        }
        for (Period period : Period.values()) {
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
    public Long getEndTime(Long time) {
        return addPeriod(getStartTime(time));
    }

    public static Period getHighestPeriod(long minTime, long maxTime) {
        Calendar minDate = Calendar.getInstance();
        minDate.setTimeInMillis(minTime);

        Calendar maxDate = Calendar.getInstance();
        maxDate.setTimeInMillis(maxTime);

        // starting with highest period
        Period highestPeriod;
        Period result = null;

        for (int i = 0; i < PERIODS.length && result == null; i++) {
            highestPeriod = PERIODS[i];

            if (highestPeriod == Period.HOURLY) {
                result = highestPeriod;
            } else {
                int minDatePeriod = minDate.get(highestPeriod.getUnderlyingPeriodCalendarField());
                int maxDatePeriod = maxDate.get(highestPeriod.getUnderlyingPeriodCalendarField());

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
    private static Long addOnePeriod(Long time, int period) {
        GregorianCalendar cl = new GregorianCalendar();
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

}
