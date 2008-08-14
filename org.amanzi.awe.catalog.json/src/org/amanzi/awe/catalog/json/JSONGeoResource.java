package org.amanzi.awe.catalog.json;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;

public class JSONGeoResource extends IGeoResource {
    private URL identifierUrl;
    private JSONService service;
    
    public JSONGeoResource( JSONService service ) {
        this.service = service;
        try {
            URL serviceUrl = service.getIdentifier();
            identifierUrl = new URL( serviceUrl+"#"+serviceUrl.getFile() );
        } catch (MalformedURLException e) {            
        }
    }

    @Override
    public URL getIdentifier() {
        return identifierUrl;
    }

    private JSONGeoResourceInfo info;
    public JSONGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    info = new JSONGeoResourceInfo( this, monitor );
                }
            }
        }
        return info;
    }

    @Override
    public JSONService service( IProgressMonitor monitor ) throws IOException {
        return service;
    }

    private JSONReader jsonReader;
    /**
     * Return a utility object used to read the CVS data. This is used in two places:
     * <ul>
     * <li>the CSVGeoResourceInfo class uses this to determine the envelope for the map</li>
     * <li>the resolve method returns this to the map for use in drawing the points</li>
     * </ul>
     * 
     * @see org.amanzi.awe.catalog.json.JSONReader
     * @param monitor
     * @return
     * @throws IOException
     */
    public JSONReader getJSONReader( IProgressMonitor monitor ) throws IOException {
        if (jsonReader == null) {
            synchronized (this) {
                if (jsonReader == null) {
                    jsonReader = new JSONReader(service);
                }
            }
        }
        return jsonReader;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee.isAssignableFrom(JSONReader.class) || super.canResolve(adaptee);
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee.isAssignableFrom(JSONReader.class)) {
            return adaptee.cast(getJSONReader(monitor));
        }
        return super.resolve(adaptee, monitor);
    }

    public Throwable getMessage() {
        return service.getMessage();
    }

    public Status getStatus() {
        return service.getStatus();
    }
}
