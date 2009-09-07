package org.amanzi.awe.awe.views.view.provider;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for NetworkTree
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NetworkTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
    
    /*
     * NeoServiceProvider
     */
    
    protected NeoServiceProvider neoServiceProvider;
    
    /**
     * Constructor of ContentProvider
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */
    
    public NetworkTreeContentProvider(NeoServiceProvider neoProvider) {
        this.neoServiceProvider = neoProvider;
    }
    
    public Object[] getElements(Object inputElement) {
        return new Object[] {new Root(neoServiceProvider)};
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof NeoNode) {
            return ((NeoNode)parentElement).getChildren();
        }
        
        return new Object[0];
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {        
        if (element instanceof NeoNode) {
            return ((NeoNode)element).hasChildren();
        }
        return false;
    }

}
