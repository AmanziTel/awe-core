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

import org.amanzi.neo.model.distribution.impl.DistributionManager;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang.ArrayUtils;
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

    /**
     * Constructor of ContentProvider
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */
    public ProjectTreeContentProvider() {
    }

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /**
     * collect models of current selected element
     * 
     * @param parentElement
     * @return
     * @throws AWEException
     */
    private List<IModel> collectModelMap(Object parentElement) throws AWEException {
        ArrayList<IModel> modelMap = new ArrayList<IModel>();
        if (parentElement instanceof IProjectModel) {
            IProjectModel project = ((IProjectModel)parentElement);
            // add all networkModel model
            addToModelCollection(project.findAllNetworkModels(), modelMap);
            // add all drive model
            addToModelCollection(project.findAllDriveModels(), modelMap);
            // add all counters models
            addToModelCollection(project.findAllCountersModel(), modelMap);
        } else if (parentElement instanceof INetworkModel) {
            INetworkModel networkModel = ((INetworkModel)parentElement);
            // add all selection model
            addToModelCollection(networkModel.getAllSelectionModels(), modelMap);
            // add all n2n models
            addToModelCollection(networkModel.getNodeToNodeModels(), modelMap);
            // add all corelation models
            addToModelCollection(networkModel.getCorrelationModels(), modelMap);
            modelMap.addAll(DistributionManager.getManager().getAllDistributionModels(networkModel));
        } else if (parentElement instanceof IDriveModel) {
            IDriveModel driveModel = ((IDriveModel)parentElement);
            // add virtual datasets
            addToModelCollection(driveModel.getVirtualDatasets(), modelMap);
            modelMap.addAll(DistributionManager.getManager().getAllDistributionModels(driveModel));
        }
        return modelMap;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        ArrayList<IModel> modelMap = new ArrayList<IModel>();
        try {
            modelMap.addAll(collectModelMap(parentElement));
        } catch (AWEException e) {
            MessageDialog.openError(null, "Error", "Couldn't get children elements for" + parentElement);
        }
        Collections.sort(modelMap, new IModelComparator());
        return modelMap.toArray();
    }

    /**
     * @param findAllNetworkModels
     * @param modelMap
     */
    private void addToModelCollection(Iterable< ? extends IModel> findedModels, ArrayList<IModel> modelMap) {
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
        public int compare(IModel dataElement1, IModel dataElement2) {
            return dataElement1 == null ? -1 : dataElement2 == null ? 1 : dataElement1.getName().compareTo(dataElement2.getName());
        }

    }

    @Override
    public Object getParent(Object element) {
        // TODO Need implement
        return null;
    }

    @Override
    public boolean hasChildren(Object parentElement) {
        ArrayList<IModel> modelMap = new ArrayList<IModel>();
        try {
            modelMap.addAll(collectModelMap(parentElement));
        } catch (AWEException e) {
            MessageDialog.openError(null, "Error", "Couldn't check for child models");
            LOGGER.error("error while checking for children", e);
        }
        if (modelMap != null && !modelMap.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object[] getElements(Object inputElement) {

        IProjectModel projectModels = null;
        try {
            projectModels = ProjectModel.getCurrentProjectModel();
        } catch (AWEException e) {
            MessageDialog.openError(null, "Error", "Couldn't get element" + projectModels);
        }

        Object[] projectModelsInObject = new Object[0];
        projectModelsInObject = ArrayUtils.add(projectModelsInObject, projectModels);

        return projectModelsInObject;
    }
}
