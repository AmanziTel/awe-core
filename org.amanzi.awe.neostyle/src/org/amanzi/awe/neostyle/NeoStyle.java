package org.amanzi.awe.neostyle;

import java.awt.Color;

/**
 * <p>
 * Contains information about network and tems style
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class NeoStyle {
    private Color line;
    private Color fill;
    private Color label;

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

}
