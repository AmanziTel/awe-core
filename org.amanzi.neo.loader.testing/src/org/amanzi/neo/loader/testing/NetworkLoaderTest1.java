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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Iterator;

import org.amanzi.neo.loader.NetworkLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * TODO extend from NetworkLoaderTest1
 * <p>
 * Test1 of NetworkLoader
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NetworkLoaderTest1 {
    protected static String filename = "Network_Sweden.csv";
    protected static String networkTestFiles = "files/network/";
    protected static NetworkLoader networkLoader;
    protected static EmbeddedNeo neo;

    /**
     * initialize test
     * 
     * @throws IOException
     */
    @BeforeClass
    public static void init() throws IOException {
        neo = new EmbeddedNeo(NeoTestPlugin.getDefault().getDatabaseLocation());
        networkLoader = new NetworkLoader(neo, networkTestFiles + filename);
        networkLoader.setup();
        networkLoader.setLimit(1000);
        networkLoader.setCommitSize(1000);
        networkLoader.run(null);
    }

    @Test
    public void testBadFields() {
        assertEquals(0, networkLoader.badFields.size());
    }

    @Test
    public void testShortLines() {
        assertEquals(0, networkLoader.shortLines.size());
    }

    @Test
    public void testSectorCount() {
        Transaction tx = neo.beginTx();
        try {
            RelationshipType nextRel = new RelationshipType() {

                @Override
                public String name() {
                    return "NEXT";
                }

            };
            RelationshipType childRel = new RelationshipType() {

                @Override
                public String name() {
                    return "CHILD";
                }

            };
            Iterator<Node> iterator = neo.getReferenceNode().traverse(Order.DEPTH_FIRST, new StopEvaluator() {

                @Override
                public boolean isStopNode(TraversalPosition currentPos) {
                    return currentPos.depth() > 2;
                }
            }, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return currentPos.currentNode().hasProperty("type")
                            && "network".equals(currentPos.currentNode().getProperty("type"))
                            && currentPos.currentNode().hasProperty("name")
                            && filename.equals(currentPos.currentNode().getProperty("name"));
                }
            }, childRel, Direction.OUTGOING, nextRel, Direction.OUTGOING).iterator();
            Node network = iterator.next();
            long sectorNumber = (Long)network.getProperty("sector_count");
            Traverser traverse = network.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return currentPos.currentNode().hasProperty("type")
                            && "sector".equals(currentPos.currentNode().getProperty("type"));
                }
            }, childRel, Direction.OUTGOING, nextRel, Direction.OUTGOING);
            int size = 0;
            for (Node node : traverse) {
                size++;
            }
            assertEquals(sectorNumber, size);
        } finally {
            tx.finish();
        }
    }

    @AfterClass
    public static void finish() {
        neo.shutdown();
    }

}
