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

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class VisualiseLayer implements MapGraphic, MapInterceptor {
    /**
     * 
     */
    public VisualiseLayer() {
        super();

    }
    public static final Logger LOGGER = Logger.getLogger(VisualiseLayer.class);
    public static final Long TIME_STEP = 10000l;
    // TODO check synchronize
    // TODO add listener on remove map!
    private static final Set<VisualiseCommand> animation = new HashSet<VisualiseCommand>();
    private final GraphDatabaseService service = NeoServiceProvider.getProvider().getService();

    @Override
    public void draw(MapGraphicContext context) {
        ILayer datasetLayer = findDatasetLayer(context);
        if (datasetLayer == null) {
            return;
        }

        ILayer layer = context.getLayer();
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
     * @param context
     * @return
     */
    private ILayer findDatasetLayer(MapGraphicContext context) {
        Transaction tx = service.beginTx();
        ILayer result = null;
        try {
            for (ILayer layer : context.getMapLayers()) {
                if (layer.getGeoResource().canResolve(Node.class)) {
                    Node node = layer.getGeoResource().resolve(Node.class, null);
                    node = NeoUtils.findRoot(node, service);
                    if (NeoUtils.isDatasetNode(node) || NodeTypes.OSS.checkNode(node)) {
                        result = layer;
                        break;
                    }
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            tx.finish();
        }
    }

    private class VisualiseCommand extends AbstractDrawCommand implements IAnimation {
        private final ILayer datasetLayer;
        private final ILayer layer;
        private final MapGraphicContext context;
        private boolean cansel;
        private IProgressMonitor monitor = new NullProgressMonitor();
        private Node rootNode;
        private Pair<Long, Long> minMax;
        private MultiPropertyIndex<Long> timestampIndex;
        private Long currentTime;
        private MathTransform transform_d2w;
        private MathTransform transform_w2d;
        private CoordinateReferenceSystem cRSDataset;

        public VisualiseCommand(ILayer layer, ILayer datasetLayer, MapGraphicContext context) throws IllegalArgumentException {
            this.layer = layer;
            this.datasetLayer = datasetLayer;
            this.context = context;
            cansel = false;
            init();

        }

        private void init() {
            Transaction tx = service.beginTx();
            try {
                Node node = datasetLayer.getGeoResource().resolve(Node.class, null);
                cRSDataset = datasetLayer.getGeoResource().getInfo(new NullProgressMonitor()).getCRS();
                rootNode = NeoUtils.findRoot(node, service);
                minMax = NeoUtils.getMinMaxTimeOfDataset(rootNode, service);
                if (minMax == null || minMax.getLeft() == null || minMax.getRight() == null) {
                    throw new IllegalArgumentException("incorrect init parameters: minmax time");
                }
                String datasetName = NeoUtils.getNodeName(node, service);
                timestampIndex = NeoUtils.getTimeIndexProperty(datasetName);
                timestampIndex.initialize(NeoServiceProvider.getProvider().getService(), null);
                currentTime = minMax.getRight();
            } catch (Exception e) {
                throw new IllegalArgumentException("incorrect init parameters", e);
            } finally {
                tx.finish();
            }
        }

        public short getFrameInterval() {
            return 100;
        }

        public boolean hasNext() {
            List<ILayer> layers = context.getMapLayers();
            boolean result = layers.contains(layer) && layers.contains(datasetLayer);
            return result;
        }

        public void nextFrame() {
            currentTime += TIME_STEP;
            if (currentTime > minMax.getRight()) {
                currentTime = minMax.getLeft();
            }

        }

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
                        LOGGER.warn(String.format("not found location on time [%s;%s)", currentTime, currentTime + TIME_STEP));
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
         * @param currentTime2
         * @return
         */
        private Node findLocation(Long currentTime) {
            Traverser traverse = timestampIndex.searchTraverser(new Long[] {currentTime}, new Long[] {currentTime + TIME_STEP});
            Node location = null;
            for (Node node : traverse) {
                if (node.hasRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING)) {
                    location = node.getRelationships(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING).iterator().next().getOtherNode(node);
                    break;
                }
            }
            return location;
        }

        public Rectangle getValidArea() {
            // to be more efficient we could set the area to draw so only the
            // area affected by this animation would be updated in the map
            // display but
            // for simplicity lets just trigger the entire viewport to redraw
            // (note that this is not
            // a re-render.
            return null;
        }

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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((datasetLayer == null) ? 0 : datasetLayer.hashCode());
            result = prime * result + ((layer == null) ? 0 : layer.hashCode());
            return result;
        }

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

        private VisualiseLayer getOuterType() {
            return VisualiseLayer.this;
        }

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
    @Override
    public void run(Map map) {
        Iterator<VisualiseCommand> it = animation.iterator();
        while (it.hasNext()) {
            VisualiseCommand comand = it.next();
            if (comand.layer.getMap() == null || comand.layer.getMap().equals(map)) {
                it.remove();
            }
        }
    }

}
