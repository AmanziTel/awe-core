/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.awe.render.drive;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.core.enums.GisTypes;

/**
 * TODO Purpose of
 * <p>
 * RenderMetricsFactory for GeoNeo with GisTypes==GisTypes.Tems
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class TemsRenderMetricsFactory implements IRenderMetricsFactory {

    @Override
    public boolean canRender(IRenderContext context) throws IOException {
        for (IGeoResource resource : context.getLayer().getGeoResources()) {
            // TODO: test also that the data is for network only.
            if (resource.canResolve(GeoNeo.class)) {
                return resource.resolve(GeoNeo.class, null).getGisType() == GisTypes.Tems;
            }
        }
        return false;
    }

    @Override
    public IRenderMetrics createMetrics(IRenderContext context) {
        return new TemsRendererMetrics(context, this);
    }

    @Override
    public Class< ? extends IRenderer> getRendererType() {
        return null;
    }

}
