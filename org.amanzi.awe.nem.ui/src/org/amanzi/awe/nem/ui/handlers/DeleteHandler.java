package org.amanzi.awe.nem.ui.handlers;

import java.util.Iterator;

import org.amanzi.awe.nem.NetworkElementManager;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
                                NetworkElementManager.getInstance().removeModel((INetworkModel)treeItem.getChild());
                                AWEEventManager.getManager().fireDataUpdatedEvent(null);
                            } else if (treeItem.getChild() instanceof IDataElement) {
                                NetworkElementManager.getInstance().removeElement((INetworkModel)treeItem.getParent(),
                                        (IDataElement)treeItem.getChild());
                                AWEEventManager.getManager().fireDataUpdatedEvent(null);
                            }
                        }
                    }
                }
            } catch (ModelException e) {
                throw new ExecutionException("can't execute action ", e);
            }
        }
        return null;
    }
}
