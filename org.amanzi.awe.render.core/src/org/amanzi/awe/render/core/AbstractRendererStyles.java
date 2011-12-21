/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse private License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.render.core;

import java.awt.Color;

/**
 * describe common visualization rendererOptions
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractRendererStyles {

    private Scale scale = Scale.MEDIUM;
    private int alpha = (int)(0.6 * 255.0);
    private int largeElementSize = 30;
    private int mediumElementSize = 10;
    private Color borderColor = Color.BLACK;
    private boolean antialiazing = true;
    private int maxSymbolSize = 40;
    private boolean drawLabels = false;
    private boolean scaleSymbols = true;
    private int maxSitesFull = 100;
    private int maxSitesLite = 1000;

    public Color changeColor(Color color, int toAlpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), toAlpha);
    }

    /**
     * @return Returns the scale.
     */
    public Scale getScale() {
        return scale;
    }

    /**
     * @return Returns the alpha.
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * @return Returns the borderColor.
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * @return Returns the antialiazing.
     */
    public boolean isAntialiazing() {
        return antialiazing;
    }

    /**
     * @return Returns the maxSymbolSize.
     */
    public int getMaxSymbolSize() {
        return maxSymbolSize;
    }

    /**
     * @return Returns the drawLabels.
     */
    public boolean isDrawLabels() {
        return drawLabels;
    }

    /**
     * @return Returns the scaleSymbols.
     */
    public boolean isScaleSymbols() {
        return scaleSymbols;
    }

    /**
     * @param scale The scale to set.
     */
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    /**
     * @param alpha The alpha to set.
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    /**
     * @param borderColor The borderColor to set.
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * @param antialiazing The antialiazing to set.
     */
    public void setAntialiazing(boolean antialiazing) {
        this.antialiazing = antialiazing;
    }

    /**
     * @param maxSymbolSize The maxSymbolSize to set.
     */
    public void setMaxSymbolSize(int maxSymbolSize) {
        this.maxSymbolSize = maxSymbolSize;
    }

    /**
     * @param drawLabels The drawLabels to set.
     */
    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
    }

    /**
     * @param scaleSymbols The scaleSymbols to set.
     */
    public void setScaleSymbols(boolean scaleSymbols) {
        this.scaleSymbols = scaleSymbols;
    }

    /**
     * @return Returns the largeElementSize.
     */
    public int getLargeElementSize() {
        return largeElementSize;
    }

    /**
     * @return Returns the mediumElementSize.
     */
    public int getMediumElementSize() {
        return mediumElementSize;
    }

    /**
     * @param largeElementSize The largeElementSize to set.
     */
    public void setLargeElementSize(int largeElementSize) {
        this.largeElementSize = largeElementSize;
    }

    /**
     * @param mediumElementSize The mediumElementSize to set.
     */
    public void setMediumElementSize(int mediumElementSize) {
        this.mediumElementSize = mediumElementSize;
    }

    /**
     * @return Returns the maxSitesFull.
     */
    public int getMaxElementsFull() {
        return maxSitesFull;
    }

    /**
     * @return Returns the maxSitesLite.
     */
    public int getMaxElementsLite() {
        return maxSitesLite;
    }

    /**
     * @param maxSitesFull The maxSitesFull to set.
     */
    public void setMaxSitesFull(int maxSitesFull) {
        this.maxSitesFull = maxSitesFull;
    }

    /**
     * @param maxSitesLite The maxSitesLite to set.
     */
    public void setMaxSitesLite(int maxSitesLite) {
        this.maxSitesLite = maxSitesLite;
    }
}
