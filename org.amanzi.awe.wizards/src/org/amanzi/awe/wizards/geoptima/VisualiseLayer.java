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

package org.amanzi.awe.wizards.geoptima;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.interceptor.MapInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.commands.DeleteLayerCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class VisualiseLayer implements MapGraphic, MapInterceptor {

    /**
     * Instantiates a new visualise layer.
     */
    public VisualiseLayer() {
        super();

    }

    // TODO synchronyze!
    public static ILayer datasetLayer = null;
    public static VisualiseParam datasetParam = null;

    /** The Constant LOGGER. */
    public static final Logger LOGGER = Logger.getLogger(VisualiseLayer.class);

    // TODO check synchronize
    // TODO add listener on remove map!
    /** The Constant animation. */
    private static final Set<VisualiseCommand> animation = new HashSet<VisualiseCommand>();

    /** The Constant DATASET_LAYER. */
    public static final String DATASET_LAYER = "DATASET_LAYER";
    public static final String DATASET_PARAM = "DATASET_PARAM";

    /** The service. */
    private final GraphDatabaseService service = NeoServiceProvider.getProvider().getService();

    /**
     * Draw.
     * 
     * @param context the context
     */
    @Override
    public void draw(MapGraphicContext context) {
        ILayer datasetLayer = findDatasetLayer(context);

        if (datasetLayer == null) {
            return;
        }

        ILayer layer = context.getLayer();
        updateParam(layer);
        VisualiseCommand command;
        try {
            command = new VisualiseCommand(layer, datasetLayer, context);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // TODO Handle IllegalArgumentException
            return;
        }
        if (animation.contains(command)) {
            return;
        }
        animation.add(command);
        AnimationUpdater.runTimer(context.getMapDisplay(), command);
    }

    /**
     * @param layer
     */
    private void updateParam(ILayer layer) {
        Object param = layer.getBlackboard().get(DATASET_PARAM);
        if (param != null) {
            return;
        }
        layer.getBlackboard().put(DATASET_PARAM, datasetParam);
        datasetParam = null;
        return;
    }

    /**
     * Find dataset layer.
     * 
     * @param context the context
     * @return the i layer
     */
    private ILayer findDatasetLayer(MapGraphicContext context) {
        ILayer datasetLayer = (ILayer)context.getLayer().getBlackboard().get(DATASET_LAYER);
        if (datasetLayer != null) {
            return datasetLayer;
        }
        if (this.datasetLayer != null) {
            datasetLayer = this.datasetLayer;
            context.getLayer().getBlackboard().put(DATASET_LAYER, this.datasetLayer);
            this.datasetLayer = null;
            return datasetLayer;
        }
        Transaction tx = service.beginTx();
        ILayer result = null;
        try {
            for (ILayer layer : context.getMapLayers()) {
                if (layer.getGeoResource().canResolve(Node.class)) {
                    Node nodeGis = layer.getGeoResource().resolve(Node.class, null);
                    Node node = NeoUtils.findRoot(nodeGis, service);
                    if (NeoUtils.isDatasetNode(node) || NodeTypes.OSS.checkNode(node)) {
                        if (datasetParam == null) {
                            Pair<Long, Long> minMax = NeoUtils.getMinMaxTimeOfDataset(node, service);
                            datasetParam = new VisualiseParam(minMax.getLeft(), minMax.getRight(), true, 1000);
                        }
                        result = layer;
                        break;
                    }
                }
            }

            context.getLayer().getBlackboard().put(DATASET_LAYER, result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            tx.finish();
        }
    }

    /**
     * The Class VisualiseCommand.
     */
    private class VisualiseCommand extends AbstractDrawCommand implements IAnimation {

        /** The dataset layer. */
        private final ILayer datasetLayer;

        /** The layer. */
        private final ILayer layer;

        /** The context. */
        private final MapGraphicContext context;

        /** The cansel. */
        private boolean cansel;

        /** The monitor. */
        private IProgressMonitor monitor = new NullProgressMonitor();

        /** The root node. */
        private Node rootNode;

        /** The timestamp index. */
        private MultiPropertyIndex<Long> timestampIndex;

        /** The current time. */
        private Long currentTime;

        /** The transform_d2w. */
        private MathTransform transform_d2w;

        /** The transform_w2d. */
        private MathTransform transform_w2d;

        /** The c rs dataset. */
        private CoordinateReferenceSystem cRSDataset;
        private VisualiseParam param;

        /**
         * Instantiates a new visualise command.
         * 
         * @param layer the layer
         * @param datasetLayer the dataset layer
         * @param context the context
         * @throws IllegalArgumentException the illegal argument exception
         */
        public VisualiseCommand(ILayer layer, ILayer datasetLayer, MapGraphicContext context) throws IllegalArgumentException {
            this.layer = layer;
            this.datasetLayer = datasetLayer;
            this.context = context;
            cansel = false;
            init();

        }

        /**
         * Inits the.
         */
        private void init() {
            Transaction tx = service.beginTx();
            try {
                Node node = datasetLayer.getGeoResource().resolve(Node.class, null);
                cRSDataset = datasetLayer.getGeoResource().getInfo(new NullProgressMonitor()).getCRS();
                rootNode = NeoUtils.findRoot(node, service);
                String datasetName = NeoUtils.getNodeName(node, service);
                timestampIndex = NeoUtils.getTimeIndexProperty(datasetName);
                timestampIndex.initialize(NeoServiceProvider.getProvider().getService(), null);
                param = (VisualiseParam)layer.getBlackboard().get(DATASET_PARAM);
                currentTime = param.getBeginTime();
            } catch (Exception e) {
                throw new IllegalArgumentException("incorrect init parameters", e);
            } finally {
                tx.finish();
            }
        }

        /**
         * Gets the frame interval.
         * 
         * @return the frame interval
         */
        public short getFrameInterval() {
            return 100;
        }

        /**
         * Checks for next.
         * 
         * @return true, if successful
         */
        public boolean hasNext() {
            try {
                List<ILayer> layers = context.getMapLayers();
                boolean result = layers.contains(layer) && layers.contains(datasetLayer);
                if (result) {
                    param = (VisualiseParam)layer.getBlackboard().get(DATASET_PARAM);
                    result = param != null && (param.isRepeat() || param.getEndTime() >= currentTime);
                }
                if (!result) {
                    animation.remove(this);
                    layer.getMap().sendCommandASync(new DeleteLayerCommand((Layer)layer));
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                animation.remove(this);
                return false;
            }
        }

        /**
         * Next frame.
         */
        public void nextFrame() {
            currentTime += param.timeWindow;
            if (currentTime > param.getEndTime()) {
                currentTime = param.getBeginTime();
            }

        }

        /**
         * Run.
         * 
         * @param monitor the monitor
         * @throws Exception the exception
         */
        public void run(IProgressMonitor monitor) throws Exception {
            // long time = System.currentTimeMillis();
            // try {
            this.monitor = monitor;
            cansel = monitor.isCanceled();
            // only draw if layer is visible
            if (layer.isVisible()) {
                Transaction tx = service.beginTx();
                try {
                    Node location = findLocation(currentTime);
                    if (location == null) {
                        LOGGER.warn(String.format("not found location on time [%s;%s)", currentTime, currentTime + param.timeWindow));
                        return;
                    }
                    Pair<MathTransform, MathTransform> driveTransform = setCrsTransforms(cRSDataset);// TODO
                    Coordinate locationC = new Coordinate((Double)location.getProperty(INeoConstants.PROPERTY_LON_NAME), (Double)location
                            .getProperty(INeoConstants.PROPERTY_LAT_NAME));
                    Coordinate world_location = new Coordinate();
                    try {
                        JTS.transform(locationC, world_location, transform_d2w);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // JTS.transform(location, world_location,
                        // transform_w2d.inverse());
                    }
                    java.awt.Point pSite = context.worldToPixel(world_location);
                    graphics.drawOval(pSite.x - 5, pSite.y - 5, 10, 10);
                } finally {

                }
            }
            // } finally {
            // time = System.currentTimeMillis() - time;
            // System.out.println(time);
            // }

        }

        /**
         * Find location.
         * 
         * @param currentTime the current time
         * @return the node
         */
        private Node findLocation(Long currentTime) {
            Traverser traverse = timestampIndex.searchTraverser(new Long[] {currentTime}, new Long[] {currentTime + param.timeWindow});
            Node location = null;
            for (Node node : traverse) {
                if (node.hasRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING)) {
                    location = node.getRelationships(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING).iterator().next().getOtherNode(node);
                    break;
                }
            }
            return location;
        }

        /**
         * Gets the valid area.
         * 
         * @return the valid area
         */
        public Rectangle getValidArea() {
            // to be more efficient we could set the area to draw so only the
            // area affected by this animation would be updated in the map
            // display but
            // for simplicity lets just trigger the entire viewport to redraw
            // (note that this is not
            // a re-render.
            return null;
        }

        /**
         * Dispose.
         */
        @Override
        public void dispose() {
            if (context.getMapLayers().contains(layer)) {
                context.getMap().sendCommandASync(new DeleteLayerCommand((Layer)layer));
            }

            System.out.println("dispose");
            animation.remove(this);
            // // we don't want to be disposed so lets run again
            // AnimationUpdater.runTimer(context.getMapDisplay(), this);
        }

        /**
         * Hash code.
         * 
         * @return the int
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((datasetLayer == null) ? 0 : datasetLayer.hashCode());
            result = prime * result + ((layer == null) ? 0 : layer.hashCode());
            return result;
        }

        /**
         * Equals.
         * 
         * @param obj the obj
         * @return true, if successful
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            VisualiseCommand other = (VisualiseCommand)obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (datasetLayer == null) {
                if (other.datasetLayer != null)
                    return false;
            } else if (!datasetLayer.equals(other.datasetLayer))
                return false;
            if (layer == null) {
                if (other.layer != null)
                    return false;
            } else if (!layer.equals(other.layer))
                return false;
            return true;
        }

        /**
         * Gets the outer type.
         * 
         * @return the outer type
         */
        private VisualiseLayer getOuterType() {
            return VisualiseLayer.this;
        }

        /**
         * Sets the crs transforms.
         * 
         * @param dataCrs the data crs
         * @return the pair
         * @throws FactoryException the factory exception
         */
        private Pair<MathTransform, MathTransform> setCrsTransforms(CoordinateReferenceSystem dataCrs) throws FactoryException {
            boolean lenient = true; // needs to be lenient to work on uDIG 1.1 (otherwise we get
            // error:
            // bursa wolf parameters required
            CoordinateReferenceSystem worldCrs = context.getCRS();
            Pair<MathTransform, MathTransform> oldTransform = new Pair<MathTransform, MathTransform>(transform_d2w, transform_w2d);
            this.transform_d2w = CRS.findMathTransform(dataCrs, worldCrs, lenient);
            this.transform_w2d = CRS.findMathTransform(worldCrs, dataCrs, lenient);
            return oldTransform;
        }
    }

    // mapclose
    /**
     * Run.
     * 
     * @param map the map
     */
    @Override
    public void run(Map map) {
        Iterator<VisualiseCommand> it = animation.iterator();
        while (it.hasNext()) {
            VisualiseCommand comand = it.next();
            if (comand.layer.getMap() == null || comand.layer.getMap().equals(map)) {
                map.sendCommandASync(new DeleteLayerCommand((Layer)comand.layer));
                it.remove();
            }
        }
    }

    /**
     * <p>
     * Container for mapgraphic properties
     * </p>
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class VisualiseParam {

        /** The begin time. */
        long beginTime;

        /** The end time. */
        long endTime;

        /** The time window. */
        long timeWindow;

        /** The is repeat. */
        boolean isRepeat;

        /**
         * Gets the begin time.
         * 
         * @return the begin time
         */
        public long getBeginTime() {
            return beginTime;
        }

        /**
         * Instantiates a new visualise param.
         */
        public VisualiseParam() {
            super();
        }

        public VisualiseParam(long beginTime, long endTime, boolean isRepeat, long timewindow) {
            this();
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.isRepeat = isRepeat;
            timeWindow = timewindow;
        }

        /**
         * Sets the begin time.
         * 
         * @param beginTime the new begin time
         */
        public void setBeginTime(long beginTime) {
            this.beginTime = beginTime;
        }

        /**
         * Gets the end time.
         * 
         * @return the end time
         */
        public long getEndTime() {
            return endTime;
        }

        /**
         * Sets the end time.
         * 
         * @param endTime the new end time
         */
        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        /**
         * Checks if is repeat.
         * 
         * @return true, if is repeat
         */
        public boolean isRepeat() {
            return isRepeat;
        }

        /**
         * Sets the repeat.
         * 
         * @param isRepeat the new repeat
         */
        public void setRepeat(boolean isRepeat) {
            this.isRepeat = isRepeat;
        }

        /**
         * Gets the time window.
         * 
         * @return the time window
         */
        public long getTimeWindow() {
            return timeWindow;
        }

        /**
         * Sets the time window.
         * 
         * @param timeWindow the new time window
         */
        public void setTimeWindow(long timeWindow) {
            this.timeWindow = timeWindow;
        }

    }
}
