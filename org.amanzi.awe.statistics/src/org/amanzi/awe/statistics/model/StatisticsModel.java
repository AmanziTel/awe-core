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

import java.util.HashSet;
import java.util.Set;

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
 * Statistics Model
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
    private Set<PeriodStatisticsModel> periodList;

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

    public StatisticsModel(Node parentNode) throws IllegalArgumentException, DatabaseException, DuplicateNodeNameException {
        super(StatisticsNodeTypes.STATISTICS);
        initStatisticsService();
        if (parentNode == null) {
            LOGGER.error("parentNode is null");
            throw new IllegalArgumentException("parentNode can't be null");
        }
        this.parentNode = parentNode;
        this.name = (String)parentNode.getProperty(DatasetService.NAME, StringUtils.EMPTY) + SPACE_SEPARATOR + STATISTICS_POSTFIX;
        if (statisticService.findStatistic(parentNode, name) != null) {
            throw new DuplicateNodeNameException(name, StatisticsNodeTypes.STATISTICS);
        }
        rootNode = statisticService.createStatisticsModelRoot(parentNode, name);

        initPeriodsModel();
    }

    /**
     * initialize periodsList
     * 
     * @throws DatabaseException
     */
    private void initPeriodsModel() throws DatabaseException {
        Long minTimestamp = (Long)parentNode.getProperty(DriveModel.MIN_TIMESTAMP);
        Long maxTimestamp = (Long)parentNode.getProperty(DriveModel.MAX_TIMESTAMP);
        LOGGER.info("minTimestamp= " + minTimestamp + " maxTimestamp=" + maxTimestamp);
        if (minTimestamp == null || maxTimestamp == null) {
            LOGGER.info("missing required parametrs");
            return;
        }
        Period highestPeriod = Period.getHighestPeriod(minTimestamp, maxTimestamp);
        periodList = new HashSet<PeriodStatisticsModel>();
        initPeriods(highestPeriod);
    }

    /**
     * @throws DatabaseException
     */
    private Node initPeriods(Period period) throws DatabaseException {
        Node periodNode = statisticService.getPeriod(rootNode, period);
        periodList.add(new PeriodStatisticsModel(periodNode));
        Period underlinePeriod = period.getUnderlyingPeriod();
        if (period.getUnderlyingPeriod() != null) {
            Node underline = initPeriods(underlinePeriod);
            statisticService.addSource(periodNode, underline);
        }
        return periodNode;

    }

    @Override
    public void finishUp() throws AWEException {
    }
}
