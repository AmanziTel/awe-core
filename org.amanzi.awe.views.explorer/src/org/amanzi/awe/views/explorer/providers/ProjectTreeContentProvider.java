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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.NetworkModel;
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
        ArrayList<IDataElement> dataElements = new ArrayList<IDataElement>();
        ArrayList<IModel> modelMap = new ArrayList<IModel>();
        try {
            if (parentElement instanceof IProjectModel) {
                addToModelCollection(((IProjectModel)parentElement).findAllNetworkModels(), modelMap);
                addToModelCollection(((IProjectModel)parentElement).findAllDriveModels(), modelMap);
            } else if (parentElement instanceof IDataElement) {
                Map<String, IModel> models = getModelOfDataElement((IDataElement)parentElement);
                if (models.containsKey(DatasetTypes.NETWORK.getId())) {
                    addToModelCollection(((INetworkModel)models.get(DatasetTypes.NETWORK.getId())).getAllSelectionModels(),
                            modelMap);
                    addToModelCollection(((INetworkModel)models.get(DatasetTypes.NETWORK.getId())).getNodeToNodeModels(), modelMap);
                    addToModelCollection(((INetworkModel)models.get(DatasetTypes.NETWORK.getId())).getCorrelationModels(), modelMap);
                } else if (models.containsKey(DatasetTypes.DRIVE.getId())) {
                    addToModelCollection(((IDriveModel)models.get(DatasetTypes.DRIVE.getId())).getVirtualDatasets(), modelMap);
                }
            }
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        for (IModel model : modelMap) {
            dataElements.add(new DataElement(model.getRootNode()));
        }
        Collections.sort(dataElements, new IDataElementComparator());
        return dataElements.toArray();
    }

    private Map<String, IModel> getModelOfDataElement(IDataElement element) {
        String type = element.get(NewAbstractService.TYPE).toString();
        boolean isRequired = false;
        Map<String, IModel> models = new HashMap<String, IModel>();
        for (DatasetTypes dType : DatasetTypes.values()) {
            if (dType.getId().equals(type)) {
                isRequired = true;
            }
        }
        if (!isRequired) {
            return models;
        }
        DatasetTypes datasetType = DatasetTypes.valueOf(type.toUpperCase());
        // NodeToNodeTypes rel = NodeToNodeTypes.valueOf(type.toUpperCase());

        IModel model = null;
        try {
            if (datasetType != null) {

                switch (datasetType) {
                case NETWORK:
                    model = new NetworkModel(((DataElement)element).getNode());
                    break;
                case DRIVE:
                    model = new DriveModel(((DataElement)element).getNode());
                }
                models.put(datasetType.getId(), model);

                // } else if (rel != null) {
                // model = new NodeToNodeRelationshipModel(((DataElement)element).getNode());
                // models.put(rel.name().toLowerCase(), model);
            }

        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return models;
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
    public static class IDataElementComparator implements Comparator<IDataElement> {

        @Override
        public int compare(IDataElement dataElement1, IDataElement dataElement2) {
            return dataElement1 == null ? -1 : dataElement2 == null ? 1 : dataElement1.get(NewAbstractService.NAME).toString()
                    .compareTo(dataElement2.get(NewAbstractService.NAME).toString());
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
                addToModelCollection(((IProjectModel)parentElement).findAllNetworkModels(), modelMap);
                addToModelCollection(((IProjectModel)parentElement).findAllDriveModels(), modelMap);
            } else if (parentElement instanceof IDataElement) {
                Map<String, IModel> models = getModelOfDataElement((IDataElement)parentElement);
                if (models.containsKey(DatasetTypes.NETWORK.getId())) {
                    addToModelCollection(((INetworkModel)models.get(DatasetTypes.NETWORK.getId())).getAllSelectionModels(),
                            modelMap);
                    addToModelCollection(((INetworkModel)models.get(DatasetTypes.NETWORK.getId())).getNodeToNodeModels(), modelMap);
                    addToModelCollection(((INetworkModel)models.get(DatasetTypes.NETWORK.getId())).getCorrelationModels(), modelMap);
                } else if (models.containsKey(DatasetTypes.DRIVE.getId())) {
                    addToModelCollection(((IDriveModel)models.get(DatasetTypes.DRIVE.getId())).getVirtualDatasets(), modelMap);
                }
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
