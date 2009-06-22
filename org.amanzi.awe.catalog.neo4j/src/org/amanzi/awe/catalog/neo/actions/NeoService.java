package org.amanzi.awe.catalog.neo.actions;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.IServiceInfo;

public class NeoService extends IService
{
	URL urlIndentifier;
	public NeoService(String fileName) throws MalformedURLException
	{
		IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
		for( IService service : serviceFactory.createService( urlIndentifier ) ){
		     try {
		         // many different providers may think they can connect to this URL (example WFS, WMS, ...)
		         // but we should try connecting to be sure ...
		         IServiceInfo info = service.getInfo( null );
		         CatalogPlugin.getDefault().getLocalCatalog().add( service );
		     }
		     catch (IOException couldNotConnect ){
		     }
		}

	}

	@Override
	public Map<String, Serializable> getConnectionParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends IGeoResource> resources(IProgressMonitor monitor)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Throwable getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
