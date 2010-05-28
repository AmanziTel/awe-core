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
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.icons.IconManager;
import org.amanzi.neo.core.icons.IconManager.EventIcons;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * DriveEvents - enum of possible events
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum DriveEvents {
    // unknown bad event
    UNKNOWN_BAD {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CONNECT_BAD;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return false;
        }

        @Override
        public String getDescription() {
            return "unknown bad event";
        }
    },
    // unknown good event
    UNKNOWN_GOOD {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CONNECT_GOOD;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return false;
        }

        @Override
        public String getDescription() {
            return "unknown good event";
        }
    },
    // unknown neutral event
    UNKNOWN_NEUTRAL {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CONNECT;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return false;
        }

        @Override
        public String getDescription() {
            return "unknown neutral event";
        }
    },
    // call blocked
    CALL_BLOCKED {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CALL_BLOCKED;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("blocked");
        }

        @Override
        public String getDescription() {
            return "call blocked";
        }
    },
    // call dropped
    CALL_DROPPED {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CALL_DROPPED;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("dropped");
        }

        @Override
        public String getDescription() {
            return "call dropped";
        }
    },
    // call failure
    CALL_FAILURE {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CALL_FAILURE;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("no service");
        }

        @Override
        public String getDescription() {
            return "call failure";
        }
    },
    // call success
    CALL_SUCCESS {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.CALL_SUCCESS;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("good");
        }

        @Override
        public String getDescription() {
            return "call success";
        }
    },
    // HANDOVER_FAILURE
    HANDOVER_FAILURE {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.HANDOVER_FAILURE;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("handover failure");
        }

        @Override
        public String getDescription() {
            return "handower failure";
        }
    },
    // HANDOVER_SUCCESS
    HANDOVER_SUCCESS {
        @Override
        public EventIcons getEventIcon() {
            return EventIcons.HANDOVER_SUCCESS;
        }

        @Override
        public boolean haveEvents(String aProperty) {
            final String property = aProperty == null ? null : aProperty.toLowerCase();
            return property != null && (property.contains("handover complete") || property.contains("ho command"));
        }

        @Override
        public String getDescription() {
            return "handower success";
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
    public boolean haveEvents(Node mpNode, GraphDatabaseService neo) {
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
    public static Set<DriveEvents> getAllEvents(Node mpNode, GraphDatabaseService neo) {
        Set<DriveEvents> result = new HashSet<DriveEvents>();
        Set<DriveEvents> unknownEwents = new HashSet<DriveEvents>();
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
            }, GeoNeoRelationshipTypes.LOCATION, Direction.INCOMING);
            boolean haveEvents = false;
            MSNODE: for (Node node : traverser) {
                haveEvents=true;
                if (node.hasProperty(INeoConstants.PROPERTY_DRIVE_TYPE_EVENT)) {
                    DriveEvents event = DriveEvents.getEvents(node, neo);
                    assert event != null;
                    result.add(event);
                    continue;
                }
                String eventTxt = node.getProperty(INeoConstants.PROPERTY_TYPE_EVENT).toString();
                for (DriveEvents event : DriveEvents.values()) {
                    if (result.contains(event)) {
                        continue;
                    }

                    if (event.haveEvents(eventTxt)) {
                        result.add(event);
                        continue MSNODE;
                    }
                }
                if (result.isEmpty()) {
                    DriveEvents unknownEvent;
                    if (eventTxt.contains("disconnect abnormal") || eventTxt.contains("disconnect drop")
                            || eventTxt.contains("block") || eventTxt.contains("failure")) {
                        unknownEvent = UNKNOWN_BAD;
                    } else if (eventTxt.contains("disconnect normal") || eventTxt.contains("completed")
                            || eventTxt.contains("success") || eventTxt.contains("attempt success")
                            || eventTxt.contains("connected") || eventTxt.contains("received") || eventTxt.contains("good")
                            || eventTxt.contains("complete")) {
                        unknownEvent = UNKNOWN_GOOD;
                    } else {
                        unknownEvent = UNKNOWN_NEUTRAL;
                    }
                    if (!unknownEwents.contains(unknownEvent)) {
                        unknownEwents.add(unknownEvent);
                    }
                }
            }
            if (result.isEmpty()&&haveEvents){
                result.addAll(unknownEwents);
            }
        } finally {
            finishTx(tx);
        }
        return result;
    }

    public static DriveEvents getEvents(Node node, GraphDatabaseService neo) {
        Transaction tx = NeoUtils.beginTx(neo);
        try {
            String eventType = (String)node.getProperty(INeoConstants.PROPERTY_DRIVE_TYPE_EVENT, null);

            return DriveEvents.getEnumById(eventType);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    public static DriveEvents getEnumById(String eventType) {
        if (StringUtils.isEmpty(eventType)) {
            return null;
        }
        return DriveEvents.valueOf(eventType);
    }

    /**
     * gets the worst events of mpNode
     * 
     * @param mpNode point drive node
     * @param neo - NeoService - if null, then transaction do not create
     * @return DriveEvents or null
     */
    public static DriveEvents getWorstEvent(Node mpNode, GraphDatabaseService neo) {
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
        if (events.contains(UNKNOWN_BAD)) {
            return UNKNOWN_BAD;
        }
        return events.iterator().next();
    }

    /**
     * Gets event description
     * 
     * @return
     */
    public abstract String getDescription();

    public void setEventType(Node node, GraphDatabaseService neo) {
        Transaction tx = neo.beginTx();
        try {
            node.setProperty(INeoConstants.PROPERTY_DRIVE_TYPE_EVENT, name());
            tx.success();
        } finally {
            tx.finish();
        }
    }
}
