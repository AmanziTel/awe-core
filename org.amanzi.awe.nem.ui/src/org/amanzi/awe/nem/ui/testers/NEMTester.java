package org.amanzi.awe.nem.ui.testers;

import org.amanzi.awe.nem.ui.utils.MenuUtils;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.IStructuredSelection;

public class NEMTester extends PropertyTester {

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

        INetworkModel model = MenuUtils.getModelFromTreeItem(item);
        IDataElement elment = MenuUtils.getElementFromTreeItem(item);
        MenuProperties creatable = MenuProperties.findByName(property);
        if (creatable != null && model != null) {
            return creatable.check(model, elment);
        }

        return false;
    }

}
