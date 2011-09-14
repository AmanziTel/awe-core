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

package org.amanzi.neo.loader.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * <p>
 * Load Network Site handler
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class LoadNetworkSiteHandler extends AbstractHandler {
    public static String PARAM_ADD_TO_SELECT = "org.amanzi.neo.loader.commands.addtoselect";

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        return null;
    }

//    @Override
//    public Object execute(ExecutionEvent arg0) throws ExecutionException {
//        String addToSelect = arg0.getParameter(PARAM_ADD_TO_SELECT);
//        IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(arg0);
//        NetworkSiteImportWizard wizard = new NetworkSiteImportWizard();
//        wizard.addToSelectParam(addToSelect);
//        wizard.init(workbenchWindow.getWorkbench(), null);
//        Shell parent = workbenchWindow.getShell();
//        WizardDialog dialog = new WizardDialog(parent, wizard);
//        dialog.create();
//        dialog.open();
//        return null;
//    }

}
