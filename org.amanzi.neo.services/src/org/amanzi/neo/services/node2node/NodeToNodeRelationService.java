/**
 * 
 */
package org.amanzi.neo.services.node2node;

import java.util.Iterator;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;

/**
 * @author Kasnitskij_V
 *
 */
public class NodeToNodeRelationService extends AbstractService {
	public enum NodeToNodeRelationshipTypes implements RelationshipType {
		INTERFERENCE_MATRIX, SHADOWING, NEIGHBOURS, TRIANGULATION;
	}
	
	private DatasetService datasetService;
	
	public NodeToNodeRelationService() {
		datasetService = NeoServiceFactory.getInstance().getDatasetService();
	}
	
	public Node findNodeToNodeRelationsRoot(Node rootNode, INodeToNodeRelationType type, final String nameRelation) {	
		Traverser traverser = Traversal.description().relationships(type.getRelationType(), Direction.OUTGOING).filter(new Predicate<Path>() {
			
			@Override
			public boolean accept(Path item) {
				return item.endNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(nameRelation);
			}
		}).traverse(rootNode);
		
		Iterator<Node> nodes = traverser.nodes().iterator();
		
		if (nodes.hasNext()) {
			Node result = nodes.next();
			
			if (nodes.hasNext()) {
				//TODO: throw exception
			}
			
			return result;
		}
		return null;
	}
	
	private Node createNodeToNodeRelationsRoot(Node rootNode, INodeToNodeRelationType type, String name) {
		Node createdNode = datasetService.createNode(NodeTypes.ROOT_PROXY, name);
		rootNode.createRelationshipTo(createdNode, type.getRelationType());
		return createdNode;
	}
	
	public Node getNodeToNodeRelationsRoot(Node rootNode, INodeToNodeRelationType type, String name) {
		Node returnedNode = findNodeToNodeRelationsRoot(rootNode, type, name);
		
		if (returnedNode == null) {
			returnedNode = createNodeToNodeRelationsRoot(rootNode, type, name);
		}
		
		return returnedNode;
	}
	
	public Node findProxy(String indexKey, String indexValue) {
		return getIndexService().getSingleNode(indexKey, indexValue);
	}
	
	public Node createProxy(String index, String name, Node rootNode, Node lastChild) {
		Node node = datasetService.createNode(NodeTypes.PROXY, name);
		datasetService.addChild(rootNode, node, lastChild);
		getIndexService().index(node, index, name);		
		
		return node;
	}
	
	public Iterator<Relationship> getRelations(Node rootNode, final INodeToNodeRelationType type) {
		return datasetService.getChildTraversal(null).
			   relationships(type.getRelationType(), Direction.OUTGOING).
			   filter(new Predicate<Path>() {
				
				@Override
				public boolean accept(Path item) {
					return ((item.lastRelationship().getType() != null) &&
							(item.lastRelationship().getType().equals(type.getRelationType())));
				}
			   }).
			   traverse(rootNode).relationships().iterator();
	}
	
	/**
	 * Clears Node2Node relations model
	 * 
	 * @param rootNode root node of structure
	 * @param indexKeys used names of indexes 
	 * @param deleteRootNode is it need to delete root node of model
	 */
	public void clearNodeToNodeStructure(Node rootNode, String[] indexKeys, boolean deleteRootNode) { 
		Iterator<Node> proxyNodes = datasetService.getChildTraversal(null).traverse(rootNode).nodes().iterator();
		while (proxyNodes.hasNext()) {
			Node proxy = proxyNodes.next();
			
			Iterator<Relationship> relationIterator = proxy.getRelationships().iterator();
			while (relationIterator.hasNext()) {
				relationIterator.next().delete();
			}
			
			proxy.delete();
		}
		
		for (String singleIndex : indexKeys) {
			getIndexService().removeIndex(singleIndex);
		}
		
		if (deleteRootNode) {
			rootNode.delete();
		}
	}
	
	public String getIndexKeyForRelation(Node rootNode, RelationshipType relationshipType) {
		return new StringBuilder("Id").append(rootNode.getId()).append("@").append(relationshipType.name()).toString();
	}
	
	public Relationship getRelation(String servingNodeId, String dependentNodeId, INodeToNodeRelationType relationType, String proxyIndexKey) {
		//TODO: LN: in Neo4j 1.2 more flexible indexing mechanism - we can index not only nodes, but also relationships
		//use this since we will move to Neo4j 1.2
		
		Node servingProxyNode = getIndexService().getSingleNode(proxyIndexKey, servingNodeId);
		
		if (servingProxyNode == null) {
			return null;
		}
		else {
			Iterator<Relationship> candidates = servingProxyNode.getRelationships(relationType.getRelationType(), Direction.OUTGOING).iterator();
			
			while (candidates.hasNext()) {
				Relationship candidateRelation = candidates.next();
				
				if (datasetService.getNodeName(candidateRelation.getEndNode()).equals(dependentNodeId)) {
					return candidateRelation;
				}
			}
			
			return null;
		}
	}
}
