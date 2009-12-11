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
package org.amanzi.awe.render.drive;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.amanzi.awe.catalog.neo.GeoConstant;
import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.catalog.neo.GeoNeo.GeoNode;
import org.amanzi.awe.neostyle.NeoStyle;
import org.amanzi.awe.neostyle.NeoStyleContent;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.DriveEvents;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.index.PropertyIndex.NeoIndexRelationshipTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * <p>
 * Renderer for GeoNeo with GisTypes==GisTypes.Tems
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class TemsRenderer extends RendererImpl implements Renderer {
    private MathTransform transform_d2w;
    private MathTransform transform_w2d;
    private AffineTransform base_transform = null;  // save original graphics transform for repeated re-use
    private Color drawColor = Color.BLACK;
    private Color fillColor = new Color(200, 128, 255, (int)(0.6*255.0));
    private Color labelColor = Color.DARK_GRAY;
    private Node aggNode;
    private String mpName;
    private String msName;
    private boolean normalSiteName;
    private boolean notMsLabel;
    private int eventIconSize;
    private static final Color COLOR_HIGHLIGHTED = Color.CYAN;;
    private static final Color COLOR_HIGHLIGHTED_SELECTED = Color.RED;

    @Override
    public void render(Graphics2D destination, IProgressMonitor monitor) throws RenderException {
        ILayer layer = getContext().getLayer();
        // Are there any resources in the layer that respond to the GeoNeo class (should be the case
        // if we found a Neo4J database with GeoNeo data)
        // TODO: Limit this to network data only
        IGeoResource resource = layer.findGeoResource(GeoNeo.class);
        if (resource != null) {
            renderGeoNeo(destination, resource, monitor);
        }
    }

    @Override
    public void render(IProgressMonitor monitor) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    private void setCrsTransforms(CoordinateReferenceSystem dataCrs) throws FactoryException {
        boolean lenient = true; // needs to be lenient to work on uDIG 1.1 (otherwise we get error:
        // bursa wolf parameters required
        CoordinateReferenceSystem worldCrs = context.getCRS();
        this.transform_d2w = CRS.findMathTransform(dataCrs, worldCrs, lenient);
        this.transform_w2d = CRS.findMathTransform(worldCrs, dataCrs, lenient);
    }

    private Envelope getTransformedBounds() throws TransformException {
        ReferencedEnvelope bounds = getRenderBounds();
        if (bounds == null) {
            bounds = this.context.getViewportModel().getBounds();
        }
        Envelope bounds_transformed = null;
        if (bounds != null && transform_w2d != null) {
            bounds_transformed = JTS.transform(bounds, transform_w2d);
        }
        return bounds_transformed;
    }

    /**
     * This method is called to render data from the Neo4j 'GeoNeo' Geo-Resource.
     */
    private void renderGeoNeo(Graphics2D g, IGeoResource neoGeoResource, IProgressMonitor monitor) throws RenderException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        monitor.beginTask("render drive test data", IProgressMonitor.UNKNOWN);
        GeoNeo geoNeo = null;
        //enable/disable rendering of rectangles for the spatial index
        boolean enableIndexRendering = false;

        // Setup default drawing parameters and thresholds (to be modified by style if found)
        int maxSitesLabel = 30;
        int maxSitesFull = 100;
        int maxSitesLite = 1000;
        // int maxSymbolSize = 40;
        int alpha = (int)(0.6*255.0);
        int drawSize = 3;
        Font font = g.getFont();
        int fontSize = font.getSize();
        IStyleBlackboard style = getContext().getLayer().getStyleBlackboard();
        NeoStyle neostyle = (NeoStyle)style.get(NeoStyleContent.ID);
        mpName = NeoStyleContent.DEF_SITE_NAME;
        msName = NeoStyleContent.DEF_SECTOR_NAME;
        if (neostyle!=null){
        	fillColor=neostyle.getFill();
        	drawColor=neostyle.getLine();
            alpha = 255 - (int)((double)neostyle.getSectorTransparency() / 100.0 * 255.0);
            try {
                fillColor = neostyle.getFill();
                drawColor = neostyle.getLine();
                labelColor = neostyle.getLabel();
                alpha = 255 - (int)((double)neostyle.getSectorTransparency() / 100.0 * 255.0);
                //drawSize = neostyle.getSymbolSize();
                drawSize = 3;
                maxSitesLabel = neostyle.getLabeling();
                maxSitesFull = neostyle.getSmallSymb();
                maxSitesLite = neostyle.getSmallestSymb();
                //scaleSectors = !neostyle.isFixSymbolSize();
                // maxSymbolSize = neostyle.getMaximumSymbolSize();
                fontSize = neostyle.getFontSize();
                //TODO: Remove these when defaults from style work property
                maxSitesLabel = 50;
                maxSitesLite = 500;
                maxSitesFull = 50;
                mpName = neostyle.getSiteName();
                msName = neostyle.getSectorName();
            } catch (Exception e) {
                //TODO: we can get here if an old style exists, and we have added new fields
            }
        }
        normalSiteName = NeoStyleContent.DEF_SITE_NAME.equals(mpName);
        notMsLabel = NeoStyleContent.DEF_SECTOR_NAME.equals(msName);
        g.setFont(font.deriveFont((float)fontSize));

        int drawWidth = 1 + 2*drawSize;
        NeoService neo = NeoServiceProvider.getProvider().getService();
        Transaction tx = neo.beginTx();
        try {
            monitor.subTask("connecting");
            geoNeo = neoGeoResource.resolve(GeoNeo.class, new SubProgressMonitor(monitor, 10));
            //String selectedProp = geoNeo.getPropertyName();
            aggNode = geoNeo.getAggrNode();
            Map<String, Object> selectionMap = getSelectionMap(geoNeo);
            Long crossHairId1 = null;
            Long crossHairId2 = null;
            if (selectionMap != null) {
                crossHairId1 = (Long)selectionMap.get(GeoConstant.Drive.SELECT_PROPERTY1);
                crossHairId2 = (Long)selectionMap.get(GeoConstant.Drive.SELECT_PROPERTY2);
            }
            // Integer propertyAdjacency = geoNeo.getPropertyAdjacency();
            setCrsTransforms(neoGeoResource.getInfo(null).getCRS());
            Envelope bounds_transformed = getTransformedBounds();
            Envelope data_bounds = geoNeo.getBounds();
            boolean drawFull = true;
            boolean drawLite = true;
            boolean drawLabels = true;
            eventIconSize = 6;
            if (bounds_transformed == null) {
                drawFull = false;
                drawLite = false;
                drawLabels = false;
            }else if (data_bounds != null && data_bounds.getHeight()>0 && data_bounds.getWidth()>0) {
                double dataScaled = (bounds_transformed.getHeight() * bounds_transformed.getWidth())
                        / (data_bounds.getHeight() * data_bounds.getWidth());
                double countScaled = dataScaled * geoNeo.getCount();
                drawLabels = countScaled < maxSitesLabel;
                drawFull = countScaled < maxSitesFull;
                if (drawFull) {
                    eventIconSize = countScaled * 16 <= maxSitesFull ? 16 : countScaled * 4 <= maxSitesFull ? 12
                            : countScaled * 2 <= maxSitesFull ? 8 : 6;
                }
                drawLite = countScaled < maxSitesLite;
            }
            int trans = alpha;
            if (haveSelectedNodes()) {
                trans = 25;
            }
            // draw event icon flag
            boolean drawEvents = /* true || */drawFull;

            fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), trans);
            g.setColor(drawColor);
            int count = 0;
            monitor.subTask("drawing");
            // single object for re-use in transform below (minimize object creation)
            Coordinate world_location = new Coordinate();
            java.awt.Point prev_p = null;
            java.awt.Point prev_l_p = null;
            java.awt.Point cached_l_p = null;
            GeoNode cached_node = null;  // for label positioning
            long startTime = System.currentTimeMillis();
            
            // First we find all selected points to draw with a highlight behind the main points
            ArrayList<Node> selectedPoints = new ArrayList<Node>();
            final Set<Node> selectedNodes = new HashSet<Node>(geoNeo.getSelectedNodes());
            // TODO refactor selection point (for example: in draws mp node add method
            // isSelected(node))
            if (selectionMap != null) {
                Long beginTime = (Long)selectionMap.get(GeoConstant.Drive.BEGIN_TIME);
                Long endTime = (Long)selectionMap.get(GeoConstant.Drive.END_TIME);
                if (beginTime != null && endTime != null && beginTime <= endTime) {
                    for (GeoNode node : geoNeo.getGeoNodes(bounds_transformed)) {
                        Long time = NeoUtils.getNodeTime(node.getNode());
                        if (time != null && time >= beginTime && time <= endTime) {
                            selectedNodes.add(node.getNode());
                        }
                    }
                }
            }
            for(Node node: selectedNodes) {
                if("file".equals(node.getProperty("type",""))){
                    //Select all 'mp' nodes in that file
                    for (Node rnode:node.traverse(Traverser.Order.BREADTH_FIRST, new StopEvaluator(){
                            @Override
                            public boolean isStopNode(TraversalPosition currentPos) {
                                return !currentPos.isStartNode() && "file".equals(currentPos.currentNode().getProperty("type", ""));
                            }}, new ReturnableEvaluator(){
        
                                @Override
                                public boolean isReturnableNode(TraversalPosition currentPos) {
                                    return "mp".equals(currentPos.currentNode().getProperty("type", ""));
                                }}, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)){
                            selectedPoints.add(rnode);
                        }
                } else {
                    //Traverse backwards on CHILD relations to closest 'mp' Point
                    for (@SuppressWarnings("unused")
                    Node rnode:node.traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator(){
                        @Override
                        public boolean isStopNode(TraversalPosition currentPos) {
                            return "mp".equals(currentPos.currentNode().getProperty("type", ""));
                        }}, new ReturnableEvaluator(){
    
                            @Override
                            public boolean isReturnableNode(TraversalPosition currentPos) {
                                return "mp".equals(currentPos.currentNode().getProperty("type", ""));
                            }}, NetworkRelationshipTypes.CHILD, Direction.INCOMING)){
                        selectedPoints.add(rnode);
                        break;
                    }
                }
            }
            // Now draw the selected points highlights
            for(Node rnode:selectedPoints){
                GeoNode node = new GeoNode(rnode);
                Coordinate location = node.getCoordinate();
                if (bounds_transformed != null && !bounds_transformed.contains(location)) {
                    continue; // Don't draw points outside viewport
                }
                try {
                    JTS.transform(location, world_location, transform_d2w);
                } catch (Exception e) {
                    continue;
                }
                java.awt.Point p = getContext().worldToPixel(world_location);
                if(prev_p != null && prev_p.x == p.x && prev_p.y == p.y) {
                    prev_p = p;
                    continue;
                } else {
                    prev_p = p;
                }
                renderSelectedPoint(g, p, drawSize, drawFull, drawLite);
            }
            Node indexNode = null;
            HashMap<String,Integer> colorErrors = new HashMap<String,Integer>();
            prev_p = null;// else we do not show selected node
            // Now draw the actual points
            for (GeoNode node : geoNeo.getGeoNodes(bounds_transformed)) {
                if(enableIndexRendering && indexNode==null) {
                    indexNode = getIndexNode(node);
                }
                Coordinate location = node.getCoordinate();

                if (bounds_transformed != null && !bounds_transformed.contains(location)) {
                    continue; // Don't draw points outside viewport
                }
                try {
                    JTS.transform(location, world_location, transform_d2w);
                } catch (Exception e) {
                    // JTS.transform(location, world_location, transform_w2d.inverse());
                }

                java.awt.Point p = getContext().worldToPixel(world_location);
                if(prev_p != null && prev_p.x == p.x && prev_p.y == p.y) {
                    prev_p = p;
                    continue;
                } else {
                    prev_p = p;
                }

                Color nodeColor = fillColor;
                    try {
                        nodeColor = getNodeColor(node.getNode(), fillColor);
                        // nodeColor = getColorOfMpNode(select, node.getNode(), fillColor,
                        // selectedProp, redMinValue, redMaxValue,
                        // lesMinValue, moreMaxValue);
                    } catch (RuntimeException e) {
                        String errName = e.toString();
                        if(colorErrors.containsKey(errName)) {
                            colorErrors.put(errName, colorErrors.get(errName) + 1);
                        }else{
                            colorErrors.put(errName, 1);
                        }
                    }
                Color borderColor = g.getColor();
                if(selectedNodes.size() > 0) {
                    if (selectedNodes.contains(node.getNode())) {
                        borderColor = COLOR_HIGHLIGHTED;
                    }
                }
                long id = node.getNode().getId();
                if ((crossHairId1 != null && id == crossHairId1) || (crossHairId2 != null && crossHairId2 == id)) {
                    borderColor = COLOR_HIGHLIGHTED_SELECTED;
                }
                renderPoint(g, p, borderColor, nodeColor, drawSize, drawWidth, drawFull, drawLite);
                if (drawLabels || drawEvents) {
                    double theta = 0.0;
                    double dx = 0.0;
                    double dy = 0.0;
                    if (prev_l_p == null) {
                        prev_l_p = p;
                        cached_l_p = p;   // so we can draw first point using second point settings
                        cached_node = node;
                    } else {
                        try {
                            dx = p.x - prev_l_p.x;
                            dy = p.y - prev_l_p.y;
                            if (Math.abs(dx) < Math.abs(dy) / 2) {
                                // drive goes north-south
                                theta = 0;
                            } else if (Math.abs(dy) < Math.abs(dx) / 2) {
                                // drive goes east-west
                                theta = Math.PI / 2;
                            } else if (dx * dy < 0) {
                                // drive has negative slope
                                theta = -Math.PI / 4;
                            } else {
                                theta = Math.PI / 4;
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (Math.abs(dx) > 20 || Math.abs(dy) > 20) {
                        if (drawLabels) {
                            renderLabel(g, count, node, p, theta);
                        }
                        if (drawEvents) {
                            renderEvents(g, node, p, theta);
                        }
                        if(cached_node != null) {
                            if (drawLabels) {
                                renderLabel(g, 0, cached_node, cached_l_p, theta);
                            }
                            if (drawEvents) {
                                renderEvents(g, cached_node, cached_l_p, theta);
                            }
                            cached_node = null;
                            cached_l_p = null;
                        }
                        prev_l_p = p;
                    }
                }
                if (base_transform != null) {
                    // recover the normal transform
                    g.setTransform(base_transform);
                    g.setColor(drawColor);
                    //base_transform = null;
                }
                monitor.worked(1);
                count++;
                if (monitor.isCanceled())
                    break;
            }
            for(String errName:colorErrors.keySet()){
                int errCount = colorErrors.get(errName);
                System.err.println("Error determining color of "+errCount+" nodes: "+errName);
            }
            if(indexNode!=null) {
                renderIndex(g, bounds_transformed, indexNode);
            }
            System.out.println("Drive renderer took " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s to draw " + count + " points");
            tx.success();
        } catch (TransformException e) {
            throw new RenderException(e);
        } catch (FactoryException e) {
            throw new RenderException(e);
        } catch (IOException e) {
            throw new RenderException(e); // rethrow any exceptions encountered
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // if (geoNeo != null)
            // geoNeo.close();
            monitor.done();
            tx.finish();
        }
    }

    private Node getIndexNode(GeoNode node) {
        try {
            System.out.println("Searching for index nodes on node: "+node.getName());
            Node endNode = node.getNode();
            System.out.println("Searching for index nodes on node: id:"+endNode.getId()+", name:"+endNode.getProperty("name", null)+", type:"+endNode.getProperty("type", null)+", index:"+endNode.getProperty("index", null)+", level:"+endNode.getProperty("level", null)+", max:"+endNode.getProperty("max", null)+", min:"+endNode.getProperty("min", null));
            for(Relationship relationship: node.getNode().getRelationships(NeoIndexRelationshipTypes.CHILD, Direction.INCOMING)){
                endNode = relationship.getStartNode();
                System.out.println("Trying possible index node: id:"+endNode.getId()+", name:"+endNode.getProperty("name", null)+", type:"+endNode.getProperty("type", null)+", index:"+endNode.getProperty("index", null)+", level:"+endNode.getProperty("level", null)+", max:"+endNode.getProperty("max", null)+", min:"+endNode.getProperty("min", null));
                int[] index = (int[])endNode.getProperty("index", new int[0]);
                if(index.length == 2) {
                    return endNode;
                }
            }
        }catch(Exception e){
            System.err.println("Failed to find index node: "+e);
            //e.printStackTrace(System.err);
        }
        return null;
    }

    private void renderIndex(Graphics2D g, Envelope bounds_transformed, Node indexNode) {
        Coordinate world_location = new Coordinate();
        try {
            INDEX_LOOP: for(Node index: indexNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, NeoIndexRelationshipTypes.CHILD, Direction.BOTH)){
                int[] ind = (int[])index.getProperty("index", new int[0]);
                if(ind.length == 2) {
                    double[] max = (double[])index.getProperty("max", new double[0]);
                    double[] min = (double[])index.getProperty("min", new double[0]);
                    int level = (Integer)index.getProperty("level", 0);
                    if(max.length == 2 && min.length==2){
                        drawColor = new Color(0.5f,0.5f,0.5f,1.0f-Math.max(0.1f, 0.8f*(5.0f-level)/5.0f));
                        g.setColor(drawColor);
                        Coordinate[] c = new Coordinate[2];
                        java.awt.Point[] p = new java.awt.Point[2];
                        c[0] = new Coordinate(min[1], max[0]);
                        c[1] = new Coordinate(max[1], min[0]);
                        for(int i=0;i<2;i++){
                            if (bounds_transformed != null && !bounds_transformed.contains(c[i])) {
                                continue INDEX_LOOP;
                            }
                            try {
                                JTS.transform(c[i], world_location, transform_d2w);
                            } catch (Exception e) {
                                // JTS.transform(location, world_location, transform_w2d.inverse());
                            }
      
                            p[i] = getContext().worldToPixel(world_location);
                        }
                        if(p[1].x > p[0].x && p[1].y > p[0].y) {
                            g.drawRect(p[0].x, p[0].y, p[1].x - p[0].x,  p[1].y - p[0].y);
                            g.drawString(""+ind[0]+":"+ind[1]+"["+level+"]", p[0].x, p[0].y);
                        } else {
                            System.err.println("Invalid index bbox: "+p[0]+":"+p[1]);
                            g.drawRect(Math.min(p[0].x,p[1].x), Math.min(p[0].y,p[1].y), Math.abs(p[1].x - p[0].x),  Math.abs(p[1].y - p[0].y));
                        }
                    }
                }
            }
        }catch(Exception e){
            System.err.println("Failed to draw index: "+e);
            e.printStackTrace(System.err);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getSelectionMap(GeoNeo geoNeo) {
        Map<String, Object> selectionMap = (Map<String, Object>)geoNeo.getProperties(GeoNeo.DRIVE_INQUIRER);
        return selectionMap;
    }

    /**
     * @return true if drive have selected node
     */
    private boolean haveSelectedNodes() {
        return aggNode != null;
    }

    private void renderLabel(Graphics2D g, int count, GeoNode node, java.awt.Point p, double theta) {
        if (base_transform == null)
            base_transform = g.getTransform();
        g.setTransform(base_transform);
        g.translate(p.x, p.y);
        g.rotate(-theta);
        g.setColor(labelColor);
        //g.drawString(""+Integer.toString(count)+": "+node.toString(), 10, 5);
        g.drawString(getPointLabel(node), 10, 5);
    }

    /**
     *Gets label of mp node
     * 
     * @param node GeoNode
     * @return String
     */
    private String getPointLabel(GeoNode node) {
        String pointName = normalSiteName ? node.toString() : node.getNode().getProperty(mpName, node.toString()).toString();
        if (!notMsLabel) {
            String msNames = NeoUtils.getMsNames(node.getNode(), msName);
            if (!msNames.isEmpty()) {
                pointName += ", " + msNames;
            }
        }
        return pointName;
    }

    /**
     * gets sector color
     * 
     * @param child - sector node
     * @param defColor - default value
     * @return color
     */
    private Color getNodeColor(Node node, Color defColor) {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            if (aggNode == null) {
                return defColor;
            }
            Node chartNode = NeoUtils.getChartNode(node, aggNode);
            if (chartNode == null) {
                return defColor;
            }
            return new Color((Integer)chartNode.getProperty(INeoConstants.AGGREGATION_COLOR, defColor.getRGB()));
        } finally {
            tx.finish();
        }
    }

    /**
     * This one is very simple, just draw a rectangle at the point location.
     * 
     * @param g
     * @param p
     * @param node
     */
    private void renderPoint(Graphics2D g, java.awt.Point p, Color borderColor, Color fillColor, int drawSize, int drawWidth,
            boolean drawFull, boolean drawLite) {
        Color oldColor = g.getColor();
        if(drawFull) {
            g.setColor(fillColor);
            g.fillRect(p.x - drawSize, p.y - drawSize, drawWidth, drawWidth);
            g.setColor(borderColor);
            g.drawRect(p.x - drawSize, p.y - drawSize, drawWidth, drawWidth);

        } else if (drawLite) {
            g.setColor(fillColor);
            g.fillOval(p.x - drawSize, p.y - drawSize, drawWidth, drawWidth);
        } else {
            g.setColor(fillColor);
            g.fillOval(p.x - 1, p.y - 1, 3, 3);
        }
        g.setColor(oldColor);
    }

    /**
     * This one is very simple, just draw a rectangle at the point location.
     * 
     * @param g
     * @param p
     * @param node
     */
    private void renderEvents(Graphics2D g, GeoNode node, java.awt.Point p, double theta) {
        // null - use current transaction
        DriveEvents event = DriveEvents.getWorstEvent(node.getNode(), null);
        if (event == null) {
            return;
        }
        if (base_transform == null)
            base_transform = g.getTransform();
        g.setTransform(base_transform);
        g.translate(p.x, p.y);
        g.rotate(-theta);

        Image eventImage = event.getEventIcon().getImage(eventIconSize);
        if (eventImage != null) {
            ImageObserver imOb = null;
            final int width = eventImage.getWidth(imOb);
            final int height = eventImage.getHeight(imOb);
            g.drawImage(eventImage, -10 - width, -height / 2, width, height, imOb);
            return;
        }
    }
    /**
     * This one is very simple, just draw a rectangle at the point location.
     * 
     * @param g
     * @param p
     */
    private void renderSelectedPoint(Graphics2D g, java.awt.Point p, int drawSize, boolean drawFull, boolean drawLite) {
        Color oldColor = g.getColor();
        if(drawFull) {
            renderSelectionGlow(g, p, drawSize * 3);
        } else if (drawLite) {
            renderSelectionGlow(g, p, drawSize * 2);
        } else {
            renderSelectionGlow(g, p, drawSize);
        }
        g.setColor(oldColor);
    }

    /**
     * This method draws a fading glow around a point for selected site/sectors
     *
     * @param g
     * @param p
     * @param drawSize
     */
    private void renderSelectionGlow(Graphics2D g, java.awt.Point p, int drawSize) {
        drawSize *= 3;
        Color highColor = new Color(COLOR_HIGHLIGHTED.getRed(), COLOR_HIGHLIGHTED.getGreen(), COLOR_HIGHLIGHTED.getBlue(), 8);
        g.setColor(highColor);
        for(;drawSize > 2; drawSize *= 0.8) {
            g.fillOval(p.x - drawSize, p.y - drawSize, 2 * drawSize, 2 * drawSize);
        }
    }

}
