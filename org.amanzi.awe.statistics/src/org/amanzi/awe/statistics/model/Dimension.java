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
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * storage for {@link StatisticsLevel}, may store some common information.
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class Dimension extends AbstractLevelElement {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(Dimension.class);

    private DimensionTypes dimensionType;

    /**
     * initialize new dimension Level
     * 
     * @param statisticsRoot
     * @param dimensionType
     * @throws DatabaseException
     */
    Dimension(Node statisticsRoot, DimensionTypes dimensionType) throws DatabaseException {
        super(StatisticsNodeTypes.DIMENSION);
        initStatisticsService();
        if (statisticsRoot == null) {
            LOGGER.error("parent can't be null");
            throw new IllegalArgumentException("statistics root cann't be null");
        }
        rootNode = statisticService.findDimension(statisticsRoot, dimensionType);
        this.dimensionType = dimensionType;
        name = dimensionType.getId();
        if (rootNode == null) {
            rootNode = statisticService.createDimension(statisticsRoot, dimensionType);
        }
    }

    /**
     * @param dimension
     */
    Dimension(Node dimension) {
        super(StatisticsNodeTypes.DIMENSION);
        initStatisticsService();
        if (dimension == null) {
            LOGGER.error("Cann't initialize dimension with existed node .Node is null is null.");
            throw new IllegalArgumentException("statistics root cann't be null");
        }
        rootNode = dimension;
        name = (String)statisticService.getNodeProperty(dimension, DatasetService.NAME);
        dimensionType = DimensionTypes.findById(name);
        if (dimensionType == null) {
            LOGGER.error("can't identify dimension type: " + dimensionType);
            throw new IllegalArgumentException("incorrect dimensionType");
        }

    }

    /**
     * return statistics Level . try to find it out. if not found- create new one
     * 
     * @param name
     * @return
     * @throws DatabaseException
     */
    public StatisticsLevel getLevel(String name) throws DatabaseException {
        Node level = statisticService.findStatisticsLevelNode(rootNode, name);
        if (level == null) {
            level = statisticService.createStatisticsLevelNode(rootNode, name);
        }
        return new StatisticsLevel(level);
    }

    /**
     * return all existed levels of this dimension
     * 
     * @return
     */
    public Iterable<StatisticsLevel> getAllLevels() {
        Iterable<Node> allLevelsNodes = statisticService.getFirstRelationsipsNodes(rootNode, DatasetRelationTypes.CHILD);
        List<StatisticsLevel> levelModels = new ArrayList<StatisticsLevel>();
        if (allLevelsNodes == null) {
            return levelModels;
        }
        for (Node level : allLevelsNodes) {
            levelModels.add(new StatisticsLevel(level));
        }
        return levelModels;
    }

    /**
     * @return Returns the dimensionType.
     */
    public DimensionTypes getDimensionType() {
        return dimensionType;
    }
}
