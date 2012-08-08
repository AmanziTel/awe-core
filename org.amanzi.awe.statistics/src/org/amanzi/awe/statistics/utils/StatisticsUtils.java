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

package org.amanzi.awe.statistics.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.statistics.enumeration.Period;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * manipulation with statistics data
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsUtils {
    private static final String NO_TIME = "No time";
    private static StatisticsUtils utils;

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
    private static final String WEEK = "Week ";
    private static final String MONTH = "Month";
    private static final String TO = " to ";
    private static final String SPACE_SEPARATOR = " ";
    private static final String DEFIS_SEPARATOR = "-";
    private static final String COMMA_SEPARATOR = ", ";
    private static final String[] MONTHES = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
            "November", "December"};

    /**
     * default date format
     */
    private String defFormat = "HH:mm";

    /**
     * get statistics utils instance
     */
    public static StatisticsUtils getInstance() {
        if (utils == null) {
            utils = new StatisticsUtils();
        }
        return utils;
    }

    /*
     * can't be instantiated directly
     */
    private StatisticsUtils() {
    }

    /**
     * get format date string.
     * 
     * @param startTime - begin timestamp
     * @param endTime end timestamp
     * @param dayFormat the day format
     * @param periodId the period id
     * @return the format date string
     */
    public String getFormatDateStringForSrow(Long startTime, Long endTime, Period period) {
        if (startTime == null || endTime == null) {
            return NO_TIME;
        }
        switch (period) {
        case HOURLY:
            return getNameForHourlySRow(startTime, endTime);
        case DAILY:
            return getNameForDailySRow(startTime);
        case WEEKLY:
            return getNameForWeeklySRow(startTime, endTime);
        default:
            break;
        }
        return getNameForMonthlySRow(startTime, endTime);
    }

    /**
     * Gets the name for hourly s row.
     * 
     * @param startTime the start time
     * @param endTime the end time
     * @param dayFormat the day format
     * @return the name for hourly s row
     */
    private String getNameForHourlySRow(Long startTime, Long endTime) {
        Calendar endTimeCal = Calendar.getInstance();
        endTimeCal.setTimeInMillis(endTime);

        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.setTimeInMillis(startTime);

        String pattern = YEAR_MONTH_DAY_PATTERN + SPACE_SEPARATOR + defFormat;
        SimpleDateFormat sf = new SimpleDateFormat(pattern);

        StringBuilder sb = new StringBuilder();
        if (startTimeCal.get(Calendar.DAY_OF_WEEK) == endTimeCal.get(Calendar.DAY_OF_WEEK)) {
            SimpleDateFormat sf2 = new SimpleDateFormat(defFormat);
            sb.append(sf.format(startTimeCal.getTime()));
            sb.append(DEFIS_SEPARATOR).append(sf2.format(endTimeCal.getTime()));
        } else {
            SimpleDateFormat sfMulDay2 = new SimpleDateFormat(pattern);
            sb.append(sf.format(startTimeCal.getTime()));
            sb.append(TO).append(sfMulDay2.format(endTimeCal.getTime()));
        }
        return sb.toString();
    }

    /**
     * Gets the name for daily s row.
     * 
     * @param startTime the start time
     * @return the name for daily s row
     */
    private String getNameForDailySRow(Long startTime) {
        String pattern = YEAR_MONTH_DAY_PATTERN;
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(new Date(startTime));
    }

    /**
     * Gets the name for weekly s row.
     * 
     * @param startTime the start time
     * @param endTime the end time
     * @return the name for weekly s row
     */
    private String getNameForWeeklySRow(Long startTime, Long endTime) {
        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.setTimeInMillis(startTime);
        String result = WEEK + startTimeCal.get(Calendar.WEEK_OF_YEAR);
        if (isNeedAddYear(startTime, endTime)) {
            result = result + COMMA_SEPARATOR + startTimeCal.get(Calendar.YEAR);
        }
        return result;
    }

    /**
     * Gets the name for monthly s row.
     * 
     * @param startTime the start time
     * @param endTime the end time
     * @return the name for monthly s row
     */
    private String getNameForMonthlySRow(Long startTime, Long endTime) {
        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.setTimeInMillis(startTime);
        int month = startTimeCal.get(Calendar.MONTH);
        boolean needYear = isNeedAddYear(startTime, endTime);
        int year = startTimeCal.get(Calendar.YEAR);
        if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
            return MONTH + SPACE_SEPARATOR + month + (needYear ? (SPACE_SEPARATOR + year) : StringUtils.EMPTY);
        } else {
            return MONTHES[month] + (needYear ? (SPACE_SEPARATOR + year) : StringUtils.EMPTY);
        }
    }

    /**
     * Checks if is need add year.
     * 
     * @param start the start
     * @param end the end
     * @return true, if is need add year
     */
    private static boolean isNeedAddYear(Long start, Long end) {
        Calendar currTimeCal = Calendar.getInstance();
        currTimeCal.setTimeInMillis(System.currentTimeMillis());
        int currYear = currTimeCal.get(Calendar.YEAR);

        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.setTimeInMillis(start);

        int startYear = startTimeCal.get(Calendar.YEAR);
        if (startYear != currYear) {
            return true;
        }

        Calendar endTimeCal = Calendar.getInstance();
        endTimeCal.setTimeInMillis(end);
        int endYear = endTimeCal.get(Calendar.YEAR);
        return startYear != endYear;
    }

    /**
     * @return Returns the defFormat.
     */
    public String getDefFormat() {
        return defFormat;
    }

    /**
     * @param defFormat The defFormat to set.
     */
    public void setDefFormat(String defFormat) {
        this.defFormat = defFormat;
    }

}
