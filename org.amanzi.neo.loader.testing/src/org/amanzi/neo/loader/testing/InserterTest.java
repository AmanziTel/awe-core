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

package org.amanzi.neo.loader.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class InserterTest {
    private static final Logger LOGGER = Logger.getLogger(InserterTest.class);
    public static void main(String[] args) {
        LOGGER.debug("Started");
        if (true) return;
//        test5kWriteStandartService();
//
//        test5kWriteBatchInserter();

//        testTraversSpeed();


        
        
        EmbeddedReadOnlyGraphDatabase readOnlyService = null;

        Map<String, Object> properties = new HashMap<String, Object>();
        List<Long> ids = new ArrayList<Long>(1000);
        properties.put("pn1", "pv2");
        properties.put("pn2", "pv2");
        BatchInserterImpl inserter = null;
     
        try {
            inserter = new BatchInserterImpl("D:\\AWE_temp_DB\\DB4");
            long parentId = inserter.getReferenceNode();
//            long startTime = System.currentTimeMillis();
            for (int I = 0; I < 1000; I++) {
                ids.add(inserter.createNode(properties));
                inserter.createRelationship(parentId, ids.get(ids.size()-1), Rel.CHILD, null);
                parentId = ids.get(ids.size()-1);
            }
            
//            long endTime = System.currentTimeMillis();
//            LOGGER.debug("5k write BatchInserterImpl finished in " + (endTime - startTime));
            inserter.shutdown();
            
            
            
            readOnlyService = new EmbeddedReadOnlyGraphDatabase("D:\\AWE_temp_DB\\DB4");
            inserter = new BatchInserterImpl("D:\\AWE_temp_DB\\DB4");
            long id = inserter.createNode(properties);
            Transaction tx = readOnlyService.beginTx();
            Node rn = readOnlyService.getReferenceNode();
            Iterator<Node> iterator = rn.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return !currentPos.currentNode().hasRelationship(Rel.CHILD, Direction.OUTGOING);
                }
            }, Rel.CHILD, Direction.OUTGOING, Rel.CHILD, Direction.OUTGOING).iterator();
            
            while(iterator.hasNext()){
                LOGGER.debug("Last node id = " + iterator.next().getId());
            }
            
//            LOGGER.debug("Node id = " + readOnlyService.getNodeById(ids.get(0)).getId());
//            LOGGER.debug("Node id = " + readOnlyService.getNodeById(id).getId());
            tx.finish();
            
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
             if (inserter != null)
             inserter.shutdown();
        }
        
        LOGGER.debug("Finished");
    }

    /**
     *
     */
    private static void test1kReadBatchInserter() {
        EmbeddedReadOnlyGraphDatabase readOnlyService = new EmbeddedReadOnlyGraphDatabase("D:\\AWE_temp_DB\\DB3");

        Map<String, Object> properties = new HashMap<String, Object>();
        List<Long> ids = new ArrayList<Long>(1000);
        properties.put("pn1", "pv2");
        properties.put("pn2", "pv2");
        BatchInserterImpl inserter = null;

        try {
            inserter = new BatchInserterImpl("D:\\AWE_temp_DB\\DB3");
            long startTime = System.currentTimeMillis();
            for (int I = 0; I < 1000; I++) {
                ids.add(inserter.createNode(properties));
            }
            long endTime = System.currentTimeMillis();
            LOGGER.debug("5k write BatchInserterImpl finished in " + (endTime - startTime));
        } finally {
            // if (inserter != null)
            // inserter.shutdown();
        }

        readOnlyService = new EmbeddedReadOnlyGraphDatabase("D:\\AWE_temp_DB\\DB3");

        long startTime = System.currentTimeMillis();
        try {
            for (Long id : ids) {
                Map<String, Object> ps = inserter.getNodeProperties(id);
            }
        } finally {
            if (inserter != null) {
                inserter.shutdown();
            }
        }
        try {
            for (Long id : ids) {
                Node node = readOnlyService.getNodeById(id);
            }
        } finally {
            if (readOnlyService != null) {
                readOnlyService.shutdown();
            }
        }
        long endTime = System.currentTimeMillis();
        LOGGER.debug("1k read BatchInserterImpl finished in " + (endTime - startTime));

    }

    private static enum Rel implements RelationshipType {
        CHILD;
    }

    /**
     *
     */
    private static void testTraversSpeed() {
        EmbeddedReadOnlyGraphDatabase readOnlyService = null;
        GraphDatabaseService service = null;
        List<Long> ids = new ArrayList<Long>(1000);
        try {

            service = new EmbeddedGraphDatabase("D:\\AWE_temp_DB\\DB3");

            Transaction tx = service.beginTx();
            Node parent = service.getReferenceNode();
            try {
                for (int I = 0; I < 10; I++) {
                    Node node = service.createNode();
                    node.setProperty("pn1", "pv1");
                    node.setProperty("pn2", "pv2");
                    ids.add(node.getId());
                    parent.createRelationshipTo(node, Rel.CHILD);
                    parent = node;
                }
                tx.success();
            } finally {
                tx.finish();
            }
            long startTime = System.nanoTime();
            for(int k = 0;k<5;k++){
            
            service.getReferenceNode().traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return !currentPos.currentNode().hasRelationship(Rel.CHILD, Direction.OUTGOING);
                }
            }, Rel.CHILD, Direction.OUTGOING, Rel.CHILD, Direction.OUTGOING);
            
            }
            long endTime = System.nanoTime();
            LOGGER.debug("GraphDatabaseService service full travers finished in " + (endTime - startTime));
            service.shutdown();

            readOnlyService = new EmbeddedReadOnlyGraphDatabase("D:\\AWE_temp_DB\\DB3");
            tx = readOnlyService.beginTx();
            startTime = System.nanoTime();
            for(int k = 0;k<5;k++){
                
            readOnlyService.getReferenceNode().traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return !currentPos.currentNode().hasRelationship(Rel.CHILD, Direction.OUTGOING);
                }
            }, Rel.CHILD, Direction.OUTGOING, Rel.CHILD, Direction.OUTGOING);
            
            }
            endTime = System.nanoTime();
            tx.finish();
            LOGGER.debug("EmbeddedReadOnlyGraphDatabase service full travers finished in " + (endTime - startTime));
            readOnlyService.shutdown();

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (service != null) {
                service.shutdown();
            }
            if (readOnlyService != null) {
//                readOnlyService.shutdown();
            }
        }
    }

    private static void test5kWriteStandartService() {
        for (int c = 0; c < 2; c++) {
            GraphDatabaseService service = null;
            try {
                service = new EmbeddedGraphDatabase("D:\\AWE_temp_DB\\DB1");
                long startTime = System.currentTimeMillis();
                for (int i = 0; i < 1; i++) {
                    Transaction tx = service.beginTx();
                    try {
                        for (int I = 0; I < 10; I++) {
                            Node node = service.createNode();
                            node.setProperty("pn1", "pv1");
                            node.setProperty("pn2", "pv2");
                        }
                        tx.success();
                    } finally {
                        tx.finish();
                    }
                }
                long endTime = System.currentTimeMillis();
                LOGGER.debug("5k write EmbeddedGraphDatabase service finished in " + (endTime - startTime));
            } finally {
                if (service != null)
                    service.shutdown();
            }
        }
    }

    private static void test5kWriteBatchInserter() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("pn1", "pv2");
        properties.put("pn2", "pv2");
        BatchInserterImpl inserter = null;
        for (int c = 0; c < 2; c++) {
            try {
                inserter = new BatchInserterImpl("D:\\AWE_temp_DB\\DB2");
                long startTime = System.currentTimeMillis();
                for (int I = 0; I < 10; I++) {
                    inserter.createNode(properties);
                }
                long endTime = System.currentTimeMillis();
                LOGGER.debug("5k write BatchInserterImpl finished in " + (endTime - startTime));

            } finally {
                if (inserter != null)
                    inserter.shutdown();
            }
        }
    }

}
