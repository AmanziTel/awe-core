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

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Vladislav_Kondratenko
 */
public class ProjectTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
    /*
     * NeoServiceProvider
     */

    protected NeoServiceProviderUi neoServiceProvider;

    /**
     * Constructor of ContentProvider
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */

    public ProjectTreeContentProvider(NeoServiceProviderUi neoProvider) {
        this.neoServiceProvider = neoProvider;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    @Override
    public Object[] getChildren(Object parentElement) {
        ArrayList<IModel> dataElements = new ArrayList<IModel>();
        ArrayList<IModel> modelMap = new ArrayList<IModel>();
        try {
            if (parentElement instanceof IProjectModel) {
                addToModelCollection(((IProjectModel)parentElement).findAllNetworkModels(), modelMap);
                addToModelCollection(((IProjectModel)parentElement).findAllDriveModels(), modelMap);
            } else if (parentElement instanceof INetworkModel) {
                INetworkModel parent = (INetworkModel)parentElement;
                addToModelCollection(parent.getAllSelectionModels(), modelMap);
                addToModelCollection(parent.getNodeToNodeModels(), modelMap);
                addToModelCollection(parent.getCorrelationModels(), modelMap);
            } else if (parentElement instanceof IDriveModel) {
                IDriveModel parent = (IDriveModel)parentElement;
                addToModelCollection(parent.getVirtualDatasets(), modelMap);
            }
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        for (IModel model : modelMap) {
            dataElements.add(model);
        }
        Collections.sort(dataElements, new IModelComparator());
        return dataElements.toArray();
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
            return dataElement1 == null ? -1 : dataElement2 == null ? 1 : dataElement1.getName().compareTo(dataElement1.getName());
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
            if (parentElement instanceof IProjectModel) {
                addToModelCollection(((IProjectModel)parentElement).findAllDriveModels(), modelMap);
                addToModelCollection(((IProjectModel)parentElement).findAllNetworkModels(), modelMap);
            } else if (parentElement instanceof INetworkModel) {
                INetworkModel parent = (INetworkModel)parentElement;
                addToModelCollection(parent.getAllSelectionModels(), modelMap);
                addToModelCollection(parent.getNodeToNodeModels(), modelMap);
                addToModelCollection(parent.getCorrelationModels(), modelMap);
            } else if (parentElement instanceof IDriveModel) {
                IDriveModel parent = (IDriveModel)parentElement;
                addToModelCollection(parent.getVirtualDatasets(), modelMap);
            }
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        if (modelMap != null && !modelMap.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object[] getElements(Object inputElement) {

        IProjectModel projectModels;
        try {
            projectModels = ProjectModel.getCurrentProjectModel();
        } catch (AWEException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        Object[] projectModelsInObject = new Object[0];
        projectModelsInObject = ArrayUtils.add(projectModelsInObject, projectModels);

        return projectModelsInObject;
    }
}
