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

package org.amanzi.awe.nem.ui.testers;

import java.util.Arrays;

import org.amanzi.awe.nem.managers.structure.NetworkStructureManager;
import org.amanzi.awe.nem.ui.utils.MenuUtils;
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
public enum MenuProperties {

    IS_COPYABLE("isCopyable"), IS_DELETABLE("isDeletable"), IS_ADDETABLE("isAddetable"), IS_EXPORTABLE("isExportable");

    public static MenuProperties findByName(final String parameterName) {
        for (final MenuProperties item : values()) {
            if (item.getName().equals(parameterName)) {
                return item;
            }
        }
        return null;
    }

    private String name;

    private MenuProperties(final String parameterName) {
        this.name = parameterName;
    }

    public boolean check(final INetworkModel model, final IDataElement element) {
        switch (this) {
        case IS_ADDETABLE:
            if (model == null) {
                return false;
            }
            final INodeType type = MenuUtils.getType(model, element);
            return NetworkStructureManager.getInstance().getUnderlineElements(type, Arrays.asList(model.getNetworkStructure()))
                    .size() > 0;
        case IS_DELETABLE:
        case IS_COPYABLE:
            return model != null ? true : false;
        case IS_EXPORTABLE:
            return model != null && element == null;
        default:
            break;
        }
        return false;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

}
