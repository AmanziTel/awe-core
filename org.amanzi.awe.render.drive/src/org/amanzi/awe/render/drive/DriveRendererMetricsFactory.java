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

import org.amanzi.neo.models.drive.IDriveModel;

/**
 * metrics factory for drive rendere
 * 
 * @author Vladislav_Kondratenko
 */
public class DriveRendererMetricsFactory implements IRenderMetricsFactory {

    /**
     * @return a DriveRendererMetrics constructed on this context
     */
    @Override
    public AbstractRenderMetrics createMetrics(IRenderContext context) {
        return new DriveRenderMetrics(context, this);
    }

    /**
     * @return DriveRenderer.class
     */
    @Override
    public Class< ? extends IRenderer> getRendererType() {
        return DriveRenderer.class;
    }

    @Override
    public boolean canRender(IRenderContext context) throws IOException {
        for (IGeoResource resource : context.getLayer().getGeoResources()) {
            return resource.canResolve(IDriveModel.class);
        }
        return false;
    }
}
