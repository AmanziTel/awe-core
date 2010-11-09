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

package org.amanzi.awe.views.reuse.views;

import java.awt.Color;
import java.util.Collection;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * Adds colors to distribution statistics
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class DefaultColorer {
    /**
     * Adds colors from green to red
     * 
     * @param aggregationNode the aggregation node
     * @param service the database service
     */
    public static void addColors(Node aggregationNode, GraphDatabaseService service) {
        RGB green = new RGB(0, 255, 0);
        RGB red = new RGB(255, 0, 0);
        addColors(aggregationNode, green, red, service);
    }

    /**
     * Adds colors to single aggregation nodes
     * 
     * @param aggregationNode the root aggregation node
     * @param startColor the start color
     * @param endColor the end color
     * @param service the database service
     */
    public static void addColors(Node aggregationNode, RGB startColor, RGB endColor, GraphDatabaseService service) {
        Transaction tx = service.beginTx();
        try {
            String type = (String)aggregationNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME);
            if (!NodeTypes.AGGREGATION.getId().equalsIgnoreCase(type)) {
                return;
            }
            saveColor(aggregationNode, INeoConstants.COLOR_LEFT, startColor);
            saveColor(aggregationNode, INeoConstants.COLOR_RIGHT, endColor);
            final Traverser traverser = aggregationNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING,
                    GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
            float ratio = 0;
            final Collection<Node> nodes = traverser.getAllNodes();
            long count = nodes.size();
            float perc = count <= 0 ? 1 : 1f / count;
            for (Node node : nodes) {
                RGB colrRgb = ReuseAnalyserView.blend(startColor, endColor, ratio);
                Color color = new Color(colrRgb.red, colrRgb.green, colrRgb.blue);
                node.setProperty(INeoConstants.AGGREGATION_COLOR, color.getRGB());
                ratio += perc;
            }
            tx.success();
        } finally {
            tx.finish();
        }

    }

    /**
     * Save color in database
     * 
     * @param node node
     * @param property property name
     * @param rgb color
     */
    private static void saveColor(Node node, String property, RGB rgb) {
        if (node == null || property == null || rgb == null) {
            return;
        }
        int[] array = new int[3];
        array[0] = rgb.red;
        array[1] = rgb.green;
        array[2] = rgb.blue;
        node.setProperty(property, array);
    }
}
