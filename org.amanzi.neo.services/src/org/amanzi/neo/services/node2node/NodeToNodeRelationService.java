/**
 * 
 */
package org.amanzi.neo.services.node2node;

import java.util.Iterator;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
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
		INTERFERENCE_MATRIX;
	}
	
	private DatasetService datasetService;
	
	public NodeToNodeRelationService() {
		datasetService = new DatasetService();
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
}
