package org.amanzi.awe.awe.views.view.provider;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
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

	private static String NETWORK_MODEL_NAME = "NetworkModel";
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
		if (parentElement instanceof INetworkModel) {
			Iterable<IDataElement> children = 
					((INetworkModel)parentElement).getChildren(null);
			rootNetworkModel = (INetworkModel)parentElement;
			for (IDataElement dataElement : children) {
				// add network model to data element
				dataElement.put(NETWORK_MODEL_NAME, parentElement);
				dataElements.add(dataElement);
			}
			return dataElements.toArray();
        }
        else if (parentElement instanceof IDataElement) {
        	IDataElement child = (IDataElement)parentElement;
        	INetworkModel localRootNetworkModel = (INetworkModel)(child).get(NETWORK_MODEL_NAME);
        	Iterable<IDataElement> children = localRootNetworkModel.getChildren(child);
        	
        	for (IDataElement dataElement : children) {
        		// add network model to data element
				dataElement.put(NETWORK_MODEL_NAME, rootNetworkModel);
        		dataElements.add(dataElement);
        	}
        	return dataElements.toArray();
        }
    	else {
    		return new Object[0];
    	}
	}

	@Override
	public Object getParent(Object element) {
		// TODO Need implement
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
//        if (element instanceof INetworkModel) {
//            return ((INetworkModel)parentElement).getChildren(null)
//        }
//        return false;
		return true;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		
		List<INetworkModel> networkModels = NetworkModel.findAllNetworkModels();
		
		Object[] networkModelsInObjects = new Object[networkModels.size()];
		int i = 0;
		for (INetworkModel model : networkModels) {
			networkModelsInObjects[i++] = model;
		}
		
		return networkModelsInObjects;
	}
}
