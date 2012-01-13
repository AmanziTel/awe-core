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
package org.amanzi.awe.models.catalog.neo;

import net.refractions.udig.catalog.IGeoResourceInfo;

import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.RenderableModel.GisModel;
import org.amanzi.neo.services.ui.icons.IconManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.geotools.geometry.jts.ReferencedEnvelope;

public class GeoResourceInfo extends IGeoResourceInfo {

    GeoResourceInfo(final IRenderableModel sourceModel, GisModel gis, IProgressMonitor monitor) {
        this.name = gis.getName();
        this.title = name;
        this.description = sourceModel.getDescription();
        this.bounds = sourceModel.getBounds();

        // taken from previous implementation
        this.icon = new ImageDescriptor() {
            private ImageData imageData;

            @Override
            public ImageData getImageData() {
                if (imageData == null) {
                    imageData = IconManager.getInstance().getImage(sourceModel.getType()).getImageData();
                }
                return imageData;
            }
        };
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return this.bounds;
    }
}
