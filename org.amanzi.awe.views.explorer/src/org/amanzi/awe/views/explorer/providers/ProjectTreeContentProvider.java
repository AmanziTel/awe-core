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
import java.util.Comparator;
import java.util.List;

import org.amanzi.awe.views.explorer.ProjectExplorerPluginMessages;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.awe.views.treeview.provider.impl.TreeViewItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.project.IProjectModel;
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

    /**
     * <p>
     * Comparator of IDataElement
     * </p>
     * 
     * @author Kasnitskij_V
     * @since 1.0.0
     */
    // TODO: LN: 01.08.2012, make it constant
    // TODO: LN: 01.08.2012, why name starts with I? is it inteface?
    public static class IModelComparator implements Comparator<IModel> {

        @Override
        public int compare(final IModel dataElement1, final IModel dataElement2) {
            return dataElement1 == null ? -1 : dataElement2 == null ? 1 : dataElement1.getName().compareTo(dataElement2.getName());
        }

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
            // TODO: LN: 01.08.2012, log exception
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
        // TODO: LN: 01.08.2012, why we need to use new class IModelComparator and sort models, if
        // we can add ITreeItems and sort using comparator from Abstract Class?
        Collections.sort(models, new IModelComparator());
        List<ITreeItem<IModel>> treeItems = new ArrayList<ITreeItem<IModel>>();
        for (IModel model : models) {
            ITreeItem<IModel> item = new TreeViewItem<IModel>(t, model.asDataElement());
            treeItems.add(item);
        }
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
