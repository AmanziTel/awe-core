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

package org.amanzi.neo.models.network;

import org.amanzi.neo.models.network.INetworkModel.INetworkElementType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public enum NetworkElementType implements INetworkElementType {
    NETWORK, CITY, MSC, BSC, SITE, SECTOR;

    private static NetworkElementType[] GENERAL_NETWORK_ELEMENT = ArrayUtils.subarray(values(), NETWORK.ordinal() + 1,
            values().length);

    @Override
    public String getId() {
        return NodeTypeUtils.getTypeId(this);
    }

    public static NetworkElementType[] getGeneralNetworkElements() {
        return GENERAL_NETWORK_ELEMENT;
    }

}
