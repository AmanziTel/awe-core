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

    public NetworkTreeContentProvider() {
        this(AWEUIPlugin.getDefault().getNetworkModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider());

    }

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected NetworkTreeContentProvider(INetworkModelProvider networkModelProvider, IProjectModelProvider projectModelProvider) {
        super(projectModelProvider);
        this.networkModelProvider = networkModelProvider;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    protected void handleInnerElements(ITreeItem<INetworkModel, IDataElement> item) throws ModelException {
        setChildren(item.getParent().getChildren(item.getChild()));
    }

    @Override
    protected void handleRoot(ITreeItem<INetworkModel, IDataElement> item) throws ModelException {
        handleInnerElements(item);
    }

    @Override
    protected boolean additionalCheckChild(Object element) throws ModelException {
        return true;
    }

    @Override
    protected List<INetworkModel> getRootElements() throws ModelException {
        return networkModelProvider.findAll(getActiveProjectModel());
    }

    @Override
    protected boolean checkNext(ITreeItem<INetworkModel, IDataElement> item) throws ModelException {
        return item.getParent().getChildren(item.getChild()).iterator().hasNext();
    }
}
