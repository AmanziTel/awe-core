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

package org.amanzi.awe.views.treeview.provider.impl;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractTreeViewItem<T extends IModel, E extends Object> implements ITreeItem<T, E> {

    private static final IProjectModelProvider PROJECT_PROVIDER = AWEUIPlugin.getDefault().getProjectModelProvider();

    private T root;

    private E child;

    /**
     * @param root
     * @param child
     */
    public AbstractTreeViewItem(T root, E child) {
        this.root = root;
        this.child = child;
    }

    protected IProjectModelProvider getProjectModelProvider() {
        return PROJECT_PROVIDER;
    }

    @Override
    public T getModel() {
        return root;
    }

    @Override
    public E getChild() {
        return child;
    }
}
