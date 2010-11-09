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

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.services.ui.IconManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * Label Provider for Network Tree
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NetworkTreeLabelProvider extends LabelProvider {
    
    /*
     * Icon manager
     */
    private IconManager manager; 
    
    /**
     * Constructor. Gets an instance of IconManager
     * 
     * @param viewer of this LabelProvider
     */
    public NetworkTreeLabelProvider(Viewer viewer) {        
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
        if (element instanceof Root) {
            return manager.getImage(IconManager.NEO_ROOT);
        }
        //else search for image by given type
        else if (element instanceof NeoNode) {
            return manager.getImage(((NeoNode)element).getType());
        }
        
        return null;
    }
    
}
