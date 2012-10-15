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
public abstract class AbstractDistributionColoring implements IDistributionColoring {

    private final List<IDistributionBar> distributionBars;

    protected AbstractDistributionColoring(final List<IDistributionBar> distributionBars) {
        this.distributionBars = distributionBars;
    }

    @Override
    public void updateColors() {
        beforeColoring();
        for (int i = 0; i < getBarCount(); i++) {
            final IDistributionBar bar = distributionBars.get(i);
            bar.setColor(getBarColor(i));
        }
    }

    protected void beforeColoring() {

    }

    protected abstract Color getBarColor(int index);

    protected int getBarCount() {
        return distributionBars.size();
    }
}
