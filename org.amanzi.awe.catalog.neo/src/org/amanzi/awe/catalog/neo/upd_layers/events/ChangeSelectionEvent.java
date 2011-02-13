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

package org.amanzi.awe.catalog.neo.upd_layers.events;

import java.util.Collection;

import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ChangeSelectionEvent extends UpdateLayerEvent {
    
    private Collection<Node> selected;

    /**
     * Constructor.
     * @param aGisNode
     * @param selectedNodes
     */
    public ChangeSelectionEvent(Node aGisNode, Collection<Node> selectedNodes) {
        this(UpdateLayerEventTypes.CHANGE_SELECTION,aGisNode,selectedNodes);
    }
    
    /**
     * Constructor.
     * @param aType
     * @param aGisNode
     * @param selectedNodes
     */
    public ChangeSelectionEvent(UpdateLayerEventTypes aType, Node aGisNode, Collection<Node> selectedNodes){
        super(aType,aGisNode);
        selected = selectedNodes;
    }
    
    /**
     * @return Returns the selected.
     */
    public Collection<Node> getSelected() {
        return selected;
    }

}
