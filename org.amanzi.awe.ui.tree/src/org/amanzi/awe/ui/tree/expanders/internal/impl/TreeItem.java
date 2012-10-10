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

package org.amanzi.awe.ui.tree.expanders.internal.impl;

import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.neo.models.IModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class TreeItem<T extends IModel, E extends Object> implements IUIItem<T, E> {

    private final T parent;

    private final E child;

    public TreeItem(final T model, final E child) {
        this.parent = model;
        this.child = child;
    }

    @Override
    public E getChild() {
        return child;
    }

    @Override
    public T getParent() {
        return parent;
    }

}
