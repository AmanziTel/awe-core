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

package org.amanzi.awe.distribution.ui.charts.coloring;

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
public class TwoColorsBlendDistributionColoring extends AbstractDistributionColoring {

    private float ratio;

    private float percentage;

    private Color leftColor;

    private Color rightColor;

    /**
     * @param dataset
     */
    public TwoColorsBlendDistributionColoring(final List<IDistributionBar> distributionBars) {
        super(distributionBars);
    }

    @Override
    protected void beforeColoring() {
        ratio = 0;
        percentage = (float)1 / getBarCount();
    }

    @Override
    protected Color getBarColor(final int index) {
        final Color result = blend(leftColor, rightColor, ratio);
        ratio += percentage;

        return result;
    }

    protected Color getLeftColor() {
        return leftColor;
    }

    protected Color getRightColor() {
        return rightColor;
    }

    public void setLeftColor(final Color leftColor) {
        this.leftColor = leftColor;
    }

    public void setRightColor(final Color rightColor) {
        this.rightColor = rightColor;
    }

    protected Color blend(final Color leftColor, final Color rightColor, float factor) {
        if (factor < 0.0) {
            factor = 0f;
        }
        if (factor > 1.0) {
            factor = 1f;
        }

        final float complement = 1.0F - factor;
        return new Color((int)(complement * leftColor.getRed() + factor * rightColor.getRed()), (int)(complement
                * leftColor.getGreen() + factor * rightColor.getGreen()), (int)(complement * leftColor.getBlue() + factor
                * rightColor.getBlue()));
    }

}
