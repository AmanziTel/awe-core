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

import org.amanzi.neo.loader.dialogs.DriveDialog;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Delegates drive loading to DriveDialog
 * 
 * @see org.amanzi.neo.loader.dialogs.DriveDialog
 * @author Pechko Elena
 * @since 1.0.0
 */
public class NewDriveAction implements IWorkbenchWindowActionDelegate {
    private IWorkbenchWindow window;
    @Override
    public void dispose() {
    }

    @Override
    public void init(IWorkbenchWindow window) {
        this.window=window;
    }

    @Override
    public void run(IAction action) {
        DriveDialog dialog = new DriveDialog(window.getShell());
        dialog.open();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

}
