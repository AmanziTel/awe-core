package org.amanzi.awe.awe.views.view.provider;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.icons.IconManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
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
