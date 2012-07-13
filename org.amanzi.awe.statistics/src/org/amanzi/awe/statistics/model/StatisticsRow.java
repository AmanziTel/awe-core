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

import java.util.LinkedHashMap;

import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * StatisticsRow entity. Can be instantiated only from {@link StatisticGroup}. play role of storage
 * for {@link StatisticsCell}
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsRow extends AbstractEntity {
    private static final Logger LOGGER = Logger.getLogger(StatisticsRow.class);
    private static final String PROPERTY_SUMMARY_NAME = "summary";
    static final String SUMMARY_NAME = "total";
    // key - column/header name
    private LinkedHashMap<String, StatisticsCell> cells;
    private StatisticsGroup group;

    /**
     * @param parent
     * @param current
     * @param type
     */
    StatisticsRow(Node parent, Node current) {
        super(parent, current, StatisticsNodeTypes.S_ROW);
    }

    /**
     * try to find S_CELL node by name if not exist return null;
     * 
     * @param timestamp
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsCell getSCell(String name) throws DatabaseException, IllegalNodeDataException, IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            IllegalArgumentException e = new IllegalArgumentException("provided S_CELL name is Incorrect");
            LOGGER.error("name of S_CELL node must have a name. currently name is " + name);
            throw e;
        }
        loadChildIfNecessary();
        return cells.get(name);
    }

    /**
     * try to create new group in this statistics. if group is already exists throw
     * DuplicatedNodeNameException
     * 
     * @param timestamp
     * @return
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsCell createStatisticsCell(String name) throws DuplicateNodeNameException, DatabaseException,
            IllegalNodeDataException {
        loadChildIfNecessary();
        if (cells.containsKey(name)) {
            LOGGER.error("s_cell with name." + name + "is already exists");
            throw new DuplicateNodeNameException();
        }
        Node sCell = statisticService.createSCell(rootNode, name, false);
        StatisticsCell newCell = new StatisticsCell(rootNode, sCell);
        cells.put(name, newCell);
        return newCell;
    }

    @Override
    protected void loadChildIfNecessary() {
        if (cells == null) {
            LOGGER.debug("Start loading srows");
            cells = new LinkedHashMap<String, StatisticsCell>();
            Iterable<Node> rowsNodes = statisticService.getChildrenChainTraverser(rootNode);
            if (rowsNodes == null) {
                return;
            }
            for (Node cellNode : rowsNodes) {
                String name = (String)statisticService.getNodeProperty(cellNode, StatisticsService.NAME);
                cells.put(name, new StatisticsCell(rootNode, cellNode));
            }
        }
    }

    /**
     * return summury condition for this node
     * 
     * @return
     */
    public boolean isSummaryNode() {
        Boolean summury = (Boolean)statisticService.getNodeProperty(rootNode, SUMMARY_NAME);
        if (summury == null) {
            return false;
        }
        return summury;
    }

    /**
     * @param isSummary
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public void setSummary(boolean isSummary) throws IllegalNodeDataException, DatabaseException {
        Boolean summury = (Boolean)statisticService.getNodeProperty(rootNode, PROPERTY_SUMMARY_NAME);
        if (summury != null && isSummary == Boolean.TRUE) {
            statisticService.setAnyProperty(rootNode, PROPERTY_SUMMARY_NAME, isSummary);
        }
    }

    /**
     * get group its row belongs to;
     * 
     * @return
     */
    public StatisticsGroup getParent() {
        if (group == null) {
            group = new StatisticsGroup(statisticService.getParentLevelNode(parentNode), parentNode);
        }
        return group;
    }
}
