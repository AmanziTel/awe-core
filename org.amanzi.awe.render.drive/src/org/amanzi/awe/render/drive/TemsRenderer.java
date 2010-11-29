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
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;
import net.refractions.udig.ui.PlatformGIS;

import org.amanzi.awe.catalog.neo.GeoConstant;
import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.catalog.neo.GeoNeo.GeoNode;
import org.amanzi.awe.filters.AbstractFilter;
import org.amanzi.awe.filters.FilterUtil;
import org.amanzi.awe.filters.experimental.CompositeFilter;
import org.amanzi.awe.filters.experimental.Filter;
import org.amanzi.awe.filters.experimental.GroupFilter;
import org.amanzi.awe.filters.experimental.IFilter;
import org.amanzi.awe.neostyle.NeoStyle;
import org.amanzi.awe.neostyle.NeoStyleContent;
import org.amanzi.awe.neostyle.ShapeType;
import org.amanzi.neo.core.utils.DriveEvents;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.CorrelationRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.indexes.PropertyIndex.NeoIndexRelationshipTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.ui.enums.ColoredFlags;
import org.amanzi.neo.services.utils.Pair;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.feature.Feature;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.index.lucene.LuceneIndexService;
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
    private static final Logger LOGGER = Logger.getLogger(TemsRenderer.class);
    private MathTransform transform_d2w;
    private MathTransform transform_w2d;
    private AffineTransform base_transform = null; // save original graphics transform for repeated
    // re-use
    private Color drawColor = Color.BLACK;
    private Color fillColor = new Color(200, 128, 255, (int)(0.6 * 255.0));
    private Color labelColor = Color.DARK_GRAY;
    private Node aggNode;
    private String mpName;
    private String msName;
    private boolean normalSiteName;
    private boolean notMsLabel;
    private int eventIconSize;
    private int eventIconOffset;
    private boolean scale = false;
    private int eventIconBaseSize = 12;
    private int eventIconMaxSize = 32;
    private static final int[] eventIconSizes = new int[] {6, 8, 12, 16, 32, 48, 64};
    private static final Color COLOR_HIGHLIGHTED = Color.CYAN;;
    private static final Color COLOR_HIGHLIGHTED_SELECTED = Color.RED;
    private static final Color FADE_LINE = new Color(127, 127, 127, 127);
    private AbstractFilter filterMp;

    private final LuceneIndexService index;
    private boolean notMpLabel;
    private int[] xPoints;
    private int[] yPoints;
    private boolean changeTransp;
    private HashMap<Long,java.awt.Point> sectorPoints = new HashMap<Long,java.awt.Point>();
    private HashMap<String,java.awt.Point[]> driveSectorCorrelationLines = new HashMap<String,java.awt.Point[]>();
    private ArrayList<IGeoResource> networkGeoResources;
    private IGeoResource networkGeoResource;
    private CoordinateReferenceSystem networkCRS;
    private java.awt.Point previousPoint;

    private static int getIconSize(int size) {
        int lower = eventIconSizes[0];
        for (int s : eventIconSizes) {
            if (size > s) {
                lower = s;
            }
        }
        return lower;
    }

    private static int calcIconSize(int min, int max, int minT, int maxT, double count) {
        int iconSize = min;
        try {
            double ratio = (maxT - count) / (maxT - minT);
            iconSize = min + (int)(ratio * (max - min));
        } catch (Exception e) {
            LOGGER.debug("Error calculating icons sizes: " + e);
        }
        return getIconSize(iconSize);
    }

    public TemsRenderer() {
        index = NeoServiceProviderUi.getProvider().getIndexService();
    }

    @Override
    public void render(Graphics2D destination, IProgressMonitor monitor) throws RenderException {
        try {
            ILayer layer = getContext().getLayer();
            // Are there any resources in the layer that respond to the GeoNeo class (should be the case
            // if we found a Neo4J database with GeoNeo data)
            IGeoResource resource = layer.findGeoResource(GeoNeo.class);
            if (resource != null) {
                renderGeoNeo(destination, resource, monitor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(IProgressMonitor monitor) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    private Pair<MathTransform, MathTransform> setCrsTransforms(CoordinateReferenceSystem dataCrs) throws FactoryException {
        boolean lenient = true; // needs to be lenient to work on uDIG 1.1 (otherwise we get error:
        // bursa wolf parameters required
        CoordinateReferenceSystem worldCrs = context.getCRS();
        Pair<MathTransform, MathTransform> oldTransform = new Pair<MathTransform, MathTransform>(transform_d2w, transform_w2d);
        this.transform_d2w = CRS.findMathTransform(dataCrs, worldCrs, lenient);
        this.transform_w2d = CRS.findMathTransform(worldCrs, dataCrs, lenient);
        return oldTransform;
    }

    private void setCrsTransforms(Pair<MathTransform, MathTransform> oldTransform) throws FactoryException {
        this.transform_d2w = oldTransform.left();
        this.transform_w2d = oldTransform.right();
    }

    private Envelope getTransformedBounds() throws TransformException {
        Envelope bounds = getRenderBounds();
        if (bounds == null) {
            bounds = this.context.getViewportModel().getBounds();
        }
        Envelope bounds_transformed = null;
        if (bounds != null && transform_w2d != null) {
            bounds_transformed = JTS.transform(bounds, transform_w2d);
            final Feature geoFilter =(Feature) getContext().getLayer().getBlackboard().get("GEO_FILTER");
            if (geoFilter!=null){
                bounds_transformed=bounds_transformed.intersection(geoFilter.getBounds());
            }
        }
        return bounds_transformed;
    }

    /**
     * This class controls filtering of points by proximity. It simply checks to see if a point is
     * within a specified number of pixels of the previous point and returns a boolean true/false
     * based on that. It can be used to filter the frequency of drawing on the screen.
     * 
     * @author craig
     * @since 1.0.0
     */
    private class ShouldDraw {
        java.awt.Point prev_p;
        boolean always = false;
        double dx = 0.0;
        double dy = 0.0;
        int step = 0;

        private ShouldDraw(int step) {
            this.step = step;
            always = step < 0.0001;
            dx = 0.0;
            dy = 0.0;
        }

        private void setData(java.awt.Point p) {
            if (prev_p != null) {
                try {
                    dx = p.x - prev_p.x;
                    dy = p.y - prev_p.y;
                } catch (Exception e) {
                }
            }
            prev_p = p;
        }

        protected boolean shouldDraw() {
            return always || Math.abs(dx) > step || Math.abs(dy) > step;
        }
    }
    
    /**
     * This drawing filter extends the simple proximity filter by adding a time range. You specify
     * two proximity settings and a time range, and the second proximity setting is used for points
     * inside the range, while the first is used for points outside. This allows for data inside a
     * time range to be rendered with higher detail.
     * 
     * @author craig
     * @since 1.0.0
     */
    private class ShouldDrawTime extends ShouldDraw implements Iterable<CachedPoint> {
        Long beginTime = null;
        Long endTime = null;
        boolean inTimeRange = false;
        ShouldDrawCache outRangeCache;
        ShouldDrawCache inRangeCache;

        private ShouldDrawTime(int step, int smallStep, Long beginTime, Long endTime, int maxCache) {
            super(step);
            if (beginTime != null && endTime != null && beginTime < endTime) {
                this.beginTime = beginTime;
                this.endTime = endTime;
            }
            this.inRangeCache = new ShouldDrawCache(smallStep, maxCache);
            this.outRangeCache = new ShouldDrawCache(step, maxCache);
        }

        private void setData(java.awt.Point p, Long time) {
            inTimeRange = beginTime != null && time != null && time >= beginTime && time < endTime;
            if (inTimeRange) {
                inRangeCache.setData(p);
            } else {
                outRangeCache.setData(p);
            }
        }

        protected boolean shouldDraw() {
            if (inTimeRange) {
                return inRangeCache.shouldDraw();
            } else {
                return outRangeCache.shouldDraw();
            }
        }

        protected void updateData(java.awt.Point sector, Color color) {
            if (inTimeRange) {
                inRangeCache.updateData(sector,color);
            } else {
                outRangeCache.updateData(sector,color);
            }
        }

        public Iterator<CachedPoint> iterator() {
            MultiCacheIterator iterator = new MultiCacheIterator();
            iterator.addCache(inRangeCache.cache);
            iterator.addCache(outRangeCache.cache);
            return iterator;
        }
    }
    private static class CachedPoint {
        java.awt.Point p;
        java.awt.Point sector;
        Color color;

        private CachedPoint(java.awt.Point p, java.awt.Point sector, Color color) {
            this.p = p;
            this.sector = sector;
            this.color = color;
        }
    }
    
    private static class MultiCacheIterator implements Iterator<CachedPoint> {
        ArrayList<HashMap<String, CachedPoint>> caches = new ArrayList<HashMap<String, CachedPoint>>();
        private Iterator<HashMap<String, CachedPoint>> cacheIterator = null;
        private Iterator<CachedPoint> pointIterator = null;

        public void addCache(HashMap<String, CachedPoint> cache) {
            if (!cache.isEmpty())
                caches.add(cache);
        }

        public boolean hasNext() {
            if (cacheIterator == null)
                cacheIterator = caches.iterator();
            if (pointIterator == null || !pointIterator.hasNext()) {
                if (cacheIterator.hasNext()) {
                    pointIterator = cacheIterator.next().values().iterator();
                } else {
                    return false;
                }
            }
            return pointIterator.hasNext();
        }

        public CachedPoint next() {
            return pointIterator.next();
        }

        public void remove() {
        }

    }

    /**
     * This drawing filter is an alternative to the simple proximity filter by allowing for any
     * order. The normal filter compares the position to the previous position only, which does not
     * work if the points are not ordered. This version creates a location key and adds points to a
     * cache, and if they key accurs multipe times it keeps only one of them. As such, instead of
     * testing each point, just add them all to the cache, and at the end read them out again. The
     * cache will only contain the points to draw.
     * 
     * @author craig
     * @since 1.0.0
     */
    private class ShouldDrawCache extends ShouldDraw implements Iterable<CachedPoint> {
        HashMap<String, CachedPoint> cache = new HashMap<String, CachedPoint>();
        String key;
        int maxCache;

        private ShouldDrawCache(int step, int maxCache) {
            super(step);
            this.maxCache = maxCache;
        }

        private void setData(java.awt.Point p) {
            // Do not call super, because we do not need to calculate dx or dy
            this.prev_p = p;
            key = makeKey(p);
        }

        private String makeKey(java.awt.Point p) {
            return "" + (int)(p.x / step) + ":" + (int)(p.y / step);
        }

        protected boolean shouldDraw() {
            if (cache.size()>= maxCache || cache.containsKey(key)) {
                return false;
            } else {
                return true;
            }
        }

        protected void updateData(java.awt.Point sector, Color color) {
            cache.put(key, new CachedPoint(prev_p, sector, color));
        }

        public Iterator<CachedPoint> iterator() {
            return cache.values().iterator();
        }
    }

    /**
     * This filter extends the simple proximity filter with a theta calculation, so that drawing can
     * be angled according to the angle between subsequent points. It also remembers the first point
     * and second theta, so that they can be drawn later since they are not known about in the
     * beginning.
     * 
     * @author craig
     * @since 1.0.0
     */
    private class ShouldDrawTheta extends ShouldDraw {
        GeoNode firstNode;
        java.awt.Point firstPoint;
        Double firstTheta = null;
        double theta = 0.0;
        
        private ShouldDrawTheta(int step) {
            super(step);
            theta = 0.0;
        }

        private void setData(java.awt.Point p, GeoNode geoNode) {
            super.setData(p);
            try {
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
            if (firstPoint == null) {
                firstPoint = p; // so we can draw first point using second point settings
                firstNode = geoNode;
            } else {
                // we do this on the second point
                if(firstTheta == null) {
                    firstTheta = theta;
                }
            }
        }

        public double getFirstTheta() {
            return firstTheta == null ? 0.0 : firstTheta;
        }
    }

    /**
     * This method is called to render data from the Neo4j 'GeoNeo' Geo-Resource.
     */
    @SuppressWarnings("unchecked")
    private void renderGeoNeo(Graphics2D g, IGeoResource neoGeoResource, IProgressMonitor monitor) throws RenderException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        monitor.beginTask("render drive test data", IProgressMonitor.UNKNOWN);
        GeoNeo geoNeo = null;
        // enable/disable rendering of rectangles for the spatial index
        boolean enableIndexRendering = false;

        // Setup default drawing parameters and thresholds (to be modified by style if found)
        int maxSitesLabel = 30;
        int maxSitesFull = 100;
        int maxSitesLite = 1000;
         int maxSymbolSize = 40;
        int alpha = (int)(0.6 * 255.0);
         changeTransp = true;
        int drawSize = 3;
        Font font = g.getFont();
        int fontSize = font.getSize();
        IStyleBlackboard style = getContext().getLayer().getStyleBlackboard();
        NeoStyle neostyle = (NeoStyle)style.get(NeoStyleContent.ID);
        final List<Pair<ShapeType, List<Color>>> styles = neostyle!=null?neostyle.getStyles():new ArrayList<Pair<ShapeType, List<Color>>>(0);
        
        final GroupFilter[] groupFilters =(GroupFilter[]) getContext().getLayer().getBlackboard().get("FILTER");
     
        mpName = NeoStyleContent.DEF_MAIN_PROPERTY;
        msName = NeoStyleContent.DEF_SECONDARY_PROPERTY;
        eventIconSize = eventIconBaseSize;
        if (neostyle != null) {
            fillColor = neostyle.getFill();
            drawColor = neostyle.getLine();
            alpha = 255 - (int)((double)neostyle.getSymbolTransparency() / 100.0 * 255.0);
            try {
                fillColor = neostyle.getFill();
                drawColor = neostyle.getLine();
                labelColor = neostyle.getLabel();
                alpha = 255 - (int)((double)neostyle.getSymbolTransparency() / 100.0 * 255.0);
                changeTransp=neostyle.isChangeTransparency();
                drawSize = (neostyle.getSymbolSize()-1)/2;
                maxSitesLabel = neostyle.getLabeling() / 4;
                maxSitesFull = neostyle.getSmallSymb();
                maxSitesLite = neostyle.getSmallestSymb() * 10;
                maxSymbolSize = neostyle.getMaximumSymbolSize();
                fontSize = neostyle.getFontSize();
                mpName = neostyle.getMainProperty();
                msName = neostyle.getSecondaryProperty();
                scale = !neostyle.isFixSymbolSize();
                eventIconOffset = neostyle.getIconOffset();
                eventIconBaseSize = getIconSize(neostyle.getSymbolSize());
                eventIconMaxSize = getIconSize(neostyle.getMaximumSymbolSize());
                eventIconSize = eventIconBaseSize;
                if (neostyle.getSymbolSize() < eventIconSizes[0]) {
                    eventIconSize = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // we can get here if an old style exists, and we have added new fields
            }
        }
        normalSiteName = NeoStyleContent.DEF_MAIN_PROPERTY.equals(mpName);
        notMpLabel = !normalSiteName && NeoStyleContent.DEF_SECONDARY_PROPERTY.equals(mpName);
        notMsLabel = NeoStyleContent.DEF_SECONDARY_PROPERTY.equals(msName);
        g.setFont(font.deriveFont((float)fontSize));

        int drawWidth = 1 + 2 * drawSize;
        GraphDatabaseService neo = NeoServiceProviderUi.getProvider().getService();
        Transaction tx = neo.beginTx();
        try {
            monitor.subTask("connecting");
            geoNeo = neoGeoResource.resolve(GeoNeo.class, new SubProgressMonitor(monitor, 10));
            String gisName = NeoUtils.getSimpleNodeName(geoNeo.getMainGisNode(), "");
            
            filterMp = FilterUtil.getFilterOfData(geoNeo.getMainGisNode(), neo);
            // String selectedProp = geoNeo.getPropertyName();
            aggNode = geoNeo.getAggrNode();
            Map<String, Object> selectionMap = getSelectionMap(geoNeo);
            Long crossHairId1 = null;
            Long crossHairId2 = null;
            List<String> eventList = new ArrayList<String>();
            String selected_events = null;
            if (selectionMap != null) {
                crossHairId1 = (Long)selectionMap.get(GeoConstant.Drive.SELECT_PROPERTY1);
                crossHairId2 = (Long)selectionMap.get(GeoConstant.Drive.SELECT_PROPERTY2);
                selected_events = (String)selectionMap.get(GeoConstant.SELECTED_EVENT);
                eventList = (List<String>)selectionMap.get(GeoConstant.EVENT_LIST);
                if (eventList == null) {
                    eventList = new ArrayList<String>();
                }
            }
            // Integer propertyAdjacency = geoNeo.getPropertyAdjacency();
            setCrsTransforms(neoGeoResource.getInfo(null).getCRS());
            Envelope bounds_transformed = getTransformedBounds();
            Envelope data_bounds = geoNeo.getBounds();
            boolean drawFull = true;
            boolean drawLite = true;
            boolean drawLabels = true;
            if (bounds_transformed == null) {
                drawFull = false;
                drawLite = false;
                drawLabels = false;
            } else if (data_bounds != null && data_bounds.getHeight() > 0 && data_bounds.getWidth() > 0) {
                double dataScaled = (bounds_transformed.getHeight() * bounds_transformed.getWidth())
                        / (data_bounds.getHeight() * data_bounds.getWidth());
                double countScaled = dataScaled * geoNeo.getCount();
                drawLabels = countScaled < maxSitesLabel;
                drawFull = countScaled < maxSitesFull;
                if (scale && eventIconSize > 0) {
                    if (countScaled < maxSitesFull) {
                        eventIconSize = calcIconSize(eventIconBaseSize, eventIconMaxSize, maxSitesLabel, maxSitesFull, countScaled);
                    } else if (countScaled < maxSitesLite) {
                        eventIconSize = calcIconSize(eventIconSizes[0], eventIconBaseSize, maxSitesFull, maxSitesLite, countScaled);
                    } else {
                        eventIconSize = 0;
                    }
                    if (eventIconSize > eventIconMaxSize)
                        eventIconSize = eventIconMaxSize;
                    // eventIconSize = countScaled * 32 <= maxSitesFull ? 32 :countScaled * 16 <=
                    // maxSitesFull ? 16 : countScaled * 4 <= maxSitesFull ? 12
                    // : countScaled * 2 <= maxSitesFull ? 8 : 6;
                }
                drawLite = countScaled > maxSitesLite;
                if (drawFull && scale) {
                    drawWidth *= Math.sqrt(maxSitesFull) / (0.8 * Math.sqrt(countScaled));
                    drawWidth = Math.min(drawWidth, maxSymbolSize);
                    drawWidth=drawWidth|1;
                    drawSize=(drawWidth-1)/2;
                }
            }
            int trans = alpha;
            if (haveSelectedNodes()) {
                trans = 25;
            }
            
            fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), trans);
            g.setColor(drawColor);
            int count = 0;
            monitor.subTask("drawing");
            // single object for re-use in transform below (minimize object creation)
            Coordinate world_location = new Coordinate();
//            final Feature geoFilter = getContext().getFeaturesInBbox(layer, bbox);
            final Feature geoFilter =(Feature) getContext().getLayer().getBlackboard().get("GEO_FILTER");
            LOGGER.debug("[DEBUG] geo filter "+geoFilter);
            if (geoFilter != null) {
                final Coordinate[] coordinates = geoFilter.getDefaultGeometry().getCoordinates();
                final int n = coordinates.length;
                xPoints = new int[n];
                yPoints = new int[n];
                for (int i = 0; i < n; i++) {
                    try {
                         JTS.transform(coordinates[i], world_location, transform_d2w);
                    } catch (Exception e) {
                        continue;
                    }
                    java.awt.Point p = getContext().worldToPixel(world_location);
                    xPoints[i]=p.x;
                    yPoints[i]=p.y;
                }
                Color oldColor=g.getColor();
                g.setColor(Color.red);
                g.drawPolygon(xPoints, yPoints, n);
                g.setColor(oldColor);
            }
            //TODO: Check if the groupFilters code really should be an entirely different renderer logic? This code is missing lots of other logic from below, so we probably have bugs here.
            if (groupFilters!=null){
                try {
                    Node gisNode = geoNeo.getMainGisNode();
                    for (Node node : gisNode.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                            new ReturnableEvaluator() {

                                @Override
                                public boolean isReturnableNode(TraversalPosition currentPos) {
                                    boolean result = false;
                                    for (GroupFilter groupFilter : groupFilters) {
                                        final List<IFilter> filters = groupFilter.getFilters();
                                        Node currentNode = currentPos.currentNode();
                                        if (!NeoUtils.isDriveMNode(currentNode))
                                            return false;
                                        for (IFilter filter : filters) {
                                            if (filter instanceof CompositeFilter) {
                                                CompositeFilter cFilter = (CompositeFilter)filter;
                                                final String property = cFilter.getProperty();
                                                if (currentNode.hasProperty(property)) {
                                                    result = result || cFilter.accept(currentNode);
                                                }
                                            } else if (filter instanceof Filter) {
                                                Filter simpleFilter = (Filter)filter;
                                                final String property = simpleFilter.getProperty();
                                                if (currentNode.hasProperty(property)) {
                                                    result = result || simpleFilter.accept(currentNode.getProperty(property));
                                                }
                                            }
                                        }
                                    }
                                    return result;
                                }

                            }, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING,GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)) {
                        if (monitor.isCanceled()){
                            break;
                        }
                        else{
                            if (!node.hasRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING)){
                                LOGGER.debug("Node "+node.getId()+" has no location relationship");
                                continue;
                            }
                        GeoNode geoNode = new GeoNode(node.getSingleRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING).getEndNode());
                        Coordinate location = geoNode.getCoordinate();
                        if (bounds_transformed != null && !bounds_transformed.contains(location)) {
                            continue; // Don't draw points outside viewport
                        }
                        try {
                            JTS.transform(location, world_location, transform_d2w);
                        } catch (Exception e) {
                            continue;
                        }
                        java.awt.Point p = getContext().worldToPixel(world_location);
                        if (geoFilter!=null && !insidePolygon(p)){
                            continue;
                        }
                        
                       for (int i=0;i<groupFilters.length;i++) {
                           GroupFilter groupFilter=groupFilters[i];
                                List<IFilter> filters = groupFilter.getFilters();
                                for (int j = 0; j < filters.size(); j++) {
                                    IFilter filter = filters.get(j);
                                    //TODO refactor - identical code
                                    if (filter instanceof CompositeFilter) {
                                        CompositeFilter cFilter = (CompositeFilter)filter;
                                        final String property = cFilter.getProperty();
                                        if (node.hasProperty(property)) {
                                            final Object value = node.getProperty(property);
                                            if (cFilter.accept(node)) {
                                                Pair<ShapeType, List<Color>> shapeStyle = styles.get(i);
                                                List<Color> colors = shapeStyle.r();
                                                Color color = colors.get(j < colors.size() ? j : colors.size() - 1);
                                                g.setColor(color);
                                                // drawSize=10;
                                                switch (shapeStyle.l()) {
                                                case CIRCLE:
                                                    int radius = 5;
                                                    g.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
                                                    break;
                                                case RECTANGLE:
                                                    g.fillRect(p.x - drawSize, p.y - drawSize, drawWidth, drawWidth);
                                                    break;
                                                case TEXT:
                                                    g.drawString(value.toString(), p.x, p.y - 5);
                                                    break;
                                                }

                                            }
                                        }
                                    } else if (filter instanceof Filter) {
                                        Filter simpleFilter = (Filter)filter;
                                        final String property = simpleFilter.getProperty();
                                        if (node.hasProperty(property)) {
                                            final Object value = node.getProperty(property);
                                            if (simpleFilter.accept(value)) {
                                                Pair<ShapeType, List<Color>> shapeStyle = styles.get(i);
                                                List<Color> colors = shapeStyle.r();
                                                Color color = colors.get(j < colors.size() ? j : colors.size() - 1);
                                                g.setColor(color);
                                                switch (shapeStyle.l()) {
                                                case CIRCLE:
                                                    int radius = 5;
                                                    g.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
                                                    break;
                                                case RECTANGLE:
                                                    g.fillRect(p.x - drawSize, p.y - drawSize, drawWidth, drawWidth);
                                                    break;
                                                case TEXT:
                                                    g.drawString(value.toString(), p.x, p.y);
                                                    break;
                                                }

                                            }
                                        }
                                    }
                                }
                        }
//                        g.setColor(groupFilter.getColor(node));
//                        g.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
                        }
                    }
                    monitor.done();
                    tx.finish();
                } catch (Exception e){
                    e.printStackTrace();
                }
                    finally {
                    tx.finish();
                }

            }
            else{  // groupFilters == null
                previousPoint = null;
                ShouldDrawTheta shouldDrawLabels = new ShouldDrawTheta(20);   // filter for drawing labels every 20 pixels
                ShouldDrawTheta shouldDrawIcons = new ShouldDrawTheta(20);    // filter for drawing event icons eveny 10 pixels
                long startTime = System.currentTimeMillis();
    
                // First we find all selected points to draw with a highlight behind the main points
                ArrayList<Node> selectedPoints = new ArrayList<Node>();
                final Set<Node> selectedNodes = new HashSet<Node>(geoNeo.getSelectedNodes());
                // TODO refactor selection point (for example: in draws mp node add method
                // isSelected(node))
                Long beginTime = null;
                Long endTime = null;
                BrewerPalette palette = null;
                if (selectionMap != null) {
                    String paletteName = (String)selectionMap.get(GeoConstant.Drive.SELECT_PALETTE);
                    try {
                        palette = PlatformGIS.getColorBrewer().getPalette(paletteName);
                    } catch (Exception e) {
                        palette = null;
                    }
                    beginTime = (Long)selectionMap.get(GeoConstant.Drive.BEGIN_TIME);
                    endTime = (Long)selectionMap.get(GeoConstant.Drive.END_TIME);
                    if (beginTime != null && endTime != null && beginTime <= endTime) {
                        MultiPropertyIndex<Long> timestampIndex = NeoUtils.getTimeIndexProperty(geoNeo.getName());
                        timestampIndex.initialize(NeoServiceProviderUi.getProvider().getService(), null);
                        for (Node node : timestampIndex.searchTraverser(new Long[] {beginTime}, new Long[] {endTime})) {
                            if (!node.hasRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING)) {
                                continue;
                            }
                            Node mpNode = node.getSingleRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING).getOtherNode(
                                    node);
                            selectedNodes.add(mpNode);
                        }
                    }
                }
                ShouldDrawTime shouldDrawLines = new ShouldDrawTime(30,10,beginTime,endTime, 500);    // filter for drawing correlation lines every 10 pixels
                boolean canDrawLines = !getNetworkGeoResources(geoNeo).isEmpty();
                boolean haveSelectedEvents = canDrawLines && palette != null && selected_events != null;
                boolean allEvents = haveSelectedEvents && selected_events.equals(GeoConstant.ALL_EVENTS);
                Color eventColor = null;
                if (haveSelectedEvents && !allEvents) {
                    int i = eventList.indexOf(selected_events);
                    if (i < 0) {
                        i = 0;
                    }
                    Color[] colors = palette.getColors(palette.getMaxColors());
                    int index = i % colors.length;
                    eventColor = colors[index];
                }
                // We need to draw the section nodes first so that all highlights will appear behind all drive nodes.
                // If we tried to draw the highlights and drive points in one loop, it would look ugly.
                for (Node node : selectedNodes) {
                    if (NeoUtils.isFileNode(node)) {
                        // Select all 'mp' nodes in that file
                        for (Node rnode : node.traverse(Order.BREADTH_FIRST, new StopEvaluator() {
                            @Override
                            public boolean isStopNode(TraversalPosition currentPos) {
                                return !currentPos.isStartNode() && !NeoUtils.isDriveMNode(currentPos.currentNode());
                            }
                        }, new ReturnableEvaluator() {
    
                            @Override
                            public boolean isReturnableNode(TraversalPosition currentPos) {
                                return NeoUtils.isDrivePointNode(currentPos.currentNode());
                            }
                        }, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING, GeoNeoRelationshipTypes.CHILD, Direction.INCOMING)) {
                            selectedPoints.add(rnode);
                        }
                    } else {
                        // Traverse backwards on CHILD relations to closest 'mp' Point
                        for (Node rnode : node.traverse(Order.DEPTH_FIRST, new StopEvaluator() {
                            @Override
                            public boolean isStopNode(TraversalPosition currentPos) {
                                return NeoUtils.isDrivePointNode(currentPos.currentNode());
                            }
                        }, new ReturnableEvaluator() {
    
                            @Override
                            public boolean isReturnableNode(TraversalPosition currentPos) {
                                return NeoUtils.isDrivePointNode(currentPos.currentNode());
                            }
                        }, GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING)) {
                            selectedPoints.add(rnode);
                            break;
                        }
                    }
                }
                // Now draw the selected points highlights
                for (Node rnode : selectedPoints) {
                    GeoNode node = new GeoNode(rnode);
                    Coordinate location = node.getCoordinate();
                    if (bounds_transformed != null && !bounds_transformed.contains(location)) {
                        continue; // Don't draw points outside viewport
                    }
                    if (filterMp != null) {
                        if (!filterMp.filterNodesByTraverser(node.getNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.LOCATION,Direction.INCOMING)).isValid()) {
                            continue;
                        }
                    }
                    try {
                        JTS.transform(location, world_location, transform_d2w);
                    } catch (Exception e) {
                        continue;
                    }
                    java.awt.Point p = getContext().worldToPixel(world_location);
                    if (samePixel(p) || geoFilter!=null && !insidePolygon(p)){
                        continue;
                    }
                    renderSelectedPoint(g, p, drawSize, drawFull, drawLite);
                }
                Node indexNode = null;
                HashMap<String, Integer> colorErrors = new HashMap<String, Integer>();
                previousPoint = null;// else we do not show selected node
                // Now draw the actual points. This must happen after drawing the highlights so it looks like two layers
                for (GeoNode node : geoNeo.getGeoNodes(bounds_transformed)) {
                    if (filterMp != null) {
                        if (!filterMp.filterNodesByTraverser(node.getNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.LOCATION,Direction.INCOMING)).isValid()) {
                            continue;
                        }
                    }
                    if (enableIndexRendering && indexNode == null) {
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
                    if (samePixel(p) || geoFilter!=null && !insidePolygon(p)){
                        continue;
                    }
                    Color nodeColor = fillColor;
                    try {
                        nodeColor = getNodeColor(node.getNode(), fillColor);
                        // nodeColor = getColorOfMpNode(select, node.getNode(), fillColor,
                        // selectedProp, redMinValue, redMaxValue,
                        // lesMinValue, moreMaxValue);
                    } catch (RuntimeException e) {
                        String errName = e.toString();
                        if (colorErrors.containsKey(errName)) {
                            colorErrors.put(errName, colorErrors.get(errName) + 1);
                        } else {
                            colorErrors.put(errName, 1);
                        }
                    }
                    Color borderColor = g.getColor();
                    if (selectedNodes.size() > 0) {
                        if (selectedNodes.contains(node.getNode())) {
                            borderColor = COLOR_HIGHLIGHTED;
                        }
                    }
                    long id = node.getNode().getId();
                    if ((crossHairId1 != null && id == crossHairId1) || (crossHairId2 != null && crossHairId2 == id)) {
                        borderColor = COLOR_HIGHLIGHTED_SELECTED;
                    }
    
                    renderPoint(g, p, borderColor, nodeColor, drawSize, drawWidth, drawFull, drawLite);
                    if (drawLabels) {
                        shouldDrawLabels.setData(p, node);
                        if(shouldDrawLabels.shouldDraw()) {
                            renderLabel(g, count, node, p, shouldDrawLabels.theta);
                        }
                    }
                    if (base_transform != null) {
                        // recover the normal transform
                        g.setTransform(base_transform);
                        g.setColor(drawColor);
                        // base_transform = null;
                    }
                    monitor.worked(1);
                    count++;
                    if (monitor.isCanceled())
                        break;
                    
                    if (canDrawLines) {
                        Node mpNode = node.getNode();
                        Long time = NeoUtils.getNodeTime(mpNode);
                        shouldDrawLines.setData(p, time);
                        if(shouldDrawLines.shouldDraw()) {
                            Node sector = findCorrelatedSectorFromPointNode(geoNeo, mpNode, monitor);
                            if (sector != null && networkCRS != null) {
                                Node site = sector.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING)
                                        .getOtherNode(sector);
                                GeoNode siteGn = new GeoNode(site);
                                location = siteGn.getCoordinate();
                                try {
                                    JTS.transform(location, world_location, transform_d2w);
                                } catch (Exception e) {
                                    // JTS.transform(location, world_location,
                                    // transform_w2d.inverse());
                                }
                                java.awt.Point pSite = getContext().worldToPixel(world_location);
                                if (drawFull) {
                                    pSite = getSectorCenter(g, sector, pSite);
                                }
                                Color lineColor = getCorrelationLineColor(eventList, selected_events, palette, haveSelectedEvents,
                                        allEvents, eventColor, mpNode);
                                shouldDrawLines.updateData(pSite, lineColor);
                            }

                        }
                    }
                }
                // Now draw correlation lines. This should be done after the drive, so they appear
                // as a separate layer.
                for (CachedPoint linePoint : shouldDrawLines) {
                    Color oldColor = g.getColor();
                    Pair<MathTransform, MathTransform> driveTransform = setCrsTransforms(networkCRS);
                    g.setColor(linePoint.color);
                    g.drawLine(linePoint.p.x, linePoint.p.y, linePoint.sector.x, linePoint.sector.y);
                    // restore old transform and color
                    setCrsTransforms(driveTransform);
                    g.setColor(oldColor);
                }
                // Draw the first label. We do this last because we did not know enough to do it first.
                if (drawLabels && shouldDrawLabels.firstNode != null) {
                    renderLabel(g, 0, shouldDrawLabels.firstNode, shouldDrawLabels.firstPoint, shouldDrawLabels.getFirstTheta());
                }
                previousPoint = null;
                
                // Now draw the event icons
                if (eventIconSize > 0) {
                    for (Node node1 : index.getNodes(INeoConstants.EVENTS_LUCENE_INDEX_NAME, gisName)) {
                        if (monitor.isCanceled())
                            break;
                        GeoNode node = new GeoNode(node1);
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
                        if (samePixel(p) || geoFilter!=null && !insidePolygon(p)){
                            continue;
                        }
                        shouldDrawIcons.setData(p, node);
                        if (shouldDrawIcons.shouldDraw()) {
                            renderEvents(g, node, p, shouldDrawIcons.theta);
                        }
    
                    }
                    if (shouldDrawIcons.firstNode != null) {
                        renderEvents(g, shouldDrawIcons.firstNode, shouldDrawIcons.firstPoint, shouldDrawIcons.getFirstTheta());
                    }
                }
                for (String errName : colorErrors.keySet()) {
                    int errCount = colorErrors.get(errName);
                    System.err.println("Error determining color of " + errCount + " nodes: " + errName);
                }
                if (indexNode != null) {
                    renderIndex(g, bounds_transformed, indexNode);
                }
                LOGGER.debug("Drive renderer took " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s to draw " + count
                        + " points");
                tx.success();
            }//end if
            } catch (TransformException e) {
            throw new RenderException(e);
        } catch (FactoryException e) {
            throw new RenderException(e);
        } catch (IOException e) {
            throw new RenderException(e); // rethrow any exceptions encountered
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.finish();
            // if (geoNeo != null)
            // geoNeo.close();
            clearCaches();
            monitor.done();

        }
    }

    private boolean samePixel(Point p) {
        boolean same = false;
        if (previousPoint != null && previousPoint.x == p.x && previousPoint.y == p.y) {
            same = true;
        }
        previousPoint = p;
        return same;
    }

    private void clearCaches() {
        sectorPoints.clear();
        driveSectorCorrelationLines.clear();
        networkGeoResources.clear();
        networkGeoResource = null;
        networkCRS = null;
    }
    
    private ArrayList<IGeoResource> getNetworkGeoResources(GeoNeo geoNeo) {
        if(networkGeoResources==null || networkGeoResources.isEmpty()) {
            Iterable<Relationship> relations = geoNeo.getMainGisNode().getRelationships(
                    CorrelationRelationshipTypes.LINKED_NETWORK_DRIVE, Direction.INCOMING);
            networkGeoResources = new ArrayList<IGeoResource>();
            for (Relationship relationship : relations) {
                IGeoResource network = getNetwork(relationship.getOtherNode(geoNeo.getMainGisNode()));
                if (network != null) {
                    networkGeoResources.add(network);
                }
            }
            
            //Lagutko, 15.05.2010, another way of correlation
            relations = NeoUtils.getDatasetNodeByGis(geoNeo.getMainGisNode()).getRelationships(CorrelationRelationshipTypes.CORRELATED, Direction.OUTGOING);
            for (Relationship relation : relations) {
            	Node correlationNode = relation.getEndNode();
            	
            	IGeoResource network = getNetwork(NeoUtils.getGisNodeByDataset(correlationNode.getSingleRelationship(CorrelationRelationshipTypes.CORRELATION, Direction.INCOMING).getStartNode()));
                if (network != null) {
                    networkGeoResources.add(network);
                }
            }
        }
        return networkGeoResources;
    }
    
    private Node findCorrelatedSectorFromPointNode(GeoNeo geoNeo, Node mpNode, IProgressMonitor monitor) throws IOException {
        Iterator<Relationship> links = mpNode.getRelationships(GeoNeoRelationshipTypes.LOCATION, Direction.INCOMING).iterator();
        HashSet<Node> mNodes = new HashSet<Node>();
        ArrayList<IGeoResource> networkGeoResources = getNetworkGeoResources(geoNeo);
        while (links.hasNext()) {
            mNodes.add(links.next().getStartNode());
        }
        
        for (Node mNode : mNodes) {
            Iterator<Relationship> rels = mNode.getRelationships(CorrelationRelationshipTypes.CORRELATED,
                    Direction.INCOMING).iterator();
            while (rels.hasNext()) {
                Relationship singleLink = rels.next();
                if ((!rels.hasNext() && (!singleLink.hasProperty(INeoConstants.NETWORK_GIS_NAME)))
                        || geoNeo.getName().equals(singleLink.getProperty(INeoConstants.NETWORK_GIS_NAME))) {

                    Relationship relationSector = singleLink.getStartNode().getSingleRelationship(
                            CorrelationRelationshipTypes.CORRELATION, Direction.OUTGOING);

                    Object networkGisName = relationSector.getProperty(INeoConstants.NETWORK_GIS_NAME);
                    for (IGeoResource networkResource : networkGeoResources) {
                        GeoNeo networkGis = networkResource.resolve(GeoNeo.class, null);
                        if (networkGisName.equals(NeoUtils.getSimpleNodeName(networkGis.getMainGisNode(), ""))) {
                            this.networkGeoResource = networkResource;
                            this.networkCRS = networkResource.getInfo(monitor).getCRS();
                            return relationSector.getEndNode();
                        }
                    }
                }
            }
        }
        return null;
    }

    private Color getCorrelationLineColor(List<String> eventList, String selected_events, BrewerPalette palette,
            boolean haveSelectedEvents, boolean allEvents, Color eventColor, final Node mpNode) {
        Color lineColor;
        if (haveSelectedEvents) {
            Set<String> events = NeoUtils.getEventsList(mpNode, null);
            if (!events.isEmpty() && (allEvents || events.contains(selected_events))) {
                if (allEvents) {
                    int i = eventList.indexOf(events.iterator().next());
                    if (i < 0) {
                        i = 0;
                    }
                    Color[] colors = palette.getColors(palette.getMaxColors());
                    int index = i % colors.length;
                    eventColor = colors[index];
                }
                lineColor = eventColor;
            } else {
                lineColor = FADE_LINE;
            }
        } else {
            lineColor = FADE_LINE;
        }
        return lineColor;
    }

    private boolean insidePolygon(Point point) {
        int intersections = 0;
        final int n = xPoints.length;
        for (int i = 1; i < n; ++i) {
          if (doesIntersect(point, xPoints[i],yPoints[i],xPoints[i-1],yPoints[i-1]))
            ++intersections;
        }
        if (doesIntersect(point, xPoints[n-1],yPoints[n-1],xPoints[0],yPoints[0]))
          ++intersections;
        return (intersections % 2 != 0);
    }
    private boolean doesIntersect(Point point, int x1,int y1, int x2, int y2) {
        if ((x2 < point.x && x1 >= point.x) ||
            (x2 >= point.x && x1 < point.x)) {

            double y = (y2 - y1) / (x2 - x1) * (point.x - x1) + y1;
            return y > point.y;
        }
        return false;
       }
    /**
     * Gets center of sector
     * 
     * @param sector sector mode
     * @param pSite site coordinate
     * @return sector coordinate
     */
    private Point getSectorCenter(Graphics2D g, Node sector, Point pSite) {
        // double beamwidth = ((Number)sector.getProperty("beamwidth", 360.0)).doubleValue();
        double azimuth = ((Number)sector.getProperty("azimuth", Double.NaN)).doubleValue();
        if (azimuth == Double.NaN) {
            return pSite;
        }
        double angdeg = -90 + azimuth;
        AffineTransform transform2 = new AffineTransform(g.getTransform());
        transform2.translate(pSite.x, pSite.y);
        transform2.rotate(Math.toRadians(angdeg), 0, 0);
        double xLoc = 10;
        double yLoc = 0;
        transform2.concatenate(g.getTransform());
        int x = (int)(transform2.getScaleX() * xLoc + transform2.getShearX() * yLoc + transform2.getTranslateX());
        int y = (int)(transform2.getShearY() * xLoc + transform2.getScaleY() * yLoc + transform2.getTranslateY());
        return new Point(x, y);
    }

    /**
     * @param otherNode
     * @return
     */
    private IGeoResource getNetwork(Node networkNode) {
        try {
            List<ILayer> layers = getContext().getMap().getMapLayers();
            for (ILayer iLayer : layers) {
                if (iLayer.getGeoResource().canResolve(GeoNeo.class)) {
                    GeoNeo resource;
                    resource = iLayer.getGeoResource().resolve(GeoNeo.class, null);

                    if (resource.getMainGisNode().equals(networkNode)) {
                        return iLayer.getGeoResource();
                    }
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Node getIndexNode(GeoNode node) {
        try {
            LOGGER.debug("Searching for index nodes on node: " + node.getName());
            Node endNode = node.getNode();
            LOGGER.debug("Searching for index nodes on node: id:" + endNode.getId() + ", name:"
                    + endNode.getProperty("name", null) + ", type:" + endNode.getProperty("type", null) + ", index:"
                    + endNode.getProperty("index", null) + ", level:" + endNode.getProperty("level", null) + ", max:"
                    + endNode.getProperty("max", null) + ", min:" + endNode.getProperty("min", null));
            for (Relationship relationship : node.getNode().getRelationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.INCOMING)) {
                endNode = relationship.getStartNode();
                LOGGER.debug("Trying possible index node: id:" + endNode.getId() + ", name:"
                        + endNode.getProperty("name", null) + ", type:" + endNode.getProperty("type", null) + ", index:"
                        + endNode.getProperty("index", null) + ", level:" + endNode.getProperty("level", null) + ", max:"
                        + endNode.getProperty("max", null) + ", min:" + endNode.getProperty("min", null));
                int[] index = (int[])endNode.getProperty("index", new int[0]);
                if (index.length == 2) {
                    return endNode;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to find index node: " + e);
            // e.printStackTrace(System.err);
        }
        return null;
    }

    private void renderIndex(Graphics2D g, Envelope bounds_transformed, Node indexNode) {
        Coordinate world_location = new Coordinate();
        try {
            INDEX_LOOP: for (Node index : indexNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    ReturnableEvaluator.ALL_BUT_START_NODE, NeoIndexRelationshipTypes.IND_CHILD, Direction.BOTH)) {
                int[] ind = (int[])index.getProperty("index", new int[0]);
                if (ind.length == 2) {
                    double[] max = (double[])index.getProperty("max", new double[0]);
                    double[] min = (double[])index.getProperty("min", new double[0]);
                    int level = (Integer)index.getProperty("level", 0);
                    if (max.length == 2 && min.length == 2) {
                        drawColor = new Color(0.5f, 0.5f, 0.5f, 1.0f - Math.max(0.1f, 0.8f * (5.0f - level) / 5.0f));
                        g.setColor(drawColor);
                        Coordinate[] c = new Coordinate[2];
                        java.awt.Point[] p = new java.awt.Point[2];
                        c[0] = new Coordinate(min[1], max[0]);
                        c[1] = new Coordinate(max[1], min[0]);
                        for (int i = 0; i < 2; i++) {
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
                        if (p[1].x > p[0].x && p[1].y > p[0].y) {
                            g.drawRect(p[0].x, p[0].y, p[1].x - p[0].x, p[1].y - p[0].y);
                            g.drawString("" + ind[0] + ":" + ind[1] + "[" + level + "]", p[0].x, p[0].y);
                        } else {
                            System.err.println("Invalid index bbox: " + p[0] + ":" + p[1]);
                            g.drawRect(Math.min(p[0].x, p[1].x), Math.min(p[0].y, p[1].y), Math.abs(p[1].x - p[0].x), Math
                                    .abs(p[1].y - p[0].y));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to draw index: " + e);
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
        // g.drawString(""+Integer.toString(count)+": "+node.toString(), 10, 5);
        g.drawString(getPointLabel(node), 10, 5);
    }

    /**
     *Gets label of mp node
     * 
     * @param node GeoNode
     * @return String
     */
    private String getPointLabel(GeoNode node) {
        StringBuilder pointName = new StringBuilder(normalSiteName ? node.toString() : notMpLabel ? "" : node.getNode()
                .getProperty(mpName, node.toString()).toString());
        if (!notMsLabel) {
            String msNames = NeoUtils.getMsNames(node.getNode(), msName);
            if (!msNames.isEmpty()) {
                pointName.append(", ").append(msNames);
            }
        }
        return notMpLabel && pointName.length() > 1 ? pointName.substring(2) : pointName.toString();
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
            // System.out.println("Get color for node with type "+NodeTypes.getNodeType(node,
            // null).getId());
            if (aggNode != null) {               
                Node chartNode = NeoUtils.getChartNode(node, aggNode);
                if (chartNode != null) {
                    Integer rgb = (Integer)chartNode.getProperty(INeoConstants.AGGREGATION_COLOR, null);
                    if (rgb!=null) {
                        if (changeTransp) {
                            return new Color(rgb);
                        } else {
                            return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 0) & 0xFF, defColor.getAlpha());
                        }

                    }
                }
            }
            Node coloredNode = null;
            if(NeoUtils.isDrivePointNode(node)){
                Traverser nodes = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {                    
                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                       Node curr = currentPos.currentNode();
                        return NeoUtils.isCallNode(curr)||NeoUtils.isDriveMNode(curr);
                    }
                }, GeoNeoRelationshipTypes.LOCATION, Direction.INCOMING);
                ColoredFlags flag = ColoredFlags.NONE;
                for(Node curr : nodes){
                    ColoredFlags currFlag = ColoredFlags.getFlagById((String)curr.getProperty(INeoConstants.PROPERTY_FLAGGED_NAME, ColoredFlags.NONE.getId()));
                    if(currFlag.getOrder()>flag.getOrder()){
                        flag = currFlag;
                        coloredNode = curr;
                    }
                }
            } else if(NeoUtils.isDriveMNode(node)||NeoUtils.isCallNode(node)){
                coloredNode = node;
            }
            if (coloredNode!=null) {
                Integer rgb = (Integer)coloredNode.getProperty(INeoConstants.AGGREGATION_COLOR, null);
                if (rgb != null) {
                    if (changeTransp) {
                        return new Color(rgb);
                    } else {
                        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 0) & 0xFF, defColor.getAlpha());
                    }
                }
            }
            return defColor;
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
        if (drawFull) {
            g.setColor(fillColor);
            g.fillOval(p.x - drawSize, p.y - drawSize, drawWidth, drawWidth);
        } else if (drawLite) {
            g.setColor(fillColor);
            g.fillRect(p.x - 1, p.y - 1, 3, 3);
        } else {
            g.setColor(fillColor);
            g.fillRect(p.x - drawSize, p.y - drawSize, drawWidth, drawWidth);
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
        if (event == null || eventIconSize < eventIconSizes[0]) {
            return;
        }
        Image eventImage = event.getEventIcon().getImage(eventIconSize);
        if (eventImage != null) {
            if (base_transform == null)
                base_transform = g.getTransform();
            g.setTransform(base_transform);
            g.translate(p.x, p.y);
            if (eventIconOffset > 0) {
                g.rotate(-theta);
            }

            ImageObserver imOb = null;
            final int width = eventImage.getWidth(imOb);
            final int height = eventImage.getHeight(imOb);
            g.drawImage(eventImage, -eventIconOffset - width / 2, -height / 2, width, height, imOb);
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
        if (drawFull) {
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
        for (; drawSize > 2; drawSize *= 0.8) {
            g.fillOval(p.x - drawSize, p.y - drawSize, 2 * drawSize, 2 * drawSize);
        }
    }

}
