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

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.IDataElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NewNetworkRenderer extends AbstractRenderer {

    @Override
    protected void renderElement(Graphics2D destination, Point point, IDataElement element) {
        INodeType type = NodeTypeManager.getType(element.get(NewAbstractService.TYPE).toString());
        if (NetworkElementNodeType.SECTOR.equals(type)) {
            renderSector(destination, point, element);
        } else if (NetworkElementNodeType.SITE.equals(type)) {
            renderSite(destination, point, element);
        }
    }

    /**
     * @param destination
     * @param point
     * @param element
     */
    private void renderSite(Graphics2D destination, Point point, IDataElement element) {
        switch (RenderOptions.scale) {
        case SMALL:
            destination.setColor(Color.BLACK);// TODO: hardcoded style
            destination.drawRect(point.x - 1, point.y - 1, 2, 2);
            break;
        case MEDIUM:
            destination.setColor(Color.BLACK);
            destination.drawOval(point.x - 5, point.y - 5, 10, 10);
            break;
        }

    }

    /**
     * @param destination
     * @param point
     * @param element
     */
    private void renderSector(Graphics2D destination, Point point, IDataElement element) {
    }
}
