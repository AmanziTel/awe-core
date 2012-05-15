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
public class NetworkNeoStyle extends BaseNeoStyle implements Cloneable {

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultBeamwidth == null) ? 0 : defaultBeamwidth.hashCode());
        result = prime * result + (drawCorrelations ? 1231 : 1237);
        result = prime * result + ((fill == null) ? 0 : fill.hashCode());
        result = prime * result + ((fillSite == null) ? 0 : fillSite.hashCode());
        result = prime * result + (fixSymbolSize ? 1231 : 1237);
        result = prime * result + ((fontSize == null) ? 0 : fontSize.hashCode());
        result = prime * result + (ignoreTransparency ? 1231 : 1237);
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((labeling == null) ? 0 : labeling.hashCode());
        result = prime * result + ((line == null) ? 0 : line.hashCode());
        result = prime * result + ((mainProperty == null) ? 0 : mainProperty.hashCode());
        result = prime * result + ((maximumSymbolSize == null) ? 0 : maximumSymbolSize.hashCode());
        result = prime * result + ((secondaryFontSize == null) ? 0 : secondaryFontSize.hashCode());
        result = prime * result + ((sectorLabelProperty == null) ? 0 : sectorLabelProperty.hashCode());
        result = prime * result + ((sectorLabelTypeId == null) ? 0 : sectorLabelTypeId.hashCode());
        result = prime * result + ((smallSymb == null) ? 0 : smallSymb.hashCode());
        result = prime * result + ((smallestSymb == null) ? 0 : smallestSymb.hashCode());
        result = prime * result + ((symbolSize == null) ? 0 : symbolSize.hashCode());
        result = prime * result + ((symbolTransparency == null) ? 0 : symbolTransparency.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NetworkNeoStyle other = (NetworkNeoStyle)obj;
        if (defaultBeamwidth == null) {
            if (other.defaultBeamwidth != null)
                return false;
        } else if (!defaultBeamwidth.equals(other.defaultBeamwidth))
            return false;
        if (drawCorrelations != other.drawCorrelations)
            return false;
        if (fill == null) {
            if (other.fill != null)
                return false;
        } else if (!fill.equals(other.fill))
            return false;
        if (fillSite == null) {
            if (other.fillSite != null)
                return false;
        } else if (!fillSite.equals(other.fillSite))
            return false;
        if (fixSymbolSize != other.fixSymbolSize)
            return false;
        if (fontSize == null) {
            if (other.fontSize != null)
                return false;
        } else if (!fontSize.equals(other.fontSize))
            return false;
        if (ignoreTransparency != other.ignoreTransparency)
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (labeling == null) {
            if (other.labeling != null)
                return false;
        } else if (!labeling.equals(other.labeling))
            return false;
        if (line == null) {
            if (other.line != null)
                return false;
        } else if (!line.equals(other.line))
            return false;
        if (mainProperty == null) {
            if (other.mainProperty != null)
                return false;
        } else if (!mainProperty.equals(other.mainProperty))
            return false;
        if (maximumSymbolSize == null) {
            if (other.maximumSymbolSize != null)
                return false;
        } else if (!maximumSymbolSize.equals(other.maximumSymbolSize))
            return false;
        if (secondaryFontSize == null) {
            if (other.secondaryFontSize != null)
                return false;
        } else if (!secondaryFontSize.equals(other.secondaryFontSize))
            return false;
        if (sectorLabelProperty == null) {
            if (other.sectorLabelProperty != null)
                return false;
        } else if (!sectorLabelProperty.equals(other.sectorLabelProperty))
            return false;
        if (sectorLabelTypeId == null) {
            if (other.sectorLabelTypeId != null)
                return false;
        } else if (!sectorLabelTypeId.equals(other.sectorLabelTypeId))
            return false;
        if (smallSymb == null) {
            if (other.smallSymb != null)
                return false;
        } else if (!smallSymb.equals(other.smallSymb))
            return false;
        if (smallestSymb == null) {
            if (other.smallestSymb != null)
                return false;
        } else if (!smallestSymb.equals(other.smallestSymb))
            return false;
        if (symbolSize == null) {
            if (other.symbolSize != null)
                return false;
        } else if (!symbolSize.equals(other.symbolSize))
            return false;
        if (symbolTransparency == null) {
            if (other.symbolTransparency != null)
                return false;
        } else if (!symbolTransparency.equals(other.symbolTransparency))
            return false;
        return true;
    }

    @Override
    protected Object clone() {
        NetworkNeoStyle clone = new NetworkNeoStyle();
        clone.setFill(getFill());
        clone.setDefaultBeamwidth(getDefaultBeamwidth());
        clone.setFixSymbolSize(isFixSymbolSize());
        clone.setFontSize(getFontSize());
        clone.setIgnoreTransparency(isIgnoreTransparency());
        clone.setLabel(getLabel());
        clone.setLabeling(getLabeling());
        clone.setLine(getLine());
        clone.setMainProperty(getMainProperty());
        clone.setMaximumSymbolSize(getMaximumSymbolSize());
        clone.setSectorFontSize(getSecondaryFontSize());
        clone.setSectorLabelProperty(getSectorLabelProperty());
        clone.setSiteFill(getSiteFill());
        clone.setSmallestSymb(getSmallestSymb());
        clone.setSmallSymb(getSmallSymb());
        clone.setSymbolSize(getSymbolSize());
        clone.setSymbolTransparency(getSymbolTransparency());
        clone.setSectorLabelTypeId(getSectorLabelTypeId());
        
        return clone;
    }
}