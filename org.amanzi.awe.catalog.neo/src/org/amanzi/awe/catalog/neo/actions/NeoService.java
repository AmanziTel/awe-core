package org.amanzi.awe.catalog.neo.actions;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;

public class NeoService extends IService
{
	URL urlIndentifier;
	
    private URL url; // original URL created with
 
    private Map<String, Serializable> params;
    
    private Class<Object> type;
    
    private Throwable msg;
    
    boolean validDir;
    
    private NeoServiceInfo info;
    
    private List<NeoGeoResource> members;
 

	@SuppressWarnings("unchecked")
	public NeoService(Map<String, Serializable> params) 
	{
		// TODO Auto-generated constructor stub
		this.params=params;
		 url = (URL) params.get(NeoServiceExtension.URL_KEY);
		 type = (Class<Object>) params.get(NeoServiceExtension.CLASS_KEY);
	}
	

	@Override
	public Map<String, Serializable> getConnectionParams() {
		// TODO Auto-generated method stub
		return params;
	}
	

	@Override
	 public NeoServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    info = new NeoServiceInfo(this);
                }
            }
        }
        return info;
    }
	

	@Override
	 public List<NeoGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            synchronized (this) {
                if (members == null) {
                    NeoGeoResource dataHandle = new NeoGeoResource(this);
                    members = Collections.singletonList(dataHandle);
                }
            }
        }
        return members;
    }
	
	
	@Override
	public URL getIdentifier() {
		// TODO Auto-generated method stub
		 return url;
	}
	

	@Override
	 public Throwable getMessage() {
        return msg;
    }
	

	@Override
	public Status getStatus() {
        // did an error occur
        if (msg != null)
            return Status.BROKEN;
        
		// looking if dir is valid Neostore 
        if (!getValidDir())
            return Status.NOTCONNECTED;
        return Status.CONNECTED;
    }
	
	
	private boolean getValidDir()
	{
		 File neoLocationFile=new File(url.toString());
		 if(neoLocationFile.isDirectory())
		 {
			if(neoLocationFile.list().length>0)
			{
				for(int j=0;j<neoLocationFile.list().length;j++)
				{
					if(neoLocationFile.list()[j].equals("neostore.nodestore"))
					{
						//here should be loaded data from Neo database
						
						return true;
					}
				}
			} 
		 }
		return false;
	}
	
}
