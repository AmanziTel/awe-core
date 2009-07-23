package org.amanzi.splash.ui.neo4j.wizards;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.splash.neo4j.utilities.Util;
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
		String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
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
				
				//Lagutko 20.07.2009, create Spreadsheet also for EMF structure		
				URL spreadsheetURL = getSpreadsheetURL();
				
				AWEProjectManager.createNeoSpreadsheet(resource.getProject(), file.getName(), spreadsheetURL);
				
				Util.openSpreadsheet(PlatformUI.getWorkbench(), spreadsheetURL);
			}
		});
		monitor.worked(1);
	}
	
	/**
	 * Method that computes URL of Neo4J Spreadsheet
	 *
	 * @return url of Spreadsheet
	 * @author Lagutko_N
	 */
	
	private URL getSpreadsheetURL() {
	    //TODO: this method must return path to Neo4j database and node of created spreadsheet
	    //if it must be computed in other place than we must replace creating EMF Spreadsheet
	    
	    //it's a fake
	    try {
	        return Platform.getLocation().toFile().toURI().toURL();
	    }
	    catch (MalformedURLException e) {
	        return null;
	    }
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