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

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Test of NetworkLoader
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NetworkLoaderTest1 extends AbstractLoaderTest{
	
//	protected NetworkLoader networkLoader;    
    protected String filename;
    private long loadTime;
    
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
     * @throws IOException (loading problem)
     */
    @Test
    public void testEmptyLoading()throws IOException{
    	initDataBase(BUNDLE_KEY_EMPTY);
    	doAssert(BUNDLE_KEY_EMPTY);
    }
    
    /**
     * Tests load correct data base.
     * @throws IOException (loading problem)
     */
    @Test
    public void testCorrectLoading()throws IOException{
    	initDataBase(BUNDLE_KEY_CORRECT);
		doAssert(BUNDLE_KEY_CORRECT);
    }
    
    /**
     * Tests time of load.
     * @throws IOException (loading problem)
     */
    @Test
    public void testTimeLoad()throws IOException{
    	loadTime = System.currentTimeMillis();
		initDataBase(BUNDLE_KEY_TIME);
		loadTime = System.currentTimeMillis() - loadTime;
		assertLoadTime(loadTime, BUNDLE_KEY_TIME);
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
     * Do after all tests.
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
    private void initDataBase(String aTestKey) throws IOException {
//		initProjectService();
//		networkLoader = new NetworkLoader(getNeo(), getFileDirectory() + getDbName(aTestKey), initIndex());
//        networkLoader.setup();
//        networkLoader.setLimit(1000);
//        networkLoader.setCommitSize(1000);
//        networkLoader.run(null);
	}
    
    /**
     * Execute assertion.
     * @param aTestKey String (key for test)
     */
    private void doAssert(String aTestKey) {
		assertBadFields(aTestKey);
		assertShortLines(aTestKey);
		assertSectorCount(aTestKey);
//    	assertLoader(networkLoader);
	}

    /**
     * Execute assertion (bad files).
     * @param aTestKey String (key for test)
     */
    private void assertBadFields(String aTestKey) {
        int etalon = parceStringToInt(getProperty("test_loader.bad_fields."+aTestKey));
//		assertEquals("Wrong count of bad fields by key <"+aTestKey+">.",
//				etalon, networkLoader.badFields.size());
    }

    /**
     * Execute assertion (short lines).
     * @param aTestKey String (key for test)
     */
    private void assertShortLines(String aTestKey) {
    	int etalon = parceStringToInt(getProperty("test_loader.short_lines."+aTestKey));
//		assertEquals("Wrong count of short lines by key <"+aTestKey+">.",
//				etalon, networkLoader.shortLines.size());
    }

    /**
     * Execute assertion (count of sectors).
     * @param aTestKey String (key for test)
     */
    private void assertSectorCount(String aTestKey) {
    	filename = getDbName(aTestKey);
        Transaction tx = getNeo().beginTx();
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
            Iterator<Node> iterator = getNeo().getReferenceNode().traverse(Order.DEPTH_FIRST, new StopEvaluator() {

                @Override
                public boolean isStopNode(TraversalPosition currentPos) {
                    return currentPos.depth() > 2;
                }
            }, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return currentPos.currentNode().hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                            && NodeTypes.NETWORK.getId().equals(currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME))
                            && currentPos.currentNode().hasProperty(INeoConstants.PROPERTY_NAME_NAME)
                            && filename.equals(currentPos.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME));
                }
            }, childRel, Direction.OUTGOING, nextRel, Direction.OUTGOING).iterator();
            Node network = iterator.next();
            long sectorNumber = (Long)network.getProperty("sector_count");
            Traverser traverse = network.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return currentPos.currentNode().hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                            && NodeTypes.SECTOR.getId().equals(currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME));
                }
            }, childRel, Direction.OUTGOING, nextRel, Direction.OUTGOING);
            int size = 0;
            iterator = traverse.iterator();
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
            assertEquals("Wrong count of sectors by key <"+aTestKey+">.",
            		sectorNumber, size);
        } finally {
            tx.finish();
        }
    }

}
