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

package org.amanzi.awe.views.distribution.charts.internal;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.distribution.model.bar.IDistributionBar;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class CountDistributionChartDataset implements IDistributionChartDatasetDelegate {

    private final List<String> rowKeys = new ArrayList<String>();

    private List<IDistributionBar> distributionBars = new ArrayList<IDistributionBar>();

    public CountDistributionChartDataset() {
        rowKeys.add("Values");
    }

    @Override
    public int getColumnIndex(final Comparable arg0) {
        return distributionBars.indexOf(arg0);
    }

    @Override
    public Comparable getColumnKey(final int arg0) {
        return distributionBars.get(arg0);
    }

    @Override
    public List getColumnKeys() {
        return distributionBars;
    }

    @Override
    public int getRowIndex(final Comparable arg0) {
        return 0;
    }

    @Override
    public Comparable getRowKey(final int arg0) {
        return "Value";
    }

    @Override
    public List getRowKeys() {
        return rowKeys;
    }

    @Override
    public Number getValue(final Comparable arg0, final Comparable arg1) {
        if (arg1 instanceof IDistributionBar) {
            IDistributionBar bar = (IDistributionBar)arg1;

            return bar.getCount();
        }

        return 0;
    }

    @Override
    public int getColumnCount() {
        return distributionBars.size();
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public Number getValue(final int arg0, final int arg1) {
        return getValue(getRowKey(arg0), getColumnKey(arg1));
    }

    @Override
    public void setDistributionBars(final List<IDistributionBar> distributionBars) {
        this.distributionBars = distributionBars;
    }

}
