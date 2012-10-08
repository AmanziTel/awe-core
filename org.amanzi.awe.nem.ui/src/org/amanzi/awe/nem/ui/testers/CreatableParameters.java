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

import org.amanzi.neo.models.network.NetworkElementType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum CreatableParameters {

    CREATE_MSC("isMSCAllow", NetworkElementType.MSC), CREATE_BSC("isBSCAllow", NetworkElementType.BSC), CREATE_SITE("isSiteAllow",
            NetworkElementType.SITE), CREATE_SECTOR("isSectorAllow", NetworkElementType.SECTOR);

    private String name;
    private NetworkElementType type;

    private CreatableParameters(String parameterName, NetworkElementType type) {
        this.name = parameterName;
        this.type = type;
    }

    public static CreatableParameters findByName(String parameterName) {
        for (CreatableParameters item : values()) {
            if (item.getName().equals(parameterName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the type.
     */
    public NetworkElementType getType() {
        return type;
    }
}
