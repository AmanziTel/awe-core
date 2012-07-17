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

package org.amanzi.awe.statistics.entities.impl;

import java.util.LinkedHashMap;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
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
public class Dimension extends AbstractEntity {

    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(Dimension.class);

    private DimensionTypes dimensionType;
    private LinkedHashMap<String, StatisticsLevel> levels;

    /**
     * initialize new dimension Level
     * 
     * @param statisticsRoot
     * @param dimensionType
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Dimension(Node statisticsRoot, DimensionTypes dimensionType) throws DatabaseException, IllegalNodeDataException {
        super(StatisticsNodeTypes.DIMENSION);
        initStatisticsService();
        if (statisticsRoot == null) {
            LOGGER.error("parent can't be null");
            throw new IllegalArgumentException("statistics root cann't be null");
        }
        rootNode = statisticService.findDimension(statisticsRoot, dimensionType);
        if (rootNode == null) {
            rootNode = statisticService.createDimension(statisticsRoot, dimensionType, false);
        }
        this.dimensionType = dimensionType;
        name = dimensionType.getId();
        parentNode = statisticsRoot;
    }

    /**
     * @param parent
     * @param current
     * @param type
     */
    public Dimension(Node parent, Node current) {
        super(parent, current, StatisticsNodeTypes.DIMENSION);
        dimensionType = DimensionTypes.findById(name);
        if (dimensionType == null) {
            LOGGER.error("can't identify dimension type: " + dimensionType);
            throw new IllegalArgumentException("incorrect dimensionType");
        }
    }

    /**
     * return statistics Level . try to find it out else return null
     * 
     * @param name
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsLevel getLevel(String name) throws DatabaseException, IllegalNodeDataException {
        loadChildIfNecessary();
        return levels.get(name);
    }

    /**
     * try to create new level in this statistics. if level is already exists throw
     * DuplicatedNodeNameException
     * 
     * @param timestamp
     * @return
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsLevel createStatisticsLevel(String name) throws DuplicateNodeNameException, DatabaseException,
            IllegalNodeDataException {
        loadChildIfNecessary();
        if (levels.containsKey(name)) {
            LOGGER.error("level with name." + name + "is already exists");
            throw new DuplicateNodeNameException();
        }
        Node statisticsLevel = statisticService.createStatisticsLevelNode(rootNode, name, Boolean.FALSE);
        StatisticsLevel newLevel = new StatisticsLevel(rootNode, statisticsLevel);
        levels.put(name, newLevel);
        return newLevel;
    }

    /**
     * return all existed levels of this dimension
     * 
     * @return
     * @throws DatabaseException
     */
    public Iterable<StatisticsLevel> getAllLevels() throws DatabaseException {
        loadChildIfNecessary();
        return levels.values();
    }

    /**
     * @return Returns the dimensionType.
     */
    public DimensionTypes getDimensionType() {
        return dimensionType;
    }

    protected void loadChildIfNecessary() throws DatabaseException {
        if (levels == null) {
            levels = new LinkedHashMap<String, StatisticsLevel>();
            Iterable<Node> levelNodes = statisticService.getFirstRelationTraverser(rootNode, DatasetRelationTypes.CHILD);
            if (levelNodes == null) {
                return;
            }
            for (Node rowNode : levelNodes) {
                String name = (String)statisticService.getNodeProperty(rowNode, StatisticsService.NAME);
                levels.put(name, new StatisticsLevel(rootNode, rowNode));
            }
        }
    }
}
