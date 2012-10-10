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

package org.amanzi.awe.nem;

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

    public Collection<String> getUnderlineElements(INodeType root, List<String> structure) {
        List<String> undrlines = new ArrayList<String>();
        int index = structure.indexOf(root.getId()) + 1;
        int siteIndex = structure.indexOf(NetworkElementType.SITE.getId());

        for (int i = index; i != structure.size(); i++) {
            String existed = structure.get(i);
            if (i == siteIndex && root.equals(NetworkElementType.SITE.getId())) {
                String lastElement = structure.get(structure.indexOf(structure.size() - 1));
                undrlines.add(existed);
                undrlines.add(lastElement);
                break;
            } else if (i == siteIndex && !root.equals(NetworkElementType.SITE.getId())) {
                undrlines.add(existed);
                break;
            }
            undrlines.add(existed);

        }
        return undrlines;
    }

    public boolean isInUderline(INodeType type, NetworkElementType expectedType, List<String> structure) {
        return getUnderlineElements(type, structure).contains(expectedType.getId());
    }

    public List<INodeType> getRequiredNetworkElements() {
        return requiredTypes;
    }

}
