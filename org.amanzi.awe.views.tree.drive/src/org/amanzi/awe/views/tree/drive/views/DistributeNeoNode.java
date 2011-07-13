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

package org.amanzi.awe.views.tree.drive.views;

import java.util.ArrayList;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Neo node for distribution analysis.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class DistributeNeoNode extends DriveNeoNode{

    /**
     * Constructor.
     * @param node
     * @param number
     */
    public DistributeNeoNode(Node node, int number) {
        super(node, number);
    }
    
    @Override
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> children = new ArrayList<NeoNode>();
        Traverser traverse;
        if (NeoUtils.isCountNode(node)) {
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, 
                    NetworkRelationshipTypes.AGGREGATE, Direction.OUTGOING);
        
        }else if (NeoUtils.isDrivePointNode(node)) {
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, 
                    GeoNeoRelationshipTypes.LOCATION, Direction.INCOMING);
        }else{
            traverse = NeoUtils.getChildTraverser(node);
        }        
        int nextNum = number+1;
        for (Node node : traverse) {
            children.add(new DistributeNeoNode(node,nextNum++));
        }        
        return children.toArray(NO_NODES);
    }
    
    @Override
    public boolean hasChildren() {
        if (NeoUtils.isCountNode(node)) {
            return node.hasRelationship(NetworkRelationshipTypes.AGGREGATE, Direction.OUTGOING);
        }
        
        if (NeoUtils.isDrivePointNode(node)) {
            return node.hasRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.INCOMING);
        }
        return node.hasRelationship(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING);
    }
    
    public NeoNode getParent(){
        int nextNum = number+1;
        Relationship parentLink;
        if(NeoUtils.isAggregationNode(node)){
            parentLink = node.getSingleRelationship(NetworkRelationshipTypes.AGGREGATION, Direction.INCOMING);
            return new DriveNeoNode(parentLink.getOtherNode(node), nextNum);
        }
        parentLink = node.getSingleRelationship(NetworkRelationshipTypes.AGGREGATE, Direction.INCOMING);
        if(parentLink!=null){
            return new DistributeNeoNode(parentLink.getOtherNode(node), nextNum);
        }
        parentLink = node.getSingleRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING);
        if(parentLink!=null){
            return new DistributeNeoNode(parentLink.getOtherNode(node), nextNum);
        }
        
        return new DistributeNeoNode(NeoUtils.getParent(null, node), nextNum);
    }

}
