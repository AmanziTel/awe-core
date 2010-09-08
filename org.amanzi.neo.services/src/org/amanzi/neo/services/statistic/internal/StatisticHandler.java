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

import org.amanzi.neo.db.manager.INeoDbService;
import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.kernel.Traversal;


/**
 * <p>
 * Provide work with statistic
 * </p>
 *
 * @author TsAr
 * @since 1.0.0
 */
public class StatisticHandler {
    static final TraversalDescription PROPERTYS=Traversal.description().depthFirst().relationships(StatisticRelationshipTypes.PROPERTIES, Direction.OUTGOING).uniqueness(Uniqueness.NONE).filter(Traversal.returnAllButStartNode()).prune(Traversal.pruneAfterDepth( 1));
    /** The vaults. */
    private HashMap<String,Vault>vaults=new HashMap<String, Vault>();
    
    /** The Constant MAX_VALUES_SIZE. */
    public static final int MAX_VALUES_SIZE=100;
    private boolean isChanged=false;
    private Node root;

    /**
     * Load statistic.
     *
     * @param root the root
     */
    public void loadStatistic(Node root){
        this.root = root;
        clearStatistic();
        for (Path path:PROPERTYS.traverse(root)){
           String key= (String)path.endNode().getProperty(StatisticProperties.PROPERTY_KEY);
           Vault vault=new Vault(key);
           vault.loadVault(path.endNode());
           vaults.put(key, vault);
        }
        isChanged=false;
    }

    /**
     * Clear statistic.
     */
    private void clearStatistic() {
        vaults.clear();
    }
    
    /**
     * Save statistic.
     *
     * @param root the root
     */
    public void saveStatistic(INeoDbService service,Node root){
        if (isChanged(root)) {
            // TODO implement
            Transaction tx = service.beginTx();
            try {
                for (Path path:PROPERTYS.traverse(root)){
                    String key= (String)path.endNode().getProperty(StatisticProperties.PROPERTY_KEY);
                    Vault vault=getVault(key);
                    if (vault==null){
//                        deleteTree
                    }
                    vault.loadVault(path.endNode());
                    vaults.put(key, vault);
                 }              
                tx.success();
            } finally {
                tx.finish();
            }
        }
        isChanged=false;
    }
    
    /**
     *
     * @param root2
     * @return
     */
    private boolean isChanged(Node root) {
        return isChanged||this.root==null||!root.equals(this.root);
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
        Vault vault=getVault(key);
        boolean result = vault.addValue(nodeType,propertyName,value);
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
}
