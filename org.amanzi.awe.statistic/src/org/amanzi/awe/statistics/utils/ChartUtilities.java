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

import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.database.entity.StatisticsCell;
import org.amanzi.awe.statistics.database.entity.StatisticsGroup;
import org.amanzi.awe.statistics.database.entity.StatisticsRow;
import org.amanzi.awe.statistics.template.Template;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

/**
 * Utility class to create charts, datasets and other JFreeChart related components
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ChartUtilities {

    public static Map<String, Map<String, TimeSeries[]>> createChartDatasets(Statistics stat, String timeAggregation, Template template) {
        final Map<String, StatisticsGroup> groups = stat.getGroups();
        // site -> KPI -> datasets
        Map<String, Map<String, TimeSeries[]>> result = new HashMap<String, Map<String, TimeSeries[]>>(groups.size());
        for (StatisticsGroup group : groups.values()) {
            String key = group.getGroupName();
            Map<String, Double> thresholds = new HashMap<String, Double>();
            HashMap<String, TimeSeries[]> dsPerKPI = new HashMap<String, TimeSeries[]>(group.getRows().size());
            result.put(key, dsPerKPI);
            for (Entry<String, StatisticsRow> entry : group.getRows().entrySet()) {
                StatisticsRow row = entry.getValue();

                if (!row.getName().equalsIgnoreCase("total")) {
                    RegularTimePeriod period = null;
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(row.getPeriod());
                    if (CallTimePeriods.HOURLY.getId().equalsIgnoreCase(timeAggregation)) {
                        period = new Hour(calendar.getTime());
                    } else if (CallTimePeriods.DAILY.getId().equalsIgnoreCase(timeAggregation)) {
                        period = new Day(calendar.getTime());
                    }

                    Map<String, StatisticsCell> cells = row.getCells();
                    for (StatisticsCell cell : cells.values()) {
                        final String kpiName = cell.getName();
                        TimeSeries[] ts = dsPerKPI.get(kpiName);
                        ts[1].add(period, cell.getValue().doubleValue());
                        ts[0].add(period, thresholds.get(kpiName));

                    }
                } else {
                    final Map<String, StatisticsCell> cells = row.getCells();
                    for (StatisticsCell cell : cells.values()) {
                        String kpiName = cell.getName();
                        double threshold = cell.getValue().doubleValue();
                        thresholds.put(kpiName, threshold);
                        TimeSeries timeSeries = new TimeSeries(kpiName);
                        TimeSeries thresholdTimeSeries = new TimeSeries(template.getColumnByName(kpiName).getFunction()
                                .getFunctionName()
                                + " (" + new DecimalFormat("#0.0").format(threshold) + ")");
                        dsPerKPI.put(kpiName, new TimeSeries[] {thresholdTimeSeries, timeSeries});

                    }
                }
            }
            result.put(key, dsPerKPI);
        }
        return result;
    }
   
  
}
