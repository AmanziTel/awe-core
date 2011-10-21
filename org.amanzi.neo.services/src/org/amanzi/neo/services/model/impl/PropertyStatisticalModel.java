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

package org.amanzi.neo.services.model.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewStatisticsService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.IndexPropertyException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.exceptions.LoadVaultException;
import org.amanzi.neo.services.model.IPropertyStatisticalModel;
import org.amanzi.neo.services.statistic.IVault;
import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class PropertyStatisticalModel extends DataModel implements IPropertyStatisticalModel {

    private NewStatisticsService statisticsService = NeoServiceFactory.getInstance().getNewStatisticsService();

    protected IVault statisticsVault;

    protected ArrayList<String> notNecessaryListOfProperties = new ArrayList<String>();
    
    /**
     * Method to fill properties which contains not need properties
     */
    private void fillListOfNotNecessaryProperties() {
        notNecessaryListOfProperties.add(INeoConstants.PROPERTY_LAT_NAME);
        notNecessaryListOfProperties.add(INeoConstants.PROPERTY_LATITUDE_NAME);
        notNecessaryListOfProperties.add(INeoConstants.PROPERTY_LON_NAME);
        notNecessaryListOfProperties.add(INeoConstants.PROPERTY_LONGITUDE_NAME);
        notNecessaryListOfProperties.add(INeoConstants.PROPERTY_TIMESTAMP_NAME);
    }
    
    protected void initializeStatistics() {
        try {
            statisticsVault = statisticsService.loadVault(getRootNode());
            fillListOfNotNecessaryProperties();
        } catch (AWEException e) {

        }
    }

    protected void indexProperty(INodeType nodeType, String propertyName, Object propertyValue)
            throws InvalidStatisticsParameterException, LoadVaultException, IndexPropertyException {

        if (!notNecessaryListOfProperties.contains(propertyName)) {
            statisticsVault.indexProperty(nodeType.getId(), propertyName, propertyValue);
        }
    }
    
    protected void removeProperty(INodeType nodeType, String propertyName, Object propertyValue)
            throws InvalidStatisticsParameterException, LoadVaultException, IndexPropertyException {

        statisticsVault.removeProperty(nodeType.getId(), propertyName, propertyValue);
    }

    protected void indexProperty(INodeType nodeType, Map<String, Object> params) throws AWEException {
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null) {
                indexProperty(nodeType, key, value);
            }
        }
    }
    
    protected void removeProperty(INodeType nodeType, Map<String, Object> params) throws AWEException {
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null) {
                removeProperty(nodeType, key, value);
            }
        }
    }

//    protected Object parse(INodeType nodeType, String propertyName, String propertyValue) throws AWEException {
//        return statisticsVault.parse(nodeType.getId(), propertyName, propertyValue);
//    }

    @Override
    public int getNodeCount(INodeType nodeType) {
        return statisticsVault.getNodeCount(nodeType.getId());
    }

    @Override
    public int getPropertyCount(INodeType nodeType, String propertyName) {
        return statisticsVault.getPropertyCount(nodeType.getId(), propertyName);
    }

    @Override
    public String[] getAllPropertyNames(INodeType nodeType) {
        Set<String> allProperties = statisticsVault.getAllPropertyNames(nodeType.getId());
        String[] result = new String[allProperties.size()];
        allProperties.toArray(result);
        return result;
    }

    @Override
    public String[] getAllProperties(INodeType nodeType, Class< ? > klass) {
        Set<String> allProperties = statisticsVault.getAllProperties(nodeType.getId(), klass);
        String[] result = new String[allProperties.size()];
        allProperties.toArray(result);
        return result;
    }
    
    @Override
    public Set<Object> getAllProperties(INodeType nodeType, String propertyName) {
        Map<Object, Integer> allProperties = statisticsVault.getAllProperties(nodeType.getId(), propertyName);
        return allProperties.keySet(); 
    }
    

    @Override
    public void finishUp() throws AWEException{
        statisticsService.saveVault(rootNode, statisticsVault);
    }
    
    @Override
    public Class<?> getPropertyClass(INodeType nodeType, String propertyName) {
        IVault nodeTypeVault = statisticsVault.getSubVaults().get(nodeType.getId());
        
        if (nodeTypeVault != null) {
            NewPropertyStatistics property = nodeTypeVault.getPropertyStatisticsMap().get(propertyName);
            
            if (property != null) {
                return property.getKlass();
            }
        }
        
        return null;
    }
    
    @Override
    public Object[] getPropertyValues(INodeType nodeType, String propertyName) {
        IVault nodeTypeVault = statisticsVault.getSubVaults().get(nodeType.getId());
        
        if (nodeTypeVault != null) {
            NewPropertyStatistics property = nodeTypeVault.getPropertyStatisticsMap().get(propertyName);
            if (property != null) {
                return property.getPropertyMap().keySet().toArray();
            }
        }
        
        return null;
    }
}
