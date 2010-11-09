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
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.enums.ProbeCallRelationshipType;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.ui.enums.ColoredFlags;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

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
    protected static final int TRUNCATE_NODE =20 ;


    /**
     * Constructor
     * 
     * @param node node
     */
    public DriveNeoNode(Node node, int number) {
        super(node,number);
    }

    @Override
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> children = new ArrayList<NeoNode>();
        Traverser traverse;
        int i = 0;
        int nextNum = number+1;
        if(NeoUtils.isDatasetNode(node)){
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, 
                                     NetworkRelationshipTypes.AGGREGATION, Direction.OUTGOING);
            for (Node node : traverse) {
                children.add(new DistributeNeoNode(node,nextNum++));
            }
        }
        if (NeoUtils.isCallNode(node)) {
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, 
                                     ProbeCallRelationshipType.CALL_M, Direction.OUTGOING);
        }
        else {
            traverse = NeoUtils.getChildTraverser(node);
        }        
        for (Node node : traverse) {
            if (++i <= TRUNCATE_NODE) {
                children.add(new DriveNeoNode(node,nextNum++));
            } else {
                children.add(new AggregatesNode(node, nextNum++));
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
        return node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NodeTypes.DATASET.getId());

    }

    @Override
    public boolean hasChildren() {
        return node.hasRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING) || 
               ((isFileNode() || isDatasetNode() || isDirectoryNode()) && 
               node.hasRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)) ||  
               node.hasRelationship(ProbeCallRelationshipType.CALL_M, Direction.OUTGOING);
    }
    
    @Override
    public String getImageKey() {
        if (NeoUtils.isCallNode(node)) {
            ColoredFlags flag = ColoredFlags.getFlagById((String)node.getProperty(INeoConstants.PROPERTY_FLAGGED_NAME, ColoredFlags.NONE.getId()));
            if(!flag.equals(ColoredFlags.NONE)){
                return getType()+"_"+flag.getId();
            }
        }
        return super.getImageKey();
    }

    /**
     * Return true if current node has type 'File'
     * 
     * @return
     */
    private boolean isFileNode() {
        return node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NodeTypes.FILE.getId());
    }
    
    private boolean isDirectoryNode() {
    	return node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NodeTypes.DIRECTORY.getId());
    }
}
