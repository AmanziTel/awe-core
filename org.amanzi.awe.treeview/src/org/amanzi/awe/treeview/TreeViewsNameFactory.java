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

package org.amanzi.awe.treeview;

/**
 * <p>
 * contains all tree views id
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TreeViewsNameFactory {
    /**
     * TODO Purpose of ViewsNameFactory instance holder for {@link TreeViewsNameFactory}
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private static final class InstanceHolder {
        private static final TreeViewsNameFactory INSTANCE = new TreeViewsNameFactory();
    }

    private final String NETWORK_TREE_VIEW = "org.amanzi.awe.views.network.views.NewNetworkTreeView";
    private final String PROJECT_EXPLORER_ID = "org.amanzi.awe.views.explorer.view.ProjectExplorer";

    public static TreeViewsNameFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * return network tree view ID
     * 
     * @return
     */
    public String getNetworkTreeViewId() {
        return NETWORK_TREE_VIEW;
    }

    /**
     * return project explorer view id
     * 
     * @return
     */
    public String getProjectTreeViewId() {
        return PROJECT_EXPLORER_ID;
    }
}
