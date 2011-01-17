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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.db.manager.INeoDbService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;


// TODO: Auto-generated Javadoc
/**
 * <p>
 * Provide work with statistic
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class StatisticHandler {
    /** The vaults. */
    private HashMap<String,Vault>vaults=new HashMap<String, Vault>();
    
    /** The Constant MAX_VALUES_SIZE. */
    public static final int MAX_VALUES_SIZE=100;
    
    /** The is changed. */
    private boolean isChanged=false;
    
    /** The root. */
    private Node root;
    
    /** The total count. */
    private long totalCount;
    
    /** The stat root. */
    private Node statRoot;

    /**
     * Load statistic.
     *
     * @param root the root
     */
    public void loadStatistic(Node root){
        this.root = root;
        clearStatistic();
        
        Relationship rel = root.getSingleRelationship(StatisticRelationshipTypes.STATISTIC_PROP,Direction.OUTGOING);
        if (rel==null){
            return;
        }
        statRoot=rel.getEndNode();
        totalCount=(Long)statRoot.getProperty(StatisticProperties.COUNT, 0l);
        vaults.putAll(Vault.loadVaults(statRoot));
        isChanged=false;
    }

    /**
     * Clear statistic.
     */
    private void clearStatistic() {
        vaults.clear();
        totalCount=0;
    }
    /**
     * Save statistic.
     *
     * @param service the service
     * @param root the root
     */
    public void saveStatistic(INeoDbService service,Node root){
        if (isChanged(root)) {
            this.root=root;
            Transaction tx = service.beginTx();
            try {
                Relationship rel = root.getSingleRelationship(StatisticRelationshipTypes.STATISTIC_PROP,Direction.OUTGOING);
                if (rel==null){
                    statRoot=service.createNode();
                    statRoot.setProperty(StatisticProperties.KEY, "PROPERTIES");
                    root.createRelationshipTo(statRoot, StatisticRelationshipTypes.STATISTIC_PROP);
                }else{
                    statRoot=rel.getEndNode();
                }
                statRoot.setProperty(StatisticProperties.COUNT, totalCount);
                HashSet<Node>treeToDelete=new HashSet<Node>();
                HashSet<Vault>savedVault=new HashSet<Vault>();
                for (Path path:Vault.PROPERTYS.traverse(statRoot)){
                    String key= (String)path.endNode().getProperty(StatisticProperties.KEY);
                    Vault vault=vaults.get(key);
                    if (vault==null){
                        treeToDelete.add(path.endNode());
                    }else {
                        vault.saveVault(service,statRoot,path.endNode());
                        savedVault.add(vault);
                    }
                 }   
                for (Node node:treeToDelete){
                    NeoServiceFactory.getInstance().getDatasetService().deleteTree(service, node);
                }
                for (Vault vault:vaults.values()){
                    if (!savedVault.contains(vault)){
                        vault.saveVault(service,statRoot, null);
                    }
                }
                tx.success();
            } finally {
                tx.finish();
            }
        }
        isChanged=false;
    }
    
    /**
     * Checks if is changed.
     *
     * @param root the root
     * @return true, if is changed
     */
    private boolean isChanged(Node root) {
        if ( isChanged||this.root==null||!root.equals(this.root)){
            return true;
        }
        for (Vault vault:vaults.values()){
            if (vault.isChanged(statRoot)){
                return true;
            }
        }
        return false;
    }

    /**
     * Index value.
     *
     * @param key the key
     * @param nodeType the node type
     * @param propertyName the property name
     * @param value the value
     * @return true, if successful
     */
    public boolean indexValue(String key,String nodeType,String propertyName,Object value){
        return indexValue(key, nodeType, propertyName, value,1);
    }

   public boolean indexValue(String rootKey, String nodeType, String propertyName, Object value, int count) {
       Vault vault=getVault(rootKey);
       boolean result = vault.addValue(nodeType,propertyName,value,count);
       isChanged=isChanged|result;
       return result;
   } 
    /**
     * Register property.
     *
     * @param key the key
     * @param nodeType the node type
     * @param propertyName the property name
     * @param klass the klass
     * @param rule the rule
     * @return true, if successful
     */
    public boolean registerProperty(String key,String nodeType,String propertyName,Class klass,ChangeClassRule rule){
        Vault vault=getVault(key);
        boolean result =  vault.registerProperty(nodeType,propertyName,klass,rule);
        isChanged=isChanged|result;
        return result;
    }
    
    /**
     * Gets the vault.
     *
     * @param key the key
     * @return the vault
     */
    private Vault getVault(String key) {
        Vault vault=vaults.get(key);
        if (vault==null){
            vault=new Vault(key);
            vaults.put(key, vault);
        }
        return vault;
    }


    /**
     * Find property.
     *
     * @param rootname the rootname
     * @param nodeType the node type
     * @param key the key
     * @return the property statistics
     */
    public PropertyStatistics findProperty(String rootname, String nodeType, String key) {
        Vault vault=vaults.get(rootname);
        if (vault==null){
            return null;
        }
        return vault.findProperty(nodeType,key);
    }


    /**
     * Increase type count.
     *
     * @param rootKey the root key
     * @param nodeType the node type
     * @param count the count
     */
    public void increaseTypeCount(String rootKey, String nodeType, long count) {
        increaseTotalCount(count);
        getVault(rootKey).increaseTypeCount(nodeType,count);
    }

    /**
     * Increase total count.
     *
     * @param count the count
     */
    private void increaseTotalCount(long count) {
        isChanged=true;
        totalCount+=count;
    }

    /**
     * Gets the total count.
     *
     * @param rootNode the root node
     * @param nodeType the node type
     * @return the total count
     */
    public long getTotalCount(String rootNode, String nodeType) {
        Vault vault=vaults.get(rootNode);
        if (vault==null){
            return 0;
        }
        return vault.getTotalCount(nodeType);
    }


    public Collection<String> getPropertyNameCollection(String key, String nodeTypeId, @SuppressWarnings("rawtypes") Comparable<Class> comparable) {
        Vault vault=vaults.get(key);
        if (vault==null){
            return new ArrayList<String>();
        }
        return vault.getPropertyNameCollection(nodeTypeId,comparable);
    }

    public void setTypeCount(String rootKey, String nodeType, long count) {
        if (totalCount!=count){
            isChanged=true;
            totalCount=count;
            getVault(rootKey).increaseTypeCount(nodeType,count-totalCount);
        }
    }

    public Set<String> getRootKey() {
        return Collections.unmodifiableSet(vaults.keySet());
    }

    public Set<String> getNodeTypeKey(String rootKey) {
        Vault res = vaults.get(rootKey);
        Set<String> resl=Collections.emptySet();
        return res==null?resl:res.getNodeTypeKey();
    }





}
