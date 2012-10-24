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

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

public class GeoService extends IService {

    private static Logger LOGGER = Logger.getLogger(GeoService.class);

    private List<IGeoResource> members;
    private Throwable message;
    private final URL url;
    private final Map<String, Serializable> params;

    GeoService(final Map<String, Serializable> params) {
        this.params = params;
        url = (URL)params.get(GeoServiceExtension.URL_KEY);
    }

    private void addAllGIS(final IRenderableModel model, final List<IGeoResource> resourcesList) {
        for (IGISModel gisModel : model.getAllGIS()) {
            if (gisModel.canRender()) {
                resourcesList.add(new GeoResource(this, gisModel));
            }
        }
    }

    @Override
    public IServiceInfo createInfo(final IProgressMonitor monitor) throws IOException {
        return new GeoServiceInfo(this);
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    @Override
    public URL getIdentifier() {
        return url;
    }

    @Override
    public Throwable getMessage() {
        return message;
    }

    @Override
    public Status getStatus() {
        // did an error occur
        if (message != null) {
            return Status.BROKEN;
        }
        // has the file been parsed yet
        if (url == null) {
            return Status.NOTCONNECTED;
        }
        return Status.CONNECTED;
    }

    @Override
    public List< ? extends IGeoResource> resources(final IProgressMonitor monitor) throws IOException {
        if (members == null) {
            synchronized (this) {
                List<IGeoResource> result = new ArrayList<IGeoResource>();
                try {
                    IProjectModel activeProject = NeoCatalogPlugin.getDefault().getProjectModelProvider().getActiveProjectModel();

                    // add all network models
                    if (activeProject != null) {
                        for (INetworkModel networkModel : NeoCatalogPlugin.getDefault().getNetworkModelProvider()
                                .findAll(activeProject)) {
                            addAllGIS(networkModel, result);
                        }
                        for (IDriveModel driveModel : NeoCatalogPlugin.getDefault().getDriveModelProvider().findAll(activeProject)) {
                            addAllGIS(driveModel, result);
                        }
                    }
                } catch (ModelException e) {
                    LOGGER.error("Could not create a list of resources.", e);
                    message = e;
                }
                members = result;

            }
        }
        return members;
    }

}
