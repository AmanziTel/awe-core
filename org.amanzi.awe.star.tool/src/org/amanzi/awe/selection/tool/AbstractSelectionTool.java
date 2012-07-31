package org.amanzi.awe.selection.tool;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.command.navigation.ZoomCommand;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public abstract class AbstractSelectionTool extends AbstractModalTool {

    private static final Logger LOGGER = Logger.getLogger(AbstractSelectionTool.class);

    /**
     * TODO rename NETWORK_properties_view Network properties id
     */
    public static final String PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.PropertiesView";

    private Coordinate startCoordinate;
    private Point startPoint;
    protected boolean dragged;
    protected IGeoResource iGeoResource;
    private net.refractions.udig.project.ui.commands.SelectionBoxCommand shapeCommand;

    public AbstractSelectionTool() {
        super(MOUSE | MOTION);
        dragged = false;
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    @Override
    public void mouseDragged(MapMouseEvent event) {
        Point endPoint = event.getPoint();
        dragged = true;
        if (startCoordinate == null) {
            return;
        }
        shapeCommand.setShape(new Rectangle(Math.min(startPoint.x, endPoint.x), Math.min(startPoint.y, endPoint.y), Math
                .abs(startPoint.x - endPoint.x), Math.abs(startPoint.y - endPoint.y)));
        context.getViewportPane().repaint();
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    @Override
    public void mousePressed(MapMouseEvent event) {
        startCoordinate = getContext().pixelToWorld(event.x, event.y);
        startPoint = event.getPoint();
        shapeCommand = new SelectionBoxCommand();
        shapeCommand.setValid(true);
        shapeCommand.setShape(new Rectangle(startPoint.x, startPoint.y, 0, 0));
        context.sendASyncCommand(shapeCommand);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    @Override
    public void mouseReleased(MapMouseEvent event) {
        IDataModel model = getAnalyzedModel();
        Envelope selectionBounds = new Envelope(startCoordinate, getContext().pixelToWorld(event.x, event.y));
        handleSelection(model, selectionBounds, event.getPoint());
        if (dragged) {
            zoomIn(getContext().getViewportModel(), calculateZoomInRectangle(event));
        }
        dragged = false;
        shapeCommand.setValid(false);
        getContext().getViewportPane().repaint();
    }

    /**
     * Zoom in command
     * 
     * @param viewPort view port
     * @param rectangle rectangle
     */
    private void zoomIn(IViewportModel viewPort, Rectangle rectangle) {
        ZoomCommand cmd = new ZoomCommand(getContext().getMapDisplay().getDisplaySize().getWidth() / rectangle.width);
        cmd.setFixedPoint(viewPort.pixelToWorld(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2));
        getContext().sendASyncCommand(cmd);
    }

    /**
     * Zoom in rectangle calculation
     * 
     * @param event map mouse event
     * @return rectangle
     */
    private Rectangle calculateZoomInRectangle(MapMouseEvent event) {
        if (startPoint == null)
            mousePressed(event);
        int x1 = startPoint.x;
        int x2 = event.x;
        int y1 = startPoint.y;
        int y2 = event.y;
        int width1, height1;
        int width2, height2;
        int width, height;
        height1 = Math.abs(y2 - y1);
        width1 = (int)(height1 * context.getViewportModel().getAspectRatio());
        width2 = Math.abs(x2 - x1);
        height2 = (int)(width2 / context.getViewportModel().getAspectRatio());
        // choose heights and widths based on which axis is the longest
        if (height1 > height2) {
            width = width1;
            height = height1;
        } else {
            width = width2;
            height = height2;
        }

        // center user selected area in center of new box.
        int x = x1, y = y1;
        if (x1 > x2) {
            x = x1 - width + (width - Math.abs(x2 - x1)) / 2;
        } else {
            x = x - (width - Math.abs(x2 - x1)) / 2;
        }
        if (y1 > y2) {
            y = y1 - height + (height - Math.abs(y2 - y1)) / 2;
        } else {
            y = y - (height - Math.abs(y2 - y1)) / 2;
        }

        return new Rectangle(x, y, width, height);
    }

    /**
     * Searching analyzed model
     * 
     * @return analyzed model or null
     * @throws AWEException
     */
    protected IDataModel getAnalyzedModel() {
        for (ILayer layer : context.getMapLayers()) {
            IGeoResource resource = layer.findGeoResource(IDataModel.class);
            if (resource == null) {
                continue;
            }
            try {
                IDataModel resolvedElement = resource.resolve(IDataModel.class, null);
                iGeoResource = resource;
                ((IRenderableModel)resolvedElement).clearSelectedElements();
                return resolvedElement;
            } catch (IOException e) {
                LOGGER.error("Selection Tool: resolved resource was unavailable due to a technical problem. " + e);
            }
        }
        return null;
    }

    /**
     * Handle selected elements according to the model to which they belong.
     * 
     * @param model
     * @param selectionBounds
     * @param point Contains coordinates of clicked element on map.
     */
    protected abstract void handleSelection(IDataModel model, Envelope selectionBounds, Point point);
}
