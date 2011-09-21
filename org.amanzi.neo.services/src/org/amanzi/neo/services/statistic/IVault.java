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

import java.util.Map;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.FailedParseValueException;
import org.amanzi.neo.services.exceptions.IndexPropertyException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.exceptions.UnsupportedClassException;
import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;

/**
 * <p>
 * interface for vault classes
 * </p>
 * 
 * @author Kruglik_A
 * @since 1.0.0
 */
public interface IVault {

    /**
     * Method return subvaults of this vault
     * 
     * @return Return map of subvaults of this vault
     */
    public Map<String, IVault> getSubVaults();

    /**
     * Method return count of properties in node
     * 
     * @return Count of all properties
     */
    public int getCount();
    
    /**
     * Method return count of properties in node with certain node type
     *
     * @param nodeType Type of node
     * @return Count of all properties in node with certain node type
     */
    public int getNodeCount(String nodeType);
    
    /**
     * Method return count of properties in node with certain node type and 
     * certain property name
     *
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @return Count of property with certain property name
     */
    public int getPropertyCount(String nodeType, String propertyName);
    
    /**
     * Method return count of properties in node with certain node type and
     * certain property name and property value
     *
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @param propertyValue Value of property
     * @return Count of property with certain property name and property value
     */
    public int getPropertyValueCount(String nodeType, String propertyName, Object propertyValue);
    
    /**
     * Method find all properties in all vaults
     *
     * @return All properties from statistics
     */
    public Map<Object, Integer> getAllProperties();
    
    /**
     * Method find properties with certain node type
     *
     * @param nodeType Type of node
     * @return All properties from statistics with certain node type
     */
    public Map<Object, Integer> getAllProperties(String nodeType);
    
    /**
     * Method find properties with certain type of Class
     *
     * @param klass Type of Class
     * @return All properties from statistics with certain type of Class
     */
    public Map<Object, Integer> getAllProperties(Class<?> klass);

    /**
     * Method find properties with certain node type and certain property name
     *
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @return All properties from statistics with certain node type and certain property name
     */
    public Map<Object, Integer> getAllProperties(String nodeType, String propertyName);
    
    /**
    * Method find properties with certain name of property
    *
    * @param propertyName Name of property
    * @return All properties from statistics with certain name of property
    */
    public Map<Object, Integer> getAllPropertiesWithName(String propertyName);
   
    /**
     * Method to delete all properties with certain type of node
     *
     * @param nodeType Type of node
     */
    public void deletePropertiesWithNodeType(String nodeType);
    
    /**
     * Method to delete all properties with certain name of property
     *
     * @param propertyName Name of property
     */
    public void deleteProperties(String propertyName);
    
    /**
     * Method to delete all properties with certain type of node and certain name of property
     *
     * @param nodeType Type of node
     * @param propertyName Name of property
     */
    public void deleteProperties(String nodeType, String propertyName);
    
    /**
     * Method to delete all properties with certain type of node and 
     * certain name of property and
     * certain value of property
     *
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @param propertyValue Value of property
     */
    public void deleteProperties(String nodeType, String propertyName, Object propertyValue);
    
    /**
     * Method to update count of propertyValue by certain count of propertyValue
     *
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @param propertyValue Value of property
     * @param newCount New count of propertyValue
     */
    public void updatePropertiesCount(String nodeType, String propertyName, Object propertyValue, int newCount);
    
    /**
     * Method return node type of vault
     * 
     * @return Type of vault
     */
    public String getType();

    /**
     * Add subVault to this vault
     * 
     * @param vault Subvault to this vault
     */
    public void addSubVault(IVault vault);

    /**
     * This method set count to vault
     * 
     * @param count Count of properties in vault
     */
    public void setCount(int count);

    /**
     * this method set type to vault
     * 
     * @param type
     */
    public void setType(String type);

    /**
     * this method get list of propertyStatistics
     * 
     * @return List<NewPropertyStatistics> propertyStatisticsList
     */
    public Map<String, NewPropertyStatistics> getPropertyStatisticsMap();

    /**
     * add propertyStatistics to propertyStatisticsList
     * 
     * @param propStat
     */
    public void addPropertyStatistics(NewPropertyStatistics propStat);

    /**
     * * this method index property in PropertyStatistics and update counts in vaults
     * 
     * @param nodeType
     * @param propName
     * @param propValue
     * @throws IndexPropertyException - method throw this exception if type of given propValue is
     *         wrong
     * @throws InvalidStatisticsParameterException - method throw this exception if some parameter =
     *         null
     * 
     */
    public void indexProperty(String nodeType, String propName, Object propValue) throws IndexPropertyException,
            InvalidStatisticsParameterException;

   /**
    * this method defines type of property value and return this value
    *
    * @param nodeType - type of vault
    * @param propertyName - property name
    * @param propertyValue - String property value
    * @return Object property value
    * @throws UnsupportedClassException 
    * @throws AWEException 
    */
    public Object parse(String nodeType, String propertyName, String propertyValue) throws InvalidStatisticsParameterException, FailedParseValueException, UnsupportedClassException, AWEException ;

}
