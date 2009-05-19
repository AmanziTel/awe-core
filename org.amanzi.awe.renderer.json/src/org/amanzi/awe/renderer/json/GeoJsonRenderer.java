package org.amanzi.awe.renderer.json;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.amanzi.awe.catalog.json.Feature;
import org.amanzi.awe.catalog.json.FeatureIterator;
import org.amanzi.awe.catalog.json.JSONGeoFeatureType;
import org.amanzi.awe.catalog.json.JSONReader;
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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Custom renderer for GeoJson files.
 * 
 * @author Milan Dinic
 */
public class GeoJsonRenderer extends RendererImpl {

    /**
     * Rendering method called by system.
     * 
     * @param monitor reference to a {@link IProgressMonitor} implementation
     * @exception RenderException error occurred while rendering
     */
    @Override
    public final void render( final IProgressMonitor monitor ) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    /**
     * Drawing operations are done in this method.
     * 
     * @param g {@link Graphics2D} object
     * @param monitor reference to a {@link IProgressMonitor} implementation
     * @exception RenderException error occurred while rendering
     */
    public final void render( final Graphics2D g, IProgressMonitor monitor ) throws RenderException {

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        monitor.beginTask("geojson render", 100);

        try {
            g.setColor(Color.BLACK);
            ILayer layer = getContext().getLayer();
            IGeoResource resource = layer.findGeoResource(JSONReader.class);
            if (resource == null) {
                return;
            }

            CoordinateReferenceSystem dataCRS = layer.getCRS();
            CoordinateReferenceSystem worldCRS = context.getCRS();
            MathTransform dataToWorld = CRS.findMathTransform(dataCRS, worldCRS, false);

            ReferencedEnvelope bounds = getRenderBounds();
            monitor.subTask("connecting");

            final JSONReader geoJson = resource.resolve(JSONReader.class, new SubProgressMonitor(
                    monitor, 10));

            final IProgressMonitor drawMonitor = new SubProgressMonitor(monitor, 90);
            Coordinate worldLocation = new Coordinate();

            FeatureIterator features = geoJson.getFeatures();

            for( Feature feature : features ) {
                boolean containsSelector = feature.getProperties().containsKey("sectors");
                if (containsSelector) {
                    // do not render
                    return;
                }
                Geometry geometry = feature.createGeometry();

                Coordinate dataLocation = ((Geometry) geometry).getCoordinate();
                try {
                    JTS.transform(dataLocation, worldLocation, dataToWorld);
                } catch (TransformException e) {
                    continue;
                }
                if (bounds != null && !bounds.contains(worldLocation)) {
                    continue; // optimize!
                }

                final JSONGeoFeatureType type = JSONGeoFeatureType.fromCode(feature.getType());
                switch( type ) {
                case POINT:
                    drawPoint(g, (Point) geometry);
                    break;
                case MULTI_POINT:
                    final MultiPoint multiPoint = (MultiPoint) geometry;
                    int numPoints = multiPoint.getNumPoints();
                    for( int i = 0; i < numPoints; i++ ) {
                        final Point point = (Point) multiPoint.getGeometryN(i);
                        drawPoint(g, point);
                    }
                    break;
                case LINE:
                    drawLineString(g, (LineString) geometry);
                    break;
                case MULTI_LINE_STRING:
                    final MultiLineString multiLineString = (MultiLineString) geometry;
                    int numGeometries = multiLineString.getNumGeometries();
                    for( int i = 0; i < numGeometries; i++ ) {
                        final LineString lineString = (LineString) multiLineString.getGeometryN(i);
                        drawLineString(g, lineString);
                    }
                    break;
                case POLYGON:
                    drawPolygon(g, (Polygon) geometry);
                    break;
                case MULTI_POLYGON:
                    final MultiPolygon multiPolygon = (MultiPolygon) geometry;
                    for( int i = 0; i < multiPolygon.getNumGeometries(); i++ ) {
                        drawPolygon(g, (Polygon) multiPolygon.getGeometryN(i));
                    }
                    break;
                default:
                    break;
                }

                if (drawMonitor.isCanceled()) {
                    break;
                }
            }
            drawMonitor.done();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RenderException(e);
            // rethrow any exceptions encountered
        } catch (FactoryException e) {
            e.printStackTrace();
            throw new RenderException(e);
            // rethrow any exception encountered
        } finally {
            monitor.done();
        }
    }

    /**
     * Draws given point on graphics2D.
     * 
     * @param g {@link Graphics2D} object
     * @param point {@link Point} object
     */
    private void drawPoint( final Graphics2D g, final Point point ) {
        final java.awt.Point p = getAwtPoint(point);
        g.fillOval(p.x, p.y, 2, 2);
    }

    /**
     * Draws given polygon on graphics2D.
     * 
     * @param g {@link Graphics2D} object
     * @param polygon {@link Polygon} object
     */
    private void drawPolygon( final Graphics2D g, final Polygon polygon ) {
        final LineString exteriorRing = polygon.getExteriorRing();
        int numPoints = exteriorRing.getNumPoints();
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];
        for( int i = 0; i < numPoints; i++ ) {
            Point point = exteriorRing.getPointN(i);
            java.awt.Point awtPoint = getAwtPoint(point);
            xpoints[i] = awtPoint.x;
            ypoints[i] = awtPoint.y;
        }
        java.awt.Polygon p = new java.awt.Polygon(xpoints, ypoints, numPoints);
        g.drawPolygon(p);
    }

    /**
     * Draws {@link LineString} object on g from given points.
     * 
     * @param g {@link Graphics2D} object
     * @param lineString {@link LineString} object
     */
    private void drawLineString( final Graphics2D g, final LineString lineString ) {
        final int numPoints = lineString.getNumPoints();
        Point startPoint = lineString.getPointN(0);
        for( int i = 1; i < numPoints; i++ ) {
            final Point pointN = lineString.getPointN(i);
            drawLine(g, startPoint, pointN);
            startPoint = pointN;
        }
    }

    /**
     * Draws line on g from given points.
     * 
     * @param g {@link Graphics} object
     * @param startPoint {@link Point} object
     * @param endPoint {@link Point} object
     */
    private void drawLine( final Graphics g, final Point startPoint, final Point endPoint ) {

        final java.awt.Point awtStartPoint = getAwtPoint(startPoint);
        final java.awt.Point awtEndPoint = getAwtPoint(endPoint);

        g.drawLine(awtStartPoint.x, awtStartPoint.y, awtEndPoint.x, awtEndPoint.y);
    }

    /**
     * Converts {@link Point} object to {@link java.awt.Point} object.
     * 
     * @param point {@link Point} object
     * @return {@link java.awt.Point} object
     */
    private java.awt.Point getAwtPoint( final Point point ) {
        return getContext().worldToPixel(point.getCoordinate());
    }

}
