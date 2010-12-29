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

import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Pair;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * Model of node to node relation
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NodeToNodeRelationModel {
    // node-root
	private Node rootNode;
	// type of node to node relation
	private INodeToNodeRelationType type;
	// node to node relation service
	private NodeToNodeRelationService node2nodeRelationService;
	// dataset service
	private DatasetService datasetService;
	// node-lastChild
	private Node lastChildNode = null;
	private String proxyIndexKey;
	
	/**
	 * constructor of this class
	 * @param rootModelNode rootModel-node
	 * @param type type of relation
	 * @param name name of model
	 */
	public NodeToNodeRelationModel(Node rootModelNode, INodeToNodeRelationType type, String name) {
		datasetService = NeoServiceFactory.getInstance().getDatasetService();
		node2nodeRelationService = NeoServiceFactory.getInstance().getNodeToNodeRelationService();
		
		this.type = type;
		this.rootNode = node2nodeRelationService.getNodeToNodeRelationsRoot(rootModelNode, type, name);
		
		proxyIndexKey = Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.PROXY);
	}
	
	/**
	 * method to add relation between serving node and dependent node
	 * with some parameters to relation
	 *
	 * @param servingNode
	 * @param dependentNode
	 * @param parameters
	 */
	public int addRelation(Node servingNode, Node dependentNode, Map<String, Object> parameters) {
	    Pair<Node, Boolean> servingPair = getProxy(servingNode, (String)servingNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
	    Pair<Node, Boolean> dependentPair = getProxy(dependentNode, (String)dependentNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
	    
		Node servingProxyNode = servingPair.getLeft();
		Node dependentProxyNode = dependentPair.getLeft();
		Relationship relationship = servingProxyNode.createRelationshipTo(dependentProxyNode, (RelationshipType) type.getRelationType());
		
		int count = 0;
		if (servingPair.getRight()) {
		    count++;
		}
		if (dependentPair.getRight()) {
		    count++;
		}
		
		for (Entry<String, Object> entry : parameters.entrySet()) {
			relationship.setProperty(entry.getKey(), entry.getValue());
		}
		
		return count;
	}
	
	/**
	 * method to get proxyNode
	 *
	 * @param name
	 * @return
	 */
	private Pair<Node, Boolean> getProxy(Node originalNode, String name) {
		Node node = node2nodeRelationService.findProxy(proxyIndexKey, name);
		boolean created = false;
		if (node == null) {
			node = node2nodeRelationService.createProxy(originalNode, proxyIndexKey, name, rootNode, lastChildNode);
			lastChildNode = node;
			created = true;
		}
		return new Pair<Node, Boolean>(node, created);
	}
	
	/**
	 * method to get lastChild node
	 *
	 * @return last child node
	 */
	public Node getLastChild() { 
		return lastChildNode;
	}
	
	/**
	 * method to get relation between two nodes
	 *
	 * @param servingNode one of two nodes
	 * @param dependentNode one of two nodes
	 * @return relation between two nodes
	 */
	public Relationship getRelation(Node servingNode, Node dependentNode) {
		String servingName = datasetService.getNodeName(servingNode);
		String dependentName = datasetService.getNodeName(dependentNode);
		
		return getRelation(servingName, dependentName);
	}
	
	/**
	 * method to get relation between two nodes
	 *
	 * @param servingNodeId id of serving node
	 * @param dependentNodeId id of dependent node 
	 * @return relation between two nodes
	 */
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
