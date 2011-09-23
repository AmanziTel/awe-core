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

package org.amanzi.neo.services;

import java.util.HashMap;
import java.util.HashSet;

import org.amanzi.neo.db.manager.INeoDbService;
import org.amanzi.neo.services.statistic.internal.StatisticProperties;
import org.amanzi.neo.services.statistic.internal.StatisticRelationshipTypes;
import org.amanzi.neo.services.statistic.internal.Vault;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kruglik_A
 * @since 1.0.0
 */
public class StatisticService extends AbstractService {
    
    public Node findStatRoot(Node root) {
        Node statRoot;
        Relationship rel = root.getSingleRelationship(StatisticRelationshipTypes.STATISTIC_PROP, Direction.OUTGOING);
        if (rel == null) {
            statRoot = null;
        }
        else {
            statRoot=rel.getEndNode();
        }
        return statRoot;
    }
    
    public Node findOrCreateStatRoot(Node root) {
        Node statRoot = findStatRoot(root);
        if (statRoot == null) {
            Transaction tx = databaseService.beginTx();
            try {
                statRoot = databaseService.createNode();
                statRoot.setProperty(StatisticProperties.KEY, "PROPERTIES");
                root.createRelationshipTo(statRoot, StatisticRelationshipTypes.STATISTIC_PROP);
                tx.success();
            } finally {
                tx.finish();
            }
        }
        return statRoot;
    }
    
    public void saveStatistic(Node statRoot, Node root, long totalCount, 
            TraversalDescription traversalDescription, HashMap<String, Vault> vaults) {
        
        Transaction tx = databaseService.beginTx();
        try {
            statRoot = findOrCreateStatRoot(root);

            statRoot.setProperty(StatisticProperties.COUNT, totalCount);
            HashSet<Node> treeToDelete = new HashSet<Node>();
            HashSet<Vault> savedVault = new HashSet<Vault>();
            
            for (Path path : traversalDescription.traverse(statRoot)) {
                String key = (String)path.endNode().getProperty(StatisticProperties.KEY);
                Vault vault = vaults.get(key);
                if (vault == null) {
                    treeToDelete.add(path.endNode());
                }
                else {
                    vault.saveVault((INeoDbService)databaseService,statRoot,path.endNode());
                    savedVault.add(vault);
                }
            }   
            for (Node node:treeToDelete) {
                NeoServiceFactory.getInstance().getDatasetService().deleteTree((INeoDbService)databaseService, node);
            }
            for (Vault vault:vaults.values()) {
                if (!savedVault.contains(vault)) {
                    vault.saveVault((INeoDbService)databaseService,statRoot, null);
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
    }

    

}
