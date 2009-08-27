/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.awe.render.drive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.catalog.neo.GeoNeo.GeoNode;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
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
 * <p>
 * Renderer for GeoNeo with GisTypes==GisTypes.Tems
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class TemsRenderer extends RendererImpl implements Renderer {
    private MathTransform transform_d2w;
    private MathTransform transform_w2d;
    // private AffineTransform base_transform = null;
    private Color drawColor = Color.BLACK;
    private Color fillColor = Color.BLUE;// new Color(184,184,221);
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
        this.transform_w2d = CRS.findMathTransform(worldCrs, dataCrs, lenient); // could use
                                                                                // transform_d2w.inverse()
                                                                                // also
    }

    private Envelope getTransformedBounds() throws TransformException {
        ReferencedEnvelope bounds = getRenderBounds();
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
        monitor.beginTask("render network sites and sectors", IProgressMonitor.UNKNOWN);
        GeoNeo geoNeo = null;
        try {
            monitor.subTask("connecting");
            geoNeo = neoGeoResource.resolve(GeoNeo.class, new SubProgressMonitor(monitor, 10));
            String selectedProp = geoNeo.getPropertyName();
            Integer propertyValue = geoNeo.getPropertyValue();
            // Integer propertyAdjacency = geoNeo.getPropertyAdjacency();
            setCrsTransforms(neoGeoResource.getInfo(null).getCRS());
            Envelope bounds_transformed = getTransformedBounds();

            g.setColor(drawColor);
            int count = 0;
            monitor.subTask("drawing");
            Coordinate world_location = new Coordinate(); // single object for re-use in transform
                                                          // below (minimize object creation)
            for (GeoNode node : geoNeo.getGeoNodes()) {
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
                renderPoint(g, p);
                monitor.worked(1);
                count++;
                if (monitor.isCanceled())
                    break;
            }
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
        }
    }

    /**
     * This one is very simple, just draw a rectangle at the point location.
     * 
     * @param g
     * @param p
     */
    private void renderPoint(Graphics2D g, java.awt.Point p) {
        g.setColor(fillColor);
        g.fillRect(p.x - 3, p.y - 3, 7, 7);
        g.setColor(drawColor);
        g.drawRect(p.x - 3, p.y - 3, 7, 7);

    }
}
