package org.amanzi.awe.render.sites.tool;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.ui.PlatformGIS;

import org.amanzi.awe.catalog.json.Feature;
import org.amanzi.awe.catalog.json.FeatureIterator;
import org.amanzi.awe.catalog.json.JSONReader;
import org.amanzi.awe.views.network.views.NetworkTreeView;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class ArrowSelect extends SimpleTool implements ModalTool {

    private Point start;
    private SelectionBoxCommand shapeCommand;

    private boolean selecting;

    public ArrowSelect() {
        super(MOUSE | MOTION);
    }

    public void onMousePressed( MapMouseEvent e ) {
        shapeCommand = new SelectionBoxCommand();

        if (((e.button & MapMouseEvent.BUTTON1) != 0)) {
            selecting = true;

            start = e.getPoint();
            shapeCommand.setValid(true);
            shapeCommand.setShape(new Rectangle(start.x, start.y, 0, 0));
            context.sendASyncCommand(shapeCommand);
        }
    }

    protected void onMouseDragged( MapMouseEvent e ) {
        Point end = e.getPoint();
        shapeCommand.setShape(new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y),
                Math.abs(start.x - end.x), Math.abs(start.y - end.y)));
        context.getViewportPane().repaint();
    }

    public void onMouseReleased( MapMouseEvent e ) {
        if (selecting) {
            Point end = e.getPoint();
            if (start == null || start.equals(end)) {

                Envelope bounds = getContext().getBoundingBox(e.getPoint(), 3);
                sendSelectionCommand(e, bounds);
            } else {
                Coordinate c1 = context.getMap().getViewportModel().pixelToWorld(start.x, start.y);
                Coordinate c2 = context.getMap().getViewportModel().pixelToWorld(end.x, end.y);

                Envelope bounds = new Envelope(c1, c2);
                sendSelectionCommand(e, bounds);
            }
        }
    }

    protected void sendSelectionCommand( MapMouseEvent e, Envelope bounds ) {
        // MapCommand command;
        // if (e.isModifierDown(MapMouseEvent.MOD2_DOWN_MASK)) {
        // command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds,
        // BBoxSelectionCommand.ADD);
        // } else if (e.isModifierDown(MapMouseEvent.MOD1_DOWN_MASK)) {
        // command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds,
        // BBoxSelectionCommand.SUBTRACT);
        // } else {
        // command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds,
        // BBoxSelectionCommand.NONE);
        // }
        //
        // getContext().sendASyncCommand(command);
        selecting = false;
        shapeCommand.setValid(false);
        getContext().getViewportPane().repaint();
        mouseReleasedf(e, bounds);
    }

    public void mouseReleasedf( final MapMouseEvent e, final Envelope bounds ) {
        PlatformGIS.run(new IRunnableWithProgress(){

            @SuppressWarnings("unchecked")
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException {
                monitor.beginTask("Selecting", 5);
                try {
                    ILayer selectedLayer = getContext().getSelectedLayer();
                    IGeoResource geoResource = selectedLayer.getGeoResource();
                    final JSONReader resolve = geoResource.resolve(JSONReader.class, null);
                    FeatureIterator features = resolve.getFeatures();
                    final List<String> selected = new ArrayList<String>();
                    for( Feature feature : features ) {
                        if (bounds.contains(feature.createGeometry().getCoordinate())) {
                            String name = (String) feature.getProperties().get("name");
                            selected.add(name);
                        }
                    }
                    Display display = PlatformUI.getWorkbench().getDisplay();
                    display.syncExec(new Runnable(){

                        public void run() {
                            final IWorkbenchWindow window = PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow();
                            try {
                                final NetworkTreeView viewPart = (NetworkTreeView) window
                                        .getActivePage().showView(NetworkTreeView.NETWORK_VIEW_ID);
                                viewPart.select(selected);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    return;
                } finally {
                    monitor.done();
                }
            }

        });
    }
}
