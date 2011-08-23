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
import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.ui.enums.ColoredFlags;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsNeoNode extends DriveNeoNode {
    public static final Color ALERT_COLOR=new Color(Display.getCurrent(),255,0,0);
    private String type;

    public StatisticsNeoNode(Node node, int number) {
        super(node, getNodeName(node), number);
        type = NeoUtils.getNodeType(node);
    }

    private static String getNodeName(Node node) {
        String nodeType = NeoUtils.getNodeType(node);
        if (nodeType.equals(NodeTypes.S_GROUP.getId())) {
            return getGroupName(node) + ", " + nodeType;
        }else if (nodeType.equals(NodeTypes.STATISTICS_ROOT.getId())){
            return node.getProperty("template").toString();
        }
        return NeoUtils.getFormatedNodeName(node, "");
    }

    public static String getGroupName(Node node) {
        if (node.hasRelationship(GeoNeoRelationshipTypes.KEY, Direction.OUTGOING)) {
            return node.getSingleRelationship(GeoNeoRelationshipTypes.KEY, Direction.OUTGOING).getOtherNode(node).getProperty(
                    INeoConstants.PROPERTY_NAME_NAME).toString();
        } else if (node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)) {
            return node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        }
        return "unknown";
    }

    @Override
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> children = new ArrayList<NeoNode>();
        Traverser traverse;
        if (type.equals(NodeTypes.S_CELL.getId())) {
            traverse = getSourceNodes(node);
        } else if (type.equals(NodeTypes.STATISTICS.getId()) || type.equals(NodeTypes.S_GROUP.getId())
                || type.equals(NodeTypes.S_ROW.getId())) {
            traverse = NeoUtils.getChildTraverser(node);
        } else {
            traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                    GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
        }
        int nextNum = number + 1;
        for (Node node : traverse) {
            children.add(new StatisticsNeoNode(node, nextNum++));
        }
        return children.toArray(NO_NODES);
    }

    /**
     * @return
     */
    private Traverser getSourceNodes(Node node) {
        Traverser traverse;
        traverse = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING);
        return traverse;
    }

    @Override
    public boolean hasChildren() {
        if (type.equals(NodeTypes.S_CELL.getId())) {
            return node.hasRelationship(GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING);
        }
        return node.hasRelationship(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
    }

    @Override
    public String getImageKey() {
        if (type.equals(NodeTypes.S_CELL.getId()) || type.equals(NodeTypes.S_ROW.getId()) || type.equals(NodeTypes.S_GROUP.getId())) {
            if (node.hasProperty(INeoConstants.PROPERTY_FLAGGED_NAME)) {
                System.out.println(type + "_" + ColoredFlags.RED.getId());
                return type + "_" + ColoredFlags.RED.getId();
            }
        }
        return super.getImageKey();
    }

    @Override
    public Set<Node> getNodesForMap() {
        Set<Node> nodes = new HashSet<Node>();
        addNodes(nodes, node);
        if (type.equals(NodeTypes.S_CELL.getId()) || type.equals(NodeTypes.S_ROW.getId()) || type.equals(NodeTypes.S_GROUP.getId())) {
            addNodes(nodes, node);
            return nodes;
        }
        return super.getNodesForMap();
    }

    /**
     * @param nodes
     */
    private void addNodes(Set<Node> nodes, Node parent) {
        if (NeoUtils.getNodeType(parent).equals(NodeTypes.S_CELL)) {
            nodes.addAll(getSourceNodes(parent).getAllNodes());
        } else {
            Traverser children = NeoUtils.getChildTraverser(parent);
            for (Node child : children) {
                addNodes(nodes, child);
            }
        }
    }

    public static Traverser getChildTraverser(Node rootNode) {
        return rootNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);

    }

    public NeoNode getParent() {
        if (type.equals(NodeTypes.STATISTICS_ROOT.getId())) {
            Node parent =node.getRelationships(GeoNeoRelationshipTypes.ANALYSIS,Direction.INCOMING).iterator().next().getStartNode();
            return new DriveNeoNode(parent, number + 1);
        }
        return new StatisticsNeoNode(NeoUtils.getParent(null, node), number + 1);
    }

    @Override
    public Color getTextColor() {
        if ((Boolean)node.getProperty(INeoConstants.PROPERTY_FLAGGED_NAME,false)){
            return ColoredFlags.RED.getColor(Display.getCurrent());
        }
        return super.getTextColor();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StatisticsNeoNode) {
            return getNode().equals(((StatisticsNeoNode)obj).getNode());
        } else {
            if (obj instanceof Node) {
                return getNode().equals((Node)obj);
            }
        }
        return super.equals(obj);
    }

}
