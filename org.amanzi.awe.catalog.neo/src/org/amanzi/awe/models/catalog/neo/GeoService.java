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

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.model.impl.RenderableModel.GisModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

public class GeoService extends IService {

    private static Logger LOGGER = Logger.getLogger(GeoService.class);

    private List<IGeoResource> members;
    private Throwable message;
    private URL url;
    private final Map<String, Serializable> params;

    GeoService(Map<String, Serializable> params) {
        this.params = params;
        url = (URL)params.get(GeoServiceExtension.URL_KEY);
    }

    @Override
    public Status getStatus() {
        // did an error occur
        if (message != null)
            return Status.BROKEN;
        // has the file been parsed yet
        if (url == null)
            return Status.NOTCONNECTED;
        return Status.CONNECTED;
    }

    @Override
    public Throwable getMessage() {
        return message;
    }

    @Override
    public URL getIdentifier() {
        return url;
    }

    @Override
    public List< ? extends IGeoResource> resources(IProgressMonitor monitor) throws IOException {
        if (members == null) {
            synchronized (this) {
                List<IGeoResource> result = new ArrayList<IGeoResource>();
                try {
                    // TODO: current project or all the projects?
                    for (IRenderableModel model : ProjectModel.getCurrentProjectModel().getAllRenderableModels()) {
                        if (checkForExistCoordinateElement(model)) {
                            for (GisModel gisElements : model.getAllGisModels()) {
                                result.add(new GeoResource(this, model, gisElements));
                            }
                        }
                    }
                } catch (AWEException e) {
                    LOGGER.error("Could not create a list of resources.", e);
                    message = e;
                }
                members = result;

            }
        }
        return members;
    }

    /**
     * just check for location contain
     * 
     * @param gis
     * @return
     */
    private boolean checkForExistCoordinateElement(IRenderableModel gis) {
        if (gis.getMaxLongitude() != 0d && 
            gis.getMinLongitude() != 0d && 
            gis.getMaxLatitude() != 0d && 
            gis.getMinLatitude() != 0d) {
            return true;
        }
        return false;
    }

    @Override
    public IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
        return new GeoServiceInfo(this);
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

}
