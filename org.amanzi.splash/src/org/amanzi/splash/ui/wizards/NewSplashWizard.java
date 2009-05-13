package org.amanzi.splash.ui.wizards;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Specialization of the new file creation processing that adds
 * fields to initialize the number of rows and columns in the new
 * mini-spreadsheet.
 */
public class NewSplashWizard extends Wizard implements INewWizard {
	IStructuredSelection selection;
	SplashWizardNewFileCreationPage fileCreationPage;
	IWorkbench workbench;

	/**
	 * Constructor for NewSplashWizard.
	 */
	public NewSplashWizard() {
		super();
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		return fileCreationPage.finish();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle("New JRuby Spreadsheet");
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		fileCreationPage =
			new SplashWizardNewFileCreationPage(workbench, selection);
		addPage(fileCreationPage);
	}
}
