package org.amanzi.awe.catalog.neo.actions;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IGeoResourceInfo;

public class NeoGeoResourceInfo extends IGeoResourceInfo
{
          private NeoGeoResource neoGeoResource;
	public NeoGeoResourceInfo(NeoGeoResource neoGeoResource,
			IProgressMonitor monitor) {
		// TODO Auto-generated constructor stub
		this.neoGeoResource=neoGeoResource;
	}
	

}
