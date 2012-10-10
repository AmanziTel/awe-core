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

import java.util.Arrays;
import java.util.Collection;

import org.amanzi.awe.nem.managers.structure.NetworkStructureManager;
import org.amanzi.awe.nem.ui.utils.MenuUtils;
import org.amanzi.awe.nem.ui.wizard.NetworkCreationWizard;
import org.amanzi.awe.nem.ui.wizard.PropertyCreationWizard;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
public class CreateNetworkElementContribution extends ContributionItem {

    private static final Logger LOGGER = Logger.getLogger(CreateNetworkElementContribution.class);

    private enum LoggerStatus {
        WARN, INFO, ERROR;
    }

    @Override
    public void fill(Menu menu, int index) {
        ITreeItem< ? , ? > item = getSelectedItem();

        if (item == null) {
            return;
        }

        final INetworkModel model = MenuUtils.getInstance().getModelFromTreeItem(item);
        IDataElement element = MenuUtils.getInstance().getElementFromTreeItem(item);
        INodeType type = MenuUtils.getInstance().getType(model, element);

        Collection<INodeType> types = NetworkStructureManager.getInstance().getUnderlineElements(type,
                Arrays.asList(model.getNetworkStructure()));

        log("Underline types " + Arrays.toString(types.toArray()), LoggerStatus.INFO);

        for (final INodeType newType : types) {
            MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
            menuItem.setText(newType.getId());
            menuItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    openWizard(model, newType);
                }
            });
        }

    }

    /**
     * @param model
     * @param newType
     */
    protected void openWizard(INetworkModel model, INodeType newType) {
        PropertyCreationWizard wizard = new PropertyCreationWizard(model, newType);
        Dialog wizardDialog = createDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), wizard);
        wizardDialog.create();
        wizardDialog.open();

    }

    /**
     * @param activeWorkbenchWindow
     * @param wizard
     * @return
     */
    private Dialog createDialog(IWorkbenchWindow activeWorkbenchWindow, NetworkCreationWizard wizard) {
        return new WizardDialog(activeWorkbenchWindow.getShell(), wizard);
    }

    /**
     * @return
     */
    private ITreeItem< ? , ? > getSelectedItem() {
        log("geting selected tree item", LoggerStatus.INFO);

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if (window == null) {
            LOGGER.warn("Active window is null");
            return null;
        }
        IStructuredSelection selection = (IStructuredSelection)window.getSelectionService().getSelection();
        if (selection == null) {
            log("No selection data", LoggerStatus.WARN);
            return null;
        }
        Object firstElement = selection.getFirstElement();
        ITreeItem< ? , ? > item = null;
        if (firstElement instanceof ITreeItem< ? , ? >) {
            item = (ITreeItem< ? , ? >)firstElement;
            item = MenuUtils.getInstance().getModelFromTreeItem(item) == null ? null : item;
        }
        log("found ITreeItem parent:" + item.getParent() + " child: " + item.getChild(), LoggerStatus.INFO);
        return item;
    }

    private void log(String message, LoggerStatus status) {
        if (LOGGER.isDebugEnabled()) {
            switch (status) {
            case WARN:
                LOGGER.warn(message);
                break;
            case INFO:
                LOGGER.info(message);
                break;
            case ERROR:
                LOGGER.error(message);
                break;
            default:
                break;
            }
        } else {
            if (status == LoggerStatus.ERROR) {
                LOGGER.error(message);
            }
        }
    }
}
