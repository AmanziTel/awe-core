package org.amanzi.splash.neo4j.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.CellRelationTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Traverser;

/**
 * Wrapper of Spreadsheet Cell Node
 * 
 * @author Lagutko_N
 */

public class CellNode extends AbstractNode {
    
    /*
     * Background Color property. Blue component.
     */    
    private static final String CELL_BG_COLOR_B = "bg_color_b";

    /*
     * Background Color property. Green component.
     */
    private static final String CELL_BG_COLOR_G = "bg_color_g";

    /*
     * Background Color property. Red component.
     */
    private static final String CELL_BG_COLOR_R = "bg_color_r";

    /*
     * Font Color property. Blue component.
     */
    private static final String CELL_FONT_COLOR_B = "font_color_b";

    /*
     * Font Color property. Green component.
     */
    private static final String CELL_FONT_COLOR_G = "font_color_g";

    /*
     * Font Color property. Red component.
     */
    private static final String CELL_FONT_COLOR_R = "font_color_r";

    /*
     * Horizontal Alignment property.
     */
    private static final String CELL_HORIZONTAL_ALIGNMENT = "horizontal_alignment";

    /*
     * Vertical Alignment property.
     */
    private static final String CELL_VERTICAL_ALIGNMENT = "vertical_alignment";

    /*
     * Font Style property.
     */
    private static final String CELL_FONT_STYLE = "font_style";

    /*
     * Font Size property.
     */
    private static final String CELL_FONT_SIZE = "font_size";

    /*
     * Font Name property.
     */
    private static final String CELL_FONT_NAME = "font_name";

    /*
     * Cell Definition property.
     */
    private static final String CELL_DEFINITION = "cell_definition";

    /*
     * Name of Value property
     */    
    private static final String CELL_VALUE = "value";
    
    /*
     * Type of this Node
     */
    private static final String CELL_NODE_TYPE = "Spreadsheet_Cell";
    
    /*
     * Name of this Node
     */
    private static final String CELL_NODE_NAME = "Spreadsheet Cell";
    
    /**
     * Constructor. Wraps a Node from database and sets type and name of Node
     * 
     * @param node database node
     */
    public CellNode(Node node) {
        super(node);
        setParameter(INeoConstants.PROPERTY_TYPE_NAME, CELL_NODE_TYPE);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, CELL_NODE_NAME);
    }
    
    /**
     * Computes the Row of Cell
     *
     * @return Row of Cell
     */    
    public RowNode getRow() {
        Node rowNode = node.getSingleRelationship(SplashRelationshipTypes.ROW_CELL, Direction.INCOMING).getStartNode();
        return new RowNode(rowNode);
    }
    
    /**
     * Computes the Column of Cell
     *
     * @return Column of Cell
     */
    
    public ColumnNode getColumn() {
        Node columnNode = node.getSingleRelationship(SplashRelationshipTypes.COLUMN_CELL, Direction.INCOMING).getStartNode();
        return new ColumnNode(columnNode);
    }
    
    /**
     * Sets the value of Cell
     *
     * @param value value of Cell
     */
    
    public void setValue(String value) {
        setParameter(CELL_VALUE, value);
    }
    
    /**
     * Returns the value of Cell
     *
     * @return value of Cell
     */
    
    public String getValue() {
        return (String)getParameter(CELL_VALUE);
    }
    
    /**
     * Returns Definition of Cell
     *
     * @return cell's definition
     */
    public String getDefinition() {
        return (String)getParameter(CELL_DEFINITION);
    }
    
    /**
     * Sets Definition for Cell
     *
     * @param definition cell's definition
     */
    public void setDefinition(String definition) {
        setParameter(CELL_DEFINITION, definition);
    }
    
    /**
     * Returns Font Name of Cell
     *
     * @return cell's font name
     */
    public String getFontName() {
        return (String)getParameter(CELL_FONT_NAME);
    }
    
    /**
     * Sets Font Name for Cell
     *
     * @param fontName cell's font name
     */
    public void setFontName(String fontName) {
        setParameter(CELL_FONT_NAME, fontName);
    }
    
    /**
     * Returns Font Size of Cell
     *
     * @return cell's font size
     */
    public Integer getFontSize() {
        return (Integer)getParameter(CELL_FONT_SIZE);
    }
    
    /**
     * Sets Font Size for Cell 
     *
     * @param fontSize cell's font size
     */    
    public void setFontSize(Integer fontSize) {
        setParameter(CELL_FONT_SIZE, fontSize);
    }
    
    /**
     * Returns Font Style of Cell
     *
     * @return cell's font style
     */
    public Integer getFontStyle() {
        return (Integer)getParameter(CELL_FONT_STYLE);
    }
    
    /**
     * Sets Font Style for Cell
     *
     * @param fontStyle cell's font style
     */
    public void setFontStyle(Integer fontStyle) {
        setParameter(CELL_FONT_STYLE, fontStyle);
    }
    
    /**
     * Returns Vertical Alignment of Cell
     *
     * @return cell's vertical alignment
     */
    public Integer getVerticalAlignment() {
        return (Integer)getParameter(CELL_VERTICAL_ALIGNMENT);
    }
    
    /**
     * Sets Vertical Alignment for Cell
     *
     * @param verticalAlignment cell's vertical alignment
     */
    public void setVerticalAlignment(Integer verticalAlignment) {
        setParameter(CELL_VERTICAL_ALIGNMENT, verticalAlignment);
    }
    
    /**
     * Returns Horizontal Alignment of Cell
     *
     * @return cell's horizontal alignment
     */
    public Integer getHorizontalAlignment() {
        return (Integer)getParameter(CELL_HORIZONTAL_ALIGNMENT);
    }
    
    public void setHorizontalAlignment(Integer horizontalAlignment) {
        setParameter(CELL_HORIZONTAL_ALIGNMENT, horizontalAlignment);
    }
    
    /**
     * Returns Red Component of Cell's Font Color
     *
     * @return red component of Font Color
     */
    public Integer getFontColorR() {
        return (Integer)getParameter(CELL_FONT_COLOR_R);
    }
    
    /**
     * Sets Red Component for Cell's Font Color
     *
     * @param fontColorR red component of Font Color
     */
    public void setFontColorR(Integer fontColorR) {
        setParameter(CELL_FONT_COLOR_R, fontColorR);
    }
    
    /**
     * Returns Green Component of Cell's Font Color
     *
     * @return green component of Font Color
     */
    public Integer getFontColorG() {
        return (Integer)getParameter(CELL_FONT_COLOR_G);
    }
    
    /**
     * Sets Green Component for Cell's Font Color
     *
     * @param fontColorG green component of Font Color
     */
    public void setFontColorG(Integer fontColorG) {
        setParameter(CELL_FONT_COLOR_G, fontColorG);
    }
    
    /**
     * Returns Blue Component of Cell's Font Color
     *
     * @return Blue component of Font Color
     */
    public Integer getFontColorB() {
        return (Integer)getParameter(CELL_FONT_COLOR_B);
    }
    
    /**
     * Sets Blue Component for Cell's Font Color
     *
     * @param fontColorB blue component of Font Color
     */
    public void setFontColorB(Integer fontColorB) {
        setParameter(CELL_FONT_COLOR_B, fontColorB);
    }
    
    /**
     * Returns Red Component of Cell's Background Color
     *
     * @return red component of Background Color
     */
    public Integer getBackgroundColorR() {
        return (Integer)getParameter(CELL_BG_COLOR_R);
    }
    
    /**
     * Sets Red Component for Cell's Background Color
     *
     * @param backgroundColorR red component of Background Color
     */
    public void setBackgroundColorR(Integer backgroundColorR) {
        setParameter(CELL_BG_COLOR_R, backgroundColorR);
    }
    
    /**
     * Returns Red Component of Cell's Background Color
     *
     * @return red component of Background Color
     */
    public Integer getBackgroundColorG() {
        return (Integer)getParameter(CELL_BG_COLOR_G);
    }
    
    /**
     * Sets Green Component for Cell's Background Color
     *
     * @param backgroundColorG green component of Background Color
     */
    public void setBackgroundColorG(Integer backgroundColorG) {
        setParameter(CELL_BG_COLOR_G, backgroundColorG);
    }
    
    /**
     * Returns Blue Component of Cell's Background Color
     *
     * @return blue component of Background Color
     */
    public Integer getBackgroundColorB() {
        return (Integer)getParameter(CELL_BG_COLOR_B);
    }
    
    /**
     * Sets Blue Component for Cell's Background Color
     *
     * @param backgroundColorB blue component of Background Color
     */
    public void setBackgroundColorB(Integer backgroundColorB) {
        setParameter(CELL_BG_COLOR_B, backgroundColorB);
    }
    
    /**
     * Adds a RFD Cell
     *
     * @param rfdNode wrapped RFD Cell
     */    
    public void addRFDNode(CellNode rfdNode) {
        addRelationship(CellRelationTypes.RFD, rfdNode.getUnderlyingNode());
    }
    
    /**
     * Adds a RFG Cell
     *
     * @param rfgNode wrapped RFG Cell
     */    
    public void addRFGNode(CellNode rfgNode) {
        addRelationship(CellRelationTypes.RFG, rfgNode.getUnderlyingNode());
    }
    
    /**
     * Returns Iterator for RFD Cells of this Cell
     *
     * @return
     */
    public Iterator<CellNode> getRFDNodes() {
        return new RFDNodeIterator();
    }
    
    /**
     * Iterator for RFD cells;
     * 
     * @author Lagutko_N
     */
    private class RFDNodeIterator extends AbstractIterator<CellNode> {
        
        public RFDNodeIterator() {
            this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST,
                                          StopEvaluator.END_OF_GRAPH,
                                          ReturnableEvaluator.ALL_BUT_START_NODE,
                                          CellRelationTypes.RFD,
                                          Direction.OUTGOING).iterator();
        }

        @Override
        protected CellNode wrapNode(Node node) {
            return new CellNode(node);
        }
        
    }
}
