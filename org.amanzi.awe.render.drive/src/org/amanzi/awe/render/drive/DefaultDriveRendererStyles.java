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

package org.amanzi.awe.render.drive;

import java.awt.Color;

import org.amanzi.awe.render.core.AbstractRendererStyles;

/**
 * contains default styles for measurement data rendering
 * 
 * @author Vladislav_Kondratenko
 */
public class DefaultDriveRendererStyles extends AbstractRendererStyles {
    private static DefaultDriveRendererStyles renderStyle;
    private Color defaultMpColor = Color.MAGENTA;

    public static DefaultDriveRendererStyles getInstance() {
        if (renderStyle == null) {
            renderStyle = new DefaultDriveRendererStyles();
        }
        return renderStyle;
    }

    /**
     * @return Returns the defaultMpColor.
     */
    public Color getDefaultMpColor() {
        return defaultMpColor;
    }

    /**
     * @param defaultMpColor The defaultMpColor to set.
     */
    public void setDefaultMpColor(Color defaultMpColor) {
        this.defaultMpColor = defaultMpColor;
    }
}
