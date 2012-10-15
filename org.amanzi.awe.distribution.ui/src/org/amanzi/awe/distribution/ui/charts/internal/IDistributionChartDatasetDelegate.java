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

package org.amanzi.awe.distribution.ui.charts.internal;

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
public interface IDistributionChartDatasetDelegate {

    int getColumnIndex(final Comparable arg0);

    void setDistributionBars(final List<IDistributionBar> distributionBars);

    Comparable getColumnKey(final int arg0);

    List getColumnKeys();

    int getRowIndex(final Comparable arg0);

    Comparable getRowKey(final int arg0);

    List getRowKeys();

    Number getValue(final Comparable arg0, final Comparable arg1);

    int getColumnCount();

    int getRowCount();

    Number getValue(final int arg0, final int arg1);

}
