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

package org.amanzi.awe.views.distribution.charts;

import java.awt.Paint;

import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.jfree.chart.renderer.category.BarRenderer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionBarRenderer extends BarRenderer {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 2804693537629411550L;

    private final DistributionChartDataset dataset;

    public DistributionBarRenderer(final DistributionChartDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        Comparable< ? > columnKey = dataset.getColumnKey(column);

        if (columnKey instanceof IDistributionBar) {
            IDistributionBar bar = (IDistributionBar)columnKey;

            return bar.getColor();
        }

        return null;
    }

}
