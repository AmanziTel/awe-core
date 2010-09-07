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
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
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
    static final TraversalDescription PROPERTYS=Traversal.description().depthFirst().relationships(StatisticRelationshipTypes.PROPERTIES, Direction.OUTGOING).uniqueness(Uniqueness.NONE);
    /** The vaults. */
    private HashMap<String,Vault>vaults=new HashMap<String, Vault>();
    
    /** The Constant MAX_VALUES_SIZE. */
    public static final int MAX_VALUES_SIZE=100;

    /**
     * Load statistic.
     *
     * @param root the root
     */
    public void loadStatistic(Node root){
        clearStatistic();
        for (Path path:PROPERTYS.prune(Traversal.pruneAfterDepth( 1)).traverse(root)){
           String key= (String)path.endNode().getProperty(StatisticProperties.PROPERTY_KEY);
           Vault vault=new Vault(key);
           vault.loadVault(path.endNode());
           vaults.put(key, vault);
        }
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
    public void saveStatistic(Node root){
        //TODO implement
        Transaction tx = root.getGraphDatabase().beginTx();
        try{

            tx.success();
        }finally{
            tx.finish();
        }
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
        return vault.addValue(nodeType,propertyName,value);
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
        return vault.registerProperty(nodeType,propertyName,klass,rule);
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
     * The Enum ChangeClassRule.
     */
    public enum ChangeClassRule {
        
        /** The IGNOR e_ ne w_ class. */
        IGNORE_NEW_CLASS, 
 /** The REMOV e_ ol d_ class. */
 REMOVE_OLD_CLASS;
    }

    /**
     * <p>
     * Vault handle information by all statistic for one key
     * </p>.
     *
     * @author TsAr
     * @since 1.0.0
     */
    public static class Vault {
        
        /** The key. */
        private final String key;
        
        /** The default rule. */
        private  ChangeClassRule defaultRule;
        
        /** The property map. */
        private HashMap<String,Map<String,PropertyStatistics>> propertyMap=new HashMap<String,Map<String,PropertyStatistics>>();

        /**
         * Instantiates a new vault.
         *
         * @param key the key
         */
        public Vault(String key) {
            super();
            this.key = key;
            defaultRule=ChangeClassRule.REMOVE_OLD_CLASS;
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
            Map<String,PropertyStatistics> propertySet=getPropertysForType(nodeType);
            if (propertySet.get(klass)!=null){
                return false;
            }
            propertySet.put(propertyName, new PropertyStatistics(propertyName, klass, rule));
            return true;
        }

        /**
         * Gets the default rule.
         *
         * @return the default rule
         */
        public ChangeClassRule getDefaultRule() {
            return defaultRule;
        }

        /**
         * Sets the default rule.
         *
         * @param defaultRule the new default rule
         */
        public void setDefaultRule(ChangeClassRule defaultRule) {
            this.defaultRule = defaultRule;
        }

        /**
         * Adds the value.
         *
         * @param nodeType the node type
         * @param propertyName the property name
         * @param value the value
         * @return true, if successful
         */
        public boolean addValue(String nodeType, String propertyName,Object value) {
            PropertyStatistics propStat=getPropertyStatistic(nodeType,propertyName);
            return propStat.addNewValue(value);
        }

        /**
         * Gets the property statistic.
         *
         * @param nodeType the node type
         * @param propertyName the property name
         * @return the property statistic
         */
        private PropertyStatistics getPropertyStatistic(String nodeType, String propertyName) {
            Map<String,PropertyStatistics> propertySet=getPropertysForType(nodeType);
            return getProperty(propertySet,propertyName);
        }


        /**
         * Gets the property.
         *
         * @param propertySet the property set
         * @param propertyName the property name
         * @return the property
         */
        private PropertyStatistics getProperty(Map<String, PropertyStatistics> propertySet, String propertyName) {
            
            PropertyStatistics property=propertySet.get(propertyName);
            if (property==null){
                property=new PropertyStatistics(propertyName, ChangeClassRule.REMOVE_OLD_CLASS);
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
        private Map<String, PropertyStatistics> getPropertysForType(String nodeType) {
            Map<String, PropertyStatistics> properties=propertyMap.get(nodeType);
                if (properties==null){
                    properties=new HashMap<String, PropertyStatistics>();
                    propertyMap.put(nodeType, properties);
                }
            return properties;
        }

        /**
         * Load vault.
         *
         * @param root the root
         */
        public void loadVault(Node root){
            clearVault();
            //TODO implement
        }
        
        /**
         * Clear vault.
         */
        private void clearVault() {
            propertyMap.clear();
        }
        
        /**
         * Save vault.
         *
         * @param root the root
         */
        public void saveVault(Node root){
            //change rule, because in fynnaly during save the vault contains correct information about property
            //TODO implement
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
        
    }

    /**
     * The Class PropertyStatistics.
     */
    public static class PropertyStatistics {
        
        /** The property name. */
        private final String propertyName;
        
        /** The klass. */
        private Class< ? > klass=null;
        
        /** The rule. */
        private ChangeClassRule rule;
        
        /** The min value. */
        private Object minValue;
        
        /** The max value. */
        private Object maxValue;
        
        /** The count. */
        private long count;
        
        /** The values. */
        private Map<Object, Long> values = new HashMap<Object, Long>();
        
        /** The is comparable. */
        private boolean isComparable;

        /**
         * Instantiates a new property statistics.
         * 
         * @param propertyName the property name
         * @param rule the rule
         */
        public PropertyStatistics(String propertyName, ChangeClassRule rule) {
            super();
            this.propertyName = propertyName;
            this.rule = rule;
        }

        /**
         * Instantiates a new property statistics.
         *
         * @param propertyName the property name
         * @param klass the klass
         * @param rule the rule
         */
        public PropertyStatistics(String propertyName, Class< ? > klass, ChangeClassRule rule) {
           this(propertyName,rule);
            setClass(klass);
        }

        /**
         * Adds the new value.
         *
         * @param value the value
         * @return true, if successful
         */
        public boolean addNewValue(Object value) {
            if (value == null) {
                return false;
            }
            Class< ? extends Object> classValue = value.getClass();
            if (klass == null) {
                setClass(classValue);
            }
            if (klass == classValue) {
                addValueToStatistic(value);
            } else {
                switch (rule) {
                case REMOVE_OLD_CLASS:
                    clearStatistic();
                    setClass(classValue);
                    addValueToStatistic(value);
                    break;
                default/* ignore variant */:
                    return false;
                }
            }
            return true;

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
            result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
            return result;
        }

        /**
         * Equals.
         *
         * @param obj the obj
         * @return true, if successful
         */
        @Override
        //only by one field - propertyName
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PropertyStatistics other = (PropertyStatistics)obj;
            if (propertyName == null) {
                if (other.propertyName != null)
                    return false;
            } else if (!propertyName.equals(other.propertyName))
                return false;
            return true;
        }

        /**
         * Sets the class.
         *
         * @param classValue the new class
         */
        private void setClass(Class< ? extends Object> classValue) {
            klass=classValue;
            isComparable=Comparable.class.isAssignableFrom(classValue);
        }


        /**
         * Clear statistic.
         */
        private void clearStatistic() {
            count = 0;
            values.clear();
            minValue=null;
            maxValue=null;

        }


        /**
         * Adds the value to statistic.
         *
         * @param value the value
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        private void addValueToStatistic(Object value) {
            count++;
            if (isComparable){
                if (minValue==null){
                    minValue=value;
                }else if(((Comparable)minValue).compareTo((Comparable)value)>0){
                    minValue=value;
                }
                if (maxValue==null){
                    maxValue=value;
                }else if(((Comparable)maxValue).compareTo((Comparable)value)<0){
                    maxValue=value;
                }
             //because from possible neo4j storing data only array is not comparable we use  isComparable for define: should we compute statistics by value
                if (values.size()<MAX_VALUES_SIZE){
                    Long countStat=values.get(value);
                    if (countStat==null){
                        countStat=0l;
                    }
                    values.put(value,1l+countStat);
                }else{
                    //compute statistic only for first MAX_VALUES_SIZE values
                    Long countStat=values.get(value);
                    if (countStat!=null){
                        values.put(value,1l+countStat);
                    }               
                }
            }
        }
    }
/**
 * 
 * TODO Purpose of StatisticHandler
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
    public enum StatisticRelationshipTypes implements RelationshipType{
        PROPERTIES,PROPERTY;
    }
    public static class StatisticProperties{

        private StatisticProperties() {
            //hide constructor
        }
        public static final String PROPERTY_KEY="property_key";
    }
}
