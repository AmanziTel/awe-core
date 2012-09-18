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

package org.amanzi.awe.charts.model.provider.impl;

import org.amanzi.awe.charts.model.ChartType;
import org.amanzi.awe.charts.model.IChartDataFilter;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.charts.model.impl.ChartDataFilter;
import org.amanzi.awe.charts.model.impl.ChartModel;
import org.amanzi.awe.charts.model.impl.RangeAxisContainer;
import org.amanzi.awe.charts.model.provider.IChartModelProvider;
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
public class ChartModelProvider implements IChartModelProvider {

    private static class ChartModelProviderInstanceHolder {
        private static final ChartModelProvider INSTANCE = new ChartModelProvider();
    }

    public static ChartModelProvider getInstance() {
        return ChartModelProviderInstanceHolder.INSTANCE;
    }

    private ChartModelProvider() {
    }

    @Override
    public IChartModel getChartModel(String name, String domainAxisName, ChartType chartType, IStatisticsModel model,
            Period period, ChartDataFilter filter, IRangeAxis... rangeAxis) {
        if (rangeAxis.length == 1) {
            return new ChartModel(name, domainAxisName, chartType, model, period, filter, rangeAxis[0]);
        } else {
            return new ChartModel(name, domainAxisName, chartType, model, period, filter, rangeAxis[0], rangeAxis[1]);
        }
    }

    @Override
    public IRangeAxis getRangeAxisContainer(String name, String... cells) {
        return new RangeAxisContainer(name, cells);
    }

    @Override
    public IChartDataFilter getChartDataFilter(long minRowPeriod, long maxRowPeriod, String... groups) {
        return new ChartDataFilter(minRowPeriod, maxRowPeriod, groups);
    }

    @Override
    public IChartDataFilter getChartDataFilter(long minRowPeriod, long maxRowPeriod) {
        return new ChartDataFilter(minRowPeriod, maxRowPeriod);
    }

    @Override
    public IChartDataFilter getChartDataFilter(String... groups) {
        return new ChartDataFilter(groups);
    }

}
