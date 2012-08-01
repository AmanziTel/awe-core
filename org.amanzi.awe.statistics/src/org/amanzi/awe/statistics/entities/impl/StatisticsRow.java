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
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * StatisticsRow entity. Can be instantiated only from {@link StatisticsGroup}. play role of storage
 * for {@link StatisticsCell}
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsRow extends AbstractStorageEntity<StatisticsCell> implements IAggregatedStatisticsEntity {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsRow.class);

    private static final String PROPERTY_SUMMARY_NAME = "summary";
    static final String SUMMARY_NAME = "total";
    // key - column/header name
    private StatisticsGroup group;

    /**
     * @param parent
     * @param current
     * @param type
     */
    public StatisticsRow(Node parent, Node current) {
        super(parent, current, StatisticsNodeTypes.S_ROW);
    }

    /**
     * return summury condition for this node
     * 
     * @return
     */
    public boolean isSummaryRow() {
        Boolean summury = (Boolean)statisticService.getNodeProperty(rootNode, PROPERTY_SUMMARY_NAME);
        if (summury == null) {
            return false;
        }
        return summury;
    }

    /**
     * set timestamp
     * 
     * @param timestamp
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     */
    // TODO: LN: 01.08.2012, private/protected?
    void setTimestamp(long timestamp) throws IllegalNodeDataException, DatabaseException {
        statisticService.setAnyProperty(rootNode, DriveModel.TIMESTAMP, timestamp);
    }

    /**
     * get row timestamp
     * 
     * @return
     */
    public Long getTimestamp() {
        return (Long)statisticService.getNodeProperty(rootNode, DriveModel.TIMESTAMP);
    }

    /**
     * @param isSummary
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public void setSummary(boolean isSummary) throws IllegalNodeDataException, DatabaseException {
        // Boolean summury = (Boolean)statisticService.getNodeProperty(rootNode,
        // PROPERTY_SUMMARY_NAME);
        if (isSummary == Boolean.TRUE) {
            statisticService.setAnyProperty(rootNode, PROPERTY_SUMMARY_NAME, isSummary);
        }
    }

    /**
     * add source row;
     * 
     * @param source
     * @throws DatabaseException
     */
    public void addSourceRow(StatisticsRow source) throws DatabaseException {
        if (source == null) {
            LOGGER.error("source node can't be null");
            throw new IllegalArgumentException("source is null");
        }
        statisticService.addSource(rootNode, source.getRootNode());
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

    @Override
    protected StatisticsCell instantiateChild(Node rootNode, Node rowNode) {
        return new StatisticsCell(rootNode, rowNode);
    }

    /**
     * add new cell in chain. if element with current name is already exists- throw
     * DuplicateNodeNameException
     * 
     * @param name
     * @return
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsCell addSCell(String name) throws DuplicateNodeNameException, DatabaseException, IllegalNodeDataException {
        return createChildWithName(name, StatisticsNodeTypes.S_CELL);
    }

}
