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
import java.awt.RadialGradientPaint;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

import org.amanzi.awe.neostyle.NetworkNeoStyle;
import org.amanzi.awe.neostyle.NetworkNeoStyleContent;
import org.amanzi.awe.render.core.AbstractRenderer;
import org.amanzi.awe.render.core.AbstractRendererStyles;
import org.amanzi.awe.render.core.RenderShape;
import org.amanzi.awe.render.core.Scale;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.utils.Pair;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * <p>
 * network renderer
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NetworkRenderer extends AbstractRenderer {

	// TODO: find a better place for the constants
	public static final String AZIMUTH = "azimuth";
	public static final String BEAMWIDTH = "beam";
	private static final double FULL_CIRCLE = 360.0;
	private static final Color SELECTED_SECTOR_COLOR = Color.BLACK;

	/**
	 * styler for current renderer
	 */
	private DefaultNetworkRenderStyle networkRendererStyle;

	@Override
	protected AbstractRendererStyles initDefaultRendererStyle() {
		networkRendererStyle = DefaultNetworkRenderStyle.getInstance();
		return networkRendererStyle;
	}

	@Override
	protected void renderElement(Graphics2D destination, Point point,
			IDataElement site, IRenderableModel model) {
		renderCoordinateElement(destination, point, site);
		if (networkRendererStyle.getScale() == Scale.LARGE) {
			sectorRendering(destination, point, model, site, null, false);
		}
	}

	@Override
	protected void renderSelectedElement(Graphics2D destination, Point point,
			IRenderableModel model, IDataElement sector, Envelope selectedBounds)
			throws TransformException {
		IDataElement site = ((INetworkModel) model).getParentElement(sector);
		highlightSelectedItem(destination, point);
		renderElement(destination, point, site, model);
		if (networkRendererStyle.getScale() == Scale.LARGE) {
			if (model.isDrawNeighbors()) {
				sectorRendering(destination, point, model, site, sector,
						model.isDrawNeighbors());
				try {					
					renderSectorNeighbours(destination, point,
							(INetworkModel) model, sector, selectedBounds);
				} catch (AWEException e) {
				}
			}
		}
	}

	/**
	 * Sector rendering
	 * 
	 * @param destination
	 * @param point
	 * @param site
	 * @param selectedSector
	 * @param isSelected
	 */
	private void sectorRendering(Graphics2D destination, Point point,
			IRenderableModel model, IDataElement site,
			IDataElement selectedSector, boolean isSelected) {
		int i = 0;
		Pair<Double, Double> sectorParameters = null;
		for (IDataElement sector : ((INetworkModel) model).getChildren(site)) {
			if (isSelected) {
				sectorParameters = getSectorParameters(site, sector,
						getSelectedSectorIndex(model, site, selectedSector));
				renderSector(destination, point, sectorParameters.getLeft(),
						sectorParameters.getRight(), selectedSector, isSelected);
				break;
			} else {
				sectorParameters = getSectorParameters(site, sector, i);
				renderSector(destination, point, sectorParameters.getLeft(),
						sectorParameters.getRight(), sector, isSelected);
			}
			i++;
		}
	}

	/**
	 * Get selected sector index
	 * 
	 * @param model
	 * @param site
	 * @param selectedSector
	 * @return selected sector index
	 */
	private int getSelectedSectorIndex(IRenderableModel model,
			IDataElement site, IDataElement selectedSector) {
		int result = 0;
		for (IDataElement sector : ((INetworkModel) model).getChildren(site)) {
			if (selectedSector.equals(sector)) {
				break;
			} else {
				result++;
			}
		}
		return result;
	}

	/**
	 * Get azimuth and beamwidth
	 * 
	 * @param site
	 * @param sector
	 * @param i
	 * @return
	 */
	private Pair<Double, Double> getSectorParameters(IDataElement site,
			IDataElement sector, int i) {
		Integer sCount = (Integer) site.get(NetworkService.SECTOR_COUNT);
		Double azimuth = (Double) sector.get(AZIMUTH);
		Double beamwidth = (Double) sector.get(BEAMWIDTH);
		if (azimuth == null || beamwidth == null || beamwidth == 0) {
			beamwidth = FULL_CIRCLE / (sCount == null ? 1 : sCount);
			azimuth = beamwidth * i;

			beamwidth = beamwidth.doubleValue() * 0.8;
			// TODO default beamwidth
			// Double defaultBeamwidth = (double)
			// networkRendererStyle.getDefaultBeamwidth();
			// // if (defaultBeamwidth < beamwidth) {
			// // beamwidth = defaultBeamwidth;
			// // azimuth = beamwidth * i;
			// // }
		}
		return new Pair<Double, Double>(azimuth, beamwidth);
	}

	/**
	 * render sector element on map
	 * 
	 * @param destination
	 * @param point
	 * @param element
	 */
	private void renderSector(Graphics2D destination, Point point,
			double azimuth, double beamwidth, IDataElement sector,
			boolean selected) {
		int size = getSize();
		int x = getSectorXCoordinate(point, size);
		int y = getSectorYCoordinate(point, size);
		destination.setColor(networkRendererStyle.changeColor(getColor(sector),
				networkRendererStyle.getAlpha()));
		GeneralPath path = new GeneralPath();
		path.moveTo(x, y);
		Arc2D a = createSector(point,
				networkRendererStyle.getLargeElementSize(),
				getAngle(azimuth, beamwidth), beamwidth);
		path.append(a.getPathIterator(null), true);
		path.closePath();
		if (selected) {
			destination.setColor(networkRendererStyle.changeColor(
					SELECTED_SECTOR_COLOR, networkRendererStyle.getAlpha()));
			destination.draw(path);
			destination.drawString(sector.toString(), (int) a.getEndPoint()
					.getX() + 10, (int) a.getEndPoint().getY());

		} else {
			destination.draw(path);
			destination.fill(path);
		}

	}

	/**
	 * Render relationships between selected Sector and neighbors
	 * 
	 * @param destination
	 * @param startPoint
	 * @param model
	 * @param site
	 * @param sector
	 * @param bounds_transformed
	 * @throws TransformException
	 * @throws AWEException
	 */
	private void renderSectorNeighbours(Graphics2D destination,
			Point startPoint, INetworkModel model, IDataElement sector,
			Envelope bounds_transformed) throws TransformException,
			AWEException {
		INodeToNodeRelationsModel n2nModel = model
				.getCurrentNodeToNodeRelationshipModel();
		Arc2D selectedSector = getSector(model, sector, startPoint);
		if (n2nModel != null) {
			for (IDataElement currentNeighbour : n2nModel
					.getNeighboursForCurrentSector(sector)) {
				Point endPoint = getPoint(model, currentNeighbour,
						bounds_transformed);
				if (endPoint != null) {
					Pair<Double, Double> parameters = getSectorParameters(
							model.getParentElement(currentNeighbour),
							currentNeighbour,
							getSelectedSectorIndex(model,
									model.getParentElement(currentNeighbour),
									currentNeighbour));
					renderSector(destination, endPoint, parameters.getLeft(),
							parameters.getRight(), currentNeighbour,
							Boolean.TRUE);
					renderNeighbourRelationship(destination, selectedSector,
							getSector(model, currentNeighbour, endPoint));
				}
			}
		}
	}

	/**
	 * Get Sector
	 * 
	 * @param model
	 * @param sector
	 * @param point
	 * @return Arc2D
	 */
	private Arc2D getSector(INetworkModel model, IDataElement sector,
			Point point) {
		IDataElement site = model.getParentElement(sector);
		Pair<Double, Double> coordinates = getSectorParameters(site, sector,
				getSelectedSectorIndex(model, site, sector));
		double beamwidth = coordinates.getRight();
		return createSector(point,
				networkRendererStyle.getLargeElementSize() / 1.5,
				getAngle(coordinates.getLeft(), beamwidth), beamwidth / 2);
	}

	/**
	 * Create sector
	 * 
	 * @param point
	 * @param radius
	 * @param angle
	 * @param beamwidth
	 * @return sector
	 */
	private Arc2D createSector(Point point, double radius, double angle,
			double beamwidth) {
		int size = getSize();
		Arc2D sector = new Arc2D.Double();
		sector.setArcByCenter(getSectorXCoordinate(point, size),
				getSectorYCoordinate(point, size), radius, angle, beamwidth,
				Arc2D.OPEN);
		return sector;
	}

	/**
	 * Render relationship between selected by user sector and neighbor
	 * 
	 * @param destination
	 *            Graphics2D
	 * @param selectedSector
	 *            selected by user sector
	 * @param neighbourSector
	 *            neighbor sector
	 */
	private void renderNeighbourRelationship(Graphics2D destination,
			Arc2D selectedSector, Arc2D neighbourSector) {
		destination.setColor(networkRendererStyle.changeColor(Color.RED,
				networkRendererStyle.getAlpha()));
		GeneralPath path = new GeneralPath();
		path.moveTo((selectedSector.getEndPoint().getX()),
				(selectedSector.getEndPoint().getY()));
		double neighbourX = neighbourSector.getEndPoint().getX();
		double neighbourY = neighbourSector.getEndPoint().getY();
		path.lineTo(neighbourX, neighbourY);
		path.closePath();
		destination.draw(path);
	}

	private int getSectorXCoordinate(Point point, int size) {
		return point.x - (size / 2);
	}

	private int getSectorYCoordinate(Point point, int size) {
		return point.y - (size / 2);
	}

	private double getAngle(double azimuth, double beamwidth) {
		return (90 - azimuth - beamwidth / 2.0) + beamwidth;
	}

	@Override
	protected void setStyle(Graphics2D destination) {
		super.setStyle(destination);
		NetworkNeoStyle newStyle = (NetworkNeoStyle) getContext().getLayer()
				.getStyleBlackboard().get(NetworkNeoStyleContent.ID);
		if (newStyle.equals(style)) {
			return;
		}

		style = newStyle;
		networkRendererStyle.setAlpha(255 - (int) ((double) newStyle
				.getSymbolTransparency() / 100.0 * 255.0));
		networkRendererStyle.setBorderColor(networkRendererStyle.changeColor(
				newStyle.getLine(), networkRendererStyle.getAlpha()));
		networkRendererStyle.setLargeElementSize(newStyle.getSymbolSize());
		networkRendererStyle.setSectorFill(networkRendererStyle.changeColor(
				newStyle.getFill(), networkRendererStyle.getAlpha()));
		networkRendererStyle.setSiteFill(networkRendererStyle.changeColor(
				newStyle.getSiteFill(), networkRendererStyle.getAlpha()));

		networkRendererStyle.setMaxSitesFull(newStyle.getSmallSymb());
		networkRendererStyle.setMaxSitesLabel(newStyle.getLabeling());
		networkRendererStyle.setMaxSitesLite(newStyle.getSmallestSymb());
		networkRendererStyle.setMaxSymbolSize(newStyle.getMaximumSymbolSize());
		networkRendererStyle
				.setDefaultBeamwidth(newStyle.getDefaultBeamwidth());
	}

	@Override
	protected double calculateResult(Envelope dbounds, IRenderableModel resource) {
		return (((INetworkModel) resource)
				.getNodeCount(NetworkElementNodeType.SITE) / 2)
				/ (dbounds.getHeight() * dbounds.getWidth());
	}

	@Override
	protected void setDrawLabel(double countScaled) {
		networkRendererStyle.setDrawLabels(countScaled < networkRendererStyle
				.getMaxSitesLabel());
	}

	@Override
	protected long getRenderableElementCount(IRenderableModel model) {
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
		// };
		return (((INetworkModel) model)
				.getNodeCount(NetworkElementNodeType.SITE) / 2);// TODO:
	}

	@Override
	protected Class<? extends IRenderableModel> getResolvedClass() {
		return INetworkModel.class;
	}

	@Override
	protected Color getDefaultFillColorByElement(IDataElement element) {
		INodeType type = NodeTypeManager.getType(element);
		if (type.getId().equals(NetworkElementNodeType.SECTOR.getId())) {
			return networkRendererStyle.getSectorFill();
		} else if (type.getId().equals(NetworkElementNodeType.SITE.getId())) {
			return networkRendererStyle.getSiteFill();
		}
		return networkRendererStyle.getBorderColor();
	}

	@Override
	protected void renderCoordinateElement(Graphics2D destination, Point point,
			IDataElement element) {
		switch (networkRendererStyle.getScale()) {
		case SMALL:
			drawCoordinateElement(RenderShape.RECTANGLE, destination, point,
					element, false);
			break;
		case MEDIUM:
			drawCoordinateElement(RenderShape.ELLIPSE, destination, point,
					element, false);
			break;
		case LARGE:
			drawCoordinateElement(RenderShape.ELLIPSE, destination, point,
					element, true);
		}
	}

	/**
	 * Highlight selected items
	 * 
	 * @param destination
	 * @param point
	 *            point
	 */
	private void highlightSelectedItem(Graphics2D destination,
			java.awt.Point point) {
		int elementSize = getSize() * 2;
		float radius = 60;
		float[] fractions = { 0.01f, 1.0f };
		for (; elementSize > 0; elementSize *= 0.8) {
			Color[] colors = {
					networkRendererStyle.changeColor(Color.CYAN, elementSize),
					networkRendererStyle.changeColor(Color.WHITE,
							Color.TRANSLUCENT) };
			destination.setPaint(new RadialGradientPaint(
					(point.x - elementSize / 3), (point.y - elementSize / 3),
					radius, fractions, colors));
			destination.fillOval((int) (point.x - (elementSize * 2.25)),
					(int) (point.y - (elementSize * 2.25)), 4 * elementSize,
					4 * elementSize);
		}
	}
}