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

    /**
     * Default Drive Renderer Styles instance
     */
    private static DefaultDriveRendererStyles renderStyle;

    /**
     * default colors
     */
    private Color defaultLocationColor = Color.MAGENTA;
    private Color defaultLabelColor = Color.GRAY;
    private Color defaultLineColor = Color.GRAY;

    /**
     * parameters
     */
    private Integer defaultFontSize;
    private String defaultLocationLabelType;
    private String defaultMeasurementPropertyName;

    public static DefaultDriveRendererStyles getInstance() {
        if (renderStyle == null) {
            renderStyle = new DefaultDriveRendererStyles();
        }
        return renderStyle;
    }

    /**
     * @return Returns the defaultLocationColor.
     */
    public Color getLocationColor() {
        return defaultLocationColor;
    }

    /**
     * @param defaultLocationColor The defaultMeasurementColor to set.
     */
    public void setLocationColor(Color defaultLocationColor) {
        this.defaultLocationColor = defaultLocationColor;
    }

    /**
     * @return default location color
     */
    public Color getDefaultLocationColor() {
        return defaultLocationColor;
    }

    /**
     * @param defaultLocationColor color
     */
    public void setDefaultLocationColor(Color defaultLocationColor) {
        this.defaultLocationColor = defaultLocationColor;
    }

    /**
     * @return default label color
     */
    public Color getDefaultLabelColor() {
        return defaultLabelColor;
    }

    /**
     * @param defaultFontColor color
     */
    public void setDefaultLabelColor(Color defaultFontColor) {
        this.defaultLabelColor = defaultFontColor;
    }

    /**
     * @return default line color
     */
    public Color getDefaultLineColor() {
        return defaultLineColor;
    }

    /**
     * @param defaultLineColor color
     */
    public void setDefaultLineColor(Color defaultLineColor) {
        this.defaultLineColor = defaultLineColor;
    }

    /**
     * @return font size
     */
    public Integer getDefaultFontSize() {
        return defaultFontSize;
    }

    /**
     * @param defaultFontSize font size
     */
    public void setDefaultFontSize(Integer defaultFontSize) {
        this.defaultFontSize = defaultFontSize;
    }

    /**
     * @return default location label type
     */
    public String getDefaultLocationLabelType() {
        return defaultLocationLabelType;
    }

    /**
     * @param defaultLocationLabelType label type
     */
    public void setDefaultLocationLabelType(String defaultLocationLabelType) {
        this.defaultLocationLabelType = defaultLocationLabelType;
    }

    /**
     * @return measurement property name
     */
    public String getDefaultMeasurementPropertyName() {
        return defaultMeasurementPropertyName;
    }

    /**
     * @param defaultMeasurementPropertyName measurement property name
     */
    public void setDefaultMeasurementPropertyName(String defaultMeasurementPropertyName) {
        this.defaultMeasurementPropertyName = defaultMeasurementPropertyName;
    }

}
