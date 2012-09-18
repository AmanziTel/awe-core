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

package org.amanzi.awe.charts.model.provider;

import org.amanzi.awe.charts.model.ChartType;
import org.amanzi.awe.charts.model.IChartDataFilter;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.charts.model.impl.ChartDataFilter;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.neo.core.period.Period;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public interface IChartModelProvider {
    IChartModel getChartModel(String name, String domainAxisName, ChartType chartType, IStatisticsModel model, Period period,
            ChartDataFilter filter, IRangeAxis... rangeAxis);

    IRangeAxis getRangeAxisContainer(String name, String... cells);

    IChartDataFilter getChartDataFilter(long minRowPeriod, long maxRowPeriod, String... groups);

    IChartDataFilter getChartDataFilter(long minRowPeriod, long maxRowPeriod);

    IChartDataFilter getChartDataFilter(String... groups);
}
