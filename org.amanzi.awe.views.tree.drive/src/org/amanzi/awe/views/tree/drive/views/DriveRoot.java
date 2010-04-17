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
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
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
    public DriveRoot(NeoServiceProvider serviceProvider) {
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
            LinkedHashMap<String, Node> datasets = NeoUtils.getAllDatasetNodes(service);
            // Node reference = service.getReferenceNode();
            // Traverser rootDriveTraverse = reference.traverse(Order.DEPTH_FIRST, new
            // StopEvaluator() {
            //
            // @Override
            // public boolean isStopNode(TraversalPosition currentPos) {
            // return currentPos.depth() >= 3;
            // }
            // }, new ReturnableEvaluator() {
            //
            // @Override
            // public boolean isReturnableNode(TraversalPosition currentPos) {
            // Relationship lastRelationshipTraversed = currentPos.lastRelationshipTraversed();
            // return currentPos.depth() > 1
            // && (NeoUtils.getNodeType(lastRelationshipTraversed.getStartNode(),
            // "").equals(INeoConstants.AWE_PROJECT_NODE_TYPE) ||
            // (NeoUtils.getNodeType(lastRelationshipTraversed.getStartNode(),
            // "").equals(INeoConstants.DATASET_TYPE_NAME)))
            // &&
            // (lastRelationshipTraversed.getEndNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,
            // "").equals(
            // INeoConstants.DATASET_TYPE_NAME) ||
            // (lastRelationshipTraversed.getEndNode().getProperty(
            // INeoConstants.PROPERTY_TYPE_NAME, "").equals(INeoConstants.FILE_TYPE_NAME)));
            // }
            // }, SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING,
            // NetworkRelationshipTypes.CHILD, Direction.OUTGOING,
            // GeoNeoRelationshipTypes.VIRTUAL_DATASET, Direction.OUTGOING);
            int nextNum = number+1;
            for (Node node : datasets.values()) {
                driveNodes.add(new DriveNeoNode(node,nextNum++));
                
                //Lagutko, 3.02.2010, if we have a AMS Call Dataset than we should add Analyzis to tree
                if (NeoUtils.getDatasetType(node, service) == DriveTypes.AMS_CALLS) {
                    Iterator<Relationship> analyzis = node.getRelationships(ProbeCallRelationshipType.CALL_ANALYZIS, Direction.OUTGOING).iterator();
                    
                    while (analyzis.hasNext()) {
                        driveNodes.add(new CallAnalyzisNeoNode(analyzis.next().getEndNode(),nextNum++));
                    }
                }
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
