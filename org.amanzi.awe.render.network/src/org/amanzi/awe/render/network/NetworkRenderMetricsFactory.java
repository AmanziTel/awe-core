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

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IRenderableModel;

public class NetworkRenderMetricsFactory implements IRenderMetricsFactory {

    /**
     * @return a NetworkRenderMetrics constructed on this context
     */
    public AbstractRenderMetrics createMetrics(IRenderContext context) {
        return new NetworkRenderMetrics(context, this);
    }

    /**
     * @return NetworkRenderer.class
     */
    public Class< ? extends IRenderer> getRendererType() {
        return NewNetworkRenderer.class;
    }

    @Override
    public boolean canRender(IRenderContext context) throws IOException {
        for (IGeoResource resource : context.getLayer().getGeoResources()) {
            if (resource.canResolve(IRenderableModel.class) && resource.canResolve(INetworkModel.class)) {
                return true;
                // return resource.resolve(GeoNeo.class, null).getGisType() == GisTypes.NETWORK;
            }
        }
        return false;
    }

}
