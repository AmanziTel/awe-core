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

import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class UpdateLayerEvent {
    
private Node gisNode;
private UpdateLayerEventTypes type;
    
    /**
     * @param aType UpdateViewEventType
     * @param aGisNode Node
     */
    protected UpdateLayerEvent(UpdateLayerEventTypes aType, Node aGisNode) {
        type = aType;
        gisNode = aGisNode;
    }
    
    /**
     * Constructor.
     * @param aGisNode Node
     */     
    public UpdateLayerEvent(Node aGisNode) {
        this(UpdateLayerEventTypes.REFRESH,aGisNode);
    }

    /**
     * @return Returns the gisNode.
     */
    public Node getGisNode() {
        return gisNode;
    }

    /**
     * @return Returns the type.
     */
    public UpdateLayerEventTypes getType() {
        return type;
    }
}
