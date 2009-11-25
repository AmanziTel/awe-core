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
import org.neo4j.api.core.Node;
import org.neo4j.api.core.RelationshipType;

/**
 * Header of Column
 *
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ColumnHeaderNode extends AbstractHeaderNode {
    
    /**
     * Name of this Node's Type
     */
    private static final String COLUMN_HEADER_TYPE = "Column Header";
    
    /**
     * Creates Column Header from Node and set's index
     * 
     * @param node
     */
    public ColumnHeaderNode(Node node, int columnIndex) {
        super(node, columnIndex);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, COLUMN_HEADER_TYPE);        
    }
    
    /**
     * Creates a Header node from Neo Node
     * 
     * @param node node 
     */
    public ColumnHeaderNode(Node node) {
        super(node);
    }
    
    @Override
    protected RelationshipType getRelationshipType() {
        return SplashRelationshipTypes.NEXT_CELL_IN_COLUMN;
    }

    @Override
    public void setIndex(int columnIndex) {
        setCellColumn(columnIndex);
        setCellRow(0);
    }

    @Override
    protected int getIndex() {
        return getCellColumn();
    }

    @Override
    protected String getIndexProperty() {
        return CellNode.CELL_ROW;
    }    

}
