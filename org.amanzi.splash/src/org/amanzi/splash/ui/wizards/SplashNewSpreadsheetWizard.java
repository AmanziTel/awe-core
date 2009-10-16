/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.splash.ui.wizards;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.ui.*;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "jrss". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class SplashNewSpreadsheetWizard extends NewRubyElementCreationWizard implements INewWizard {
	private SplashNewSpreadsheetWizardPage page;
	
	/**
	 * Constructor for SplashNewSpreadsheetWizard.
	 */
	public SplashNewSpreadsheetWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
	    //Lagutko, 22.07.2009, use selection from parent class
		NeoSplashUtil.logn("selection: " + getSelection().toString());
		page = new SplashNewSpreadsheetWizardPage(getSelection());
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		
		NeoSplashUtil.logn("containerName: " + containerName);
		
		final String fileName = page.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,
		final String fileName,
		final IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}		
		monitor.worked(1);
		
		monitor.setTaskName("Opening spreadsheet for editing...");
		
		//Lagutko 20.07.2009, create Spreadsheet also for EMF structure  
		try {
		    final URL spreadsheetURL = NeoSplashUtil.getSpeadsheetURL(fileName);
		    getShell().getDisplay().asyncExec(new Runnable() {
		        public void run() {AWEProjectManager.createNeoSpreadsheet(resource.getProject(), fileName, spreadsheetURL);
				
				    NeoSplashUtil.openSpreadsheet(PlatformUI.getWorkbench(), spreadsheetURL, resource.getProject().getName());
				
				    //Lagutko, 11.08.2009, put newly created Spreadsheet to Delta
				    CreateSpreadsheetOperation op= new CreateSpreadsheetOperation((IProject)resource, fileName);
				    try {
				        op.runOperation(monitor);
				    }
				    catch (RubyModelException e) {
				        SplashPlugin.error(null, e);
				    }				
		        }
		    });
		}
		catch (MalformedURLException e) {
		    throw new CoreException(new Status(Status.ERROR, SplashPlugin.getId(), null, e));
		}
		monitor.worked(1);
	}
	
	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "org.amanzi.splash", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	    //Lagutko, 22.07.2009, call init method from parent class to simplify Spreadsheet creation
		super.init(workbench, selection);
	}

	@Override
    protected void finishPage(IProgressMonitor monitor) {
        //do nothing        
    }

    @Override
    public IRubyElement getCreatedElement() {
        //do nothing
        return null;
    }
}
