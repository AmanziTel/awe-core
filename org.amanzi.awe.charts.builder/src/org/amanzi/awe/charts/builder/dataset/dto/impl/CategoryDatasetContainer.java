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

import org.amanzi.awe.charts.model.IChartModel;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CategoryDatasetContainer extends AbstractChartDatasetContainer<DefaultCategoryDataset> {

    /**
     * @param model
     */
    public CategoryDatasetContainer(IChartModel model) {
        super(model);
    }

    @Override
    protected void finishup(DefaultCategoryDataset dataset) {
        Iterable<ColumnImpl> columns = getCachedColumns();
        for (ColumnImpl column : columns) {
            for (CategoryRowImpl item : column.getRows()) {
                if (item.getValue() == 0d) {
                    continue;
                }
                dataset.addValue(item.getValue(), item.getName(), column);
            }

        }
    }

    @Override
    protected DefaultCategoryDataset createDataset() {
        return new DefaultCategoryDataset();
    }

}
