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
import java.util.Comparator;
import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.lang3.ArrayUtils;
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
public abstract class AbstractContentProvider<T extends IModel> implements ITreeContentProvider {

    private static final Logger LOGGER = Logger.getLogger(AbstractContentProvider.class);

    // TODO: LN: 01.08.2012, same as for Providers
    private static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = AWEUIPlugin.getDefault().getGeneralNodeProperties();

    /**
     * <p>
     * Comparator for treeElements
     * </p>
     * 
     * @author Kondratenko_Vladislav
     * @since 1.0.0
     */
    // TODO: LN: 01.08.2012, make constant
    // TODO: LN: 01.08.2012, why name starts with I? is it inteface?
    public static class IDataElementComparator implements Comparator<ITreeItem<INetworkModel>> {

        @Override
        public int compare(ITreeItem<INetworkModel> dataElement1, ITreeItem<INetworkModel> dataElement2) {
            return dataElement1.getDataElement() == null ? -1 : dataElement2.getDataElement() == null ? 1 : dataElement1
                    .getDataElement().get(GENERAL_NODE_PROPERTIES.getNodeNameProperty()).toString()
                    .compareTo(dataElement2.getDataElement().get(GENERAL_NODE_PROPERTIES.getNodeNameProperty()).toString());
        }

    }

    private final List<ITreeItem<T>> rootList = new ArrayList<ITreeItem<T>>();
    private INetworkModelProvider networkModelProvider;
    private IProjectModelProvider projectModelProvider;

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected AbstractContentProvider(INetworkModelProvider networkModelProvider, IProjectModelProvider projectModelProvider) {
        super();
        this.networkModelProvider = networkModelProvider;
        this.projectModelProvider = projectModelProvider;
    }

    @Override
    public void dispose() {
    }

    // TODO: LN: 01.08.2012, incorrect constructor, children of this class should use protected
    // constructor
    @Deprecated
    public AbstractContentProvider() {
        this(AWEUIPlugin.getDefault().getNetworkModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider());

    }

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
            LOGGER.error("can't get child for parentElement " + parentElement, e);
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

            // TODO: LN: 01.08.2012, review code of ArrayUtils.isEmpty and remove unnecessary
            // condition
            return (children != null) && (!ArrayUtils.isEmpty(children)) && additionalCheckChild(element);
        } catch (ModelException e) {
            LOGGER.error("exception when trying to get child", e);
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
    protected abstract void handleRoot(ITreeItem<T> item) throws ModelException;

    /**
     * @return Returns the logger.
     */
    // TODO: LN: 01.08.2012, ?????? remove this method, no one should use Logger of this class
    // except this class
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * @return Returns the generalNodeProperties.
     */
    // TODO: LN: 01.08.2012, this method didn't used anywhere
    public static IGeneralNodeProperties getGeneralNodeProperties() {
        return GENERAL_NODE_PROPERTIES;
    }

    /**
     * @return Returns the rootList.
     */
    // TODO: LN: 01.08.2012, make it protected for data protection
    public List<ITreeItem<T>> getRootList() {
        return rootList;
    }

    /**
     * @return Returns the networkModelProvider.
     */
    // TODO: LN: 01.08.2012, make it protected for data protection
    public INetworkModelProvider getNetworkModelProvider() {
        return networkModelProvider;
    }

    /**
     * @return Returns the projectModelProvider.
     */
    // TODO: LN: 01.08.2012, make it protected for data protection
    public IProjectModelProvider getProjectModelProvider() {
        return projectModelProvider;
    };

}
