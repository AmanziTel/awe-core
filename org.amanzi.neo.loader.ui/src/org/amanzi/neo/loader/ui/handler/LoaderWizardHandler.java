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

package org.amanzi.neo.loader.ui.handler;

import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.amanzi.neo.loader.ui.wizard.impl.internal.LoaderContext;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class LoaderWizardHandler extends AbstractHandler {

    protected static final String LOADER_WIZARD_ID = "org.amanzi.loader.wizard.id"; //$NON-NLS-1$

    private final LoaderContext loaderContext;

    public LoaderWizardHandler() {
        this(LoaderContext.getInstance());
    }

    protected LoaderWizardHandler(final LoaderContext loaderContext) {
        this.loaderContext = loaderContext;
    }

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        String wizardId = event.getParameter(LOADER_WIZARD_ID);
        if (wizardId != null) {
            ILoaderWizard wizard = loaderContext.getLoaderWizard(wizardId);

            if (wizard != null) {
                IWorkbenchWindow window = getWorkbenchWindow(event);
                IWorkbench workbench = window.getWorkbench();

                wizard.init(workbench, null);

                Dialog wizardDialog = createDialog(window.getShell(), wizard);
                wizardDialog.create();
                wizardDialog.open();
            } else {
                throw new ExecutionException(Messages.formatString(Messages.LoaderWizardHandler_NoWizardByIdError, wizardId));
            }
        } else {
            throw new ExecutionException(Messages.LoaderWizardHandler_NoWizardIdError);
        }

        return null;
    }

    protected IWorkbenchWindow getWorkbenchWindow(final ExecutionEvent event) throws ExecutionException {
        return HandlerUtil.getActiveWorkbenchWindowChecked(event);
    }

    protected Dialog createDialog(final Shell shell, final IWizard wizard) {
        return null;
    }
}
