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

package org.amanzi.awe.chart.builder;

import java.awt.Font;

import org.amanzi.awe.charts.model.IChartModel;

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

    protected AbstractChartBuilder(IChartModel model) {
        this.model = model;
    }

    protected IChartModel getModel() {
        return model;
    }

    protected Font getDefaultDomainAxisFont() {
        return DEFAULT_DOMAIN_FONT;
    }
}
