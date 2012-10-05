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

package org.amanzi.awe.distribution.engine.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.distribution.engine.DistributionEngine;
import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.awe.distribution.model.type.IDistributionType.ChartType;
import org.amanzi.awe.distribution.model.type.impl.EnumeratedDistributionType;
import org.amanzi.awe.distribution.model.type.impl.NumberDistributionRange;
import org.amanzi.awe.distribution.model.type.impl.NumberDistributionType;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionManager {

    private static final Logger LOGGER = Logger.getLogger(DistributionManager.class);

    private interface IDistributionCacheKey {

    }

    @SuppressWarnings("unused")
    private class NumberDistributionCacheKey extends EnumeratedDistributionCacheKey {

        private final NumberDistributionRange numberDistributionType;

        /**
         * @param model
         * @param nodeType
         * @param propertyName
         */
        public NumberDistributionCacheKey(final IPropertyStatisticalModel model, final INodeType nodeType,
                final String propertyName, final NumberDistributionRange numberDistributionType) {
            super(model, nodeType, propertyName);
            this.numberDistributionType = numberDistributionType;
        }

    }

    @SuppressWarnings("unused")
    private class EnumeratedDistributionCacheKey implements IDistributionCacheKey {

        private final IPropertyStatisticalModel model;

        private final INodeType nodeType;

        private final String propertyName;

        /**
         * @param model
         * @param nodeType
         * @param propertyName
         */
        public EnumeratedDistributionCacheKey(final IPropertyStatisticalModel model, final INodeType nodeType,
                final String propertyName) {
            super();
            this.model = model;
            this.nodeType = nodeType;
            this.propertyName = propertyName;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, true);
        }

        @Override
        public boolean equals(final Object o) {
            return EqualsBuilder.reflectionEquals(this, o, true);
        }

    }

    private static Map<IPropertyStatisticalModel, DistributionManager> managerCache = new HashMap<IPropertyStatisticalModel, DistributionManager>();

    private static final Map<IDistributionCacheKey, IDistributionType< ? >> distributionTypeCache = new HashMap<DistributionManager.IDistributionCacheKey, IDistributionType< ? >>();

    private final IPropertyStatisticalModel model;

    private INodeType nodeType;

    private String propertyName;

    private ChartType chartType = ChartType.getDefault();

    private IDistributionType< ? > distributionType;

    private DistributionManager(final IPropertyStatisticalModel model) {
        this.model = model;
        managerCache.put(model, this);
    }

    public static synchronized DistributionManager getManager(final IPropertyStatisticalModel model) {
        DistributionManager result = managerCache.get(model);

        if (result == null) {
            result = new DistributionManager(model);
        }

        return result;
    }

    public void setNodeType(final INodeType nodeType) {
        this.nodeType = nodeType;
    }

    public void setProperty(final String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean canBuild() {
        return (model != null) && (distributionType != null);
    }

    public void setChartType(final ChartType chartType) {
        this.chartType = chartType;
    }

    public Set<IDistributionType< ? >> getAvailableDistirbutions() {
        if ((nodeType != null) && (propertyName != null)) {
            final Class< ? > clazz = model.getPropertyStatistics().getPropertyClass(nodeType, propertyName);

            final Set<IDistributionType< ? >> result = new HashSet<IDistributionType< ? >>();

            if (clazz.equals(String.class) || clazz.equals(Boolean.class)) {
                switch (chartType) {
                case COUNTS:
                case LOGARITHMIC:
                case PERCENTS:
                    result.add(getEnumeratedDistributionType());
                    break;
                default:
                    // TODO: LN: 26.09.2012, throw exception
                }
            } else if (Number.class.isAssignableFrom(clazz)) {
                for (final NumberDistributionRange numberDistributionType : NumberDistributionRange.values()) {
                    result.add(getNumberDistributionType(numberDistributionType));
                }
            }

            return result;
        }
        return null;
    }

    public IDistributionModel build(final IProgressMonitor progressMonitor) throws ModelException {
        if (canBuild()) {
            return DistributionEngine.getEngine().build(model, distributionType, progressMonitor);
        }

        return null;
    }

    private IDistributionType< ? > getEnumeratedDistributionType() {
        final IDistributionCacheKey key = new EnumeratedDistributionCacheKey(model, nodeType, propertyName);

        IDistributionType< ? > result = distributionTypeCache.get(key);

        if (result == null) {
            LOGGER.info("Creating DistributionType by Parameters <" + model + ", " + nodeType + ", " + propertyName + ">.");

            result = new EnumeratedDistributionType(model, nodeType, propertyName);
            distributionTypeCache.put(key, result);
        }

        return result;
    }

    private IDistributionType< ? > getNumberDistributionType(final NumberDistributionRange numberDistributionType) {
        final IDistributionCacheKey key = new NumberDistributionCacheKey(model, nodeType, propertyName, numberDistributionType);

        IDistributionType< ? > result = distributionTypeCache.get(key);

        if (result == null) {
            LOGGER.info("Creating DistributionType by Parameters <" + model + ", " + nodeType + ", " + propertyName + ">.");

            result = new NumberDistributionType(model, nodeType, propertyName, numberDistributionType);
            distributionTypeCache.put(key, result);
        }

        return result;
    }

    public void setDistributionType(final IDistributionType< ? > distributionType) {
        this.distributionType = distributionType;
    }

    public IDistributionType< ? > getCurrentDistributionType() {
        return distributionType;
    }

    public Set<ChartType> getPossibleDistributionTypes() {
        if ((nodeType == null) || (propertyName == null)) {
            return null;
        }

        final Set<ChartType> chartTypes = new HashSet<IDistributionType.ChartType>();

        chartTypes.add(ChartType.COUNTS);
        chartTypes.add(ChartType.LOGARITHMIC);
        chartTypes.add(ChartType.PERCENTS);

        final Class< ? > clazz = model.getPropertyStatistics().getPropertyClass(nodeType, propertyName);
        if (!clazz.equals(String.class) && !clazz.equals(Boolean.class)) {
            chartTypes.add(ChartType.CDF);
        }

        return chartTypes;
    }
}
