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

import org.neo4j.graphdb.Node;

/**
 * <p>
 * Event for update correlation list
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class NewCorrelationEvent extends UpdateViewEvent {
    private Node networkNode;
    private Node driveNode;

    /**
     * @param aType
     * @param networkNode
     * @param driveNode
     */
    public NewCorrelationEvent(Node networkNode, Node driveNode) {
        super(UpdateViewEventType.NEW_CORRELATION);
        this.networkNode = networkNode;
        this.driveNode = driveNode;
    }

    /**
     * @return
     */
    public Node getNetworkNode() {
        return networkNode;
    }

    /**
     * @return Returns the driveNode.
     */
    public Node getDriveNode() {
        return driveNode;
    }

}
