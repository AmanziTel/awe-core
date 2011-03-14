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

import org.amanzi.awe.statistics.database.StatisticsGroupIterator;
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
public class Statistics {
    private Node node;
    // key - network element name
    private Map<String, StatisticsGroup> groups;

    private StatisticsGroup lastGroup;
    private DatasetStatistics datasetStatistics;

    /**
     * @param networkLevel
     * @param timeLevel
     */
    public Statistics(Node node,DatasetStatistics datasetStatistics) {
        this.node = node;
        this.datasetStatistics=datasetStatistics;
    }

    public Node getNode() {
        return node;
    }

    public StatisticsGroup getGroupByKey(String key) {
        loadGroupsIfNecessary();
        return groups.get(key);
    }

    /**
     * @return Returns the groups.
     */
    public Map<String, StatisticsGroup> getGroups() {
        loadGroupsIfNecessary();
        return groups;
    }

    /**
     * Loads groups if necessary
     */
    private void loadGroupsIfNecessary() {
        if (groups == null) {
            loadGroups();
        }
    }

    /**
     * Loads groups from the database
     */
    private void loadGroups() {
        groups = new LinkedHashMap<String, StatisticsGroup>();
        for (StatisticsGroup group : new StatisticsGroupIterator(node)) {
            groups.put(group.getGroupName(), group);
            lastGroup = group;
        }
    }

    public void addGroup(StatisticsGroup group) {
        loadGroupsIfNecessary();
        if (lastGroup == null) {
            node.createRelationshipTo(group.getNode(), GeoNeoRelationshipTypes.CHILD);
        } else {
            lastGroup.getNode().createRelationshipTo(group.getNode(), GeoNeoRelationshipTypes.NEXT);
        }
        groups.put(group.getGroupName(), group);
        lastGroup = group;
    }

    public String getName() {
        return node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
    }
   public DatasetStatistics getStatisticsRoot(){
       return datasetStatistics;
   }
}
