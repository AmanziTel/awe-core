package org.amanzi.neo.services.statistic.internal;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Vault handle information by all statistic for one key
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class Vault {
    
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