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

package org.amanzi.neo.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * <p>
 * Utility class
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class Utils {
    private Utils() {
        // hide constructor
    }
    /**
     * Gets the number value.
     * 
     * @param <T> the generic type
     * @param klass the klass
     * @param value the value
     * @return the number value
     * @throws SecurityException the security exception
     * @throws NoSuchMethodException the no such method exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T getNumberValue(Class<T> klass, String value) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String methodName = klass == Integer.class ? "parseInt" : "parse" + klass.getSimpleName();

        Method metod = klass.getMethod(methodName, String.class);
        return (T)metod.invoke(null, value);
    }
    /**
     * Gets the relations set.
     * 
     * @param from the 'from' node
     * @param to the 'to' node
     * @param relationType the relation type
     * @return the relations
     */
    public static Set<Relationship> getRelations(Node from, Node to, RelationshipType relationType) {
        Set<Relationship> result = new HashSet<Relationship>();
        for (Relationship relation : from.getRelationships(relationType, Direction.OUTGOING)) {
            if (relation.getOtherNode(from).equals(to)) {
                result.add(relation);
            }
        }
        return result;
    }
    /**
     * Gets the neighbour relation.
     * 
     * @param server the server
     * @param neighbour the neighbour
     * @param neighbourName the neighbour name
     * @return the neighbour relation
     */
    public static Relationship getNeighbourRelation(Node server, Node neighbour, String neighbourName) {
            Set<Relationship> allRelations = getRelations(server, neighbour, NetworkRelationshipTypes.NEIGHBOUR);
            for (Relationship relation : allRelations) {
                if (relation.getProperty(INeoConstants.NEIGHBOUR_NAME, "").equals(neighbourName)) {
                    return relation;
                }
            }
            return null;
    }
}
