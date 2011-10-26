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

package org.amanzi.neo.services.model;

import java.util.Set;

import org.amanzi.neo.services.enums.INodeType;

//TODO: LN: comments
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IPropertyStatisticalModel extends IDataModel {

    public int getNodeCount(INodeType nodeType);

    public int getPropertyCount(INodeType nodeType, String propertyName);

    /**
     * Method find properties with certain node type
     * 
     * @param nodeType Type of node
     * @return All properties from statistics with certain node type
     */
    public String[] getAllPropertyNames(INodeType nodeType);
    
    /**
     * Method find properties with certain type of Class
     * 
     * @param klass Type of Class
     * @return All properties from statistics with certain type of Class
     */
    public String[] getAllProperties(INodeType nodeType, Class< ? > klass);
    
    /**
     * Method find properties with certain node type and certain property name
     * 
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @return All properties from statistics with certain node type and certain property name
     */
    public Set<Object> getAllProperties(INodeType nodeType, String propertyName);
    
    /**
     * Returns Class of Property
     *
     * @param nodeType type of node
     * @param propertyName name of property
     * @return
     */
    public Class<?> getPropertyClass(INodeType nodeType, String propertyName);
    
    /**
     * Returns all values of this property
     *
     * @param nodeType type of node
     * @param propertyName name of property
     * @return
     */
    public Object[] getPropertyValues(INodeType nodeType, String propertyName);
    
    /**
     * Method find min value in statistics by nodeType and propertyName
     *
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @return Minimum value in statistics
     */
    public Number getMinValue(INodeType nodeType, String propertyName);
    
    /**
     * Method find max value in statistics by nodeType and propertyName
     *
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @return Maximum value in statistics
     */
    public Number getMaxValue(INodeType nodeType, String propertyName);
}
