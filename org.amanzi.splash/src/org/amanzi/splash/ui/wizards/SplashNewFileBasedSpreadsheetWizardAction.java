package org.amanzi.splash.ui.wizards;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.rubypeople.rdt.ui.actions.AbstractOpenWizardAction;
/**
 * Action for SplashNewSpreadsheetWizard (File based)
 * @author Pechko_E
 * 
 */
public class SplashNewFileBasedSpreadsheetWizardAction extends AbstractOpenWizardAction
		implements IViewActionDelegate {

	public void dispose() {

	}

	
	public void run(IAction action) {
		super.run();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}
	
	@Override
	protected INewWizard createWizard() throws CoreException {
		return new SplashNewSpreadsheetWizard();
	}

	public void init(IViewPart view) {
			
	}
}
