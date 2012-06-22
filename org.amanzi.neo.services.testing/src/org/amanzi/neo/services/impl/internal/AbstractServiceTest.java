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

package org.amanzi.neo.services.impl.internal;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.IDatabaseManager;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractServiceTest extends AbstractMockitoTest {

    private AbstractService service;

    private IDatabaseManager dbManager;

    private GraphDatabaseService graphDb;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        graphDb = mock(GraphDatabaseService.class);

        dbManager = DatabaseManagerFactory.getDatabaseManager();
        dbManager.setDatabaseService(graphDb);
    }

    @After
    public void tearDown() throws Exception {
        dbManager.cleanDatabaseEventListeners();
    }

    @Test
    public void testCheckServiceDbOnConstructore() {
        service = new AbstractService(graphDb) {
        };

        assertEquals("Unexpected GraphDb", graphDb, service.getGraphDb());
    }

    @Test
    public void testCheckServiceDbOnShutDown() {
        service = new AbstractService(graphDb) {
        };

        dbManager.shutdown();

        assertNull("GraphDb should be null", service.getGraphDb());
    }

    @Test
    public void testCheckServiceDbOnRestart() {
        service = new AbstractService(graphDb) {
        };

        dbManager.shutdown();
        dbManager.setDatabaseService(graphDb);

        assertNotNull("GraphDb cannot be null", service.getGraphDb());
        assertEquals("Unexpected GraphDb", graphDb, service.getGraphDb());
    }

}
