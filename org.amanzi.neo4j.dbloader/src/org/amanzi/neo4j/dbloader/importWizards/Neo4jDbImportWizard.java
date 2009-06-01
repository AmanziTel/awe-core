/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.amanzi.neo4j.dbloader.importWizards;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.neo4j.apps.imdb.parser.InjectImdbData;
import org.neo4j.neoclipse.preference.NeoPreferences;

public class Neo4jDbImportWizard extends Wizard implements IImportWizard {

	Neo4jDbImportWizardPage mainPage;

	public Neo4jDbImportWizard() {
		super();
	}

//	public static void main( String[] args )
//	{
//		InjectImdbData iid = new InjectImdbData();
//		iid.create_db("/home/amabdelsalam/Desktop/test-movies.list", "/home/amabdelsalam/Desktop/db");
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		InjectImdbData iid = new InjectImdbData();
		//iid.create_db();
		System.out.println("mainPage.filename : " + mainPage.filename);

		String folder = mainPage.getContainerFullPath().toString().replace("/","");
		System.out.println("folder : " + folder);

		String newFile = mainPage.getFileName();
		System.out.println("newFile : " + newFile);

		String newFileExt = ".list";//mainPage.getFileExtension();
		System.out.println("newFileExt : " + newFileExt);

		String fileWithoutExt = newFile.replaceAll(newFileExt, "");
		System.out.println("fileWithoutExt : " + fileWithoutExt);

		String newFolder = folder + "/" + fileWithoutExt;
		System.out.println("newFolder : " + newFolder);

		System.out.println("mainPage.filename : " + mainPage.filename);

		IPreferenceStore pref = org.neo4j.neoclipse.Activator.getDefault().getPreferenceStore();
		//pref.setDefault( NeoPreferences.DATABASE_LOCATION, "" );
		String defaultDBLocation = pref.getString(NeoPreferences.DATABASE_LOCATION);


		System.out.println("defaultDBLocation: "+defaultDBLocation); 

		//iid.create_db(mainPage.filename, defaultDBLocation);
		
		try {
			openPerspective("org.neo4j.neoclipse.NeoPerspective",
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		

		if (file == null)
			return false;
		return true;
	}

	/**
	 * Opens the specified perspective in a new window.
	 *
	 * @param perspectiveId
	 * The perspective to open; must not be null
	 * @throws ExecutionException
	 * If the perspective could not be opened.
	 */
	private void openNewWindowPerspective(String perspectiveId,
			IWorkbenchWindow activeWorkbenchWindow) throws ExecutionException {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			IAdaptable input = ((Workbench) workbench).getDefaultPageInput();
			workbench.openWorkbenchWindow(perspectiveId, input);
		} catch (WorkbenchException e) {
			ErrorDialog.openError(activeWorkbenchWindow.getShell(),
					WorkbenchMessages.ChangeToPerspectiveMenu_errorTitle, e
					.getMessage(), e.getStatus());
		}
	} 

	private final void openPerspective(final String perspectiveId,
			final IWorkbenchWindow activeWorkbenchWindow)
	throws ExecutionException {
		final IWorkbench workbench = PlatformUI.getWorkbench();

		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		IPerspectiveDescriptor desc = activeWorkbenchWindow.getWorkbench()
		.getPerspectiveRegistry().findPerspectiveWithId(perspectiveId);
		if (desc == null) {
			throw new ExecutionException("Perspective " + perspectiveId //$NON-NLS-1$
					+ " cannot be found."); //$NON-NLS-1$
		}

		try {
			if (activePage != null) {
				activePage.setPerspective(desc);
			} else {
				IAdaptable input = ((Workbench) workbench)
				.getDefaultPageInput();
				activeWorkbenchWindow.openPage(perspectiveId, input);
			}
		} catch (WorkbenchException e) {
			throw new ExecutionException("Perspective could not be opened.", e); //$NON-NLS-1$
		}
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("File Import Wizard"); //NON-NLS-1
		setNeedsProgressMonitor(true);
		mainPage = new Neo4jDbImportWizardPage("Import Neo4j Database",selection); //NON-NLS-1
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages(); 
		addPage(mainPage);        
	}

}
