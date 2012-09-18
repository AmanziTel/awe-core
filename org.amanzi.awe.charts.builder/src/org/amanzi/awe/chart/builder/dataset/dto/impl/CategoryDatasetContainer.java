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
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.neo.models.exceptions.ModelException;
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
    protected DefaultCategoryDataset buildAxis(IRangeAxis axis) throws ModelException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        IStatisticsModel statisticsModel = getModel().getStatisticsModel();
        for (IStatisticsRow row : statisticsModel.getStatisticsRows(getModel().getPeriod().getId())) {
            if (getModel().getChartDataFilter().check(row, false)) {
                for (String requiredCell : axis.getCellsNames()) {
                    Number value = null;
                    String rowName = getName(row);
                    for (IStatisticsCell cell : row.getStatisticsCells()) {
                        if (!cell.getName().equals(requiredCell)) {
                            continue;
                        }
                        value = cell.getValue();
                        if (value == null) {
                            break;
                        }
                        dataset.addValue(value, requiredCell, rowName);
                    }
                }
            }
        }
        return dataset;

    }
}
