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

package org.amanzi.awe.report.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.AbstractIntervalXYDataset;

/**
 * <p>
 * Event dataset
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class EventDataset extends AbstractIntervalXYDataset {

    private TimeSeriesCollection collection;
    private List<String> events = new ArrayList<String>(0);
    private TimeSeries series;
    private String name;
    private String propertyName;

    /**
     * 
     */
    public EventDataset(String name, String propertyName) {
        super();
        collection = new TimeSeriesCollection();
        series = new TimeSeries(name);
        collection.addSeries(series);
        this.name=name;
        this.propertyName=propertyName;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return Returns the events.
     */
    public List<String> getEvents() {
        return events;
    }

    public void addEvent(String event, RegularTimePeriod timePeriod) {
        if (!events.contains(event)){
            events.add(event);
        }
        series.addOrUpdate(timePeriod, events.indexOf(event));
    }
    
    public String getEvent(int index){
        return events.get(series.getDataItem(index).getValue().intValue());
    }
    
    @Override
    public int getSeriesCount() {
        return collection.getSeriesCount();
    }

    @Override
    public Comparable getSeriesKey(int i) {
        return collection.getSeriesKey(i);
    }

    @Override
    public Number getEndX(int i, int j) {
        return collection.getEndX(i, j);
    }

    @Override
    public Number getEndY(int i, int j) {
        return 1;
    }

    @Override
    public Number getStartX(int i, int j) {
        return collection.getStartX(i, j);
    }

    @Override
    public Number getStartY(int i, int j) {
        return 1;
    }

    @Override
    public int getItemCount(int i) {
        return collection.getItemCount(i);
    }

    @Override
    public Number getX(int i, int j) {
        return collection.getX(i, j);
    }

    @Override
    public Number getY(int i, int j) {
        return 1;
    }

}
