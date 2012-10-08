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
public class ThreeColorsBlendDistributionColoring extends TwoColorsBlendDistributionColoring {

    private float ratioBeforeMiddle;

    private float ratioAfterMidle;

    private float percentageBeforeMiddle;

    private float percentageAfterMiddle;

    private int middleIndex;

    private Color middleColor;

    /**
     * @param dataset
     */
    public ThreeColorsBlendDistributionColoring(final List<IDistributionBar> distributionBars) {
        super(distributionBars);
    }

    @Override
    protected void beforeColoring() {
        middleIndex = getBarCount() / 2;

        ratioBeforeMiddle = 0;
        ratioAfterMidle = 0;

        percentageBeforeMiddle = middleIndex == 0 ? 1 : (float)1 / middleIndex;
        percentageAfterMiddle = getBarCount() - middleIndex == 0 ? 1 : (float)1 / (getBarCount() - middleIndex);
    }

    public void setMiddleColor(final Color middleColor) {
        this.middleColor = middleColor;
    }

    @Override
    protected Color getBarColor(final int index) {
        Color result = null;

        if (index < middleIndex) {
            result = blend(getLeftColor(), middleColor, ratioBeforeMiddle);
            ratioBeforeMiddle += percentageBeforeMiddle;
        } else if (index > middleIndex) {
            result = blend(middleColor, getRightColor(), ratioAfterMidle);
            ratioAfterMidle += percentageAfterMiddle;
        } else {
            result = middleColor;
        }

        return result;
    }
}
