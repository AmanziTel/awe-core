package org.amanzi.splash.ui.wizards;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.splash.utilities.Util;
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
import java.io.*;

import org.eclipse.ui.*;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

import com.eteks.openjeks.format.CellFormat;

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
//Lagutko, 30.06.2009, this wizard extends from NewRubyElementCreationWizard to add functionality
//for creating default AWE and Ruby Project
public class SplashNewSpreadsheetWizard extends NewRubyElementCreationWizard implements INewWizard {
	private SplashNewSpreadsheetWizardPage page;
	//Lagutko, 30.06.2009, field for selection will come from NewRubyElementCreationWizard
	//private ISelection selection;

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
		Util.logn("selection: " + getSelection().toString());
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
	protected InputStream getInitialContents() {
		int rowCount = Util.MAX_SPLASH_ROW_COUNT; //page.getRowCount();
		int columnCount = Util.MAX_SPLASH_COL_COUNT; //page.getColumnCount();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				sb.append(";" + "" + ";" + Util.getFormatString(new CellFormat()) + ";");
			}
			sb.append("\n");
		}

		return new ByteArrayInputStream(sb.toString().getBytes());
	}
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,
		final String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject resource = root.getProject(containerName);
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}		
		final IFile file = resource.getFile(new Path(fileName));
		try {
			InputStream stream = getInitialContents();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				//IDE.openEditor(page, file, Util.AMANZI_SPLASH_EDITOR);
				
				//Lagutko, 29.06.2009, create Spreadsheet also for uDIG project structure
				//Lagutko, 21.07.2009, we need URL of resource instead of IResource
				URL resourceURL = null;
				try {
				    resourceURL = file.getLocationURI().toURL();
				}
				catch (MalformedURLException e) {
				    //TODO: handle this exception
				    e.printStackTrace();
				}
				AWEProjectManager.createFileSpreadsheet(resource, fileName, resourceURL);
				
				Util.openSpreadsheet(PlatformUI.getWorkbench(), file);
			}
		});
		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
		String contents =
			"This is the initial file contents for *.jrss file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
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
	//Lagutko, 30.06.2009, selection will be initialized in super.init so we don't need this method
//	public void init(IWorkbench workbench, IStructuredSelection selection) {			
//		this.selection = selection;
//	}

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