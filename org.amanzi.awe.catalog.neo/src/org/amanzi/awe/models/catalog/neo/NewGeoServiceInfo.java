package org.amanzi.awe.models.catalog.neo;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

public class NewGeoServiceInfo extends IServiceInfo {
	private IService service;

	public NewGeoServiceInfo(IService service) {
		this.service = service;
		// TODO: set some params
	}
}
