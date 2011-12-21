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

import java.awt.Graphics2D;
import java.awt.Point;

import org.amanzi.awe.render.core.AbstractRenderer;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IMeasurementModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;

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

    @Override
    protected void renderElement(Graphics2D destination, Point point, IDataElement mpLocation, IRenderableModel model) {
        INodeType type = NodeTypeManager.getType(mpLocation.get(AbstractService.TYPE).toString());
        if (!DriveNodeTypes.MP.equals(type)) {
            throw new IllegalArgumentException("Could not render element of type " + type.getId());
        }

        renderMPelement(destination, point, mpLocation);
    }

    /**
     * render location element on map
     * 
     * @param destination
     * @param point
     * @param element
     */
    private void renderMPelement(Graphics2D destination, Point point, IDataElement element) {
        int size = 2;
        switch (RenderOptions.scale) {
        case SMALL:
            destination.setColor(RenderOptions.border);
            destination.drawRect(point.x - size / 2, point.y - size / 2, size, size);
            break;
        case MEDIUM:
            size = RenderOptions.siteSize;
            destination.setColor(RenderOptions.border);
            destination.drawOval(point.x - size / 2, point.y - size / 2, size, size);
            break;
        case LARGE:
            size = RenderOptions.largeSectorsSize / 3;
            destination.setColor(RenderOptions.border);
            destination.drawOval(point.x - size / 2, point.y - size / 2, size, size);
            destination.setColor(RenderOptions.siteFill);
            destination.fillOval(point.x - size / 2, point.y - size / 2, size, size);
        }

    }

    @Override
    protected void setStyle(Graphics2D destination) {
        super.setStyle(destination);

    }

    @Override
    protected double calculateResult(Envelope dbounds, IMeasurementModel resource) {
        return (resource.getNodeCount(DriveNodeTypes.MP) / 2) / (dbounds.getHeight() * dbounds.getWidth());
    }
}
