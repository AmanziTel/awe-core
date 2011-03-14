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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Level {
    private Node node;
    private List<Statistics> stats;
    private DatasetStatistics datasetStatistics;

    public Level(Node node,DatasetStatistics datasetStatistics) {
        this.node = node;
        this.datasetStatistics=datasetStatistics;
    }

    public Node getNode() {
        return node;
    }

    public String getName() {
        return node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
    }

    public void setName(String name) {
        node.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
    }

    public void addStatistics(Statistics stat) {
        loadStatsIfNecessary();
        node.createRelationshipTo(stat.getNode(), GeoNeoRelationshipTypes.CHILD);
        stats.add(stat);
    }

    /**
     * Loads statistics if necessary
     */
    private void loadStatsIfNecessary() {
        if (stats == null) {
            loadStats();
        }
    }

    /**
     * Loads groups from the database
     */
    private void loadStats() {
        stats = new ArrayList<Statistics>();
        for (Statistics stat : new StatisticsIterator(node,datasetStatistics)) {
            stats.add(stat);
        }
    }

    public Statistics getStatistics(String level) {
        loadStatsIfNecessary();
        for (Statistics stat : stats) {
            if (stat.getName().contains(level)) {
                return stat;
            }
        }
        return null;
    }
}
