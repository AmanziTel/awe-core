package org.amanzi.splash.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;

import org.amanzi.splash.swing.SplashTable;
import org.amanzi.splash.swing.SplashTableModel;
import org.amanzi.splash.ui.AbstractSplashEditor;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class NeoDataFileImportWizard extends Wizard implements IImportWizard {

	NeoDataImportWizardPage mainPage;

	public NeoDataFileImportWizard() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		final IFile file = mainPage.createNewFile();

		final String containerName = "project.AWEScript";

		NeoSplashUtil.logn("containerName: " + containerName);

		final String fileName = mainPage.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor) throws InvocationTargetException {
				try {
					getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							AbstractSplashEditor editor = (AbstractSplashEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

							SplashTable table = editor.getTable();

							SplashTableModel model = (SplashTableModel) table.getModel();

							monitor.beginTask("Loading ", 100);

							IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

							monitor.worked(1);

							monitor.setTaskName("Loading records from file...");

							NeoSplashUtil.LoadFileIntoSpreadsheet(file.getLocation().toString(), model, monitor);		
						}
					});


					//doFinish(file, monitor);
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

	private long getFileLength(IFile file){
		String path = file.getLocation().toString();
		return (new File(path)).length();
	}

	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
			final IFile file,
			final IProgressMonitor monitor)
	throws CoreException {


		// create a sample file





		monitor.worked(1);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Splash Import Wizard"); //NON-NLS-1
		setNeedsProgressMonitor(true);
		mainPage = new NeoDataImportWizardPage("Import Data File as Amanzi Splash Sheet", selection); //NON-NLS-1
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages(); 
		addPage(mainPage);        
	}

}
