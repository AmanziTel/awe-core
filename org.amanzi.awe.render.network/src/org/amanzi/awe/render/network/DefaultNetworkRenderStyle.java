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

package org.amanzi.awe.render.network;

import org.amanzi.awe.render.core.AbstractRendererStyles;
import java.awt.Color;

/**
 * contain common styles for rendering network data;
 * 
 * @author Vladislav_Kondratenko
 */
public class DefaultNetworkRenderStyle extends AbstractRendererStyles {

    public static DefaultNetworkRenderStyle renderStyle;

    /*
     * properties styles
     */

    private int maxSitesLabel = 50;
    private Color siteFill = new Color(128, 128, 128, getAlpha());
    private Color sectorFill = new Color(255, 255, 128, getAlpha());

    public static DefaultNetworkRenderStyle getInstance() {
        if (renderStyle == null) {
            renderStyle = new DefaultNetworkRenderStyle();
        }
        return renderStyle;
    }

    /**
     * instantiate values;
     */
    public DefaultNetworkRenderStyle() {
        super();
    }

    /**
     * @return Returns the maxSitesLabel.
     */
    public int getMaxSitesLabel() {
        return maxSitesLabel;
    }

    /**
     * @return Returns the siteFill.
     */
    public Color getSiteFill() {
        return siteFill;
    }

    /**
     * @return Returns the sectorFill.
     */
    public Color getSectorFill() {
        return sectorFill;
    }

    /**
     * @param maxSitesLabel The maxSitesLabel to set.
     */
    public void setMaxSitesLabel(int maxSitesLabel) {
        this.maxSitesLabel = maxSitesLabel;
    }

    /**
     * @param siteFill The siteFill to set.
     */
    public void setSiteFill(Color siteFill) {
        this.siteFill = siteFill;
    }

    /**
     * @param sectorFill The sectorFill to set.
     */
    public void setSectorFill(Color sectorFill) {
        this.sectorFill = sectorFill;
    }

}
