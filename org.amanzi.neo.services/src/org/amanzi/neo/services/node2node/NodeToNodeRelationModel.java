/**
 * 
 */
package org.amanzi.neo.services.node2node;

import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * @author Kasnitskij_V
 *
 */
public class NodeToNodeRelationModel {
	private Node rootNode;
	private INodeToNodeRelationType type;
	private NodeToNodeRelationService node2nodeRelationService;
	private Node lastChildNode = null;
	private String indexKey;
	
	public NodeToNodeRelationModel(Node rootModelNode, INodeToNodeRelationType type, String name) {
		node2nodeRelationService = new NodeToNodeRelationService();
		this.type = type;
		this.rootNode = node2nodeRelationService.getNodeToNodeRelationsRoot(rootModelNode, NodeToNodeRelationTypes.INTERFERENCE_MATRIX, name);
		setIndexKey(rootNode);
	}
	
	public void addRelation(Node servingNode, Node dependentNode, Map<String, Object> parameters) {		
		Node servingProxyNode = getProxy((String)servingNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
		Node dependentProxyNode = getProxy((String)dependentNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
		Relationship relationship = servingProxyNode.createRelationshipTo(dependentProxyNode, (RelationshipType) type);
		
		for (Entry<String, Object> entry : parameters.entrySet()) {
			relationship.setProperty(entry.getKey(), entry.getValue());
		}
	}
	
	private Node getProxy(String name) {
		Node node = node2nodeRelationService.findProxy(indexKey, name);
		if (node == null) {
			node = node2nodeRelationService.createProxy(indexKey, name, rootNode, lastChildNode);
			lastChildNode = node;
		}
		return node;
	}
	
	private void setIndexKey(Node rootNode) {
		indexKey = Utils.getLuceneIndexKeyByProperty(rootNode, 
				INeoConstants.PROPERTY_NAME_NAME, NodeTypes.PROXY);
	}
	
	public Node getLastChild() { 
		return lastChildNode;
	}
	
	public Relationship getRelation(Node servingNode, Node dependentNode) {
		return null;
	}
	
	public Relationship getRelation(String servingNode, String dependentNode) {
		return null;
	}
}
