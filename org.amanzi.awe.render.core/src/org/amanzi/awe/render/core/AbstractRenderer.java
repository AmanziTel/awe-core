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

package org.amanzi.awe.render.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.awe.models.catalog.neo.GeoResource;
import org.amanzi.awe.neostyle.BaseNeoStyle;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IMeasurementModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class AbstractRenderer extends RendererImpl {
    private static Logger LOGGER = Logger.getLogger(AbstractRenderer.class);
    protected static BaseNeoStyle style;

    private MathTransform transform_d2w;
    private MathTransform transform_w2d;

    /**
     * commonly describe style of rendered elements
     */
    public static class RenderOptions {
        public static Scale scale = Scale.MEDIUM;
        public static int alpha = (int)(0.6 * 255.0);
        public static int largeSectorsSize = 30;
        public static int siteSize = 10;
        public static Color border = Color.BLACK;
        public static Color siteFill = new Color(128, 128, 128, alpha);
        public static Color sectorFill = new Color(255, 255, 128, alpha);
        public static boolean antialiazing = true;
        public static int maxSitesLabel = 50;
        public static int maxSitesFull = 100;
        public static int maxSitesLite = 1000;
        public static int maxSymbolSize = 40;
        public static boolean drawLabels = false;
        public static boolean scaleSymbols = true;
    }

    public void setScaling(Envelope bounds_transformed, Envelope data_bounds, final IProgressMonitor monitor, long count) {
        double dataScaled = (bounds_transformed.getHeight() * bounds_transformed.getWidth())
                / (data_bounds.getHeight() * data_bounds.getWidth());

        double countScaled = dataScaled * count / 2;
        RenderOptions.drawLabels = countScaled < RenderOptions.maxSitesLabel;
        if (countScaled < RenderOptions.maxSitesFull) {
            RenderOptions.scale = Scale.LARGE;
        } else if (countScaled > RenderOptions.maxSitesLite) {
            RenderOptions.scale = Scale.SMALL;
        } else {
            RenderOptions.scale = Scale.MEDIUM;
        }
        if (RenderOptions.scale.equals(Scale.LARGE) && RenderOptions.scaleSymbols) {
            RenderOptions.largeSectorsSize *= Math.sqrt(RenderOptions.maxSitesFull) / (3 * Math.sqrt(countScaled));
            RenderOptions.largeSectorsSize = Math.min(RenderOptions.largeSectorsSize, RenderOptions.maxSymbolSize);
        }

        bounds_transformed.expandBy(0.75 * (bounds_transformed.getHeight() + bounds_transformed.getWidth()));
    }

    public static final String BLACKBOARD_NODE_LIST = "org.amanzi.awe.tool.star.StarTool.nodes";

    @Override
    public void render(Graphics2D destination, IProgressMonitor monitor) throws RenderException {
        ILayer layer = getContext().getLayer();
        IGeoResource resource = layer.findGeoResource(GeoResource.class);
        // c+v
        layer.getMap().getBlackboard().get(BLACKBOARD_NODE_LIST);
        if (resource != null) {
            try {
                renderGeoResource(destination, resource, monitor);
            } catch (AWEException e) {
                LOGGER.error("Could not render resource.", e);
            }
        }
    }

    /**
     * @param destination
     * @param resource
     * @param monitor
     * @throws AWEException
     */
    private void renderGeoResource(Graphics2D destination, IGeoResource resource, IProgressMonitor monitor) throws RenderException,
            AWEException {

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        // TODO: Get size from info (???)
        monitor.beginTask("render network sites and sectors: " + resource.getIdentifier(), IProgressMonitor.UNKNOWN);

        try {

            setStyle(destination);

            // find a resource to render
            IRenderableModel model = resource.resolve(IRenderableModel.class, monitor);
            // get rendering bounds and zoom
            setCrsTransforms(resource.getInfo(null).getCRS());
            Envelope bounds_transformed = getTransformedBounds();
            Envelope data_bounds = model.getBounds();

            // TODO: refactor
            if (bounds_transformed == null) {
                RenderOptions.scale = Scale.MEDIUM;
            } else if (data_bounds != null && data_bounds.getHeight() > 0 && data_bounds.getWidth() > 0) {
                if (model instanceof INetworkModel) {
                    long count = ((INetworkModel)model).getNodeCount(NetworkElementNodeType.SITE) / 2;// TODO:
                    // TODO: LN: this property should be moved to another preference store
                    // if (NeoLoaderPlugin
                    // .getDefault()
                    // .getPreferenceStore()
                    // .getBoolean(
                    // DataLoadPreferences.NETWORK_COMBINED_CALCULATION)) {
                    // double density = getAverageDensity(monitor);
                    // if (density > 0)
                    // count = (long) (density * data_bounds.getHeight() * data_bounds
                    // .getWidth());
                    // }
                    setScaling(bounds_transformed, data_bounds, monitor, count);
                } else if (model instanceof IMeasurementModel) {
                    long count = ((IMeasurementModel)model).getNodeCount(((IMeasurementModel)model).getPrimaryType()) / 2;
                    setScaling(bounds_transformed, data_bounds, monitor, count);
                }
            }
            // TODO: selection

            for (IDataElement element : model.getElements(data_bounds)) {
                Coordinate location = model.getCoordinate(element);
                if (location == null) {
                    continue;
                }
                Coordinate world_location = new Coordinate();
                if (bounds_transformed != null && !bounds_transformed.contains(location)) {
                    continue; // Don't draw points outside viewport
                }
                try {
                    JTS.transform(location, world_location, transform_d2w);
                } catch (Exception e) {
                    JTS.transform(location, world_location, transform_w2d.inverse());
                }

                java.awt.Point p = getContext().worldToPixel(world_location);

                renderElement(destination, p, element, model);

                monitor.worked(1);
                // count++;
                if (monitor.isCanceled())
                    break;
            }

        } catch (IOException e) {
            LOGGER.error("Could not relosve resource.", e);
            throw new RenderException(e);
        } catch (TransformException e) {
            LOGGER.error("Could not transform bounds.", e);
            throw new RenderException(e);
        } catch (FactoryException e) {
            LOGGER.error("Could not set CRS transforms.", e);
            throw new RenderException(e);
        }
    }

    /**
     * set default style to destination
     */
    protected void setStyle(Graphics2D destination) {
        if (RenderOptions.antialiazing) {
            destination.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            destination.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
    }

    /**
     * @param destination
     * @param point
     * @param element
     */
    protected void renderElement(Graphics2D destination, Point point, IDataElement element, IRenderableModel model) {
        destination.setColor(Color.BLACK);
        destination.drawOval(point.x - 1, point.y - 1, 2, 2);
    }

    @Override
    public void render(IProgressMonitor monitor) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    private void setCrsTransforms(CoordinateReferenceSystem dataCrs) throws FactoryException {
        boolean lenient = true; // needs to be lenient to work on uDIG 1.1
                                // (otherwise we get error:
                                // bursa wolf parameters required
        CoordinateReferenceSystem worldCrs = context.getCRS();
        this.transform_d2w = CRS.findMathTransform(dataCrs, worldCrs, lenient);
        this.transform_w2d = CRS.findMathTransform(worldCrs, dataCrs, lenient); // could
                                                                                // use
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

    protected Color changeColor(Color color, int toAlpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), toAlpha);
    }

    /**
     * gets average count of geoNeo.getCount() from all resources in map
     * 
     * @param data_bounds
     * @return average count
     */
    protected double getAverageDensity(IProgressMonitor monitor) {
        double result = 0;
        long count = 0;
        try {
            for (ILayer layer : getContext().getMap().getMapLayers()) {
                if (layer.getGeoResource().canResolve(IMeasurementModel.class)) {
                    IMeasurementModel resource = layer.getGeoResource().resolve(IMeasurementModel.class, monitor);
                    Envelope dbounds = resource.getBounds();
                    if (dbounds != null) {
                        result += calculateResult(dbounds, resource);
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while try to return average density, return 0", e);
            AweConsolePlugin.error("Error while try to return average density, return 0 ");
            return 0;
        }
        return result / (double)count;
    }

    /**
     * calculate average between necessary nodes count and size
     * 
     * @param dbounds
     * @param resource
     * @return
     */
    protected double calculateResult(Envelope dbounds, IMeasurementModel resource) {
        return 0d;
    }
}
