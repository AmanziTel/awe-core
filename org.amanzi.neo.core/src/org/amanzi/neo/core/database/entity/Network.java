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

package org.amanzi.neo.core.database.entity;


import org.amanzi.neo.services.INeoConstants;
import org.neo4j.graphdb.Node;
import org.neo4j.neoclipse.property.NodeTypes;

/**
 * <p>
 * Network wrapper
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class Network extends DataRoot{


    /**
     * Instantiates a new network.
     */
    public Network() {
        super();
    }


    /**
     * Instantiates a new network.
     *
     * @param node the node
     * @param service the service
     */
    public Network(Node node, NeoDataService service) {
        super(node, service);
    }
@Override
void create(NeoDataService service) {
    super.create(service);
    setPropertyValue(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NETWORK.getId());
}

}
