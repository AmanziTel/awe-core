package org.amanzi.awe.awe.views.view.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * New content provider for NetworkTree
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NetworkTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    private static String COULD_NOT_GET_ALL_NETWORK_MODELS = "Could not get all network models";

    private INetworkModel rootNetworkModel;

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    @Override
    public Object[] getChildren(Object parentElement) {

        ArrayList<IDataElement> dataElements = new ArrayList<IDataElement>();
        List<Object> additionalObjects = new ArrayList<Object>();
        Iterable<IDataElement> children = null;
        Object networkModel = null;
        if (parentElement instanceof INetworkModel) {
            children = ((INetworkModel)parentElement).getChildren(null);
            rootNetworkModel = (INetworkModel)parentElement;
            networkModel = parentElement;
        } else if (parentElement instanceof IDataElement) {
            IDataElement child = (IDataElement)parentElement;
            INetworkModel localRootNetworkModel = (INetworkModel)(child).get(INeoConstants.NETWORK_MODEL_NAME);
            children = localRootNetworkModel.getChildren(child);

            networkModel = rootNetworkModel;
        }
        for (IDataElement dataElement : children) {
            // add network model to data element
            dataElement.put(INeoConstants.NETWORK_MODEL_NAME, networkModel);
            dataElements.add(dataElement);
        }
        Collections.sort(dataElements, new IDataElementComparator());
        List<Object> res = new ArrayList<Object>(dataElements);
        res.addAll(additionalObjects);
        return res.toArray();
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
            return dataElement1 == null ? -1 : dataElement2 == null ? 1 : dataElement1.get(INeoConstants.PROPERTY_NAME_NAME)
                    .toString().compareTo(dataElement2.get(INeoConstants.PROPERTY_NAME_NAME).toString());
        }

    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof IDistributionModel) {
            return ((IDistributionModel)element).getAnalyzedModel();
        } else if (element instanceof IDistributionBar) {
            return ((IDistributionBar)element).getDistribution();
        }
        // TODO implement for other elements
        return null;
    }

    @Override
    public boolean hasChildren(Object parentElement) {

        Iterable<IDataElement> children = null;
        if (parentElement instanceof INetworkModel) {
            children = ((INetworkModel)parentElement).getChildren(null);
        } else if (parentElement instanceof IDataElement) {
            IDataElement child = (IDataElement)parentElement;
            INetworkModel localRootNetworkModel = (INetworkModel)(child).get(INeoConstants.NETWORK_MODEL_NAME);
            children = localRootNetworkModel.getChildren(child);
        } else if (parentElement instanceof IDistributionModel) {
            try {
                return ((IDistributionModel)parentElement).getDistributionBars().size() > 0;
            } catch (AWEException e) {
                // TODO Handle AWEException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        } else {
            return false;
        }
        if (children.iterator().hasNext()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object[] getElements(Object inputElement) {

        Iterable<INetworkModel> networkModels;
        try {
            networkModels = ProjectModel.getCurrentProjectModel().findAllNetworkModels();
        } catch (AWEException e) {
            MessageDialog.openError(null, "Error", COULD_NOT_GET_ALL_NETWORK_MODELS);
            return null;
        }

        Object[] networkModelsInObjects = new Object[0];
        for (INetworkModel model : networkModels) {
            networkModelsInObjects = ArrayUtils.add(networkModelsInObjects, model);
        }

        return networkModelsInObjects;
    }
}
