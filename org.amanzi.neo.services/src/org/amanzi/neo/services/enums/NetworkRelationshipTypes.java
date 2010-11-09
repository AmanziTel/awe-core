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
package org.amanzi.neo.services.enums;

import org.neo4j.graphdb.Direction;

/**
 * RelationshipTypes for Network
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public enum NetworkRelationshipTypes implements DeletableRelationshipType {
    AGGREGATION(null,null),
    CHILD(RelationDeletableTypes.DELETE_WITH_LINKED,RelationDeletableTypes.DELETE_ONLY_LINK),
    SIBLING(null,null),
    INTERFERS(null,null),
    DELTA_REPORT(null,null),
    MISSING(null,null),
    NEIGHBOUR(null,null), 
    TRANSMISSION(null,null),
    TRANSMISSIONS(null,null),
    DIFFERENT(null,null), 
    NEIGHBOUR_DATA(null,null), 
    TRANSMISSION_DATA(null,null),
    AGGREGATE(null,null),
    SECTOR_DRIVE(null,null),
    DRIVE(null,null), 
    SECTOR(null,null),
    NEXT(null, null),
    NEIGHBOURS(null, null),
    INTERFERENCE_DATA(null, null), 
    INTERFERENCE(null, null),
    EXCEPTION_DATA(null, null),
    EXCEPTION(null, null),
    EXCEPTIONS(null, null);
    
    private RelationDeletableTypes deletableOut;
    private RelationDeletableTypes deletableIn;
    
    /**
     * Constructor.
     * @param aDeletableIn (if link is incoming)
     * @param aDeletableOut (if link is outgoing)
     */
    private NetworkRelationshipTypes(RelationDeletableTypes aDeletableIn, RelationDeletableTypes aDeletableOut){
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
