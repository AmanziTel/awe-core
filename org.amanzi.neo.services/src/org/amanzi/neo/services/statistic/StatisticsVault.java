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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.IndexPropertyException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;
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
    private Map<String, NewPropertyStatistics> propertyStatisticsMap = new HashMap<String, NewPropertyStatistics>();
    private Map<Object, Integer> allProperties = new HashMap<Object, Integer>();

    private static Logger LOGGER = Logger.getLogger(StatisticsVault.class);

    private static final String TRUE = "true";
    private static final String FALSE = "false";
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
    public Map<String, NewPropertyStatistics> getPropertyStatisticsMap() {
        return this.propertyStatisticsMap;
    }

    @Override
    public void addPropertyStatistics(NewPropertyStatistics propStat) {
        this.propertyStatisticsMap.put(propStat.getName(), propStat);
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void indexProperty(String nodeType, String propName, Object propValue) throws IndexPropertyException,
            InvalidStatisticsParameterException {

        LOGGER.debug("start method indexProperty(String nodeType, String propName, Object propValue)");

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
            NewPropertyStatistics propStat = ((StatisticsVault)vault).getPropertyStatistics(propName, propValue.getClass());
            propStat.updatePropertyMap(propValue, 1);
            vault.setCount(vault.getCount() + 1);
        } catch (IndexPropertyException e) {
            this.setCount(this.getCount() - 1);
            LOGGER.error("IndexPropertyException: index property has wrong type");
            throw e;
        }
        LOGGER.debug("finish method indexProperty(String nodeType, String propName, Object propValue)");

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
            NewPropertyStatistics propStat = ((StatisticsVault)vault).getPropertyStatistics(propName, propValue.getClass());
            propStat.updatePropertyMap(propValue, -1);
            vault.setCount(vault.getCount() - 1);
        } catch (IndexPropertyException e) {
            this.setCount(this.getCount() + 1);
            LOGGER.error("IndexPropertyException: index property has wrong type");
            throw e;
        }
        LOGGER.debug("finish method removeProperty(String nodeType, String propName, Object propValue)");
    }


    /**
     * this method find propertyStatistics by name and check matches the types
     * 
     * @param name - propertyStatistics name
     * @param klass - class of property value type
     * @return NewPropertyStatistics instance by name
     * @throws IndexPropertyException - method throw this exception if given class is wrong
     */
    private NewPropertyStatistics getPropertyStatistics(String name, Class< ? > klass) throws IndexPropertyException {
        NewPropertyStatistics propStat = this.getPropertyStatisticsMap().get(name);
        if (propStat != null) {
            if (propStat.getKlass().equals(klass)) {
                return propStat;
            }
            throw new IndexPropertyException();
        } else {
            propStat = new NewPropertyStatistics(name, klass);
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
    public Object parse(String nodeType, String propertyName, String propertyValue) throws AWEException {
        LOGGER.debug("start method parse(String nodeType, String propertyName, String propertyValue)");
        if (nodeType == null || nodeType.isEmpty()) {
            LOGGER.error("InvalidStatisticsParameterException: parameter nodeType is null or Empty String");
            throw new InvalidStatisticsParameterException(PARAM_NODE_TYPE, nodeType);
        }
        if (propertyName == null || propertyName.isEmpty()) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propertyName is null or Empty String");
            throw new InvalidStatisticsParameterException(PARAM_PROP_NAME, propertyName);
        }
        if (propertyValue == null || propertyValue.isEmpty()) {
            return null;
        }

        boolean hasPropStat;
        IVault vault;

        if (this.getType().equals(nodeType)) {
            vault = this;
        } else {
            vault = this.getSubVault(nodeType);
        }

        hasPropStat = vault.getPropertyStatisticsMap().containsKey(propertyName);
        try {
            if (hasPropStat) {
                return vault.getPropertyStatisticsMap().get(propertyName).parseValue(propertyValue);
            } else {
                Object result = autoParse(propertyValue);
                vault.addPropertyStatistics(new NewPropertyStatistics(propertyName, result.getClass()));
                return result;
            }
        } catch (AWEException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * this method try to parse String propValue if its type is unknown
     * 
     * @param propertyValue - String propValue
     * @return Object parseValue
     */
    private Object autoParse(String propertyValue) {

        try {
            char separator = '.';
            if (propertyValue.indexOf(separator) != -1) {
                Number numberValue = NumberFormat.getNumberInstance().parse(propertyValue);
                int lastIndex = propertyValue.indexOf("e") + propertyValue.indexOf("E");
                lastIndex = (lastIndex < 0) ? (propertyValue.length() - 1) : lastIndex;

                Boolean isDouble = 7 < (lastIndex - propertyValue.indexOf(separator));

                if (isDouble) {
                    return numberValue.doubleValue();
                } else {
                    return numberValue.floatValue();
                }
            } else {
                try {
                    return Integer.parseInt(propertyValue);
                } catch (NumberFormatException e) {
                    return Long.parseLong(propertyValue);
                }
            }
        } catch (Exception e) {
            if (propertyValue.equalsIgnoreCase(TRUE)) {
                return Boolean.TRUE;
            } else if (propertyValue.equalsIgnoreCase(FALSE)) {
                return Boolean.FALSE;
            }
            return propertyValue;
        }

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
        getAllProperties(nodeType, propertyName);
        return allProperties.size();
    }

    @Override
    public int getPropertyValueCount(String nodeType, String propertyName, Object propertyValue) {
        int countOfPropertyValue = 0;
        getAllProperties(nodeType, propertyName);
        for (Object propValue : allProperties.keySet()) {
            if (propValue.toString().equals(propertyValue.toString())) {
                countOfPropertyValue += allProperties.get(propValue);
            }
        }
        return countOfPropertyValue;
    }

    @Override
    public Map<Object, Integer> getAllProperties() {
        allProperties = new HashMap<Object, Integer>();
        return getAllProperties(this);
    }

    private Map<Object, Integer> getAllProperties(IVault subVault) {

        if (subVault.getSubVaults().values().size() != 0) {
            for (String nameOfSubVault : subVault.getSubVaults().keySet()) {
                IVault subV = subVault.getSubVaults().get(nameOfSubVault);
                if (subV.getSubVaults().values().size() == 0) {
                    Map<String, NewPropertyStatistics> propertyStatisticMap = subV.getPropertyStatisticsMap();
                    for (String property : propertyStatisticMap.keySet()) {
                        NewPropertyStatistics newPropertyStatistic = propertyStatisticMap.get(property);
                        allProperties.putAll(newPropertyStatistic.getPropertyMap());
                    }
                } else {
                    getAllProperties(subV);
                }
            }
        } else {
            Map<String, NewPropertyStatistics> propertyStatisticMap = subVault.getPropertyStatisticsMap();
            for (String property : propertyStatisticMap.keySet()) {
                NewPropertyStatistics newPropertyStatistic = propertyStatisticMap.get(property);
                allProperties.putAll(newPropertyStatistic.getPropertyMap());
            }
        }

        return allProperties;
    }

    @Override
    public Map<Object, Integer> getAllProperties(String nodeType) {
        allProperties = new HashMap<Object, Integer>();
        return getAllProperties(this, nodeType);
    }

    private Map<Object, Integer> getAllProperties(IVault subVault, String nodeType) {
        if (subVault.getSubVaults().size() != 0) {
            for (String nameOfSubVault : subVault.getSubVaults().keySet()) {
                IVault subV = subVault.getSubVaults().get(nameOfSubVault);
                if (subV.getType().equals(nodeType)) {
                    getAllProperties(subV);
                } else {
                    getAllProperties(subV, nodeType);
                }
            }
        } else {
            for (IVault subSubVault : subVault.getSubVaults().values()) {
                if (subSubVault.getType().equals(nodeType)) {
                    getAllProperties(subSubVault);
                }
            }
        }

        return allProperties;
    }

    @Override
    public Map<Object, Integer> getAllProperties(String nodeType, String propertyName) {
        allProperties = new HashMap<Object, Integer>();
        return getAllProperties(this, nodeType, propertyName);
    }

    private Map<Object, Integer> getAllProperties(IVault subVault, String nodeType, String propertyName) {
        if (subVault.getSubVaults().size() != 0) {
            for (String nameOfSubVault : subVault.getSubVaults().keySet()) {
                IVault subV = subVault.getSubVaults().get(nameOfSubVault);
                if (subV.getType().equals(nodeType)) {
                    getAllPropertiesWithName(subV, propertyName);
                } else {
                    getAllProperties(subV, nodeType, propertyName);
                }
            }
        } else {
            for (IVault subSubVault : subVault.getSubVaults().values()) {
                if (subSubVault.getType().equals(nodeType)) {
                    getAllPropertiesWithName(subSubVault, propertyName);
                }
            }
        }

        return allProperties;
    }

    @Override
    public Map<Object, Integer> getAllPropertiesWithName(String propertyName) {
        allProperties = new HashMap<Object, Integer>();
        return getAllPropertiesWithName(this, propertyName);
    }

    private Map<Object, Integer> getAllPropertiesWithName(IVault subVault, String propertyName) {
        if (subVault.getSubVaults().values().size() != 0) {
            for (String nameOfSubVault : subVault.getSubVaults().keySet()) {
                IVault subV = subVault.getSubVaults().get(nameOfSubVault);
                if (subV.getSubVaults().values().size() == 0) {
                    Map<String, NewPropertyStatistics> propertyStatisticMap = subV.getPropertyStatisticsMap();
                    for (String property : propertyStatisticMap.keySet()) {
                        if (property.equals(propertyName)) {
                            NewPropertyStatistics newPropertyStatistic = propertyStatisticMap.get(property);
                            Map<Object, Integer> propertyMap = newPropertyStatistic.getPropertyMap();
                            for (Object key : propertyMap.keySet()) {
                                allProperties.put(key, propertyMap.get(key));
                            }
                        }
                    }
                } else {
                    getAllPropertiesWithName(subV, propertyName);
                }
            }
        } else {
            Map<String, NewPropertyStatistics> propertyStatisticMap = subVault.getPropertyStatisticsMap();
            for (String property : propertyStatisticMap.keySet()) {
                if (property.equals(propertyName)) {
                    NewPropertyStatistics newPropertyStatistic = propertyStatisticMap.get(property);
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
    public Map<Object, Integer> getAllProperties(Class< ? > klass) {
        allProperties = new HashMap<Object, Integer>();
        return getAllProperties(this, klass);
    }

    private Map<Object, Integer> getAllProperties(IVault subVault, Class< ? > klass) {
        if (subVault.getSubVaults().values().size() != 0) {
            for (String nameOfSubVault : subVault.getSubVaults().keySet()) {
                IVault subV = subVault.getSubVaults().get(nameOfSubVault);
                if (subV.getSubVaults().values().size() == 0) {
                    Map<String, NewPropertyStatistics> propertyStatisticMap = subV.getPropertyStatisticsMap();
                    for (String property : propertyStatisticMap.keySet()) {
                        NewPropertyStatistics newPropertyStatistic = propertyStatisticMap.get(property);
                        if (newPropertyStatistic.getKlass().equals(klass)) {
                            Map<Object, Integer> propertyMap = newPropertyStatistic.getPropertyMap();
                            for (Object key : propertyMap.keySet()) {
                                allProperties.put(key, propertyMap.get(key));
                            }
                        }
                    }
                } else {
                    getAllProperties(subV, klass);
                }
            }
        } else {
            Map<String, NewPropertyStatistics> propertyStatisticMap = subVault.getPropertyStatisticsMap();
            for (String property : propertyStatisticMap.keySet()) {
                NewPropertyStatistics newPropertyStatistic = propertyStatisticMap.get(property);
                if (newPropertyStatistic.getKlass().equals(klass)) {
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
                        NewPropertyStatistics propertyStatistics = vault.getPropertyStatisticsMap().get(property);
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
                                NewPropertyStatistics propertyStatistics = subVault.getPropertyStatisticsMap().get(property);
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
                        NewPropertyStatistics propertyStatistics = vault.getPropertyStatisticsMap().get(property);
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
                                NewPropertyStatistics propertyStatistics = subVault.getPropertyStatisticsMap().get(property);
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

}
