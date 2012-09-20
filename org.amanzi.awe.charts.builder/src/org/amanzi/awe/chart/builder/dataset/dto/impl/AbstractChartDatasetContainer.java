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

import org.amanzi.awe.chart.builder.dataset.dto.IChartDatasetContainer;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.neo.core.period.PeriodManager;
import org.amanzi.neo.dateformat.DateFormatManager;
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

    private static final PeriodManager PERIOD_MANAGER = PeriodManager.getInstance();

    private static final DateFormatManager DATE_FORMAT_MANAGER = DateFormatManager.getInstance();
    private Map<IRangeAxis, T> datasets;

    private IChartModel model;

    public AbstractChartDatasetContainer(IChartModel model) {
        datasets = new HashMap<IRangeAxis, T>();
        this.model = model;
    }

    protected void addDataset(IRangeAxis axis, T dataset) {
        datasets.put(axis, dataset);
    }

    public T getDataset(IRangeAxis axis) {
        return datasets.get(axis);
    }

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

    protected abstract T buildAxis(IRangeAxis axis) throws ModelException;

    protected String getName(IStatisticsRow row) {
        return PERIOD_MANAGER.getPeriodName(getModel().getPeriod(), DATE_FORMAT_MANAGER.longToDate(row.getStartDate()),
                DATE_FORMAT_MANAGER.longToDate(row.getEndDate()));
    }

    @Override
    public boolean isMultyAxis() {
        return datasets.size() > 1;
    }
}
