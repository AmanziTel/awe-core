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

package org.amanzi.awe.charts.model;

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
public interface IChartModel {

    String getName();

    String getDomainAxisName();

    PlotOrientation getPlotOrientation();

    ChartAggregationType getChartAggregation();

    IStatisticsModel getStatisticsModel();

    ChartType getChartType();

    IChartDataFilter getChartDataFilter();

    Period getPeriod();

    IRangeAxis getMainRangeAxis();

    IRangeAxis getSecondRangeAxis();

}
