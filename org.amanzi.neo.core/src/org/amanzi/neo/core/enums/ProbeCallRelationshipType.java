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
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public enum ProbeCallRelationshipType implements DeletableRelationshipType {

    CALL_M(null,null),
    NTPQ_M(null,null),
    CALLEE(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK),
    CALLER(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK),
	PROBE_DATASET(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_WITH_LINKED),
	DRIVE_CALL(null,null),
	PROBE_CALL(null,null),
	CALLS(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_WITH_LINKED), 
	CALL_ANALYSIS(null,null),
	NTPQS(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_WITH_LINKED);

    private RelationDeletableTypes deletableOut;
    private RelationDeletableTypes deletableIn;
    
    /**
     * Constructor.
     * @param aDeletableIn (if link is incoming)
     * @param aDeletableOut (if link is outgoing)
     */
    private ProbeCallRelationshipType(RelationDeletableTypes aDeletableIn, RelationDeletableTypes aDeletableOut){
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
