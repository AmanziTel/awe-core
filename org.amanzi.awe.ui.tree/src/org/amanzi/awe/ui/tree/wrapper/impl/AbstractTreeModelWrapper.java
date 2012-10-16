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

package org.amanzi.awe.ui.tree.wrapper.impl;

import java.util.Iterator;

import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.ITreeModel;
import org.amanzi.neo.models.exceptions.ModelException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractTreeModelWrapper<T extends ITreeModel> extends AbstractModelWrapper<T> {

    /**
     * @param wrapper
     * @param model
     */
    public AbstractTreeModelWrapper(final T model) {
        super(model);
    }

    protected abstract Class<T> getModelClass();

    @Override
    protected ITreeItem getParentInternal(final ITreeItem item) throws ModelException {
        final IDataElement dataElement = item.castChild(IDataElement.class);

        ITreeItem result = null;

        if (dataElement != null) {
            final IDataElement parent = getModel().getParentElement(dataElement);

            if (parent != null) {
                result = createTreeItem(parent);
            }
        }
        return result;
    }

    @Override
    protected Iterator<ITreeItem> getChildrenInternal(final ITreeItem item) throws ModelException {
        IDataElement dataElement = item.castChild(IDataElement.class);

        if (dataElement == null) {
            final IModel treeModel = item.castChild(IModel.class);
            if (treeModel != null) {
                dataElement = treeModel.asDataElement();
            }
        }

        Iterator<ITreeItem> result = null;

        if (dataElement != null) {
            final Iterable<IDataElement> dataElements = getModel().getChildren(dataElement);

            if (dataElements != null) {
                result = new TreeItemIterator(dataElements.iterator());
            }
        }

        return result;
    }

}
