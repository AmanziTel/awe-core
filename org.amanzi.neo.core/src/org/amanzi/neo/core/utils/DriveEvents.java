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

package org.amanzi.neo.core.utils;

import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.icons.IconManager;
import org.amanzi.neo.core.icons.IconManager.EventIcons;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * DriveEvents - enum of possible events
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum DriveEvents {
    /*
     * CONNECTION {
     * @Override public EventIcons getEventIcon() { return EventIcons.CONNECT; }
     * @Override public boolean haveEvents(String aProperty) { return aProperty != null &&
     * aProperty.toLowerCase().contains("connect"); } },
     */
    CALL_BLOCKED {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CALL_BLOCKED;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("blocked");
        }
    },
    CALL_DROPPED {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CALL_DROPPED;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("dropped");
        }
    },
    CALL_FAILURE {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CALL_FAILURE;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("no service");
        }
    },
    CALL_SUCCESS {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CALL_SUCCESS;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("good");
        }
    },
    HANDOVER_FAILURE {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.HANDOVER_FAILURE;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("handover failure");
        }
    },
    HANDOVER_SUCCESS {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.HANDOVER_SUCCESS;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            final String property = aProperty == null ? null : aProperty.toLowerCase();
            return property != null && (property.contains("handover complete)") || property.contains("ho command"));
        }
    };
    /**
     * Gets EventIcons of events
     * 
     * @return EventIcons
     */
    public abstract IconManager.EventIcons getEventIcon();

    /**
     * finish transaction;
     * 
     * @param tx - transaction
     */
    protected static void finishTx(Transaction tx) {
        if (tx != null) {
            tx.finish();
        }
    }

    /**
     * check on existing necessary events in mpNode
     * 
     * @param mpNode - point drive node
     * @param neo - NeoService - if null, then transaction do not create
     */
    public boolean haveEvents(Node mpNode, NeoService neo) {
        Transaction tx = neo == null ? null : neo.beginTx();
        try {
            Traverser traverser = eventTraverser(mpNode);
            return traverser.iterator().hasNext();
        } finally {
            finishTx(tx);
        }
    }

    /**
     * Checks that events by types
     * 
     * @param aProperty - event string
     * @return true if string contains event of necessary types
     */
    public abstract boolean haveEvents(String aProperty);

    /**
     * get event traverser, thats return ms node with necessary event
     * 
     * @param mpNode - point drive node
     * @return Traverser
     */
    protected Traverser eventTraverser(Node mpNode) {
        return mpNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                if (currentPos.isStartNode()) {
                    return false;
                }
                Node node = currentPos.currentNode();
                boolean result = node.hasProperty(INeoConstants.PROPERTY_TYPE_EVENT);
                if (!result) {
                    return false;
                }
                return haveEvents(node.getProperty(INeoConstants.PROPERTY_TYPE_EVENT).toString());
            }
        }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
    }

    /**
     * get list of all events
     * 
     * @param mpNode point drive node
     * @param neo - NeoService - if null, then transaction do not create
     * @return set of DriveEvents
     */
    public static Set<DriveEvents> getAllEvents(Node mpNode, NeoService neo) {
        Set<DriveEvents> result = new HashSet<DriveEvents>();
        Transaction tx = neo == null ? null : neo.beginTx();
        try {
            Traverser traverser = mpNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    if (currentPos.isStartNode()) {
                        return false;
                    }
                    Node node = currentPos.currentNode();
                    return node.hasProperty(INeoConstants.PROPERTY_TYPE_EVENT);
                }
            }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
            MSNODE: for (Node node : traverser) {
                for (DriveEvents event : DriveEvents.values()) {
                    if (result.contains(event)) {
                        continue;
                    }
                    if (event.haveEvents(node.getProperty(INeoConstants.PROPERTY_TYPE_EVENT).toString())) {
                        result.add(event);
                        continue MSNODE;
                    }
                }
            }
        } finally {
            finishTx(tx);
        }
        return result;
    }

    /**
     * gets the worst events of mpNode
     * 
     * @param mpNode point drive node
     * @param neo - NeoService - if null, then transaction do not create
     * @return DriveEvents or null
     */
    public static DriveEvents getWorstEvent(Node mpNode, NeoService neo) {
        Set<DriveEvents> events = getAllEvents(mpNode, neo);
        if (events.isEmpty()) {
            return null;
        }
        if (events.contains(HANDOVER_FAILURE)) {
            return HANDOVER_FAILURE;
        }
        if (events.contains(CALL_FAILURE)) {
            return CALL_FAILURE;
        }
        if (events.contains(CALL_DROPPED)) {
            return CALL_DROPPED;
        }
        if (events.contains(CALL_BLOCKED)) {
            return CALL_BLOCKED;
        }
        return events.iterator().next();
    }
}
