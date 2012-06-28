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
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.Assert;

import org.amanzi.awe.statistics.AbstractStatisticsTest;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * Test period calculation functionality
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PeriodTests extends AbstractStatisticsTest {

    private static final Map<Integer, Integer> LEAP_YEAR_VALUES = new LinkedHashMap<Integer, Integer>();
    static {
        LEAP_YEAR_VALUES.put(Calendar.YEAR, 2012);
        LEAP_YEAR_VALUES.put(Calendar.MONTH, Calendar.FEBRUARY);
        LEAP_YEAR_VALUES.put(Calendar.DATE, 29);
    }

    @BeforeClass
    public static void init() {
        setUpClass();
    }

    @Test
    public void testGetHighestPeriodSimpleHourly() {
        PeriodRange range = generatePeriod(Period.HOURLY);
        Period period = Period.getHighestPeriod(range.getMin(), range.getMax());
        Assert.assertEquals("Unexpected period ", Period.HOURLY, period);
    }

    @Test
    public void testGetHighestPeriodSimpleDaily() {
        PeriodRange range = generatePeriod(Period.DAILY);
        Period period = Period.getHighestPeriod(range.getMin(), range.getMax());
        Assert.assertEquals("Unexpected period ", Period.DAILY, period);
    }

    @Test
    public void testGetHighestPeriodMonthly() {
        PeriodRange range = generatePeriod(Period.MONTHLY);
        Period period = Period.getHighestPeriod(range.getMin(), range.getMax());
        Assert.assertEquals("Unexpected period ", Period.MONTHLY, period);
    }

    @Test
    public void testGetHighestPeriodWeeklyInTheSameWeek() {
        PeriodRange range = generatePeriod(Period.WEEKLY);
        Period period = Period.getHighestPeriod(range.getMin(), range.getMax());
        Assert.assertEquals("Unexpected period ", Period.WEEKLY, period);
    }

    @Test
    public void testGetHighestPeriodYearly() {
        PeriodRange range = generatePeriod(Period.YEARLY);
        Period period = Period.getHighestPeriod(range.getMin(), range.getMax());
        Assert.assertEquals("Unexpected period ", Period.YEARLY, period);
    }

    @Test
    public void testGetHighestPeriodYearlyLeapYear() {
        Calendar startTime = getLeapYear();
        Calendar endTime = getCalendar();
        endTime.add(Calendar.DATE, NumberUtils.INTEGER_ONE);
        Period period = Period.getHighestPeriod(startTime.getTimeInMillis(), endTime.getTimeInMillis());
        Assert.assertEquals(Period.YEARLY, period);
    }

    private Calendar getLeapYear() {
        Calendar calendar = getCalendar();
        for (int key : LEAP_YEAR_VALUES.keySet()) {
            calendar.set(key, LEAP_YEAR_VALUES.get(key));
        }
        return calendar;
    }

    private Calendar getCalendar() {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        return time;
    }
}
