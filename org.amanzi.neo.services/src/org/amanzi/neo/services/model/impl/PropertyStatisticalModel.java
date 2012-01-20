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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.StatisticsService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IPropertyStatisticalModel;
import org.amanzi.neo.services.statistic.IVault;
import org.amanzi.neo.services.statistic.internal.PropertyStatistics;
import org.neo4j.graphdb.Node;

/**
 * Model to work with statistics
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class PropertyStatisticalModel extends DataModel implements IPropertyStatisticalModel {

    /**
     * @param nodeType
     */
    protected PropertyStatisticalModel(INodeType nodeType) {
        super(nodeType);
    }

    private StatisticsService statisticsService = NeoServiceFactory.getInstance().getStatisticsService();

    protected IVault statisticsVault;

    protected List<String> notNecessaryListOfProperties = new ArrayList<String>();

    protected List<String> uniqueListOfProperties = new ArrayList<String>();

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

    @Override
    public boolean isUniqueProperties(String property) {
        return uniqueListOfProperties.contains(property) ? true : false;
    }

    /**
     * Method to initialize statistics from other models
     */
    protected void initializeStatistics() {
        try {
            statisticsVault = statisticsService.loadVault(getRootNode());
            fillListOfNotNecessaryProperties();
        } catch (AWEException e) {
            // TODO: LN: handle exception
        }
    }

    /**
     * Method to add property in statistics by type of node and name of property
     * 
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @param propertyValue Value of property
     * @throws AWEException
     */
    protected void indexProperty(INodeType nodeType, String propertyName, Object propertyValue) throws AWEException {

        if (!notNecessaryListOfProperties.contains(propertyName)) {
            statisticsVault.indexProperty(nodeType.getId(), propertyName, propertyValue);
        }
    }

    /**
     * Method to remove property from statistics by type of node and name of property
     * 
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @param propertyValue Value of property
     * @throws AWEException
     */
    protected void removeProperty(INodeType nodeType, String propertyName, Object propertyValue) throws AWEException {

        if (!notNecessaryListOfProperties.contains(propertyName)) {
            statisticsVault.removeProperty(nodeType.getId(), propertyName, propertyValue);
        }
    }

    /**
     * Method to rename property from statistics from old value of property to new value of property
     * 
     * @param nodeType Type of node
     * @param propertyName Name of property
     * @param oldPropValue Old value of property
     * @param newPropValue New value of property
     * @throws AWEException
     */
    protected void renameProperty(INodeType nodeType, String propertyName, Object oldPropValue, Object newPropValue)
            throws AWEException {
        statisticsVault.renamePropertyValue(nodeType.getId(), propertyName, oldPropValue, newPropValue);
    }

    /**
     * Method to add all properties from dataElement in statistics by type of node and name of
     * property
     * 
     * @param nodeType Type of node
     * @param dataElement Element which contains map with properties
     * @throws AWEException
     */
    protected void indexProperty(INodeType nodeType, Map<String, Object> dataElement) throws AWEException {
        for (String key : dataElement.keySet()) {
            Object value = dataElement.get(key);
            if (value != null) {
                indexProperty(nodeType, key, value);
            }
        }
    }

    /**
     * Method to remove all properties from dataElement in statistics by type of node and name of
     * property
     * 
     * @param nodeType Type of node
     * @param dataElement Element which contains map with properties
     * @throws AWEException
     */
    protected void removeProperty(INodeType nodeType, Map<String, Object> dataElement) throws AWEException {
        Node nodeFromDataElement = ((DataElement)dataElement).getNode();
        for (String propertyName : nodeFromDataElement.getPropertyKeys()) {
            Object propertyValue = nodeFromDataElement.getProperty(propertyName);
            if (propertyValue != null) {
                removeProperty(nodeType, propertyName, propertyValue);
            }
        }
    }

    // protected Object parse(INodeType nodeType, String propertyName, String propertyValue) throws
    // AWEException {
    // return statisticsVault.parse(nodeType.getId(), propertyName, propertyValue);
    // }

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
    public Number getMinValue(INodeType nodeType, String propertyName) {
        return statisticsVault.getMinValue(nodeType.getId(), propertyName);
    }

    @Override
    public Number getMaxValue(INodeType nodeType, String propertyName) {
        return statisticsVault.getMaxValue(nodeType.getId(), propertyName);
    }

    @Override
    public void finishUp() throws AWEException {
        if (statisticsVault.isStatisticsChanged()) {
            statisticsService.saveVault(rootNode, statisticsVault);
        }
        super.finishUp();
    }

    @Override
    public Class< ? > getPropertyClass(INodeType nodeType, String propertyName) {
        IVault nodeTypeVault = statisticsVault.getSubVaults().get(nodeType.getId());

        if (nodeTypeVault != null) {
            PropertyStatistics property = nodeTypeVault.getPropertyStatisticsMap().get(propertyName);

            if (property != null) {
                return property.getKlass();
            }
        }

        return null;
    }

    @Override
    public int getPropertyValueCount(INodeType nodeType, String propertyName, Object propertyValue) {
        return statisticsVault.getPropertyValueCount(nodeType.getId(), propertyName, propertyValue);
    }

    @Override
    public Object[] getPropertyValues(INodeType nodeType, String propertyName) {
        IVault nodeTypeVault = statisticsVault.getSubVaults().get(nodeType.getId());

        if (nodeTypeVault != null) {
            PropertyStatistics property = nodeTypeVault.getPropertyStatisticsMap().get(propertyName);
            if (property != null) {
                return property.getPropertyMap().keySet().toArray();
            }
        }

        return null;
    }
}
