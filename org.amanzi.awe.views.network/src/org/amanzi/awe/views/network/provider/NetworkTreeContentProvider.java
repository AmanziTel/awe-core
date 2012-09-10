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

import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * New content provider for NetworkTree
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class NetworkTreeContentProvider extends AbstractContentProvider<INetworkModel, IDataElement> {
    private INetworkModelProvider networkModelProvider;

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected NetworkTreeContentProvider(INetworkModelProvider networkModelProvider, IProjectModelProvider projectModelProvider) {
        super(projectModelProvider);
        this.networkModelProvider = networkModelProvider;
    }

    /**
     * create instance of network tree content provider
     */
    public NetworkTreeContentProvider() {
        this(AWEUIPlugin.getDefault().getNetworkModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider());
    }

    @Override
    public ITreeItem<INetworkModel, IDataElement> createInnerItem(INetworkModel key, IDataElement value) {
        return new NetworkTreeItem(key, value);
    }

    @Override
    public List<INetworkModel> getRootList() throws ModelException {
        return networkModelProvider.findAll(getProjectModelProvider().getActiveProjectModel());
    }

    @Override
    public ITreeItem<INetworkModel, IDataElement> createInnerItem(INetworkModel key, INetworkModel value) {
        return createInnerItem(key, value.asDataElement());
    }
}
