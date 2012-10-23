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

package org.amanzi.awe.views.properties.handler;

import org.amanzi.awe.ui.util.ActionUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ShowPropertiesHandler extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        if (selection instanceof IStructuredSelection) {
            final IStructuredSelection structuredSelection = (IStructuredSelection)selection;

            if (!structuredSelection.isEmpty()) {
                if (structuredSelection.size() == 1) {
                    runViewActivationTask(IPageLayout.ID_PROP_SHEET);
                }
            }
        }

        return null;
    }

    private void runViewActivationTask(final String viewId) {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                runViewActivation(viewId);

            }
        }, false);
    }

    private void runViewActivation(final String viewId) {
        try {
            final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            final IViewPart viewPart = activePage.findView(viewId);

            if (!activePage.isPartVisible(viewPart)) {
                activePage.showView(viewId);
            }
        } catch (final PartInitException e) {
            MessageDialog.openError(Display.getDefault().getActiveShell(), "Cannot open Property Sheet", e.getLocalizedMessage());
        }
    }
}
