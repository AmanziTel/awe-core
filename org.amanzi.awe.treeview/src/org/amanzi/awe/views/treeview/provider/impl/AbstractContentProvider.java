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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * <p>
 * common content provider
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractContentProvider<T extends IModel, E extends Object> implements ITreeContentProvider {

    private static final Logger LOGGER = Logger.getLogger(AbstractContentProvider.class);

    private static final DataElementComparator DEFAULT_DATA_ELEMENT_COMPARER = new DataElementComparator();

    private Iterable<E> children = null;

    private final IProjectModelProvider projectModelProvider;

    private final List<ITreeItem<T, T>> rootList = new ArrayList<ITreeItem<T, T>>();

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected AbstractContentProvider(final IProjectModelProvider projectModelProvider) {
        this.projectModelProvider = projectModelProvider;
    }

    /**
     * <p>
     * Comparator for treeElements
     * </p>
     * 
     * @author Kondratenko_Vladislav
     * @since 1.0.0
     */
    @SuppressWarnings("rawtypes")
    protected static class DataElementComparator implements Comparator<ITreeItem> {
        @Override
        public int compare(final ITreeItem dataElement1, final ITreeItem dataElement2) {
            return dataElement1.getChild() == null ? -1 : dataElement2.getChild() == null ? 1 : 0;
        }
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }

    @Override
    public void dispose() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getChildren(final Object parentElement) {
        ITreeItem<T, E> item = (ITreeItem<T, E>)parentElement;
        try {
            getChildren(item);
        } catch (ModelException e) {
            LOGGER.error("can't get child for parentElement " + parentElement, e);
            return null;
        }
        return processReturment(getRoot(item));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasChildren(final Object element) {
        try {
            ITreeItem<T, E> item = (ITreeItem<T, E>)element;
            return checkNext(item);
        } catch (ModelException e) {
            LOGGER.error("exception when trying to get child", e);
        }
        return false;
    }

    protected abstract boolean checkNext(ITreeItem<T, E> item) throws ModelException;

    /**
     * @param element
     * @return
     * @throws ModelException
     */
    protected abstract boolean additionalCheckChild(Object element) throws ModelException;

    /**
     * @param t
     * @return
     */
    protected Object[] processReturment(final T model) {
        List<ITreeItem<T, E>> dataElements = new ArrayList<ITreeItem<T, E>>();
        for (E dataElement : children) {
            ITreeItem<T, E> item = createItem(model, dataElement);
            dataElements.add(item);
        }
        Collections.sort(dataElements, getDataElementComparer());
        return dataElements.toArray();
    }

    /**
     * handle inner elements
     * 
     * @param parentElement
     * @throws ModelException
     */
    protected abstract void getChildren(ITreeItem<T, E> parentElement) throws ModelException;

    @Override
    public Object[] getElements(final Object inputElement) {
        rootList.clear();
        List<T> roots = null;
        try {
            if (getActiveProjectModel() != null) {
                roots = getRootElements();
                for (T root : roots) {
                    rootList.add(createRootItem(root));
                }
            }
        } catch (ModelException e) {
            LOGGER.error("can't get roots", e);
        }

        return rootList.toArray();
    }

    protected ITreeItem<T, T> createRootItem(T model) {
        return new TreeViewItem<T, T>(null, model);
    }

    /**
     * create item
     * 
     * @param root
     * @return
     */
    protected ITreeItem<T, E> createItem(T root, E element) {
        return new TreeViewItem<T, E>(root, element);
    }

    /**
     * get root elements
     * 
     * @return
     * @throws ModelException
     */
    protected abstract List<T> getRootElements() throws ModelException;

    /**
     * @return Returns the rootList.
     */
    protected List<ITreeItem<T, T>> getRootList() {
        return rootList;
    }

    /**
     * @return Returns the projectModelProvider.
     */
    protected IProjectModelProvider getProjectModelProvider() {
        return projectModelProvider;
    }

    /**
     * @return Returns the DATA_ELEMENT_COMPARATOR.
     */
    public DataElementComparator getDataElementComparer() {
        return DEFAULT_DATA_ELEMENT_COMPARER;
    };

    /**
     * @param children
     */
    protected void setChildren(final Iterable<E> children) {
        this.children = children;
    }

    protected IProjectModel getActiveProjectModel() {
        return projectModelProvider.getActiveProjectModel();
    }

    protected boolean isRoot(ITreeItem< ? , ? > item) {
        return item.getParent() == null;
    }

    @SuppressWarnings("unchecked")
    protected T getRoot(ITreeItem<T, ? > item) {
        if (isRoot(item)) {
            return (T)item.getChild();
        } else {
            return item.getParent();
        }
    }
}
