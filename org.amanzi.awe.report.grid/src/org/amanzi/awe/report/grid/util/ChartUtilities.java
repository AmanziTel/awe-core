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

package org.amanzi.awe.report.grid.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.report.charts.ChartType;
import org.amanzi.awe.report.charts.Charts;
import org.amanzi.awe.report.model.Chart;
import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.database.entity.StatisticsCell;
import org.amanzi.awe.statistics.database.entity.StatisticsGroup;
import org.amanzi.awe.statistics.database.entity.StatisticsRow;
import org.amanzi.neo.services.utils.Pair;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;

/**
 * Utility class to create charts, datasets and other JFreeChart related components
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ChartUtilities {
    public static TimeSeriesCollection[] createChartDataset(Statistics stat, String siteName, String kpiName,
            String timeAggregation, ChartType type) {
        StatisticsGroup statisticsGroup = stat.getGroupByKey(siteName);
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries timeSeries = new TimeSeries(kpiName);
        TimeSeriesCollection tresholdCollection = new TimeSeriesCollection();
        TimeSeries thresholdTimeSeries = new TimeSeries("Average");
        double threshold = 0;
        for (Entry<String, StatisticsRow> entry : statisticsGroup.getRows().entrySet()) {
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
                final StatisticsCell cell = row.getCellByKey(kpiName);
                if (cell != null) {
                    timeSeries.add(period, cell.getValue().doubleValue());
                }
                thresholdTimeSeries.add(period, threshold);
            } else {
                threshold = row.getCellByKey(kpiName).getValue().doubleValue();
            }
        }
        tresholdCollection.addSeries(thresholdTimeSeries);
        timeSeriesCollection.addSeries(timeSeries);
        return new TimeSeriesCollection[] {tresholdCollection, timeSeriesCollection};
    }

    public static TimeSeriesCollection[] createChartDataset(Statistics stat, String siteName, String kpiName,
            String timeAggregation, ChartType type, long startTime, long endTime) {
        StatisticsGroup statisticsGroup = stat.getGroupByKey(siteName);
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries timeSeries = new TimeSeries(kpiName);
        TimeSeriesCollection tresholdCollection = new TimeSeriesCollection();
        TimeSeries thresholdTimeSeries = new TimeSeries("Average");
        double threshold = 0;
        for (Entry<String, StatisticsRow> entry : statisticsGroup.getRows().entrySet()) {
            StatisticsRow row = entry.getValue();
            if (!row.getName().equalsIgnoreCase("total")) {
                Long rowPeriod = row.getPeriod();
                if (rowPeriod >= startTime && rowPeriod <= endTime) {
                    RegularTimePeriod period = null;
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(rowPeriod);
                    if (CallTimePeriods.HOURLY.getId().equalsIgnoreCase(timeAggregation)) {
                        period = new Hour(calendar.getTime());
                    } else if (CallTimePeriods.DAILY.getId().equalsIgnoreCase(timeAggregation)) {
                        period = new Day(calendar.getTime());
                    }
                    final StatisticsCell cell = row.getCellByKey(kpiName);
                    if (cell != null) {
                        timeSeries.add(period, cell.getValue().doubleValue());
                    }
                    thresholdTimeSeries.add(period, threshold);
                }
            } else {
                threshold = row.getCellByKey(kpiName).getValue().doubleValue();
            }
        }
        tresholdCollection.addSeries(thresholdTimeSeries);
        timeSeriesCollection.addSeries(timeSeries);
        return new TimeSeriesCollection[] {tresholdCollection, timeSeriesCollection};
    }

    public static TimeSeriesCollection[] createChartDataset(Statistics stat, String siteName, String kpiName,
            String timeAggregation, ChartType type, long startTime, long endTime, Double thresholdValue) {
        StatisticsGroup statisticsGroup = stat.getGroupByKey(siteName);
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries timeSeries = new TimeSeries(kpiName);
        TimeSeriesCollection tresholdCollection = new TimeSeriesCollection();
        TimeSeries thresholdTimeSeries = new TimeSeries("Average");
        double threshold = 0;
        for (Entry<String, StatisticsRow> entry : statisticsGroup.getRows().entrySet()) {
            StatisticsRow row = entry.getValue();
            if (!row.getName().equalsIgnoreCase("total")) {
                Long rowPeriod = row.getPeriod();
                if (rowPeriod >= startTime && rowPeriod <= endTime) {
                    RegularTimePeriod period = null;
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(rowPeriod);
                    if (CallTimePeriods.HOURLY.getId().equalsIgnoreCase(timeAggregation)) {
                        period = new Hour(calendar.getTime());
                    } else if (CallTimePeriods.DAILY.getId().equalsIgnoreCase(timeAggregation)) {
                        period = new Day(calendar.getTime());
                    }
                    final StatisticsCell cell = row.getCellByKey(kpiName);
                    if (cell != null) {
                        timeSeries.add(period, cell.getValue().doubleValue());
                    }
                    thresholdTimeSeries.add(period, threshold);
                }
            } else {
                threshold = thresholdValue != null ? thresholdValue : row.getCellByKey(kpiName).getValue().doubleValue();
            }
        }
        tresholdCollection.addSeries(thresholdTimeSeries);
        timeSeriesCollection.addSeries(timeSeries);
        return new TimeSeriesCollection[] {tresholdCollection, timeSeriesCollection};
    }

    public static Map<String, Map<String, TimeSeries[]>> createChartDatasets(Statistics stat, String timeAggregation, ChartType type) {
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
                        TimeSeries thresholdTimeSeries = new TimeSeries("Average (" + new DecimalFormat("#0.0").format(threshold)
                                + ")");
                        dsPerKPI.put(kpiName, new TimeSeries[] {thresholdTimeSeries, timeSeries});

                    }
                }
            }
            result.put(key, dsPerKPI);
        }
        return result;
    }

    public static Map<String, Map<String, TimeSeries[]>> createChartDatasets(Statistics stat, String timeAggregation, String kpi,
            ChartType type, List<String> networkElements, long startTime, long endTime, Double thresholdValue) {
        final Map<String, StatisticsGroup> groups = stat.getGroups();
        // site -> KPI -> datasets
        Map<String, Map<String, TimeSeries[]>> result = new HashMap<String, Map<String, TimeSeries[]>>(groups.size());
        for (StatisticsGroup group : groups.values()) {
            String key = group.getGroupName();
            if (!networkElements.contains(key)) {
                continue;
            }
            Map<String, Double> thresholds = new HashMap<String, Double>();
            HashMap<String, TimeSeries[]> dsPerKPI = new HashMap<String, TimeSeries[]>(group.getRows().size());
            result.put(key, dsPerKPI);
            for (Entry<String, StatisticsRow> entry : group.getRows().entrySet()) {
                StatisticsRow row = entry.getValue();

                if (!row.getName().equalsIgnoreCase("total")) {
                    Long rowPeriod = row.getPeriod();
                    if (rowPeriod >= startTime && rowPeriod <= endTime) {
                        RegularTimePeriod period = null;
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTimeInMillis(rowPeriod);
                        if (CallTimePeriods.HOURLY.getId().equalsIgnoreCase(timeAggregation)) {
                            period = new Hour(calendar.getTime());
                        } else if (CallTimePeriods.DAILY.getId().equalsIgnoreCase(timeAggregation)) {
                            period = new Day(calendar.getTime());
                        }

                        StatisticsCell cell = row.getCellByKey(kpi);
                        TimeSeries[] ts = dsPerKPI.get(kpi);
                        ts[1].add(period, cell.getValue().doubleValue());
                        ts[0].add(period, thresholds.get(kpi));
                    }
                } else {
                    StatisticsCell cell = row.getCellByKey(kpi);
                    double threshold = thresholdValue == null ? cell.getValue().doubleValue() : thresholdValue;
                    thresholds.put(kpi, threshold);
                    TimeSeries timeSeries = new TimeSeries(kpi);
                    TimeSeries thresholdTimeSeries = new TimeSeries((thresholdValue == null ? "Average (" : "Threshold (")
                            + new DecimalFormat("#0.0").format(threshold) + ")");
                    dsPerKPI.put(kpi, new TimeSeries[] {thresholdTimeSeries, timeSeries});

                }
            }
            result.put(key, dsPerKPI);
        }
        return result;
    }

    public static DefaultValueDataset createDialChartDataset(Statistics stat, String siteName, String kpiName) {
        StatisticsGroup group = stat.getGroupByKey(siteName);
        StatisticsRow row = group.getRowByKey("total");
        StatisticsCell cell = row.getCellByKey(kpiName);
        DefaultValueDataset ds = new DefaultValueDataset();
        ds.setValue(cell.getValue().doubleValue());
        return ds;
    }

    public static List<Pair<String, DefaultValueDataset>> createDialChartDatasets(Statistics stat, String kpiName) {
        List<Pair<String, DefaultValueDataset>> datasets = new ArrayList<Pair<String, DefaultValueDataset>>(stat.getGroups().size());
        for (StatisticsGroup group : stat.getGroups().values()) {
            final StatisticsRow row = group.getRowByKey("total");
            final StatisticsCell cell = row.getCellByKey(kpiName);
            DefaultValueDataset ds = new DefaultValueDataset();
            ds.setValue(cell.getValue().doubleValue());
            datasets.add(new Pair<String, DefaultValueDataset>(group.getGroupName(), ds));
        }
        Collections.sort(datasets, new Comparator<Pair<String, DefaultValueDataset>>() {

            @Override
            public int compare(Pair<String, DefaultValueDataset> o1, Pair<String, DefaultValueDataset> o2) {
                final double o1val = o2.r().getValue().doubleValue();
                final double o2val = o1.r().getValue().doubleValue();
                if (o1val > o2val) {
                    return 1;
                } else if (o1val < o2val) {
                    return 0;
                }
                return 0;
            }
        });
        return datasets;
    }

    /**
     * @param statistics
     * @param aggregation
     * @param siteName
     * @param kpiName
     * @param chart
     */
    public static void updateTimeChart(Statistics statistics, String aggregation, String siteName, String kpiName, Chart chart) {
        TimeSeriesCollection[] chartDataset = ChartUtilities.createChartDataset(statistics, siteName, kpiName, aggregation,
                ChartType.TIME);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[0], 0);
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[1], 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }

    public static void updateTimeChart(Statistics statistics, String aggregation, String siteName, String kpiName, Chart chart,
            long startTime, long endTime) {
        TimeSeriesCollection[] chartDataset = ChartUtilities.createChartDataset(statistics, siteName, kpiName, aggregation,
                ChartType.TIME, startTime, endTime);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[0], 0);
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[1], 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }
    public static void updateTimeChart(Statistics statistics, String aggregation, String siteName, String kpiName, Chart chart,
            long startTime, long endTime,Double thresholdValue) {
        TimeSeriesCollection[] chartDataset = ChartUtilities.createChartDataset(statistics, siteName, kpiName, aggregation,
                ChartType.TIME, startTime, endTime,thresholdValue);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[0], 0);
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[1], 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }

    /**
     * @param statistics
     * @param aggregation
     * @param siteName
     * @param kpiName
     * @param chart
     */
    public static void updateCombinedChart(final Statistics statistics, final String aggregation, final String siteName,
            final String kpiName, final Chart chart) {
        TimeSeriesCollection[] chartDataset = ChartUtilities.createChartDataset(statistics, siteName, kpiName, aggregation,
                ChartType.COMBINED);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[0], 0);
        Charts.applyDefaultSettingsToDataset(plot, new XYBarDataset(chartDataset[1], 1000 * 60 * 60 * 0.5), 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }

    public static void updateCombinedChart(final Statistics statistics, final String aggregation, final String siteName,
            final String kpiName, final Chart chart, long startTime, long endTime) {
        TimeSeriesCollection[] chartDataset = ChartUtilities.createChartDataset(statistics, siteName, kpiName, aggregation,
                ChartType.COMBINED, startTime, endTime);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[0], 0);
        Charts.applyDefaultSettingsToDataset(plot, new XYBarDataset(chartDataset[1], 1000 * 60 * 60 * 0.5), 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }
    
    public static void updateCombinedChart(final Statistics statistics, final String aggregation, final String siteName,
            final String kpiName, final Chart chart, long startTime, long endTime,Double thresholdValue) {
        TimeSeriesCollection[] chartDataset = ChartUtilities.createChartDataset(statistics, siteName, kpiName, aggregation,
                ChartType.COMBINED, startTime, endTime,thresholdValue);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[0], 0);
        Charts.applyDefaultSettingsToDataset(plot, new XYBarDataset(chartDataset[1], 1000 * 60 * 60 * 0.5), 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }

    /**
     * @param statistics
     * @param siteName
     * @param kpiName
     * @param chart
     */
    public static void updateDialChart(final Statistics statistics, final String siteName, final String kpiName, final Chart chart) {
        DialPlot dialplot = new DialPlot();
        Charts.applyDefaultSettingsToDataset(dialplot, ChartUtilities.createDialChartDataset(statistics, siteName, kpiName), 0);
        Charts.applyMainVisualSettings(dialplot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(dialplot);
    }

    public static Chart createReportChart(String siteName, String kpiName, ChartType chartType) {
        Chart chart = new Chart(siteName);
        chart.addSubtitle(kpiName);
        chart.setChartType(chartType);
        chart.setDomainAxisLabel("Value");
        chart.setRangeAxisLabel("Time");
        return chart;
    }

    public static Chart createReportChart(String kpiName, ChartType chartType) {
        Chart chart = new Chart("Trend");
        chart.addSubtitle(kpiName);
        chart.setChartType(chartType);
        chart.setDomainAxisLabel("Value");
        chart.setRangeAxisLabel("Time");
        return chart;
    }
}
