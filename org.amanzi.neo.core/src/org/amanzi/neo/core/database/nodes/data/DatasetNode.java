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

package org.amanzi.neo.core.database.nodes.data;

import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Dataset root node wrapper
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DatasetNode extends DataRootNode {
    /**
     * constructor
     * 
     * @param node - wrapped node
     */
    public DatasetNode(Node node) {
        super(node);
    }

    /**
     * Gets type of dataset
     * 
     * @return DriveTypes
     */
    public DriveTypes getDriveType() {
        return DriveTypes.getNodeType(node, null);
    }

    /**
     * Set drive types
     * 
     * @param networkType - new type
     */
    public void setNetworkType(DriveTypes driveType) {
        driveType.setTypeToNode(getUnderlyingNode(), null);
    }

    @Override
    public NodeTypes getNodeType() {
        return NodeTypes.DATASET;
    }

}
