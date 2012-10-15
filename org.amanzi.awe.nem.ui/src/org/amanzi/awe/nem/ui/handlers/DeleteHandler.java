package org.amanzi.awe.nem.ui.handlers;

import java.text.MessageFormat;
import java.util.Iterator;

import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.ui.messages.NemMessages;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
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

public class DeleteHandler extends AbstractHandler {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        if (selection instanceof IStructuredSelection) {
            Iterator<Object> selectionIterator = ((IStructuredSelection)selection).iterator();
            try {
                while (selectionIterator.hasNext()) {
                    Object selectedObject = selectionIterator.next();
                    if (selectedObject instanceof ITreeItem) {
                        ITreeItem treeItem = (ITreeItem)selectedObject;
                        if (treeItem.getParent() instanceof INetworkModel || treeItem.getChild() instanceof INetworkModel) {
                            if (treeItem.getParent() == null) {
                                INetworkModel model = (INetworkModel)treeItem.getChild();
                                if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                        NemMessages.REMOVE_DIALOG_TITLE,
                                        MessageFormat.format(NemMessages.REMOVE_MODEL_CONFIRMATION_TEXT, model.getName()))) {
                                    NetworkElementManager.getInstance().removeModel(model);
                                }
                            } else if (treeItem.getChild() instanceof IDataElement) {
                                INetworkModel model = (INetworkModel)treeItem.getParent();
                                IDataElement element = (IDataElement)treeItem.getChild();
                                if (MessageDialog.openConfirm(
                                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                        NemMessages.REMOVE_DIALOG_TITLE,
                                        MessageFormat.format(NemMessages.REMOVE_ELEMENT_CONFIRMATION_TEXT, element.getName(),
                                                model.getName()))) {
                                    NetworkElementManager.getInstance().removeElement(model, element);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new ExecutionException("can't execute action ", e);
            }
        }
        return null;
    }
}
