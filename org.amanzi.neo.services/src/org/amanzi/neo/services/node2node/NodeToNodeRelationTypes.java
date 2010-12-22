/**
 * 
 */
package org.amanzi.neo.services.node2node;

import org.neo4j.graphdb.RelationshipType;
import static org.amanzi.neo.services.node2node.NodeToNodeRelationService.NodeToNodeRelationshipTypes;

/**
 * @author Kasnitskij_V
 *
 */
public enum NodeToNodeRelationTypes implements INodeToNodeRelationType {
	INTERFERENCE_MATRIX(NodeToNodeRelationshipTypes.INTERFERENCE_MATRIX),
	SHADOWING(NodeToNodeRelationshipTypes.SHADOWING),
	NEIGHBOURS(NodeToNodeRelationshipTypes.NEIGHBOURS),
	TRIANGULATION(NodeToNodeRelationshipTypes.TRIANGULATION);
	
	
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
