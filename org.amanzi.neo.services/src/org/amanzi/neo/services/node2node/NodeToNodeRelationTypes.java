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
package org.amanzi.neo.services.node2node;

import org.neo4j.graphdb.RelationshipType;
import static org.amanzi.neo.services.node2node.NodeToNodeRelationService.NodeToNodeRelationshipTypes;

/**
 * enum to represent type of relation
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public enum NodeToNodeRelationTypes implements INodeToNodeRelationType {
	INTERFERENCE_MATRIX(NodeToNodeRelationshipTypes.INTERFERENCE_MATRIX),
	SHADOWING(NodeToNodeRelationshipTypes.SHADOWING),
	NEIGHBOURS(NodeToNodeRelationshipTypes.NEIGHBOURS),
	TRIANGULATION(NodeToNodeRelationshipTypes.TRIANGULATION);
	
	
	private RelationshipType relationshipType;
	
	/**
	 * set reltion type
	 * @param type type of relation
	 */
	private NodeToNodeRelationTypes(RelationshipType type) {
		relationshipType = type;
	}
	
	/**
	 * get type of relation
	 */
	public RelationshipType getRelationType() {
		return relationshipType;
		
	}

	@Override
	public String getName() {
		return this.getName();
	}
}
