package org.amanzi.awe.nem.ui.testers;

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.nodetypes.INodeType;
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
        if (!checkForNetworkElement(item)) {
            return false;
        }
        INetworkModel model = getModel(item);
        IDataElement elment = getElement(item);
        if (property.equals("isAddetable")) {
            return model != null;
        }
        CreatableParameters creatable = CreatableParameters.findByName(property);
        if (creatable != null) {
            return checkAddetable(model, elment, creatable.getType());
        }

        return false;
    }

    /**
     * @param model
     * @param element
     * @return
     */
    private boolean checkAddetable(INetworkModel model, IDataElement element, NetworkElementType expectedType) {
        INodeType type;
        if (element == null) {
            type = model.getType();
        } else {
            type = element.getNodeType();
        }
        return model.getNetworkStructure().isInUderline(type, expectedType);
    }

    @SuppressWarnings("rawtypes")
    private boolean checkForNetworkElement(ITreeItem item) {
        return getModel(item) != null ? true : false;
    }

    @SuppressWarnings("rawtypes")
    private INetworkModel getModel(ITreeItem item) {
        if (item.getParent() == null && item.getChild() instanceof INetworkModel) {
            return (INetworkModel)item.getChild();
        } else if (item.getParent() instanceof INetworkModel && item.getChild() instanceof IDataElement) {
            return (INetworkModel)item.getParent();
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private IDataElement getElement(ITreeItem item) {
        if (item.getParent() != null && item.getChild() != null) {
            return (IDataElement)item.getChild();
        }
        return null;
    }
}
