package org.amanzi.awe.afp.loaders;

import java.io.IOException;

import org.amanzi.awe.afp.exporters.AfpExporter;
import org.amanzi.neo.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.neo4j.graphdb.Node;



public class AfpOutputFileLoaderJob extends Job {
	
	private AfpOutputFileLoader loader;

	public AfpOutputFileLoaderJob(String name, Node networkRoot, Node afpDataset, AfpExporter exporter) {
		super(name);
		loader = new AfpOutputFileLoader(networkRoot, afpDataset, exporter);
		
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		try {
			loader.run(monitor);
            NeoServicesUiPlugin.getDefault().getUpdateViewManager()
                    .fireUpdateView(new UpdateDatabaseEvent(UpdateViewEventType.GIS));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

}
