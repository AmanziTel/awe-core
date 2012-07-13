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
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * StatisticsGroup entity. Can be instantiated only from {@link AggregatedStatistics}. play role of
 * storage for {@link StatisticsRow}
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsGroup extends AbstractEntity {
    private static final Logger LOGGER = Logger.getLogger(StatisticsGroup.class);
    private LinkedHashMap<Long, StatisticsRow> rows;

    /**
     * constructor for instantiation
     * 
     * @param parent
     * @param current
     * @param type
     */
    StatisticsGroup(Node parent, Node current) {
        super(parent, current, StatisticsNodeTypes.S_GROUP);
    }

    /**
     * try to find S_ROW node by timestamp ;
     * 
     * @param timestamp
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsRow getSRow(Long timestamp) {
        if (timestamp == null) {
            LOGGER.error("timestamp element is null.");
            throw new IllegalArgumentException("timestamp element is null");
        }
        loadChildIfNecessary();
        return rows.get(timestamp);
    }

    /**
     * try to create new row in this group. if row is already exists throw
     * DuplicatedNodeNameException
     * 
     * @param timestamp
     * @return
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsRow createStatisticsRow(Long timestamp) throws DuplicateNodeNameException, DatabaseException,
            IllegalNodeDataException {
        loadChildIfNecessary();
        if (rows.containsKey(timestamp)) {
            LOGGER.error("s_row with timestamp." + timestamp + "is already exists");
            throw new DuplicateNodeNameException();
        }
        Node statisticsRow = statisticService.createSRow(rootNode, timestamp, Boolean.FALSE);
        StatisticsRow newRow = new StatisticsRow(rootNode, statisticsRow);
        rows.put(timestamp, newRow);
        return newRow;
    }

    /**
     * return all Statistics Rows
     * 
     * @return
     */
    public Iterable<StatisticsRow> getAllSRows() {
        loadChildIfNecessary();
        return rows.values();
    }

    /**
     * set or remove flagged is true- set flaggedProperty to group else remove it from group node
     * 
     * @param flagged
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public void setFlagged(boolean flagged) throws IllegalNodeDataException, DatabaseException {
        if (flagged) {
            statisticService.setAnyProperty(rootNode, PROPERTY_FLAGGED_NAME, flagged);
        } else {
            statisticService.removeNodeProperty(rootNode, PROPERTY_FLAGGED_NAME);

        }
    }

    /**
     * return flagged value of group
     * 
     * @return
     */
    public boolean isFlagged() {
        Boolean isFlagged = (Boolean)statisticService.getNodeProperty(rootNode, PROPERTY_FLAGGED_NAME);
        if (isFlagged == null) {
            return Boolean.FALSE;
        }
        return isFlagged();
    }

    /**
     * Loads rows if necessary
     */
    @Override
    protected void loadChildIfNecessary() {
        if (rows == null) {
            rows = new LinkedHashMap<Long, StatisticsRow>();
            Iterable<Node> rowsNodes = statisticService.getChildrenChainTraverser(rootNode);
            if (rowsNodes == null) {
                return;
            }
            for (Node rowNode : rowsNodes) {
                Long timestamp = (Long)statisticService.getNodeProperty(rowNode, DriveModel.TIMESTAMP);
                rows.put(timestamp, new StatisticsRow(rootNode, rowNode));
            }
        }
    }

}
