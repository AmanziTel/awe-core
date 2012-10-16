package org.amanzi.awe.nem.ui.handlers;

import java.text.MessageFormat;
import java.util.Iterator;

import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.utils.MenuUtils;
import org.amanzi.awe.ui.dto.IUIItemNew;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteNetworkHandler extends AbstractHandler {

    @SuppressWarnings({"unchecked"})
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        if (selection instanceof IStructuredSelection) {
            final Iterator<Object> selectionIterator = ((IStructuredSelection)selection).iterator();
            try {
                while (selectionIterator.hasNext()) {
                    final Object selectedObject = selectionIterator.next();
                    if (selectedObject instanceof IUIItemNew) {
                        final IUIItemNew treeItem = (IUIItemNew)selectedObject;

                        final INetworkModel networkModel = MenuUtils.getModelFromItem(treeItem);
                        final IDataElement dataElement = MenuUtils.getElementFromItem(treeItem);
                        if (networkModel != null) {
                            if (dataElement == null) {
                                if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                        NEMMessages.REMOVE_DIALOG_TITLE,
                                        MessageFormat.format(NEMMessages.REMOVE_MODEL_CONFIRMATION_TEXT, networkModel.getName()))) {
                                    NetworkElementManager.getInstance().removeModel(networkModel);
                                }
                            } else {
                                if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                        NEMMessages.REMOVE_DIALOG_TITLE, MessageFormat.format(
                                                NEMMessages.REMOVE_ELEMENT_CONFIRMATION_TEXT, dataElement.getName(),
                                                networkModel.getName()))) {
                                    NetworkElementManager.getInstance().removeElement(networkModel, dataElement);
                                }
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                throw new ExecutionException("can't execute action ", e);
            }
        }
        return null;
    }
}
