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

import org.amanzi.neo.core.utils.Pair;
import org.eclipse.core.runtime.IAdaptable;
import org.neo4j.api.core.Node;

/**
 * <p>
 * Call statistic Selection node
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class StatisticSelectionNode implements IAdaptable {
    Node mainNode;// main node
    Node clarifyingNode;// clarifying node

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == Node.class) {
            return mainNode;
        } else if (adapter == Pair.class) {
            return new Pair<Node, Node>(mainNode, clarifyingNode);
        }
        return null;
    }

    /**
     * constructor
     * 
     * @param mainNode - main node
     * @param clarifyingNode - clarifying node (periods)
     */
    public StatisticSelectionNode(Node mainNode, Node clarifyingNode) {
        super();
        this.mainNode = mainNode;
        this.clarifyingNode = clarifyingNode;
    }

    /**
     * @return Returns the mainNode.
     */
    public Node getMainNode() {
        return mainNode;
    }

    /**
     * @return Returns the clarifyingNode.
     */
    public Node getClarifyingNode() {
        return clarifyingNode;
    }

}
