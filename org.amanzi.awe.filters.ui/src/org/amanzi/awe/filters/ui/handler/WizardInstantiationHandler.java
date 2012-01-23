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

package org.amanzi.awe.filters.ui.handler;

import org.amanzi.awe.filters.ui.wizards.FilterManagerWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * instantiate wizard
 * 
 * @author Vladislav_Kondratenko
 */
public class WizardInstantiationHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IImportWizard wizard = getWizardInstance(event);
        wizard.init(workbenchWindow.getWorkbench(), null);
        Shell parent = workbenchWindow.getShell();
        WizardDialog dialog = new WizardDialog(parent, wizard);
        dialog.create();
        dialog.open();
        return null;
    }

    /**
     * return wizard instance
     * 
     * @param event
     * @return
     */
    protected IImportWizard getWizardInstance(ExecutionEvent event) {
        return new FilterManagerWizard();
    }
}
