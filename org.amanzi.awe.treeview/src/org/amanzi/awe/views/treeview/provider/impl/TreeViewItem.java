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

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IDataModel;

/**
 * TODO Purpose of
 * <p>
 * storage for tree items
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TreeViewItem<T extends IDataModel> implements ITreeItem<T> {

    private T model;
    private IDataElement element;

    /**
     * @param model
     * @param element
     */
    public TreeViewItem(T model, IDataElement element) {
        super();
        this.model = model;
        this.element = element;
    }

    @Override
    public IDataElement getDataElement() {
        return element;
    }

    @Override
    public T getParent() {
        return model;
    }

}
