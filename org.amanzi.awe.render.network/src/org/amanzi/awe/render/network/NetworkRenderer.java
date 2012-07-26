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
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.INetworkModel.ISectorElement;
import org.amanzi.neo.models.network.INetworkModel.ISiteElement;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.models.render.IRenderableModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
    protected void renderElement(final Graphics2D destination, final Point point, final ILocationElement site, final IGISModel model)
            throws ModelException {

        ISiteElement siteElement = (ISiteElement)site;

        renderCoordinateElement(destination, point, site);
        if (networkRendererStyle.getScale() == Scale.LARGE) {
            int i = 0;
            Pair<Double, Double> sectorParameters = null;
            for (ISectorElement sector : siteElement.getSectors()) {

                sectorParameters = getSectorParameters(siteElement, sector, i, siteElement.getSectors().size());
                renderSector(destination, point, sectorParameters.getLeft(), sectorParameters.getRight(), sector);
                i++;
            }
        }
    }

    /**
     * Get azimuth and beamwidth
     * 
     * @param site
     * @param sector
     * @param i
     * @return
     */
    private Pair<Double, Double> getSectorParameters(final ISiteElement site, final ISectorElement sector, final int i,
            final int sectorCount) {
        Double azimuth = sector.getAzimuth();
        Double beamwidth = sector.getBeamwidth();
        if ((azimuth == null) || (beamwidth == null) || (beamwidth == 0)) {
            beamwidth = FULL_CIRCLE / (sectorCount == 0 ? 1 : sectorCount);
            azimuth = beamwidth * i;

            beamwidth = beamwidth.doubleValue() * 0.8;
        }
        return new ImmutablePair<Double, Double>(azimuth, beamwidth);
    }

    /**
     * render sector element on map
     * 
     * @param destination
     * @param point
     * @param element
     */
    private void renderSector(final Graphics2D destination, final Point point, final double azimuth, final double beamwidth,
            final IDataElement sector) {
        int size = getSize();
        int x = getSectorXCoordinate(point, size);
        int y = getSectorYCoordinate(point, size);
        destination.setColor(networkRendererStyle.changeColor(getColor(sector), networkRendererStyle.getAlpha()));
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);
        Arc2D a = createSector(point, networkRendererStyle.getLargeElementSize(), getAngle(azimuth, beamwidth), beamwidth);
        path.append(a.getPathIterator(null), true);
        path.closePath();
        destination.draw(path);
        destination.fill(path);
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
    private Arc2D createSector(final Point point, final double radius, final double angle, final double beamwidth) {
        int size = getSize();
        Arc2D sector = new Arc2D.Double();
        sector.setArcByCenter(getSectorXCoordinate(point, size), getSectorYCoordinate(point, size), radius, angle, beamwidth,
                Arc2D.OPEN);
        return sector;
    }

    private int getSectorXCoordinate(final Point point, final int size) {
        return point.x - (size / 2);
    }

    private int getSectorYCoordinate(final Point point, final int size) {
        return point.y - (size / 2);
    }

    private double getAngle(final double azimuth, final double beamwidth) {
        return (90 - azimuth - (beamwidth / 2.0)) + beamwidth;
    }

    @Override
    protected void setStyle(final Graphics2D destination) {
        super.setStyle(destination);
        NetworkNeoStyle newStyle = (NetworkNeoStyle)getContext().getLayer().getStyleBlackboard().get(NetworkNeoStyleContent.ID);
        if ((newStyle == null) || (style == null) || newStyle.equals(style)) {
            return;
        }

        style = newStyle;
        networkRendererStyle.setAlpha(255 - (int)(((double)newStyle.getSymbolTransparency() / 100.0) * 255.0));
        networkRendererStyle.setBorderColor(networkRendererStyle.changeColor(newStyle.getLine(), networkRendererStyle.getAlpha()));
        networkRendererStyle.setLargeElementSize(newStyle.getSymbolSize());
        networkRendererStyle.setSectorFill(networkRendererStyle.changeColor(newStyle.getFill(), networkRendererStyle.getAlpha()));
        networkRendererStyle.setSiteFill(networkRendererStyle.changeColor(newStyle.getSiteFill(), networkRendererStyle.getAlpha()));

        networkRendererStyle.setMaxSitesFull(newStyle.getSmallSymb());
        networkRendererStyle.setMaxSitesLabel(newStyle.getLabeling());
        networkRendererStyle.setMaxSitesLite(newStyle.getSmallestSymb());
        networkRendererStyle.setMaxSymbolSize(newStyle.getMaximumSymbolSize());
        networkRendererStyle.setDefaultBeamwidth(newStyle.getDefaultBeamwidth());
    }

    @Override
    protected double calculateResult(final Envelope dbounds, final IGISModel resource) {

        return (getRenderableElementCount(resource) / (dbounds.getHeight() * dbounds.getWidth()));
    }

    @Override
    protected void setDrawLabel(final double countScaled) {
        networkRendererStyle.setDrawLabels(countScaled < networkRendererStyle.getMaxSitesLabel());
    }

    @Override
    protected long getRenderableElementCount(final IGISModel model) {
        return model.getCount();
    }

    @Override
    protected Class< ? extends IRenderableModel> getResolvedClass() {
        return INetworkModel.class;
    }

    @Override
    protected Color getDefaultFillColorByElement(final IDataElement element) {
        try {
            INodeType type = NodeTypeManager.getInstance().getType(element);
            if (type.getId().equals(NetworkElementType.SECTOR.getId())) {
                return networkRendererStyle.getSectorFill();
            } else if (type.getId().equals(NetworkElementType.SITE.getId())) {
                return networkRendererStyle.getSiteFill();
            }
        } catch (NodeTypeNotExistsException e) {
            // TODO: error!
        }
        return networkRendererStyle.getBorderColor();
    }

    @Override
    protected void renderCoordinateElement(final Graphics2D destination, final Point point, final IDataElement element) {
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