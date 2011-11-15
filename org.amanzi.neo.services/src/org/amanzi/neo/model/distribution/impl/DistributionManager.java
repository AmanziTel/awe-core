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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistribution.ChartType;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.types.impl.EnumeratedDistribution;
import org.amanzi.neo.model.distribution.types.impl.NumberDistribution;
import org.amanzi.neo.model.distribution.types.impl.NumberDistributionType;
import org.amanzi.neo.model.distribution.xml.DistributionXmlParser;
import org.amanzi.neo.model.distribution.xml.DistributionXmlParsingException;
import org.amanzi.neo.model.distribution.xml.schema.Distribution;
import org.amanzi.neo.services.DistributionService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;

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

        public DistributionManagerException(Class< ? > clazz, ChartType chartType) {
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
    private Map<String, IDistribution< ? >> distributionCache = new HashMap<String, IDistribution< ? >>();

    /*
     * Reuse plugin that contains bundle with distribution xml files
     */
    private Plugin reusePlugin;
    public static final String DISTRIBUTION_XML_PATH = "distributions";

    /*
     * Distribution Service
     */
    private static DistributionService distributionService = NeoServiceFactory.getInstance().getDistributionService();

    /**
     * Returns instance of this Manager
     */
    public static DistributionManager getManager() {
        if (manager == null) {
            manager = new DistributionManager();
        }

        return manager;
    }

    public void registerReusePlugin(Plugin plugin) {
        this.reusePlugin = plugin;
    }

    /**
     * Private constructor, to prevent non-singleton access
     */
    private DistributionManager() {

    }

    /**
     * Returns list of Distributions available for current parameters including User Defined
     * Distributions.
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     * @param chartType
     * @return
     * @throws DistributionManagerException
     */
    public List<IDistribution< ? >> getDistributions(IDistributionalModel model, INodeType nodeType, String propertyName,
            ChartType chartType) throws DistributionManagerException {
        return getDistributions(model, nodeType, propertyName, chartType, true);
    }

    /**
     * Returns list of Distributions available for current parameters. User Defined Distributions
     * include if includeUserDefined is true.
     * 
     * @param model model to Analyze
     * @param nodeType type of Node to Analyze
     * @param propertyName property to Analyze
     * @param chartType type of output chart
     * @param includeUserDefined
     * @return
     * @throws DistributionManagerException
     */
    @SuppressWarnings("unchecked")
    public List<IDistribution< ? >> getDistributions(IDistributionalModel model, INodeType nodeType, String propertyName,
            ChartType chartType, boolean includeUserDefined) throws DistributionManagerException {
        LOGGER.debug("start getDistributions(<" + model + ">, <" + nodeType + ">, " + propertyName + ">, <" + chartType + ">, <" + includeUserDefined + ">)");

        // check input
        if (model == null) {
            LOGGER.error("Analyzed model cannot be null");
            throw new IllegalArgumentException("Analyzed model cannot be null");
        }
        if (nodeType == null) {
            LOGGER.error("NodeType cannot be null");
            throw new IllegalArgumentException("NodeType cannot be null");
        }
        if (propertyName == null || propertyName.isEmpty()) {
            LOGGER.error("PropertyName to Analyze cannot be null or empty");
            throw new IllegalArgumentException("PropertyName to Analyze cannot be null or empty");
        }
        if (chartType == null) {
            LOGGER.error("ChartType cannot be null");
            throw new IllegalArgumentException("ChartType cannot be null");
        }

        Class< ? > clazz = model.getPropertyClass(nodeType, propertyName);

        List<IDistribution< ? >> result = new ArrayList<IDistribution< ? >>();

        if (clazz.equals(String.class) || clazz.equals(Boolean.class)) {
            switch (chartType) {
            case COUNTS:
            case LOGARITHMIC:
            case PERCENTS:
                result.add(getStringDistribution(model, nodeType, propertyName));
                break;
            default:
                throw new DistributionManagerException(clazz, chartType);
            }
        } else if (Number.class.isAssignableFrom(clazz)) {
            for (NumberDistributionType distrType : NumberDistributionType.values()) {
                result.add(getNumberDistribution(model, nodeType, propertyName, distrType));
            }
            result.add(getStringDistribution(model, nodeType, propertyName));
        }

        if (includeUserDefined) {
            // get user defined distributions from reuse plugin
            if (reusePlugin != null) {
                Enumeration<URL> xmlDistr = reusePlugin.getBundle().findEntries(DISTRIBUTION_XML_PATH, "*.xml", false);
                while (xmlDistr.hasMoreElements()) {
                    try {
                        URL url = xmlDistr.nextElement();
                        IDistribution< ? > distribution = getUserDefinedDistribution(model, nodeType, propertyName, url.getFile(),
                                new DistributionXmlParser(url));
                        if (distribution != null) {
                            result.add(distribution);
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }

            // get user defined distributions from database
            List<Distribution> userDefinedDistributions = distributionService.findUserDefinedDistributions();
            for (Distribution userDefinedDistribution : userDefinedDistributions) {
                try {
                    IDistribution< ? > distribution = getUserDefinedDistribution(model, nodeType, propertyName,
                            userDefinedDistribution.getData().getName(), new DistributionXmlParser(userDefinedDistribution));
                    if (distribution != null) {
                        result.add(distribution);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }

        LOGGER.debug("finish getDistributions()");

        return result;
    }

    /**
     * Returns array of possible chart types for current properties
     * 
     * @param analyzedModel model to Analyze
     * @param nodeType type of node to Analyze
     * @param propertyName name of Property to Analyze
     * @return
     */
    public ChartType[] getPossibleChartTypes(IDistributionalModel analyzedModel, INodeType nodeType, String propertyName) {
        LOGGER.debug("start getPossibleChartTypes(<" + analyzedModel + ">, <" + nodeType + ">, <" + propertyName + ">)");

        // check input
        if (analyzedModel == null) {
            LOGGER.error("Input analyzedModel is null");
            throw new IllegalArgumentException("Input analyzedModel is null");
        }
        if (nodeType == null) {
            LOGGER.error("Input nodeType is null");
            throw new IllegalArgumentException("Input nodeType is null");
        }
        if (StringUtils.isEmpty(propertyName)) {
            LOGGER.error("Input propertyName is null or empty");
            throw new IllegalArgumentException("Input propertyName is null or empty");
        }

        // create list of Charts
        List<ChartType> result = new ArrayList<ChartType>();

        // add chart types for all classees
        result.add(ChartType.COUNTS);
        result.add(ChartType.LOGARITHMIC);
        result.add(ChartType.PERCENTS);

        // get type of property
        Class< ? > klass = analyzedModel.getPropertyClass(nodeType, propertyName);
        if (!klass.equals(String.class) && !klass.equals(Boolean.class)) {
            result.add(ChartType.CDF);
        }

        LOGGER.debug("finish getPossibleChartTypes()");

        return result.toArray(new ChartType[0]);
    }

    /**
     * Creates Distribution from XML file
     *
     * @param path
     * @throws FileNotFoundException
     * @throws DistributionXmlParsingException
     * @throws IOException
     * @throws DatabaseException
     */
    public void createDistributionFromFile(String path) throws FileNotFoundException, DistributionXmlParsingException, IOException,
            DatabaseException {
        DistributionXmlParser xmlParser = new DistributionXmlParser(new FileInputStream(path), path);
        distributionService.createUserDefinedDistribution(xmlParser.getXmlDistr());
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

        result.append(CACHE_KEY_SEPARATOR).append(nodeType.getId()).append(CACHE_KEY_SEPARATOR).append(propertyName);

        return result.toString();
    }

    /**
     * Tries to find Distribution for Strings and Enums in Cache Creates new one if nothing found and put it to
     * cache
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     * @return
     */
    private IDistribution< ? > getStringDistribution(IDistributionalModel model, INodeType nodeType, String propertyName) {
        String cacheKey = getStringDistributionCacheKey(model, nodeType, propertyName);
        IDistribution< ? > result = distributionCache.get(cacheKey);

        if (result == null) {
            LOGGER.info("No Distribution for params <" + model + ", " + nodeType + ", " + propertyName + ">. " + "Create new one.");

            result = new EnumeratedDistribution(model, nodeType, propertyName);
            distributionCache.put(cacheKey, result);
        }

        return result;
    }

    /**
     * Computes key of Number Distribution in Cache
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     * @param distrType
     * @return
     */
    private String getNumberDistributionCacheKey(IDistributionalModel model, INodeType nodeType, String propertyName,
            NumberDistributionType distrType) {
        StringBuilder result = new StringBuilder(model.getName());

        result.append(CACHE_KEY_SEPARATOR).append(nodeType.getId()).append(CACHE_KEY_SEPARATOR).append(propertyName);
        result.append(CACHE_KEY_SEPARATOR).append(distrType);

        return result.toString();
    }

    /**
     * Tries to find Distribution in Cache Creates new one if nothing found and put it to
     * cache
     *
     * @param model
     * @param nodeType
     * @param propertyName
     * @param distrType
     * @return
     */
    private IDistribution< ? > getNumberDistribution(IDistributionalModel model, INodeType nodeType, String propertyName,
            NumberDistributionType distrType) {
        String cacheKey = getNumberDistributionCacheKey(model, nodeType, propertyName, distrType);
        IDistribution< ? > result = distributionCache.get(cacheKey);

        if (result == null) {
            LOGGER.info("No Distribution for params <" + model + ", " + nodeType + ", " + propertyName + ", " + distrType + ">. "
                    + "Create new one.");

            result = new NumberDistribution(model, nodeType, propertyName, distrType);
            distributionCache.put(cacheKey, result);
        }

        return result;
    }

    /**
     * Computes key of User-Defined Distribution in Cache
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     * @param distrName
     * @return
     */
    private String getUserDefinedDistributionCacheKey(IDistributionalModel model, INodeType nodeType, String propertyName,
            String distrName) {
        StringBuilder result = new StringBuilder(model.getName());

        result.append(CACHE_KEY_SEPARATOR).append(nodeType.getId()).append(CACHE_KEY_SEPARATOR).append(propertyName);
        result.append(CACHE_KEY_SEPARATOR).append(distrName);

        return result.toString();
    }

    /**
     * Tries to find User-Defined Distribution in Cache Creates new one if nothing found and put it
     * to cache
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     * @param url
     * @return
     * @throws DistributionXmlParsingException
     * @throws IOException
     */
    private IDistribution< ? > getUserDefinedDistribution(IDistributionalModel model, INodeType nodeType, String propertyName,
            String distrName, DistributionXmlParser distrXmlParser) throws DistributionXmlParsingException, IOException {
        String cacheKey = getUserDefinedDistributionCacheKey(model, nodeType, propertyName, distrName);
        IDistribution< ? > result = distributionCache.get(cacheKey);

        if (result == null) {
            LOGGER.info("No Distribution for params <" + model + ", " + nodeType + ", " + propertyName + ", " + distrName + ", "
                    + distrXmlParser + ">. " + "Create new one.");
            if (distrXmlParser.checkCompatibility(model, nodeType, propertyName)) {
                result = distrXmlParser.getDistribution(model, nodeType, propertyName);
                distributionCache.put(cacheKey, result);
            }
        }

        return result;
    }
}
