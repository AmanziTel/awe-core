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

package org.amanzi.awe.nem.ui.handlers;

import org.amanzi.awe.nem.ui.wizard.NetworkCreationWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CreateNetworkHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        NetworkCreationWizard wizard = new NetworkCreationWizard();
        Dialog wizardDialog = createDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), wizard);
        wizardDialog.create();
        wizardDialog.open();
        return null;
    }

    /**
     * @param activeWorkbenchWindow
     * @param wizard
     * @return
     */
    private Dialog createDialog(IWorkbenchWindow activeWorkbenchWindow, NetworkCreationWizard wizard) {
        return new WizardDialog(activeWorkbenchWindow.getShell(), wizard);
    }
}
