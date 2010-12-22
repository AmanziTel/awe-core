/**
 * 
 */
package org.amanzi.neo.services.node2node;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author Kasnitskij_V
 *
 */
public enum NodeToNodeRelationTypes implements INodeToNodeRelationType {
	INTERFERENCE_MATRIX(NodeToNodeRelationService.NodeToNodeRelationshipTypes.INTERFERENCE_MATRIX);
	
	private RelationshipType relationshipType;
	
	private NodeToNodeRelationTypes(RelationshipType type) {
		relationshipType = type;
	}
	
	public RelationshipType getRelationType() {
		return relationshipType;
		
	}

	@Override
	public String getName() {
		return this.getName();
	}
}
