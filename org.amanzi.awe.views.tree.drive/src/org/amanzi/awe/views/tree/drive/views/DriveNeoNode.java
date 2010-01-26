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
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * Proxy class that provides access for drive Node, it's children and properties
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DriveNeoNode extends NeoNode {
    /** int TRUNCATE_NODE field */
    protected static final int TRUNCATE_NODE = 10;

    /**
     * Constructor
     * 
     * @param node node
     */
    public DriveNeoNode(Node node) {
        super(node);
    }

    @Override
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> children = new ArrayList<NeoNode>();
        Traverser traverse;
        if (isFileNode() || NeoUtils.isVirtualDataset(node)) {
            traverse = node.traverse(Order.BREADTH_FIRST, new StopEvaluator() {

                @Override
                public boolean isStopNode(TraversalPosition currentPos) {
                    return !currentPos.isStartNode() && NeoUtils.isFileNode(currentPos.currentNode());
                }
            }, ReturnableEvaluator.ALL_BUT_START_NODE,
                    GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
        } else if (isDatasetNode() || isDirectoryNode()) {        	
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                    GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
        } else {
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                    NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
        }
        int i = 0;
        for (Node node : traverse) {
            // todo now aggregation works only for nodes with type=m
            if (++i <= TRUNCATE_NODE || !isFileNode()) {
                children.add(new DriveNeoNode(node));
            } else {
                children.add(new AggregatesNode(node));
                break;
            }
        }        
        return children.toArray(NO_NODES);
    }

    /**
     * Return true if current node has type 'DATASET'
     * 
     * @return
     */
    private boolean isDatasetNode() {
        return node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(INeoConstants.DATASET_TYPE_NAME);

    }

    @Override
    public boolean hasChildren() {
        return node.hasRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)
                || ((isFileNode() || isDatasetNode() || isDirectoryNode()) && node.hasRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING));
    }

    /**
     * Return true if current node has type 'File'
     * 
     * @return
     */
    private boolean isFileNode() {
        return node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(INeoConstants.FILE_TYPE_NAME);
    }
    
    private boolean isDirectoryNode() {
    	return node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(INeoConstants.DIRECTORY_TYPE_NAME);
    }
}
