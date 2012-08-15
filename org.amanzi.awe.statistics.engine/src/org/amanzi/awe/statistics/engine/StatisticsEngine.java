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

package org.amanzi.awe.statistics.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.exceptions.StatisticsEngineException;
import org.amanzi.awe.statistics.exceptions.UnderlyingModelException;
import org.amanzi.awe.statistics.impl.internal.StatisticsModelPlugin;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.period.Period;
import org.amanzi.awe.statistics.provider.IStatisticsModelProvider;
import org.amanzi.awe.statistics.template.ITemplate;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsEngine {

    /** String UNKNOWN_PROPERTY field */
    private static final String UNKNOWN_VALUE = "unknown";

    private static final Logger LOGGER = Logger.getLogger(StatisticsEngine.class);

    @SuppressWarnings("unused")
    private static class ID {

        private final IMeasurementModel model;

        private final Period period;

        private final ITemplate template;

        private final String propertyName;

        /**
         * @param model
         * @param period
         * @param template
         * @param propertyName
         */
        public ID(final IMeasurementModel model, final ITemplate template, final Period period, final String propertyName) {
            super();
            this.model = model;
            this.period = period;
            this.template = template;
            this.propertyName = propertyName;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, false);
        }

        @Override
        public boolean equals(final Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj, false);
        }

    }

    private static Map<ID, StatisticsEngine> engineCache = new HashMap<ID, StatisticsEngine>();

    private final IStatisticsModelProvider statisticsModelProvider;

    private final Period period;

    private final ITemplate template;

    private final IMeasurementModel measurementModel;

    private final String propertyName;

    /**
     * 
     */
    private StatisticsEngine(final IMeasurementModel measurementModel, final ITemplate template, final Period period, final String propertyName) {
        this(StatisticsModelPlugin.getDefault().getStatisticsModelProvider(), measurementModel, template, period, propertyName);
    }

    protected StatisticsEngine(final IStatisticsModelProvider statisticsModelProvider, final IMeasurementModel measurementModel,
            final ITemplate template, final Period period, final String propertyName) {
        this.statisticsModelProvider = statisticsModelProvider;
        this.period = period;
        this.template = template;
        this.measurementModel = measurementModel;
        this.propertyName = propertyName;
    }

    public static synchronized StatisticsEngine getEngine(final IMeasurementModel measurementModel, final ITemplate template, final Period period,
            final String propertyName) {
        ID id = new ID(measurementModel, template, period, propertyName);
        StatisticsEngine result = engineCache.get(id);

        if (result == null) {
            result = new StatisticsEngine(measurementModel, template, period, propertyName);

            engineCache.put(id, result);
        }

        return result;
    }

    public IStatisticsModel build(IProgressMonitor monitor) throws StatisticsEngineException {
        LOGGER.info("Started Statistics Calculation for Model <" + measurementModel + "> on property <" + propertyName
                + "> by template <" + template + "> with period <" + period + ">.");

        // TODO: LN: 10.08.2012, check input

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        IStatisticsModel statisticsModel = null;

        try {
            statisticsModel = statisticsModelProvider.find(measurementModel, template.getName(), propertyName);
            if (statisticsModel == null) {
                LOGGER.info("Statistics not exists in Database. Calculate new one.");

                statisticsModel = buildStatistics(monitor);

            } else {
                LOGGER.info("Statistics already exists in Database");
            }
        } catch (ModelException e) {
            LOGGER.error("An error occured on Statistics Calculation");
            throw new UnderlyingModelException(e);
        }

        LOGGER.info("Finished Statistics Calculation");

        return statisticsModel;
    }

    protected IStatisticsModel buildStatistics(final IProgressMonitor monitor) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Building statistics");
        }

        IStatisticsModel result = statisticsModelProvider.create(measurementModel, template.getName(), propertyName);

        calculateStatistics(result, period, monitor);

        return result;
    }

    protected void calculateStatistics(final IStatisticsModel statisticsModel, final Period period, final IProgressMonitor monitor)
            throws ModelException {
        Period underlyingPeriod = period.getUnderlyingPeriod();

        if (underlyingPeriod != null) {
            calculateStatistics(statisticsModel, underlyingPeriod, monitor);
        } else {
            long currentStartTime = period.getStartTime(measurementModel.getMinTimestamp());
            long nextStartTime = getNextStartDate(period, measurementModel.getMaxTimestamp(), currentStartTime);

            do {
                for (IDataElement dataElement : measurementModel.getElements(currentStartTime, nextStartTime)) {
                    String propertyValue = dataElement.contains(propertyName) ? dataElement.get(propertyName).toString() : UNKNOWN_VALUE;

                    IStatisticsGroup statisticsGroup = statisticsModel.getGroup(period.getId(), propertyValue);
                    IStatisticsRow statisticsRow = statisticsModel.getStatisticsRow(statisticsGroup, currentStartTime, nextStartTime, period.getId());

                    Map<String, Object> result = template.calculate(dataElement.asMap());
                    for (Entry<String, Object> statisticsEntry : result.entrySet()) {

                    }
                }

                currentStartTime = nextStartTime;
                nextStartTime = getNextStartDate(period, measurementModel.getMaxTimestamp(), currentStartTime);
            } while (currentStartTime < nextStartTime);
        }
    }

    private long getNextStartDate(final Period period, final long endDate, final long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if (!period.equals(Period.HOURLY) && (nextStartDate > endDate)) {
            nextStartDate = endDate;
        }
        return nextStartDate;
    }
}
