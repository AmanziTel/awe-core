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
package org.amanzi.awe.views.tree.drive.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.ProbeCallRelationshipType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * Proxy class that provides access for Neo-database
 * 
 * @since 1.0.0
 */
public class DriveRoot extends Root {

    /**
     * Constructor
     * 
     * @param serviceProvider service Provider
     */
    public DriveRoot(NeoServiceProviderUi serviceProvider) {
        super(serviceProvider);
    }

    /**
     * Returns all DRIVE Nodes of database
     */

    public NeoNode[] getChildren() {
        ArrayList<NeoNode> driveNodes = new ArrayList<NeoNode>();

        GraphDatabaseService service = serviceProvider.getService();

        Transaction transaction = service.beginTx();
        try {

            int nextNum = number+1;
            for (Node node : NeoServiceFactory.getInstance().getDatasetService().getAllDatasetNodes().nodes()) {
                driveNodes.add(new DriveNeoNode(node,nextNum++));
               if(driveNodes.size()>MAX_CHILDREN_COUNT){
                    break;
                }
            }

            transaction.success();
        } finally {
            transaction.finish();
        }

        if (driveNodes.isEmpty()) {
            return NO_NODES;
        } else {
            Collections.sort(driveNodes, new NeoNodeComparator());
            return driveNodes.toArray(NO_NODES);
        }
    }

}
