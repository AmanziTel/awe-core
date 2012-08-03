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
import java.util.Collections;
import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.explorer.ProjectExplorerPluginMessages;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.awe.views.treeview.provider.impl.TreeViewItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * content provider for project explorer
 * 
 * @author Vladislav_Kondratenko
 */
public class ProjectTreeContentProvider extends AbstractContentProvider<IProjectModel> {

    private static final Logger LOGGER = Logger.getLogger(ProjectTreeContentProvider.class);

    private List<IModel> models;

    public ProjectTreeContentProvider() {
        this(AWEUIPlugin.getDefault().getNetworkModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider(), AWEUIPlugin
                .getDefault().getGeneralNodeProperties());

    }

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected ProjectTreeContentProvider(INetworkModelProvider networkModelProvider, IProjectModelProvider projectModelProvider,
            IGeneralNodeProperties generalNodeProperties) {
        super(networkModelProvider, projectModelProvider, generalNodeProperties);
    }

    @Override
    public Object getParent(final Object element) {
        // TODO Need implement
        return null;
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        super.getElements(inputElement);
        try {
            for (IProjectModel model : getProjectModelProvider().findAll()) {
                getRootList().add(new TreeViewItem<IProjectModel>(model, model.asDataElement()));
            }
        } catch (ModelException e) {
            LOGGER.error("can't get element", e);
            MessageDialog.openError(null, ProjectExplorerPluginMessages.ErrorTitle,
                    ProjectExplorerPluginMessages.GetElementsException);
        }

        return getRootList().toArray();
    }

    @Override
    protected boolean additionalCheckChild(Object element) throws ModelException {
        return true;
    }

    @Override
    protected Object[] processReturment(IProjectModel t) {
        LOGGER.info("process returment statement for project " + t);
        List<ITreeItem<IProjectModel>> treeItems = new ArrayList<ITreeItem<IProjectModel>>();
        for (IModel model : models) {
            ITreeItem<IProjectModel> item = new TreeViewItem<IProjectModel>(t, model.asDataElement());
            treeItems.add(item);
        }
        Collections.sort(treeItems, getDataElementComparator());
        return treeItems.toArray();
    }

    @Override
    protected void handleInnerElements(ITreeItem<IProjectModel> item) throws ModelException {
        models = new ArrayList<IModel>();
        if (!item.getParent().asDataElement().equals(item.getDataElement())) {
            return;
        }
        for (INetworkModel model : getNetworkModelProvider().findAll(item.getParent())) {
            LOGGER.info("add model " + model + " to project node");
            models.add(model);
        }
    }

    @Override
    protected void handleRoot(ITreeItem<IProjectModel> item) throws ModelException {
        handleInnerElements(item);
    }
}
