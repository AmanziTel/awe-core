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

import org.amanzi.awe.catalog.neo.NeoServiceInfo;
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

	NewGeoService() {
		// TODO:
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
	public List<? extends IGeoResource> resources(IProgressMonitor monitor)
			throws IOException {
		if (members == null) {
			synchronized (this) {
				List<IGeoResource> result = new ArrayList<IGeoResource>();
				try {
					for (IRenderableModel model : ProjectModel
							.getCurrentProjectModel().getAllRenderableModels()) {
						result.add(new NewGeoResource(this, model));
					}
				} catch (AWEException e) {
					LOGGER.error("Could not create a list of resources.", e);
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
		// TODO Auto-generated method stub
		return null;
	}

}
