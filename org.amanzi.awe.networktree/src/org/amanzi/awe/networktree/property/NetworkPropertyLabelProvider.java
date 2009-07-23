package org.amanzi.awe.networktree.property;

import org.amanzi.neo.core.icons.IconManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label Provider for Node property
 * 
 * @author Lagutko_N
 * @since 1.1.0
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
