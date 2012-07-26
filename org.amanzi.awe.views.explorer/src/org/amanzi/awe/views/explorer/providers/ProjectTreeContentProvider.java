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
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.explorer.ProjectExplorerPluginMessages;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * content provider for project explorer
 * 
 * @author Vladislav_Kondratenko
 */
public class ProjectTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    private static final Logger LOGGER = Logger.getLogger(ProjectTreeContentProvider.class);

    private final INetworkModelProvider networkModelProvider;

    private final IProjectModelProvider projectModelProvider;

    /**
     * Constructor of ContentProvider
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */
    public ProjectTreeContentProvider() {
        networkModelProvider = AWEUIPlugin.getDefault().getNetworkModelProvider();
        projectModelProvider = AWEUIPlugin.getDefault().getProjectModelProvider();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

    }

    /**
     * collect models of current selected element
     * 
     * @param parentElement
     * @return
     * @throws AWEException
     */
    private List<IModel> collectModelMap(final Object parentElement) throws ModelException {
        ArrayList<IModel> modelMap = new ArrayList<IModel>();
        if (parentElement instanceof IProjectModel) {
            IProjectModel project = ((IProjectModel)parentElement);

            addToModelCollection(networkModelProvider.findAll(project), modelMap);
        }
        return modelMap;
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        ArrayList<IModel> modelMap = new ArrayList<IModel>();
        try {
            modelMap.addAll(collectModelMap(parentElement));
        } catch (ModelException e) {
            MessageDialog.openError(null, ProjectExplorerPluginMessages.ErrorTitle,
                    ProjectExplorerPluginMessages.GetChildrenException + parentElement);
        }
        Collections.sort(modelMap, new IModelComparator());
        return modelMap.toArray();
    }

    /**
     * @param findAllNetworkModels
     * @param modelMap
     */
    private void addToModelCollection(final Iterable< ? extends IModel> findedModels, final ArrayList<IModel> modelMap) {
        Iterator< ? extends IModel> modelsIterator = findedModels.iterator();
        while (modelsIterator.hasNext()) {
            IModel model = modelsIterator.next();
            modelMap.add(model);
        }
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
    public boolean hasChildren(final Object parentElement) {
        ArrayList<IModel> modelMap = new ArrayList<IModel>();
        try {
            modelMap.addAll(collectModelMap(parentElement));
        } catch (ModelException e) {
            MessageDialog.openError(null, ProjectExplorerPluginMessages.ErrorTitle,
                    ProjectExplorerPluginMessages.HasChildrenException);
            LOGGER.error(ProjectExplorerPluginMessages.HasChildrenException, e);
        }
        if ((modelMap != null) && !modelMap.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        IProjectModel projectModels = null;
        try {

            return projectModelProvider.findAll().toArray();
        } catch (ModelException e) {
            MessageDialog.openError(null, ProjectExplorerPluginMessages.ErrorTitle,
                    ProjectExplorerPluginMessages.GetElementsException + projectModels);
        }

        return null;
    }
}
