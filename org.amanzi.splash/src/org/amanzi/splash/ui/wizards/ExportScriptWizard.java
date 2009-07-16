package org.amanzi.splash.ui.wizards;

import org.amanzi.splash.swing.Cell;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.wizards.NewFileCreationWizard;

/**
 * Wizard for exporting script that is base on New Ruby Script creation
 * 
 * @author Lagutko_N
 *
 */

public class ExportScriptWizard extends NewFileCreationWizard {
	
	//Cell for export
	private Cell cell;
	
	//LN, 16.07.2009, we need to add selection to init method
	public ExportScriptWizard(Cell cell, IStructuredSelection selection) {
		super();
		this.cell = cell;
		init(PlatformUI.getWorkbench(), selection);
	}
	
	

	public void addPages() {		
		if (fPage == null) {
			fPage= new ExportScriptWizardPage(cell);	
			fPage.init(getSelection());
			
		}
		addPage(fPage);
	}
	
	public boolean performFinish() {
		//add ScriptURI to exported script
		cell.setScriptURI(((ExportScriptWizardPage)super.getPages()[0]).getModifiedResource().getLocationURI());
		return super.performFinish();
	}
	
	
	
}
