package org.amanzi.awe.nem.ui.testers;

import org.amanzi.awe.nem.ui.utils.MenuUtils;
import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.IStructuredSelection;

public class NEMTester extends PropertyTester {

    @Override
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
        IUIItem item;
        IStructuredSelection selection;
        if (receiver instanceof IStructuredSelection) {
            selection = (IStructuredSelection)receiver;

        } else {
            return false;
        }

        if (selection.getFirstElement() instanceof IUIItem) {
            item = (IUIItem)selection.getFirstElement();
        } else {
            return false;
        }

        final INetworkModel model = MenuUtils.getModelFromItem(item);
        final IDataElement elment = MenuUtils.getElementFromItem(item);
        final MenuProperties creatable = MenuProperties.findByName(property);
        if (creatable != null && model != null) {
            return creatable.check(model, elment);
        }

        return false;
    }

}
