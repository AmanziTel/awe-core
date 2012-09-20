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

package org.amanzi.awe.chart.builder.dataset.dto.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TimeSeriesCollectionContainer extends AbstractChartDatasetContainer<TimeSeriesCollection> {

    private static final String CACHE_KEY_FORMAT = "%s _ %s";

    public TimeSeriesCollectionContainer(IChartModel model) {
        super(model);
    }

    private static class TimeSeriesContainer {
        private String cellName;

        private Long startDate;

        private double value = 0.0d;

        private int count = 0;

        protected TimeSeriesContainer(Long startDate, String cellName) {
            this.startDate = startDate;
            this.cellName = cellName;
        }

        protected void increase(Number value) {
            this.value += value.doubleValue();
            this.count++;
        }

        protected int getCount() {
            return count;
        }

        protected double getValue() {
            return value;
        }

        protected String getCellName() {
            return cellName;
        }

        protected void setValue(Double value) {
            this.value = value;
        }

        protected Long getStartDate() {
            return startDate;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = HashCodeBuilder.reflectionHashCode(cellName + startDate, false);
            result = HashCodeBuilder.reflectionHashCode(cellName + startDate, false);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TimeSeriesContainer other = (TimeSeriesContainer)obj;
            if (cellName == null) {
                if (other.cellName != null)
                    return false;
            } else if (!cellName.equals(other.cellName))
                return false;
            if (startDate == null) {
                if (other.startDate != null)
                    return false;
            } else if (!startDate.equals(other.startDate))
                return false;
            return true;
        }
    }

    private Map<String, TimeSeriesContainer> tsCache = new HashMap<String, TimeSeriesContainer>();

    @Override
    protected TimeSeriesCollection buildAxis(IRangeAxis axis) throws ModelException {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        IStatisticsModel statisticsModel = getModel().getStatisticsModel();
        for (IStatisticsRow row : statisticsModel.getStatisticsRows(getModel().getPeriod().getId())) {
            if (getModel().getChartDataFilter().check(row, false)) {
                for (String requiredCell : axis.getCellsNames()) {

                    Long firstTime = getModel().getPeriod().getStartTime(row.getStartDate());
                    TimeSeriesContainer container = getContainer(firstTime, requiredCell);

                    for (IStatisticsCell cell : row.getStatisticsCells()) {
                        if (!cell.getName().equals(requiredCell)) {
                            continue;
                        }
                        Number cellValue = cell.getValue();
                        if (cellValue == null) {
                            break;
                        }
                        container.increase(cellValue);
                    }
                }
            }
        }
        prepareDataset(dataset);
        return dataset;

    }

    /**
     * compute dataset in according width cache
     * 
     * @param dataset
     */
    private void prepareDataset(TimeSeriesCollection dataset) {
        for (TimeSeriesContainer container : tsCache.values()) {
            switch (getModel().getChartAggregation()) {
            case AVERAGE:
                container.setValue(container.getValue() / container.getCount());
                break;
            default:
                break;
            }
            TimeSeries ts = getTimeSeries(dataset, container.getCellName());
            updateTimeSeries(ts, container);
        }
    }

    /**
     * get existed timeseries from dataset or create new one if not exists
     * 
     * @param dataset
     * @param cellName
     * @return
     */
    private TimeSeries getTimeSeries(TimeSeriesCollection dataset, String cellName) {
        TimeSeries ts = dataset.getSeries(cellName);
        if (ts == null) {
            ts = new TimeSeries(cellName);
            dataset.addSeries(ts);
        }
        return ts;
    }

    /**
     * update timeSeries
     * 
     * @param dataset
     * @param container
     * @return
     */
    private TimeSeries updateTimeSeries(TimeSeries ts, TimeSeriesContainer container) {

        Date date = new Date(container.getStartDate());
        switch (getModel().getPeriod()) {
        case HOURLY:
        case ALL:
            ts.addOrUpdate(new Hour(date), container.getValue());
            break;
        case DAILY:
            ts.addOrUpdate(new Day(date), container.getValue());
            break;
        case WEEKLY:
            ts.addOrUpdate(new Week(date), container.getValue());
            break;
        case MONTHLY:
            ts.addOrUpdate(new Month(date), container.getValue());
            break;
        default:
            break;
        }
        return ts;
    }

    /**
     * get TimeSeriesContainer from cache; or create new one if not exists
     * 
     * @param firstTime
     * @param requiredCell
     * @return
     */
    private TimeSeriesContainer getContainer(Long firstTime, String requiredCell) {
        String key = String.format(CACHE_KEY_FORMAT, firstTime, requiredCell);
        TimeSeriesContainer container;
        if (!tsCache.containsKey(key)) {
            container = new TimeSeriesContainer(firstTime, requiredCell);
            tsCache.put(key, container);
        } else {
            container = tsCache.get(key);
        }
        return container;
    }

}
