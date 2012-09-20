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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PieDatasetContainer extends AbstractChartDatasetContainer<PieDataset> {

    /**
     * @param model
     */
    public PieDatasetContainer(IChartModel model) {
        super(model);
    }

    @Override
    protected PieDataset buildAxis(IRangeAxis axis) throws ModelException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> kpiCache = new HashMap<String, Double>();

        IStatisticsModel statisticsModel = getModel().getStatisticsModel();

        for (IStatisticsRow row : statisticsModel.getStatisticsRows(getModel().getPeriod().getId())) {
            if (getModel().getChartDataFilter().check(row, false)) {
                for (String requiredCell : axis.getCellsNames()) {
                    Number value = null;
                    for (IStatisticsCell cell : row.getStatisticsCells()) {
                        if (!cell.getName().equals(requiredCell)) {
                            continue;
                        }
                        value = cell.getValue();
                        if (value == null) {
                            break;
                        }
                        updateCache(kpiCache, cell.getValue(), cell.getName());
                    }
                }
            }
        }
        for (Entry<String, Double> entry : kpiCache.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        return dataset;
    }

    /**
     * update cached cell value for dataset
     * 
     * @param kpiCache
     * @param value
     * @param cellName
     */
    private void updateCache(Map<String, Double> kpiCache, Number value, String cellName) {
        if (value == null) {
            value = 0d;
        }
        if (!kpiCache.containsKey(cellName)) {
            kpiCache.put(cellName, value.doubleValue());
        } else {
            Double oldValue = kpiCache.get(cellName);
            kpiCache.put(cellName, oldValue + value.doubleValue());
        }

    }
}
