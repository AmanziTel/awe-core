package org.amanzi.awe.catalog.neo.actions;

import java.io.IOException;
import java.net.URL;


import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;

public class NeoGeoResource extends IGeoResource
{

	    private URL identifierUrl;
	    private NeoService service;
	
	
	
	public NeoGeoResource(NeoService service) {
		super();
		// TODO Auto-generated constructor stub
		 this.service = service;
		 URL serviceUrl = service.getIdentifier();
	}

	@Override
	public URL getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGeoResourceInfo getInfo(IProgressMonitor monitor)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IService service(IProgressMonitor monitor) throws IOException {
		// TODO Auto-generated method stub
		return service;
	}

	@Override
	public Throwable getMessage() {
		// TODO Auto-generated method stub
		return service.getMessage();
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return service.getStatus();
	}

}
