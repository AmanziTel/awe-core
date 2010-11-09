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
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Aggregated node
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class AggregatesNode extends DriveNeoNode {

    // private final ArrayList<DriveNeoNode> subnodes;
    
    /**
     * Constructor
     * 
     * @param subnodes - list of subnodes
     */
    public AggregatesNode(Node firstNode, int nodeNumber) {
		//for icons sets the first node
        super(firstNode,nodeNumber);
        // this.subnodes = subnodes;
        // Collections.sort(this.subnodes, new NeoNodeComparator());
        // TODO compute size if necessary
        name = name + " aggregation";
        // name="and "+subnodes.size()+" more";
	}
	@Override
	public NeoNode[] getChildren() {
        ArrayList<NeoNode> children = new ArrayList<NeoNode>();
        int nextNum = number+1;
        if (number<MAX_CHILDREN_COUNT) {
            children.add(new DriveNeoNode(getNode(),nextNum++));
            Traverser traverse;
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE,
                    GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
            int i = 0;
            for (Node node : traverse) {
                if (++i <= TRUNCATE_NODE) {
                    children.add(new DriveNeoNode(node,nextNum++));
                } else {
                    children.add(new AggregatesNode(node, nextNum++));
                    break;
                }
            }
        }
        return children.toArray(NO_NODES);
	}
	@Override
	public boolean hasChildren() {
        return getNode().hasRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
	}
}
