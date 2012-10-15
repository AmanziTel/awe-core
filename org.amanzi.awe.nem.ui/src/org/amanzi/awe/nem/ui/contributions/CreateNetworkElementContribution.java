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
import org.amanzi.awe.ui.dto.IUIItemNew;
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
    public void fill(final Menu menu, final int index) {
        final IUIItemNew item = getSelectedItem();

        if (item == null) {
            return;
        }

        final INetworkModel model = MenuUtils.getModelFromItem(item);
        final IDataElement element = MenuUtils.getElementFromItem(item);
        final INodeType type = MenuUtils.getType(model, element);

        final Collection<INodeType> types = NetworkStructureManager.getInstance().getUnderlineElements(type,
                Arrays.asList(model.getNetworkStructure()));
        if (LOGGER.isDebugEnabled()) {
            log("Underline types " + Arrays.toString(types.toArray()), LoggerStatus.INFO);
        }
        for (final INodeType newType : types) {
            final MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
            menuItem.setText(newType.getId());
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    openWizard(model, element, newType);
                }
            });
        }

    }

    /**
     * @param model
     * @param newType
     */
    protected void openWizard(final INetworkModel model, final IDataElement root, final INodeType newType) {
        final PropertyCreationWizard wizard = new PropertyCreationWizard(model, root, newType);
        final Dialog wizardDialog = createDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), wizard);
        wizardDialog.create();
        wizardDialog.open();

    }

    /**
     * @param activeWorkbenchWindow
     * @param wizard
     * @return
     */
    private Dialog createDialog(final IWorkbenchWindow activeWorkbenchWindow, final NetworkCreationWizard wizard) {
        return new WizardDialog(activeWorkbenchWindow.getShell(), wizard);
    }

    /**
     * @return
     */
    private IUIItemNew getSelectedItem() {
        if (LOGGER.isDebugEnabled()) {
            log("geting selected tree item", LoggerStatus.INFO);
        }

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if (window == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("Active window is null");
            }
            return null;
        }
        final IStructuredSelection selection = (IStructuredSelection)window.getSelectionService().getSelection();
        if (selection == null) {
            if (LOGGER.isDebugEnabled()) {
                log("No selection data", LoggerStatus.ERROR);
            }
            return null;
        }
        final Object firstElement = selection.getFirstElement();
        IUIItemNew item = null;
        if (firstElement instanceof IUIItemNew) {
            item = (IUIItemNew)firstElement;
            item = MenuUtils.getModelFromItem(item) == null ? null : item;
        }
        if (LOGGER.isDebugEnabled()) {
            log("found ITreeItem " + item, LoggerStatus.INFO);
        }
        return item;
    }

    private void log(final String message, final LoggerStatus status) {
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
    }
}
