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
import org.geotools.brewer.color.BrewerPalette;

public class DistributionColoringContainer {

    private final DefaultDistributionColoring defaultColoring;

    private final PaletteDistributionColoring paletteColoring;

    private final ThreeColorsBlendDistributionColoring threeColorsBlend;

    private final TwoColorsBlendDistributionColoring twoColorsBlend;

    private IDistributionColoring current;

    public DistributionColoringContainer(final List<IDistributionBar> distributionBars,
            final DistributionColoringContainer previousContainer) {
        defaultColoring = new DefaultDistributionColoring(distributionBars);
        paletteColoring = new PaletteDistributionColoring(distributionBars);
        threeColorsBlend = new ThreeColorsBlendDistributionColoring(distributionBars);
        twoColorsBlend = new TwoColorsBlendDistributionColoring(distributionBars);

        updateCurrent(previousContainer);
    }

    private void updateCurrent(final DistributionColoringContainer previousContainer) {
        current = defaultColoring;

        if (previousContainer != null) {
            if (previousContainer.current instanceof PaletteDistributionColoring) {
                setPalette();
            } else if (previousContainer.current instanceof ThreeColorsBlendDistributionColoring) {
                setThreeColors();
            } else if (previousContainer.current instanceof TwoColorsBlendDistributionColoring) {
                setTwoColors();
            }
        }
    }

    public IDistributionColoring getCurrent() {
        return current;
    }

    public void setDefault() {
        current = defaultColoring;
        defaultColoring.setSelectedIndex(-1);
    }

    public void setPalette() {
        current = paletteColoring;
    }

    public void setThreeColors() {
        current = threeColorsBlend;
    }

    public void setTwoColors() {
        current = twoColorsBlend;
    }

    public boolean updateLeftColor(final Color leftColor) {
        threeColorsBlend.setLeftColor(leftColor);
        twoColorsBlend.setLeftColor(leftColor);

        return current == threeColorsBlend || current == twoColorsBlend;
    }

    public boolean updateMiddleColor(final Color middleColor) {
        threeColorsBlend.setMiddleColor(middleColor);

        return current == threeColorsBlend;
    }

    public boolean updateRightColor(final Color rightColor) {
        threeColorsBlend.setRightColor(rightColor);
        twoColorsBlend.setRightColor(rightColor);

        return current == threeColorsBlend || current == twoColorsBlend;
    }

    public boolean updateCurrentSelection(final int selectedIndex) {
        defaultColoring.setSelectedIndex(selectedIndex);

        return current == defaultColoring;
    }

    public boolean updateSelectionAdjency(final int selectionAdjency) {
        defaultColoring.setSelectionAdjency(selectionAdjency);

        return current == defaultColoring;
    }

    public boolean updatePalette(final BrewerPalette palette) {
        paletteColoring.setPalette(palette);

        return current == paletteColoring;
    }
}