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

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.statistics.database.StatisticsRowIterator;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsGroup {
    public static final String UNKNOWN = "unknown";
    private Node node;
    private StatisticsRow lastRow;
    // key - period name
    private LinkedHashMap<String, StatisticsRow> rows;

    /**
     * @param node
     */
    public StatisticsGroup(Node node) {
        this.node = node;
    }

    /**
     * @return Returns the rows.
     */
    public Map<String, StatisticsRow> getRows() {
        loadRowsIfNecessary();
        return rows;
    }

    /**
     * Loads rows if necessary
     */
    private void loadRowsIfNecessary() {
        if (rows == null) {
            loadRows();
        }
    }

    /**
     * Loads rows from database
     */
    private void loadRows() {
        rows = new LinkedHashMap<String, StatisticsRow>();
        for (StatisticsRow row : new StatisticsRowIterator(node)) {
            rows.put(row.getName(), row);
            lastRow = row;
        }
    }

    public StatisticsRow getRowByKey(String key) {
        loadRowsIfNecessary();
        return rows.get(key);
    }

    public void addRow(StatisticsRow row) {
        loadRowsIfNecessary();
        if (lastRow == null) {
            node.createRelationshipTo(row.getNode(), GeoNeoRelationshipTypes.CHILD);
        } else {
            lastRow.getNode().createRelationshipTo(row.getNode(), GeoNeoRelationshipTypes.NEXT);
        }
        rows.put(row.getName(), row);
        lastRow = row;
    }

    /**
     * @return Returns the node.
     */
    Node getNode() {
        return node;
    }

    public String getGroupName() {
        if (hasKeyNode()) {
            return getKeyNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        } else if (node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)) {
            return node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        }
        return UNKNOWN;
    }

    public boolean hasKeyNode() {
        return node.hasRelationship(GeoNeoRelationshipTypes.KEY, Direction.OUTGOING);
    }

    public Node getKeyNode() {
        Relationship rel = node.getSingleRelationship(GeoNeoRelationshipTypes.KEY, Direction.OUTGOING);
        if (rel != null) {
            return rel.getOtherNode(node);
        }
        return null;
    }

    public void setGroupName(String name) {
        node.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
    }

    public void setKeyNode(Node keyNode) {
        node.createRelationshipTo(keyNode, GeoNeoRelationshipTypes.KEY);
    }
    public void setFlagged(boolean flagged) {
        if (flagged) {
            node.setProperty(INeoConstants.PROPERTY_FLAGGED_NAME, true);
        } else {
            node.removeProperty(INeoConstants.PROPERTY_FLAGGED_NAME);

        }
    }

    public boolean isFlagged() {
        return (Boolean)node.getProperty(INeoConstants.PROPERTY_FLAGGED_NAME, false);
    }
}