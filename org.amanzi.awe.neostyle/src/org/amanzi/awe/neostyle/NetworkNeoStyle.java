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
 *Network style config
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkNeoStyle extends BaseNeoStyle {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6426890966777143171L;
	
	private Color line;
    private Color fill;
    private Color fillSite;
    private Color label;
    private Integer smallestSymb;
    private Integer smallSymb;
    private Integer labeling;
    private boolean fixSymbolSize = NetworkNeoStyleContent.DEF_FIX_SYMB_SIZE;
    private boolean ignoreTransparency = NetworkNeoStyleContent.IGNORE_TRANSPARENCY;
    private boolean drawCorrelations = NetworkNeoStyleContent.DRAW_CORRELATIONS;
    private Integer symbolSize;
    private Integer symbolTransparency;
    private Integer maximumSymbolSize;
    private Integer defaultBeamwidth;
    private Integer fontSize;
    private Integer secondaryFontSize;
    private String mainProperty;
    private String sectorLabelProperty;
    private String sectorLabelTypeId;
    
    /**
     * @return Returns the main property to use for labelling.
     */
    public String getMainProperty() {
        return mainProperty ;
    }

    public String getSectorLabelTypeId() {
        return sectorLabelTypeId;
    }

    public void setSectorLabelTypeId(String sectorLabelType) {
        this.sectorLabelTypeId = sectorLabelType;
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
    public String getSectorLabelProperty() {
        return sectorLabelProperty;
    }

    /**
     * @param name The secondary property to use for labelling.
     */
    public void setSectorLabelProperty(String name) {
        this.sectorLabelProperty = name;
    }

    /**
     * @return Returns the sectorFontSize.
     */
    public Integer getSecondaryFontSize() {
        return secondaryFontSize ;
    }

    /**
     * @param sectorFontSize The sectorFontSize to set.
     */
    public void setSectorFontSize(Integer sectorFontSize) {
        this.secondaryFontSize = sectorFontSize;
    }
    public NetworkNeoStyle() {
    }
    /**
     * Constructor
     * 
     * @param line line color
     * @param fill fill color
     * @param label label color
     */
    public NetworkNeoStyle(Color line, Color fill, Color label) {
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
        return line;
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
        return fill;
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
        return label;
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
        return smallestSymb ;
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
        return smallSymb ;
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
        return labeling;
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
        return symbolSize ;
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
        return symbolTransparency;
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
        return fillSite ;
    }

    /**
     * @return the maximumSymbolSize
     */
    public int getMaximumSymbolSize() {
        return maximumSymbolSize;
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
     * @return Returns the fontSize.
     */
    public Integer getFontSize() {
        return fontSize ;
    }

    /**
     * @param fontSize The fontSize to set.
     */
    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }



    public boolean isIgnoreTransparency() {
        return ignoreTransparency;
    }

    public void setIgnoreTransparency(boolean ignoreTransparency) {
        this.ignoreTransparency = ignoreTransparency;
    }

    public boolean isDrawCorrelations() {
        System.out.println("Have correlation "+drawCorrelations+" for "+this);
        return drawCorrelations;
    }

    public void setDrawCorrelations(boolean drawCorrelations) {
        System.out.println("Set correlation "+drawCorrelations+" for "+this);
        this.drawCorrelations = drawCorrelations;
    }
    
}
