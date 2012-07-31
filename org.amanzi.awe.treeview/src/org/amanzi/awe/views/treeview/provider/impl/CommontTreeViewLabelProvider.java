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

import org.amanzi.awe.ui.icons.IconManager;
import org.amanzi.awe.ui.label.CommonViewLabelProvider;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IDataModel;
import org.eclipse.jface.viewers.Viewer;
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
    public CommontTreeViewLabelProvider(Viewer viewer) {
        super(viewer);
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getText(final Object element) {
        String name = super.getText(element);
        if (name != null) {
            return name;
        } else if (element instanceof ITreeItem) {
            ITreeItem<IDataModel> item = (ITreeItem<IDataModel>)element;
            name = (String)item.getDataElement().get(generalNodeProperties.getNodeNameProperty());
            return name != null ? name : element.toString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Image getImage(Object element) {
        Image img = super.getImage(element);
        if (img != null) {
            return img;
        } else if (element instanceof ITreeItem) {
            ITreeItem<IDataModel> item = (ITreeItem<IDataModel>)element;
            String type = (String)item.getDataElement().get(generalNodeProperties.getNodeTypeProperty());
            return IconManager.getInstance().getImage(type);
        }

        return null;
    }
}
