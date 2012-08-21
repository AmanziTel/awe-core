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

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.exceptions.FatalStatisticsException;
import org.amanzi.awe.statistics.exceptions.StatisticsEngineException;
import org.amanzi.awe.statistics.exceptions.UnderlyingModelException;
import org.amanzi.awe.statistics.impl.internal.StatisticsModelPlugin;
import org.amanzi.awe.statistics.internal.StatisticsPlugin;
import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.period.Period;
import org.amanzi.awe.statistics.provider.IStatisticsModelProvider;
import org.amanzi.awe.statistics.template.ITemplate;
import org.amanzi.awe.statistics.template.ITemplateColumn;
import org.amanzi.awe.statistics.template.functions.IAggregationFunction;
import org.amanzi.neo.core.transactional.AbstractTransactional;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.jruby.RubySymbol;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsEngine extends AbstractTransactional {

    private static final Logger LOGGER = Logger.getLogger(StatisticsEngine.class);

    private static final String UNKNOWN_VALUE = "unknown";

    @SuppressWarnings("unused")
    private static class ID {

        private final IMeasurementModel model;

        private final ITemplate template;

        private final String propertyName;

        private final Period period;

        /**
         * @param model
         * @param period
         * @param template
         * @param propertyName
         */
        public ID(final IMeasurementModel model, final ITemplate template, final Period period, final String propertyName) {
            super();
            this.model = model;
            this.template = template;
            this.propertyName = propertyName;
            this.period = period;
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

    private final ITemplate template;

    private final IMeasurementModel measurementModel;

    private final String propertyName;

    private final Period period;

    private final Map<Pair<IStatisticsRow, ITemplateColumn>, IAggregationFunction> functionCache = new HashMap<Pair<IStatisticsRow, ITemplateColumn>, IAggregationFunction>();

    /**
     * 
     */
    private StatisticsEngine(final IMeasurementModel measurementModel, final ITemplate template, final Period period,
            final String propertyName) {
        this(StatisticsModelPlugin.getDefault().getStatisticsModelProvider(), measurementModel, template, period, propertyName);
    }

    protected StatisticsEngine(final IStatisticsModelProvider statisticsModelProvider, final IMeasurementModel measurementModel,
            final ITemplate template, final Period period, final String propertyName) {
        super();
        this.statisticsModelProvider = statisticsModelProvider;
        this.template = template;
        this.measurementModel = measurementModel;
        this.propertyName = propertyName;
        this.period = period;
    }

    public static synchronized StatisticsEngine getEngine(final IMeasurementModel measurementModel, final ITemplate template,
            final Period period, final String propertyName) {
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
                + "> by template <" + template + ">.");

        // TODO: LN: 10.08.2012, check input

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        startTransaction();

        IStatisticsModel statisticsModel = null;

        boolean isSuccess = false;

        monitor.beginTask("Statistics calculation", period.ordinal() + 1);

        try {
            statisticsModel = statisticsModelProvider.find(measurementModel, template.getName(), propertyName);
            if (statisticsModel == null) {
                LOGGER.info("Statistics not exists in Database. Create new one.");
                statisticsModel = statisticsModelProvider.create(measurementModel, template.getName(), propertyName);
            }

            buildStatistics(statisticsModel, monitor);

            isSuccess = true;
        } catch (ModelException e) {
            LOGGER.error("An error occured on Statistics Calculation", e);
            throw new UnderlyingModelException(e);
        } catch (Exception e) {
            LOGGER.error("An error occured on Statistics Calculation", e);
            throw new FatalStatisticsException(e);
        }

        saveTx(isSuccess, false);

        monitor.done();

        LOGGER.info("Finished Statistics Calculation");

        return statisticsModel;
    }

    protected void buildStatistics(final IStatisticsModel statisticsModel, final IProgressMonitor monitor) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Building statistics");
        }

        try {
            calculateStatistics(statisticsModel, period, monitor);
        } catch (Exception e) {
            LOGGER.error("Error on calculating statistics", e);
        } finally {
            statisticsModel.finishUp();
        }
    }

    protected void calculateHighLevelStatistics(final IStatisticsModel statisticsModel, final Period currentPeriod, final Period previousPeriod, final IProgressMonitor monitor) throws ModelException {
        String subTaskName = "Period <" + period + ">";
        IProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 1);
        monitor.subTask(subTaskName);
        subProgressMonitor.beginTask(subTaskName, statisticsModel.getLevelCount(DimensionType.TIME, previousPeriod.getId()));

        int periodCount = 0;
        for (IStatisticsRow previousStatisticsRow : statisticsModel.getStatisticsRows(previousPeriod.getId())) {
            IStatisticsGroup previousStatisticsGroup = previousStatisticsRow.getStatisticsGroup();

            IStatisticsGroup currentStatisticsGroup = statisticsModel.getStatisticsGroup(currentPeriod.getId(), previousStatisticsGroup.getPropertyValue());

            long startTime = period.getStartTime(previousStatisticsRow.getStartDate());
            long endTime = period.getEndTime(startTime);

            IStatisticsRow currentStatisticsRow = statisticsModel.getStatisticsRow(currentStatisticsGroup, previousStatisticsRow, startTime, endTime);

            for (IStatisticsCell previousStatisticsCell : previousStatisticsRow.getStatisticsCells()) {
                String columnName = previousStatisticsCell.getName();
                ITemplateColumn column = template.getColumn(previousStatisticsCell.getName());

                Number statisticsValue = previousStatisticsCell.getValue();
                Number statisticsResult = null;
                if ((statisticsValue != null) && (statisticsValue instanceof Number)) {
                    statisticsResult = calculateValue(currentStatisticsRow, column, statisticsValue);
                }

                if (statisticsModel.updateStatisticsCell(currentStatisticsRow, columnName, statisticsResult, previousStatisticsCell)) {
                    periodCount++;
                }
            }
        }

        statisticsModel.setLevelCount(DimensionType.TIME, currentPeriod.getId(), periodCount);
        statisticsModel.flush();
        subProgressMonitor.done();

        flush();
    }

    protected void calculateStatistics(final IStatisticsModel statisticsModel, final Period period, final IProgressMonitor monitor)
            throws ModelException, StatisticsEngineException {
        if (statisticsModel.containsLevel(DimensionType.TIME, period.getId())) {
            LOGGER.info("Statistics Model already contain Period <" + period + ">.");

            monitor.worked(1);
        } else {
            LOGGER.info("Statistics Model didn't contain Period <" + period + ">. Calculate new one");
            Period underlyingPeriod = period.getUnderlyingPeriod();

            if (underlyingPeriod != null) {
                calculateStatistics(statisticsModel, underlyingPeriod, monitor);

                calculateHighLevelStatistics(statisticsModel, period, underlyingPeriod, monitor);

                monitor.worked(1);
            } else {
                long currentStartTime = period.getStartTime(measurementModel.getMinTimestamp());
                long nextStartTime = getNextStartDate(period, measurementModel.getMaxTimestamp(), currentStartTime);

                String subTaskName = "Period <" + period + ">";
                IProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 1);
                monitor.subTask(subTaskName);
                subProgressMonitor.beginTask(subTaskName,
                        measurementModel.getPropertyStatistics().getCount(measurementModel.getMainMeasurementNodeType()));

                int periodCount = 0;

                do {
                    for (IDataElement dataElement : measurementModel.getElements(currentStartTime, nextStartTime)) {
                        String propertyValue = dataElement.contains(propertyName) ? dataElement.get(propertyName).toString()
                                : UNKNOWN_VALUE;

                        IStatisticsGroup statisticsGroup = statisticsModel.getStatisticsGroup(period.getId(), propertyValue);
                        IStatisticsRow statisticsRow = statisticsModel.getStatisticsRow(statisticsGroup, currentStartTime, nextStartTime);

                        try {
                            Map<RubySymbol, Object> rubySymbolMap = StatisticsPlugin.getDefault().getRuntimeWrapper()
                                    .toSymbolMap(dataElement.asMap());
                            IRubyObject rubyDataElement = StatisticsPlugin.getDefault().getRuntimeWrapper().wrap(rubySymbolMap);
                            Map<String, Object> result = template.calculate(rubyDataElement);
                            for (Entry<String, Object> statisticsEntry : result.entrySet()) {
                                ITemplateColumn column = template.getColumn(statisticsEntry.getKey());

                                Object statisticsValue = statisticsEntry.getValue();

                                if (LOGGER.isTraceEnabled()) {
                                    LOGGER.trace("Statistics Calculation result for Cell <" + column.getName() + "> in Row <"
                                            + statisticsRow + ">: <" + statisticsValue + ">.");
                                }

                                Number statisticsResult = null;
                                if ((statisticsValue != null) && (statisticsValue instanceof Number)) {
                                    statisticsResult = calculateValue(statisticsRow, column, (Number)statisticsValue);
                                }

                                if (statisticsModel.updateStatisticsCell(statisticsRow, column.getName(), statisticsResult, dataElement)) {
                                    periodCount++;
                                }

                                updateTransaction();
                            }
                        } catch (Exception e) {
                            LOGGER.error("Error on calculating statistics by template on element " + dataElement + ".");
                            throw new FatalStatisticsException(e);
                        }

                        subProgressMonitor.worked(1);
                        if (subProgressMonitor.isCanceled()) {
                            break;
                        }
                    }

                    currentStartTime = nextStartTime;
                    nextStartTime = getNextStartDate(period, measurementModel.getMaxTimestamp(), currentStartTime);

                    if (monitor.isCanceled()) {
                        break;
                    }
                } while (currentStartTime < measurementModel.getMaxTimestamp());

                statisticsModel.setLevelCount(DimensionType.TIME, period.getId(), periodCount);
                statisticsModel.flush();

                subProgressMonitor.done();

                flush();
            }
        }
    }

    private Number calculateValue(final IStatisticsRow statisticsRow, final ITemplateColumn templateColumn, final Number value) {
        Pair<IStatisticsRow, ITemplateColumn> key = new ImmutablePair<IStatisticsRow, ITemplateColumn>(statisticsRow, templateColumn);

        IAggregationFunction function = functionCache.get(key);

        if (function == null) {
            function = templateColumn.getFunction();

            functionCache.put(key, function);
        }

        return function.update(value).getResult();
    }

    private long getNextStartDate(final Period period, final long endDate, final long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if (!period.equals(Period.HOURLY) && (nextStartDate > endDate)) {
            nextStartDate = endDate;
        }
        return nextStartDate;
    }

    protected void flush() {
        functionCache.clear();
    }
}
