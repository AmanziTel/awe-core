package org.amanzi.awe.wizards.geoptima;

import org.amanzi.awe.gsm.GSMCorrelator;
import org.amanzi.awe.wizards.pages.SelectCorrelationDataPage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.Node;

public class GeoptimaWizard extends Wizard implements INewWizard, IWizard {
	
	private SelectCorrelationDataPage page;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPages() {
		page = new SelectCorrelationDataPage("Select");
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		final Node network = page.getNetworkGISNode();
		final Node gps = page.getGPSGisNode();
		final Node oss = page.getOSSNode();
		final Node gpeh = page.getGPEHGisNode();
		Job job = new Job("Correlation") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				GSMCorrelator correlator = new GSMCorrelator(network);
				
				correlator.correlate(gps, oss, gpeh);
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		try {
			job.join();
		}
		catch (Exception e) {
			
		}
		
		return true;
	}

	
}
