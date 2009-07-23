package org.amanzi.awe.networktree.views.provider;

import org.amanzi.awe.networktree.proxy.NeoNode;
import org.amanzi.awe.networktree.proxy.Root;
import org.amanzi.neo.core.icons.IconManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label Provider for Network Tree
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NetworkTreeLabelProvider extends LabelProvider {
    
    /*
     * Icon manager
     */
    private IconManager manager; 
    
    /**
     * Constructor. Gets an instance of IconManager
     * 
     */
    public NetworkTreeLabelProvider() {
        manager = IconManager.getIconManager();
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
