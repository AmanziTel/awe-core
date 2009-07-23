package org.amanzi.awe.networktree.property;

import org.amanzi.awe.networktree.proxy.NeoNode;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * Provider for PropertySource of Network Nodes
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NetworkPropertySourceProvider implements IPropertySourceProvider {
    
    /**
     * Return PropertySource for given element
     */
    
    public IPropertySource getPropertySource(Object element) {
        if (element instanceof NeoNode) {
            return new NetworkPropertySource((NeoNode)element);            
        }
        return null;
    }

}
