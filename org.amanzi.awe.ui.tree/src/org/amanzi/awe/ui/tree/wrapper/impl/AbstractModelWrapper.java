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
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractModelWrapper<T extends IModel> extends AbstractTreeWrapper {

    private static final Logger LOGGER = Logger.getLogger(AbstractModelWrapper.class);

    private T model;

    protected AbstractModelWrapper(final T model) {
        this(null, model);

        setWrapper(this);
    }

    protected AbstractModelWrapper(final ITreeWrapper wrapper, final T model) {
        super(null, null, wrapper);

        this.model = model;
    }

    protected T getModel() {
        return model;
    }

    @Override
    public ITreeItem getParent(final ITreeItem item) {
        try {
            return getParentInternal(item);
        } catch (final ModelException e) {
            LOGGER.error("Error on getting Parent of Element <" + item + ">", e);
        }

        return null;
    }

    protected abstract ITreeItem getParentInternal(final ITreeItem item) throws ModelException;

    @Override
    public Iterator<ITreeItem> getChildren(final ITreeItem item) {
        try {
            return getChildrenInternal(item);
        } catch (final ModelException e) {
            LOGGER.error("Error on getting Children of Element <" + item + ">", e);
        }

        return null;
    }

    protected abstract Iterator<ITreeItem> getChildrenInternal(final ITreeItem item) throws ModelException;
}
