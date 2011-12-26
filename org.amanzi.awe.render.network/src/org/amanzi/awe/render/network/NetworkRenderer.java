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
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.apache.commons.lang.ObjectUtils;

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
    protected void renderElement(Graphics2D destination, Point point, IDataElement site, IRenderableModel model) {
        renderCoordinateElement(destination, point, site);
        if (networkRendererStyle.getScale() == Scale.LARGE) {
            int i = 0;
            Integer sCount = (Integer)site.get(NetworkService.SECTOR_COUNT);
            for (IDataElement sector : ((INetworkModel)model).getChildren(site)) {
                Double azimuth = (Double)sector.get(AZIMUTH);
                Double beamwidth = (Double)sector.get(BEAMWIDTH);
                if (azimuth == null || beamwidth == null || beamwidth == 0) {
                    beamwidth = FULL_CIRCLE / (sCount == null ? 1 : sCount);
                    azimuth = beamwidth * i;
                    beamwidth = beamwidth.doubleValue() * 0.8;
                }
                renderSector(destination, point, azimuth, beamwidth, sector);
                i++;
            }
        }
    }

    /**
     * render sector element on map
     * 
     * @param destination
     * @param point
     * @param element
     */
    private void renderSector(Graphics2D destination, Point point, double azimuth, double beamwidth, IDataElement sector) {
        int size = getSize();
        int x = point.x - size / 2;
        int y = point.y - size / 2;
        destination.setColor(networkRendererStyle.changeColor(getColor(sector), networkRendererStyle.getAlpha()));
        double angle1 = 90 - azimuth - beamwidth / 2.0;
        double angle2 = angle1 + beamwidth;
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);
        Arc2D a = new Arc2D.Double();
        a.setArcByCenter(x, y, networkRendererStyle.getLargeElementSize(), angle2, beamwidth, Arc2D.OPEN);
        path.append(a.getPathIterator(null), true);
        path.closePath();
        destination.draw(path);
        destination.fill(path);
    }

    @Override
    protected void setStyle(Graphics2D destination) {
        super.setStyle(destination);
        NetworkNeoStyle newStyle = (NetworkNeoStyle)getContext().getLayer().getStyleBlackboard().get(NetworkNeoStyleContent.ID);
        if (ObjectUtils.equals(style, newStyle)) {
            return;
        }
        style = newStyle;
        networkRendererStyle.setAlpha(255 - (int)((double)newStyle.getSymbolTransparency() / 100.0 * 255.0));
        networkRendererStyle.setBorderColor(networkRendererStyle.changeColor(newStyle.getLine(), networkRendererStyle.getAlpha()));
        networkRendererStyle.setLargeElementSize(newStyle.getSymbolSize());
        networkRendererStyle.setSectorFill(networkRendererStyle.changeColor(newStyle.getFill(), networkRendererStyle.getAlpha()));
        networkRendererStyle.setSiteFill(networkRendererStyle.changeColor(newStyle.getSiteFill(), networkRendererStyle.getAlpha()));

        networkRendererStyle.setMaxSitesFull(newStyle.getSmallSymb());
        networkRendererStyle.setMaxSitesLabel(newStyle.getLabeling());
        networkRendererStyle.setMaxSitesLite(newStyle.getSmallestSymb());
        networkRendererStyle.setMaxSymbolSize(newStyle.getMaximumSymbolSize());

    }

    @Override
    protected double calculateResult(Envelope dbounds, IRenderableModel resource) {
        return (((INetworkModel)resource).getNodeCount(NetworkElementNodeType.SITE) / 2)
                / (dbounds.getHeight() * dbounds.getWidth());
    }

    @Override
    protected void setDrawLabel(double countScaled) {
        networkRendererStyle.setDrawLabels(countScaled < networkRendererStyle.getMaxSitesLabel());
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
        return (((INetworkModel)model).getNodeCount(NetworkElementNodeType.SITE) / 2);// TODO:

    }

    @Override
    protected Class< ? extends IRenderableModel> getResolvedClass() {
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
    protected void renderCoordinateElement(Graphics2D destination, Point point, IDataElement element) {
        switch (networkRendererStyle.getScale()) {
        case SMALL:
            drawCoordinateElement(RenderShape.RECTANGLE, destination, point, element, false);
            break;
        case MEDIUM:
            drawCoordinateElement(RenderShape.ELLIPSE, destination, point, element, false);
            break;
        case LARGE:
            drawCoordinateElement(RenderShape.ELLIPSE, destination, point, element, true);
        }
    }
}
