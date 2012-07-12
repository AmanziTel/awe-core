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

import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
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

    /**
     * @param parent
     * @param current
     * @param type
     */
    StatisticsRow(Node parent, Node current) {
        super(parent, current, StatisticsNodeTypes.S_ROW);
    }

    /**
     * constructor for instantiation
     * 
     * @param existed
     * @throws DatabaseException
     */
    StatisticsRow(Node existed) throws DatabaseException {
        super(existed, StatisticsNodeTypes.S_ROW);
    }

    /**
     * try to find S_CELL node by name if not exist- create new one;
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
        Node scellNode = statisticService.findNodeInChain(rootNode, DatasetService.NAME, name);
        if (scellNode == null) {
            scellNode = statisticService.createSCell(rootNode, name, false);
        }
        return new StatisticsCell(rootNode, scellNode);
    }
}
