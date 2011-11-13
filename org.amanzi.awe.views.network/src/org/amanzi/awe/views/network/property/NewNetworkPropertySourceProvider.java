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

import org.amanzi.neo.services.model.IDataElement;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.neo4j.neoclipse.property.NodePropertySource;

/**
 * Provider for PropertySource of Network Nodes
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class NewNetworkPropertySourceProvider implements IPropertySourceProvider {
    
    private IDataElement lastRawObject;
    private NodePropertySource propertySource;
    private boolean isEditable;
    
    /**
     * Return PropertySource for given element
     */
    
    public IPropertySource getPropertySource(Object element) {
        if (element instanceof IDataElement) {
            lastRawObject = (IDataElement)element;
            if (isEditable) {
            	propertySource = new NewNetworkPropertySourceEditable((IDataElement)element);
            }
            else {
            	propertySource = new NewNetworkPropertySource((IDataElement)element);
            }
            return propertySource;
        }
        return null;
    }
    
    /**
     * Allow to set is view of property is editable
     * 
     * @param isEditablePropertyView
     */
    public void setEditableToPropertyView(boolean isEditablePropertyView) {
    	isEditable = isEditablePropertyView;
    }
    
    public void reloadTable() {
    	propertySource.getPropertyDescriptors();
    }
    
    public IDataElement getLastRawObject() {
        return lastRawObject;
    }

}
