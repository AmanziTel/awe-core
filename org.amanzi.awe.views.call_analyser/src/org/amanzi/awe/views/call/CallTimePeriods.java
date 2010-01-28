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

package org.amanzi.awe.views.call;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <p>
 * enum of periods for call time
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public enum CallTimePeriods {
    // all time
    ALL("ALL") {
        @Override
        public Long getFirstTime(Long time) {
            return time;
        }

        @Override
        public Long addPeriod(Long time) {
            return Long.MAX_VALUE;
        }
    },
    // 1 day
    ONE_DAY("1 day") {
        @Override
        public Long addPeriod(Long time) {
            return addOnePeriod(time, Calendar.DAY_OF_MONTH);
        }

        @Override
        public Long getFirstTime(Long time) {
            GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.HOUR_OF_DAY, 0);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }
    },
    // 1 hour
    ONE_HOUR("1 hour") {
        @Override
        public Long addPeriod(Long time) {
            return addOnePeriod(time, Calendar.HOUR_OF_DAY);
        }

        @Override
        public Long getFirstTime(Long time) {
            GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }
    },
    // 1 minute
    ONE_MINUTES("1 minutes") {
        @Override
        public Long addPeriod(Long time) {
            return addOnePeriod(time, Calendar.MINUTE);
        }

        @Override
        public Long getFirstTime(Long time) {
            GregorianCalendar cl = new GregorianCalendar();
            cl.setTimeInMillis(time);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTimeInMillis();
        }
    };
    private final String id;

    CallTimePeriods(String id) {
        this.id = id;

    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Finds enum by id
     * 
     * @param periodId type id
     * @return enum or null
     */
    public static CallTimePeriods findById(String periodId) {
        if (periodId == null) {
            return null;
        }
        for (CallTimePeriods period : CallTimePeriods.values()) {
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
    public abstract Long getFirstTime(Long time);

    /**
     * gets time+period Length
     * 
     * @param time- timestamp
     */
    public abstract Long addPeriod(Long time);

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
}
