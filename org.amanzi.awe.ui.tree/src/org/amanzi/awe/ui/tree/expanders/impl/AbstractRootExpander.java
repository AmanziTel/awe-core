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
import java.util.List;

import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.awe.ui.tree.expanders.IRootExpander;
import org.amanzi.awe.ui.tree.expanders.internal.impl.RootTreeItem;
import org.amanzi.awe.ui.tree.views.IAWETreeView;
import org.amanzi.neo.models.IModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractRootExpander<M extends IModel> implements IRootExpander<M> {

    protected abstract Class<M> getSupportedClass();

    private IUIItem< ? , M> createItem(final M model) {
        return new RootTreeItem<M>(model);
    }

    @Override
    public boolean canHandle(final IAWETreeView treeView) {
        return treeView.getSupportedModel() != null && treeView.getSupportedModel().equals(getSupportedClass());
    }

    protected abstract Collection<M> getRootElements();

    @Override
    public Collection<IUIItem< ? , M>> getRootItems() {
        final List<IUIItem< ? , M>> result = new ArrayList<IUIItem< ? , M>>();

        final Collection<M> rootElements = getRootElements();
        if (rootElements != null) {
            for (final M element : getRootElements()) {
                result.add(createItem(element));
            }
        }

        return result;
    }
}
