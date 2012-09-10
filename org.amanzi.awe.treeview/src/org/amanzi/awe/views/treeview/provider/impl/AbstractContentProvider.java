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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractContentProvider<T extends IModel, E extends Object> implements ITreeContentProvider {

    private static final Logger LOGGER = Logger.getLogger(AbstractContentProvider.class);

    private final IProjectModelProvider projectModelProvider;

    private final Map<Object, ITreeItem<T, E>> treeItemCache = new HashMap<Object, ITreeItem<T, E>>();

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected AbstractContentProvider(final IProjectModelProvider projectModelProvider) {
        this.projectModelProvider = projectModelProvider;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    @Override
    public Object[] getElements(Object inputElement) {
        List<T> roots;
        try {
            roots = getRootList();

            for (T model : roots) {
                if (!treeItemCache.containsKey(model)) {
                    treeItemCache.put(model, createInnerItem(model, model));
                }
            }
            return roots.toArray();
        } catch (ModelException e) {
            LOGGER.error("can't get roots from element ");
        }
        return null;
    }

    public abstract ITreeItem<T, E> createInnerItem(T key, T value);

    public abstract ITreeItem<T, E> createInnerItem(T key, E value);

    /**
     * @return rootList
     * @throws ModelException
     */
    public abstract List<T> getRootList() throws ModelException;

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getChildren(Object parentElement) {
        ITreeItem<T, E> item = treeItemCache.get(parentElement);
        E[] children = null;
        try {
            Iterator<E> childrenIterator = item.getChildren().iterator();
            children = (E[])IteratorUtils.toArray(childrenIterator);

            for (E element : children) {
                treeItemCache.put(element, createInnerItem(item.getModel(), element));
            }
        } catch (ModelException e) {
            LOGGER.error("can't get children from element " + item);
        }
        return children;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        ITreeItem<T, E> item = treeItemCache.get(element);
        try {
            return item.hasChildren();
        } catch (ModelException e) {
            LOGGER.error("can't get validate childrent for element " + item);
            return false;
        }
    }

    /**
     * @return Returns the projectModelProvider.
     */
    public IProjectModelProvider getProjectModelProvider() {
        return projectModelProvider;
    }

}
