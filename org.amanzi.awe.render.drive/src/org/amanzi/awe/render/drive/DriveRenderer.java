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

package org.amanzi.awe.render.drive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.amanzi.awe.neostyle.drive.DriveStyle;
import org.amanzi.awe.neostyle.drive.DriveStyleContent;
import org.amanzi.awe.render.core.AbstractRenderer;
import org.amanzi.awe.render.core.AbstractRendererStyles;
import org.amanzi.awe.render.core.RenderShape;
import org.amanzi.awe.render.core.Scale;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IMeasurementModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.apache.commons.lang3.StringUtils;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * <p>
 * draw points on map
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DriveRenderer extends AbstractRenderer {
	private static final String TIMESTAMP = "timestamp";
	private static final String MEASUREMENT = "measurement";
	private DefaultDriveRendererStyles driveRendererStyle;

	@Override
	protected AbstractRendererStyles initDefaultRendererStyle() {
		driveRendererStyle = DefaultDriveRendererStyles.getInstance();
		return driveRendererStyle;
	}

	@Override
	protected void renderElement(Graphics2D destination, Point point,
			IDataElement mpLocation, IRenderableModel model) {
		renderCoordinateElement(destination, point, mpLocation);
		if (driveRendererStyle.getScale() == Scale.LARGE) {
			renderLabel(destination, point, mpLocation, model);
		}
	}

	@Override
	protected void setStyle(Graphics2D destination) {
		super.setStyle(destination);
		DriveStyle style = (DriveStyle) getContext().getLayer()
				.getStyleBlackboard().get(DriveStyleContent.ID);
		driveRendererStyle.setDefaultLabelColor(style.getLabelColor());
		driveRendererStyle.setDefaultLineColor(style.getLineColor());
		driveRendererStyle.setDefaultLocationColor(style.getLocationColor());
		driveRendererStyle.setDefaultFontSize(style.getFontSize());
		driveRendererStyle.setDefaultLocationLabelType(style
				.getLocationLabelType());
		driveRendererStyle.setDefaultMeasurementPropertyName(style
				.getMeasurementNameProperty());
	}

	@Override
	protected double calculateResult(Envelope dbounds, IRenderableModel resource) {
		return (((IMeasurementModel) resource).getNodeCount(DriveNodeTypes.MP) / 2)
				/ (dbounds.getHeight() * dbounds.getWidth());
	}

	@Override
	protected void setDrawLabel(double countScaled) {
	}

	@Override
	protected long getRenderableElementCount(IRenderableModel model) {
		return ((IMeasurementModel) model)
				.getNodeCount(((IMeasurementModel) model).getPrimaryType()) / 2;
	}

	@Override
	protected Class<? extends IRenderableModel> getResolvedClass() {
		return IMeasurementModel.class;
	}

	@Override
	protected Color getDefaultFillColorByElement(IDataElement element) {
		return driveRendererStyle.getDefaultLocationColor();
	}

	@Override
	protected void renderCoordinateElement(Graphics2D destination, Point point,
			IDataElement element) {
		switch (driveRendererStyle.getScale()) {
		case SMALL:
			drawCoordinateElement(RenderShape.RECTANGLE, destination, point,
					element, true);
			break;
		case MEDIUM:
			drawCoordinateElement(RenderShape.RECTANGLE, destination, point,
					element, true);
			break;
		case LARGE:
			drawCoordinateElement(RenderShape.ELLIPSE, destination, point,
					element, true);
		}
	}

	@Override
	protected void renderSelectedElement(Graphics2D destination, Point point,
			IRenderableModel model, IDataElement element,
			Envelope selectedBounds) throws TransformException {
		highlightSelectedItem(destination, point);
		renderElement(destination, point, element, model);
	}

	/**
	 * Render Label
	 * 
	 * @param destination
	 * @param point
	 * @param mpLocation
	 */
	private void renderLabel(Graphics2D destination, Point point,
			IDataElement mpLocation, IRenderableModel model) {
		destination.setColor(driveRendererStyle.getDefaultLabelColor());
		// TODO labels rendering at the same points
		int x = point.x + 5;
		int y = point.y + 5;
		destination.drawString(getLabel(mpLocation, model), x, y);
	}

	/**
	 * Return label for measurement depending on default label property
	 * 
	 * @param dataElement
	 *            Measurement element
	 * @return label or empty
	 */
	private String getLabel(IDataElement dataElement, IRenderableModel model) {
		if (TIMESTAMP.equals(driveRendererStyle.getDefaultLocationLabelType())) {
			return getTime((Long) dataElement.get(TIMESTAMP));
		} else if (MEASUREMENT.equals(driveRendererStyle
				.getDefaultLocationLabelType())) {
			return getMeasurementLabel(dataElement, model);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Get Measurement property label
	 * 
	 * @param dataElement
	 *            location
	 * @param model
	 *            DriveModel
	 * @return Measurement property name or Empty
	 */
	private String getMeasurementLabel(IDataElement dataElement,
			IRenderableModel model) {
		String measurementPropertyName = driveRendererStyle
				.getDefaultMeasurementPropertyName();
		IDriveModel driveModel = (IDriveModel) model;
		Iterator<IDataElement> it = driveModel.getMeasurements(
				model.getSelectedElements(), null).iterator();
		Object foundedObject = null;
		while (it.hasNext()) {
			IDataElement element = it.next();
			foundedObject = element.get(measurementPropertyName);
			if (foundedObject != null) {
				break;
			}
		}
		return foundedObject == null ? StringUtils.EMPTY : String
				.valueOf(foundedObject);

	}

	/**
	 * Get time by timestamp
	 * 
	 * @param milliseconds
	 *            timestamp
	 * @return time in format HH:mm:ss.S or EMPTY
	 */
	private String getTime(Long milliseconds) {
		if (milliseconds != null && milliseconds != 0L) {
			Date date = new Date(milliseconds);
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
			return dateFormat.format(date);
		}
		return "";
	}

}
