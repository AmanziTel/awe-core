package org.amanzi.awe.awe.views.view.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * New content provider for NetworkTree
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NewNetworkTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
    /*
     * NeoServiceProvider
     */

    protected NeoServiceProviderUi neoServiceProvider;

    private INetworkModel rootNetworkModel;

    /**
     * Constructor of ContentProvider
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */

    public NewNetworkTreeContentProvider(NeoServiceProviderUi neoProvider) {
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
        Iterable<IDataElement> children = null;
        if (parentElement instanceof INetworkModel) {
            children = ((INetworkModel)parentElement).getChildren(null);
            rootNetworkModel = (INetworkModel)parentElement;
            for (IDataElement dataElement : children) {
                // add network model to data element
                dataElement.put(INeoConstants.NETWORK_MODEL_NAME, parentElement);
                dataElements.add(dataElement);
            }
        } else if (parentElement instanceof IDataElement) {
            IDataElement child = (IDataElement)parentElement;
            INetworkModel localRootNetworkModel = (INetworkModel)(child).get(INeoConstants.NETWORK_MODEL_NAME);
            children = localRootNetworkModel.getChildren(child);

            for (IDataElement dataElement : children) {
                // add network model to data element
                dataElement.put(INeoConstants.NETWORK_MODEL_NAME, rootNetworkModel);
                dataElements.add(dataElement);
            }
        }
        Collections.sort(dataElements, new IDataElementComparator());
        return dataElements.toArray();
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
        // TODO Need implement
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
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        Object[] networkModelsInObjects = new Object[0];
        for (INetworkModel model : networkModels) {
            ArrayUtils.add(networkModelsInObjects, model);
        }

        return networkModelsInObjects;
    }
}
