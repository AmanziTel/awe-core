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
import java.util.TreeMap;

import org.amanzi.awe.charts.builder.dataset.dto.IChartDatasetContainer;
import org.amanzi.awe.charts.model.IChartDataFilter;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.jfree.data.general.Dataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractChartDatasetContainer<T extends Dataset> implements IChartDatasetContainer {

    private Map<IRangeAxis, T> datasets;

    private IChartModel model;

    private Map<Long, ColumnImpl> columnCache = new TreeMap<Long, ColumnImpl>();

    public AbstractChartDatasetContainer(IChartModel model) {
        datasets = new HashMap<IRangeAxis, T>();
        this.model = model;
    }

    /**
     * added axis dataset to cache
     * 
     * @param axis
     * @param dataset
     */
    protected void addDataset(IRangeAxis axis, T dataset) {
        datasets.put(axis, dataset);
    }

    @Override
    public T getDataset(IRangeAxis axis) {
        return datasets.get(axis);
    }

    /**
     * get model
     * 
     * @return
     */
    protected IChartModel getModel() {
        return model;
    }

    @Override
    public void computeDatasets() throws ModelException {
        datasets.put(model.getMainRangeAxis(), buildAxis(model.getMainRangeAxis()));
        if (model.getSecondRangeAxis() != null) {
            datasets.put(model.getSecondRangeAxis(), buildAxis(model.getSecondRangeAxis()));
        }
    }

    /**
     * get cached columns
     * 
     * @return
     */
    protected Iterable<ColumnImpl> getCachedColumns() {
        return columnCache.values();
    }

    /**
     * build dataset for axis
     * 
     * @param axis
     * @return
     * @throws ModelException
     */
    protected T buildAxis(IRangeAxis axis) throws ModelException {
        T dataset = createDataset();
        IChartDataFilter filter = getModel().getChartDataFilter();
        IStatisticsModel statisticsModel = getModel().getStatisticsModel();
        Iterable<IStatisticsRow> rows = statisticsModel.getStatisticsRows(getModel().getPeriod().getId());
        for (IStatisticsRow row : rows) {
            if (filter.check(row, false)) {
                ColumnImpl column = getColumnFromCache(row);
                for (String requiredCell : axis.getCellsNames()) {
                    handleAxisCell(column, row, requiredCell);
                }
            }
        }
        finishup(dataset);
        columnCache.clear();
        return dataset;

    }

    /**
     * @param dataset
     */
    protected abstract void finishup(T dataset);

    /**
     * handle single axis try to find required cells in current row, calculate sunn and cache
     * results
     * 
     * @param column
     * @param row
     * @param requiredCell
     */
    protected void handleAxisCell(ColumnImpl column, IStatisticsRow row, String requiredCell) {
        CategoryRowImpl container = (CategoryRowImpl)column.getItemByName(requiredCell);
        if (container == null) {
            container = new CategoryRowImpl(requiredCell);
            column.addItem(container);
        }
        for (IStatisticsCell cell : row.getStatisticsCells()) {
            if (!cell.getName().equals(requiredCell)) {
                continue;
            }
            Number cellValue = cell.getValue();
            if (cellValue == null) {
                break;
            }
            container.increase(cellValue);
            container.addGroup(row.getStatisticsGroup().getPropertyValue());
            break;
        }
    }

    /**
     * @return
     */
    protected abstract T createDataset();

    @Override
    public boolean isMultyAxis() {
        return datasets.size() > 1;
    }

    /**
     * get ColumnCachedItem from cache; or create new one if not exists
     * 
     * @param firstTime
     * @param requiredCell
     * @return
     */
    protected ColumnImpl getColumnFromCache(IStatisticsRow row) {
        ColumnImpl column = columnCache.get(row.getStartDate());
        if (column == null) {
            column = createColumn(row);
            columnCache.put(row.getStartDate(), column);
        }
        return column;
    }

    protected ColumnImpl createColumn(IStatisticsRow row) {
        return new ColumnImpl(row.getStartDate(), row.getEndDate());
    }

}
