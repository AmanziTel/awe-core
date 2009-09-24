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

import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.core.Pair;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.internal.MapGraphicResource;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.commands.draw.TranslateCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import org.amanzi.awe.mapgraphic.star.StarMapGraphic;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.StarDataVault;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.api.core.Node;

/**
 * Custom uDIG Map Tool for performing a 'star analysis'. This means it interacts with objects on
 * the map and a custom star mapgraphic is used to draw lines representing relations to the objects
 * on the map. These lines look like a star, hence the name.
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class StarTool extends AbstractModalTool {
    private boolean dragging=false;
    private Point start=null;
    private Node gisNode;

    private TranslateCommand command;
    private Map<Node, java.awt.Point> nodesMap;
    private ILayer starMapGraphicLayer; // cache so that mousemove event does not do so much work

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
        super.setActive(active);
        // add layer on map if necessary
        if (active) {
            setsGisLayeronMap();
            setLayerOnMap(StarMapGraphic.class);
        }
    }

    /**
     *
     */
    private void setsGisLayeronMap() {
        IProgressMonitor monitor = new NullProgressMonitor();
        if (gisNode == null) {
            synchronized (this) {
                if (gisNode == null) {
                    gisNode = NeoUtils.findOrCreateStarGisNode();
                }
            }
        }
        IMap map = getContext().getMap();
        List<ILayer> layers = map.getMapLayers();
        try {
            for (ILayer layer : layers) {
                if (layer.getGeoResource().canResolve(Node.class)) {
                    Node node = layer.getGeoResource().resolve(Node.class, monitor);
                    if (node.equals(gisNode)) {
                        return;
                    }

                }
            }

            String databaseLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            List<IResolve> serv = catalog.find(new URL("file://" + databaseLocation), monitor);

            List<IGeoResource> list = new ArrayList<IGeoResource>();
            for (IResolve iResolve : serv) {
                List< ? extends IGeoResource> resources = ((IService)iResolve).resources(null);
                for (IGeoResource singleResource : resources) {
                    if (singleResource.canResolve(Node.class) && singleResource.resolve(Node.class, monitor).equals(gisNode)) {
                        list.add(singleResource);
                        ApplicationGIS.addLayersToMap(map, list, map.getMapLayers().size());
                        return;
                    }
                }
            }
            ApplicationGIS.addLayersToMap(map, list, map.getMapLayers().size());
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     *
     */
    protected void setLayerOnMap(Class<? extends MapGraphic> resourceClass) {
        IMap map = getContext().getMap();
        List<ILayer> layers = map.getMapLayers();
        for (ILayer layer : layers) {
            if (layer.getGeoResource().canResolve(resourceClass)) {
                return;
            }
        }
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        List<IResolve> serv = catalog.find(MapGraphicService.SERVICE_URL, null);
        try {
            for (IResolve iResolve : serv) {
                List<MapGraphicResource> resources;
                resources = ((MapGraphicService)iResolve).resources(null);
                for (MapGraphicResource mapGraphicResource : resources) {
                    if (mapGraphicResource.canResolve(resourceClass)) {
                        List<IGeoResource> list = new ArrayList<IGeoResource>();
                        list.add(mapGraphicResource);
                        ApplicationGIS.addLayersToMap(map, list, map.getMapLayers().size());
                        return;
                    }
                }
            }
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
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
        if (dragging) {
        	((ViewportPane)context.getMapDisplay()).enableDrawCommands(true);
            Point end=e.getPoint();
            NavCommand finalPan = context.getNavigationFactory().createPanCommandUsingScreenCoords(start.x-end.x, start.y-end.y);
            context.sendASyncCommand(new PanAndInvalidate(finalPan, command));
            nodesMap = null;  // after panning the nodes might have changed, so force reload on next mouse released
            dragging = false;

        }else{
            final IMap map = getContext().getMap();
            IBlackboard blackboard = map.getBlackboard();
            Map<Node, java.awt.Point> nodesMap = getNodesMap();
            if (nodesMap == null) {
                return;
            }
            Pair<Point, Node> pair = StarMapGraphic.getSector(e.getPoint(), nodesMap);
            blackboard.put(StarMapGraphic.BLACKBOARD_START_ANALYSER, pair);
            updateLayerStarLayer();
        }
        // clear layer cache in case user deletes or adds star map graphic
        starMapGraphicLayer = null;
    }

    private Map<Node,java.awt.Point> getNodesMap() {
        if(nodesMap==null) {
            //nodesMap = (Map<Node, java.awt.Point>)blackboard.get(StarMapGraphic.BLACKBOARD_NODE_LIST);
            nodesMap = StarDataVault.getInstance().getCopyOfAllMap();
        }
        return nodesMap;
    }
    
    /**
     *
     */
    private void updateLayerStarLayer() {
        IMap map = getContext().getMap();
        List<ILayer> layers = map.getMapLayers();
        try {
            for (ILayer layer : layers) {
                if (layer.getGeoResource().canResolve(Node.class)
                        && gisNode.equals(layer.getGeoResource().resolve(Node.class, null))) {
                    layer.refresh(null);
                    return;
                }
            }
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

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
            map.getBlackboard().put(StarMapGraphic.BLACKBOARD_CENTER_POINT, e.getPoint());
            updateStarMapGraphic();
        } else {
            map.getBlackboard().put(StarMapGraphic.BLACKBOARD_CENTER_POINT, null);
        }
    }

    /**
     * Tell the star map graphic to redraw the circle on the closest sector
     */
    private void updateStarMapGraphic() {
        if(starMapGraphicLayer == null) {
            IMap map = getContext().getMap();
            List<ILayer> layers = map.getMapLayers();
            for (ILayer layer : layers) {
                if (layer.getGeoResource().canResolve(StarMapGraphic.class)) {
                    starMapGraphicLayer = layer;
                    break;
                }
            }
        }
        if (starMapGraphicLayer != null) {
            starMapGraphicLayer.refresh(null);
        }
    }

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