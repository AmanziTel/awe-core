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

package org.amanzi.neo.db.manager.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.IDatabaseManager.AccessType;
import org.amanzi.neo.db.manager.events.DatabaseEvent;
import org.amanzi.neo.db.manager.events.DatabaseEvent.EventType;
import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.apache.commons.io.FileUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

/**
 * Tests on DatabaseManager Test
 * 
 * @author gerzog
 * @since 1.0.0 TODO: re-write with using Mockito
 */
public class Neo4jDatabaseManagerTest {

    /** String INCORRECT_MEMORY_MAPPING field */
    private static final String INCORRECT_MEMORY_MAPPING = "Incorrect Memory Mapping";

    /** String INCORRECT_ACCESS_TYPE field */
    private static final String INCORRECT_ACCESS_TYPE = "Incorrect Access Type";

    /** String INCORRECT_DATABASE_LOCATION field */
    private static final String INCORRECT_DATABASE_LOCATION = "Incorrect Database location";

    private static final String USER_HOME = "user.home";

    private static final String[] TEST_DIRECTORIES = new String[] {".amanzi", "test"};

    private static final String[] NEO4J_DEFAULT_DIRECTORIES = new String[] {".amanzi", "neo"};

    private static final String DATABASE_DIRECTORY = "neo";

    private static final Random RANDOM = new Random();

    private final Mockery context = new Mockery();

    /**
     * Type of Event Actions
     * 
     * @author gerzog
     * @since 1.0.0
     */
    private static enum EventActionType {
        SHUTDOWN, STARTUP, FULL_COMMIT, FULL_ROLLBACK;
    }

    @BeforeClass
    public static void setUpTest() {
        // initialize Log4j
        new LogStarter().earlyStartup();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        clearDbLocation(new File(getDirectoryLocation(TEST_DIRECTORIES)));
        clearDbLocation(new File(getDirectoryLocation(NEO4J_DEFAULT_DIRECTORIES)));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        DatabaseManagerFactory.getDatabaseManager().cleanDatabaseEventListeners();
        DatabaseManagerFactory.getDatabaseManager().shutdown();

        clearDbLocation(new File(getDirectoryLocation(TEST_DIRECTORIES)));
        clearDbLocation(new File(getDirectoryLocation(NEO4J_DEFAULT_DIRECTORIES)));

    }

    private static String getDirectoryLocation(String[] subDirectories) throws IOException {
        String userHome = System.getProperty(USER_HOME);

        File testHomeFile = new File(userHome);
        for (String subDir : subDirectories) {
            testHomeFile = new File(testHomeFile, subDir);
        }
        FileUtils.forceMkdir(testHomeFile);

        return testHomeFile.getAbsolutePath();
    }

    private static void clearDbLocation(File dbLocation) throws IOException {
        if (dbLocation.exists()) {
            for (File subFile : dbLocation.listFiles()) {
                if (subFile.isDirectory()) {
                    clearDbLocation(subFile);
                } else {
                    FileUtils.forceDelete(subFile);
                }
            }
            FileUtils.forceDelete(dbLocation);
        }
    }

    private String getRandomDatabaseLocation() throws IOException {
        File databaseDirectory = new File(new File(getDirectoryLocation(TEST_DIRECTORIES)), DATABASE_DIRECTORY + RANDOM.nextInt());
        FileUtils.forceMkdir(databaseDirectory);
        return databaseDirectory.getAbsolutePath();
    }

    private String getDefaultDatabaseLocation() throws IOException {
        File databaseDirectory = new File(new File(getDirectoryLocation(TEST_DIRECTORIES)), DATABASE_DIRECTORY);
        FileUtils.forceMkdir(databaseDirectory);
        return databaseDirectory.getAbsolutePath();
    }

    private String getDefaultNeo4jDatabaseLocation() throws IOException {
        File databaseDirectory = new File(getDirectoryLocation(NEO4J_DEFAULT_DIRECTORIES));
        FileUtils.forceMkdir(databaseDirectory);
        return databaseDirectory.getAbsolutePath();
    }

    private Map<String, String> getFakeMappingParameters() {
        HashMap<String, String> result = new HashMap<String, String>();

        result.put("some key", "some value");

        return result;
    }

    @Test
    public void checkFullConstructor() throws IOException {
        final String dbLocation = getDefaultDatabaseLocation();
        final AccessType accessType = AccessType.READ_ONLY;
        final Map<String, String> memoryMapping = getFakeMappingParameters();

        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation, accessType, memoryMapping);

        Assert.assertEquals(INCORRECT_DATABASE_LOCATION, dbLocation, dbManager.getLocation());
        Assert.assertEquals(INCORRECT_ACCESS_TYPE, accessType, dbManager.getAccessType());
        Assert.assertEquals(INCORRECT_MEMORY_MAPPING, memoryMapping, dbManager.getMemoryMapping());
    }

    @Test
    public void checkConstructorWithDefaultMemoryMapping() throws IOException {
        final String dbLocation = getRandomDatabaseLocation();
        final AccessType accessType = AccessType.READ_ONLY;

        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation, accessType);

        Assert.assertEquals(INCORRECT_DATABASE_LOCATION, dbLocation, dbManager.getLocation());
        Assert.assertEquals(INCORRECT_ACCESS_TYPE, accessType, dbManager.getAccessType());
        Assert.assertEquals(INCORRECT_MEMORY_MAPPING, Neo4jDatabaseManager.DEFAULT_MEMORY_MAPPING, dbManager.getMemoryMapping());
    }

    @Test
    public void checkConstructorWithDefaultAccessType() throws IOException {
        final String dbLocation = getRandomDatabaseLocation();
        final Map<String, String> memoryMapping = getFakeMappingParameters();

        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation, memoryMapping);

        Assert.assertEquals(INCORRECT_DATABASE_LOCATION, dbLocation, dbManager.getLocation());
        Assert.assertEquals(INCORRECT_ACCESS_TYPE, AccessType.READ_WRITE, dbManager.getAccessType());
        Assert.assertEquals(INCORRECT_MEMORY_MAPPING, memoryMapping, dbManager.getMemoryMapping());
    }

    @Test
    public void checkConstructorWithDefaultLocation() throws IOException {
        final AccessType accessType = AccessType.READ_ONLY;
        final Map<String, String> memoryMapping = getFakeMappingParameters();

        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(accessType, memoryMapping);

        Assert.assertEquals(INCORRECT_DATABASE_LOCATION, getDefaultNeo4jDatabaseLocation(), dbManager.getLocation());
        Assert.assertEquals(INCORRECT_ACCESS_TYPE, AccessType.READ_ONLY, dbManager.getAccessType());
        Assert.assertEquals(INCORRECT_MEMORY_MAPPING, memoryMapping, dbManager.getMemoryMapping());
    }

    @Test
    public void checkConstructorWithDefaultMemoryMappingAndAccessType() throws IOException {
        final String dbLocation = getRandomDatabaseLocation();

        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation);

        Assert.assertEquals(INCORRECT_DATABASE_LOCATION, dbLocation, dbManager.getLocation());
        Assert.assertEquals(INCORRECT_ACCESS_TYPE, AccessType.getDefaulAccessType(), dbManager.getAccessType());
        Assert.assertEquals(INCORRECT_MEMORY_MAPPING, Neo4jDatabaseManager.DEFAULT_MEMORY_MAPPING, dbManager.getMemoryMapping());
    }

    @Test
    public void checkConstructorWithDefaultMemoryMappingAndLocation() throws IOException {
        final AccessType accessType = AccessType.READ_ONLY;

        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(accessType);

        Assert.assertEquals(INCORRECT_DATABASE_LOCATION, getDefaultNeo4jDatabaseLocation(), dbManager.getLocation());
        Assert.assertEquals(INCORRECT_ACCESS_TYPE, AccessType.READ_ONLY, dbManager.getAccessType());
        Assert.assertEquals(INCORRECT_MEMORY_MAPPING, Neo4jDatabaseManager.DEFAULT_MEMORY_MAPPING, dbManager.getMemoryMapping());
    }

    @Test
    public void checkConstructorWithDefaultLocationAndAccessType() throws IOException {
        final Map<String, String> memoryMapping = getFakeMappingParameters();

        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(memoryMapping);

        Assert.assertEquals(INCORRECT_DATABASE_LOCATION, getDefaultNeo4jDatabaseLocation(), dbManager.getLocation());
        Assert.assertEquals(INCORRECT_ACCESS_TYPE, AccessType.READ_WRITE, dbManager.getAccessType());
        Assert.assertEquals(INCORRECT_MEMORY_MAPPING, memoryMapping, dbManager.getMemoryMapping());
    }

    @Test
    public void checkDefaultConstructor() throws IOException {
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager();

        Assert.assertEquals(INCORRECT_DATABASE_LOCATION, getDefaultNeo4jDatabaseLocation(), dbManager.getLocation());
        Assert.assertEquals(INCORRECT_ACCESS_TYPE, AccessType.READ_WRITE, dbManager.getAccessType());
        Assert.assertEquals(INCORRECT_MEMORY_MAPPING, Neo4jDatabaseManager.DEFAULT_MEMORY_MAPPING, dbManager.getMemoryMapping());
    }

    @Test
    public void checkGraphDatabaseService() {
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager();

        Assert.assertNotNull("Graph DB Service should not be null", dbManager.getDatabaseService());

        // shutdown db
        dbManager.shutdown();
    }

    @Test
    public void checkReadWriteDatabaseService() {
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(AccessType.READ_WRITE);

        Assert.assertEquals("Type of Graph DB Service incorrect", EmbeddedGraphDatabase.class, dbManager.getDatabaseService()
                .getClass());

        // shutdown db
        dbManager.shutdown();
    }

    @Test
    public void checkDatabaseLocationOfService() throws IOException {
        String dbLocation = getDefaultDatabaseLocation();

        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation);

        Assert.assertTrue("Location of Service incorrect", dbManager.getDatabaseService().toString().contains(dbLocation));

        // shutdown db
        dbManager.shutdown();
    }

    @Test
    public void checkBeforeAndAfterStartupEvent() {
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager();
        dbManager.addDatabaseEventListener(getDatabaseListener(EventType.BEFORE_STARTUP, EventType.AFTER_STARTUP));

        dbManager.getDatabaseService();

        context.assertIsSatisfied();
    }

    @Test
    public void checkReadOnlyDatabaseService() {
        // for read only we need to have already created DB
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager();
        dbManager.getDatabaseService();
        dbManager.shutdown();

        dbManager = new Neo4jDatabaseManager(AccessType.READ_ONLY);

        Assert.assertEquals("Type of Graph DB Service incorrect", EmbeddedReadOnlyGraphDatabase.class, dbManager
                .getDatabaseService().getClass());

        // shutdown db
        dbManager.shutdown();
    }

    @Test
    public void checkBeforeAndAfterShutdownEvent() {
        Neo4jDatabaseManager dbManager = getMockDbManagerForEvents(EventActionType.SHUTDOWN, EventType.BEFORE_SHUTDOWN,
                EventType.AFTER_SHUTDOWN);

        dbManager.shutdown();

        context.assertIsSatisfied();
    }

    @Test
    public void checkBeforeAndAfterFullCommitEvent() {
        Neo4jDatabaseManager dbManager = getMockDbManagerForEvents(EventActionType.FULL_COMMIT, EventType.BEFORE_FULL_COMMIT,
                EventType.AFTER_FULL_COMMIT);

        dbManager.commitMainTransaction();

        context.assertIsSatisfied();
    }

    @Test
    public void checkBeforeAndAfterFullRollbackEvent() {
        Neo4jDatabaseManager dbManager = getMockDbManagerForEvents(EventActionType.FULL_ROLLBACK, EventType.BEFORE_FULL_ROLLBACK,
                EventType.AFTER_FULL_ROLLBACK);

        dbManager.rollbackMainTransaction();

        context.assertIsSatisfied();
    }

    /**
     * Creates Mock for Event testing
     * 
     * @param eventType type of event to test
     * @return Neo4jDatabaseManager configured with corresponding mocks
     */
    private Neo4jDatabaseManager getMockDbManagerForEvents(EventActionType actionType, EventType... eventTypes) {
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager();
        dbManager.setDatabaseService(getGraphDbServiceMock(actionType));

        IDatabaseEventListener listener = getDatabaseListener(eventTypes);
        dbManager.addDatabaseEventListener(listener);

        return dbManager;
    }

    /**
     * Returns a Mock for Graph DB Service
     * 
     * @return
     */
    private GraphDatabaseService getGraphDbServiceMock(EventActionType actionType) {
        final GraphDatabaseService service = context.mock(GraphDatabaseService.class);

        switch (actionType) {
        case SHUTDOWN:
            context.checking(new Expectations() {
                {
                    oneOf(service).shutdown();
                }
            });
            break;
        default:
            // do nothing
            break;
        }

        return service;
    }

    private IDatabaseEventListener getDatabaseListener(final EventType... eventTypes) {
        final IDatabaseEventListener eventListener = context.mock(IDatabaseEventListener.class);

        for (final EventType eventType : eventTypes) {
            context.checking(new Expectations() {
                {
                    oneOf(eventListener).onDatabaseEvent(with(equal(new DatabaseEvent(eventType))));
                }
            });
        }

        return eventListener;
    }

}
