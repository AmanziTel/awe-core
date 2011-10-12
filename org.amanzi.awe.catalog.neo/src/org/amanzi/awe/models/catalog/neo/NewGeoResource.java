package org.amanzi.awe.models.catalog.neo;

import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.eclipse.core.runtime.IProgressMonitor;

public class NewGeoResource extends IGeoResource {

	private IRenderableModel source;
	private IService service;
	private IGeoResourceInfo resInfo;

	NewGeoResource(IService service, IRenderableModel source) {
		// validate
		if (service == null) {
			throw new IllegalArgumentException("Geo service is null.");
		}
		if (source == null) {
			throw new IllegalArgumentException("Source is null.");
		}

		this.source = source;
		this.service = service;
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
	public IGeoResourceInfo getInfo(IProgressMonitor monitor)
			throws IOException {
		if (resInfo == null) {
			synchronized (this) {
				resInfo = new NewGeoResourceInfo();
			}
		}
		return resInfo;
	}

	@Override
	public IService service(IProgressMonitor monitor) throws IOException {
		return service;
	}

	@Override
	public URL getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> boolean canResolve(Class<T> adaptee) {
		return adaptee.isAssignableFrom(INetworkModel.class)
				|| adaptee.isAssignableFrom(IRenderableModel.class)
				|| super.canResolve(adaptee);
	}

	@Override
	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor)
			throws IOException {
		if (adaptee.isAssignableFrom(INetworkModel.class)) {
			return adaptee.cast(source);
		} else if (adaptee.isAssignableFrom(IRenderableModel.class)) {
			return adaptee.cast(source);
		}
		return super.resolve(adaptee, monitor);
	}

}
