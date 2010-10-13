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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.amanzi.awe.statistics.database.StatisticsLevelIterator;
import org.amanzi.awe.statistics.database.StatisticsRowIterator;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Dimension {
    private Node node;
    private Map<String, Level> levels;
    private Level lastLevel;

    public Dimension(Node node) {
        this.node = node;
    }

    Node getNode() {
        return node;
    }

    public void addLevel(Level level) {
        loadLevelsIfNecessary();
        levels.put(level.getName(), level);
        node.createRelationshipTo(level.getNode(), GeoNeoRelationshipTypes.CHILD);
    }

    public void setName(String name) {
        node.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
    }

    public String getName() {
        return node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
    }

    public Level getLevelByKey(String key) {
        loadLevelsIfNecessary();
        return levels.get(key);
    }

    private void loadLevelsIfNecessary() {
        if (levels == null) {
            loadLevels();
        }

    }

    private void loadLevels() {
        levels = new LinkedHashMap<String, Level>();
        for (Level level : new StatisticsLevelIterator(node)) {
            levels.put(level.getName(), level);
            lastLevel = level;
        }
    }
}
