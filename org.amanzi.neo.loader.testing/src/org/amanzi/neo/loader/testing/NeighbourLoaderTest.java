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

import org.amanzi.neo.loader.NeighbourLoader;
import org.amanzi.neo.loader.NetworkLoader;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeighbourLoaderTest extends AbstractLoaderTest{

    private static Node gis;

    /**
     * initialize test
     * 
     * @throws IOException
     */
    @BeforeClass
    public static void init() throws IOException {
    	clearDbDirectory();
    }    
    
    /**
     * Tests load empty data base.
     */
    @Test
    public void testEmptyLoading()throws IOException{
    	NeighbourLoader loader = initDataBase(BUNDLE_KEY_EMPTY);
    	assertLoader(loader);        
    }
    
    /**
     * Tests load correct data base.
     */
    @Test
    public void testCorrectLoading()throws IOException{
    	NeighbourLoader loader = initDataBase(BUNDLE_KEY_CORRECT);
    	assertLoader(loader);
    }
    
    /**
     * Tests load incorrect data bases.
     */
    @Ignore("Unknown reaction, need to be rewrited.")
    @Test
    public void testIncorrectLoading()throws IOException{
    	initDataBase(BUNDLE_KEY_WRONG);
    }
    
    /**
     * Execute after even test. 
     * Clear data base.
     */
    @After
    public void finishOne(){
    	doFinish();
    }  
    
    /**
     *finish 
     */
    @AfterClass
    public static void finish() {
        doFinish();
    }

    /**
     * Initialize loader.
     * @param aTestKey String (key for test)
     * @throws IOException (loading problem)
     */
	private NeighbourLoader initDataBase(String aTestKey) throws IOException {
		initProjectService();
		String fileDirectory = getFileDirectory();
		String filename = getProperty("test_loader.common.net_file");
		LuceneIndexService index = initIndex();
        NetworkLoader networkLoader = new NetworkLoader(getNeo(), fileDirectory + filename, index);
        networkLoader.setup();
        networkLoader.setLimit(1000);
        networkLoader.setCommitSize(1000);
        networkLoader.run(null);
        gis = findGisNode(filename);
        NeighbourLoader loader = new NeighbourLoader(gis, fileDirectory + getDbName(aTestKey), getNeo(),index, true);
        IProgressMonitor monitor = new NullProgressMonitor();
        loader.run(monitor);
		return loader;
	}

    /**
     * finds gis node by name
     * 
     * @param gisName name of gis node
     * @return gis node or null
     */
    public static Node findGisNode(final String gisName) {
        Transaction tx = getNeo().beginTx();
        try {
            if (gisName == null || gisName.isEmpty()) {
                return null;
            }
            Node root = getNeo().getReferenceNode();
            Iterator<Node> gisIterator = root.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return NodeTypes.GIS.getId().equals(node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "")) 
                    				&& gisName.equals(node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString());
                }
            }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            return gisIterator.hasNext() ? gisIterator.next() : null;
        } finally {
            tx.finish();
        }
    }


}
