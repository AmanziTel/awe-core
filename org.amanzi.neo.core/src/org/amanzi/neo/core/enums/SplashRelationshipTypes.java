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
package org.amanzi.neo.core.enums;

import org.amanzi.neo.core.database.nodes.DeletableRelationshipType;
import org.neo4j.graphdb.Direction;

/**
 * Enum for RelationshipTypes used in Splash
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public enum SplashRelationshipTypes implements DeletableRelationshipType {
	SPREADSHEET(null,null), 
	CHART_ITEM(null,null), 
	CHART(null,null), 
	RUBY_PROJECT(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK), 
	AWE_PROJECT(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK), 
	PIE_CHART_ITEM(null,null), 
	PIE_CHART(null,null),
	SCRIPT_CELL(null,null), 
	SCRIPT(null,null), 
	SPLASH_FORMAT(null,null), 
	CHART_CATEGORY(null,null),
	CHART_VALUE(null,null), 
	REPORT(null,null), 
	REPORT_TEXT(null,null),
	NEXT_CELL_IN_ROW(RelationDeletableTypes.RELINK,RelationDeletableTypes.RELINK),
	NEXT_CELL_IN_COLUMN(RelationDeletableTypes.RELINK,RelationDeletableTypes.RELINK),
	CHILD_SPREADSHEET(null,null),
	COMPARE_RESULTS(null,null);
	
	private RelationDeletableTypes deletableOut;
    private RelationDeletableTypes deletableIn;
    
    /**
     * Constructor.
     * @param aDeletableIn (if link is incoming)
     * @param aDeletableOut (if link is outgoing)
     */
    private SplashRelationshipTypes(RelationDeletableTypes aDeletableIn, RelationDeletableTypes aDeletableOut){
        deletableIn = aDeletableIn;
        deletableOut = aDeletableOut;
    }

    @Override
    public RelationDeletableTypes getDeletableTypeIn() {
        return deletableIn;
    }

    @Override
    public RelationDeletableTypes getDeletableTypeOut() {
        return deletableOut;
    }
    
    @Override
    public RelationDeletableTypes getDeletableType(Direction aDirection) {
        switch (aDirection) {
        case INCOMING:
            return deletableIn;
        case OUTGOING:
            return deletableOut;
        default:
            throw new IllegalArgumentException("Unknown direction <"+aDirection+">.");
        }
    }
}
