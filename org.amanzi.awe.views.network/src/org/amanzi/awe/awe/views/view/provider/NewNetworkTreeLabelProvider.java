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
package org.amanzi.awe.awe.views.view.provider;

import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.ui.IconManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * New Label Provider for Network Tree
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class NewNetworkTreeLabelProvider extends LabelProvider {
    
    /*
     * Icon manager
     */
    private IconManager manager; 
    
    /**
     * Constructor. Gets an instance of IconManager
     * 
     * @param viewer of this LabelProvider
     */
    public NewNetworkTreeLabelProvider(Viewer viewer) {        
        manager = IconManager.getIconManager();
        manager.addViewer(viewer);
    }
    
    /**
     * The <code>LabelProvider</code> implementation of this
     * <code>ILabelProvider</code> method returns <code>null</code>.
     * Subclasses may override.
     */
    public Image getImage(Object element) {
        //if element is Root than get a NeoRoot image
        if (element instanceof INetworkModel) {
            return manager.getImage(IconManager.NEO_ROOT);
        }
        //else search for image by given type
        else if (element instanceof IDataElement) {
            return manager.getImage(((IDataElement)element).
            		get(INeoConstants.PROPERTY_TYPE_NAME).toString());
        } else if (element instanceof IDistributionModel) {
            return manager.getImage(IconManager.NEO_ROOT);
        } else if (element instanceof IDistributionBar) {
            return manager.getImage(IconManager.NETWORK_ICON);
        }
        
        return null;
    }
    
}
