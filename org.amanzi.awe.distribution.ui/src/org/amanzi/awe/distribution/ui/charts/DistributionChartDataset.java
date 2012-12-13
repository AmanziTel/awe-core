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

package org.amanzi.awe.distribution.ui.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.type.IDistributionType.ChartType;
import org.amanzi.awe.distribution.ui.charts.internal.CountDistributionChartDataset;
import org.amanzi.awe.distribution.ui.charts.internal.IDistributionChartDatasetDelegate;
import org.amanzi.awe.distribution.ui.charts.internal.PercentageDistributionChartDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class DistributionChartDataset extends AbstractDataset implements CategoryDataset {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 4499019919403799164L;

    private final Map<ChartType, IDistributionChartDatasetDelegate> delegateMap = new HashMap<ChartType, IDistributionChartDatasetDelegate>();

    private IDistributionChartDatasetDelegate currentDelegate;

    private List<IDistributionBar> distributionBars = new ArrayList<IDistributionBar>();

    public DistributionChartDataset() {
        updateDelegate(ChartType.COUNTS);
    }

    @Override
    public int getColumnCount() {
        return currentDelegate.getColumnCount();
    }

    @Override
    public int getColumnIndex(final Comparable arg0) {
        return currentDelegate.getColumnIndex(arg0);
    }

    @Override
    public Comparable getColumnKey(final int arg0) {
        return currentDelegate.getColumnKey(arg0);
    }

    @Override
    public List getColumnKeys() {
        return currentDelegate.getColumnKeys();
    }

    @Override
    public int getRowCount() {
        return currentDelegate.getRowCount();
    }

    @Override
    public int getRowIndex(final Comparable arg0) {
        return currentDelegate.getRowIndex(arg0);
    }

    @Override
    public Comparable getRowKey(final int arg0) {
        return currentDelegate.getRowKey(arg0);
    }

    @Override
    public List getRowKeys() {
        return currentDelegate.getRowKeys();
    }

    @Override
    public Number getValue(final Comparable arg0, final Comparable arg1) {
        return currentDelegate.getValue(arg0, arg1);
    }

    @Override
    public Number getValue(final int arg0, final int arg1) {
        return currentDelegate.getValue(arg0, arg1);
    }

    public void setDistributionBars(final List<IDistributionBar> distributionBars) {
        this.distributionBars = distributionBars;

        currentDelegate.setDistributionBars(distributionBars);

        fireDatasetChanged();
    }

    public void updateDelegate(final ChartType chartType) {
        currentDelegate = delegateMap.get(chartType);

        if (currentDelegate == null) {
            switch (chartType) {
            case COUNTS:
            case LOGARITHMIC:
                currentDelegate = new CountDistributionChartDataset();
                break;
            case PERCENTS:
                currentDelegate = new PercentageDistributionChartDataset();
                break;
            }
            delegateMap.put(chartType, currentDelegate);

            currentDelegate.setDistributionBars(distributionBars);
        }
    }

}
