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
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IDataModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractContentProvider<T extends IDataModel> implements IStructuredContentProvider, ITreeContentProvider {

    protected List<ITreeItem<T>> rootList = new ArrayList<ITreeItem<T>>();
    protected INetworkModelProvider networkModelProvider = AWEUIPlugin.getDefault().getNetworkModelProvider();
    protected IProjectModelProvider projectModelProvider = AWEUIPlugin.getDefault().getProjectModelProvider();
    protected static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = AWEUIPlugin.getDefault().getGeneralNodeProperties();

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return processReturment(item.getParent());
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasChildren(Object element) {
        Iterable<IDataElement> children = null;
        try {
            if (element instanceof ITreeItem) {
                ITreeItem<INetworkModel> item = (ITreeItem<INetworkModel>)element;
                children = item.getParent().getChildren(item.getDataElement());
                if (children.iterator().hasNext()) {
                    return true;
                }
            } else {
                return additionalCheckChild(element);
            }
        } catch (ModelException e) {

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
     * @param parentElement
     * @throws ModelException
     */
    protected abstract void handleInnerElements(ITreeItem<T> parentElement) throws ModelException;

    protected void addToRoot(T root) {
        rootList.add(new TreeViewItem<T>(root, root.asDataElement()));
    }

    private boolean isInRootList(Object object) {
        return rootList.contains(object);
    }

    /**
     *
     */
    protected abstract void handleRoot(ITreeItem<T> item) throws ModelException;;

}
