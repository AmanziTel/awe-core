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
import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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
// TODO: LN: 01.08.2012: why implements both IStructuredContentProvider and ITreeContentProvider
// when ITreeContentProvider itself extends IStructureContentProvider?
public abstract class AbstractContentProvider<T extends IModel> implements IStructuredContentProvider, ITreeContentProvider {

    // TODO: LN: 01.08.2012, fix field order

    // TODO: LN: 01.08.2012, make fields private with protected getter
    protected List<ITreeItem<T>> rootList = new ArrayList<ITreeItem<T>>();
    // TODO: LN: 01.08.2012, initialize providers in constructors (see previous implementation of
    // ContentProvider for ProjectExplorerView for example)
    protected INetworkModelProvider networkModelProvider = AWEUIPlugin.getDefault().getNetworkModelProvider();
    protected IProjectModelProvider projectModelProvider = AWEUIPlugin.getDefault().getProjectModelProvider();
    protected static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = AWEUIPlugin.getDefault().getGeneralNodeProperties();
    private static final Logger LOGGER = Logger.getLogger(AbstractContentProvider.class);

    // TODO: LN: 01.08.2012, it's abstract class, do we really need empty implementation of this
    // method?
    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getChildren(Object parentElement) {
        ITreeItem<T> item = (ITreeItem<T>)parentElement;
        try {
            if (isInRootList(item)) {
                handleRoot(item);

            } else {
                handleInnerElements(item);
            }
        } catch (ModelException e) {
            LOGGER.error("cann't get child for parentElement " + parentElement, e);
            return null;
        }
        return processReturment(item.getParent());
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasChildren(Object element) {
        Object[] children = null;
        try {
            ITreeItem<T> item = (ITreeItem<T>)element;
            children = getChildren(item);
            // TODO: LN: 01.08.2012, why not used ArrayUtils.isEmpty()
            return (children != null) && (children.length > 0) && additionalCheckChild(element);
        } catch (ModelException e) {
            // TODO: LN: 01.08.2012, handle exception
        }
        return false;
    }

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
    protected abstract Object[] processReturment(T t);

    /**
     * handle inner elements
     * 
     * @param parentElement
     * @throws ModelException
     */
    protected abstract void handleInnerElements(ITreeItem<T> parentElement) throws ModelException;

    /**
     * add to rootElement
     * 
     * @param root
     */
    protected void addToRoot(T root) {
        rootList.add(new TreeViewItem<T>(root, root.asDataElement()));
    }

    @Override
    public Object[] getElements(Object inputElement) {
        rootList.clear();
        // TODO Auto-generated method stub
        return rootList.toArray();
    }

    /**
     * check if object in rootList
     * 
     * @param object
     * @return
     */
    private boolean isInRootList(Object object) {
        return rootList.contains(object);
    }

    /**
     * handle get roots element child
     */
    protected abstract void handleRoot(ITreeItem<T> item) throws ModelException;;

}
