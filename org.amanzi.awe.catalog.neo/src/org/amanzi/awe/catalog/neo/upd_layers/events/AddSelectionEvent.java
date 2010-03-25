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
public class AddSelectionEvent extends ChangeSelectionEvent {

    /**
     * @param aGisNode
     * @param selectedNodes
     */
    public AddSelectionEvent(Node aGisNode, Collection<Node> selectedNodes) {
        super(UpdateLayerEventTypes.ADD_SELECTION, aGisNode, selectedNodes);
    }
    
    

}
