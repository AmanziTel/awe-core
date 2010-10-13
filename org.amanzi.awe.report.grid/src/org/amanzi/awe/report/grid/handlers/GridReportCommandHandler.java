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

package org.amanzi.awe.report.grid.handlers;

import org.amanzi.awe.report.grid.wizards.GridReportWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public class GridReportCommandHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        GridReportWizard wizard = new GridReportWizard();
        wizard.setWindowTitle(GridReportWizard.WIZARD_TITLE);
        WizardDialog wizardDialog = new WizardDialog(window.getShell(), wizard);
        wizardDialog.open();
        return null;
    }

}
