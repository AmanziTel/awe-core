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

    public TimeSeriesCollectionContainer(IChartModel model) {
        super(model);
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
    private TimeSeries updateTimeSeries(TimeSeries ts, ColumnCachedItem container) {

        Date date = new Date(container.getRow().getStartDate());
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

    @Override
    protected void finishup(TimeSeriesCollection dataset) {
        for (ColumnCachedItem column : getCachedColumns()) {
            switch (getModel().getChartAggregation()) {
            case AVERAGE:
                column.setValue(column.getValue() / column.getCount());
                break;
            default:
                break;
            }
            TimeSeries ts = getTimeSeries(dataset, column.getCellName());
            updateTimeSeries(ts, column);
        }
    }

    @Override
    protected TimeSeriesCollection createDataset() {
        return new TimeSeriesCollection();
    }

}
