/**
 * 
 */
package org.amanzi.neo.services.node2node;

import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
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
	private DatasetService datasetService;
	private Node lastChildNode = null;
	private String proxyIndexKey;
	
	public NodeToNodeRelationModel(Node rootModelNode, INodeToNodeRelationType type, String name) {
		datasetService = NeoServiceFactory.getInstance().getDatasetService();
		node2nodeRelationService = NeoServiceFactory.getInstance().getNodeToNodeRelationService();
		
		this.type = type;
		this.rootNode = node2nodeRelationService.getNodeToNodeRelationsRoot(rootModelNode, type, name);
		
		proxyIndexKey = Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.PROXY);
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
		Node node = node2nodeRelationService.findProxy(proxyIndexKey, name);
		if (node == null) {
			node = node2nodeRelationService.createProxy(proxyIndexKey, name, rootNode, lastChildNode);
			lastChildNode = node;
		}
		return node;
	}
	
	public Node getLastChild() { 
		return lastChildNode;
	}
	
	public Relationship getRelation(Node servingNode, Node dependentNode) {
		String servingName = datasetService.getNodeName(servingNode);
		String dependentName = datasetService.getNodeName(dependentNode);
		
		return getRelation(servingName, dependentName);
	}
	
	public Relationship getRelation(String servingNodeId, String dependentNodeId) {
		return node2nodeRelationService.getRelation(servingNodeId, dependentNodeId, type, proxyIndexKey);
	}
	
	/**
	 * Cleares Model
	 * 
	 * @param deleteRootNode is Root Node should be deleted
	 */
	public void clear(boolean deleteRootNode) {
		node2nodeRelationService.clearNodeToNodeStructure(rootNode, new String[] {proxyIndexKey}, deleteRootNode);
	}
}
