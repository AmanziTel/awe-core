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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.amanzi.awe.models.catalog.neo.NewGeoResource;
import org.amanzi.neo.services.model.IDataElement;
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
    
    public static class RenderOptions{
        public static Scale scale = Scale.MEDIUM;
        
    }

    protected enum Scale {
        SMALL, MEDIUM, LARGE;
    }

    private static Logger LOGGER = Logger.getLogger(AbstractRenderer.class);

    private AffineTransform base_transform = null; // save original graphics transform for repeated
                                                   // re-use
    private MathTransform transform_d2w;
    private MathTransform transform_w2d;

    public static final String BLACKBOARD_NODE_LIST = "org.amanzi.awe.tool.star.StarTool.nodes";

    @Override
    public void render(Graphics2D destination, IProgressMonitor monitor) throws RenderException {
        ILayer layer = getContext().getLayer();
        IGeoResource resource = layer.findGeoResource(NewGeoResource.class);
        // c+v
        layer.getMap().getBlackboard().get(BLACKBOARD_NODE_LIST);
        if (resource != null) {
            renderGeoResource(destination, resource, monitor);
        }
    }

    /**
     * @param destination
     * @param resource
     * @param monitor
     */
    private void renderGeoResource(Graphics2D destination, IGeoResource resource, IProgressMonitor monitor) throws RenderException {

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        // TODO: Get size from info (???)
        monitor.beginTask("render network sites and sectors: " + resource.getIdentifier(), IProgressMonitor.UNKNOWN);

        try {
            // find a resource to render
            IRenderableModel model = resource.resolve(IRenderableModel.class, monitor);
            // get rendering bounds and zoom
            setCrsTransforms(resource.getInfo(null).getCRS());
            Envelope bounds_transformed = getTransformedBounds();
            Envelope data_bounds = model.getBounds();

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
                
                renderElement(destination, p, element);
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
     * @param destination
     * @param point
     * @param element
     */
    protected void renderElement(Graphics2D destination, Point point, IDataElement element) {
        destination.setColor(Color.BLACK);
        destination.drawOval(point.x - 1, point.y - 1, 2, 2);
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

}
