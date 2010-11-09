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

package org.amanzi.neo.services.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.neo4j.graphdb.Node;

/**
 * Event for show view with prepare information.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ShowPreparedViewEvent extends ShowViewEvent {
    
    private List<Node> nodes;
    
    /**
     * Constructor.
     * @param aView String
     * @param aNodes Collection of Nodes (information for prepare)
     */
    public ShowPreparedViewEvent(String aView, Collection<Node> aNodes) {
        super(aView, UpdateViewEventType.SHOW_PREPARED_VIEW);
        nodes = new ArrayList<Node>(aNodes);
    }
    
    /**
     * Constructor.
     * @param aView String
     * @param aNode Node (information for prepare)
     */
    public ShowPreparedViewEvent(String aView, Node aNode) {
        super(aView, UpdateViewEventType.SHOW_PREPARED_VIEW);
        nodes = Collections.singletonList(aNode);
    }
    
    /**
     * @return Returns the nodes.
     */
    public List<Node> getNodes() {
        return nodes;
    }

}
