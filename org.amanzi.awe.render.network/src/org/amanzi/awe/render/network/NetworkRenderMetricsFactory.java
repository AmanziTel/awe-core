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
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.services.enums.GisTypes;

public class NetworkRenderMetricsFactory implements IRenderMetricsFactory {

    /**
     * This metrics factory supports the Amanzi GeoNeo GIS conventions for Neo4j
     * data sources (IGeoResources). See the org.amanzi.awe.catalog.neo plugin.
     * Here we return true if the IGeoResource passed in the context canResolve
     * the GeoNeo.class the catalog package.
     * 
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#canRender(net.refractions.udig.project.render.IRenderContext)
     */
    public boolean canRender( IRenderContext context ) throws IOException {
        for(IGeoResource resource : context.getLayer().getGeoResources()){
            //TODO: test also that the data is for network only.
            if(resource.canResolve(GeoNeo.class)){
                return resource.resolve(GeoNeo.class, null).getGisType() == GisTypes.NETWORK;
            }
        }
        return false;
    }

    /**
     * @return a NetworkRenderMetrics constructed on this context
     */
    public IRenderMetrics createMetrics( IRenderContext context ) {
        return new NetworkRenderMetrics(context, this);
    }

    /**
     * @return NetworkRenderer.class
     */
    public Class< ? extends IRenderer> getRendererType() {
        return NetworkRenderer.class;
    }

}
