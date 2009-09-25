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

package org.amanzi.awe.mapgraphic.star;

import java.awt.Color;
import java.awt.Point;
//import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.core.Pair;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.amanzi.neo.core.utils.StarDataVault;
import org.neo4j.api.core.Node;

/**
 * Star analyser mapgraphic. Draws lines on the map from a selected point to all related points.
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class StarMapGraphic implements MapGraphic {
    public static final String BLACKBOARD_CENTER_POINT = "org.amanzi.awe.tool.star.StarTool.point";
    public static final String BLACKBOARD_START_ANALYSER = "org.amanzi.awe.tool.star.StarTool.analyser";
    public static final String PROPERTY_KEY = "org.amanzi.awe.tool.star.StarTool.property";
    public static final Integer MAXIMUM_SELECT_LEN = 40 * 40;
    private IBlackboard blackboard;
    private Map<Node, Point> nodesMap;

    @Override
    public void draw(MapGraphicContext context) {
        // long t1 = System.currentTimeMillis();
        blackboard = context.getMap().getBlackboard();

//        nodesMap = StarDataVault.getInstance().getCopyOfAllMap();
        // System.out.println("StarMapGraphic getCopyOfAllMap\t" + (System.currentTimeMillis() -
        // t1));
//        if (nodesMap == null) {
//            return;
//        }
        // t1 = System.currentTimeMillis();
        drawSelection(context);
        // System.out.println("StarMapGraphic drawSelection\t" + (System.currentTimeMillis() - t1));
    }

    /**
     * draw selection
     * 
     * @param context context
     */
    private void drawSelection(MapGraphicContext context) {
        Point point = (Point)blackboard.get(BLACKBOARD_CENTER_POINT);
        if (point == null) {
            return;
        }
        drawSelected(context, point);

//        Point sector = getSector(point, nodesMap).getLeft();
//        if (sector == null) {
//            return;
//        }
//        drawSelected(context, sector);
    }

    /**
     * paint selected sector
     * 
     * @param context context
     * @param sector sector
     */
    private void drawSelected(MapGraphicContext context, Point sector) {
        ViewportGraphics g = context.getGraphics();
        g.setColor(Color.RED);
        g.setStroke(ViewportGraphics.LINE_SOLID, 2);
        //Ellipse2D e = new Ellipse2D.Double(sector.x - 4, sector.y - 4, 10, 10);
        //g.draw(e);
        g.drawOval(sector.x - 4, sector.y - 4, 9, 9);
    }

    /**
     * gets closest sector
     * 
     * @param point start point
     * @param nodesMap map of nodes
     * @return closest sector or null
     */
    public static Pair<Point, Node> getSector(Point point, Map<Node, Point> nodesMap) {
        int minLen = Integer.MAX_VALUE;
        Node result = null;
        final Set<Node> keySet = nodesMap.keySet();
        for (Node node : keySet) {
            Point sectorCenter = nodesMap.get(node);
            int len = (point.x - sectorCenter.x) * (point.x - sectorCenter.x) + (point.y - sectorCenter.y)
                    * (point.y - sectorCenter.y);
            if (len < MAXIMUM_SELECT_LEN && len < minLen) {
                result = node;
                minLen = len;
            }
        }
        return result == null ? new Pair<Point, Node>(null, null) : new Pair<Point, Node>(nodesMap.get(result),result);
    }

}
