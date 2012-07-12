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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Statistics Model play role of container for {@link Dimension}. may store some common information
 * about statistics;
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsModel extends AbstractStatisticsModel {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsModel.class);

    private Long maxTimestamp;
    private Long minTimestamp;

    /**
     * create new statistics if not exist .else initialize existed.
     * 
     * @param parentNode
     * @throws IllegalArgumentException
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     */
    public StatisticsModel(Node parentNode, String templateName) throws IllegalArgumentException, DatabaseException {
        super(StatisticsNodeTypes.STATISTICS_MODEL);
        initStatisticsService();
        if (parentNode == null) {
            LOGGER.error("parentNode is null");
            throw new IllegalArgumentException("parentNode can't be null");
        }
        this.parentNode = parentNode;
        this.name = (String)templateName;
        rootNode = statisticService.findStatistic(parentNode, name);
        if (rootNode == null) {
            rootNode = statisticService.createStatisticsModelRoot(parentNode, name, false);
        }
        LOGGER.info("minTimestamp= " + minTimestamp + " maxTimestamp=" + maxTimestamp);
        minTimestamp = (Long)this.parentNode.getProperty(DriveModel.MIN_TIMESTAMP);
        maxTimestamp = (Long)this.parentNode.getProperty(DriveModel.MAX_TIMESTAMP);
        initDimension();
    }

    /**
     * initialize periodsList
     * 
     * @throws DatabaseException
     */
    private void initDimension() throws DatabaseException {

        if (minTimestamp == null || maxTimestamp == null) {
            LOGGER.info("missing required parametrs");
            return;
        }
        Iterable<Dimension> dimensions = getAllDimensions();
        if (dimensions == null) {
            initDefaultDimensions();
        }
    }

    /**
     * get list of all dimension
     * 
     * @return
     * @throws DatabaseException
     */
    public Iterable<Dimension> getAllDimensions() throws DatabaseException {
        Iterable<Node> dimensions = statisticService.getFirstRelationsipsNodes(rootNode, DatasetRelationTypes.CHILD);
        List<Dimension> dimensionsList = new ArrayList<Dimension>();
        if (dimensions == null) {
            return dimensionsList;
        }
        for (Node dimension : dimensions) {
            dimensionsList.add(new Dimension(dimension));
        }
        return dimensionsList;
    }

    /**
     * initialize default dimensions for all models
     * 
     * @throws DatabaseException
     */
    private void initDefaultDimensions() throws DatabaseException {
        Dimension timeModel;
        try {
            timeModel = new Dimension(rootNode, DimensionTypes.TIME);
            Period highestPeriod = Period.getHighestPeriod(minTimestamp, maxTimestamp);
            createTimeLevels(timeModel, highestPeriod);
            new Dimension(rootNode, DimensionTypes.NETWORK);
        } catch (IllegalNodeDataException e) {
            LOGGER.error("cann't intialize default Dimensions because of", e);
        }

    }

    /**
     * get existed or new(if not found) dimension
     * 
     * @param type
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Dimension getDimension(DimensionTypes type) throws DatabaseException, IllegalNodeDataException {
        return new Dimension(rootNode, type);
    }

    /**
     * initialize period chain
     * 
     * @param timeModel
     * @throws DatabaseException
     */
    private Period createTimeLevels(Dimension timeModel, Period period) throws DatabaseException {
        StatisticsLevel level;
        try {
            level = timeModel.getLevel(period.getId());
            Period underlinePeriod = period.getUnderlyingPeriod();
            if (period.getUnderlyingPeriod() != null) {
                createTimeLevels(timeModel, underlinePeriod);
                level.addSourceLevel(timeModel.getLevel(underlinePeriod.getId()));
            }
        } catch (IllegalNodeDataException e) {
            LOGGER.error("can't create time level because of", e);
        }
        return period;

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
