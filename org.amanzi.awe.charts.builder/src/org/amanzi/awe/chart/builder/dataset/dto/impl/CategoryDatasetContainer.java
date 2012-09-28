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

import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CategoryDatasetContainer extends AbstractChartDatasetContainer<DefaultCategoryDataset, ColumnCachedItem> {

    /**
     * @param model
     */
    public CategoryDatasetContainer(IChartModel model) {
        super(model);
    }

    @Override
    protected void finishup(DefaultCategoryDataset dataset) {
        Iterable<ColumnCachedItem> columns = getCachedColumns();
        for (ColumnCachedItem column : columns) {
            if (column.getValue() == 0d) {
                continue;
            }
            dataset.setValue(column.getValue(), column.getCellName(), column);
        }
    }

    @Override
    protected DefaultCategoryDataset createDataset() {
        return new DefaultCategoryDataset();
    }

    @Override
    protected ColumnCachedItem createColumn(IStatisticsRow row, String cellName) {
        return new ColumnCachedItem(row, cellName);
    }

}
