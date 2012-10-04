package org.amanzi.awe.nem.ui.testers;

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.IStructuredSelection;

public class NemTester extends PropertyTester {

    @SuppressWarnings("rawtypes")
    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        ITreeItem item;
        IStructuredSelection selection;
        if (receiver instanceof IStructuredSelection) {
            selection = (IStructuredSelection)receiver;

        } else {
            return false;
        }

        if (selection.getFirstElement() instanceof ITreeItem) {
            item = (ITreeItem)selection.getFirstElement();
        } else {
            return false;
        }
        if (property.equals("isDeletable")) {
            return chechDeletable(item);
        }
        return false;
    }

    /**
     * @param item
     */
    @SuppressWarnings("rawtypes")
    private boolean chechDeletable(ITreeItem item) {
        if (item.getChild() instanceof INetworkModel || item.getParent() instanceof INetworkModel) {
            return true;
        }

        return false;
    }
}
