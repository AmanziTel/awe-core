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

import org.amanzi.awe.neostyle.drive.DriveStyle;
import org.amanzi.awe.neostyle.drive.DriveStyleContent;
import org.amanzi.awe.render.core.AbstractRenderer;
import org.amanzi.awe.render.core.AbstractRendererStyles;
import org.amanzi.awe.render.core.RenderShape;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.models.render.IRenderableModel;

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
    protected void renderElement(Graphics2D destination, Point point, ILocationElement mpLocation, IGISModel model) {
        renderCoordinateElement(destination, point, mpLocation);
    }

    @Override
    protected void setStyle(Graphics2D destination) {
        super.setStyle(destination);
        DriveStyle style = (DriveStyle)getContext().getLayer().getStyleBlackboard().get(DriveStyleContent.ID);
        driveRendererStyle.setDefaultLabelColor(style.getLabelColor());
        driveRendererStyle.setDefaultLineColor(style.getLineColor());
        driveRendererStyle.setDefaultLocationColor(style.getLocationColor());
        driveRendererStyle.setDefaultFontSize(style.getFontSize());
        driveRendererStyle.setDefaultLocationLabelType(style.getLocationLabelType());
        driveRendererStyle.setDefaultMeasurementPropertyName(style.getMeasurementNameProperty());
    }

    @Override
    protected double calculateResult(Envelope dbounds, IGISModel resource) {
        return (getRenderableElementCount(resource) / (dbounds.getHeight() * dbounds.getWidth()));
    }

    @Override
    protected void setDrawLabel(double countScaled) {
    }

    @Override
    protected long getRenderableElementCount(IGISModel model) {
        return model.getCount();
    }

    @Override
    protected Class< ? extends IRenderableModel> getResolvedClass() {
        return IDriveModel.class;
    }

    @Override
    protected Color getDefaultFillColorByElement(IDataElement element) {
        return driveRendererStyle.getDefaultLocationColor();
    }

    @Override
    protected void renderCoordinateElement(Graphics2D destination, Point point, IDataElement element) {
        switch (driveRendererStyle.getScale()) {
        case SMALL:
            drawCoordinateElement(RenderShape.RECTANGLE, destination, point, element, true);
            break;
        case MEDIUM:
            drawCoordinateElement(RenderShape.RECTANGLE, destination, point, element, true);
            break;
        case LARGE:
            drawCoordinateElement(RenderShape.ELLIPSE, destination, point, element, true);
        }

        if (isSelected(element, true, false)) {
            highlightSelectedItem(destination, point);
        }
    }

}
