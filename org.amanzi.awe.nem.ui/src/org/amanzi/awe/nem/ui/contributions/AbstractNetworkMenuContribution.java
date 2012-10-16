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
import org.amanzi.awe.views.treeview.provider.ITreeItem;
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

    protected enum LoggerStatus {
        WARN, INFO, ERROR;
    }

    /**
     * @param model
     * @param newType
     */
    protected void openWizard(INetworkModel model, IDataElement root, INodeType newType) {
        IWizard wizard = getWizard(model, root, newType);
        Dialog wizardDialog = createDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), wizard);
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
    protected Dialog createDialog(IWorkbenchWindow activeWorkbenchWindow, IWizard wizard) {
        return new WizardDialog(activeWorkbenchWindow.getShell(), wizard);
    }

    /**
     * @return
     */
    protected ITreeItem< ? , ? > getSelectedItem(Logger logger) {
        if (logger.isDebugEnabled()) {
            log(logger, "geting selected tree item", LoggerStatus.INFO);
        }

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if (window == null) {
            if (logger.isDebugEnabled()) {
                log(logger, "Active window is null", LoggerStatus.WARN);
            }
            return null;
        }
        IStructuredSelection selection = (IStructuredSelection)window.getSelectionService().getSelection();
        if (selection == null) {
            if (logger.isDebugEnabled()) {
                log(logger, "No selection data", LoggerStatus.ERROR);
            }
            return null;
        }
        Object firstElement = selection.getFirstElement();
        ITreeItem< ? , ? > item = null;
        if (firstElement instanceof ITreeItem< ? , ? >) {
            item = (ITreeItem< ? , ? >)firstElement;
            item = MenuUtils.getModelFromTreeItem(item) == null ? null : item;
        }
        if (logger.isDebugEnabled()) {
            log(logger, "found ITreeItem " + item, LoggerStatus.INFO);
        }
        return item;
    }

    protected void log(Logger logger, String message, LoggerStatus status) {
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
