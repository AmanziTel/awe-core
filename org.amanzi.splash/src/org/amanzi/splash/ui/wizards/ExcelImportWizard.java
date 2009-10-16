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
package org.amanzi.splash.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.amanzi.splash.database.services.Messages;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.amanzi.splash.views.importbuilder.ExcelImporter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

public class ExcelImportWizard extends NewRubyElementCreationWizard implements IImportWizard {
	
	ExcelImportWizardPage mainPage;

	public ExcelImportWizard() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		ExcelImporter importer = null;
		try {
		    importer = new ExcelImporter(mainPage.getContainerFullPath(), 
                    mainPage.getFileName(),
                    mainPage.getInitialContents(),
                    mainPage.getFileSize());
			getContainer().run(true, false, importer);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), Messages.Wizard_Error_Message, realException.getMessage());
			return false;
		}
		finally {
		    NeoSplashUtil.openSpreadsheet(PlatformUI.getWorkbench(), importer.getSpreadsheet());
		}
		return true;
	}
	 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	    super.init(workbench, selection);
		setWindowTitle(Messages.File_Import_Wizard_Title); //NON-NLS-1
		setNeedsProgressMonitor(true);
		mainPage = new ExcelImportWizardPage(Messages.Excel_Import_Title, selection); //NON-NLS-1
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(mainPage);        
    }

    @Override
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        //no need
    }

    @Override
    public IRubyElement getCreatedElement() {
        //no need
        return null;
    }

}
