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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.amanzi.awe.filters.experimental.GroupFilter;
import org.amanzi.neo.core.utils.Pair;

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
    private boolean changeTransparency = NeoStyleContent.CHANGE_TRANSPARENCY;
    private Integer symbolSize;
    private Integer symbolTransparency;
    private Integer maximumSymbolSize;
    private Integer defaultBeamwidth;
    private Integer iconOffset;
    private Integer fontSize;
    private Integer secondaryFontSize;
    private String mainProperty;
    private String secondaryProperty;
    private List<Pair<ShapeType,List<Color>>> styles=new ArrayList<Pair<ShapeType,List<Color>>>();
    private GroupFilter filter;

    /**
     * @return Returns the main property to use for labelling.
     */
    public String getMainProperty() {
        return mainProperty != null ? mainProperty : NeoStyleContent.DEF_MAIN_PROPERTY;
    }

    /**
     * @param name The main property to use for labelling.
     */
    public void setMainProperty(String name) {
        this.mainProperty = name;
    }

    /**
     * @return Returns the secondary property to use for labelling.
     */
    public String getSecondaryProperty() {
        return secondaryProperty != null ? secondaryProperty : NeoStyleContent.DEF_NONE;
    }

    /**
     * @param name The secondary property to use for labelling.
     */
    public void setSecondaryProperty(String name) {
        this.secondaryProperty = name;
    }

    /**
     * @return Returns the sectorFontSize.
     */
    public Integer getSecondaryFontSize() {
        return secondaryFontSize != null ? secondaryFontSize : NeoStyleContent.DEF_FONT_SIZE_SECTOR;
    }

    /**
     * @param sectorFontSize The sectorFontSize to set.
     */
    public void setSectorFontSize(Integer sectorFontSize) {
        this.secondaryFontSize = sectorFontSize;
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
     * @return Returns the current symbol transparency setting (0-100)
     */
    public Integer getSymbolTransparency() {
        return symbolTransparency != null ? symbolTransparency : NeoStyleContent.DEF_TRANSPARENCY;
    }

    /**
     * @param transparency The symbol transparency to set from 0-100.
     */
    public void setSymbolTransparency(Integer transparency) {
        this.symbolTransparency = transparency;
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
     * sets maximum size of symbol
     * 
     * @param maximumSymbolSize The maximumSymbolSize to set.
     */
    public void setMaximumSymbolSize(Integer maximumSymbolSize) {
        this.maximumSymbolSize = maximumSymbolSize;
    }

    /**
     * @return Returns the defaultBeamwidth.
     */
    public int getDefaultBeamwidth() {
        return defaultBeamwidth;
    }
    
    /**
     * @param defaultBeamwidth The defaultBeamwidth to set.
     */
    public void setDefaultBeamwidth(Integer defaultBeamwidth) {
        this.defaultBeamwidth = defaultBeamwidth;
    }
    
    /**
     * @return the icon offset
     */
    public int getIconOffset() {
        return iconOffset != null ? iconOffset : NeoStyleContent.DEF_ICON_OFFSET;
    }

    /**
     * sets icon offset
     * 
     * @param offset the icon offset to set
     */
    public void setIconOffset(Integer offset) {
        this.iconOffset = offset;
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

    /**
     * @return Returns the styles.
     */
    public List<Pair<ShapeType, List<Color>>> getStyles() {
        return styles;
    }
    public void addStyle(ShapeType shape, Color[] colors){
        styles.add(new Pair<ShapeType,List<Color>>(shape,new ArrayList<Color>(Arrays.asList(colors))));
    }
    public void clearStyle(){
        styles.clear();
    }
public void addFilter(GroupFilter filter){
    this.filter=filter;
}

/**
 * @return Returns the filter.
 */
public GroupFilter getFilter() {
    return filter;
}

    /**
     * Checks if is change transparency.
     * 
     * @return true, if is change transparency
     */
    public boolean isChangeTransparency() {
        return changeTransparency;
    }

    /**
     * Sets the change transparency.
     * 
     * @param changeTransparency the new change transparency
     */
    public void setChangeTransparency(boolean changeTransparency) {
        this.changeTransparency = changeTransparency;
    }

}
