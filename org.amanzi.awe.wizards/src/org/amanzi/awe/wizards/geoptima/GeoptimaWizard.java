package org.amanzi.awe.wizards.geoptima;

import java.lang.reflect.InvocationTargetException;

import org.amanzi.awe.gps.GPSCorrelator;
import org.amanzi.awe.wizards.pages.SelectCorrelationDataPage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
		
		try {
		    getContainer().run(true, false, new IRunnableWithProgress() {
            
		        @Override
		        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		            GPSCorrelator correlator = new GPSCorrelator(network, monitor);
	                
	                correlator.correlate(gps, oss, gpeh);
		        }
		    });
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

	
}
