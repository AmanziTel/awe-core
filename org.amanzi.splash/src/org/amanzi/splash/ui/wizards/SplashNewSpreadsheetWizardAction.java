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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.rubypeople.rdt.ui.actions.AbstractOpenWizardAction;
/**
 * Action for launching SplashNewSpreadsheetWizard
 * @author Pechko_E
 * 
 */
public class SplashNewSpreadsheetWizardAction extends AbstractOpenWizardAction
		implements IViewActionDelegate,IWorkbenchWindowActionDelegate {

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
		//setShell(view.getShell());
		
	}


    @Override
    public void init(IWorkbenchWindow window) {
    }
}
