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

    @Override
    public void dispose() {

    }

    /**
     * <p>
     * Comparator of IDataElement
     * </p>
     * 
     * @author Kasnitskij_V
     * @since 1.0.0
     */
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
            for (IProjectModel model : projectModelProvider.findAll()) {
                rootList.add(new TreeViewItem<IProjectModel>(model, model.asDataElement()));
            }
        } catch (ModelException e) {
            MessageDialog.openError(null, ProjectExplorerPluginMessages.ErrorTitle,
                    ProjectExplorerPluginMessages.GetElementsException);
        }

        return rootList.toArray();
    }

    @Override
    protected boolean additionalCheckChild(Object element) throws ModelException {
        return false;
    }

    @Override
    protected Object[] processReturment(IProjectModel t) {
        LOGGER.info("process returment statement for project " + t);
        Collections.sort(models, new IModelComparator());
        return models.toArray();
    }

    @Override
    protected void handleInnerElements(ITreeItem<IProjectModel> item) throws ModelException {
        models = new ArrayList<IModel>();
        for (INetworkModel model : networkModelProvider.findAll(item.getParent())) {
            LOGGER.info("add model " + model + " to project node");
            models.add(model);
        }
    }

    @Override
    protected void handleRoot(ITreeItem<IProjectModel> item) throws ModelException {
        handleInnerElements(item);
    }
}
