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

package org.amanzi.awe.charts.model.impl;

import org.amanzi.awe.charts.model.ChartAggregationType;
import org.amanzi.awe.charts.model.ChartType;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.neo.core.period.Period;
import org.jfree.chart.plot.PlotOrientation;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChartModel implements IChartModel {

    private String name;

    private String domainAxisName;

    private IStatisticsModel model;

    private PlotOrientation orientation = PlotOrientation.HORIZONTAL;

    private IRangeAxis mainRangeAxis;

    private ChartType chartType;

    private ChartAggregationType chartAggregation = ChartAggregationType.SUMM;

    private ChartDataFilter filter;

    private Period period;

    private IRangeAxis secondAxis;

    public ChartModel(String name, String domainAxisName, ChartType chartType, IStatisticsModel model, Period period,
            ChartDataFilter filter, IRangeAxis rangeAxis) {
        this.name = name;
        this.domainAxisName = domainAxisName;
        this.model = model;
        this.chartType = chartType;
        this.filter = filter;
        this.period = period;
        this.mainRangeAxis = rangeAxis;
    }

    public ChartModel(String name, String domainAxisName, ChartType chartType, IStatisticsModel model, Period period,
            ChartDataFilter filter, IRangeAxis rangeAxis, IRangeAxis secondAxis) {
        this(name, domainAxisName, chartType, model, period, filter, rangeAxis);
        this.secondAxis = secondAxis;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDomainAxisName() {
        return this.domainAxisName;
    }

    @Override
    public PlotOrientation getPlotOrientation() {
        return this.orientation;
    }

    @Override
    public ChartAggregationType getChartAggregation() {
        return this.chartAggregation;
    }

    @Override
    public IStatisticsModel getStatisticsModel() {
        return this.model;
    }

    @Override
    public ChartType getChartType() {
        return this.chartType;
    }

    @Override
    public Period getPeriod() {
        return period;
    }

    @Override
    public ChartDataFilter getChartDataFilter() {
        return filter;
    }

    @Override
    public IRangeAxis getMainRangeAxis() {
        return mainRangeAxis;
    }

    @Override
    public IRangeAxis getSecondRangeAxis() {
        return secondAxis;
    }

}
