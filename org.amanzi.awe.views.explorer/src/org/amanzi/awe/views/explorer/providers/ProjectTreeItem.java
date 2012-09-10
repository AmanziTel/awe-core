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

package org.amanzi.awe.views.explorer.providers;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.impl.AbstractTreeViewItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.INetworkModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ProjectTreeItem extends AbstractTreeViewItem<IProjectModel, IModel> {

    private static final IDriveModelProvider DRIVE_MODEL_PROVIDER = AWEUIPlugin.getDefault().getDriveModelProvider();

    private static final INetworkModelProvider NETWORK_MODEL_PROVIDER = AWEUIPlugin.getDefault().getNetworkModelProvider();

    public ProjectTreeItem(IProjectModel root, IModel child) {
        super(root, child);
    }

    @Override
    public List<IModel> getChildren() throws ModelException {
        List<IModel> models = new ArrayList<IModel>();
        for (INetworkModel model : NETWORK_MODEL_PROVIDER.findAll(getModel())) {
            models.add(model);
        }
        for (IDriveModel model : DRIVE_MODEL_PROVIDER.findAll(getModel())) {
            models.add(model);
        }

        return models;
    }

    @Override
    public boolean hasChildren() throws ModelException {
        if (getModel().equals(getChild())) {
            return !NETWORK_MODEL_PROVIDER.findAll(getModel()).isEmpty() || !DRIVE_MODEL_PROVIDER.findAll(getModel()).isEmpty();
        }
        return false;
    }

}
