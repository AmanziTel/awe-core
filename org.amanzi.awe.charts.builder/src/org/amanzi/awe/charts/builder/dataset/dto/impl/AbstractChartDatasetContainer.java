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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.charts.builder.dataset.dto.IChartDatasetContainer;
import org.amanzi.awe.charts.builder.dataset.dto.IColumnItem;
import org.amanzi.awe.charts.manger.ChartsManager;
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
public abstract class AbstractChartDatasetContainer<T extends Dataset, C extends ColumnCachedItem>
        implements
            IChartDatasetContainer {

    private Map<IRangeAxis, T> datasets;

    private IChartModel model;

    private static final String CACHE_KEY_FORMAT = "%s_%s";

    private Map<String, C> columnCache = new HashMap<String, C>();

    private class ColumnsSorter implements Comparator<IColumnItem> {

        @Override
        public int compare(IColumnItem o1, IColumnItem o2) {
            Long firstTime = o1.getRow().getStartDate();
            Long secondTime = o2.getRow().getStartDate();
            return firstTime.compareTo(secondTime);
        }

    }

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
        columnCache.clear();
        if (model.getSecondRangeAxis() != null) {
            datasets.put(model.getSecondRangeAxis(), buildAxis(model.getSecondRangeAxis()));
        }
        columnCache.clear();
    }

    protected Iterable<C> getCachedColumns() {
        List<C> columns = new ArrayList<C>(columnCache.values());
        Collections.sort(columns, new ColumnsSorter());
        return columns;
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
        Iterable<IStatisticsRow> rows = statisticsModel.getStatisticsRowsInTimeRange(getModel().getPeriod().getId(),
                filter.getMinRowPeriod(), filter.getMaxRowPeriod());
        for (IStatisticsRow row : rows) {
            if (filter.check(row, false)) {
                for (String requiredCell : axis.getCellsNames()) {
                    handleAxisCell(row, requiredCell);
                }
            }
        }
        finishup(dataset);
        return dataset;

    }

    /**
     * @param dataset
     */
    protected abstract void finishup(T dataset);

    /**
     * @param row
     * @param requiredCell
     */
    protected void handleAxisCell(IStatisticsRow row, String requiredCell) {
        for (IStatisticsCell cell : row.getStatisticsCells()) {
            if (!cell.getName().equals(requiredCell)) {
                continue;
            }
            Number cellValue = cell.getValue();
            if (cellValue == null) {
                break;
            }
            C column = getColumnFromCache(row, requiredCell);
            column.increase(cellValue);
            column.addGroup(row.getStatisticsGroup().getPropertyValue());
            break;
        }
    }

    /**
     * @return
     */
    protected abstract T createDataset();

    /**
     * get row name in according to its date format and period
     * 
     * @param row
     * @return
     */
    protected String getName(IStatisticsRow row) {
        return ChartsManager.getInstance().getDefaultDateFormat().format(row.getStartDate());
    }

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
    protected C getColumnFromCache(IStatisticsRow row, String requiredCell) {
        String key = getCacheKey(row, requiredCell);
        C container;
        if (!columnCache.containsKey(key)) {
            container = createColumn(row, requiredCell);
            columnCache.put(key, container);
        } else {
            container = columnCache.get(key);
        }
        return container;
    }

    protected String getCacheKey(IStatisticsRow row, String requiredCell) {
        return String.format(CACHE_KEY_FORMAT, row.getStartDate(), requiredCell);
    }

    protected abstract C createColumn(IStatisticsRow row, String requiredCell);
}
