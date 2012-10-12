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

package org.amanzi.awe.nem.managers.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class NetworkStructureManager {
    private final List<INodeType> requiredTypes = new ArrayList<INodeType>() {
        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        {
            add(NetworkElementType.NETWORK);
            add(NetworkElementType.SITE);
            add(NetworkElementType.SECTOR);
        }
    };

    private static class NetworkStructureManagerInstanceHolder {
        private static final NetworkStructureManager INSTANCE = new NetworkStructureManager();
    }

    public static final NetworkStructureManager getInstance() {
        return NetworkStructureManagerInstanceHolder.INSTANCE;
    }

    public Collection<INodeType> getUnderlineElements(INodeType root, List<INodeType> structure) {
        List<INodeType> undrlines = new ArrayList<INodeType>();
        int index = structure.indexOf(root) + 1;
        int siteIndex = getElementIndex(structure, NetworkElementType.SITE);

        for (int i = index; i != structure.size(); i++) {
            INodeType existed = structure.get(i);
            System.out.println(existed);
            if (i == siteIndex && rootIs(root, NetworkElementType.SITE)) {
                INodeType lastElement = structure.get(structure.size());
                undrlines.add(existed);
                undrlines.add(lastElement);
                break;
            } else if (i == siteIndex && !rootIs(root, NetworkElementType.SITE)) {
                undrlines.add(existed);
                break;
            }
            undrlines.add(existed);

        }
        return undrlines;
    }

    /**
     * @param checkableType
     * @return
     */
    private boolean rootIs(INodeType rootNode, INodeType checkableType) {
        return rootNode.getId().equals(checkableType.getId());
    }

    /**
     * @param structure
     * @return
     */
    private int getElementIndex(List<INodeType> structure, INodeType searchableType) {
        int i = 0;
        for (INodeType type : structure) {
            if (type.getId().equals(searchableType.getId())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public List<INodeType> getRequiredNetworkElements() {
        return requiredTypes;
    }

}
