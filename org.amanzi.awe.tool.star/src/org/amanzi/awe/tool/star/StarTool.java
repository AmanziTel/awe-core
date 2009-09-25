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
package org.amanzi.awe.tool.star;

import java.awt.Color;
import java.awt.Point;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.core.Pair;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.internal.commands.draw.TranslateCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import org.amanzi.neo.core.utils.StarDataVault;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Custom uDIG Map Tool for performing a 'star analysis'. This means it interacts with objects on
 * the map and a custom star mapgraphic is used to draw lines representing relations to the objects
 * on the map. These lines look like a star, hence the name.
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class StarTool extends AbstractModalTool {
    public static final String BLACKBOARD_START_ANALYSER = "org.amanzi.awe.tool.star.StarTool.analyser";
    public static final String BLACKBOARD_CENTER_POINT = "org.amanzi.awe.tool.star.StarTool.point";
    private static final int MAXIMUM_SELECT_LEN = 10000;
    private boolean dragging=false;
    private Point start=null;

    private TranslateCommand command;
    private Map<Long, java.awt.Point> nodesMap;
//    private ILayer starMapGraphicLayer; // cache so that mousemove event does not do so much work
    private DrawShapeCommand drawSelectedSectorCommand;
    private DrawShapeCommand drawSelectionLineCommand;
    private Pair<Point, Long> selected;

    /**
     * Creates an new instance of Pan
     */
    public StarTool() {
        super(MOUSE | MOTION);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged(MapMouseEvent e) {
        if (dragging) {
            command.setTranslation(e.x- start.x, e.y - start.y);
            context.getViewportPane().repaint();
        }
    }

    @Override
    public void setActive(boolean active) {
        if(super.isActive() != active) {
            super.setActive(active);
            // add layer on map if necessary
            if (active) {
//                setsGisLayeronMap();
//                setLayerOnMap(StarMapGraphic.class);
            }
        }
    }

    /**
     *
     */
//    private void setsGisLayeronMap() {
//        IProgressMonitor monitor = new NullProgressMonitor();
//        if (gisNode == null) {
//            synchronized (this) {
//                if (gisNode == null) {
//                    gisNode = NeoUtils.findOrCreateStarGisNode();
//                }
//            }
//        }
//        IMap map = getContext().getMap();
//        List<ILayer> layers = map.getMapLayers();
//        try {
//            for (ILayer layer : layers) {
//                if (layer.getGeoResource().canResolve(Node.class)) {
//                    Node node = layer.getGeoResource().resolve(Node.class, monitor);
//                    if (node.equals(gisNode)) {
//                        return;
//                    }
//
//                }
//            }
//
//            String databaseLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();
//            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
//            List<IResolve> serv = catalog.find(new URL("file://" + databaseLocation), monitor);
//
//            List<IGeoResource> list = new ArrayList<IGeoResource>();
//            for (IResolve iResolve : serv) {
//                List< ? extends IGeoResource> resources = ((IService)iResolve).resources(null);
//                for (IGeoResource singleResource : resources) {
//                    if (singleResource.canResolve(Node.class) && singleResource.resolve(Node.class, monitor).equals(gisNode)) {
//                        list.add(singleResource);
//                        ApplicationGIS.addLayersToMap(map, list, map.getMapLayers().size());
//                        return;
//                    }
//                }
//            }
//            ApplicationGIS.addLayersToMap(map, list, map.getMapLayers().size());
//        } catch (IOException e) {
//            // TODO Handle IOException
//            throw (RuntimeException)new RuntimeException().initCause(e);
//        }
//    }

    /**
     *
     */
//    protected void setLayerOnMap(Class< ? extends MapGraphic> resourceClass) {
//        IMap map = getContext().getMap();
//        List<ILayer> layers = map.getMapLayers();
//        for (ILayer layer : layers) {
//            if (layer.getGeoResource().canResolve(resourceClass)) {
//                return;
//            }
//        }
//        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
//        List<IResolve> serv = catalog.find(MapGraphicService.SERVICE_URL, null);
//        try {
//            for (IResolve iResolve : serv) {
//                List<MapGraphicResource> resources;
//                resources = ((MapGraphicService)iResolve).resources(null);
//                for (MapGraphicResource mapGraphicResource : resources) {
//                    if (mapGraphicResource.canResolve(resourceClass)) {
//                        List<IGeoResource> list = new ArrayList<IGeoResource>();
//                        list.add(mapGraphicResource);
//                        ApplicationGIS.addLayersToMap(map, list, map.getMapLayers().size());
//                        return;
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw (RuntimeException)new RuntimeException().initCause(e);
//        }
//    }
    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed(MapMouseEvent e) {
    	
        if (validModifierButtonCombo(e)) {
        	((ViewportPane)context.getMapDisplay()).enableDrawCommands(false);
            dragging = true;
            start = e.getPoint();
            command=context.getDrawFactory().createTranslateCommand(0,0);
            //TODO: Does this line actually do anything?
            context.sendASyncCommand(command);
        }
    }

    /**
     * Returns true if the combination of buttons and modifiers are legal to execute the pan.
     * <p>
     * This version returns true if button 1 is down and no modifiers
     * </p>
     * @param e
     * @return
     */
	protected boolean validModifierButtonCombo(MapMouseEvent e) {
		return e.buttons== MapMouseEvent.BUTTON1
                && !(e.modifiersDown());
	}

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased(MapMouseEvent e) {
        boolean activateStar = true;
        if (dragging) {
            activateStar = false;
        	((ViewportPane)context.getMapDisplay()).enableDrawCommands(true);
            Point end=e.getPoint();
            int dx = start.x-end.x;
            int dy = start.y-end.y;
            if(dx == 0 && dy == 0) {
                activateStar = true;
            } else {
                nodesMap = null;  // after panning the nodes might have changed, so force reload on next mouse released
            }
            //TODO: Perhaps only run this if dx||dy non-zero ?
            NavCommand finalPan = context.getNavigationFactory().createPanCommandUsingScreenCoords(dx, dy);
            context.sendASyncCommand(new PanAndInvalidate(finalPan, command));
            dragging = false;

        }
        if (activateStar) {
            final IMap map = getContext().getMap();
            IBlackboard blackboard = map.getBlackboard();
            blackboard.put(BLACKBOARD_START_ANALYSER, selected);
            if (selected != null) {
                getContext().getSelectedLayer().refresh(null);
                // updateLayerStarLayer();
            }
        }
        // clear layer cache in case user deletes or adds star map graphic
        //starMapGraphicLayer = null;
    }

    /**
     * gets closest sector
     * 
     * @param point start point
     * @param nodesMap map of nodes
     * @return closest sector or null
     */
    private static Pair<Point, Long> getSector(Point point, Map<Long, Point> nodesMap) {
        int minLen = Integer.MAX_VALUE;
        Long result = null;
        final Set<Long> keySet = nodesMap.keySet();
        for (Long node : keySet) {
            Point sectorCenter = nodesMap.get(node);
            int len = (point.x - sectorCenter.x) * (point.x - sectorCenter.x) + (point.y - sectorCenter.y)
                    * (point.y - sectorCenter.y);
            if (len < MAXIMUM_SELECT_LEN && len < minLen) {
                result = node;
                minLen = len;
            }
        }
        return result == null ? null : new Pair<Point, Long>(nodesMap.get(result),result);
    }
    
    private Map<Long,java.awt.Point> getNodesMap() {
        if(true || nodesMap==null) {
            //nodesMap = (Map<Node, java.awt.Point>)blackboard.get(StarMapGraphic.BLACKBOARD_NODE_LIST);
            nodesMap = StarDataVault.getInstance().getCopyOfMap(getContext().getSelectedLayer().getGeoResource().getIdentifier());
        }
        return nodesMap;
    }
    
    /**
     *
     */
//    private void updateLayerStarLayer() {
//        IMap map = getContext().getMap();
//        List<ILayer> layers = map.getMapLayers();
//        try {
//            for (ILayer layer : layers) {
//                if (layer.getGeoResource().canResolve(Node.class)
//                        && gisNode.equals(layer.getGeoResource().resolve(Node.class, null))) {
//                    layer.refresh(null);
//                    return;
//                }
//            }
//        } catch (IOException e) {
//            // TODO Handle IOException
//            throw (RuntimeException)new RuntimeException().initCause(e);
//        }
//    }

    /**
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    @Override
    public void mouseMoved(MapMouseEvent e) {
        super.mouseMoved(e);
        IMap map = getContext().getMap();
        if (!dragging) {
            map.getBlackboard().put(BLACKBOARD_CENTER_POINT, e.getPoint());
            //updateStarMapGraphic();

            if (drawSelectionLineCommand != null) {
                drawSelectionLineCommand.setValid(false);
                getContext().sendASyncCommand(drawSelectionLineCommand);
                drawSelectionLineCommand = null;
            }

            Pair<Point, Long> pair = getSector(e.getPoint(), getNodesMap());
            if (selected == null || pair == null || !selected.left().equals(pair.left())) {
                selected = pair;
                if (drawSelectedSectorCommand != null) {
                    System.out.println("Deleting old sector marker: "+drawSelectedSectorCommand.getValidArea());
                    drawSelectedSectorCommand.setValid(false);
                    getContext().sendASyncCommand(drawSelectedSectorCommand);
                    drawSelectedSectorCommand = null;
                }
                if (pair != null) {
                    System.out.println("Drawing sector marker at "+pair.left()+" near point "+e.getPoint());
                    java.awt.geom.Ellipse2D r = new java.awt.geom.Ellipse2D.Float(pair.left().x - 3, pair.left().y - 3, 7, 7);
                    // java.awt.geom.Path2D p = new java.awt.geom.Path2D.Float(s );
                    // Rectangle2D r = new Rectangle2D.Float(pair.left().x-2, pair.left().y-2, 5,
                    // 5);
                    drawSelectedSectorCommand = getContext().getDrawFactory().createDrawShapeCommand(r, Color.RED, 1, 2);
                    java.awt.geom.Line2D l = new java.awt.geom.Line2D.Float(pair.left().x, pair.left().y, e.getPoint().x, e.getPoint().y);
                    drawSelectionLineCommand = getContext().getDrawFactory().createDrawShapeCommand(l, Color.BLUE);

                    getContext().sendSyncCommand(drawSelectedSectorCommand);
                    getContext().sendSyncCommand(drawSelectionLineCommand);
                    getContext().getSelectedLayer().refresh(null);
                } else {
                    System.out.println("No sector found near point "+e.getPoint());
                }
            }

        } else {
            map.getBlackboard().put(BLACKBOARD_CENTER_POINT, null);
        }
    }

    /**
     * Tell the star map graphic to redraw the circle on the closest sector
     */
//    private void updateStarMapGraphic() {
//        if(starMapGraphicLayer == null) {
//            IMap map = getContext().getMap();
//            List<ILayer> layers = map.getMapLayers();
//            for (ILayer layer : layers) {
//                if (layer.getGeoResource().canResolve(StarMapGraphic.class)) {
//                    starMapGraphicLayer = layer;
//                    break;
//                }
//            }
//        }
//        if (starMapGraphicLayer != null) {
//            starMapGraphicLayer.refresh(null);
//        }
//    }

    /**
     * Executes the specified pan command, and only after it is executed, expires the last translate
     * command
     */
    private class PanAndInvalidate implements Command, NavCommand {

        private NavCommand command;
        private TranslateCommand expire;

        PanAndInvalidate(NavCommand command, TranslateCommand expire) {
            this.command = command;
            this.expire = expire;
        }

        public Command copy() {
            return new PanAndInvalidate(command, expire);
        }

        public String getName() {
            return "PanAndDiscard";
        }

        public void run( IProgressMonitor monitor ) throws Exception {
            try {
                command.run(monitor);
            } finally {
                expire.setValid(false);
            }
        }

        public void setViewportModel( ViewportModel model ) {
            command.setViewportModel(model);
        }

        public net.refractions.udig.project.internal.Map getMap() {
            return command.getMap();
        }

        public void setMap( IMap map ) {
            command.setMap(map);
        }

        public void rollback( IProgressMonitor monitor ) throws Exception {
            command.rollback(monitor);
        }

    }

}