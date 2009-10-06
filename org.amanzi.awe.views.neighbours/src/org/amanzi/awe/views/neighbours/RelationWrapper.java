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

package org.amanzi.awe.views.neighbours;

import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;

/**
 * <p>
 * Wrapper of Neighbour relationship
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class RelationWrapper {
    private final Relationship relation;
    private String name;

    /**
     * Constructor
     * 
     * @param relation - Relationship
     */
    public RelationWrapper(Relationship relation) {
        this.relation = relation;
        name = NeoUtils.getNeighbourName(relation, null);
    }

    /**
     * get serve node
     * 
     * @return node
     */
    public Node getServeNode() {
        return relation.getStartNode();
    }

    /**
     * get Neighbour node
     * 
     * @return node
     */
    public Node getNeighbourNode() {
        return relation.getEndNode();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return Returns the relation.
     */
    public Relationship getRelation() {
        return relation;
    }

}
