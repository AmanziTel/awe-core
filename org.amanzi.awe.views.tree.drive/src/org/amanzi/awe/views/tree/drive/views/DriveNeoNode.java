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
import java.util.Collections;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
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
    private static final int TRUNCATE_NODE = 10;

    /**
     * Constructor
     * 
     * @param node node
     */
    public DriveNeoNode(Node node) {
        super(node);
        if (getType().equals(INeoConstants.MP_TYPE_NAME)) {
            name = node.getProperty(INeoConstants.PROPERTY_TIME_NAME, "").toString();
        } else if (getType().equals(INeoConstants.HEADER_MS)) {
            name = node.getProperty(INeoConstants.PROPERTY_CODE_NAME, "").toString();
        }
    }

    @Override
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> children = new ArrayList<NeoNode>();
        Traverser traverse;
        if (isFileNode()) {
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE,
                    GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
        } else if (isDatasetNode() || isDirectoryNode()) {
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                    GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
        } else {
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                    NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
        }
        int i = 0;
        ArrayList<DriveNeoNode> subnodes = new ArrayList<DriveNeoNode>();
        for (Node node : traverse) {
            if (++i <= TRUNCATE_NODE) {
                children.add(new DriveNeoNode(node));
            } else {
                subnodes.add(new DriveNeoNode(node));
            }
        }
        // there are no necessary to create aggregated node for one subnode
        if (subnodes.size() > 1) {
            Collections.sort(children, new NeoNodeComparator());
            children.add(new AggregatesNode(subnodes));
        } else {
            children.addAll(subnodes);
            Collections.sort(children, new NeoNodeComparator());
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
