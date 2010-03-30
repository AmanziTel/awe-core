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

import org.amanzi.neo.core.enums.NetworkTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Probe Network
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ProbeNetwork extends Network{


    public ProbeNetwork() {
        super();
    }


    ProbeNetwork(Node node, NeoDataService service) {
        super(node, service);
    }

    @Override
    void create(NeoDataService service) {
        super.create(service);
        setPropertyValue(NetworkTypes.PROPERTY_NAME, NetworkTypes.PROBE.getId());
    }


}
