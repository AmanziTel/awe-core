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
import java.util.Iterator;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class CallAnalyzisNeoNode extends DriveNeoNode {
    
    private String type;
    
    private Node statisticsNode;

    /**
     * @param node
     */
    public CallAnalyzisNeoNode(Node node) {
        super(node);
        
        type = NeoUtils.getNodeType(node);
        
        if (type.equals(INeoConstants.CALL_ANALYZIS_ROOT)) {
            name = "Call Analysis: " + node.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
        }        
        if (type.equals(INeoConstants.S_CELL)) {
            name = node.getProperty(INeoConstants.PROPERTY_NAME_NAME) + ": " + node.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
        }
    }
    
    protected CallAnalyzisNeoNode(Node probeNode, Node statisticsNode) {
        this(probeNode);
        
        this.statisticsNode = statisticsNode; 
    }
    
    @Override
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> children = new ArrayList<NeoNode>();
        
        Iterator<Node> iterator;
        if (type.equals(INeoConstants.CALL_ANALYZIS_ROOT)) {
            iterator = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, 
                                     GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
        }
        else if (type.equals(INeoConstants.CALL_ANALYZIS)) {
            Node root = node.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
            Node dataset = root.getSingleRelationship(ProbeCallRelationshipType.CALL_ANALYZIS, Direction.INCOMING).getStartNode();
            iterator = NeoUtils.getAllProbesOfDataset(dataset).iterator();
        }
        else if (type.equals(INeoConstants.PROBE_TYPE_NAME)) {
            iterator = NeoUtils.getChildTraverser(statisticsNode, new ReturnableEvaluator() {
                
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node probe = currentPos.currentNode().getSingleRelationship(GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING).getEndNode();
                    
                    return probe.equals(node);
                }
            }).iterator();
        }
        else if (type.equals(INeoConstants.S_CELL)) {
            iterator = node.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return NeoUtils.isCallNode(currentPos.currentNode());
                }
            }, GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING).iterator();
        }
        else {
            iterator = NeoUtils.getChildTraverser(node).iterator();
        }
        int i = 0;
        while (iterator.hasNext()) {
            Node child = iterator.next();
            // TODO refactoring
            children.add(new CallAnalyzisNeoNode(child, node));
            // if (++i <= TRUNCATE_NODE) {
            // } else {
            // children.add(new AggregatesNode(child));
            // break;
            // }
        }        
        return children.toArray(NO_NODES);
    }
    
    @Override
    public boolean hasChildren() {
        return node.hasRelationship(Direction.OUTGOING);
    }

    /**
     * @return
     */
    public NeoNode getParent() {
        if (type.equals(INeoConstants.PROBE_TYPE_NAME)) {
            return new CallAnalyzisNeoNode(statisticsNode, statisticsNode);
        } else if (type.equals(INeoConstants.S_ROW)) {
            return new CallAnalyzisNeoNode(node.getSingleRelationship(GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING).getOtherNode(node), statisticsNode);
        } else {
            return new CallAnalyzisNeoNode(NeoUtils.getParent(null, node), statisticsNode);
        }
    }
}
