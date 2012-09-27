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
import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.jfree.data.general.DefaultPieDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PieDatasetContainer extends AbstractChartDatasetContainer<DefaultPieDataset, ColumnCachedItem> {

    /**
     * @param model
     */
    public PieDatasetContainer(IChartModel model) {
        super(model);
    }

    private Map<String, Double> kpiCache = new HashMap<String, Double>();

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

    @Override
    protected void finishup(DefaultPieDataset dataset) {
        for (Entry<String, Double> entry : kpiCache.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

    }

    @Override
    protected void handleAxisCell(IStatisticsRow row, String requiredCell) {
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

    @Override
    protected DefaultPieDataset createDataset() {
        return new DefaultPieDataset();
    }

    @Override
    protected ColumnCachedItem createColumn(IStatisticsRow row, String cellName) {
        return new ColumnCachedItem(row, cellName);
    }

}
