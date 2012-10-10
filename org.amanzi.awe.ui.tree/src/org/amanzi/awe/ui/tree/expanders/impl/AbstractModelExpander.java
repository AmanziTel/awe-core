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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
public abstract class AbstractModelExpander<M extends IModel, C extends IModel> extends AbstractChildrenExpander<M, C> {

    @Override
    public Collection<IUIItem< ? , ? >> getChildren(final IUIItem<M, C> parent) {
        final List<IUIItem< ? , ? >> result = new ArrayList<IUIItem< ? , ? >>();

        final Iterator< ? > children = getChildren(parent.getParent(), parent.getChild());
        if (children != null) {
            while (children.hasNext()) {
                result.add(createItem(parent.getChild(), children.next()));
            }
        }

        return result;
    }

    @Override
    public IUIItem< ? , ? > getParent(final IUIItem<M, C> child) {
        return createItem(getParent(child.getParent()), child.getParent());
    }

    protected abstract <T extends IModel> T getParent(M child);

}
