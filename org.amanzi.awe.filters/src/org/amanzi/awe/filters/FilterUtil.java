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

package org.amanzi.awe.filters;

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Utility class
 * </p>
 * 
 * @author Tsinkel_A
 * @since 1.0.0
 */
public class FilterUtil {
    /**
     * hide constructor
     */
    private FilterUtil() {
    }

    public static final String PROPERTY_FILTERED_NAME = "filter_property";
//    public static final String PROPERTY_FILTERED_VALID = "valid";
    public static final String PROPERTY_FIRST = "first";
    public static final String PROPERTY_SECOND = "second";
    public static final String PROPERTY_FIRST_TXT = "firstTXT";
    public static final String PROPERTY_SECOND_REL = "second_rel";
    public static final String PROPERTY_SECOND_TXT = "secondTXT";
    public static final String PROPERTY_ORDER = "order";
    public static final String PROPERTY_FILTER_COLOR = "filter_color";

    public static String getGroupProperty(Node node, String defValue, GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            return (String)node.getProperty(PROPERTY_FILTERED_NAME, defValue);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * @return
     */
    public static String[] getFilterDes() {
        return new String[] {"","<", "<=", "==", ">", ">=", "!="};
    }

    /**
     * @return
     */
    public static String[] getFilterRel() {
        return new String[] {"","||", "&&"};
    }

    /**
     * @param node
     * @param property
     * @param service
     */
    public static void setGroupProperty(Node node, String property, GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            node.setProperty(PROPERTY_FILTERED_NAME, property);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * @param dataNode
     * @param service
     * @return
     */
    public static AbstractFilter getFilterOfData(Node dataNode,  GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Relationship filterRelation = dataNode.getSingleRelationship(GeoNeoRelationshipTypes.USE_FILTER, Direction.OUTGOING);
            if (filterRelation==null){
                return null;
            }
            return AbstractFilter.getInstance(filterRelation.getOtherNode(dataNode), service);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * get data node traverser of filter
     * @param filterNode - filter node
     * @return
     */
    public static Traverser getDataNodesOfFilter(Node filterNode) {
        return filterNode.traverse(Order.DEPTH_FIRST, new StopEvaluator() {
            
            @Override
            public boolean isStopNode(TraversalPosition currentPos) {
                Relationship rel = currentPos.lastRelationshipTraversed();
                return rel!=null&&rel.isType(GeoNeoRelationshipTypes.USE_FILTER);
            }
        },new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Relationship rel = currentPos.lastRelationshipTraversed();
                return rel!=null&&rel.isType(GeoNeoRelationshipTypes.USE_FILTER);
            }
        },GeoNeoRelationshipTypes.CHILD,Direction.INCOMING,GeoNeoRelationshipTypes.NEXT,Direction.INCOMING,GeoNeoRelationshipTypes.USE_FILTER,Direction.INCOMING);
    }

}
