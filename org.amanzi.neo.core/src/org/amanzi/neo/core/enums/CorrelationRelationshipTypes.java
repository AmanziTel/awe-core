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
public enum CorrelationRelationshipTypes implements DeletableRelationshipType {
    
    LINKED_NETWORK_DRIVE,
    CORRELATION,
    CORRELATED,
    CORRELATED_LOCATION;

    @Override
    public RelationDeletableTypes getDeletableType(Direction aDirection) {
        return null;
    }

    @Override
    public RelationDeletableTypes getDeletableTypeIn() {
        return null;
    }

    @Override
    public RelationDeletableTypes getDeletableTypeOut() {
        return null;
    }

}
