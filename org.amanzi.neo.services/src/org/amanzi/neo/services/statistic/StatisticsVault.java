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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.NewPropertyStatistics;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.IndexPropertyException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
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
    private Map<String, NewPropertyStatistics> propertyStatisticsMap = new HashMap<String, NewPropertyStatistics>();

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
            DecimalFormat format = new DecimalFormat();
            char separator = format.getDecimalFormatSymbols().getDecimalSeparator();
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
}
