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

package org.amanzi.awe.nem.ui.utils;

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class MenuUtils {

    private static final class MenuUtilsInstanceHolder {
        private static final MenuUtils UTILS = new MenuUtils();
    }

    public static MenuUtils getInstance() {
        return MenuUtilsInstanceHolder.UTILS;
    }

    public INetworkModel getModelFromTreeItem(ITreeItem< ? , ? > item) {
        if (item.getParent() == null && item.getChild() instanceof INetworkModel) {
            return (INetworkModel)item.getChild();
        } else if (item.getParent() instanceof INetworkModel && item.getChild() instanceof IDataElement) {
            return (INetworkModel)item.getParent();
        }
        return null;
    }

    public IDataElement getElementFromTreeItem(ITreeItem< ? , ? > item) {
        if (item.getParent() != null && item.getChild() != null) {
            return (IDataElement)item.getChild();
        }
        return null;
    }

    /**
     * @param model
     * @param element
     * @return
     */
    public INodeType getType(INetworkModel model, IDataElement element) {
        INodeType type;
        if (element == null) {
            type = model.getType();
        } else {
            type = element.getNodeType();
        }
        return type;
    }
}
