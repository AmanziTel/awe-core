package org.amanzi.neo.core.utils;

import java.util.Collection;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.nodes.DeletableRelationshipType;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeDeletableTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.RelationDeletableTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;

/**
 * Manager for node deleting with its links and children.
 * @author Shcharbatsevich_A
 */
public class NodeDeletingManager {
    
    private GraphDatabaseService neo;
    
    /**
     * Constructor.
     * @param service NeoService
     */
    public NodeDeletingManager(GraphDatabaseService service) {
        neo = service;
    }

    /**
     * Delete common node
     *
     * @param aNode Node for delete
     * @throws IllegalAccessException if node or one of linked to it can not be delete.
     */
	public void deleteNode(Node aNode)throws IllegalAccessException{
	    Transaction transaction = neo.beginTx();
		try {
            deleteNodeRequrcive(aNode, true, null);
            transaction.success();
        } finally{
            transaction.finish();
        }
		
	}
	
	/**
	 * Delete node
	 *
	 * @param aNode Node for delete
	 * @param isFirst true if we came first time.
	 * @param type RelationshipType (type of deleting line)
	 * @throws IllegalAccessException if node or one of linked to it can not be delete.
	 */
	private void deleteNodeRequrcive(Node aNode, boolean isFirst,RelationshipType type)throws IllegalAccessException{
	    if(isFirst&&isNodeFixed(aNode)){
	        throw new IllegalAccessException("Node can not be delete in any case!");
	    }
	    for(Relationship link : aNode.getRelationships()){
	        Node linked = link.getOtherNode(aNode);
	        NodeDeletableTypes deletable = getNodeDelState(linked, link);
	        switch (deletable) {
            case FIXED:
                throw new IllegalAccessException("Node can not be delete in any case!");
            case UNLINK:
                link.delete();
                break;
            case RELINK:
                doRelink(aNode, link);
                link.delete();
                break;                
            case DELETE:
                link.delete();
                deleteNodeRequrcive(linked, false, null);
                break;
            case DELETE_LINE:
                RelationshipType currType = link.getType();
                link.delete();
                if(type==null || !currType.equals(type)){
                    deleteLine(aNode, link);
                }
                break;   
            default:
                throw new IllegalArgumentException("Unknown node deleting state <"+deletable+">!");
            }
	    }
	}
	
	/**
	 * Create a new link with next node.
	 *
	 * @param aNode Node for delete
	 * @param link Relationship for delete
	 */
	private void doRelink(Node aNode, Relationship link){
        boolean isLinkOut = link.getStartNode().equals(aNode);
        Relationship otherLink = null;
        if(hasType(aNode)){
            otherLink = NodeTypes.getSecondLinkForRelink(aNode, link, neo);
        }
        else{
            otherLink = getOtherLink(aNode, link, isLinkOut);
        }
        if(otherLink == null){
            return;
        }
        Node node1;
        Node node2;
        RelationshipType linkType;
        if(isLinkOut){
            node1= otherLink.getStartNode();
            node2= link.getEndNode();
            linkType = otherLink.getType();
        }
        else{
            node1= link.getStartNode();
            node2= otherLink.getEndNode();
            linkType = link.getType();
        }
        boolean hasLink = false;
        for(Relationship currLink : node1.getRelationships(linkType,Direction.OUTGOING)){
            if(currLink.getEndNode().equals(node2)){
                hasLink = true;
                break;
            }
        }
        if (!hasLink) {
            node1.createRelationshipTo(node2, linkType);
        }
    }
    
	/**
	 * Get next node for relink.
	 *
	 * @param aNode Node for delete
	 * @param link Relationship that need pair.
	 * @param isLinkOut is Relationship outgoing
	 * @return Relationship
	 */
    private Relationship getOtherLink(Node aNode, Relationship link, boolean isLinkOut) {
        Direction direction = isLinkOut?Direction.INCOMING:Direction.OUTGOING;
        Iterable<Relationship> allLinks = aNode.getRelationships(direction);     
        for(Relationship currLink : allLinks){
            DeletableRelationshipType linkType = NeoUtils.getRelationType(currLink);
            RelationDeletableTypes deletable = linkType.getDeletableType(direction);
            if(deletable.equals(RelationDeletableTypes.RELINK)){
                return currLink;
            }
        } 
        return null;
    }
    
    /**
     * Delete line of nodes whit similar link types.
     *
     * @param aNode Node for delete
     * @param link Relationship for delete
     * @throws IllegalAccessException if node or one of linked to it can not be delete.
     */
    private void deleteLine(Node aNode, Relationship link)throws IllegalAccessException{
        RelationshipType type = link.getType();
        Direction direction = link.getStartNode().equals(aNode)?Direction.OUTGOING:Direction.INCOMING;
        Traverser traverser = aNode.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, 
                type, direction);
        Collection<Node> allNodes = traverser.getAllNodes();
        for(Node founded : allNodes){
            deleteNodeRequrcive(founded, false, type);
        }
    }
	
    /**
     * Returns node deleting state.
     *
     * @param aNode Node for delete
     * @param cameFrom Relationship
     * @return NodeDeletableTypes
     */
	private NodeDeletableTypes getNodeDelState(Node aNode, Relationship cameFrom){
	    if(hasType(aNode)){
            return NodeTypes.getNodeDeletableType(aNode, cameFrom, neo);
	    }
	    if(aNode.equals(neo.getReferenceNode())){
	        return NodeDeletableTypes.UNLINK;
	    }
	    RelationshipType linkType = cameFrom.getType();
	    if(linkType.equals(GeoNeoRelationshipTypes.PROPERTIES)){
	        return NodeDeletableTypes.DELETE; 
	    }
	    throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
	}
	
	/**
	 * Is node can not be delete?
	 *
	 * @param aNode Node for delete
	 * @return boolean
	 */
	private boolean isNodeFixed(Node aNode){
	    if(aNode.equals(neo.getReferenceNode())){
            return true;
        }
	    if(hasType(aNode)&&NodeTypes.isNodeFixed(aNode, neo)){
            return true;
        }
	    for(Relationship link : aNode.getRelationships()){
	        Direction direction = link.getStartNode().equals(aNode)?Direction.OUTGOING:Direction.INCOMING;
	        DeletableRelationshipType linkType = NeoUtils.getRelationType(link);
	        RelationDeletableTypes deletable = linkType.getDeletableType(direction);
	        if(deletable.equals(RelationDeletableTypes.FIXED)){
	            return true;
	        }
	    }
        return false;
    }

	/**
	 * Is node has property 'type'
	 *
	 * @param aNode Node
	 * @return boolean
	 */
    private boolean hasType(Node aNode) {
        return aNode.hasProperty(INeoConstants.PROPERTY_TYPE_NAME);
    }
}
