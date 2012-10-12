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

package org.amanzi.awe.nem.managers.properties;

import org.amanzi.neo.models.network.INetworkModel.INetworkElementType;
import org.amanzi.neo.nodetypes.DynamicNodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DynamicNetworkType extends DynamicNodeType implements INetworkElementType {

    /**
     * @param type
     */
    public DynamicNetworkType(String type) {
        super(type);
    }

}
