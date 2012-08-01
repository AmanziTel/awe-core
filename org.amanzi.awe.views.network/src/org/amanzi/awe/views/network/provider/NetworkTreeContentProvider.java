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
package org.amanzi.awe.views.network.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.amanzi.awe.views.network.NetworkTreePluginMessages;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.awe.views.treeview.provider.impl.TreeViewItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * New content provider for NetworkTree
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class NetworkTreeContentProvider extends AbstractContentProvider<INetworkModel> {
    Iterable<IDataElement> children = null;

    @Override
    public void dispose() {

    }

    /**
     * <p>
     * Comparator for treeElements
     * </p>
     * 
     * @author Kondratenko_Vladislav
     * @since 1.0.0
     */
    public static class IDataElementComparator implements Comparator<ITreeItem<INetworkModel>> {

        @Override
        public int compare(ITreeItem<INetworkModel> dataElement1, ITreeItem<INetworkModel> dataElement2) {
            return dataElement1.getDataElement() == null ? -1 : dataElement2.getDataElement() == null ? 1 : dataElement1
                    .getDataElement().get(GENERAL_NODE_PROPERTIES.getNodeNameProperty()).toString()
                    .compareTo(dataElement2.getDataElement().get(GENERAL_NODE_PROPERTIES.getNodeNameProperty()).toString());
        }

    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        super.getElements(inputElement);
        Iterable<INetworkModel> networkModels;
        try {
            networkModels = networkModelProvider.findAll(projectModelProvider.getActiveProjectModel());
        } catch (ModelException e) {
            MessageDialog.openError(null, NetworkTreePluginMessages.ERROR_TITLE,
                    NetworkTreePluginMessages.COULD_NOT_GET_ALL_NETWORK_MODELS);
            return null;
        }

        for (INetworkModel model : networkModels) {
            addToRoot(model);
        }
        return rootList.toArray();
    }

    @Override
    protected Object[] processReturment(INetworkModel networkModel) {
        List<ITreeItem<INetworkModel>> dataElements = new ArrayList<ITreeItem<INetworkModel>>();
        for (IDataElement dataElement : children) {
            ITreeItem<INetworkModel> item = new TreeViewItem<INetworkModel>(networkModel, dataElement);
            dataElements.add(item);
        }
        Collections.sort(dataElements, new IDataElementComparator());
        List<Object> res = new ArrayList<Object>(dataElements);
        return res.toArray();
    }

    @Override
    protected void handleInnerElements(ITreeItem<INetworkModel> item) throws ModelException {
        children = item.getParent().getChildren(item.getDataElement());

    }

    @Override
    protected void handleRoot(ITreeItem<INetworkModel> item) throws ModelException {
        handleInnerElements(item);

    }

    @Override
    protected boolean additionalCheckChild(Object element) throws ModelException {
        return true;
    }
}
