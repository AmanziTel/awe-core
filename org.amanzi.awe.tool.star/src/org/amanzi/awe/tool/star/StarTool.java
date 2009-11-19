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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.core.Pair;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.command.SetLayerVisibilityCommand;
import net.refractions.udig.project.internal.commands.selection.SelectLayerCommand;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.internal.commands.draw.TranslateCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import org.amanzi.awe.views.network.view.NetworkTreeView;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;
import org.neo4j.neoclipse.view.NeoGraphViewPart;

/**
 * Custom uDIG Map Tool for performing a 'star analysis'. This means it interacts with objects on
 * the map and a custom star mapgraphic is used to draw lines representing relations to the objects
 * on the map. These lines look like a star, hence the name.
 * 
 * @author Cinkel_A
 * @author Craig
 * @since 1.0.0
 */
public class StarTool extends AbstractModalTool {
    /** StarTool BLACKBOARD_START_ANALYSER field */
    public static final String BLACKBOARD_START_ANALYSER = "org.amanzi.awe.tool.star.StarTool.analyser";
    /** StarTool BLACKBOARD_NODE_LIST field */
    public static final String BLACKBOARD_NODE_LIST = "org.amanzi.awe.tool.star.StarTool.nodes";
    /** StarTool BLACKBOARD_CENTER_POINT field */
    public static final String BLACKBOARD_CENTER_POINT = "org.amanzi.awe.tool.star.StarTool.point";
    private static final int MAXIMUM_SELECT_LEN = 10000; // find sectors in 100x100 pixels
    private boolean dragging = false;
    private Point start = null;

    private TranslateCommand command;
    private Map<Long, java.awt.Point> nodesMap;
    private DrawShapeCommand drawSelectedSectorCommand;
    private Pair<Point, Long> selected;
    private Node gisNode;
    private ILayer selectedLayer;

    /**
     * Creates an new instance of the StarTool which supports mouse actions and mouse motion. These
     * are used to support panning as well as the star analysis.
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
        if (super.isActive() != active) {
            super.setActive(active);
            // add layer on map if necessary
            if (active) {
                chooseAnalysisData();
            } else {
                IBlackboard blackboard = getContext().getMap().getBlackboard();
                blackboard.put(BLACKBOARD_CENTER_POINT, null);
                blackboard.put(BLACKBOARD_NODE_LIST, null);
                blackboard = getContext().getSelectedLayer().getBlackboard();
                blackboard.put(BLACKBOARD_START_ANALYSER, null);
                if (drawSelectedSectorCommand != null) {
                    System.out.println("Deleting old sector marker: " + drawSelectedSectorCommand.getValidArea());
                    drawSelectedSectorCommand.setValid(false);
                    getContext().sendASyncCommand(drawSelectedSectorCommand);
                    drawSelectedSectorCommand = null;
                }
                getContext().getSelectedLayer().refresh(null);

            }
        }
    }

    /**
     * This method checks that the user has not made changes to the distribution analysis or layers
     * view that would require changing the dataset being used in the star analysis. If changes are
     * detected, the dataset and layer are re-determined.
     */
    private void checkAnalysisData() {
        selectedLayer = getContext().getSelectedLayer();
        if(gisNode != null && selectedLayer != null) {
            Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
            try {
                String aggregatedProperty = gisNode.getProperty(INeoConstants.PROPERTY_SELECTED_AGGREGATION, "").toString();
                if (aggregatedProperty.length() < 1) {
                    gisNode = null;
                }
                tx.success();
            } finally {
                tx.finish();
            }
            if(gisNode != null) {
                try {
                    Node node = null;
                    if(selectedLayer.getGeoResource().canResolve(Node.class)) {
                        node = selectedLayer.getGeoResource().resolve(Node.class, new NullProgressMonitor());
                    }
                    if(!node.equals(gisNode)) {
                        gisNode = null;
                        selectedLayer = null;
                    }
                } catch (IOException e) {
                    // TODO Handle IOException
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
            }
        }
        if(gisNode == null || selectedLayer == null) {
            chooseAnalysisData();
        }
    }
    
    /**
     * This method works out from the database and layers views which dataset the user intends to
     * use in the star analysis. First the database is scanned for gis nodes that have statistics
     * and marked recent selected properties (see reuse analysis code). There should be only 1 or 0
     * results, but we deal with the chance of multiple results also (in case the logic of the reuse
     * analyser changes, we don't want to be sensitive to that). Then the map layers are scanned for
     * GeoNeo layers. Then the subset of gis nodes that exist both in the reuse analyser results and
     * the layers view are used. If one node is found, we use that node and layer. If several are
     * found we notify the user and select the first. If none are found we don't use anything, and
     * notify the user.
     * 
     * @TODO: we could automatically add a reuse analysis dataset to the map for the user if one
     *        exists and is not in the current map. Some commented out code for this is in place,
     *        but incomplete.
     */
    private void chooseAnalysisData() {
        // First find valid Reuse Analyser datasets and selected properties
        LinkedHashMap<Node, String> selectedGisNodes = new LinkedHashMap<Node, String>();
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            Node root = NeoServiceProvider.getProvider().getService().getReferenceNode();
            Iterator<Node> gisIterator = root.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    String property = currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").toString();
                    return INeoConstants.GIS_TYPE_NAME.equals(property);
                }
            }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            while (gisIterator.hasNext()) {
                Node node = gisIterator.next();
                String aggregatedProperty = node.getProperty(INeoConstants.PROPERTY_SELECTED_AGGREGATION, "").toString();
                if (aggregatedProperty.length() > 0) {
                    selectedGisNodes.put(node, aggregatedProperty);
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
        HashMap<Node, ILayer> validLayers = new HashMap<Node, ILayer>();
        ArrayList<Node> validNodes = new ArrayList<Node>();

        // Now find valid layers in the map (GeoNeo layers)
        try {
            for (ILayer layer : getContext().getMapLayers()) {
                if (layer.getGeoResource().canResolve(Node.class)) {
                    Node node = layer.getGeoResource().resolve(Node.class, new NullProgressMonitor());
                    validLayers.put(node, layer);
                    if(selectedGisNodes.containsKey(node)) {
                        validNodes.add(node);
                    }
                }
            }
        }catch(IOException e){
        }
        
        // And finally decide what to do based on the results of above search
        if(validNodes.size()==1) {
            // Perfect match, use it
            gisNode = validNodes.get(0);
        }else if (validNodes.size()>1) {
            // more than one match, check with selected layer, ask the user to select the best one
            for(Node node:validNodes){
                ILayer layer = getContext().getSelectedLayer();
                if(validLayers.get(node).equals(layer)) {
                    gisNode = node;
                    selectedLayer = layer;
                    break;
                }
            }
            if(gisNode == null) {
                // no selected layer matches, ask use to select a layer
                gisNode = validNodes.get(0);
                String message = "Several datasets are available for star analysis: \n";
                for(Node node:validNodes){
                    message += " " + validLayers.get(node).getName();
                }
                message += "\nThe first set, "+validLayers.get(gisNode).getName()+", has been chosen for the analysis.";
                message += "Should you wish to use another, select the one you want in the layers view and restart the star analysis.";
                tellUser(message);
            }
        }else if(selectedGisNodes.size()>0){
            // No valid nodes found, none of the reuse analyser gis nodes are layers in the map, add
            // a layer to the map
            //throw new Exception("Unimplemented: support automatic addition of distribution analysis layer to the map");
            tellUser("The star analysis requires the distribution analysis to provide the data for geographic display");

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
        } else {
            // No layers or reuse analyser nodes found, we cannot do the star analysis
            String message = "No dataset is available for star analysis.\n\n";
            message += "Please use the 'Distribution' analysis view to select\n";
            message += "a dataset and property and make sure that dataset\n";
            message += "is visible as a layer in the current map.";
            tellUser(message);
        }
        if(gisNode != null) {
            selectedLayer = validLayers.get(gisNode);
            if(selectedLayer!=null) {
                getContext().sendASyncCommand(new SetLayerVisibilityCommand(selectedLayer,true));
                if(selectedLayer != getContext().getSelectedLayer()) {
                    getContext().sendASyncCommand(new SelectLayerCommand(selectedLayer));
                }
            }
        }
    }

    private void tellUser(String message) {
        MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
        msg.setText("Select data for star analysis");
        msg.setMessage(message);
        msg.open();
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
        boolean activateStar = true;
        if (dragging) {
            activateStar = false;
        	((ViewportPane)context.getMapDisplay()).enableDrawCommands(true);
            Point end=e.getPoint();
            int dx = start.x-end.x;
            int dy = start.y-end.y;
            if(dx == 0 && dy == 0) {
                activateStar = true;
            }// else {
            // nodesMap = null; // after panning the nodes might have changed, so force reload on
            // next mouse released
            // }
            //TODO: Perhaps only run this if dx||dy non-zero ?
            NavCommand finalPan = context.getNavigationFactory().createPanCommandUsingScreenCoords(dx, dy);
            context.sendASyncCommand(new PanAndInvalidate(finalPan, command));
            dragging = false;

        }
        if (activateStar && selectedLayer != null) {
            selectedLayer.getBlackboard().put(BLACKBOARD_START_ANALYSER, selected);
            if (selected != null) {
                sendSelection(selected);
                selectedLayer.refresh(null);
            }
        }
        checkAnalysisData();
    }

    /**
     * Selects node in database and network views
     * 
     * @param selectedPair - pair of necessary node
     */
    private void sendSelection(Pair<Point, Long> selectedPair) {
        if (selectedPair == null || selectedPair.getRight() == null) {
            return;
        }
        Node nodeToSelect = NeoUtils.getNodeById(selectedPair.getRight());
        IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(NeoGraphViewPart.ID);
        if (view != null) {
        NeoGraphViewPart viewGraph = (NeoGraphViewPart)view;
            viewGraph.showNode(nodeToSelect);
            final StructuredSelection selection = new StructuredSelection(new Object[] {nodeToSelect});
            viewGraph.getViewer().setSelection(selection, true);
        }
        view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(NetworkTreeView.NETWORK_TREE_VIEW_ID);
        if (view != null) {
            NetworkTreeView networkView = (NetworkTreeView)view;
            networkView.selectNode(nodeToSelect);
        }
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
    
    @SuppressWarnings("unchecked")
    private Map<Long,java.awt.Point> getNodesMap() {
        if (selectedLayer != null) {
            nodesMap = (Map<Long, Point>)getContext().getMap().getBlackboard().get(BLACKBOARD_NODE_LIST);
        }
        return nodesMap;
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
        if (!dragging && selectedLayer != null) {
            map.getBlackboard().put(BLACKBOARD_CENTER_POINT, e.getPoint());
            Pair<Point, Long> pair = getSector(e.getPoint(), getNodesMap());
            if (selected == null || pair == null || !selected.left().equals(pair.left())) {
                selected = pair;
                if (drawSelectedSectorCommand != null) {
                    System.out.println("Deleting old sector marker: " + drawSelectedSectorCommand.getValidArea());
                    drawSelectedSectorCommand.setValid(false);
                    getContext().sendASyncCommand(drawSelectedSectorCommand);
                    drawSelectedSectorCommand = null;
                }
                if (pair != null) {
                    System.out.println("Drawing sector marker at " + pair.left() + " near point " + e.getPoint());
                    java.awt.geom.Ellipse2D r = new java.awt.geom.Ellipse2D.Float(pair.left().x - 3, pair.left().y - 3, 7, 7);
                    drawSelectedSectorCommand = getContext().getDrawFactory().createDrawShapeCommand(r, Color.RED, 1, 2);
                    getContext().sendSyncCommand(drawSelectedSectorCommand);
                    selectedLayer.refresh(null);
                } else {
                    // System.out.println("No sector found near point "+e.getPoint());
                }
            }

        } else {
            map.getBlackboard().put(BLACKBOARD_CENTER_POINT, null);
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