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
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.IDataElement;
import org.geotools.brewer.color.ColorBrewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NewNetworkRenderer extends AbstractRenderer {

    // TODO: find a better place for the constants
    public static final String AZIMUTH = "azimuth";
    public static final String BEAMWIDTH = "beam";

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
        int size = 2;
        switch (RenderOptions.scale) {
        case SMALL:
            destination.setColor(RenderOptions.border);// TODO: hardcoded style
            destination.drawRect(point.x - size / 2, point.y - size / 2, size, size);
            break;
        case MEDIUM:
            size = RenderOptions.site_size;
            destination.setColor(RenderOptions.border);
            destination.drawOval(point.x - size / 2, point.y - size / 2, size, size);
            break;
        case LARGE:
            size = RenderOptions.large_sector_size / 4;
            destination.setColor(RenderOptions.border);
            destination.drawOval(point.x - size / 2, point.y - size / 2, size, size);
            destination.setColor(RenderOptions.site_fill);
            destination.fillOval(point.x - size / 2, point.y - size / 2, size, size);
        }

    }

    /**
     * @param destination
     * @param point
     * @param element
     */
    private void renderSector(Graphics2D destination, Point point, IDataElement element) {
        switch (RenderOptions.scale) {
        case LARGE:
            Integer azimuth = (Integer)element.get(AZIMUTH);
            Integer beamwidth = (Integer)element.get(BEAMWIDTH);
            if (azimuth == null || beamwidth == null) {
                // TODO: calculate average values(how?)
                destination.setColor(Color.RED);
                destination.drawOval(point.x - 1, point.y - 1, 2, 2);
                return;
            }
            double angle1 = 90 - azimuth - beamwidth / 2.0;
            double angle2 = angle1 + beamwidth;

            GeneralPath path = new GeneralPath();
            path.moveTo(point.x, point.y);
            Arc2D a = new Arc2D.Double();
            a.setArcByCenter(point.x, point.y, 20, angle2, beamwidth, Arc2D.OPEN);
            path.append(a.getPathIterator(null), true);
            path.closePath();

            destination.setColor(RenderOptions.border);
            destination.draw(path);
            destination.setColor(RenderOptions.sector_fill);
            destination.fill(path);

            break;
        }
    }
}
