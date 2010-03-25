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

package org.amanzi.neo.core.database.services.events;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Node;

/**
 * Event for Drill Down (update all views where used concrete nodes) 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class UpdateDrillDownEvent extends UpdateViewEvent {
    
    private List<Node> nodes;
    private String source;
    

    /**
     * Constructor.
     * @param aNodes
     */
    public UpdateDrillDownEvent(List<Node> aNodes, String aSource) {
        super(UpdateViewEventType.DRILL_DOWN);
        nodes = aNodes;
        source = aSource;
    }
    
    /**
     * Constructor.
     * @param aNode
     */
    public UpdateDrillDownEvent(Node aNode, String aSource) {
        super(UpdateViewEventType.DRILL_DOWN);
        nodes = new ArrayList<Node>(1);
        nodes.add(aNode);
        source = aSource;
    }
    
    /**
     * @return Returns nodes.
     */
    public List<Node> getNodes() {
        return nodes;
    }
    
    /**
     * @return Returns the source.
     */
    public String getSource() {
        return source;
    }

}
