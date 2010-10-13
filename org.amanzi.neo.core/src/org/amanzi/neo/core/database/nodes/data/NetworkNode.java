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

import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Network root node wrapper
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NetworkNode extends DataRootNode {

    /**
     * constructor
     * 
     * @param node - wrapped node
     */
    protected NetworkNode(Node node) {
        super(node);
    }
    /**
     * get type of network data
     *
     * @return NetworkTypes
     */
    public NetworkTypes getNetworkType(){
        return NetworkTypes.getNodeType(getUnderlyingNode());
    }
    /**
     * Set network types
     *  @param networkType - new type
     *
     */
    public void setNetworkType(NetworkTypes networkType){
        networkType.setTypeToNode(getUnderlyingNode(),null);
    }
    @Override
    public NodeTypes getNodeType() {
        return NodeTypes.NETWORK;
    }

}
