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

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.impl.AbstractModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Statistics Model play role of container for {@link PeriodStatisticsModel}. may store some common
 * information about statistics;
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsModel extends AbstractModel {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsModel.class);
    private static final String STATISTICS_POSTFIX = "Statistics";
    private static final String SPACE_SEPARATOR = " ";
    private Node parentNode;
    private PeriodStatisticsModel highestPeriod;
    private Long maxTimestamp;
    private Long minTimestamp;

    static void setStatisticsService(StatisticsService service) {
        statisticService = service;
    }

    static StatisticsService statisticService;

    /*
     * /** initialize statistics services
     */
    private static void initStatisticsService() {
        if (statisticService == null) {
            statisticService = StatisticsService.getInstance();
        }
    }

    /**
     * create new statistics if not exist .else initialize existed.
     * 
     * @param parentNode
     * @throws IllegalArgumentException
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     */
    public StatisticsModel(Node parentNode) throws IllegalArgumentException, DatabaseException {
        super(StatisticsNodeTypes.STATISTICS);
        initStatisticsService();
        if (parentNode == null) {
            LOGGER.error("parentNode is null");
            throw new IllegalArgumentException("parentNode can't be null");
        }
        this.parentNode = parentNode;
        this.name = (String)parentNode.getProperty(DatasetService.NAME, StringUtils.EMPTY) + SPACE_SEPARATOR + STATISTICS_POSTFIX;
        rootNode = statisticService.findStatistic(parentNode, name);
        if (rootNode == null) {
            rootNode = statisticService.createStatisticsModelRoot(parentNode, name);
        }
        initPeriodsModel();
    }

    /**
     * initialize periodsList
     * 
     * @throws DatabaseException
     */
    private void initPeriodsModel() throws DatabaseException {
        minTimestamp = (Long)parentNode.getProperty(DriveModel.MIN_TIMESTAMP);
        maxTimestamp = (Long)parentNode.getProperty(DriveModel.MAX_TIMESTAMP);
        LOGGER.info("minTimestamp= " + minTimestamp + " maxTimestamp=" + maxTimestamp);
        if (minTimestamp == null || maxTimestamp == null) {
            LOGGER.info("missing required parametrs");
            return;
        }
        Iterable<Node> existedPeriods = statisticService.getAllPeriods(rootNode);
        if (existedPeriods != null) {
            highestPeriod = new PeriodStatisticsModel(statisticService.getHighestPeriod(existedPeriods));
            return;
        }
        Period highestPeriod = Period.getHighestPeriod(minTimestamp, maxTimestamp);
        createPeriods(highestPeriod);
    }

    /**
     * initialize period chain
     * 
     * @throws DatabaseException
     */
    private Node createPeriods(Period period) throws DatabaseException {
        PeriodStatisticsModel periodModel = new PeriodStatisticsModel(rootNode, period);
        if (highestPeriod == null) {
            highestPeriod = periodModel;
        }
        Period underlinePeriod = period.getUnderlyingPeriod();
        if (period.getUnderlyingPeriod() != null) {
            Node underline = createPeriods(underlinePeriod);
            periodModel.addSourcePeriod(new PeriodStatisticsModel(underline));
        }
        return periodModel.getRootNode();

    }

    /**
     * return highest period model of current statistics
     * 
     * @return
     */
    public PeriodStatisticsModel getHighestPeriod() {
        return highestPeriod;
    }

    @Override
    public void finishUp() throws AWEException {
    }

    /**
     * @return Returns the maxTimestamp.
     */
    public Long getMaxTimestamp() {
        return maxTimestamp;
    }

    /**
     * @return Returns the minTimestamp.
     */
    public Long getMinTimestamp() {
        return minTimestamp;
    }
}
