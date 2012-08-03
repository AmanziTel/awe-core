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
import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.network.NetworkTreePluginMessages;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.awe.views.treeview.provider.impl.TreeViewItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * New content provider for NetworkTree
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class NetworkTreeContentProvider extends AbstractContentProvider<INetworkModel> {

    private static final Logger LOGGER = Logger.getLogger(NetworkTreeContentProvider.class);
    private Iterable<IDataElement> children = null;

    public NetworkTreeContentProvider() {
        this(AWEUIPlugin.getDefault().getNetworkModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider(), AWEUIPlugin
                .getDefault().getGeneralNodeProperties());

    }

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected NetworkTreeContentProvider(INetworkModelProvider networkModelProvider, IProjectModelProvider projectModelProvider,
            IGeneralNodeProperties generalNodeProperties) {
        super(networkModelProvider, projectModelProvider, generalNodeProperties);
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
            networkModels = getNetworkModelProvider().findAll(getProjectModelProvider().getActiveProjectModel());
        } catch (ModelException e) {
            LOGGER.error("can't get network models ", e);
            MessageDialog.openError(null, NetworkTreePluginMessages.ERROR_TITLE,
                    NetworkTreePluginMessages.COULD_NOT_GET_ALL_NETWORK_MODELS);
            return null;
        }

        for (INetworkModel model : networkModels) {
            addToRoot(model);
        }
        return getRootList().toArray();
    }

    @Override
    protected Object[] processReturment(INetworkModel networkModel) {
        List<ITreeItem<INetworkModel>> dataElements = new ArrayList<ITreeItem<INetworkModel>>();
        for (IDataElement dataElement : children) {
            ITreeItem<INetworkModel> item = new TreeViewItem<INetworkModel>(networkModel, dataElement);
            dataElements.add(item);
        }
        Collections.sort(dataElements, getDataElementComparator());
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
