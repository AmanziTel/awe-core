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

package org.amanzi.neo.services.ui.providers;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.ui.icons.IconManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * represent common view label provider actually used in NetworkTreeView and ProjectExplorerView
 * 
 * @author Vladislav_Kondratenko
 */
public class CommonViewLabelProvider extends LabelProvider {
    /**
     * Constructor. Gets an instance of IconManager
     * 
     * @param viewer of this LabelProvider
     */
    public CommonViewLabelProvider(Viewer viewer) {

    }

    @Override
    public String getText(Object element) {
        if (element instanceof IModel) {
            return ((IModel)element).getName();
        } else if (element instanceof IDataElement) {
            return (String)((IDataElement)element).get(AbstractService.NAME);
        }
        return null;
    }

    /**
     * The <code>LabelProvider</code> implementation of this <code>ILabelProvider</code> method
     * returns <code>null</code>. Subclasses may override.
     */
    public Image getImage(Object element) {
        if (element instanceof IModel) {
            IModel model = (IModel)element;
            return IconManager.getInstance().getImage(model.getType());
        } else if (element instanceof IDataElement) {
            IDataElement dataElement = (IDataElement)element;
            INodeType type = NodeTypeManager.getType(dataElement);
            return IconManager.getInstance().getImage(type);
        }
        return null;
    }
}
