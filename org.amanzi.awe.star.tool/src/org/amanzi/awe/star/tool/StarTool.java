package org.amanzi.awe.star.tool;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.internal.command.navigation.ZoomCommand;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import org.amanzi.awe.star.tool.analyzer.StarToolAnalyzer;
import org.amanzi.awe.views.network.view.NetworkPropertiesView;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.StarToolAnalyzerEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Star tool implementation
 * 
 * @author Bondoronok_p
 */
public class StarTool extends AbstractModalTool {

	/**
	 * Network properties id
	 */
	public static final String NETWORK_PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.NetworkPropertiesView";

	private Coordinate startCoordinate;
	private Point startPoint;
	private boolean dragged;
	private net.refractions.udig.project.ui.commands.SelectionBoxCommand shapeCommand;

	/**
	 * Creates an new instance of the StarTool which supports mouse actions and
	 * mouse motion. These are used to support panning as well as the star
	 * analysis.
	 */
	public StarTool() {
		super(MOUSE | MOTION);
		dragged = false;
		addListeners();

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
		shapeCommand.setShape(new Rectangle(Math.min(startPoint.x, endPoint.x),
				Math.min(startPoint.y, endPoint.y), Math.abs(startPoint.x
						- endPoint.x), Math.abs(startPoint.y - endPoint.y)));
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
		StarToolAnalyzer toolAnalyzer = new StarToolAnalyzer(getContext(),
				dragged, new Envelope(startCoordinate, getContext()
						.pixelToWorld(event.x, event.y)));
		toolAnalyzer.analyze(event.getPoint());
		if (dragged) {
			zoomIn(getContext().getViewportModel(),
					calculateZoomInRectangle(event));
		}
		dragged = false;
		shapeCommand.setValid(false);
		getContext().getViewportPane().repaint();
	}

	/**
	 * @see net.refractions.udig.project.ui.tool.Tool#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	@SuppressWarnings("unchecked")
	private void addListeners() {
		EventManager.getInstance().addListener(EventsType.ANALYSE,
				new StarToolAnalyzerListener());
	}

	/**
	 * Zoom in command
	 * 
	 * @param viewPort
	 *            view port
	 * @param rectangle
	 *            rectangle
	 */
	private void zoomIn(IViewportModel viewPort, Rectangle rectangle) {
		ZoomCommand cmd = new ZoomCommand(getContext().getMapDisplay()
				.getDisplaySize().getWidth()
				/ rectangle.width);
		cmd.setFixedPoint(viewPort.pixelToWorld(rectangle.x + rectangle.width
				/ 2, rectangle.y + rectangle.height / 2));
		getContext().sendASyncCommand(cmd);
	}

	/**
	 * Zoom in rectangle calculation
	 * 
	 * @param event
	 *            map mouse event
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
		width1 = (int) (height1 * context.getViewportModel().getAspectRatio());
		width2 = Math.abs(x2 - x1);
		height2 = (int) (width2 / context.getViewportModel().getAspectRatio());
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
	 * Star Tool Analyzer Listener
	 * 
	 * @author Bondoronok_p
	 * 
	 */
	private class StarToolAnalyzerListener implements
			IEventsListener<StarToolAnalyzerEvent> {
		@Override
		public void handleEvent(StarToolAnalyzerEvent data) {
			List<IDataElement> dataElements = data.getAnalyzedElements();
			if (!dataElements.isEmpty()) {
				Set<IDataElement> analyzedElements = new HashSet<IDataElement>();
				analyzedElements.addAll(dataElements);
				getPropertiesView().updateTableView(analyzedElements, false);
			}
		}

		@Override
		public Object getSource() {
			return null;
		}
	}

	/**
	 * Get Network properties table
	 * 
	 * @return NetworkPropertiesView
	 */
	private NetworkPropertiesView getPropertiesView() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		NetworkPropertiesView resultView = null;
		try {
			if ((resultView = (NetworkPropertiesView) page
					.findView(NETWORK_PROPERTIES_VIEW_ID)) == null) {
				resultView = (NetworkPropertiesView) page
						.showView(NETWORK_PROPERTIES_VIEW_ID);				
			}			
		} catch (PartInitException e) {			
		}
		return resultView;
	}

}
