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
 * <p>
 * Vault handle information by all statistic for one key
 * </p>
 * .
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class Vault {

    /** The key. */
    private final String key;

    /** The property map. */
    private HashMap<String, NodeTypeVault> propertyMap = new HashMap<String, NodeTypeVault>();

    private boolean isChanged;

    private Node vaultNode;

    static final TraversalDescription PROPERTYS = Traversal.description().depthFirst().relationships(StatisticRelationshipTypes.PROPERTIES, Direction.OUTGOING)
            .uniqueness(Uniqueness.NONE).filter(Traversal.returnAllButStartNode()).prune(Traversal.pruneAfterDepth(1));

    private Node parent;

    private long totalCount;

    /**
     * Instantiates a new vault.
     * 
     * @param key the key
     */
    public Vault(String key) {
        super();
        this.key = key;
        isChanged = false;
    }

    /**
     * Register property.
     * 
     * @param nodeType the node type
     * @param propertyName the property name
     * @param klass the klass
     * @param rule the rule
     * @return true, if successful
     */
    public boolean registerProperty(String nodeType, String propertyName, Class klass, ChangeClassRule rule) {
        NodeTypeVault propertySet = getPropertysForType(nodeType);
        return propertySet.registerProperty(propertyName, klass, rule);
    }

    /**
     * Adds the value.
     * 
     * @param nodeType the node type
     * @param propertyName the property name
     * @param value the value
     * @param count 
     * @return true, if successful
     */
    public boolean addValue(String nodeType, String propertyName, Object value, int count) {
        PropertyStatistics propStat = getPropertyStatistic(nodeType, propertyName);
        return propStat.addNewValue(value,count);
    }

    /**
     * Gets the property statistic.
     * 
     * @param nodeType the node type
     * @param propertyName the property name
     * @return the property statistic
     */
    private PropertyStatistics getPropertyStatistic(String nodeType, String propertyName) {
        NodeTypeVault propertySet = getPropertysForType(nodeType);
        return propertySet.getPropertyStatistic(propertyName);
    }

    /**
     * Gets the property.
     * 
     * @param propertySet the property set
     * @param propertyName the property name
     * @return the property
     */
    private PropertyStatistics getProperty(Map<String, PropertyStatistics> propertySet, String propertyName) {

        PropertyStatistics property = propertySet.get(propertyName);
        if (property == null) {
            property = new PropertyStatistics(propertyName, ChangeClassRule.REMOVE_OLD_CLASS);
            propertySet.put(propertyName, property);
        }
        return property;
    }

    /**
     * Gets the propertys for type.
     * 
     * @param nodeType the node type
     * @return the propertys for type
     */
    private NodeTypeVault getPropertysForType(String nodeType) {
        NodeTypeVault properties = propertyMap.get(nodeType);
        if (properties == null) {
            properties = new NodeTypeVault(nodeType);
            propertyMap.put(nodeType, properties);
        }
        return properties;
    }

    private void loadVault(Node parent, Node vaultNode) {
        clearVault();
        this.parent = parent;
        this.vaultNode = vaultNode;
        totalCount=(Long)vaultNode.getProperty(StatisticProperties.COUNT, 0l);
        propertyMap.putAll(NodeTypeVault.loadNodeTypes(vaultNode));
        isChanged = false;
    }

    /**
     * Clear vault.
     */
    private void clearVault() {
        propertyMap.clear();
        totalCount=0;
    }

    /**
     * Save vault.
     * 
     * @param root the root
     * @param node
     */
    public void saveVault(INeoDbService service, Node statRoot, Node vault) {
        if (isChanged(statRoot)) {
            parent = statRoot;
            Transaction tx = service.beginTx();
            try {
                if (vault == null) {
                    Iterator<Node> iterator = PROPERTYS.filter(new Predicate<Path>() {

                        @Override
                        public boolean accept(Path paramT) {
                            return key.equals(paramT.endNode().getProperty(StatisticProperties.KEY, ""));
                        }
                    }).traverse(parent).nodes().iterator();
                    if (iterator.hasNext()) {
                        vaultNode = iterator.next();
                    } else {
                        vaultNode = service.createNode();
                        vaultNode.setProperty(StatisticProperties.KEY, key);
                        parent.createRelationshipTo(vaultNode, StatisticRelationshipTypes.PROPERTIES);
                    }
                } else {
                    vaultNode = vault;
                }
                vaultNode.setProperty(StatisticProperties.COUNT, totalCount);
                HashSet<Node> treeToDelete = new HashSet<Node>();
                HashSet<NodeTypeVault> savedVault = new HashSet<NodeTypeVault>();
                for (Path path : NodeTypeVault.PROPERTYS.traverse(vaultNode)) {
                    String key = (String)path.endNode().getProperty(StatisticProperties.KEY);
                    NodeTypeVault nodeTypeV = propertyMap.get(key);
                    if (nodeTypeV == null) {
                        treeToDelete.add(path.endNode());
                    } else {
                        nodeTypeV.saveVault(service, vaultNode, path.endNode());
                        savedVault.add(nodeTypeV);
                    }
                }
                for (Node node : treeToDelete) {
                    NeoServiceFactory.getInstance().getDatasetService().deleteTree(service, node);
                }
                for (NodeTypeVault nodeTypeV : propertyMap.values()) {
                    if (!savedVault.contains(nodeTypeV)) {
                        nodeTypeV.saveVault(service, vaultNode, null);
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
     * Hash code.
     * 
     * @return the int
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    /**
     * Equals.
     * 
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vault other = (Vault)obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    public static Map<String, Vault> loadVaults(Node statRoot) {
        Map<String, Vault> result = new HashMap<String, Vault>();
        for (Path path : PROPERTYS.traverse(statRoot)) {
            String key = (String)path.endNode().getProperty(StatisticProperties.KEY);
            Vault vault = new Vault(key);
            result.put(key, vault);
            vault.loadVault(statRoot, path.endNode());
        }
        return result;
    }

    public boolean isChanged(Node statRoot) {
        if (isChanged || parent == null || !statRoot.equals(parent)) {
            return true;
        }
        for (NodeTypeVault vault : propertyMap.values()) {
            if (vault.isChanged(vaultNode)) {
                return true;
            }
        }
        return false;
    }


    public PropertyStatistics findProperty(String nodeType, String key) {
        NodeTypeVault vault=propertyMap.get(nodeType);
        if (vault==null){
            return null;
        }
        return vault.findProperty(key);
    }


    public void increaseTypeCount(String nodeType, long count) {
        increaseTotalCount(count);
        getPropertysForType(nodeType).increaseTotalCount(count);
    }


    private void increaseTotalCount(long count) {
        isChanged=true;
        totalCount+=count;
    }


    public long getTotalCount(String nodeType) {
        NodeTypeVault vault=propertyMap.get(nodeType);
        if (vault==null){
            return 0;
        }
        return vault.getTotalCount();
    }
}