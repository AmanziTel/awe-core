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
package org.amanzi.awe.neostyle;

import java.awt.Color;

/**
 * <p>
 * Contains information about network and tems style
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeoStyle {
    private Color line;
    private Color fill;
    private Color fillSite;
    private Color label;
    private Integer smallestSymb;
    private Integer smallSymb;
    private Integer labeling;
    private boolean fixSymbolSize = NeoStyleContent.DEF_FIX_SYMB_SIZE;
    private Integer symbolSize;
    private Integer sectorTransparency;
    private Integer maximumSymbolSize;
    private Integer fontSize;
    private Integer sectorFontSize;
    private String siteName;
    private String sectorName;
    /**
     * sets maximum size of symbol
     * 
     * @param maximumSymbolSize The maximumSymbolSize to set.
     */
    public void setMaximumSymbolSize(Integer maximumSymbolSize) {
        this.maximumSymbolSize = maximumSymbolSize;
    }

    /**
     * @return Returns the siteName.
     */
    public String getSiteName() {
        return siteName != null ? siteName : NeoStyleContent.DEF_SITE_NAME;
    }

    /**
     * @param siteName The siteName to set.
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * @return Returns the sectorName.
     */
    public String getSectorName() {
        return sectorName != null ? sectorName : NeoStyleContent.DEF_SECTOR_NAME;
    }

    /**
     * @param sectorName The sectorName to set.
     */
    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    /**
     * @return Returns the sectorFontSize.
     */
    public Integer getSectorFontSize() {
        return sectorFontSize != null ? sectorFontSize : NeoStyleContent.DEF_FONT_SIZE_SECTOR;
    }

    /**
     * @param sectorFontSize The sectorFontSize to set.
     */
    public void setSectorFontSize(Integer sectorFontSize) {
        this.sectorFontSize = sectorFontSize;
    }

    /**
     * Constructor
     * 
     * @param line line color
     * @param fill fill color
     * @param label label color
     */
    public NeoStyle(Color line, Color fill, Color label) {
        super();
        this.line = line;
        this.fill = fill;
        this.label = label;
    }

    /**
     * gets line color
     * 
     * @return
     */
    public Color getLine() {
        return line != null ? line : NeoStyleContent.DEF_COLOR_LINE;
    }

    /**
     * sets color of line
     * 
     * @param line
     */
    public void setLine(Color line) {
        this.line = line;
    }

    /**
     * gets fill color
     * 
     * @return
     */
    public Color getFill() {
        return fill != null ? fill : NeoStyleContent.DEF_COLOR_FILL;
    }

    /**
     * sets fill color
     * 
     * @param fill
     */
    public void setFill(Color fill) {
        this.fill = fill;
    }

    /**
     * get label color
     * 
     * @return
     */
    public Color getLabel() {
        return label != null ? label : NeoStyleContent.DEF_COLOR_LABEL;
    }

    /**
     * sets label color
     * 
     * @param label
     */
    public void setLabel(Color label) {
        this.label = label;
    }

    /**
     * @return Returns the smallestSymb.
     */
    public Integer getSmallestSymb() {
        return smallestSymb != null ? smallestSymb : NeoStyleContent.DEF_SMALLEST_SYMB;
    }

    /**
     * @param smallestSymb The smallestSymb to set.
     */
    public void setSmallestSymb(Integer smallestSymb) {
        this.smallestSymb = smallestSymb;
    }

    /**
     * @return Returns the smallSymb.
     */
    public Integer getSmallSymb() {
        return smallSymb != null ? smallSymb : NeoStyleContent.DEF_SMALL_SYMB;
    }

    /**
     * @param smallSymb The smallSymb to set.
     */
    public void setSmallSymb(Integer smallSymb) {
        this.smallSymb = smallSymb;
    }

    /**
     * @return Returns the labeling.
     */
    public Integer getLabeling() {
        return labeling != null ? labeling : NeoStyleContent.DEF_LABELING;
    }

    /**
     * @param labeling The labeling to set.
     */
    public void setLabeling(Integer labeling) {
        this.labeling = labeling;
    }

    /**
     * @param selection
     */
    public void setFixSymbolSize(boolean fixdSymbolSize) {
        this.fixSymbolSize = fixdSymbolSize;
    }

    /**
     * @return Returns the fixdSymbolSize.
     */
    public boolean isFixSymbolSize() {
        return fixSymbolSize;
    }

    /**
     * @return Returns the symbolSize.
     */
    public Integer getSymbolSize() {
        return symbolSize != null ? symbolSize : NeoStyleContent.DEF_SYMB_SIZE;
    }

    /**
     * @param symbolSize The symbolSize to set.
     */
    public void setSymbolSize(Integer symbolSize) {
        this.symbolSize = symbolSize;
    }

    /**
     * @return Returns the sectorTransparency.
     */
    public Integer getSectorTransparency() {
        return sectorTransparency != null ? sectorTransparency : NeoStyleContent.DEF_SECTOR_TR;
    }

    /**
     * @param sectorTransparency The sectorTransparency to set.
     */
    public void setSectorTransparency(Integer sectorTransparency) {
        this.sectorTransparency = sectorTransparency;
    }

    /**
     * @param fillSite The fillSite to set.
     */
    public void setSiteFill(Color fillSite) {
        this.fillSite = fillSite;
    }

    /**
     * @return Returns the fillSite.
     */
    public Color getSiteFill() {
        return fillSite != null ? fillSite : NeoStyleContent.DEF_COLOR_SITE;
    }

    /**
     * @return the maximumSymbolSize
     */
    public int getMaximumSymbolSize() {
        return maximumSymbolSize != null ? maximumSymbolSize : NeoStyleContent.DEF_MAXIMUM_SYMBOL_SIZE;
    }

    /**
     * @return Returns the fontSize.
     */
    public Integer getFontSize() {
        return fontSize != null ? fontSize : NeoStyleContent.DEF_FONT_SIZE;
    }

    /**
     * @param fontSize The fontSize to set.
     */
    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

}
