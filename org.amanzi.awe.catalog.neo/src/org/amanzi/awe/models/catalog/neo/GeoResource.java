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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.models.render.IGISModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

public class GeoResource extends IGeoResource {

    private static Logger LOGGER = Logger.getLogger(GeoResource.class);
    private final IGISModel source;
    private final IService service;
    private final URL url;

    protected GeoResource(final IService service, final IGISModel source) {
        // validate
        if (service == null) {
            throw new IllegalArgumentException("Geo service is null.");
        }
        if (source == null) {
            throw new IllegalArgumentException("Source is null.");
        }

        this.source = source;
        this.service = service;
        url = getURL(service, source);
    }

    /**
     * form URL which will be used in renderer as identifier of gis
     * 
     * @param service
     * @param source
     * @param gis
     * @return
     */
    private URL getURL(final IService service, final IGISModel source) {
        try {
            String urlString = service.getIdentifier().toString() + File.separator + "#" + source.getName();

            urlString = urlString.replace(" ", "_").replace("\\", "/");

            return new URL(urlString);
        } catch (MalformedURLException e) {
            LOGGER.error("Could not build identifier url.", e);
        }
        return null;
    }

    @Override
    public Status getStatus() {
        return service.getStatus();
    }

    @Override
    public Throwable getMessage() {
        return service.getMessage();
    }

    @Override
    public IGeoResourceInfo createInfo(final IProgressMonitor monitor) throws IOException {
        return new GeoResourceInfo(source, monitor);
    }

    @Override
    public IService service(final IProgressMonitor monitor) throws IOException {
        return service;
    }

    @Override
    public URL getIdentifier() {
        return url;
    }

    @Override
    public <T> boolean canResolve(final Class<T> adaptee) {
        return (adaptee.isAssignableFrom(IGISModel.class) || ((source.canResolve(adaptee)) && super.canResolve(adaptee)));
    }

    @Override
    public <T> T resolve(final Class<T> adaptee, final IProgressMonitor monitor) throws IOException {
        if (adaptee.isAssignableFrom(IGISModel.class)) {
            return adaptee.cast(source);
        }
        return super.resolve(adaptee, monitor);
    }
}
