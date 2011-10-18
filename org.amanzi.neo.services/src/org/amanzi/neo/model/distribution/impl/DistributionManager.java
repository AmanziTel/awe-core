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

package org.amanzi.neo.model.distribution.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistribution.ChartType;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.types.impl.StringDistribution;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.apache.log4j.Logger;

/**
 * Manager for Distribution types
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionManager {
    
    /**
     * Exception on Creating Distribution
     * 
     * @author gerzog
     * @since 1.0.0
     */
    public static class DistributionManagerException extends AWEException {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;
        
        public DistributionManagerException(Class<?> clazz, ChartType chartType) {
            super("Impossible to create chart <" + chartType + "> on class <" + clazz.getSimpleName() + ">.");
        }
        
    }
    
    private static final Logger LOGGER = Logger.getLogger(DistributionManager.class);
    
    /*
     * Separator for names in Key of Cache
     */
    private static final String CACHE_KEY_SEPARATOR = "@";
    
    /*
     * Instance of Manager
     */
    private static DistributionManager manager;
    
    /*
     * Cache of Distributions
     */
    private Map<String, IDistribution> distributionCache = new HashMap<String, IDistribution>();
    
    /**
     * Returns instance of this Manager
     */
    public static DistributionManager getManager() {
        if (manager == null) {
            manager = new DistributionManager();
        }
        
        return manager;
    }
    
    /**
     * Private constructor, to prevent non-singleton access
     */
    private DistributionManager() {
        
    }
    
    public List<IDistribution> getDistributions(IDistributionalModel model, INodeType nodeType, String propertyName, ChartType chartType) throws DistributionManagerException {
        LOGGER.debug("start getDistributions(<" + model + ">, <" + nodeType + ">, " + propertyName + ">, <" + chartType + ">)");
        
        Class<?> clazz = model.getPropertyClass(nodeType, propertyName);
        
        List<IDistribution> result = new ArrayList<IDistribution>();
        
        if (clazz.equals(String.class)) {
            switch (chartType) {
            case COUNTS:
            case LOGARITHMIC:
            case PERCENTS:
                result.add(getStringDistribution(model, nodeType, propertyName));
                break;
            default:
                throw new DistributionManagerException(clazz, chartType);
            }
        } else if (clazz.isAssignableFrom(Number.class)){ 
            //TODO: create Number distribution
        } else {
            //TODO: try to find user-defined distributions
        }
        
        LOGGER.debug("finish getDistributions()");
        
        return result;
    }
    
    /**
     * Computes key of String Distribution in Cache
     *
     * @param model
     * @param nodeType
     * @param propertyName
     * @return
     */
    private String getStringDistributionCacheKey(IDistributionalModel model, INodeType nodeType, String propertyName) {
        StringBuilder result = new StringBuilder(model.getName());
        
        result.append(CACHE_KEY_SEPARATOR).append(nodeType.getId()).
               append(CACHE_KEY_SEPARATOR).append(propertyName);
        
        return result.toString();
    }
    
    /**
     * Tries to find String Distribution in Cache
     * 
     * Creates new one if nothing found and put it to cache
     *
     * @param model
     * @param nodeType
     * @param propertyName
     * @return
     */
    private IDistribution getStringDistribution(IDistributionalModel model, INodeType nodeType, String propertyName) {
        String cacheKey = getStringDistributionCacheKey(model, nodeType, propertyName);
        IDistribution result = distributionCache.get(cacheKey);
        
        if (result == null) {
            result = new StringDistribution(model, nodeType, propertyName);
            distributionCache.put(cacheKey, result);
        }
        
        return result;
    }

}
