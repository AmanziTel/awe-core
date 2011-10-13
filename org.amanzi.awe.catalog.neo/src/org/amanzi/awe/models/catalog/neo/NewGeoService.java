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

import org.amanzi.awe.catalog.neo.NeoServiceExtension;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

public class NewGeoService extends IService {

    private static Logger LOGGER = Logger.getLogger(NewGeoService.class);

    private List<IGeoResource> members;
    private Throwable message;
    private URL url;
    private IServiceInfo info;
    private final Map<String, Serializable> params;
    private final Class<Object> type;

    @SuppressWarnings("unchecked")
    NewGeoService(Map<String, Serializable> params) {
        this.params = params;
        url = (URL)params.get(NewGeoServiceExtension.URL_KEY);
        type = (Class<Object>)params.get(NeoServiceExtension.CLASS_KEY);
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
                        result.add(new NewGeoResource(this, model));
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

    @Override
    public IServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    info = new NewGeoServiceInfo(this);
                }
            }
        }
        return info;
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

}
