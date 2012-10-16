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

package org.amanzi.awe.nem.ui.contributions;

import org.amanzi.awe.nem.ui.utils.MenuUtils;
import org.amanzi.awe.ui.dto.IUIItemNew;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
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
public abstract class AbstractNetworkMenuContribution extends ContributionItem {

    // TODO: LN: 16.10.2012, since we using Log4j why don't use Level.INFO etc. instead of new
    // entities?
    protected enum LoggerStatus {
        WARN, INFO, ERROR;
    }

    /**
     * @param model
     * @param newType
     */
    protected void openWizard(final INetworkModel model, final IDataElement root, final INodeType newType) {
        final IWizard wizard = getWizard(model, root, newType);
        final Dialog wizardDialog = createDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), wizard);
        wizardDialog.create();
        wizardDialog.open();

    }

    /**
     * @param model
     * @param root
     * @param newType
     * @return
     */
    protected abstract IWizard getWizard(INetworkModel model, IDataElement root, INodeType newType);

    /**
     * @param activeWorkbenchWindow
     * @param wizard
     * @return
     */
    protected Dialog createDialog(final IWorkbenchWindow activeWorkbenchWindow, final IWizard wizard) {
        return new WizardDialog(activeWorkbenchWindow.getShell(), wizard);
    }

    /**
     * @return
     */
    // TODO: LN: 16.10.2012, class should work with his own LOGGER but not use any provided
    protected IUIItemNew getSelectedItem(final Logger logger) {
        // TODO: LN: 16.10.2012, anyway incorrect work with loggers - some messages will be skipped
        // since it will work only on DEBUG level
        if (logger.isDebugEnabled()) {
            log(logger, "geting selected tree item", LoggerStatus.INFO);
        }

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if (window == null) {
            if (logger.isDebugEnabled()) {
                log(logger, "Active window is null", LoggerStatus.WARN);
            }
            return null;
        }
        final IStructuredSelection selection = (IStructuredSelection)window.getSelectionService().getSelection();
        if (selection == null) {
            if (logger.isDebugEnabled()) {
                log(logger, "No selection data", LoggerStatus.ERROR);
            }
            return null;
        }
        final Object firstElement = selection.getFirstElement();
        IUIItemNew item = null;
        if (firstElement instanceof IUIItemNew) {
            item = (IUIItemNew)firstElement;
            item = MenuUtils.getModelFromItem(item) == null ? null : item;
        }
        if (logger.isDebugEnabled()) {
            log(logger, "found ITreeItem " + item, LoggerStatus.INFO);
        }
        return item;
    }

    protected void log(final Logger logger, final String message, final LoggerStatus status) {
        switch (status) {
        case WARN:
            logger.warn(message);
            break;
        case INFO:
            logger.info(message);
            break;
        case ERROR:
            logger.error(message);
            break;
        default:
            break;
        }
    }
}
