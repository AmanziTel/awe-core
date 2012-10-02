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

package org.amanzi.awe.charts.builder.dataset.dto.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.charts.model.IChartModel;
import org.jfree.data.general.DefaultPieDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PieDatasetContainer extends AbstractChartDatasetContainer<DefaultPieDataset> {

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
    private void updateCache(Number value, String cellName) {
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
        Iterable<ColumnImpl> columns = getCachedColumns();
        for (ColumnImpl column : columns) {
            for (RowImpl item : column.getRows()) {
                if (item.getValue() == 0d) {
                    continue;
                }
                updateCache(item.getValue(), item.getCellName());
            }

        }
        for (Entry<String, Double> piePart : kpiCache.entrySet()) {
            dataset.setValue(piePart.getKey(), piePart.getValue());
        }
    }

    @Override
    protected DefaultPieDataset createDataset() {
        return new DefaultPieDataset();
    }

}
