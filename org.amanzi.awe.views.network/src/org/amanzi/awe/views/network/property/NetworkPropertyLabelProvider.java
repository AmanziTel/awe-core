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

import org.amanzi.neo.core.icons.IconManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label Provider for Node property
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NetworkPropertyLabelProvider extends LabelProvider {
    
    /*
     * Icon Manager
     */
    private IconManager manager;
    
    /**
     * Constructor. Gets an instance of IconManager
     */
    
    public NetworkPropertyLabelProvider() {
        manager = IconManager.getIconManager();
    }
    
    /**
     * The <code>LabelProvider</code> implementation of this
     * <code>ILabelProvider</code> method returns <code>null</code>.
     * Subclasses may override.
     */
    public Image getImage(Object element) { 
        return manager.getImage(element.getClass().getSimpleName());        
    }

}
