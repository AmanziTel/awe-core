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
import org.amanzi.awe.charts.model.IChartDataFilter;
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

    private PlotOrientation orientation = PlotOrientation.VERTICAL;

    private IRangeAxis mainRangeAxis;

    private ChartType chartType;

    private ChartAggregationType chartAggregation = ChartAggregationType.SUMM;

    private IChartDataFilter filter;

    private Period period;

    private IRangeAxis secondAxis;

    public ChartModel(String name, String domainAxisName, ChartType chartType, IStatisticsModel model, Period period,
            IChartDataFilter filter, IRangeAxis rangeAxis) {
        this.name = name;
        this.domainAxisName = domainAxisName;
        this.model = model;
        this.chartType = chartType;
        this.filter = filter;
        this.period = period;
        this.mainRangeAxis = rangeAxis;
    }

    public ChartModel(String name, String domainAxisName, ChartType chartType, IStatisticsModel model, Period period,
            IChartDataFilter filter, IRangeAxis rangeAxis, IRangeAxis secondAxis) {
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
    public IChartDataFilter getChartDataFilter() {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((chartAggregation == null) ? 0 : chartAggregation.hashCode());
        result = (prime * result) + ((chartType == null) ? 0 : chartType.hashCode());
        result = (prime * result) + ((domainAxisName == null) ? 0 : domainAxisName.hashCode());
        result = (prime * result) + ((filter == null) ? 0 : filter.hashCode());
        result = (prime * result) + ((mainRangeAxis == null) ? 0 : mainRangeAxis.hashCode());
        result = (prime * result) + ((model == null) ? 0 : model.hashCode());
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((orientation == null) ? 0 : orientation.hashCode());
        result = (prime * result) + ((period == null) ? 0 : period.hashCode());
        result = (prime * result) + ((secondAxis == null) ? 0 : secondAxis.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChartModel other = (ChartModel)obj;
        if (chartAggregation != other.chartAggregation) {
            return false;
        }
        if (chartType != other.chartType) {
            return false;
        }
        if (domainAxisName == null) {
            if (other.domainAxisName != null) {
                return false;
            }
        } else if (!domainAxisName.equals(other.domainAxisName)) {
            return false;
        }
        if (filter == null) {
            if (other.filter != null) {
                return false;
            }
        } else if (!filter.equals(other.filter)) {
            return false;
        }
        if (mainRangeAxis == null) {
            if (other.mainRangeAxis != null) {
                return false;
            }
        } else if (!mainRangeAxis.equals(other.mainRangeAxis)) {
            return false;
        }
        if (model == null) {
            if (other.model != null) {
                return false;
            }
        } else if (!model.equals(other.model)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (orientation == null) {
            if (other.orientation != null) {
                return false;
            }
        } else if (!orientation.equals(other.orientation)) {
            return false;
        }
        if (period != other.period) {
            return false;
        }
        if (secondAxis == null) {
            if (other.secondAxis != null) {
                return false;
            }
        } else if (!secondAxis.equals(other.secondAxis)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChartModel [name=" + name + ", domainAxisName=" + domainAxisName + ", model=" + model + ", orientation="
                + orientation + ", mainRangeAxis=" + mainRangeAxis + ", chartType=" + chartType + ", chartAggregation="
                + chartAggregation + ", filter=" + filter + ", period=" + period + ", secondAxis=" + secondAxis + "]";
    }

}
