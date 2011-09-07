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
     * this method get subVaults of vault
     * 
     * @return List<IVault> subVaults
     */
    public Map<String, IVault> getSubVaults();

    /**
     * this method get count
     * 
     * @return int count
     */
    public int getCount();

    /**
     * this method get type of vault
     * 
     * @return String type
     */
    public String getType();

    /**
     * add subVault to vault
     * 
     * @param vault - subVault
     */
    public void addSubVault(IVault vault);

    /**
     * this method set count to vault
     * 
     * @param count
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
