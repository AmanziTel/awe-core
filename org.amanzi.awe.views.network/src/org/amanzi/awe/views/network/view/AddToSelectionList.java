package org.amanzi.awe.views.network.view;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

public class AddToSelectionList implements IViewActionDelegate {

    @Override
    public void run(IAction action) {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        NewSelectionListDialog pdialog = new NewSelectionListDialog(shell, "New selection list", SWT.OK);
        if (pdialog.open() == SWT.OK) {

        }else{

        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    @Override
    public void init(IViewPart view) {
    }

}
