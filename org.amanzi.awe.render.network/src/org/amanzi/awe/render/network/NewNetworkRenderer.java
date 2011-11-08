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

import org.amanzi.awe.neostyle.BaseNeoStyle;
import org.amanzi.awe.neostyle.NetworkNeoStyle;
import org.amanzi.awe.neostyle.NetworkNeoStyleContent;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.apache.commons.lang.ObjectUtils;

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
    private static final double FULL_CIRCLE = 360.0;

    @Override
    protected void renderElement(Graphics2D destination, Point point, IDataElement site, IRenderableModel model) {
        INodeType type = NodeTypeManager.getType(site.get(NewAbstractService.TYPE).toString());
        if (!NetworkElementNodeType.SITE.equals(type)) {
            throw new IllegalArgumentException("Could not render element of type " + type.getId());
        }

        renderSite(destination, point, site);
        if (RenderOptions.scale == Scale.LARGE) {
            int i = 0;
            Integer sCount = (Integer)site.get(NewNetworkService.SECTOR_COUNT);
            for (IDataElement sector : ((INetworkModel)model).getChildren(site)) {
                Double azimuth = (Double)sector.get(AZIMUTH);
                Double beamwidth = (Double)sector.get(BEAMWIDTH);
                if (azimuth == null || beamwidth == null || beamwidth == 0) {
                    beamwidth = FULL_CIRCLE / (sCount == null ? 1 : sCount);
                    azimuth = beamwidth * i;
                    beamwidth = beamwidth.doubleValue() * 0.8;
                }
                renderSector(destination, point, azimuth, beamwidth);
                i++;
            }
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
            destination.setColor(RenderOptions.border);
            destination.drawRect(point.x - size / 2, point.y - size / 2, size, size);
            break;
        case MEDIUM:
            size = RenderOptions.site_size;
            destination.setColor(RenderOptions.border);
            destination.drawOval(point.x - size / 2, point.y - size / 2, size, size);
            break;
        case LARGE:
            size = RenderOptions.large_sector_size / 3;
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
    private void renderSector(Graphics2D destination, Point point, double azimuth, double beamwidth) {
        switch (RenderOptions.scale) {
        case LARGE:

            double angle1 = 90 - azimuth - beamwidth / 2.0;
            double angle2 = angle1 + beamwidth;

            GeneralPath path = new GeneralPath();
            path.moveTo(point.x, point.y);
            Arc2D a = new Arc2D.Double();
            a.setArcByCenter(point.x, point.y, RenderOptions.large_sector_size, angle2, beamwidth, Arc2D.OPEN);
            path.append(a.getPathIterator(null), true);
            path.closePath();

            destination.setColor(RenderOptions.border);
            destination.draw(path);
            destination.setColor(RenderOptions.sector_fill);
            destination.fill(path);

            break;
        }
    }

    @Override
    protected void setStyle(Graphics2D destination) {
        super.setStyle(destination);

        NetworkNeoStyle newStyle = (NetworkNeoStyle)getContext().getLayer().getStyleBlackboard().get(NetworkNeoStyleContent.ID);
        if (ObjectUtils.equals(style, newStyle)) {
            return;
        }
        style = newStyle;
        RenderOptions.alpha = 255 - (int)((double)newStyle.getSymbolTransparency() / 100.0 * 255.0);
        RenderOptions.border = changeColor(newStyle.getLine(), RenderOptions.alpha);
        RenderOptions.large_sector_size = newStyle.getSymbolSize();
        RenderOptions.sector_fill = changeColor(newStyle.getFill(), RenderOptions.alpha);
        RenderOptions.site_fill = changeColor(newStyle.getSiteFill(), RenderOptions.alpha);

        RenderOptions.maxSitesFull = newStyle.getSmallSymb();
        RenderOptions.maxSitesLabel = newStyle.getLabeling();
        RenderOptions.maxSitesLite = newStyle.getSmallestSymb();
        RenderOptions.maxSymbolSize = newStyle.getMaximumSymbolSize();

    }
}
