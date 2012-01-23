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

import org.amanzi.awe.render.core.AbstractRenderer;
import org.amanzi.awe.render.core.AbstractRendererStyles;
import org.amanzi.awe.render.core.RenderShape;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IMeasurementModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.opengis.referencing.operation.NoninvertibleTransformException;
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
	}

	@Override
	protected void setStyle(Graphics2D destination) {
		super.setStyle(destination);
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
		return driveRendererStyle.getDefaultMpColor();
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
	protected void renderSelectedElements(Graphics2D destination,
			IRenderableModel model, Envelope bounds_transformed)
			throws NoninvertibleTransformException, TransformException {			
	}

}
