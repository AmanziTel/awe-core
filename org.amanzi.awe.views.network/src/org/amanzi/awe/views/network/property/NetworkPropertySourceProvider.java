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
package org.amanzi.awe.views.network.property;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * Provider for PropertySource of Network Nodes
 * 
 * @author Lagutko_N
 * @since 1.0.0
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
