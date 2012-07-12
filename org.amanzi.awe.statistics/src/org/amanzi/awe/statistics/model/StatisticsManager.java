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

package org.amanzi.awe.statistics.model;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.ITimelineModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * execute statistics building. store common statistics information
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsManager {

    /*
     * logger
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsManager.class);
    /*
     * statistics manager singleton instance
     */
    private static StatisticsManager statisticsManager;

    private StatisticsModel currentStatisticsModel;
    private ITimelineModel aggregatedModel;

    /*
     * cann't be created directly. Just through getInstance.
     */
    private StatisticsManager() {
    }

    /**
     * get instance of {@link StatisticsManager}
     * 
     * @return
     */
    public static StatisticsManager getInstance() {
        if (statisticsManager == null) {
            statisticsManager = new StatisticsManager();
        }
        return statisticsManager;
    }

    /**
     * @param template statistics template
     * @param parentModel model which implements {@link ITimelineModel} interface
     * @param propertyName property which should be aggregated
     * @param period period for aggregation
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public void processStatistics(Object template, ITimelineModel parentModel, String propertyName, Period period,
            IProgressMonitor monitor) throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("Process statistics calculation");
        try {
            currentStatisticsModel = new StatisticsModel(parentModel.getRootNode(), "template");
        } catch (DatabaseException e) {
            LOGGER.error("Can't instantiate statistics model ", e);
        }
        aggregatedModel = parentModel;
        Dimension timeDimension = currentStatisticsModel.getDimension(DimensionTypes.TIME);
        Dimension networkDimension = currentStatisticsModel.getDimension(DimensionTypes.NETWORK);
        StatisticsLevel networkLevel = networkDimension.getLevel(propertyName);
        StatisticsLevel timeLevel = timeDimension.getLevel(period.getId());
        buildStatistics(timeLevel, networkLevel, template, monitor);
    }

    /**
     * @param timeLevel
     * @param networkLevel
     * @param template
     * @param monitor
     * @throws DatabaseException
     */
    private AggregatedStatistics buildStatistics(StatisticsLevel timeLevel, StatisticsLevel networkLevel, Object template,
            IProgressMonitor monitor) throws DatabaseException {
        StatisticsLevel sourceTimeLevel = timeLevel.getSourceLevel();
        if (sourceTimeLevel != null) {
            AggregatedStatistics statistics = networkLevel.findAggregatedStatistics(sourceTimeLevel);
            if (statistics == null) {
                statistics = buildStatistics(sourceTimeLevel, networkLevel, template, monitor);
            }
            final String task = "Building stats for " + timeLevel.getName() + "/" + networkLevel;
            LOGGER.debug(task);
            monitor.subTask(task);
            // AggregatedStatistics statistics = buildHighLevelPeriodStatistics(template, startTime,
            // endTime, period, networkLevel,
            // networkDimension, timeDimension, uStatistics, monitor);
            // updateFlags(statistics);
            return statistics;

        } else {
            // return buildLowerLevel(timeLevel, networkLevel, template, monitor);
        }
        return null;
    }

    // /**
    // * @param timeLevel
    // * @param networkLevel
    // * @param template
    // * @param monitor
    // * @return
    // * @throws IllegalNodeDataException
    // * @throws DatabaseException
    // */
    // private AggregatedStatistics buildLowerLevel(StatisticsLevel timeLevel, StatisticsLevel
    // networkLevel, Object template,
    // IProgressMonitor monitor) throws DatabaseException, IllegalNodeDataException {
    // int rowAdded = 0;
    // final String task = "Building stats for " + timeLevel.getName() + "/" +
    // networkLevel.getName();
    // LOGGER.debug(task);
    // monitor.subTask(task);
    // Period period = Period.findById(timeLevel.getName());
    // AggregatedStatistics statistics = networkLevel.getAggregateStatistics(timeLevel);
    // // String hash = createScriptForTemplate(template);
    // long noUsedNodes = 0;
    // long currentStartTime = period.getStartTime(currentStatisticsModel.getMinTimestamp());
    // long nextStartTime = getNextStartDate(period, currentStatisticsModel.getMaxTimestamp(),
    // currentStartTime);
    // long oldCount = 0;
    // long count = 0;
    // int comm = 0;
    // do {
    // long startForPeriod = System.currentTimeMillis();
    // String debugInfo = "currentStartTime=" + currentStartTime + "\tnextStartTime=" +
    // nextStartTime + "\tendTime="
    // + currentStatisticsModel.getMaxTimestamp();
    // LOGGER.debug(debugInfo);
    // if (monitor.isCanceled()) {
    // break;
    // }
    //
    // long t = System.currentTimeMillis();
    // Iterable<IDataElement> elements =
    // aggregatedModel.findAllElementsByTimestampPeriod(currentStartTime, nextStartTime);
    // long cellCalcTime = 0L;
    // long startFindGroup = 0L;
    // for (IDataElement element : elements) {
    // count++;
    // boolean isUsed = false;
    // }
    // } while (currentStartTime < currentStatisticsModel.getMaxTimestamp());
    // return null;
    // }

    public static long getNextStartDate(Period period, long endDate, long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if (!period.equals(Period.HOURLY) && (nextStartDate > endDate)) {
            nextStartDate = endDate;
        }
        return nextStartDate;
    }
}
