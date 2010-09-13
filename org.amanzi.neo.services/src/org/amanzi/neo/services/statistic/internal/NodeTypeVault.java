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

package org.amanzi.neo.services.statistic.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.amanzi.neo.db.manager.INeoDbService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NodeTypeVault {
    private final String nodeType;
    public static final TraversalDescription PROPERTYS=Traversal.description().depthFirst().relationships(StatisticRelationshipTypes.NODE_TYPES, Direction.OUTGOING).uniqueness(Uniqueness.NONE).filter(Traversal.returnAllButStartNode()).prune(Traversal.pruneAfterDepth( 1));

    private HashMap<String,PropertyStatistics> propertyMap=new HashMap<String,PropertyStatistics>();
 private boolean isChanged;
private Node parent;
private Node vaultNode;
    /**
     * @param nodeType
     */
    public NodeTypeVault(String nodeType) {
        super();
        this.nodeType = nodeType;
        isChanged=false;
    }

    /**
     *
     * @param propertySet
     * @param propertyName
     * @return
     */
    public PropertyStatistics getPropertyStatistic(String propertyName) {
        PropertyStatistics result = propertyMap.get(propertyName);
        if (result==null){
            result=new PropertyStatistics(propertyName);
            propertyMap.put(propertyName, result);
        }
        return result;
    }


    public boolean registerProperty(String propertyName, Class klass, ChangeClassRule rule) {
        return getPropertyStatistic(propertyName).register(klass,rule);
    }

    /**
     *
     * @param vaultNode
     * @return
     */
    public static Map< String, NodeTypeVault> loadNodeTypes(Node vaultNode) {
        Map<String, NodeTypeVault> result=new HashMap<String, NodeTypeVault>();
        for (Path path:PROPERTYS.traverse(vaultNode)){
            String key= (String)path.endNode().getProperty(StatisticProperties.KEY);
            NodeTypeVault vault=new NodeTypeVault(key);
            result.put(key, vault);
            vault.loadVault(vaultNode,path.endNode());
         }
        return result;
    }

    /**
     *
     * @param vaultNode
     * @param endNode
     */
    private void loadVault(Node parent, Node vaultNode) {
        clearVault();
        this.parent = parent;
        this.vaultNode = vaultNode;
        propertyMap.putAll(PropertyStatistics.loadProperties(vaultNode));
        isChanged=false;
    }

    /**
     *
     */
    private void clearVault() {
        propertyMap.clear();
    }

    /**
     *
     * @param vaultNode2
     * @return
     */
    public boolean isChanged(Node vaultRoot) {
        if (isChanged || parent == null || !vaultRoot.equals(parent)) {
            return true;
        }
        for (PropertyStatistics prop : propertyMap.values()) {
            if (prop.isChanged(vaultNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param service
     * @param vaultNode2
     * @param endNode
     */
    public void saveVault(INeoDbService service, Node parentNode, Node nodeTypeVault) {
        if (isChanged(parentNode)) {
            parent = parentNode;
            Transaction tx = service.beginTx();
            try {
                if (nodeTypeVault == null) {
                    Iterator<Node> iterator = PROPERTYS.filter(new Predicate<Path>() {

                        @Override
                        public boolean accept(Path paramT) {
                            return nodeType.equals(paramT.endNode().getProperty(StatisticProperties.KEY, ""));
                        }
                    }).traverse(parent).nodes().iterator();
                    if (iterator.hasNext()) {
                        vaultNode = iterator.next();
                    } else {
                        vaultNode = service.createNode();
                        vaultNode.setProperty(StatisticProperties.KEY, nodeType);
                        parent.createRelationshipTo(vaultNode, StatisticRelationshipTypes.NODE_TYPES);
                    }
                } else {
                    this.vaultNode = nodeTypeVault;
                }
                HashSet<Node> treeToDelete = new HashSet<Node>();
                HashSet<PropertyStatistics> savedVault = new HashSet<PropertyStatistics>();
                for (Path path : NodeTypeVault.PROPERTYS.traverse(vaultNode)) {
                    String key = (String)path.endNode().getProperty(StatisticProperties.KEY);
                    PropertyStatistics propStat = propertyMap.get(key);
                    if (propStat == null) {
                        treeToDelete.add(path.endNode());
                    } else {
                        propStat.save(service, parentNode, path.endNode());
                        savedVault.add(propStat);
                    }
                }
                for (Node node : treeToDelete) {
                    NeoServiceFactory.getInstance().getDatasetService().deleteTree(service, node);
                }
                for (PropertyStatistics property : propertyMap.values()) {
                    if (!savedVault.contains(property)) {
                        property.save(service, vaultNode, null);
                    }
                }

                tx.success();
            } finally {
                tx.finish();
            }
        }
        isChanged = false;
 
    }

    /**
     *
     * @param key
     * @return
     */
    public PropertyStatistics findProperty(String key) {
        return propertyMap.get(key);
    }
    
}
