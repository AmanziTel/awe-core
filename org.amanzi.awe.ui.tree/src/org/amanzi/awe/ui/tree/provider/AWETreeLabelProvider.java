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

import org.amanzi.awe.ui.label.AWELabelProvider;
import org.amanzi.awe.ui.tree.item.ITreeItem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AWETreeLabelProvider extends AWELabelProvider {

    public AWETreeLabelProvider() {
        super();
    }

    @Override
    public String getText(final Object element) {
        String result = null;

        if (element instanceof ITreeItem) {
            final ITreeItem item = (ITreeItem)element;

            result = item.getWrapper().getTitle(item);
        }

        if (result == null) {
            result = super.getText(element);
        }

        return result;
    }

}
