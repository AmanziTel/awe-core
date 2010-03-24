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

import net.refractions.udig.ui.PlatformGIS;

import org.geotools.brewer.color.BrewerPalette;
import org.jfree.chart.renderer.xy.XYBarRenderer;

/**
 * <p>
 * Special renderer for event dataset
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class EventRenderer extends XYBarRenderer {

    private EventDataset eventDataset;
    private Color[] colors;
    private static String ALL_EVENTS = "all events";

    public EventRenderer(EventDataset eventDataset) {
        this.eventDataset = eventDataset;
        initializeColors();
    }

    private void initializeColors() {
     // TODO palette name
        BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette(PlatformGIS.getColorBrewer().getPaletteNames()[0]);
        colors = palette.getColors(palette.getMaxColors());
    }

    @Override
    public Paint getItemPaint(int row, int column) {
        return getEventColor(column);
    }

    /**
     * Determines event color for current event
     * 
     * @param column event column
     * @return color
     */
    private Color getEventColor(int column) {
        String event = eventDataset.getEvent(column);
        int alpha = 0;
        if (ALL_EVENTS.equals(eventDataset.getPropertyName()) || event.equals(eventDataset.getPropertyName())) {
            alpha = 255;
        }
        int i = eventDataset.getEvents().indexOf(event);
        if (i < 0) {
            i = 0;
        }

        int index = i % colors.length;
        Color color = colors[index];
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
