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

package org.amanzi.neo.services.statistic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.exceptions.IndexPropertyException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.statistic.internal.PropertyStatistics;
import org.apache.log4j.Logger;

/**
 * <p>
 * this class implements IVault interface and used to store statistics
 * </p>
 * 
 * @author Kruglik_A
 * @since 1.0.0
 */
public class StatisticsVault implements IVault {
    private Map<String, IVault> subVaults = new HashMap<String, IVault>();
    private int count;
    private String type;
    private boolean isStatisticsChanged = false;
    private Map<String, PropertyStatistics> propertyStatisticsMap = new HashMap<String, PropertyStatistics>();

    private static Logger LOGGER = Logger.getLogger(StatisticsVault.class);

    private static final String PARAM_NODE_TYPE = "nodeType";
    private static final String PARAM_PROP_NAME = "propName";
    private static final String PARAM_PROP_VALUE = "propValue";

    /**
     * constructor
     */
    public StatisticsVault() {
        super();
        this.count = 0;
        this.type = "";
    }

    /**
     * constructor with type of vault
     * 
     * @param type
     */
    public StatisticsVault(String type) {
        this.type = type;
        this.count = 0;
    }

    @Override
    public void addSubVault(IVault subVault) {
        subVaults.put(subVault.getType(), subVault);
    }

    @Override
    public Map<String, IVault> getSubVaults() {
        return subVaults;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Map<String, PropertyStatistics> getPropertyStatisticsMap() {
        return this.propertyStatisticsMap;
    }

    @Override
    public void addPropertyStatistics(PropertyStatistics propStat) {
        this.propertyStatisticsMap.put(propStat.getName(), propStat);
    }

    @Override
    public void setCount(int count) {
        isStatisticsChanged = true;
        this.count = count;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void indexProperty(String nodeType, String propName, Object propValue) throws IndexPropertyException,
            InvalidStatisticsParameterException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start method indexProperty(String nodeType, String propName, Object propValue)");
        }

        if (nodeType == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter nodeType is null");
            throw new InvalidStatisticsParameterException(PARAM_NODE_TYPE, nodeType);
        }
        if (propName == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propName is null");
            throw new InvalidStatisticsParameterException(PARAM_PROP_NAME, propName);
        }
        if (propValue == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propValue is null");
            throw new InvalidStatisticsParameterException(PARAM_PROP_VALUE, propValue);
        }
        if (nodeType.isEmpty()) {
            LOGGER.error("InvalidStatisticsParameterException: parameter nodeType is empty String");
            throw new InvalidStatisticsParameterException(PARAM_NODE_TYPE, nodeType);
        }
        if (propName.isEmpty()) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propName is empty String");
            throw new InvalidStatisticsParameterException(PARAM_PROP_NAME, propName);
        }

        IVault vault;
        if (this.getType().equals(nodeType)) {
            vault = this;
        } else {
            this.setCount(this.getCount() + 1);
            vault = this.getSubVault(nodeType);
        }
        try {
            PropertyStatistics propStat = ((StatisticsVault)vault).getPropertyStatistics(propName, propValue.getClass());
            propStat.updatePropertyMap(propValue, 1);
            vault.setCount(vault.getCount() + 1);
        } catch (IndexPropertyException e) {
            this.setCount(this.getCount() - 1);
            LOGGER.error("IndexPropertyException: index property has wrong type");
            throw e;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("finish method indexProperty(String nodeType, String propName, Object propValue)");
        }
    }

    @Override
    public void removeProperty(String nodeType, String propName, Object propValue) throws IndexPropertyException,
            InvalidStatisticsParameterException {

        LOGGER.debug("start method removeProperty(String nodeType, String propName, Object propValue)");

        if (nodeType == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter nodeType is null");
            throw new InvalidStatisticsParameterException(PARAM_NODE_TYPE, nodeType);
        }
        if (propName == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propName is null");
            throw new InvalidStatisticsParameterException(PARAM_PROP_NAME, propName);
        }
        if (propValue == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propValue is null");
            throw new InvalidStatisticsParameterException(PARAM_PROP_VALUE, propValue);
        }
        if (nodeType.isEmpty()) {
            LOGGER.error("InvalidStatisticsParameterException: parameter nodeType is empty String");
            throw new InvalidStatisticsParameterException(PARAM_NODE_TYPE, nodeType);
        }
        if (propName.isEmpty()) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propName is empty String");
            throw new InvalidStatisticsParameterException(PARAM_PROP_NAME, propName);
        }

        IVault vault;
        if (this.getType().equals(nodeType)) {
            vault = this;
        } else {
            this.setCount(this.getCount() - 1);
            vault = this.getSubVault(nodeType);
        }
        try {
            PropertyStatistics propStat = ((StatisticsVault)vault).getPropertyStatistics(propName, propValue.getClass());
            propStat.updatePropertyMap(propValue, -1);
            vault.setCount(vault.getCount() - 1);
        } catch (IndexPropertyException e) {
            this.setCount(this.getCount() + 1);
            LOGGER.error("IndexPropertyException: index property has wrong type");
            throw e;
        }
        LOGGER.debug("finish method removeProperty(String nodeType, String propName, Object propValue)");
    }
    
    @Override
    public void renamePropertyValue(String nodeType, String propName, Object oldPropValue, Object newPropValue) throws IndexPropertyException,
            InvalidStatisticsParameterException {
        
        LOGGER.debug("start method renamePropertyValue(String nodeType, String propName, Object oldPropValue, Object newPropValue)");

        if (nodeType == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter nodeType is null");
            throw new InvalidStatisticsParameterException(PARAM_NODE_TYPE, nodeType);
        }
        if (nodeType.isEmpty()) {
            LOGGER.error("InvalidStatisticsParameterException: parameter nodeType is empty String");
            throw new InvalidStatisticsParameterException(PARAM_NODE_TYPE, nodeType);
        }
        if (propName == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propName is null");
            throw new InvalidStatisticsParameterException(PARAM_PROP_NAME, propName);
        }
        if (propName.isEmpty()) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propName is empty String");
            throw new InvalidStatisticsParameterException(PARAM_PROP_NAME, propName);
        }
        if (oldPropValue == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter oldPropValue is null");
            throw new InvalidStatisticsParameterException(PARAM_PROP_VALUE, oldPropValue);
        }
        if (newPropValue == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter newPropValue is null");
            throw new InvalidStatisticsParameterException(PARAM_PROP_VALUE, newPropValue);
        }

        IVault vault;
        if (this.getType().equals(nodeType)) {
            vault = this;
        } else {
            vault = this.getSubVault(nodeType);
        }
        try {
            PropertyStatistics propStat = ((StatisticsVault)vault).getPropertyStatistics(propName, oldPropValue.getClass());
            // update statistics only if it was saved early 
            if (propStat.getPropertyMap().containsKey(oldPropValue)) {
                propStat.updatePropertyMap(oldPropValue, -1);
                propStat.updatePropertyMap(newPropValue, 1);
            }
        } catch (IndexPropertyException e) {
            this.setCount(this.getCount() + 1);
            LOGGER.error("IndexPropertyException: index property has wrong type");
            throw e;
        }
        LOGGER.debug("finish method renamePropertyValue(String nodeType, String propName, Object oldPropValue, Object newPropValue)");
    }

    /**
     * this method find propertyStatistics by name and check matches the types
     * 
     * @param name - propertyStatistics name
     * @param klass - class of property value type
     * @return NewPropertyStatistics instance by name
     * @throws IndexPropertyException - method throw this exception if given class is wrong
     */
    private PropertyStatistics getPropertyStatistics(String name, Class< ? > klass) throws IndexPropertyException {
        PropertyStatistics propStat = this.getPropertyStatisticsMap().get(name);
        if (propStat != null) {
            return propStat;
        } else {
            propStat = new PropertyStatistics(name, klass);
            this.addPropertyStatistics(propStat);
        }
        return propStat;
    }

    /**
     * this method get Vault by type
     * 
     * @param nodeType - String node type
     * @return IVault vault by type
     */
    private IVault getSubVault(String nodeType) {
        Map<String, IVault> vaultMap = this.getSubVaults();
        IVault result = vaultMap.get(nodeType);
        if (result == null) {
            result = new StatisticsVault(nodeType);
            this.addSubVault(result);
        }
        return result;
    }

    

    @Override
    public int getNodeCount(String nodeType) {
        if (this.type.equals(nodeType)) {
            return this.count;
        } else {
            for (String type : this.subVaults.keySet()) {
                if (type.equals(nodeType)) {
                    return this.subVaults.get(type).getCount();
                }
            }
        }
        return 0;
    }

    @Override
    public int getPropertyCount(String nodeType, String propertyName) {
        Map<Object, Integer> allProperties = getAllProperties(nodeType, propertyName);
        return allProperties.size();
    }

    @Override
    public int getPropertyValueCount(String nodeType, String propertyName, Object propertyValue) {
        int countOfPropertyValue = 0;
        Map<Object, Integer> allProperties = getAllProperties(nodeType, propertyName);
        for (Object propValue : allProperties.keySet()) {
            if (propValue.toString().equals(propertyValue.toString())) {
                countOfPropertyValue += allProperties.get(propValue);
            }
        }
        return countOfPropertyValue;
    }

    @Override
    public Set<String> getAllPropertyNames(String nodeType) {
        Set<String> result = new HashSet<String>();
        
        IVault subVault = subVaults.get(nodeType);
        if (subVault != null) {
            result.addAll(subVault.getPropertyStatisticsMap().keySet());
        }
        
        return result;
    }

    @Override
    public Map<Object, Integer> getAllProperties(String nodeType, String propertyName) {
        return getAllProperties(this, nodeType, propertyName);
    }

    private Map<Object, Integer> getAllProperties(IVault subVault, String nodeType, String propertyName) {
        Map<Object, Integer> allProperties = new HashMap<Object, Integer>();
        if (subVault.getSubVaults().size() != 0) {
            for (String nameOfSubVault : subVault.getSubVaults().keySet()) {
                IVault subV = subVault.getSubVaults().get(nameOfSubVault);
                if (subV.getType().equals(nodeType)) {
                    allProperties.putAll(getAllPropertiesWithName(subV, propertyName));
                } else {
                    allProperties.putAll(getAllProperties(subV, nodeType, propertyName));
                }
            }
        } else {
            for (IVault subSubVault : subVault.getSubVaults().values()) {
                if (subSubVault.getType().equals(nodeType)) {
                    allProperties.putAll(getAllPropertiesWithName(subSubVault, propertyName));
                }
            }
        }

        return allProperties;
    }

    @Override
    public Map<Object, Integer> getAllPropertiesWithName(String propertyName) {
        return getAllPropertiesWithName(this, propertyName);
    }

    private Map<Object, Integer> getAllPropertiesWithName(IVault subVault, String propertyName) {
        Map<Object, Integer> allProperties = new HashMap<Object, Integer>();
        if (subVault.getSubVaults().values().size() != 0) {
            for (String nameOfSubVault : subVault.getSubVaults().keySet()) {
                IVault subV = subVault.getSubVaults().get(nameOfSubVault);
                allProperties.putAll(getAllPropertiesWithName(subV, propertyName));
            }
        } else {
            Map<String, PropertyStatistics> propertyStatisticMap = subVault.getPropertyStatisticsMap();
            for (String property : propertyStatisticMap.keySet()) {
                if (property.equals(propertyName)) {
                    PropertyStatistics newPropertyStatistic = propertyStatisticMap.get(property);
                    Map<Object, Integer> propertyMap = newPropertyStatistic.getPropertyMap();
                    for (Object key : propertyMap.keySet()) {
                        allProperties.put(key, propertyMap.get(key));
                    }
                }
            }
        }

        return allProperties;
    }

    @Override
    public Set<String> getAllProperties(String nodeType, Class< ? > klass) {
        Set<String> result = new HashSet<String>();
        
        IVault subVault = subVaults.get(nodeType);
        if (subVault != null) {
            for (PropertyStatistics singleProperty : subVault.getPropertyStatisticsMap().values()) {
                if (singleProperty.getKlass().equals(klass)) {
                    result.add(singleProperty.getName());
                }
            }
        }
        
        return result;
    }

    @Override
    public void deletePropertiesWithNodeType(String nodeType) {
        deletePropertiesWithNodeType(this, nodeType);
    }

    private void deletePropertiesWithNodeType(IVault vault, String nodeType) {
        if (vault.getType().equals(nodeType)) {
            vault.getSubVaults().remove(nodeType);
        } else {
            for (String tempNodeType : vault.getSubVaults().keySet()) {
                IVault subVault = vault.getSubVaults().get(tempNodeType);
                if (tempNodeType.equals(nodeType)) {
                    vault.getSubVaults().remove(nodeType);
                    break;
                } else {
                    deletePropertiesWithNodeType(subVault, nodeType);
                }
            }
        }
    }

    @Override
    public void deleteProperties(String propertyName) {
        deleteProperties(this, propertyName);
    }

    private void deleteProperties(IVault vault, String propertyName) {
        if (vault.getSubVaults().values().size() == 0) {
            for (String property : vault.getPropertyStatisticsMap().keySet()) {
                if (property.equals(propertyName)) {
                    vault.getPropertyStatisticsMap().remove(propertyName);
                    break;
                }
            }
        } else {
            for (String tempNodeType : vault.getSubVaults().keySet()) {
                IVault subVault = vault.getSubVaults().get(tempNodeType);
                if (subVault.getSubVaults().values().size() == 0) {
                    for (String property : subVault.getPropertyStatisticsMap().keySet()) {
                        if (property.equals(propertyName)) {
                            subVault.getPropertyStatisticsMap().remove(propertyName);
                            break;
                        }
                    }
                } else {
                    deleteProperties(subVault, propertyName);
                }
            }
        }
    }

    @Override
    public void deleteProperties(String nodeType, String propertyName) {
        deleteProperties(this, nodeType, propertyName);
    }

    private void deleteProperties(IVault vault, String nodeType, String propertyName) {
        if (vault.getSubVaults().values().size() == 0) {
            if (vault.getType().equals(nodeType)) {
                for (String property : vault.getPropertyStatisticsMap().keySet()) {
                    if (property.equals(propertyName)) {
                        vault.getPropertyStatisticsMap().remove(propertyName);
                        break;
                    }
                }
            }
        } else {
            for (String tempNodeType : vault.getSubVaults().keySet()) {
                IVault subVault = vault.getSubVaults().get(tempNodeType);
                if (subVault.getSubVaults().values().size() == 0) {
                    if (subVault.getType().equals(nodeType)) {
                        for (String property : subVault.getPropertyStatisticsMap().keySet()) {
                            if (property.equals(propertyName)) {
                                subVault.getPropertyStatisticsMap().remove(propertyName);
                                break;
                            }
                        }
                    }
                } else {
                    deleteProperties(subVault, nodeType, propertyName);
                }
            }
        }
    }

    @Override
    public void deleteProperties(String nodeType, String propertyName, Object propertyValue) {
        deleteProperties(this, nodeType, propertyName, propertyValue);
    }

    private void deleteProperties(IVault vault, String nodeType, String propertyName, Object propertyValue) {
        if (vault.getSubVaults().values().size() == 0) {
            if (vault.getType().equals(nodeType)) {
                for (String property : vault.getPropertyStatisticsMap().keySet()) {
                    if (propertyName.equals(property)) {
                        PropertyStatistics propertyStatistics = vault.getPropertyStatisticsMap().get(property);
                        Map<Object, Integer> propertyMap = propertyStatistics.getPropertyMap();
                        for (Object propValue : propertyMap.keySet()) {
                            if (propValue.toString().equals(propertyValue.toString())) {
                                propertyMap.remove(propValue);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            for (String tempNodeType : vault.getSubVaults().keySet()) {
                IVault subVault = vault.getSubVaults().get(tempNodeType);
                if (subVault.getSubVaults().values().size() == 0) {
                    if (subVault.getType().equals(nodeType)) {
                        for (String property : subVault.getPropertyStatisticsMap().keySet()) {
                            if (propertyName.equals(property)) {
                                PropertyStatistics propertyStatistics = subVault.getPropertyStatisticsMap().get(property);
                                Map<Object, Integer> propertyMap = propertyStatistics.getPropertyMap();
                                for (Object propValue : propertyMap.keySet()) {
                                    if (propValue.toString().equals(propertyValue.toString())) {
                                        propertyMap.remove(propValue);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    deleteProperties(subVault, nodeType, propertyName, propertyValue);
                }
            }
        }
    }

    @Override
    public void updatePropertiesCount(String nodeType, String propertyName, Object propertyValue, int newCount) {
        updatePropertiesCount(this, nodeType, propertyName, propertyValue, newCount);
    }

    private void updatePropertiesCount(IVault vault, String nodeType, String propertyName, Object propertyValue, int newCount) {
        if (vault.getSubVaults().values().size() == 0) {
            if (vault.getType().equals(nodeType)) {
                for (String property : vault.getPropertyStatisticsMap().keySet()) {
                    if (propertyName.equals(property)) {
                        PropertyStatistics propertyStatistics = vault.getPropertyStatisticsMap().get(property);
                        Map<Object, Integer> propertyMap = propertyStatistics.getPropertyMap();
                        for (Object propValue : propertyMap.keySet()) {
                            if (propValue.toString().equals(propertyValue.toString())) {
                                propertyMap.remove(propValue);
                                propertyMap.put(propertyValue, newCount);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            for (String tempNodeType : vault.getSubVaults().keySet()) {
                IVault subVault = vault.getSubVaults().get(tempNodeType);
                if (subVault.getSubVaults().values().size() == 0) {
                    if (subVault.getType().equals(nodeType)) {
                        for (String property : subVault.getPropertyStatisticsMap().keySet()) {
                            if (propertyName.equals(property)) {
                                PropertyStatistics propertyStatistics = subVault.getPropertyStatisticsMap().get(property);
                                Map<Object, Integer> propertyMap = propertyStatistics.getPropertyMap();
                                for (Object propValue : propertyMap.keySet()) {
                                    if (propValue.toString().equals(propertyValue.toString())) {
                                        propertyMap.remove(propValue);
                                        propertyMap.put(propertyValue, newCount);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    updatePropertiesCount(subVault, nodeType, propertyName, propertyValue, newCount);
                }
            }
        }
    }

    @Override
    public boolean isStatisticsChanged() {
        return isStatisticsChanged;
    }

    @Override
    public void setIsStatisticsChanged(boolean isStatisticsChanged) {
        this.isStatisticsChanged = isStatisticsChanged;
    }

    @Override
    public Number getMinValue(String nodeType, String propertyName) {
        for (IVault mainVault : subVaults.values()) {
            Number result = getMinValue(mainVault, nodeType, propertyName, false);
            if (result != null)
                return result;
        }
        return null;
    }
    
    /**
     * Recursive method to find minimum value
     *
     * @param vault This vault or sub-vault
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @return Minimum value of vault or sub-vaults or null
     */
    private Number getMinValue(IVault vault, String nodeType, String propertyName, boolean isNeedType) {
        if (isNeedType || vault.getType().equals(nodeType)) {
            isNeedType = true;
            Map<String, PropertyStatistics> localPropertyStatisticsMap =
                    vault.getPropertyStatisticsMap();
            if (localPropertyStatisticsMap.keySet().contains(propertyName)) {
                PropertyStatistics propertyStatistics = 
                        localPropertyStatisticsMap.get(propertyName);
                return propertyStatistics.getMinValue();
            }
            else {
                for (IVault subVault : vault.getSubVaults().values()) {
                    return getMinValue(subVault, nodeType, propertyName, isNeedType);
                }
            }
        }
        else {
            for (IVault subVault : vault.getSubVaults().values()) {
                return getMinValue(subVault, nodeType, propertyName, isNeedType);
            }
        }
        return null;
    }

    @Override
    public Number getMaxValue(String nodeType, String propertyName) {
        for (IVault mainVault : subVaults.values()) {
            Number result = getMaxValue(mainVault, nodeType, propertyName, false);
            if (result != null)
                return result;
        }
        return null;
    }
    
    /**
     * Recursive method to find maximum value
     *
     * @param vault This vault or sub-vault
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @return Maximum value of vault or sub-vaults or null
     */
    private Number getMaxValue(IVault vault, String nodeType, String propertyName, boolean isNeedType) {
        if (isNeedType || vault.getType().equals(nodeType)) {
            isNeedType = true;
            Map<String, PropertyStatistics> localPropertyStatisticsMap =
                    vault.getPropertyStatisticsMap();
            if (localPropertyStatisticsMap.keySet().contains(propertyName)) {
                PropertyStatistics propertyStatistics = 
                        localPropertyStatisticsMap.get(propertyName);
                return propertyStatistics.getMaxValue();
            }
            else {
                for (IVault subVault : vault.getSubVaults().values()) {
                    return getMaxValue(subVault, nodeType, propertyName, isNeedType);
                }
            }
        }
        else {
            for (IVault subVault : vault.getSubVaults().values()) {
                return getMaxValue(subVault, nodeType, propertyName, isNeedType);
            }
        }
        return null;
    }

}
