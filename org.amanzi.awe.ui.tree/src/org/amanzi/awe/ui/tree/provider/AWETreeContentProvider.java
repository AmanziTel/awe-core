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

package org.amanzi.awe.ui.tree.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AWETreeContentProvider implements ITreeContentProvider {

    private final Set<ITreeWrapperFactory> factories;

    /**
     * 
     */
    public AWETreeContentProvider(final Set<ITreeWrapperFactory> factories) {
        this.factories = factories;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(final Object inputElement) {
        final List<ITreeWrapper> wrappers = new ArrayList<ITreeWrapper>();

        for (final ITreeWrapperFactory factory : getFactories()) {
            final Iterator<ITreeWrapper> items = factory.getWrappers(inputElement);
            if (items != null) {
                wrappers.addAll(IteratorUtils.toList(items));
            }
        }

        return toObject(wrappers);
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        final Pair<ITreeWrapper, ITreeItem> wrapper = convertObject(parentElement);

        if (wrapper != null) {
            return getChildren(wrapper.getLeft(), wrapper.getRight());
        }

        return null;
    }

    @Override
    public Object getParent(final Object element) {
        final Pair<ITreeWrapper, ITreeItem> wrapper = convertObject(element);

        if (wrapper != null) {
            return getParent(wrapper.getLeft(), wrapper.getRight());
        }

        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
        final Pair<ITreeWrapper, ITreeItem> wrapper = convertObject(element);

        if (wrapper != null) {
            return hasChildren(wrapper.getLeft(), wrapper.getRight());
        }

        return false;
    }

    protected Set<ITreeWrapperFactory> getFactories() {
        return factories;
    }

    private Pair<ITreeWrapper, ITreeItem> convertObject(final Object element) {
        Pair<ITreeWrapper, ITreeItem> result = null;

        if (element instanceof ITreeItem) {
            final ITreeItem item = (ITreeItem)element;
            final ITreeWrapper wrapper = item.getWrapper();

            result = new ImmutablePair<ITreeWrapper, ITreeItem>(wrapper, item);
        }

        return result;
    }

    private Object getParent(final ITreeWrapper wrapper, final ITreeItem item) {
        return wrapper.getParent(item);
    }

    private Object[] getChildren(final ITreeWrapper wrapper, final ITreeItem item) {
        final Iterator<ITreeItem> iterator = wrapper.getChildren(item);

        if (iterator != null) {
            return toObject(iterator);
        }

        return null;
    }

    private boolean hasChildren(final ITreeWrapper wrapper, final ITreeItem item) {
        final Iterator<ITreeItem> iterator = wrapper.getChildren(item);

        return iterator != null && iterator.hasNext();
    }

    private <T extends ITreeItem> Object[] toObject(final Iterator<T> itemIterator) {
        return IteratorUtils.toArray(itemIterator);
    }

    private <T extends ITreeItem> Object[] toObject(final Collection<T> itemCollection) {
        return itemCollection.toArray(new Object[itemCollection.size()]);
    }
}
