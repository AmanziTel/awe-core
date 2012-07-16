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

import org.amanzi.awe.statistics.entities.IAggregatedStatisticsEntity;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
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
public class StatisticsGroup extends AbstractStorageEntity<StatisticsRow> implements IAggregatedStatisticsEntity {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsGroup.class);

    /**
     * constructor for instantiation
     * 
     * @param parent
     * @param current
     * @param type
     */
    public StatisticsGroup(Node parent, Node current) {
        super(parent, current, StatisticsNodeTypes.S_GROUP);
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
    public StatisticsRow addRow(Long timestamp, String name) throws DuplicateNodeNameException, DatabaseException,
            IllegalNodeDataException {
        loadChildIfNecessary();
        if (childs.containsKey(name)) {
            LOGGER.error("s_row with timestamp." + timestamp + "is already exists");
            throw new DuplicateNodeNameException();
        }
        StatisticsRow newRow = createChildWithName(name, StatisticsNodeTypes.S_ROW);
        newRow.setTimestamp(timestamp);
        childs.put(name, newRow);
        return newRow;
    }

    /**
     * create summury row
     * 
     * @return
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     */
    public StatisticsRow addSummuryRow() throws DuplicateNodeNameException, DatabaseException, IllegalNodeDataException {
        StatisticsRow row = createChildWithName(StatisticsRow.SUMMARY_NAME, StatisticsNodeTypes.S_ROW);
        row.setSummary(Boolean.TRUE);
        return row;
    }

    @Override
    protected StatisticsRow instantiateChild(Node rootNode, Node rowNode) {
        return new StatisticsRow(rootNode, rowNode);
    }

}
