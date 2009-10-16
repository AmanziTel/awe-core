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
package org.amanzi.awe.views.tree.drive.views;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.icons.IconManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * <p>
 * Label provider for drive tree
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class DriveTreeLabelProvider extends LabelProvider {

    /*
     * Icon manager
     */
    private IconManager manager;

    /**
     * Constructor. Gets an instance of IconManager
     * 
     * @param viewer of this LabelProvider
     */
    public DriveTreeLabelProvider(Viewer viewer) {
        manager = IconManager.getIconManager();
        manager.addViewer(viewer);
    }

    public Image getImage(Object element) {
        if (element instanceof Root) {
            return manager.getImage(IconManager.NEO_ROOT);
        }
        else if (element instanceof NeoNode) {
            return manager.getImage(((NeoNode)element).getType());
        }
        return null;
    }

}
