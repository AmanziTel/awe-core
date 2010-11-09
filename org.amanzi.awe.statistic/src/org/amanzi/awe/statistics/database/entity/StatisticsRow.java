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

package org.amanzi.awe.statistics.database.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import org.amanzi.awe.statistics.database.StatisticsCellIterator;
import org.amanzi.awe.statistics.database.StatisticsRowIterator;
import org.amanzi.awe.statistics.engine.IStatisticsHeader;
import org.amanzi.awe.statistics.template.TemplateColumn;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsRow {
    public static final String TOTAL="total";
    private Node node;
    private StatisticsCell lastCell;
    // key - column/header name
    private LinkedHashMap<String, StatisticsCell> cells;

    public StatisticsRow(Node node) {
        this.node = node;
    }

    /**
     * @return Returns the periodName.
     */
    public String getName() {
        return node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
    }

    /**
     * Sets the periodName.
     */
    public void setName(String periodName) {
        node.setProperty(INeoConstants.PROPERTY_NAME_NAME, periodName);
    }

    /**
     * @return Returns the period.
     */
    public Long getPeriod() {
        return (Long)node.getProperty(INeoConstants.PROPERTY_TIME_NAME);
    }

    /**
     * Sets the period.
     */
    public void setPeriod(Long period) {
        node.setProperty(INeoConstants.PROPERTY_TIME_NAME, period);
    }

    /**
     * @return Returns the node.
     */
    Node getNode() {
        return node;
    }

    /**
     * @return Returns the cells.
     */
    public Map<String, StatisticsCell> getCells() {
        loadCellsIfNecessary();
        return cells;
    }

    /**
     * Loads cells from the database
     */
    private void loadCells() {
        cells = new LinkedHashMap<String, StatisticsCell>();
        for (StatisticsCell cell : new StatisticsCellIterator(node)) {
            cells.put(cell.getName(), cell);
            lastCell = cell;
        }
    }

    public StatisticsCell getCellByKey(String key) {
        loadCellsIfNecessary();
        return cells.get(key);
    }

    public void addCell(StatisticsCell cell) {
        loadCellsIfNecessary();
        if (lastCell == null) {
            node.createRelationshipTo(cell.getNode(), GeoNeoRelationshipTypes.CHILD);
        } else {
            lastCell.getNode().createRelationshipTo(cell.getNode(), GeoNeoRelationshipTypes.NEXT);

        }
        cells.put(cell.getName(), cell);
        lastCell = cell;
    }

    /**
     * Loads cells if necessary
     */
    private void loadCellsIfNecessary() {
        if (cells == null) {
            loadCells();
        }
    }

    public boolean isSummaryNode() {
        return node.hasProperty(INeoConstants.PROPERTY_SUMMARY_NAME)
                && (Boolean)node.getProperty(INeoConstants.PROPERTY_SUMMARY_NAME);
    }

    public void setSummaryNode(boolean isSummary) {
        node.setProperty(INeoConstants.PROPERTY_SUMMARY_NAME, isSummary);
    }
    public void addSourceRow(StatisticsRow sourceRow) {
        node.createRelationshipTo(sourceRow.getNode(), GeoNeoRelationshipTypes.SOURCE);
    }

}
