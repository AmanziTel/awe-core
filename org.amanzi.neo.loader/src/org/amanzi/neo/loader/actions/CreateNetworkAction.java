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

package org.amanzi.neo.loader.actions;

import org.amanzi.neo.wizards.CreateNetworkWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * <p>
 * Create Network Action
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CreateNetworkAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;
    private IStructuredSelection selection;

    public void run() {

    }

    @Override
    public void run(IAction action) {
        INewWizard wizard = new CreateNetworkWizard();
        wizard.init(window.getWorkbench(), selection);
        Shell parent = window.getShell();
        WizardDialog dialog = new WizardDialog(parent, wizard);
        dialog.create();
        dialog.open();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection instanceof IStructuredSelection?(IStructuredSelection)selection:null;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void init(IWorkbenchWindow window) {
        this.window = window;
    };

}
