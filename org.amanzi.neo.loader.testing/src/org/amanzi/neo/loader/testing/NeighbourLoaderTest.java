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

import java.io.IOException;
import java.util.Iterator;

import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.loader.NeighbourLoader;
import org.amanzi.neo.loader.NetworkLoader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeighbourLoaderTest {

    protected static String filename = "Net1.csv";
    protected static String filenameNeighbour = "NetNbr.csv";
    protected static String networkTestFiles = "files/neighbour/";
    protected static EmbeddedNeo neo;
    private static Node gis;

    /**
     * initialize test
     * 
     * @throws IOException
     */
    @BeforeClass
    public static void init() throws IOException {
        neo = new EmbeddedNeo(NeoTestPlugin.getDefault().getDatabaseLocation());
        NetworkLoader networkLoader = new NetworkLoader(neo, networkTestFiles + filename);
        networkLoader.setup();
        networkLoader.setLimit(1000);
        networkLoader.setCommitSize(1000);
        networkLoader.run(null);
        gis = findGisNode(filename);
        NeighbourLoader loader = new NeighbourLoader(gis, filenameNeighbour, neo);
        IProgressMonitor monitor = new NullProgressMonitor();
        loader.run(monitor);
    }


    /**
     * finds gis node by name
     * 
     * @param gisName name of gis node
     * @return gis node or null
     */
    public static Node findGisNode(final String gisName) {
        Transaction tx = neo.beginTx();
        try {
            if (gisName == null || gisName.isEmpty()) {
                return null;
            }
            Node root = NeoServiceProvider.getProvider().getService().getReferenceNode();
            Iterator<Node> gisIterator = root.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return "gis".equals(node.getProperty("type", "")) && gisName.equals(node.getProperty("name", "").toString());
                }
            }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            return gisIterator.hasNext() ? gisIterator.next() : null;
        } finally {
            tx.finish();
        }
    }


}
