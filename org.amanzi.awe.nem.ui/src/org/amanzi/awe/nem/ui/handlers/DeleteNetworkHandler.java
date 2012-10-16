package org.amanzi.awe.nem.ui.handlers;

import java.text.MessageFormat;
import java.util.Iterator;

import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
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

public class DeleteNetworkHandler extends AbstractHandler {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        if (selection instanceof IStructuredSelection) {
            final Iterator<Object> selectionIterator = ((IStructuredSelection)selection).iterator();
            try {
                while (selectionIterator.hasNext()) {
                    final Object selectedObject = selectionIterator.next();
                    if (selectedObject instanceof ITreeItem) {
                        final ITreeItem treeItem = (ITreeItem)selectedObject;
                        if (treeItem.getParent() instanceof INetworkModel || treeItem.getChild() instanceof INetworkModel) {
                            // TODO: LN: 16.10.2012, duplication of call for MessageDialog - can be
                            // moved to single method
                            if (treeItem.getParent() == null) {
                                final INetworkModel model = (INetworkModel)treeItem.getChild();
                                if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                        NEMMessages.REMOVE_DIALOG_TITLE,
                                        MessageFormat.format(NEMMessages.REMOVE_MODEL_CONFIRMATION_TEXT, model.getName()))) {
                                    NetworkElementManager.getInstance().removeModel(model);
                                }
                            } else if (treeItem.getChild() instanceof IDataElement) {
                                final INetworkModel model = (INetworkModel)treeItem.getParent();
                                final IDataElement element = (IDataElement)treeItem.getChild();
                                if (MessageDialog.openConfirm(
                                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                        NEMMessages.REMOVE_DIALOG_TITLE,
                                        MessageFormat.format(NEMMessages.REMOVE_ELEMENT_CONFIRMATION_TEXT, element.getName(),
                                                model.getName()))) {
                                    NetworkElementManager.getInstance().removeElement(model, element);
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
