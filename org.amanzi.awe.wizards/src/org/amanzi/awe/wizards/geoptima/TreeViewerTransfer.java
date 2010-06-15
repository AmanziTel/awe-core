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

package org.amanzi.awe.wizards.geoptima;

import org.eclipse.gef.dnd.SimpleObjectTransfer;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
final class TreeViewerTransfer extends SimpleObjectTransfer {

    private static final TreeViewerTransfer INSTANCE = new TreeViewerTransfer();
    private static final String TYPE_NAME = "Local Tree Transfer"//$NON-NLS-1$
            + System.currentTimeMillis() + ":" + INSTANCE.hashCode();//$NON-NLS-1$
    private static final int TYPEID = registerType(TYPE_NAME);

    private static Viewer viewer;

    /**
     * Returns the singleton instance.
     * 
     * @return The singleton instance
     */
    public static TreeViewerTransfer getInstance() {
        return INSTANCE;
    }

    private TreeViewerTransfer() {
    }

    /**
     * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
     */
    protected int[] getTypeIds() {
        return new int[] {TYPEID};
    }

    /**
     * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
     */
    protected String[] getTypeNames() {
        return new String[] {TYPE_NAME};
    }

    /**
     * Returns the viewer where the drag started.
     * 
     * @return The viewer where the drag started
     */
    public Viewer getViewer() {
        return viewer;
    }

    /**
     * Sets the viewer where the drag started.
     * 
     * @param viewer2 The viewer
     */
    public void setViewer(Viewer viewer2) {
        viewer = viewer2;
    }

    }
