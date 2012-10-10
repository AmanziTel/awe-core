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

package org.amanzi.awe.ui.tree.expanders.impl;

import java.util.Iterator;

import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.awe.ui.tree.expanders.IChildrenExpander;
import org.amanzi.awe.ui.tree.expanders.internal.impl.TreeItem;
import org.amanzi.neo.models.IModel;
import org.apache.commons.lang3.ObjectUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractChildrenExpander<P extends IModel, C extends Object, NP extends IModel, NC extends Object>
        implements
            IChildrenExpander<P, C, NP, NC> {

    @Override
    public boolean hasChildren(final IUIItem<P, C> parent) {
        return getChildren(parent.getParent(), parent.getChild()).hasNext();
    }

    @Override
    public boolean canHandle(final Class< ? > parentClass, final Class< ? > childClass) {
        return ObjectUtils.equals(parentClass, getSupportedParentClass())
                && ObjectUtils.equals(childClass, getSupportedChildClass());
    }

    protected abstract Class<P> getSupportedParentClass();

    protected abstract Class<C> getSupportedChildClass();

    protected abstract Iterator<NC> getChildren(P model, C child);

    protected <T1 extends IModel, T2 extends Object> TreeItem<T1, T2> createItem(final T1 parent, final T2 child) {
        return new TreeItem<T1, T2>(parent, child);
    }

}
