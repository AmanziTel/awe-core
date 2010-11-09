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
import org.neo4j.graphdb.RelationshipType;

/**
 * Base for all relationships.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public interface DeletableRelationshipType extends RelationshipType {

    /**
     * State by direction. 
     *
     * @return RelationDeletableTypes
     */
    public RelationDeletableTypes getDeletableType(Direction aDirection);
    
    /**
     * State if relation is outgoing. 
     *
     * @return RelationDeletableTypes
     */
    public RelationDeletableTypes getDeletableTypeOut();
    
    /**
     * State if relation is incoming. 
     *
     * @return RelationDeletableTypes
     */
    public RelationDeletableTypes getDeletableTypeIn();
}
