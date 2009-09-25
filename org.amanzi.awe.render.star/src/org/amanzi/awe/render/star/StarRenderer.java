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

package org.amanzi.awe.render.star;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.core.Pair;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.amanzi.awe.mapgraphic.star.StarMapGraphic;
import org.amanzi.awe.views.reuse.views.ReuseAnalyserView;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.StarDataVault;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * Renderer for star analyser layer
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class StarRenderer extends RendererImpl {

    private IBlackboard blackboard;
    private Map<Node, Point> nodesMap;
    @Override
    public void render(Graphics2D destination, IProgressMonitor monitor) throws RenderException {
        try {
            blackboard = context.getMap().getBlackboard();
            nodesMap = null;//StarDataVault.getInstance().getCopyOfAllMap();
            if (nodesMap == null) {
                return;
            }
            drawAnalyser(destination);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle Exception
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public void render(IProgressMonitor monitor) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    /**
     * perform and draw star analyser
     * 
     * @param context context
     */
    private void drawAnalyser(Graphics2D g) {
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
        Pair<Point, Node> pair = (Pair<Point, Node>)blackboard.get(StarMapGraphic.BLACKBOARD_START_ANALYSER);
        if (pair == null || pair.getRight() == null || !nodesMap.containsKey(pair.getRight())) {
            return;
        }
        Node mainNiode = pair.getRight();
            System.out.println(mainNiode.getId());
        drawMainNode(g, mainNiode);
        String property = getSelectProperty();
        if (property == null) {
            return;
        }
            Point mainPoint = nodesMap.get(mainNiode);
            if (!mainNiode.hasProperty(property)) {
                return;
            }
            Object propertyValue = mainNiode.getProperty(property);
            for (Node node : nodesMap.keySet()) {
                if (node.hasProperty(property) && propertyValue.equals(node.getProperty(property))) {
                    Point nodePoint = nodesMap.get(node);
                    g.setColor(getLineColor(mainNiode, node));
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
    private void drawMainNode(Graphics2D g, Node mainNiode) {
        g.setColor(Color.RED);
        Point point = nodesMap.get(mainNiode);
        g.fillOval(point.x - 4, point.y - 4, 10, 10);
    }

    /**
     * gets closest sector
     * 
     * @param point start point
     * @param nodesMap map of nodes
     * @return closest sector or null
     */
    private static Pair<Point, Node> getSector(Point point, Map<Node, Point> nodesMap) {
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
        return minLen == null || minLen > StarMapGraphic.MAXIMUM_SELECT_LEN ? new Pair<Point, Node>(null, null) : result;
    }

}
