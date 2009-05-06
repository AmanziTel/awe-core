package org.amanzi.awe.render.sites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.amanzi.awe.catalog.csv.CSV;
import org.amanzi.awe.catalog.json.JSONReader;
import org.amanzi.awe.catalog.json.JSONReader.Feature;
import org.amanzi.awe.views.network.utils.TreeViewContentProvider;
import org.amanzi.awe.views.network.utils.ViewLabelProvider;
import org.amanzi.awe.views.network.views.NetworkTreeView;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.csvreader.CsvReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;

public class SitesRenderer extends RendererImpl {
    private static final Color SELECTION_COLOR = Color.RED;
    private AffineTransform base_transform = null; // save original graphics transform for repeated
    // re-use
    private Color drawColor = Color.DARK_GRAY;
    private Color fillColor = new Color(120, 255, 170);
    private MathTransform transform_d2w;
    private MathTransform transform_w2d;

    private List<String> selectedTreeItems;

    private void setCrsTransforms( final CoordinateReferenceSystem dataCrs )
            throws FactoryException {
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
     * This method is called to render what it can. It is passed a graphics context with which it
     * can draw. The class already contains a reference to a RenderContext from which it can obtain
     * the layer and the GeoResource to render.
     * 
     * @see net.refractions.udig.project.internal.render.impl.RendererImpl#render(java.awt.Graphics2D,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public final void render( final Graphics2D g, final IProgressMonitor monitor )
            throws RenderException {
        ILayer layer = getContext().getLayer();
        // Are there any resources in the layer that respond to the CSV class (should be the case if
        // we added any *.csv files)
        // See CSVGeoResource class in csv plugin
        IGeoResource resource = layer.findGeoResource(JSONReader.class);
        if (resource == null) {
            resource = layer.findGeoResource(CSV.class);
            if (resource != null) {
                renderCSV(g, resource, monitor);
            }
        } else {
            renderJSON(g, resource, monitor);
        }
    }

    /**
     * This method is called to render data from the basic CSV geo resource.
     */
    private void renderCSV( final Graphics2D g, final IGeoResource csvGeoResource,
            IProgressMonitor monitor ) throws RenderException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask("render network sites and sectors", IProgressMonitor.UNKNOWN);

        CsvReader reader = null;
        try {
            monitor.subTask("connecting");
            CSV csv = csvGeoResource.resolve(CSV.class, new SubProgressMonitor(monitor, 10));

            reader = csv.reader();
            reader.readHeaders();

            g.setColor(drawColor);

            setCrsTransforms(csvGeoResource.getInfo(null).getCRS());
            Envelope bounds_transformed = getTransformedBounds();

            int count = 0;
            monitor.subTask("drawing");
            int nameIndex = reader.getIndex("name");
            Coordinate world_location = new Coordinate(); // single object for re-use in transform
            // below (minimize object creation)
            while( reader.readRecord() ) {
                Point point = CSV.getPoint(reader);
                Coordinate location = point.getCoordinate();

                if (bounds_transformed != null && !bounds_transformed.contains(location)) {
                    continue; // Don't draw points outside viewport
                }
                try {
                    JTS.transform(location, world_location, transform_d2w);
                } catch (Exception e) {
                    // JTS.transform(location, world_location, transform_w2d.inverse());
                }

                java.awt.Point p = getContext().worldToPixel(world_location);
                renderSite(g, p);
                try {
                    for( int s = 0; s < 3; s++ ) {
                        double azimuth = (10.0 * count) - (120.0 * s);
                        renderSector(g, p, azimuth, 60.0);
                    }
                } finally {
                    if (base_transform != null) {
                        // recover the normal transform
                        g.setTransform(base_transform);
                        g.setColor(drawColor);
                    }
                }
                g.drawString(reader.get(nameIndex), p.x + 10, p.y + 10);
                monitor.worked(1);
                count++;
                if (monitor.isCanceled()) {
                    break;
                }
            }
        } catch (TransformException e) {
            throw new RenderException(e);
        } catch (FactoryException e) {
            throw new RenderException(e);
        } catch (IOException e) {
            throw new RenderException(e); // rethrow any exceptions encountered
        } finally {
            if (reader != null) {
                reader.close();
            }
            monitor.done();
        }
    }

    /**
     * This method is called to render data from the JSON Geo-Resource.
     */
    private void renderJSON( final Graphics2D g, final IGeoResource jsonGeoResource,
            IProgressMonitor monitor ) throws RenderException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask("render network sites and sectors", IProgressMonitor.UNKNOWN);
        // TODO: Get size from info

        try {

            monitor.subTask("connecting");

            Display display = PlatformUI.getWorkbench().getDisplay();
            display.syncExec(new Runnable(){

                public void run() {
                    final IWorkbenchWindow window = PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow();
                    try {
                        final NetworkTreeView viewPart = (NetworkTreeView) window.getActivePage()
                                .showView(NetworkTreeView.NETWORK_VIEW_ID);
                        selectedTreeItems = viewPart.getSelectedTreeItems();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            JSONReader jsonReader = jsonGeoResource.resolve(JSONReader.class,
                    new SubProgressMonitor(monitor, 10));

            setCrsTransforms(jsonGeoResource.getInfo(null).getCRS());
            Envelope bounds_transformed = getTransformedBounds();

            g.setColor(drawColor);
            int count = 0;
            monitor.subTask("drawing");
            Coordinate world_location = new Coordinate(); // single object for re-use in transform
            // below (minimize object creation)

            for( Feature feature : jsonReader.getFeatures() ) {
                Point[] points = feature.getPoints();
                Point point = points[0]; // TODO: Support multi-point geometries
                Coordinate location = point.getCoordinate();

                if (bounds_transformed != null && !bounds_transformed.contains(location)) {
                    continue; // Don't draw points outside viewport
                }
                try {
                    JTS.transform(location, world_location, transform_d2w);
                } catch (Exception e) {
                    // JTS.transform(location, world_location, transform_w2d.inverse());
                }

                java.awt.Point p = getContext().worldToPixel(world_location);
                renderSite(g, p);

                double[] label_position_angles = new double[]{0, 90};
                Map<String, Object> properties = feature.getProperties();

                String bsc = (String) properties.get("bsc");
                boolean selected = false;
                if (!selectedTreeItems.isEmpty()) {
                    if (selectedTreeItems.contains(bsc)
                            || selectedTreeItems.contains(feature.toString())) {
                        selected = true;
                    }
                }

                if (properties != null) {
                    try {
                        if (properties.containsKey("sectors")) {
                            Object sectorsObj = properties.get("sectors");
                            System.out.println("Found sectors: " + sectorsObj);
                            if (sectorsObj instanceof JSONArray) {
                                JSONArray sectors = (JSONArray) sectorsObj;
                                for( int s = 0; s < sectors.size(); s++ ) {
                                    JSONObject sector = sectors.getJSONObject(s);
                                    if (sector != null) {
                                        System.out.println("Sector: " + sector);
                                        JSONObject sectorProperties = sector
                                                .getJSONObject("properties");
                                        double azimuth = sectorProperties.getDouble("azimuth");
                                        double beamwidth = sectorProperties.getDouble("beamwidth");

                                        boolean selectedSector = false;

                                        if (sector.keySet().contains("name")) {
                                            if (selected) {
                                                selectedSector = true;
                                            } else {
                                                if (selectedTreeItems.contains(sector
                                                        .getString("name"))) {
                                                    selectedSector = true;
                                                }
                                            }
                                            renderSector(g, p, azimuth, beamwidth, selectedSector);
                                        } else {
                                            renderSector(g, p, azimuth, beamwidth);
                                        }

                                        if (s < label_position_angles.length) {
                                            label_position_angles[s] = azimuth;
                                        }
                                        // g.setColor(drawColor);
                                        // g.rotate(-Math.toRadians(beamwidth/2));
                                        // g.drawString(sector.getString("name"),20,0);
                                    }
                                }
                            } else {
                                System.err.println("sectors object is not a JSONArray: "
                                        + sectorsObj);
                            }
                        }
                    } finally {
                        if (base_transform != null) {
                            // recover the normal transform
                            g.setTransform(base_transform);
                            g.setColor(drawColor);
                        }
                    }
                }
                renderSelector(g, p, selected);

                double label_position_angle = Math.toRadians(-90
                        + (label_position_angles[0] + label_position_angles[1]) / 2.0);
                int label_x = 5 + (int) (10 * Math.cos(label_position_angle));
                int label_y = (int) (10 * Math.sin(label_position_angle));
                g.drawString(feature.toString(), p.x + label_x, p.y + label_y);
                g.setTransform(base_transform);
                monitor.worked(1);
                count++;
                if (monitor.isCanceled()) {
                    break;
                }
            }
            updateNetworkTreeView(jsonReader);
        } catch (TransformException e) {
            throw new RenderException(e);
        } catch (FactoryException e) {
            throw new RenderException(e);
        } catch (IOException e) {
            throw new RenderException(e); // rethrow any exceptions encountered
        } finally {
            // if (jsonReader != null)
            // jsonReader.close();
            monitor.done();
        }
    }
    /**
     * Below Code is added by Sachin P After loading the map, Network tree view should be shown.
     * Below code creates view in same UI thread and same renders a view which is populated with
     * geo_JSON data in tree format.
     */
    private void updateNetworkTreeView( final JSONReader jsonReader ) {
        Display display = PlatformUI.getWorkbench().getDisplay();
        display.syncExec(new Runnable(){

            public void run() {

                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                try {
                    // Finding if the view is opened.
                    IWorkbenchPart part = window.getActivePage().findView(
                            NetworkTreeView.NETWORK_VIEW_ID);
                    //TODO this is temp solution. method updateNetworkTreeView will be removed
                    if (!((NetworkTreeView) part).isInitialized()) {

                        if (part != null) {
                            window.getActivePage().hideView((IViewPart) part);
                        }

                        NetworkTreeView viewPart = (NetworkTreeView) window.getActivePage()
                                .showView(NetworkTreeView.NETWORK_VIEW_ID, null,
                                        IWorkbenchPage.VIEW_ACTIVATE);

                        viewPart.getViewer().setContentProvider(
                                new TreeViewContentProvider(jsonReader));
                        viewPart.getViewer().setLabelProvider(new ViewLabelProvider());
                        viewPart.getViewer().setInput(viewPart.getViewSite());
                        viewPart.makeActions();
                        viewPart.hookDoubleClickAction();
                        viewPart.setFocus();
                        viewPart.setSelectedTreeItems(selectedTreeItems);
                        viewPart.setInitialized(true);
                        window.getActivePage().activate(viewPart);
                    }

                } catch (PartInitException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void renderSector( final Graphics2D g, final java.awt.Point p, final double azimuth,
            final double beamwidth ) {
        renderSector(g, p, azimuth, beamwidth, false);
    }

    /**
     * Render the sector symbols based on the point and azimuth. We simply save the graphics
     * transform, then modify the graphics through the appropriate transformations (origin to site,
     * and rotations for drawing the lines and arcs).
     * 
     * @param g
     * @param p
     * @param azimuth
     * @param labelString used to check is this sector selected
     */
    private void renderSector( final Graphics2D g, final java.awt.Point p, final double azimuth,
            double beamwidth, final boolean selected ) {
        if (base_transform == null) {
            base_transform = g.getTransform();
        }
        if (beamwidth < 10) {
            beamwidth = 10;
        }

        g.setTransform(base_transform);
        g.translate(p.x, p.y);
        g.rotate(Math.toRadians(-90 + azimuth - beamwidth / 2.0));
        g.setColor(fillColor);
        if (selected) {
            g.setColor(SELECTION_COLOR);
        }

        g.fillArc(-20, -20, 40, 40, 0, -(int) beamwidth);
        g.setColor(drawColor);

        g.drawArc(-20, -20, 40, 40, 0, -(int) beamwidth);
        g.drawLine(0, 0, 20, 0);
        g.rotate(Math.toRadians(beamwidth));
        g.drawLine(0, 0, 20, 0);

        g.setColor(drawColor);
    }

    /**
     * This one is very simple, just draw a circle at the site location.
     * 
     * @param g
     * @param p
     */
    private void renderSite( final Graphics2D g, final java.awt.Point p ) {
        g.fillOval(p.x - 5, p.y - 5, 10, 10);
    }

    /**
     * Draws a red circle round the point that represents selection of site.
     * 
     * @param g {@link Graphics2D} object
     * @param p {@link java.awt.Point} object
     * @param labelString
     */
    private void renderSelector( final Graphics2D g, final java.awt.Point p, final boolean selected ) {
        if (selected) {
            g.setColor(SELECTION_COLOR);
            g.drawOval(p.x - 10, p.y - 10, 20, 20);
            g.setColor(drawColor);
        }
    }

    @Override
    public final void render( final IProgressMonitor monitor ) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

}
