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

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.apache.commons.lang3.ObjectUtils;

/**
 * <p>
 * storage for tree items
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TreeViewItem<T extends IModel, E extends Object> implements ITreeItem<T, E> {

    private final T model;
    private final E element;

    /**
     * @param model
     * @param element
     */
    public TreeViewItem(final T model, final E element) {
        super();
        this.model = model;
        this.element = element;
    }

    @Override
    public E getChild() {
        return element;

    }

    @Override
    public T getParent() {
        return model;
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ITreeItem) {
            ITreeItem< ? , ? > anotherItem = (ITreeItem< ? , ? >)obj;

            return ObjectUtils.equals(getParent(), anotherItem.getParent())
                    && ObjectUtils.equals(getChild(), anotherItem.getChild());
        }
        return false;
    }

    @Override
    public String toString() {
        return "parent:" + getParent() + " child: " + getChild();
    }
}
