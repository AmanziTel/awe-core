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

package org.amanzi.awe.chart.manger;

import org.amanzi.awe.chart.builder.CategoryChartBuilder;
import org.amanzi.awe.chart.builder.IChartBuilder;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.jfree.chart.JFreeChart;

/**
 * TODO Purpose of
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChartsManager {

    private static class ChartBuilderInstanceHolder {
        private static final ChartsManager INSTANCE = new ChartsManager();
    }

    public static ChartsManager getInstance() {
        return ChartBuilderInstanceHolder.INSTANCE;
    }

    private ChartsManager() {
    }

    public JFreeChart buildChart(IChartModel model) {
        IChartBuilder chart = null;
        try {
            switch (model.getChartType()) {
            case PIE_CATEGORY_CHART:
                break;
            case BAR_DISTRIBUTION_CHART:
            case BAR_DISTRIBUTION_MULTISERIES_CHART:
            case BAR_TOP_WORSE_CHART:
            case STACKED_KPI_VALUES_AS_STACKED_BAR_PER_GROUP_CHART:

                chart = new CategoryChartBuilder(model);
                break;
            case LINE_CDF_CHART:
            case LINE_CDF_MULTISERIES_CHART:
            case LINE_REVERSE_CDF_CHART:
            case LINE_REVERSE_CDF_MULTISERIES_CHART:
            case LINE_STATISTICS_CHART:
            case LINE_STATISTICS_FOR_MULTIPLE_GROUPS_CHART:
            case LINE_STATISTICS_FOR_MULTIPLE_KPIS_CHART:
                break;
            case LINE_TIME_CHART:
            case LINE_TIME_MULTISERIES_CHART:
                break;
            default:
                break;
            }
            return chart.createChart();
        } catch (ModelException e) {
            return null;
        }
    }

}
