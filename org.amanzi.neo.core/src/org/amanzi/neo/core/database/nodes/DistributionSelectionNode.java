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

package org.amanzi.neo.core.database.nodes;

import org.eclipse.core.runtime.IAdaptable;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Distribution selection node.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class DistributionSelectionNode  implements IAdaptable {
    
    private Node selected;
    
    /**
     * Constructor.
     * @param selected Node
     */
    public DistributionSelectionNode(Node selected) {
        this.selected = selected;
    }
    
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == Node.class) {
            return selected;
        } 
        return null;
    }
    
    /**
     * @return Returns the selected.
     */
    public Node getSelected() {
        return selected;
    }

}
