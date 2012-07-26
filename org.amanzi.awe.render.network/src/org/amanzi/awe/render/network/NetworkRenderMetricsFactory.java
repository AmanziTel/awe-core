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

import org.amanzi.neo.models.network.INetworkModel;

public class NetworkRenderMetricsFactory implements IRenderMetricsFactory {

    /**
     * @return a NetworkRenderMetrics constructed on this context
     */
    @Override
    public AbstractRenderMetrics createMetrics(final IRenderContext context) {
        return new NetworkRenderMetrics(context, this);
    }

    /**
     * @return NetworkRenderer.class
     */
    @Override
    public Class< ? extends IRenderer> getRendererType() {
        return NetworkRenderer.class;
    }

    @Override
    public boolean canRender(final IRenderContext context) throws IOException {
        for (IGeoResource resource : context.getLayer().getGeoResources()) {
            return resource.canResolve(INetworkModel.class);
        }
        return false;
    }
}
