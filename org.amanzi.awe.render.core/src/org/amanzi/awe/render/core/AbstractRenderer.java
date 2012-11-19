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
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.amanzi.awe.catalog.neo.resource.GeoResource;
import org.amanzi.awe.catalog.neo.selection.IMapSelection;
import org.amanzi.awe.neostyle.BaseNeoStyle;
import org.amanzi.awe.render.core.coloring.IColoringInterceptor;
import org.amanzi.awe.render.core.coloring.IColoringInterceptorFactory;
import org.amanzi.awe.render.core.coloring.internal.ColoringInterceptorsCache;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.models.render.IRenderableModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

import com.google.common.collect.Iterables;
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
public abstract class AbstractRenderer extends RendererImpl {
    private static final Logger LOGGER = Logger.getLogger(AbstractRenderer.class);
    protected static BaseNeoStyle style;

    protected IGISModel model;

    private MathTransform transform_d2w;
    private MathTransform transform_w2d;

    private final AbstractRendererStyles commonStyle = initDefaultRendererStyle();

    public static final String BLACKBOARD_NODE_LIST = "org.amanzi.awe.tool.star.StarTool.nodes";

    public static final String SPACE_SEPARATOR = " ";

    public static final String EQUAL_SEPARATOR = "=";

    private static final int DEFAULT_DRAW_SIZE = 15;

    private IMapSelection selection;

    private IColoringInterceptor colorer;

    private Collection<ILocationElement> locationElements;

    private Collection<IDataElement> selectedElements;

    protected double calculateCountScaled(final double dataScaled, final long count) {
        return dataScaled * count / 2;
    }

    /**
     * calculate average between necessary nodes count and size
     * 
     * @param dbounds
     * @param resource
     * @return
     */
    protected double calculateResult(final Envelope dbounds, final IGISModel resource) {
        return 0d;
    }

    /**
     * draw coordinate element(element which contain latitude and longitude properties for example
     * Site in Network Model or Mp in drive Model) on map
     * 
     * @param shape -shape of feature element
     * @param destination
     * @param size -current s
     * @param isFill - is need to feel shape
     */
    protected void drawCoordinateElement(final RenderShape shape, final Graphics2D destination, final Point point,
            final IDataElement element, final boolean isFill) {
        final int size = getSize();
        final int x = point.x - size;
        final int y = point.y - size;
        final Color color = commonStyle.changeColor(getColor(element), commonStyle.getAlpha());
        switch (shape) {
        case ELLIPSE:
            drawOval(destination, isFill, x, y, size, color);
            break;
        case RECTANGLE:
            drawRect(destination, isFill, x, y, size, color);
            break;
        default:
            break;
        }
    }

    /**
     * draw ellipse
     * 
     * @param destination
     * @param isFill
     * @param x
     * @param y
     * @param size
     * @param color
     */
    protected void drawOval(final Graphics2D destination, final boolean isFill, final int x, final int y, final int size,
            final Color color) {
        destination.setColor(commonStyle.getBorderColor());
        destination.drawOval(x, y, size, size);
        if (isFill) {
            destination.setColor(color);
            destination.fillOval(x, y, size, size);
        }

    }

    /**
     * draw rectangle
     * 
     * @param destination
     * @param isFill
     * @param x
     * @param y
     * @param size
     * @param color
     */
    protected void drawRect(final Graphics2D destination, final boolean isFill, final int x, final int y, final int size,
            final Color color) {
        if (isFill) {
            destination.setColor(color);
            destination.fillRect(x, y, size, size);
        } else {
            destination.setColor(commonStyle.getBorderColor());
            destination.drawRect(x, y, size, size);
        }
    }

    /**
     * get color from distribution ... if distribution not exist then return default border color
     * 
     * @param element
     * @return
     */
    protected Color getColor(final IDataElement element) {
        Color result = null;

        if (colorer != null) {
            result = colorer.getColor(element);
        }

        if (result == null) {
            result = getDefaultColor(element);
        }

        return result;
    }

    /**
     * return default color
     * 
     * @param element
     * @return
     */
    protected Color getDefaultColor(final IDataElement element) {
        switch (commonStyle.getScale()) {
        case MEDIUM:
        case SMALL:
            return commonStyle.getBorderColor();
        case LARGE:
            return getDefaultFillColorByElement(element);
        default:
            return null;
        }
    }

    /**
     * return default fill color for element;
     */
    protected abstract Color getDefaultFillColorByElement(IDataElement element);

    /**
     * Get point
     * 
     * @param model
     * @param element
     * @param bounds_transformed
     * @return point or null
     * @throws TransformException
     */
    protected Point getPoint(final IGISModel model, final ILocationElement element, final Envelope bounds_transformed)
            throws TransformException {

        final Coordinate location = new Coordinate(element.getLongitude(), element.getLatitude());
        java.awt.Point point = null;

        if (location != null && bounds_transformed != null && bounds_transformed.contains(location)) {
            final Coordinate world_location = new Coordinate();
            try {
                JTS.transform(location, world_location, transform_d2w);
            } catch (final Exception e) {
                JTS.transform(location, world_location, transform_w2d.inverse());
            }
            point = getContext().worldToPixel(world_location);
        }
        return point;
    }

    /**
     * return renderable element count
     * 
     * @param model2
     */
    protected abstract long getRenderableElementCount(IGISModel model);

    /**
     * return class which can be resolved with georesource
     * 
     * @return
     */
    protected abstract Class< ? extends IRenderableModel> getResolvedClass();

    /**
     * return default element size depends of scale
     * 
     * @return
     */
    protected int getSize() {
        switch (commonStyle.getScale()) {
        case MEDIUM:
            return commonStyle.getMediumElementSize() / 2;
        case LARGE:
            return commonStyle.getLargeElementSize() / 2;
        default:
            break;
        }
        return 1;
    }

    /**
     * get transformed bounds
     * 
     * @return
     * @throws TransformException
     */
    private Envelope getTransformedBounds() throws TransformException {
        ReferencedEnvelope bounds = getRenderBounds();
        if (bounds == null) {
            bounds = context.getViewportModel().getBounds();
        }
        Envelope bounds_transformed = null;
        if (bounds != null && transform_w2d != null) {
            bounds_transformed = JTS.transform(bounds, transform_w2d);
        }
        return bounds_transformed;
    }

    /**
     * Highlight selected items
     * 
     * @param destination
     * @param point point
     */
    protected void highlightSelectedItem(final Graphics2D destination, final java.awt.Point point) {
        int elementSize = getSize() * 2;
        final float radius = 60;
        final float[] fractions = {0.01f, 1.0f};
        for (; elementSize > 0; elementSize *= 0.8) {
            final Color[] colors = {commonStyle.changeColor(Color.CYAN, 5), commonStyle.changeColor(Color.WHITE, 5)};
            destination.setPaint(new RadialGradientPaint(point.x - elementSize / 3, point.y - elementSize / 3, radius, fractions,
                    colors));
            destination.fillOval((int)(point.x - elementSize * 2.25), (int)(point.y - elementSize * 2.25), 4 * elementSize,
                    4 * elementSize);
        }
    }

    /**
     * initialize default renderer styles;
     * 
     * @return
     */
    protected abstract AbstractRendererStyles initDefaultRendererStyle();

    protected boolean isSelected(final IDataElement element, final boolean locationsOnly, final boolean elementsOnly) {
        if (selection == null) {
            return false;
        } else {
            boolean isSelected = elementsOnly ? false : getSelectedLocations().contains(element);

            if (!isSelected && !locationsOnly) {
                isSelected |= getSelectedElements().contains(element);
            }

            return isSelected;
        }
    }

    private Collection<IDataElement> getSelectedElements() {
        if (selectedElements == null) {
            selectedElements = new ArrayList<IDataElement>();
            Iterables.addAll(selectedElements, selection.getSelectedElements());
        }

        return selectedElements;
    }

    private Collection<ILocationElement> getSelectedLocations() {
        if (locationElements == null) {
            locationElements = new ArrayList<ILocationElement>();
            Iterables.addAll(locationElements, selection.getSelectedLocations());
        }

        return locationElements;
    }

    @Override
    public void render(final Graphics2D destination, final IProgressMonitor monitor) throws RenderException {
        final ILayer layer = getContext().getLayer();
        final IGeoResource resource = layer.findGeoResource(GeoResource.class);

        // c+v
        selection = (IMapSelection)layer.getMap().getBlackboard().get(IMapSelection.SELECTION_BLACKBOARD_PROPERTY);
        selectedElements = null;
        locationElements = null;
        if (resource != null) {
            try {
                renderGeoResource(destination, resource, monitor);
            } catch (final ModelException e) {
                LOGGER.error("Could not render resource.", e);
            }
        }
    }

    @Override
    public void render(final IProgressMonitor monitor) throws RenderException {
        final Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    /**
     * render element based on latitude and longitude values;
     * 
     * @param destination
     * @param point
     * @param element
     */
    protected abstract void renderCoordinateElement(Graphics2D destination, Point point, IDataElement element);

    /**
     * render currentElement element (for example site or mp location element)
     * 
     * @param destination
     * @param point
     * @param element
     */
    protected abstract void renderElement(Graphics2D destination, Point point, ILocationElement element, IGISModel model)
            throws ModelException;

    /**
     * render elements from current model
     * 
     * @param destination
     * @throws TransformException
     * @throws AWEException
     * @throws NoninvertibleTransformException
     */
    private void renderElements(final Graphics2D destination, final Envelope bounds_transformed, final Envelope data_bounds,
            final IProgressMonitor monitor) throws NoninvertibleTransformException, ModelException, TransformException {
        for (final ILocationElement element : model.getElements(data_bounds)) {
            final Point point = getPoint(model, element, bounds_transformed);
            if (point != null) {
                renderElement(destination, point, element, model);
            } else {
                continue;
            }
            monitor.worked(1);
            // count++;
            if (monitor.isCanceled()) {
                break;
            }
        }
    }

    /**
     * render selected georesource
     * 
     * @param destination
     * @param resource
     * @param monitor
     * @throws AWEException
     */
    private void renderGeoResource(final Graphics2D destination, final IGeoResource resource, IProgressMonitor monitor)
            throws RenderException, ModelException {

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        // TODO: Get size from info (???)
        monitor.beginTask("render network sites and sectors: " + resource.getIdentifier(), IProgressMonitor.UNKNOWN);

        try {
            setStyle(destination);
            // find a resource to render
            model = resource.resolve(IGISModel.class, monitor);
            if (!model.canRender()) {
                return;
            }
            final IColoringInterceptorFactory colorerFactory = ColoringInterceptorsCache.getCache().getFactory(model);
            if (colorerFactory != null) {
                colorer = colorerFactory.createInterceptor(model);
            }

            if (selection != null && !selection.getModel().getAllGIS().contains(model)) {
                selection = null;
            }

            // get rendering bounds and zoom
            setCrsTransforms(resource.getInfo(null).getCRS());
            final Envelope bounds_transformed = getTransformedBounds();
            final Envelope data_bounds = model.getBounds();
            Long count;
            if (bounds_transformed == null) {
                commonStyle.setScale(Scale.MEDIUM);
            } else if (data_bounds != null && data_bounds.getHeight() > 0 && data_bounds.getWidth() > 0) {
                count = getRenderableElementCount(model);
                setScaling(bounds_transformed, data_bounds, monitor, count);
            }
            renderElements(destination, bounds_transformed, data_bounds, monitor);
        } catch (final IOException e) {
            LOGGER.error("Could not relosve resource.", e);
            throw new RenderException(e);
        } catch (final TransformException e) {
            LOGGER.error("Could not transform bounds.", e);
            throw new RenderException(e);
        } catch (final FactoryException e) {
            LOGGER.error("Could not set CRS transforms.", e);
            throw new RenderException(e);
        }
    }

    /**
     * set transforms
     * 
     * @param dataCrs
     * @throws FactoryException
     */
    private void setCrsTransforms(final CoordinateReferenceSystem dataCrs) throws FactoryException {
        final boolean lenient = true; // needs to be lenient to work on uDIG 1.1
        // (otherwise we get error:
        // bursa wolf parameters required
        final CoordinateReferenceSystem worldCrs = context.getCRS();
        transform_d2w = CRS.findMathTransform(dataCrs, worldCrs, lenient);
        transform_w2d = CRS.findMathTransform(worldCrs, dataCrs, lenient); // could
        // use
        // transform_d2w.inverse()
        // also
    }

    /**
     * set requirement to draw labels
     * 
     * @param countScaled
     */
    protected abstract void setDrawLabel(double countScaled);

    /**
     * prepare scaling value before elements render. Scale is response for the view of renderable
     * elements
     * 
     * @param bounds_transformed
     * @param data_bounds
     * @param monitor
     * @param count
     */
    public void setScaling(final Envelope bounds_transformed, final Envelope data_bounds, final IProgressMonitor monitor,
            final long count) {
        final double dataScaled = bounds_transformed.getHeight() * bounds_transformed.getWidth()
                / (data_bounds.getHeight() * data_bounds.getWidth());

        final double countScaled = calculateCountScaled(dataScaled, count);
        setDrawLabel(countScaled);
        if (countScaled < commonStyle.getMaxElementsFull()) {
            commonStyle.setScale(Scale.LARGE);
        } else if (countScaled > commonStyle.getMaxElementsLite()) {
            commonStyle.setScale(Scale.SMALL);
        } else {
            commonStyle.setScale(Scale.MEDIUM);
        }
        if (commonStyle.getScale().equals(Scale.LARGE) && commonStyle.isScaleSymbols()) {
            int size = DEFAULT_DRAW_SIZE;
            size *= Math.sqrt(commonStyle.getMaxElementsFull()) / (3 * Math.sqrt(countScaled));
            size = Math.min(size, commonStyle.getMaxSymbolSize());
            commonStyle.setLargeElementSize(size);
        }
        bounds_transformed.expandBy(0.75 * (bounds_transformed.getHeight() + bounds_transformed.getWidth()));
    }

    /**
     * set default style to destination
     */
    protected void setStyle(final Graphics2D destination) {
        if (commonStyle.isAntialiazing()) {
            destination.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            destination.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
    }
}
