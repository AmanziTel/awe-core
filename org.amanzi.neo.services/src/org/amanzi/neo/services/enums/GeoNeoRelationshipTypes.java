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
 * Relationship types defined by the GeoNeo specification for traversing
 * GIS data.
 * @author craig
 * @since 1.0.0
 */
public enum GeoNeoRelationshipTypes implements DeletableRelationshipType {
    NEXT(RelationDeletableTypes.RELINK,RelationDeletableTypes.RELINK),
    LAST(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK),
    PROPERTIES(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_WITH_LINKED),
    IDENTITY_PROPERTIES(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_WITH_LINKED),
    CHILD(RelationDeletableTypes.RELINK,RelationDeletableTypes.DELETE_WITH_LINKED),
    VIRTUAL_DATASET(RelationDeletableTypes.DELETE_WITH_LINKED,RelationDeletableTypes.DELETE_ONLY_LINK),
    ANALYSIS(RelationDeletableTypes.DELETE_WITH_LINKED,RelationDeletableTypes.DELETE_ONLY_LINK),
    KEY(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK),
    LOCATION(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DEETE_WITH_CHECK_LINKED),
    SOURCE(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK),//TODO ? 
    CALLS(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK), 
    CELLS(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_WITH_LINKED), EVENTS(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK),
    //TODO debug
    FILTERS(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK), 
    USE_FILTER(RelationDeletableTypes.DELETE_ONLY_LINK,RelationDeletableTypes.DELETE_ONLY_LINK);
    
    private RelationDeletableTypes deletableOut;
    private RelationDeletableTypes deletableIn;
    
    /**
     * Constructor.
     * @param aDeletableIn (if link is incoming)
     * @param aDeletableOut (if link is outgoing)
     */
    private GeoNeoRelationshipTypes(RelationDeletableTypes aDeletableIn, RelationDeletableTypes aDeletableOut){
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
