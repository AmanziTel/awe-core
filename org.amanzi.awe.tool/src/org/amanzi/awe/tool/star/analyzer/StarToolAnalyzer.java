/**
 * 
 */
package org.amanzi.awe.tool.star.analyzer;

import java.awt.Point;
import java.awt.geom.Arc2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.amanzi.neo.services.ui.events.StarToolAnalyzerEvent;
import org.amanzi.neo.services.utils.Pair;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * 
 * 
 * @author Bondoronok_P
 */
public class StarToolAnalyzer {

	/**
	 * Logger instance
	 */
	private static final Logger LOGGER = Logger
			.getLogger(StarToolAnalyzer.class);

	/**
	 * Sector creation constants
	 */
	private static final String AZIMUTH = "azimuth";
	private static final String BEAMWIDTH = "beam";
	private static final double FULL_CIRCLE = 360.0;
	private static final boolean LENIENT = Boolean.TRUE;
	private static final double INCREASE_VALUE = 0.003;
	private static final int RADIUS = 30;
	private static final int CORRECTION = 10;

	/**
	 * Show On Map zoom constant
	 */
	private static final double ZOOM = 20d;

	/**
	 * Star Tool context
	 */
	private IToolContext context;

	private boolean dragged;
	private Envelope selection;
	private MathTransform transformD2w;
	private MathTransform transformW2d;

	/**
	 * The constructor
	 * 
	 * @param context
	 *            star tool context
	 * @param dragged
	 *            dragged or not
	 * @param envelope
	 *            selection bounds
	 */
	public StarToolAnalyzer(IToolContext context, boolean dragged,
			Envelope envelope) {
		this.context = context;
		this.dragged = dragged;
		this.selection = envelope;
	}

	/**
	 * Analyze selected data
	 * 
	 * @param point
	 *            selected point
	 */
	public void analyze(Point point) {
		INetworkModel analyzedModel = getAnalysedModel();
		if (analyzedModel != null) {
			List<IDataElement> analyzedElements = getAnalayzedElements(analyzedModel);
			if (!dragged && !analyzedElements.isEmpty()) {
				IDataElement selectedSector = findSelectedSector(analyzedModel,
						analyzedElements, point);
				if (selectedSector == null) {
					selectedSector = analyzedElements.get(0);
				}
				analyzedModel.setSelectedDataElementToList(selectedSector);
				analyzedElements.clear();
				analyzedElements.add(selectedSector);
			} else {
				analyzedModel.setSelectedDataElements(analyzedElements);
			}
			fireEvents(analyzedModel, analyzedElements);
		} else {
			LOGGER.info("Star Tool: No model for the analysis of.");
		}
	}

	/**
	 * Find selected sector by point in selected elements
	 * 
	 * @param model
	 *            analyzed network model
	 * @param selectedElements
	 *            selected elements
	 * @param point
	 *            selected point
	 * @return founded sector or null
	 */
	private IDataElement findSelectedSector(INetworkModel model,
			List<IDataElement> selectedElements, Point point) {
		IDataElement result = null;
		int i = 0;
		for (IDataElement currentElement : selectedElements) {
			Point sectorPoint = null;
			try {
				sectorPoint = getPoint(model, currentElement);
			} catch (TransformException e) {
			}
			if (sectorPoint != null) {
				Arc2D arc2d = new Arc2D.Double();
				IDataElement site = model.getParentElement(currentElement);
				Pair<Double, Double> coordinates = getSectorCoordinates(site,
						currentElement, i);
				arc2d.setArcByCenter(
						getSectorXCoordinate(sectorPoint),
						getSectorYCoordinate(sectorPoint),
						RADIUS,
						getAngle(coordinates.getLeft(), coordinates.getRight()),
						coordinates.getRight(), Arc2D.PIE);
				if (arc2d.contains(point)) {
					result = currentElement;
					break;
				}
			}
			i++;
		}
		return result;
	}

	/**
	 * get X coordinate
	 * 
	 * @param point
	 *            point
	 * @return x coordinate
	 */
	private int getSectorXCoordinate(Point point) {
		return point.x - CORRECTION;
	}

	/**
	 * get Y coordinate
	 * 
	 * @param point
	 *            Point
	 * @return y coordinate
	 */
	private int getSectorYCoordinate(Point point) {
		return point.y - CORRECTION;
	}

	/**
	 * Get point
	 * 
	 * @param model
	 * @param element
	 * @param bounds_transformed
	 * @return point or null
	 * @throws TransformException
	 */
	protected Point getPoint(INetworkModel model, IDataElement element)
			throws TransformException {

		Coordinate location = model.getCoordinate(element);
		java.awt.Point point = null;

		if (location != null && selection != null
				&& selection.contains(location)) {
			Coordinate world_location = new Coordinate();
			try {
				JTS.transform(location, world_location, transformD2w);
			} catch (Exception e) {
				JTS.transform(location, world_location, transformW2d.inverse());
			}
			point = context.worldToPixel(world_location);
		}
		return point;
	}

	/**
	 * Get azimuth and beamwidth
	 * 
	 * @param site
	 * @param sector
	 * @param i
	 * @return sector azimuth and beamwidth
	 */
	private Pair<Double, Double> getSectorCoordinates(IDataElement site,
			IDataElement sector, int i) {
		Integer sCount = (Integer) site.get(NetworkService.SECTOR_COUNT);
		Double azimuth = (Double) sector.get(AZIMUTH);
		Double beamwidth = (Double) sector.get(BEAMWIDTH);
		if (azimuth == null || beamwidth == null || beamwidth == 0) {
			beamwidth = FULL_CIRCLE / (sCount == null ? 1 : sCount);
			azimuth = beamwidth * i;
			beamwidth = beamwidth.doubleValue() * 0.8;
		}
		return new Pair<Double, Double>(azimuth, beamwidth);
	}

	/**
	 * Get angle
	 * 
	 * @param azimuth
	 * @param beamwidth
	 * @return sector angle
	 */
	private double getAngle(double azimuth, double beamwidth) {
		return (90 - azimuth - beamwidth / 2.0) + beamwidth;
	}

	/**
	 * Searching Star Tool analyzed model
	 * 
	 * @return Star Tool analyzed model or null
	 * @throws AWEException
	 */
	private INetworkModel getAnalysedModel() {
		INetworkModel result = null;
		for (ILayer layer : context.getMapLayers()) {
			IGeoResource resource = layer.findGeoResource(IDataModel.class);
			if (resource == null) {
				continue;
			}
			try {
				IDataModel resolvedElement = resource.resolve(IDataModel.class,
						null);
				initializeCoordinateReferenceSystem(resource);
				((INetworkModel) resolvedElement).clearSelectedElements();
				INetworkModel currentModel = ((INetworkModel) resolvedElement)
						.getStarToolSelectedModel();
				result = currentModel;
			} catch (Exception e) {
				LOGGER.error("Star Tool: resolved resource was unavailable due to a technical problem."
						+ e);
			}
		}
		return result;
	}

	/**
	 * Initializing CRS
	 * 
	 * @param resource
	 *            IGeoResourse
	 * @throws IOException
	 * @throws FactoryException
	 */
	private void initializeCoordinateReferenceSystem(IGeoResource resource)
			throws IOException, FactoryException {
		CoordinateReferenceSystem dataCrs = resource.getInfo(null).getCRS();
		CoordinateReferenceSystem worldCrs = context.getCRS();
		transformD2w = CRS.findMathTransform(dataCrs, worldCrs, LENIENT);
		transformW2d = CRS.findMathTransform(worldCrs, dataCrs, LENIENT);
	}

	/**
	 * Get selected elements for analyzing
	 * 
	 * @param model
	 *            network model
	 * @return list with selection or empty
	 */
	private List<IDataElement> getAnalayzedElements(INetworkModel model) {
		List<IDataElement> result = new ArrayList<IDataElement>();
		if (!dragged) {
			selection.expandBy(INCREASE_VALUE);
		}
		try {
			for (IDataElement currentElement : model.getElements(selection)) {
				for (IDataElement sector : model.getChildren(currentElement)) {
					sector.put(INeoConstants.NETWORK_MODEL_NAME, model);
					result.add(sector);
				}
			}
		} catch (AWEException e) {
			LOGGER.error("Star Tool: cannot get analysed elements." + e);
		}
		return result;
	}

	/**
	 * Fire StarToolAnalyzerEvent and ShowOnMapEvent
	 * 
	 * @param analyzedModel
	 *            model for analysis
	 * @param analyzedElements
	 *            analyzed elements
	 */
	private void fireEvents(INetworkModel analyzedModel,
			List<IDataElement> analyzedElements) {
		EventManager eventManager = EventManager.getInstance();
		eventManager.fireEvent(new StarToolAnalyzerEvent(analyzedModel,
				analyzedElements));
		eventManager
				.fireEvent(new ShowOnMapEvent(analyzedModel, ZOOM, !dragged));
	}
}
