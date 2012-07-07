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

package org.amanzi.awe.statistics;

import java.util.Calendar;

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.testing.AbstractTest;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>
 * common functionality for statistics tests
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractStatisticsTest extends AbstractTest {

    /**
     * generate range period
     * 
     * @param monthly
     */
    protected PeriodRange generatePeriod(Period period) {
        PeriodRange range = null;
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        switch (period) {
        case HOURLY:
            min.set(Calendar.MINUTE, NumberUtils.INTEGER_ZERO);
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.MINUTE, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case DAILY:
            min.set(Calendar.HOUR_OF_DAY, NumberUtils.INTEGER_ONE);
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.HOUR_OF_DAY, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case WEEKLY:
            min.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            max.setTimeInMillis(min.getTimeInMillis());
            max.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case MONTHLY:
            min.set(Calendar.DATE, NumberUtils.INTEGER_ONE);
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.WEEK_OF_YEAR, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case YEARLY:
            min.set(Calendar.MONTH, Calendar.JANUARY);
            max.setTimeInMillis(min.getTimeInMillis());
            max.set(Calendar.MONTH, Calendar.FEBRUARY);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case ALL:
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.YEAR, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        default:
            break;
        }
        return range;
    }

    /**
     * just storage for test of min max timestamp
     * <p>
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    protected static class PeriodRange {
        private long min;
        private long max;

        /**
         * @return Returns the min.
         */
        public long getMin() {
            return min;
        }

        /**
         * @return Returns the max.
         */
        public long getMax() {
            return max;
        }

        private PeriodRange(long min, long max) {
            this.min = min;
            this.max = max;
        }

    }
}
