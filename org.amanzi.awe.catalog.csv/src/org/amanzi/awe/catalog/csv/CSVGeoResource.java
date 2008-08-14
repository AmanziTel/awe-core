package org.amanzi.awe.catalog.csv;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;

public class CSVGeoResource extends IGeoResource {
    private URL url;
    private CSVService service;
    
    public CSVGeoResource( CSVService service ) {
        this.service = service;
        try {
            URL serviceUrl = service.getIdentifier();
            url = new URL( serviceUrl+"#"+serviceUrl.getFile() );
        } catch (MalformedURLException e) {            
        }
    }

    @Override
    public URL getIdentifier() {
        return url;
    }

    private CSVGeoResourceInfo info;
    public CSVGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    info = new CSVGeoResourceInfo( this, monitor );
                }
            }
        }
        return info;
    }

    @Override
    public CSVService service( IProgressMonitor monitor ) throws IOException {
        return service;
    }

    private CSV csv;
    /**
     * Return a utility object used to read the CVS data. This is used in two places:
     * <ul>
     * <li>the CSVGeoResourceInfo class uses this to determine the envelope for the map</li>
     * <li>the resolve method returns this to the map for use in drawing the points</li>
     * </ul>
     * 
     * @see org.amanzi.awe.catalog.csv.CSV
     * @param monitor
     * @return
     * @throws IOException
     */
    public CSV getCSV( IProgressMonitor monitor ) throws IOException {
        if (csv == null) {
            synchronized (this) {
                if (csv == null) {
                    csv = new CSV(service.getValidURL());
                }
            }
        }
        return csv;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee.isAssignableFrom(CSV.class) || super.canResolve(adaptee);
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee.isAssignableFrom(CSV.class)) {
            return adaptee.cast(getCSV(monitor));
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
