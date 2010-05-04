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

package org.amanzi.awe.statistic;

import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * TODO move to neo.core?
 * <p>
 * Contains information about property and relations of statistic structure
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class StatisticNeoService {
    public static final String STATISTIC_ROOT_ID = "statisticId";
    public static final String STATISTIC_PERIOD = "statperiodId";
    public static final String STATISTIC_TIME_END = "time_end";
    public static enum Relations implements RelationshipType {
        STATISTIC_ROOT;
    }


    /**
     * Find root node.
     *
     * @param parent the parent
     * @param structureId the structure id
     * @return the node
     */
    public static Node findRootNode(Node parent, final String structureId) {
        
        Relationship rel = parent.getSingleRelationship(getStatisticRootRelationType(structureId),Direction.OUTGOING);
        return rel!=null?rel.getOtherNode(parent):null;
//        Iterator<Node> iterator = parent.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//
//            @Override
//            public boolean isReturnableNode(TraversalPosition currentPos) {
//                return currentPos.notStartNode() && structureId.equals(currentPos.currentNode().getProperty(STATISTIC_ROOT_ID, ""));
//            }
//        }, Relations.STATISTIC_ROOT, Direction.OUTGOING).iterator();
//        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * Creates the root node.
     *
     * @param parent the parent
     * @param structureId the structure id
     * @param service the service
     * @return the node
     */
    public static Node createRootNode(Node parent, String structureId, GraphDatabaseService service) {
        Transaction tx = service.beginTx();
        try {
            Node result = service.createNode();
            parent.createRelationshipTo(result, Relations.STATISTIC_ROOT);
            // TODO add type
            result.setProperty(STATISTIC_ROOT_ID, structureId);
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
    }
    
    /**
     * Gets the statistic root relation type.
     *
     * @param prefix the prefix
     * @return the statistic root relation type
     */
    public static RelationshipType getStatisticRootRelationType(final String prefix){
        if (StringUtil.isEmpty(prefix)){
            return Relations.STATISTIC_ROOT;
        }else{
            final String name=new StringBuilder(prefix).append(Relations.STATISTIC_ROOT.name()).toString();
            return new RelationshipType() {
                @Override
                public String name() {
                    return name;
                }
            };
        }
    }

}
