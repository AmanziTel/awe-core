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

import org.amanzi.awe.ui.label.CommonViewLabelProvider;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IDataModel;
import org.eclipse.swt.graphics.Image;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CommontTreeViewLabelProvider extends CommonViewLabelProvider {

    /**
     * @param viewer
     */
    public CommontTreeViewLabelProvider() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getText(final Object element) {
        if (element instanceof ITreeItem) {
            ITreeItem<IDataModel> item = (ITreeItem<IDataModel>)element;
            return super.getText(item.getDataElement());
        } else {
            return super.getText(element);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Image getImage(Object element) {
        if (element instanceof ITreeItem) {
            ITreeItem<IDataModel> item = (ITreeItem<IDataModel>)element;
            return super.getImage(item.getDataElement());
        } else {
            return super.getImage(element);
        }
    }
}
