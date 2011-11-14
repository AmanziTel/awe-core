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

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.services.enums.GisTypes;

/**
 * TODO Purpose of
 * <p>
 * RenderMetricsFactory for GeoNeo with GisTypes==GisTypes.Tems
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class TemsRenderMetricsFactory implements IRenderMetricsFactory {

    @Override
    public boolean canRender(IRenderContext context) throws IOException {
        for (IGeoResource resource : context.getLayer().getGeoResources()) {
            if (resource.canResolve(GeoNeo.class)) {
                return resource.resolve(GeoNeo.class, null).getGisType() == GisTypes.DRIVE ||
                	   resource.resolve(GeoNeo.class, null).getGisType() == GisTypes.OSS;
            }
        }
        return false;
    }

    @Override
    public AbstractRenderMetrics createMetrics(IRenderContext context) {
        return new TemsRendererMetrics(context, this);
    }

    @Override
    public Class< ? extends IRenderer> getRendererType() {
        return null;
    }

}
