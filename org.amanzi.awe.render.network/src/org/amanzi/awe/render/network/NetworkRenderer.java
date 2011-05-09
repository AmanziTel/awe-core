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
package org.amanzi.awe.render.network;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.core.Pair;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.catalog.neo.GeoNeo.GeoNode;
import org.amanzi.awe.filters.AbstractFilter;
import org.amanzi.awe.filters.FilterUtil;
import org.amanzi.awe.neostyle.NeoStyle;
import org.amanzi.awe.neostyle.NeoStyleContent;
import org.amanzi.awe.ui.IGraphModel;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.CorrelationRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkSiteType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.utils.Utils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.graphics.RGB;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class NetworkRenderer extends RendererImpl {
    private static final Logger LOGGER = Logger.getLogger(NetworkRenderer.class);
    /** double CIRCLE_BEAMWIDTH field */
    private static final double CIRCLE_BEAMWIDTH = 360.0;
    private static final double DEFAULT_BEAMWIDTH = 10.0;
    public static final String BLACKBOARD_NODE_LIST = "org.amanzi.awe.tool.star.StarTool.nodes";
    public static final String BLACKBOARD_START_ANALYSER = "org.amanzi.awe.tool.star.StarTool.analyser";
    private static final Color COLOR_SITE_SELECTED = Color.CYAN;
    private static final Color COLOR_SECTOR_SELECTED = Color.CYAN;
    private static final Color COLOR_SECTOR_STAR = Color.RED;
    private AffineTransform base_transform = null; // save original graphics transform for repeated
                                                   // re-use
    private MathTransform transform_d2w;
    private MathTransform transform_w2d;
    private Node aggNode;
    private AbstractFilter filterSectors;
    private AbstractFilter filterSites;
    private DrawHints drawHints = new DrawHints();
    private IGraphModel graphModel;
    private RelationshipIndex index;

    private void setCrsTransforms(CoordinateReferenceSystem dataCrs) throws FactoryException {
        boolean lenient = true; // needs to be lenient to work on uDIG 1.1 (otherwise we get error:
                                // bursa wolf parameters required
        CoordinateReferenceSystem worldCrs = context.getCRS();
        this.transform_d2w = CRS.findMathTransform(dataCrs, worldCrs, lenient);
        this.transform_w2d = CRS.findMathTransform(worldCrs, dataCrs, lenient); // could use
                                                                                // transform_d2w.inverse()
                                                                                // also
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
     * This method is called to render what it can. It is passed a graphics context with which it
     * can draw. The class already contains a reference to a RenderContext from which it can obtain
     * the layer and the GeoResource to render.
     * 
     * @see net.refractions.udig.project.internal.render.impl.RendererImpl#render(java.awt.Graphics2D,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void render(Graphics2D g, IProgressMonitor monitor) throws RenderException {
        ILayer layer = getContext().getLayer();
        // Are there any resources in the layer that respond to the GeoNeo class (should be the case
        // if we found a Neo4J database with GeoNeo data)
        IGeoResource resource = layer.findGeoResource(GeoNeo.class);
        // Lagutko, 22.10.2009, initialize BlackboardEntry for star, this is necessary to enable
        // IMomento to save on app exit
        layer.getMap().getBlackboard().get(BLACKBOARD_NODE_LIST);
        if (resource != null) {
            renderGeoNeo(g, resource, monitor);
        }
    }

    public static class DrawHints {
        Color drawColor;
        Color siteColor;
        Color fillColor;
        Color labelColor;
        Color surroundColor;
        Color lineColor;
        boolean drawFull;
        boolean drawLite;
        boolean drawLabels;
        int drawSize;
        int alpha;
        boolean ignoreTransp;
        int maxSitesLabel;
        int maxSitesFull;
        int maxSitesLite;
        int maxSymbolSize;
        double defaultBeamwidth;
        int fontSize;
        int sectorFontSize;
        boolean scaleSymbols;
        String siteName;
        String sectorName;
        boolean normalSiteName;
        boolean sectorLabeling;
        boolean noSiteName;
        Font font;
        public int siteSize;

        public void reset(IStyleBlackboard style, Font font) {
            this.font = font;
            drawColor = Color.DARK_GRAY;
            siteColor = new Color(128, 128, 128, (int)(0.6 * 255.0));
            fillColor = new Color(255, 255, 128, (int)(0.6 * 255.0));
            labelColor = Color.DARK_GRAY;
            surroundColor = Color.WHITE;
            lineColor = Color.GRAY;
            drawFull = true;
            drawLite = true;
            drawLabels = true;
            drawSize = 15;
            alpha = (int)(0.6 * 255.0);
            ignoreTransp = true;
            maxSitesLabel = 30;
            maxSitesFull = 100;
            maxSitesLite = 1000;
            maxSymbolSize = 40;
            defaultBeamwidth = DEFAULT_BEAMWIDTH;
            fontSize = font.getSize();
            sectorFontSize = font.getSize();
            scaleSymbols = true;
            siteName = NeoStyleContent.DEF_MAIN_PROPERTY;
            sectorName = NeoStyleContent.DEF_SECONDARY_PROPERTY;
            NeoStyle neostyle = (NeoStyle)style.get(NeoStyleContent.ID);
            if (neostyle != null) {
                try {
                    siteColor = neostyle.getSiteFill();
                    fillColor = neostyle.getFill();
                    drawColor = neostyle.getLine();
                    labelColor = neostyle.getLabel();
                    float colSum = 0.0f;
                    for (float comp : labelColor.getRGBColorComponents(null)) {
                        colSum += comp;
                    }
                    if (colSum > 2.0) {
                        surroundColor = Color.DARK_GRAY;
                    } else {
                        surroundColor = Color.WHITE;
                    }
                    drawSize = neostyle.getSymbolSize();
                    alpha = 255 - (int)((double)neostyle.getSymbolTransparency() / 100.0 * 255.0);
                    ignoreTransp = neostyle.isIgnoreTransparency();
                    maxSitesLabel = neostyle.getLabeling();
                    maxSitesFull = neostyle.getSmallSymb();
                    maxSitesLite = neostyle.getSmallestSymb();
                    scaleSymbols = !neostyle.isFixSymbolSize();
                    maxSymbolSize = neostyle.getMaximumSymbolSize();
                    fontSize = neostyle.getFontSize();
                    sectorFontSize = neostyle.getSecondaryFontSize();
                    defaultBeamwidth = neostyle.getDefaultBeamwidth();
                    siteName = neostyle.getMainProperty();
                    sectorName = neostyle.getSecondaryProperty();
                } catch (Exception e) {
                    // TODO: we can get here if an old style exists, and we have added new fields
                }
            }
            normalSiteName = NeoStyleContent.DEF_MAIN_PROPERTY.equals(siteName);
            noSiteName = !normalSiteName && NeoStyleContent.DEF_SECONDARY_PROPERTY.equals(siteName);
            sectorLabeling = !NeoStyleContent.DEF_SECONDARY_PROPERTY.equals(sectorName);
            lineColor = changeColor(drawColor, alpha);
            siteColor = changeColor(siteColor, alpha);
            fillColor = changeColor(fillColor, alpha);
        }

        public void setNoScaling() {
            drawFull = false;
            drawLite = false;
            drawLabels = false;
        }

        public void setScaling(Envelope bounds_transformed, Envelope data_bounds, final IProgressMonitor monitor, long count) {
            double dataScaled = (bounds_transformed.getHeight() * bounds_transformed.getWidth())
                    / (data_bounds.getHeight() * data_bounds.getWidth());

            double countScaled = dataScaled * count;
            drawLabels = countScaled < maxSitesLabel;
            drawFull = countScaled < maxSitesFull;
            drawLite = countScaled > maxSitesLite;
            if (drawFull && scaleSymbols) {
                drawSize *= Math.sqrt(maxSitesFull) / (3 * Math.sqrt(countScaled));
                drawSize = Math.min(drawSize, maxSymbolSize);
            }
            // expand the boundary to include sites just out of view (so partial sectors can be see)
            bounds_transformed.expandBy(0.75 * (bounds_transformed.getHeight() + bounds_transformed.getWidth()));
        }

        public Color changeColor(Color color, int toAlpha) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), toAlpha);
        }
    }

    /**
     * This method is called to render data from the Neo4j 'GeoNeo' Geo-Resource.
     */
    private void renderGeoNeo(Graphics2D g, IGeoResource neoGeoResource, IProgressMonitor monitor) throws RenderException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        monitor.beginTask("render network sites and sectors: " + neoGeoResource.getIdentifier(), IProgressMonitor.UNKNOWN); // TODO:
                                                                                                                            // Get
                                                                                                                            // size
                                                                                                                            // from
                                                                                                                            // info

        GeoNeo geoNeo = null;

        // Setup default drawing parameters and thresholds (to be modified by style if found)
        drawHints.reset(getContext().getLayer().getStyleBlackboard(), g.getFont());
        g.setFont(drawHints.font.deriveFont((float)drawHints.fontSize));
        Map<Node, java.awt.Point> nodesMap = new HashMap<Node, java.awt.Point>();
        Map<Node, java.awt.Point> sectorMap = new HashMap<Node, java.awt.Point>();
        Map<Point, String> labelsMap = new HashMap<Point, String>();
        GraphDatabaseService neo = NeoServiceProviderUi.getProvider().getService();
        Transaction tx = neo.beginTx();
        NeoUtils.addTransactionLog(tx, Thread.currentThread(), "render Network");
        try {
            monitor.subTask("connecting");
            index=neo.index().forRelationships(INeoConstants.INDEX_REL_MULTY);
            geoNeo = neoGeoResource.resolve(GeoNeo.class, new SubProgressMonitor(monitor, 10));
            graphModel = geoNeo.getGraphModel();
            LOGGER.debug("NetworkRenderer resolved geoNeo '" + geoNeo.getName() + "' from resource: "
                    + neoGeoResource.getIdentifier());
            filterSectors = FilterUtil.getFilterOfData(geoNeo.getMainGisNode(), neo);
            filterSites = FilterUtil.getFilterOfData(
                    geoNeo.getMainGisNode().getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)
                            .getOtherNode(geoNeo.getMainGisNode()), neo);
            String starProperty = getSelectProperty(geoNeo);
            Pair<Point, Long> starPoint = getStarPoint();
            Node starNode = null;
            if (starPoint != null) {
                LOGGER.debug("Have star selection: " + starPoint);
            }
            ArrayList<Pair<String, Integer>> multiOmnis = new ArrayList<Pair<String, Integer>>();
            aggNode = geoNeo.getAggrNode();
            setCrsTransforms(neoGeoResource.getInfo(null).getCRS());
            Envelope bounds_transformed = getTransformedBounds();
            Envelope data_bounds = geoNeo.getBounds();
            if (bounds_transformed == null) {
                drawHints.setNoScaling();
            } else if (data_bounds != null && data_bounds.getHeight() > 0 && data_bounds.getWidth() > 0) {
                long count = geoNeo.getCount();
                if (NeoLoaderPlugin.getDefault().getPreferenceStore().getBoolean(DataLoadPreferences.NETWORK_COMBINED_CALCULATION)) {
                    double density = getAverageDensity(monitor);
                    if (density > 0)
                        count = (long)(density * data_bounds.getHeight() * data_bounds.getWidth());
                }
                drawHints.setScaling(bounds_transformed, data_bounds, monitor, count);
            }

            g.setColor(drawHints.drawColor);
            int count = 0;
            monitor.subTask("drawing");
            Coordinate world_location = new Coordinate(); // single object for re-use in transform
                                                          // below (minimize object creation)

            // draw selection
            java.awt.Point prev_p = null;
            ArrayList<Node> selectedPoints = new ArrayList<Node>();
            final Set<Node> selectedNodes = new HashSet<Node>(geoNeo.getSelectedNodes());

            final ReturnableEvaluator returnableEvaluator = new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    final Object property = currentPos.currentNode().getProperty("type", "");
                    return "site".equals(property) || "probe".equals(property);
                }
            };
            for (Node node : selectedNodes) {
                final String nodeType = NeoUtils.getNodeType(node, "");
                if ("network".equals(nodeType)) {
                    // Select all 'site' nodes in that file
                    for (Node rnode : node.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, returnableEvaluator,
                            GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING, NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                        selectedPoints.add(rnode);
                    }
                } else if ("city".equals(nodeType) || "bsc".equals(nodeType)) {
                    for (Node rnode : node.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, returnableEvaluator,
                            NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                        selectedPoints.add(rnode);
                    }
                } else {
                    // Traverse backwards on CHILD relations to closest 'mp' Point
                    for (@SuppressWarnings("unused")
                    Node rnode : node.traverse(Order.DEPTH_FIRST, new StopEvaluator() {
                        @Override
                        public boolean isStopNode(TraversalPosition currentPos) {
                            return "site".equals(currentPos.currentNode().getProperty("type", ""));
                        }
                    }, new ReturnableEvaluator() {

                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            return "site".equals(currentPos.currentNode().getProperty("type", ""));
                        }
                    }, NetworkRelationshipTypes.CHILD, Direction.INCOMING)) {
                        selectedPoints.add(rnode);
                        break;
                    }
                }
            }
            // Now draw the selected points highlights
            // TODO remove double selection?
            for (Node rnode : selectedPoints) {
                GeoNode node = new GeoNode(rnode);
                Coordinate location = node.getCoordinate();
                if (location == null) {
                    continue;
                }
                if (bounds_transformed != null && !bounds_transformed.contains(location)) {
                    continue; // Don't draw points outside viewport
                }
                if (filterSites != null) {
                    if (!filterSites.filterNode(node.getNode()).isValid()) {
                        continue;
                    }
                }
                try {
                    JTS.transform(location, world_location, transform_d2w);
                } catch (Exception e) {
                    continue;
                }
                java.awt.Point p = getContext().worldToPixel(world_location);
                if (prev_p != null && prev_p.x == p.x && prev_p.y == p.y) {
                    prev_p = p;
                    continue;
                } else {
                    prev_p = p;
                }
                renderSelectionGlow(g, p, drawHints.drawSize * 4);
            }
            g.setColor(drawHints.drawColor);
            long startTime = System.currentTimeMillis();
            for (GeoNode node : geoNeo.getGeoNodes(bounds_transformed)) {
                if (filterSites != null) {
                    if (!filterSites.filterNode(node.getNode()).isValid()) {
                        continue;
                    }
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
                Color borderColor = g.getColor();
                boolean selected = false;
                if (geoNeo.getSelectedNodes().contains(node.getNode())) {
                    borderColor = COLOR_SITE_SELECTED;
                    // if selection exist - do not necessary to select node again
                    selected = false;
                } else {
                    // if selection exist - do not necessary to select node again
                    selected = !selectedPoints.contains(node.getNode());
                    // this selection was already checked
                    // for (Node rnode:node.getNode().traverse(Traverser.Order.BREADTH_FIRST,
                    // StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                    // NetworkRelationshipTypes.CHILD, Direction.BOTH)){
                    // if (geoNeo.getSelectedNodes().contains(rnode)) {
                    // selected = true;
                    // break;
                    // }
                    // }
                    if (selected) {
                        selected = false;
                        DELTA_LOOP: for (Node rnode : node.getNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE,
                                ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.MISSING, Direction.INCOMING,
                                NetworkRelationshipTypes.DIFFERENT, Direction.INCOMING)) {
                            if (geoNeo.getSelectedNodes().contains(rnode)) {
                                selected = true;
                                break;
                            } else {
                                for (Node xnode : rnode.traverse(Order.BREADTH_FIRST, new StopEvaluator() {

                                    @Override
                                    public boolean isStopNode(TraversalPosition currentPos) {
                                        return "delta_report".equals(currentPos.currentNode().getProperty("type", ""));
                                    }
                                }, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.CHILD, Direction.INCOMING)) {
                                    if (geoNeo.getSelectedNodes().contains(xnode)) {
                                        selected = true;
                                        break DELTA_LOOP;
                                    }
                                }
                            }
                        }
                    }
                }
                renderSite(g, p, borderColor, getSinngleColor(node.getNode(), drawHints.siteColor), selected);
                nodesMap.put(node.getNode(), p);
                if (drawHints.drawFull) {
                    int countOmnis = 0;
                    double[] label_position_angles = new double[] {0, 90};
                    try {
                        int s = 0;
                        for (Relationship relationship : node.getNode().getRelationships(NetworkRelationshipTypes.CHILD,
                                Direction.OUTGOING)) {
                            Node child = relationship.getEndNode();
                            if (child.hasProperty("type") && child.getProperty("type").toString().equals("sector")) {
                                Double azimuth = getDouble(child, "azimuth", Double.NaN);
                                Double beamwidth = Double.NaN;
                                if (azimuth.equals(Double.NaN)) {
                                    beamwidth = getDouble(child, "beamwidth", CIRCLE_BEAMWIDTH);
                                    if (beamwidth < CIRCLE_BEAMWIDTH) {
                                        azimuth = 0.0;
                                        System.err.println("Error in render GeoNeo: azimuth is defined, but beamwidth less than "
                                                + CIRCLE_BEAMWIDTH);
                                    }
                                } else {
                                    beamwidth = getDouble(child, "beamwidth", drawHints.defaultBeamwidth);
                                }
                                 Set<Color> colorsToFill = getSectorColors(child, drawHints.fillColor);
                                borderColor = drawHints.drawColor;
                                if (starPoint != null && starPoint.right().equals(child.getId())) {
                                    borderColor = COLOR_SECTOR_STAR;
                                    starNode = child;
                                } else if (geoNeo.getSelectedNodes().contains(child)) {
                                    borderColor = COLOR_SECTOR_SELECTED;
                                }
                                // put sector information in to blackboard
                                if (filterSectors != null) {
                                    if (!filterSectors.filterNode(child).isValid()) {
                                        continue;
                                    }
                                }
                                Pair<Point, Point> centerPoint = renderSector(g, p, azimuth, beamwidth, colorsToFill, borderColor);
                                nodesMap.put(child, centerPoint.getLeft());
                                if (drawHints.sectorLabeling) {
                                    sectorMap.put(child, centerPoint.getRight());
                                }
                                if (s < label_position_angles.length) {
                                    label_position_angles[s] = azimuth;
                                }
                                // g.setColor(drawColor);
                                // g.rotate(-Math.toRadians(beamwidth/2));
                                // g.drawString(sector.getString("name"),drawSize,0);
                                if (beamwidth == CIRCLE_BEAMWIDTH)
                                    countOmnis++;
                                s++;
                            }
                        }
                    } finally {
                        if (base_transform != null) {
                            // recover the normal transform
                            g.setTransform(base_transform);
                            g.setColor(drawHints.drawColor);
                        }
                    }
                    if (base_transform != null) {
                        g.setTransform(base_transform);
                    }
                    String drawString = getSiteName(node);
                    if (countOmnis > 1) {
                        // System.err.println("Site "+node+" had "+countOmnis+" omni antennas");
                        multiOmnis.add(new Pair<String, Integer>(drawString, countOmnis));
                    }
                    if (drawHints.drawLabels) {
                        labelsMap.put(p, drawString);
                    }
                }
                monitor.worked(1);
                count++;
                if (monitor.isCanceled())
                    break;
            }
            if (drawHints.drawLabels && labelsMap.size() > 0) {
                Set<Rectangle> labelRec = new HashSet<Rectangle>();
                FontMetrics metrics = g.getFontMetrics(drawHints.font);
                // get the height of a line of text in this font and render context
                int hgt = metrics.getHeight();
                for (Point p : labelsMap.keySet()) {
                    String drawString = labelsMap.get(p);
                    int label_x = drawHints.drawSize > 15 ? 15 : drawHints.drawSize;
                    int label_y = hgt / 3;
                    p = new Point(p.x + label_x, p.y + label_y);

                    // get the advance of my text in this font and render context
                    int adv = metrics.stringWidth(drawString);
                    // calculate the size of a box to hold the text with some padding.
                    Rectangle rect = new Rectangle(p.x - 1, p.y - hgt + 1, adv + 2, hgt + 2);
                    boolean drawsLabel = findNonOverlapPosition(labelRec, hgt, p, rect);
                    if (drawsLabel && !drawString.isEmpty()) {
                        labelRec.add(rect);
                        drawLabel(g, p, drawString);
                    }
                }
                // draw sector name
                if (drawHints.sectorLabeling) {
                    Font fontOld = g.getFont();
                    Font fontSector = fontOld.deriveFont((float)drawHints.sectorFontSize);
                    g.setFont(fontSector);
                    FontMetrics metric = g.getFontMetrics(fontSector);
                    hgt = metrics.getHeight();
                    int h = hgt / 3;
                    for (Node sector : sectorMap.keySet()) {
                        String name = getSectorName(sector);
                        if (name.isEmpty()) {
                            continue;
                        }
                        int w = metric.stringWidth(name);
                        Point pSector = nodesMap.get(sector);
                        Point endLine = sectorMap.get(sector);

                        // calculate p
                        int x = (endLine.x < pSector.x) ? endLine.x - w : endLine.x;
                        int y = (endLine.y < pSector.y) ? endLine.y - h : endLine.y;
                        Point p = new Point(x, y);
                        // get the advance of my text in this font and render context
                        int adv = metrics.stringWidth(name);
                        // calculate the size of a box to hold the text with some padding.
                        Rectangle rect = new Rectangle(p.x - 1, p.y - hgt + 1, adv + 2, hgt + 2);
                        boolean drawsLabel = findNonOverlapPosition(labelRec, hgt, p, rect);
                        if (drawsLabel) {
                            labelRec.add(rect);
                            drawLabel(g, p, name);
                        }

                    }
                    g.setFont(fontOld);
                }
            }
            if (multiOmnis.size() > 0) {
                // TODO: Move this to utility class
                StringBuffer sb = new StringBuffer();
                int llen = 0;
                for (Pair<String, Integer> pair : multiOmnis) {
                    if (sb.length() > 1)
                        sb.append(", ");
                    else
                        sb.append("\t");
                    if (sb.length() > 100 + llen) {
                        llen = sb.length();
                        sb.append("\n\t");
                    }
                    sb.append(pair.left()).append(":").append(pair.right());
                    if (sb.length() > 1000)
                        break;
                }
                System.err.println("There were " + multiOmnis.size() + " sites with more than one omni antenna: ");
                System.err.println(sb.toString());
            }
            if (starNode != null && starProperty != null) {
                drawAnalyser(g, starNode, starPoint.left(), starProperty, nodesMap);
            }

            if (graphModel == null) {
                String neiName = (String)geoNeo.getProperties(GeoNeo.NEIGH_NAME);
                if (neiName != null) {
                    Object properties = geoNeo.getProperties(GeoNeo.NEIGH_RELATION);
                    if (properties != null) {
                        drawRelation(g, (Relationship)properties, drawHints.lineColor, nodesMap);
                    }
                    properties = geoNeo.getProperties(GeoNeo.NEIGH_MAIN_NODE);
                    Object type = geoNeo.getProperties(GeoNeo.NEIGH_TYPE);
                    if (properties != null) {
                        drawNeighbour(g, neiName, (Node)properties, drawHints.lineColor, nodesMap, type);
                    }
                }
            } else {
                drawRelations(g, graphModel, nodesMap, drawHints.lineColor);
            }
            LOGGER.debug("Network renderer took " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s to draw " + count
                    + " sites from " + neoGeoResource.getIdentifier());
            tx.success();
        } catch (TransformException e) {
            throw new RenderException(e);
        } catch (FactoryException e) {
            throw new RenderException(e);
        } catch (IOException e) {
            throw new RenderException(e); // rethrow any exceptions encountered
        } finally {
            if (neoGeoResource != null) {
                HashMap<Long, Point> idMap = new HashMap<Long, Point>();
                for (Node node : nodesMap.keySet()) {
                    idMap.put(node.getId(), nodesMap.get(node));
                }
                getContext().getMap().getBlackboard().put(BLACKBOARD_NODE_LIST, idMap);
            }
            // if (geoNeo != null)
            // geoNeo.close();
            monitor.done();
            tx.finish();
        }
    }

    /**
     * @param g
     * @param graphModel2
     * @param nodesMap
     * @param lineColor
     */
    private void drawRelations(Graphics2D g, IGraphModel graphModel, Map<Node, Point> nodesMap, Color lineColor) {
//        g.setColor(lineColor);
        Stroke oldStr = g.getStroke();
        g.setStroke(new BasicStroke(2));
        
        for (Entry<Node, Set<Node>> entry : graphModel.getOutgoingRelationMap().entrySet()) {
            Point from = nodesMap.get(entry.getKey());
            if (from == null) {
                continue;
            }
            for (Node neighNode : entry.getValue()) {
                RGB color = graphModel.getRelationColor(entry.getKey(), neighNode);
                Color clr=color==null?lineColor: new Color(color.red, color.green, color.blue, drawHints.alpha);
                g.setColor(clr);
                Point to = nodesMap.get(neighNode);
                if (to != null) {
                    // g.drawLine(from.x, from.y, to.x, to.y);
                    drawArrow(g, from.x, from.y, to.x, to.y);
                }
            }
        }
        g.setStroke(oldStr);
    }

    @SuppressWarnings("unchecked")
    private Pair<Point, Long> getStarPoint() {
        Pair<Point, Long> starPoint = (Pair<Point, Long>)getContext().getLayer().getBlackboard().get(BLACKBOARD_START_ANALYSER);
        return starPoint;
    }

    private void drawLabel(Graphics2D g, Point p, String drawString) {
        TextLayout text = new TextLayout(drawString, g.getFont(), g.getFontRenderContext());
        AffineTransform at = AffineTransform.getTranslateInstance(p.x, p.y);
        Shape outline = text.getOutline(at);
        drawSoftSurround(g, outline);
        g.setPaint(drawHints.surroundColor);
        g.fill(outline);
        g.draw(outline);
        g.setPaint(drawHints.labelColor);
        text.draw(g, p.x, p.y);
    }

    private boolean findNonOverlapPosition(Set<Rectangle> labelRec, int hgt, Point p, Rectangle rect) {
        boolean drawLabel = true;
        if (!labelSafe(rect, labelRec)) {
            drawLabel = false;
            Rectangle tryRect = new Rectangle(rect);
            RECT: for (int tries : new int[] {1, -1}) {
                for (int shift = 0; shift != tries * (2 * hgt); shift += tries) {
                    tryRect.setLocation(rect.x, rect.y + shift);
                    if (labelSafe(tryRect, labelRec)) {
                        drawLabel = true;
                        p.y = p.y + shift;
                        rect.setLocation(tryRect.getLocation());
                        break RECT;
                    }
                }
            }
        }
        return drawLabel;
    }

    private double getDouble(Node node, String property, double def) {
        Object result = node.getProperty(property, def);
        if (result instanceof Integer) {
            return ((Integer)result).doubleValue();
        } else if (result instanceof Float) {
            return ((Float)result).doubleValue();
        } else if (result instanceof String) {
            return Double.parseDouble((String)result);
        } else {
            return (Double)result;
        }
    }

    /**
     * @param sector
     * @return
     */
    private String getSectorName(Node sector) {
        return sector.getProperty(drawHints.sectorName, "").toString();
    }

    /**
     * Gets Site name
     * 
     * @param node geo node
     * @return site name
     */
    private String getSiteName(GeoNode node) {
        return drawHints.normalSiteName ? node.toString() : drawHints.noSiteName ? "" : node.getNode()
                .getProperty(drawHints.siteName, "").toString();
    }

    private void drawSoftSurround(Graphics2D g, Shape outline) {
        g.setPaint(drawHints.changeColor(drawHints.surroundColor, 128));
        g.translate(1, 0);
        g.fill(outline);
        g.draw(outline);
        g.translate(-1, 1);
        g.fill(outline);
        g.draw(outline);
        g.translate(-1, -1);
        g.fill(outline);
        g.draw(outline);
        g.translate(1, -1);
        g.fill(outline);
        g.draw(outline);
        g.translate(0, 1);
    }

    private boolean labelSafe(Rectangle rect, Set<Rectangle> labelRectangles) {
        for (Rectangle rectangle : labelRectangles) {
            if (rectangle.intersects(rect)) {
                return false;
            }
        }
        return true;
    }

    /**
     * draws neighbour relations
     * 
     * @param g Graphics2D
     * @param neiName name of neighbour list
     * @param node serve node
     * @param lineColor - line color
     * @param nodesMap map of nodes
     * @param type
     */
    private void drawNeighbour(Graphics2D g, String neiName, Node node, Color lineColor, Map<Node, Point> nodesMap, Object type) {
        g.setColor(lineColor);
        Point point1 = nodesMap.get(node);
        Node proxyServeNode = NeoUtils.getProxySector(node, neiName);
        NetworkSiteType siteType = (NetworkSiteType)type;
        if (point1 != null && proxyServeNode != null) {
            for (RelationshipType relType : new RelationshipType[] {NetworkRelationshipTypes.NEIGHBOUR,
                    NetworkRelationshipTypes.INTERFERS}) {
                RelationshipType listRelType = relType == NetworkRelationshipTypes.NEIGHBOUR ? NetworkRelationshipTypes.NEIGHBOURS
                        : NetworkRelationshipTypes.INTERFERENCE;
                for (Relationship relation : NeoUtils.getNeighbourRelations(proxyServeNode, neiName, relType)) {
                    Node proxyNeighNode = relation.getOtherNode(proxyServeNode);
                    final Node neighNode = NeoUtils.getNodeFromProxy(proxyNeighNode, listRelType);
                    if (siteType != null) {
                        if (!siteType.checkNode(NeoUtils.getParent(null, neighNode), null)) {
                            continue;
                        }
                    }
                    Point point2 = nodesMap.get(neighNode);
                    if (point2 != null) {
                        g.drawLine(point1.x, point1.y, point2.x, point2.y);
                    }
                }
            }
            for (Relationship relation : NeoUtils.getTransmissionRelations(proxyServeNode, neiName)) {
                Node proxyTransNode = relation.getOtherNode(proxyServeNode);
                final Node transNode = NeoUtils.getNodeFromProxy(proxyTransNode, NetworkRelationshipTypes.TRANSMISSIONS);
                Point point2 = nodesMap.get(transNode);
                if (point2 != null) {
                    g.drawLine(point1.x, point1.y, point2.x, point2.y);
                }
            }
        }
    }

    private final int ARR_SIZE = 4;

    void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int)Math.sqrt(dx * dx + dy * dy);
        AffineTransform oldTr = g.getTransform();

        g.translate(x1, y1);
        g.rotate(angle);
        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, (int)len, 0);
        g.fillPolygon(new int[] {len, len - ARR_SIZE, len - ARR_SIZE, len}, new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
        g.setTransform(oldTr);
    }

    /**
     * draws neighbour relation
     * 
     * @param g Graphics2D
     * @param relation relation
     * @param lineColor - line color
     * @param nodesMap map of nodes
     */
    private void drawRelation(Graphics2D g, Relationship relation, Color lineColor, Map<Node, Point> nodesMap) {
        g.setColor(lineColor);
        Point point1 = nodesMap.get(relation.getStartNode());
        NetworkRelationshipTypes proxyRelation = relation.isType(NetworkRelationshipTypes.TRANSMISSION)
                ? NetworkRelationshipTypes.TRANSMISSIONS : NetworkRelationshipTypes.NEIGHBOURS;
        if (point1 == null) {
            Node servNode = NeoUtils.getNodeFromProxy(relation.getStartNode(), proxyRelation);
            point1 = nodesMap.get(servNode);
        }
        Point point2 = nodesMap.get(relation.getEndNode());
        if (point2 == null) {
            Node transNode = NeoUtils.getNodeFromProxy(relation.getEndNode(), proxyRelation);
            point2 = nodesMap.get(transNode);
        }
        if (point1 != null && point2 != null) {
            g.drawLine(point1.x, point1.y, point2.x, point2.y);
        }
    }

    /**
     * gets average count of geoNeo.getCount() from all resources in map
     * 
     * @param data_bounds
     * @return average count
     */
    private double getAverageDensity(IProgressMonitor monitor) {
        double result = 0;
        long count = 0;
        try {
            for (ILayer layer : getContext().getMap().getMapLayers()) {
                if (layer.getGeoResource().canResolve(GeoNeo.class)) {
                    GeoNeo resource = layer.getGeoResource().resolve(GeoNeo.class, monitor);
                    Envelope dbounds = resource.getBounds();
                    if (dbounds != null && resource.getGisType().equals(GisTypes.NETWORK)) {
                        result += resource.getCount() / (dbounds.getHeight() * dbounds.getWidth());
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            // TODO Handle IOException
            NeoCorePlugin.error(e.getLocalizedMessage(), e);
            return 0;
        }
        return result / (double)count;
    }

    /**
     * gets sector color
     * 
     * @param child - sector node
     * @param defColor - default value
     * @return color
     */
    private Color getSinngleColor(Node node, Color defColor) {
        if (graphModel != null) {
            RGB result = graphModel.getColor(node);
            if (result != null) {
                return new Color(result.red, result.green, result.blue, drawHints.alpha);
            }
        }
            if (aggNode == null) {
                return defColor;
            }
            Relationship rel=index.get("aggType", aggNode.getId(),null,node).getSingle();
            if (rel!=null){
                Node chartNode=rel.getOtherNode(node);
                final Integer rgb = (Integer)chartNode.getProperty(INeoConstants.AGGREGATION_COLOR, defColor.getRGB());
                Color clr;
                if (drawHints.ignoreTransp) {
                    clr= new Color(rgb);
                } else {
                    clr= new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 0) & 0xFF, drawHints.alpha);
                }      
                return clr;
            }
            Node chartNode = NeoUtils.getChartNode(node, aggNode);
            if (chartNode == null) {
                return defColor;
            }
            final Integer rgb = (Integer)chartNode.getProperty(INeoConstants.AGGREGATION_COLOR, defColor.getRGB());
            if (drawHints.ignoreTransp) {
                return new Color(rgb);
            } else {
                return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 0) & 0xFF, drawHints.alpha);

            }
    }
    private Set<Color> getSectorColors(Node node, Color defColor) {
        Set<Color> results=new LinkedHashSet<Color>();
        if (graphModel != null) {
            RGB result = graphModel.getColor(node);
            if (result != null) {
                results.add( new Color(result.red, result.green, result.blue, drawHints.alpha));
                return results;
            }
        }
            if (aggNode == null) {
                results.add(defColor);
                return results;
            }
            for (Relationship rel:index.get("aggType", aggNode.getId(),null,node)){
                Node chartNode=rel.getOtherNode(node);
                final Integer rgb = (Integer)chartNode.getProperty(INeoConstants.AGGREGATION_COLOR, defColor.getRGB());
                Color clr;
                if (drawHints.ignoreTransp) {
                    clr= new Color(rgb);
                } else {
                    clr= new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 0) & 0xFF, drawHints.alpha);
                }      
                results.add(clr);
            }
            if (!results.isEmpty()){
                return results;
            }
            Node chartNode = NeoUtils.getChartNode(node, aggNode);
            if (chartNode == null) {
                results.add(defColor);
                return results;
            }
            final Integer rgb = (Integer)chartNode.getProperty(INeoConstants.AGGREGATION_COLOR, defColor.getRGB());
            Color clr;
            if (drawHints.ignoreTransp) {
                clr= new Color(rgb);
            } else {
                clr= new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 0) & 0xFF, drawHints.alpha);
            }
            results.add(clr);
            return results;
    }
    /**
     * Render the sector symbols based on the point and azimuth. We simply save the graphics
     * transform, then modify the graphics through the appropriate transformations (origin to site,
     * and rotations for drawing the lines and arcs).
     * 
     * @param g
     * @param p
     * @param azimuth
     */
    private Pair<java.awt.Point, java.awt.Point> renderSector(Graphics2D g, java.awt.Point p, double azimuth, double beamwidth,
            Set<Color> colorsToFill, Color borderColor) {
        /* for testing
        colorsToFill.add(Color.GREEN);
        colorsToFill.add(Color.ORANGE);
        */
//        int drawSize = drawHints.drawSize;
        Color oldColor = g.getColor();
        Pair<java.awt.Point, java.awt.Point> result = null;
        if (base_transform == null)
            base_transform = g.getTransform();
        if (beamwidth < 10){
            beamwidth = 10;
        }else if (beamwidth>CIRCLE_BEAMWIDTH){
            beamwidth=CIRCLE_BEAMWIDTH;
        }
        g.setTransform(base_transform);
//        g.translate(p.x, p.y);
//        int draw2 = drawSize + 3;
        double h=(double)drawHints.drawSize/2;
        double r1=0.0;
        int i=0;
        double angle1 = 90 - azimuth - beamwidth / 2.0;
        double angle2 = angle1+beamwidth;
        Arc2D a=null;
        for (Color color:colorsToFill){
            double r2=r1+2d/(2+i)*h;
            i++;

            GeneralPath path = new GeneralPath();
            a = new Arc2D.Double();
            a.setArcByCenter(p.x, p.y, r1, angle2, -beamwidth, Arc2D.OPEN);
            path.append(a.getPathIterator(null), true);
            a.setArcByCenter(p.x, p.y, r2, angle1, beamwidth, Arc2D.OPEN);
            path.append(a.getPathIterator(null), true);
            path.closePath();
            g.setColor(color);
            g.fill(path);
            g.setColor(borderColor);
            g.draw(path);
            r1=r2;
        }
        g.setColor(oldColor);
        java.awt.Point right = new java.awt.Point();
        right.setLocation(a.getCenterX(), a.getCenterY());
        return new Pair<java.awt.Point, java.awt.Point>(p, right);
//        if (beamwidth >= CIRCLE_BEAMWIDTH) {
//            g.setColor(colorToFill);
//            g.fillOval(-drawSize, -drawSize, 2 * drawSize, 2 * drawSize);
//            g.setColor(borderColor);
//            g.drawOval(-drawSize, -drawSize, 2 * drawSize, 2 * drawSize);
//            result = new Pair<java.awt.Point, java.awt.Point>(p, new java.awt.Point(p.x + draw2, p.y));
//
//        } else {
//            double angdeg = -90 + azimuth - beamwidth / 2.0;
//            g.rotate(Math.toRadians(angdeg));
//            g.setColor(colorToFill);
//            g.fillArc(-drawSize, -drawSize, 2 * drawSize, 2 * drawSize, 0, -(int)beamwidth);
//            // TODO correct gets point
//
//            g.setColor(borderColor);
//            g.drawArc(-drawSize, -drawSize, 2 * drawSize, 2 * drawSize, 0, -(int)beamwidth);
//            g.drawLine(0, 0, drawSize, 0);
//            g.rotate(Math.toRadians(beamwidth / 2));
//            double xLoc = drawSize / 2;
//            double yLoc = 0;
//            AffineTransform transform = g.getTransform();
//            int x = (int)(transform.getScaleX() * xLoc + transform.getShearX() * yLoc + transform.getTranslateX());
//            int y = (int)(transform.getShearY() * xLoc + transform.getScaleY() * yLoc + transform.getTranslateY());
//
//            int x2 = (int)(transform.getScaleX() * draw2 + transform.getShearX() * yLoc + transform.getTranslateX());
//            int y2 = (int)(transform.getShearY() * draw2 + transform.getScaleY() * yLoc + transform.getTranslateY());
//
//            g.rotate(Math.toRadians(beamwidth / 2));
//            g.drawLine(0, 0, drawSize, 0);
//            Point result1 = new java.awt.Point(x, y);
//            Point result2 = new java.awt.Point(x2, y2);
//            result = new Pair<java.awt.Point, java.awt.Point>(result1, result2);
//
//            g.setColor(oldColor);
//        }
//        return result;
    }

    /**
     * This one is very simple, just draw a circle at the site location.
     * 
     * @param g
     * @param p
     * @param borderColor
     * @param drawSize
     */
    private void renderSite(Graphics2D g, java.awt.Point p, Color borderColor, Color fillColor, boolean selected) {
        Color oldColor = g.getColor();
        int drawSize = drawHints.drawSize;
        if (drawHints.drawFull) {
            if (selected)
                renderSelectionGlow(g, p, drawSize * 4);
            drawSize /= 4;
            if (drawSize < 2)
                drawSize = 2;
            drawHints.siteSize=drawSize;
            g.setColor(fillColor);
            g.fillOval(p.x - drawSize, p.y - drawSize, 2 * drawSize, 2 * drawSize);
            g.setColor(borderColor);
            g.drawOval(p.x - drawSize, p.y - drawSize, 2 * drawSize, 2 * drawSize);
        } else if (drawHints.drawLite) {
            if (selected)
                renderSelectionGlow(g, p, 10);
            g.setColor(borderColor);
            g.drawRect(p.x - 1, p.y - 1, 2, 2);
        } else {
            if (selected)
                renderSelectionGlow(g, p, 20);
            g.setColor(borderColor);
            g.drawOval(p.x - 5, p.y - 5, 10, 10);
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
        Color highColor = new Color(COLOR_SITE_SELECTED.getRed(), COLOR_SITE_SELECTED.getGreen(), COLOR_SITE_SELECTED.getBlue(), 8);
        g.setColor(highColor);
        for (; drawSize > 2; drawSize *= 0.8) {
            g.fillOval(p.x - drawSize, p.y - drawSize, 2 * drawSize, 2 * drawSize);
        }
    }

    /**
     * perform and draw star analyser
     * 
     * @param context context
     */
    private void drawAnalyser(Graphics2D g, Node mainNode, Point starPoint, String property, Map<Node, Point> nodesMap) {
        Transaction tx = NeoServiceProviderUi.getProvider().getService().beginTx();
        try {
            if (aggNode == null) {
                return;
            }
            Node chart = NeoUtils.getChartNode(mainNode, aggNode);
            if (chart == null) {
                return;
            }
            Point point = nodesMap.get(mainNode);
            if (point != null) {
                starPoint = point;
            }
            drawMainNode(g, mainNode, starPoint);
            for (Relationship relation : chart.getRelationships(NetworkRelationshipTypes.AGGREGATE, Direction.OUTGOING)) {
                Node node = relation.getOtherNode(chart);
                Point nodePoint = nodesMap.get(node);
                if (nodePoint != null) {
                    g.setColor(getLineColor(mainNode, node));
                    g.drawLine(starPoint.x, starPoint.y, nodePoint.x, nodePoint.y);
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
     * @param geoNeo
     * @return property name
     */
    private String getSelectProperty(GeoNeo geoNeo) {
        String result = null;
        Transaction transaction = NeoServiceProviderUi.getProvider().getService().beginTx();
        try {
            Node dataset = Utils.getDatasetNodeByGis(geoNeo.getMainGisNode());
            result = dataset.getProperty(INeoConstants.PROPERTY_SELECTED_AGGREGATION, "").toString();

            if (result.equals("")) {
                result = checkCorrelated(geoNeo.getMainGisNode());
            }

            transaction.success();
        } finally {
            transaction.finish();
        }
        return result == null || result.isEmpty() ? null : result;
    }

    private String checkCorrelated(Node root) {
        Relationship correlationLink = root.getSingleRelationship(CorrelationRelationshipTypes.CORRELATION, Direction.OUTGOING);

        if (correlationLink != null) {
            Iterator<Relationship> correlatedNodes = correlationLink.getEndNode()
                    .getRelationships(CorrelationRelationshipTypes.CORRELATED, Direction.INCOMING).iterator();

            while (correlatedNodes.hasNext()) {
                Node correlatedNode = correlatedNodes.next().getStartNode();
                if (correlatedNode.hasProperty(INeoConstants.PROPERTY_SELECTED_AGGREGATION)) {
                    return (String)correlatedNode.getProperty(INeoConstants.PROPERTY_SELECTED_AGGREGATION);
                }
            }
        }

        return "";
    }

    /**
     * Draw selection of main node
     * 
     * @param context context
     * @param mainNiode main node
     */
    private void drawMainNode(Graphics2D g, Node mainNode, Point point) {
        g.setColor(Color.RED);
        g.fillOval(point.x - 4, point.y - 4, 10, 10);
    }

    @Override
    public void render(IProgressMonitor monitor) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

}
