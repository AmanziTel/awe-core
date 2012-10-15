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

package org.amanzi.awe.ui.label;

import org.amanzi.awe.ui.dto.IUIItemNew;
import org.amanzi.awe.ui.icons.IconManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * represent common view label provider actually used in NetworkTreeView and ProjectExplorerView
 * 
 * @author Vladislav_Kondratenko
 */
public class AWELabelProvider extends LabelProvider {

    @Override
    public String getText(final Object element) {
        if (element instanceof IUIItemNew) {
            final IUIItemNew item = (IUIItemNew)element;

            final IDataElement dataElement = item.castChild(IDataElement.class);

            if (dataElement != null) {
                return getNameFromDataElement(dataElement);
            } else {
                final IModel model = item.castChild(IModel.class);

                return getNameFromModel(model);
            }
        } else {
            return getNameFromObject(element);
        }
    }

    /**
     * should be override if necessary
     * 
     * @param element
     * @return
     */
    protected String getNameFromObject(final Object element) {
        return element.toString();
    }

    /**
     * @param element
     * @return
     */
    protected String getNameFromDataElement(final IDataElement element) {
        final String name = element.getName();
        return name != null ? name : element.toString();
    }

    protected String getNameFromModel(final IModel model) {
        final String name = model.getName();

        return name != null ? name : model.toString();
    }

    /**
     * The <code>LabelProvider</code> implementation of this <code>ILabelProvider</code> method
     * returns <code>null</code>. Subclasses may override.
     */
    @Override
    public Image getImage(final Object element) {
        INodeType nodeType = null;

        if (element instanceof IUIItemNew) {
            final IUIItemNew item = (IUIItemNew)element;

            final IDataElement dataElement = item.castChild(IDataElement.class);

            if (dataElement != null) {
                nodeType = dataElement.getNodeType();
            } else {
                final IModel model = item.castChild(IModel.class);

                if (model != null) {
                    nodeType = model.getType();
                }
            }
        }

        if (nodeType != null) {
            return IconManager.getInstance().getImage(nodeType);
        }

        return null;
    }

}
