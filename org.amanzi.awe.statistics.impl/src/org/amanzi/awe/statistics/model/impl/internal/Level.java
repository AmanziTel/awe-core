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

package org.amanzi.awe.statistics.model.impl.internal;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.statistics.service.impl.StatisticsService.StatisticsRelationshipType;
import org.amanzi.neo.impl.dto.DataElement;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class Level extends DataElement {

    public enum LevelType {
        TIME(StatisticsRelationshipType.TIME_DIMENSION), PROPERTY(StatisticsRelationshipType.PROPERTY_DIMENSION);

        private RelationshipType relationshipType;

        private LevelType(RelationshipType relationshipType) {
            this.relationshipType = relationshipType;
        }

        public RelationshipType getRelationshipType() {
            return relationshipType;
        }
    }

    private LevelType dimensionType;

    private final Map<Object, Group> groups = new HashMap<Object, Group>();

    /**
     * 
     */
    public Level(Node node) {
        super(node);
    }

    public void setDimensionType(LevelType dimensionType) {
        this.dimensionType = dimensionType;
    }

    public LevelType getDimensionType() {
        return dimensionType;
    }

    public Group getGroup(Object key) {
        return groups.get(key);
    }

    public void addGroup(Object key, Group group) {
        groups.put(key, group);
    }

}
