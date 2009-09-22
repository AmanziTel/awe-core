/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.awe.mapgraphic.star;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.core.Pair;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.amanzi.awe.views.reuse.views.ReuseAnalyserView;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * Star analyser mapgraphic
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class StarMapGraphic implements MapGraphic {
    public static final String BLACKBOARD_CENTER_POINT = "org.amanzi.awe.tool.star.StarTool.point";
    public static final String BLACKBOARD_NODE_LIST = "org.amanzi.awe.tool.star.StarTool.nodes";
    public static final String BLACKBOARD_START_ANALYSER = "org.amanzi.awe.tool.star.StarTool.analyser";
    public static final String PROPERTY_KEY = "org.amanzi.awe.tool.star.StarTool.property";
    private static final Integer MAXIMUM_SELECT_LEN = 40 * 40;
    private IBlackboard blackboard;
    private Map<Node, Point> nodesMap;

    @Override
    public void draw(MapGraphicContext context) {
        blackboard = context.getMap().getBlackboard();
        nodesMap = new HashMap<Node, java.awt.Point>();
        Map<Node, java.awt.Point> savedNodes = (Map<Node, java.awt.Point>)blackboard.get(BLACKBOARD_NODE_LIST);
        if (savedNodes == null) {
            return;
        }
        nodesMap.putAll(Collections.synchronizedMap(savedNodes));
        drawSelection(context);
        drawAnalyser(context);
    }

    /**
     * perform and draw star analyser
     * 
     * @param context context
     */
    private void drawAnalyser(MapGraphicContext context) {
        Pair<Point, Node> pair = (Pair<Point, Node>)blackboard.get(BLACKBOARD_START_ANALYSER);
        if (pair == null || pair.getRight() == null || !nodesMap.containsKey(pair.getRight())) {
            return;
        }
        Node mainNiode = pair.getRight();
        drawMainNode(context, mainNiode);
        String property = getSelectProperty();
        if (property == null) {
            return;
        }
        Point mainPoint = nodesMap.get(mainNiode);
        ViewportGraphics g = context.getGraphics();
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            if (!mainNiode.hasProperty(property)) {
                return;
            }
            Object propertyValue = mainNiode.getProperty(property);
            for (Node node : nodesMap.keySet()) {
                if (node.hasProperty(property) && propertyValue.equals(node.getProperty(property))) {
                    Point nodePoint = nodesMap.get(node);
                    g.setColor(getLineColor(mainNiode, node));
                    g.setStroke(ViewportGraphics.LINE_SOLID, 1);
                    g.drawLine(mainPoint.x, mainPoint.y, nodePoint.x, nodePoint.y);
                }
            }

        } finally {
            tx.finish();
        }

    }

    /**
     * get Line color
     * 
     * @param mainNiode main node
     * @param node node
     * @return Line color
     */
    private Color getLineColor(Node mainNiode, Node node) {
        return Color.BLACK;
    }

    /**
     * get property to analyze
     * 
     * @return property name
     */
    private String getSelectProperty() {
        // String result = (String)blackboard.get(PROPERTY_KEY);
        // return result;
        final RunnableWithResult task = new RunnableWithResult() {

            private IViewPart view;

            @Override
            public void run() {
                view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                "org.amanzi.awe.views.reuse.views.ReuseAnalyserView");
            }

            @Override
            public Object getValue() {
                return view;
            }
        };
        ActionUtil.getInstance().runTaskWithResult(task);
        IViewPart view = (IViewPart)task.getValue();
        if (view == null) {
            return null;
        }
        String result = ((ReuseAnalyserView)view).getPropertyName();
        return result == null || result.isEmpty() ? null : result;
    }

    /**
     * Draw selection of main node
     * 
     * @param context context
     * @param mainNiode main node
     */
    private void drawMainNode(MapGraphicContext context, Node mainNiode) {
        ViewportGraphics g = context.getGraphics();
        g.setColor(Color.RED);
        g.setStroke(ViewportGraphics.LINE_SOLID, 2);
        Point point = nodesMap.get(mainNiode);
        g.fillOval(point.x - 4, point.y - 4, 10, 10);
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

        Point sector = getSector(point, nodesMap).getLeft();
        if (sector == null) {
            return;
        }
        drawSelected(context, sector);
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
        Ellipse2D e = new Ellipse2D.Double(sector.x - 4, sector.y - 4, 10, 10);
        g.draw(e);
    }

    /**
     * gets closest sector
     * 
     * @param point start point
     * @param nodesMap map of nodes
     * @return closest sector or null
     */
    public static Pair<Point, Node> getSector(Point point, Map<Node, Point> nodesMap) {
        Integer minLen = null;
        Pair<Point, Node> result = null;
        final Set<Node> keySet = nodesMap.keySet();
        for (Node node : keySet) {
            Point sectorCenter = nodesMap.get(node);
            int len = (point.x - sectorCenter.x) * (point.x - sectorCenter.x) + (point.y - sectorCenter.y)
                    * (point.y - sectorCenter.y);
            if (minLen == null || minLen > len) {
                result = new Pair<Point, Node>(sectorCenter, node);
                minLen = len;
            }
        }
        return minLen == null || minLen > MAXIMUM_SELECT_LEN ? new Pair<Point, Node>(null, null) : result;
    }

}
