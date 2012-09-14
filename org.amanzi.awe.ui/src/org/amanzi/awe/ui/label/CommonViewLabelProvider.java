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

import org.amanzi.awe.ui.icons.IconManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * represent common view label provider actually used in NetworkTreeView and ProjectExplorerView
 * 
 * @author Vladislav_Kondratenko
 */
public class CommonViewLabelProvider extends LabelProvider {

    @Override
    public String getText(final Object element) {
        if (element instanceof IModel) {
            return ((IModel)element).getName();
        } else if (element instanceof IDataElement) {
            return getStringFromDataElement((IDataElement)element);
        }
        return getStrignFromOtherElement(element);
    }

    /**
     * should be override if necessary
     * 
     * @param element
     * @return
     */
    protected String getStrignFromOtherElement(Object element) {
        return StringUtils.EMPTY;
    }

    /**
     * @param element
     * @return
     */
    protected String getStringFromDataElement(IDataElement element) {
        String name = element.getName();
        return name != null ? name : element.toString();
    }

    /**
     * The <code>LabelProvider</code> implementation of this <code>ILabelProvider</code> method
     * returns <code>null</code>. Subclasses may override.
     */
    @Override
    public Image getImage(final Object element) {
        if (element instanceof IModel) {
            IModel model = (IModel)element;
            return IconManager.getInstance().getImage(model.getType());
        } else if (element instanceof IDataElement) {
            IDataElement dataElement = (IDataElement)element;
            INodeType type = dataElement.getNodeType();
            return IconManager.getInstance().getImage(type);
        }
        return null;
    }
}
