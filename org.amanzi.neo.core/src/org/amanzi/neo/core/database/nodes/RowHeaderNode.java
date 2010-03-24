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

package org.amanzi.neo.core.database.nodes;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 * Header of Row
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class RowHeaderNode extends AbstractHeaderNode {
    
    /**
     * Name of 'Last Cell ID' property
     */
    private static final String LAST_CELL_ID = "Row Last Cell ID";
    
    /**
     * Name of this Node's Type
     */
    private static final String ROW_HEADER_TYPE = "Row Header";
    
    /**
     * Creates a Row Header from Neo Node
     * 
     * @param node name of Row Header
     */
    public RowHeaderNode(Node node) {
        super(node);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, ROW_HEADER_TYPE);
    }
    
    /**
     * Creates a Row Header and sets index
     * 
     * @param node node of Row Header
     * @param rowIndex index of this Row Header
     */
    public RowHeaderNode(Node node, int rowIndex) {
        super(node, rowIndex);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, ROW_HEADER_TYPE);
    }

    @Override
    protected RelationshipType getRelationshipType() {
        return SplashRelationshipTypes.NEXT_CELL_IN_ROW;
    }

    @Override
    public void setIndex(int rowIndex) {
        setCellRow(rowIndex);
        setCellColumn(0);
    }

    @Override
    protected int getIndex() {
        return getCellRow();
    }

    @Override
    protected String getIndexProperty() {
        return CellNode.CELL_COLUMN;
    }

    @Override
    public void setLastCellId(long lastCellId) {
        setParameter(LAST_CELL_ID, lastCellId);
    }
    
    @Override
    public Long getLastCellId() {
        return (Long)getParameter(LAST_CELL_ID);
    }
}
