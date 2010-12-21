/**
 * 
 */
package org.amanzi.neo.services.node2node;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author Kasnitskij_V
 *
 */
public interface INodeToNodeRelationType {
	public RelationshipType getRelationType();
	public String getName();
}
