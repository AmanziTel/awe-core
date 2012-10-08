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

package org.amanzi.awe.views.distribution.charts.coloring;

import java.awt.Color;
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
public class DefaultDistributionColoring extends AbstractDistributionColoring {

    private static final Color COLOR_SELECTED = Color.RED;

    private static final Color COLOR_LESS = Color.BLUE;

    private static final Color COLOR_MORE = Color.GREEN;

    private int selectedIndex;

    private int selectionAdjency;

    /**
     * @param dataset
     */
    public DefaultDistributionColoring(final List<IDistributionBar> distributionBars) {
        super(distributionBars);
    }

    @Override
    protected Color getBarColor(final int index) {
        if (selectedIndex == index) {
            return COLOR_SELECTED;
        } else if (Math.abs(selectedIndex - index) <= selectionAdjency) {
            return index > selectedIndex ? COLOR_MORE : COLOR_LESS;
        }

        return null;
    }

    public void setSelectedIndex(final int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void setSelectionAdjency(final int selectionAdjency) {
        this.selectionAdjency = selectionAdjency;
    }

}
