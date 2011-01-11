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

import java.util.ArrayList;
import java.util.Collection;
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
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Provide work with part (Node type) of statistic structure
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class NodeTypeVault {
    
    /** The node type. */
    private final String nodeType;
    
    /** The Constant PROPERTYS. */
    public static final TraversalDescription PROPERTYS = Traversal.description().depthFirst().relationships(StatisticRelationshipTypes.NODE_TYPES, Direction.OUTGOING)
            .uniqueness(Uniqueness.NONE).filter(Traversal.returnAllButStartNode()).prune(Traversal.pruneAfterDepth(1));

    /** The property map. */
    private HashMap<String, PropertyStatistics> propertyMap = new HashMap<String, PropertyStatistics>();
    
    /** The is changed. */
    private boolean isChanged;
    
    /** The parent. */
    private Node parent;
    
    /** The vault node. */
    private Node vaultNode;
    
    /** The total count. */
    private long totalCount;

    /**
     * Instantiates a new node type vault.
     *
     * @param nodeType the node type
     */
    public NodeTypeVault(String nodeType) {
        super();
        this.nodeType = nodeType;
        isChanged = false;
    }

    /**
     * Gets the property statistic.
     *
     * @param propertyName the property name
     * @return the property statistic
     */
    public PropertyStatistics getPropertyStatistic(String propertyName) {
        PropertyStatistics result = propertyMap.get(propertyName);
        if (result == null) {
            result = new PropertyStatistics(propertyName);
            propertyMap.put(propertyName, result);
        }
        return result;
    }

    /**
     * Register property.
     *
     * @param propertyName the property name
     * @param klass the klass
     * @param rule the rule
     * @return true, if successful
     */
    public boolean registerProperty(String propertyName, Class klass, ChangeClassRule rule) {
        return getPropertyStatistic(propertyName).register(klass, rule);
    }

    /**
     * Load node types.
     *
     * @param vaultNode the vault node
     * @return the map
     */
    public static Map<String, NodeTypeVault> loadNodeTypes(Node vaultNode) {
        Map<String, NodeTypeVault> result = new HashMap<String, NodeTypeVault>();
        for (Path path : PROPERTYS.traverse(vaultNode)) {
            String key = (String)path.endNode().getProperty(StatisticProperties.KEY);
            NodeTypeVault vault = new NodeTypeVault(key);
            result.put(key, vault);
            vault.loadVault(vaultNode, path.endNode());
        }
        return result;
    }

    /**
     * Load vault.
     *
     * @param parent the parent
     * @param vaultNode the vault node
     */
    private void loadVault(Node parent, Node vaultNode) {
        clearVault();
        this.parent = parent;
        this.vaultNode = vaultNode;
        totalCount = (Long)vaultNode.getProperty(StatisticProperties.COUNT, 0l);
        propertyMap.putAll(PropertyStatistics.loadProperties(vaultNode));
        isChanged = false;
    }

    /**
     * Clear vault.
     */
    private void clearVault() {
        propertyMap.clear();
    }

    /**
     * Checks if is changed.
     *
     * @param vaultRoot the vault root
     * @return true, if is changed
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
     * Save vault.
     *
     * @param service the service
     * @param parentNode the parent node
     * @param nodeTypeVault the node type vault
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
                vaultNode.setProperty(StatisticProperties.COUNT, totalCount);
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
     * Find property.
     *
     * @param key the key
     * @return the property statistics
     */
    public PropertyStatistics findProperty(String key) {
        return propertyMap.get(key);
    }


    /**
     * Increase total count.
     *
     * @param count the count
     */
    public void increaseTotalCount(long count) {
        isChanged = true;
        totalCount += count;
    }


    /**
     * Gets the total count.
     *
     * @return the total count
     */
    public long getTotalCount() {
        return totalCount;
    }


    /**
     * Gets the property name collection.
     *
     * @param comparable the comparable
     * @return the property name collection
     */
    public Collection<String> getPropertyNameCollection(Comparable<Class> comparable) {
        Collection<String> result=new ArrayList<String>();
        for (Map.Entry<String, PropertyStatistics> entry:propertyMap.entrySet()){
            if (comparable==null||comparable.compareTo(entry.getValue().getKlass())==0){
                result.add(entry.getKey());
            }
        }
        return result;
    }

}
