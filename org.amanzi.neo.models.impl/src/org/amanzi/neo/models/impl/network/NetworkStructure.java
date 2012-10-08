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

package org.amanzi.neo.models.impl.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.amanzi.neo.models.network.INetworkStructure;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkStructure implements INetworkStructure {

    private Map<Integer, NetworkElementType> structure;

    public NetworkStructure() {
        structure = new TreeMap<Integer, NetworkElementType>();
        structure.put(NetworkElementType.NETWORK.getDepth(), NetworkElementType.NETWORK);
    }

    public void initiailizeFromArray(String[] types) {
        for (String structuredType : types) {
            for (NetworkElementType type : NetworkElementType.getGeneralNetworkElements()) {
                if (structuredType.equals(type.getId())) {
                    structure.put(type.getDepth(), type);
                }
            }
        }
    }

    @Override
    public Collection<NetworkElementType> getUnderlineElements(INodeType root) {
        List<NetworkElementType> undrlines = new ArrayList<NetworkElementType>();
        NetworkElementType currenType = NetworkElementType.valueOf(root.getId().toUpperCase());
        if (currenType == null) {
            return undrlines;
        }
        for (NetworkElementType type : NetworkElementType.getGeneralNetworkElements()) {
            NetworkElementType existed = structure.get(type.getDepth());
            if (existed != null && type.getDepth() > currenType.getDepth()) {
                if (currenType == NetworkElementType.NETWORK && existed == NetworkElementType.SECTOR) {
                    continue;
                }
                undrlines.add(type);
            }
        }
        return undrlines;
    }

    public void addItem(INodeType type) {
        NetworkElementType networkType = NetworkElementType.valueOf(type.getId().toUpperCase());
        if (networkType != null) {
            structure.put(networkType.getDepth(), networkType);
        }
    }

    public String[] getStructuredArray() {
        String[] simpleType = new String[structure.size()];
        int index = 0;
        for (NetworkElementType type : structure.values()) {
            simpleType[index] = type.getId();
            index++;
        }
        return simpleType;

    }

    @Override
    public boolean isInUderline(INodeType type, NetworkElementType expectedType) {
        return getUnderlineElements(type).contains(expectedType);
    }
}
