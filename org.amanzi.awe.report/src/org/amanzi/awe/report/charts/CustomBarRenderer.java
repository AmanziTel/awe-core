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

package org.amanzi.awe.report.charts;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.jfree.chart.renderer.category.BarRenderer;

/**
 * Custom bar renderer for report charts.
 * <p>
 * Allows to specify colors. If colors are not specified uses the default color.
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class CustomBarRenderer extends BarRenderer {
    private static final Color DEFAULT_COLOR = new Color(0.75f, 0.7f, 0.4f);// the same as in
    // distribution chart

    /** long serialVersionUID field */
    private static final long serialVersionUID = -4437694093212703129L;
    private List<Color> colors = new ArrayList<Color>();

    @Override
    public Paint getItemPaint(int row, int column) {
        if (!colors.isEmpty() && column < colors.size())
            return colors.get(column);
        return DEFAULT_COLOR;
    }

    public void addColor(int color) {
        colors.add(new Color(color));
    }

    public String getColorPropertyName() {
        return INeoConstants.AGGREGATION_COLOR;
    }

}
