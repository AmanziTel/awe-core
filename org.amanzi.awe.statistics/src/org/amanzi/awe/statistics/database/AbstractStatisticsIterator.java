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

package org.amanzi.awe.statistics.database;

import java.util.Iterator;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @param <T>
 * @since 1.0.0
 */
public abstract class AbstractStatisticsIterator<T> implements Iterator<T>, Iterable<T>{
    protected Iterator<Node> nodeIterator;

    public AbstractStatisticsIterator(Node node, final NodeTypes type) {
        this.nodeIterator = node.traverse(Order.DEPTH_FIRST, new StopEvaluator() {

            @Override
            public boolean isStopNode(TraversalPosition currentPos) {
                if (currentPos.isStartNode()) {
                    return false;
                }
                if (!type.getId().equalsIgnoreCase(
                        currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString())) {
                    return true;
                }
                return false;

            }
        }, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                if (type.getId()
                        .equalsIgnoreCase(currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString())) {
                    return true;
                }
                return false;
            }
        }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
    }
    @Override
    public Iterator<T> iterator() {
        return this;
    }

    public boolean hasNext() {
        return nodeIterator.hasNext();
    }


    public void remove() {
        nodeIterator.remove();
    }
}
