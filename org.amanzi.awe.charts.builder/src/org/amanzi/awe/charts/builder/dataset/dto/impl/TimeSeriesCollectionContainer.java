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

package org.amanzi.awe.charts.builder.dataset.dto.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.charts.builder.dataset.dto.IColumn;
import org.amanzi.awe.charts.model.IChartModel;
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

    private Map<String, TimeRowImpl> cachedItem = new HashMap<String, TimeRowImpl>();

    public TimeSeriesCollectionContainer(IChartModel model) {
        super(model);
    }

    /**
     * get existed timeseries from dataset or create new one if not exists
     * 
     * @param dataset
     * @param row
     * @return
     */
    private TimeSeries getTimeSeries(TimeSeriesCollection dataset, TimeRowImpl row) {
        TimeSeries ts = dataset.getSeries(row);
        if (ts == null) {
            ts = new TimeSeries(row);
            dataset.addSeries(ts);
        }
        return ts;
    }

    /**
     * update timeSeries
     * 
     * @param dataset
     * @param iColumnPeriod
     * @param item
     * @return
     */
    private TimeSeries updateTimeSeries(TimeSeries ts, IColumn period, CategoryRowImpl item) {

        Date date = new Date(period.getStartDate());
        switch (getModel().getPeriod()) {
        case HOURLY:
        case ALL:
            ts.addOrUpdate(new Hour(date), item.getValue());
            break;
        case DAILY:
            ts.addOrUpdate(new Day(date), item.getValue());
            break;
        case WEEKLY:
            ts.addOrUpdate(new Week(date), item.getValue());
            break;
        case MONTHLY:
            ts.addOrUpdate(new Month(date), item.getValue());
            break;
        default:
            break;
        }
        return ts;
    }

    @Override
    protected void finishup(TimeSeriesCollection dataset) {
        for (ColumnImpl column : getCachedColumns()) {
            for (CategoryRowImpl categoryRow : column.getRows()) {
                switch (getModel().getChartAggregation()) {
                case AVERAGE:
                    categoryRow.setValue(categoryRow.getValue() / categoryRow.getCount());
                    break;
                default:
                    break;
                }
                TimeRowImpl timeRow = getTimeRowImpl(column, categoryRow);
                TimeSeries ts = getTimeSeries(dataset, timeRow);
                updateTimeSeries(ts, column, categoryRow);
            }
        }
    }

    /**
     * @param item
     * @return
     */
    private TimeRowImpl getTimeRowImpl(ColumnImpl column, CategoryRowImpl item) {
        TimeRowImpl row = cachedItem.get(item.getName());
        if (row == null) {
            row = new TimeRowImpl(item.getName());
            row.addGroups(column.getStartDate(), item.getGroupsNames());
            cachedItem.put(item.getName(), row);
        } else {
            row.addGroups(column.getStartDate(), item.getGroupsNames());
        }
        return row;
    }

    @Override
    protected TimeSeriesCollection createDataset() {
        return new TimeSeriesCollection();
    }

}
