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

package org.amanzi.awe.charts.builder;

import java.awt.Font;

import org.amanzi.awe.charts.model.IChartModel;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.ui.RectangleEdge;

/**
 * <p>
 * Common builder for all charts
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractChartBuilder implements IChartBuilder {

    private IChartModel model;

    private static final Font DEFAULT_DOMAIN_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    private static final Font DEFAULT_SUBTITLE_FONT = new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 10);

    private static final TextTitle SUB_TITLE = new TextTitle();

    private static final Font DEFAULT_AXIS_FONT = new Font(Font.DIALOG, Font.BOLD, 14);

    static {
        SUB_TITLE.setFont(DEFAULT_SUBTITLE_FONT);
        SUB_TITLE.setPosition(RectangleEdge.BOTTOM);
    }

    protected AbstractChartBuilder(IChartModel model) {
        this.model = model;
    }

    protected IChartModel getModel() {
        return model;
    }

    protected Font getDefaulTickLabelFont() {
        return DEFAULT_DOMAIN_FONT;
    }

    protected Title getSubTitle(String text) {
        SUB_TITLE.setText(text);;
        return SUB_TITLE;
    }

    protected Font getDefaultAxisFont() {
        return DEFAULT_AXIS_FONT;
    }
}
