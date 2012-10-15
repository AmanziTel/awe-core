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

package org.amanzi.awe.ui.tree.wrapper.factory.impl;

import java.util.Iterator;

import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.neo.models.IModel;

public abstract class TreeWrapperIterator<M extends IModel> implements Iterator<ITreeWrapper> {

    private final Iterator<M> models;

    public TreeWrapperIterator(final Iterator<M> models) {
        this.models = models;
    }

    @Override
    public boolean hasNext() {
        return models.hasNext();
    }

    @Override
    public ITreeWrapper next() {
        return createTreeWrapper(models.next());
    }

    protected abstract ITreeWrapper createTreeWrapper(M model);

    @Override
    public void remove() {
        models.remove();
    }

}