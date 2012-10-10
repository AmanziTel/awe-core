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
import org.eclipse.swt.graphics.Image;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CommonTreeViewLabelProvider extends CommonViewLabelProvider {

    /**
     * @param viewer
     */
    public CommonTreeViewLabelProvider() {
        super();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String getText(final Object element) {
        if (element instanceof ITreeItem) {
            ITreeItem item = (ITreeItem)element;
            return super.getText(item.getChild());
        } else {
            return super.getText(element);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Image getImage(Object element) {
        if (element instanceof ITreeItem) {
            ITreeItem item = (ITreeItem)element;
            return super.getImage(item.getChild());
        } else {
            return super.getImage(element);
        }
    }
}
