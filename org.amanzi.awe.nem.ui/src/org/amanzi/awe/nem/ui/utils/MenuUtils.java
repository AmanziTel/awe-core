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

import org.amanzi.awe.ui.dto.IUIItemNew;
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
public final class MenuUtils {

    private MenuUtils() {

    }

    public static INetworkModel getModelFromItem(final IUIItemNew item) {
        INetworkModel networkModel = item.castParent(INetworkModel.class);

        if (networkModel == null) {
            networkModel = item.castChild(INetworkModel.class);
        }

        return networkModel;
    }

    public static IDataElement getElementFromItem(final IUIItemNew item) {
        return item.castChild(IDataElement.class);
    }

    /**
     * @param model
     * @param element
     * @return
     */
    public static INodeType getType(final INetworkModel model, final IDataElement element) {
        INodeType type;
        if (element == null) {
            type = model.getType();
        } else {
            type = element.getNodeType();
        }
        return type;
    }
}
