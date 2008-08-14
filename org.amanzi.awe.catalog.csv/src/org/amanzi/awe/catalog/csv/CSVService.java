package org.amanzi.awe.catalog.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This implementation of IService can resolve either a File or a URL.
 * The CSVServiceExtension will already have filtered for *.csv resources
 * and will construct this class on the URL for that resource. It can then
 * be used to actually access the data in the resource, using the CSV 
 * utility class wrapper on the CSVReader library (separate plug-in).
 * 
 * We support both local files and CSV formated web resources. We expect
 * the CSV files to contain "x" and "y" columns that are then used to
 * place the data on the uDIG map using the current world CRS. It is
 * assumed that the data is in the same projection as the view.
 * 
 * @author craig
 */
public class CSVService extends IService {

	private Map<String, Serializable> params;
    private URL url;    // original URL created with
    private URL validUrl;   // URL tested for existence (either file or HTTP)
    private Throwable msg;
    private CSVServiceInfo info;

    CSVService( Map<String, Serializable> params ){
        this.params = params;
        url = (URL) params.get(CSVServiceExtension.KEY);
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee.isAssignableFrom(File.class) || adaptee.isAssignableFrom(URL.class) || super.canResolve(adaptee);
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee.isAssignableFrom(File.class)) {
            return adaptee.cast(getValidFile());
        }
        if (adaptee.isAssignableFrom(URL.class)) {
            return adaptee.cast(getValidURL());
        }
        return super.resolve(adaptee, monitor);
    }

    @Override
	public Map<String, Serializable> getConnectionParams() {
		return params;
	}

	@Override
	public CSVServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    info = new CSVServiceInfo(this);
                }
            }
        }       
        return info;
    }

	private List<CSVGeoResource> members;
    /**
     * This method returns a list containing a single CSVGeoResource object which contains the
     * actual data to be displayed.
     * 
     * @see org.amanzi.awe.catalog.csv.CSVGeoResource
     */
    public List<CSVGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            synchronized (this) {
                if (members == null) {
                    CSVGeoResource dataHandle = new CSVGeoResource(this);
                    members = Collections.singletonList(dataHandle);
                }
            }
        }
        return members;
    }

    public URL getIdentifier() {
		return url;
	}

	public Throwable getMessage() {
		return msg;
	}

    public Status getStatus() {
        // did an error occur
        if (msg != null)
            return Status.BROKEN;
        // has the file been parsed yet
        if (validUrl == null)
            return Status.NOTCONNECTED;
        return Status.CONNECTED;
    }

	private File getValidFile(){
	    try {
            return (new File(getValidURL().toURI()));
        } catch (URISyntaxException e) {
            return null;    // if we get here, the msg should already have been set in getValidURL()
        }
	}

	public URL getValidURL(){
        if (validUrl == null) { //lazy creation
            synchronized (this) { //support concurrent access
                if (validUrl == null) {
                    try {
                        if(url.getProtocol().equals("file")){
                            File file = new File(url.toURI());  // throws exception on invalid file url
                            if( !file.exists() ){
                                msg = new FileNotFoundException(url.toString());
                            }
                        }else{
                            url.openConnection();   // throws exception on invalid web url
                        }
                        validUrl = url;
                    } catch (Throwable t) {
                        msg = t;
                    }
                }
            }
        }
        return validUrl;
    }
}
