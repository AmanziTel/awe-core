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

package org.amanzi.testing;

import java.io.File;
import java.io.IOException;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.ErrorState;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractIntegrationTest extends AbstractTest {

    /**
     * TODO Purpose of
     * <p>
     * </p>
     * 
     * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
     * @since 1.0.0
     */
    private final class TestKernelListener implements KernelEventHandler {
        @Override
        public void beforeShutdown() {
            cleanDatabase();
            isRunning = false;
        }

        @Override
        public void kernelPanic(ErrorState error) {
        }

        @Override
        public Object getResource() {
            return null;
        }

        @Override
        public ExecutionOrder orderComparedTo(KernelEventHandler other) {
            return null;
        }
    }

    private static final String[] TEST_DB_LOCATION = new String[] {System.getProperty("user.home"), ".amanzi", "neo_test"};

    private static String defaultLocation;

    private GraphDatabaseService graphDb;

    private boolean isRunning;

    @BeforeClass
    public static void setUpClass() throws IOException {
        AbstractTest.setUpClass();

        File defaultDb = null;

        for (String singleLocation : TEST_DB_LOCATION) {
            if (defaultDb != null) {
                defaultDb = new File(defaultDb, singleLocation);
            } else {
                defaultDb = new File(singleLocation);
            }

            FileUtils.forceMkdir(defaultDb);
        }

        defaultLocation = defaultDb.getAbsolutePath();
    }

    public static void tearDownClass() throws IOException {
        delete(new File(defaultLocation));
    }

    @Before
    public void setUp() {
        this.graphDb = new EmbeddedGraphDatabase(defaultLocation);
        DatabaseManagerFactory.getDatabaseManager().setDatabaseService(graphDb);

        isRunning = true;

        graphDb.registerKernelEventHandler(new TestKernelListener());
    }

    @After
    public void tearDown() {
        DatabaseManagerFactory.getDatabaseManager().shutdown();
    }

    private void cleanDatabase() {
        Transaction tx = graphDb.beginTx();

        for (Relationship relationship : graphDb.getReferenceNode().getRelationships()) {
            relationship.delete();
        }

        tx.success();
        tx.finish();
    }

    private static void delete(File directory) throws IOException {
        for (File singleFile : directory.listFiles()) {
            if (singleFile.isDirectory()) {
                delete(singleFile);
            } else {
                FileUtils.forceDelete(singleFile);
            }
        }

        FileUtils.forceDelete(directory);
    }

    protected GraphDatabaseService getGraphDatabaseService() {
        if (isRunning) {
            return this.graphDb;
        } else {
            return null;
        }
    }

    protected String getDefaultLocation() {
        return defaultLocation;
    }

    protected Node createNode() {
        Transaction tx = graphDb.beginTx();

        Node result = graphDb.createNode();

        tx.success();
        tx.finish();

        return result;
    }

    protected Node createNode(String property, Object value) {
        Transaction tx = graphDb.beginTx();

        Node node = createNode();
        node.setProperty(property, value);

        tx.success();
        tx.finish();

        return node;
    }

}
