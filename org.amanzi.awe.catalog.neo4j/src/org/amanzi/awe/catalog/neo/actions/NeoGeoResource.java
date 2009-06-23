package org.amanzi.awe.catalog.neo.actions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;



import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IGeoResource;


public class NeoGeoResource extends IGeoResource {
    private URL identifierUrl;
    private NeoService service;
    
    public NeoGeoResource( NeoService service ) {
        this.service = service;
        try {
            URL serviceUrl = service.getIdentifier();
            identifierUrl = new URL( serviceUrl.toString() );
        } catch (MalformedURLException e) {            
        }
    }

    @Override
    public URL getIdentifier() {
        return identifierUrl;
    }

    private NeoGeoResourceInfo info;
    public NeoGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    info = new NeoGeoResourceInfo( this, monitor );
                }
            }
        }
        return info;
    }

    @Override
    public NeoService service( IProgressMonitor monitor ) throws IOException {
        return service;
    }

    private NeoReader jsonReader;
  
    public NeoReader getNeoReader( IProgressMonitor monitor ) throws IOException {
        if (jsonReader == null) {
            synchronized (this) {
                if (jsonReader == null) {
                    jsonReader = new NeoReader(service);
                }
            }
        }
        return jsonReader;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee.isAssignableFrom(NeoReader.class) || super.canResolve(adaptee);
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee.isAssignableFrom(NeoReader.class)) {
            return adaptee.cast(getNeoReader(monitor));
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
