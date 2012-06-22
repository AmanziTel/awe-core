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

package org.amanzi.neo.loader.core.saver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.db.manager.IDatabaseManager;
import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.testing.AbstractAWEDBTest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Vladislav_Kondratenko
 */
public class TrafficSaverTesting extends AbstractAWEDBTest {
    private static final Logger LOGGER = Logger.getLogger(TrafficSaverTesting.class);
    private TrafficSaver trafficSaver;
    private static String PATH_TO_BASE = "";
    private NetworkConfiguration config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";

    private static final String SECTOR_PARAM = "sector";
    private static final String SECTOR_VALUE = "s1";
    private static final String TRAFFIC_PARAM = "traffic";
    private static final Integer TRAFFIC_VALUE = 123;
    private int MINIMAL_COLUMN_SIZE = 2;
    private static DataLoadPreferenceInitializer initializer;
    private final static Map<String, Object> COMPLETED_SECTOR = new HashMap<String, Object>();
    private final static Map<String, Object> COLLECTED_SECTOR = new HashMap<String, Object>();
    private static NetworkModel networkModelMock;
    private static Long startTime;
    private static IDatabaseManager dbManager;

    @BeforeClass
    public static void prepare() {
        clearDb();
        initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
        new LogStarter().earlyStartup();
        startTime = System.currentTimeMillis();
        dbManager = mock(IDatabaseManager.class);
    }

    static {
        PATH_TO_BASE = System.getProperty("user.home");
        COMPLETED_SECTOR.put(SECTOR_PARAM, SECTOR_VALUE);
        COMPLETED_SECTOR.put(TRAFFIC_PARAM, TRAFFIC_VALUE);
        COLLECTED_SECTOR.put(AbstractService.NAME, SECTOR_VALUE);
        COLLECTED_SECTOR.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
    }

    private HashMap<String, String> hashMap = null;

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("NewNetworkSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Before
    public void onStart() throws AWEException {
        networkModelMock = mock(NetworkModel.class);
        hashMap = new HashMap<String, String>();
        config = new NetworkConfiguration();
        config.setDatasetName(NETWORK_NAME);
        File testFile = new File(PATH_TO_BASE + "/testFile.txt");
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            LOGGER.error(" onStart error while trying to create file", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        config.setFile(testFile);
        trafficSaver = new TrafficSaver() {
            @Override
            public void init(NetworkConfiguration configuration) throws AWEException {
                // TODO: verify
                this.networkModel = networkModelMock;
                setMainModel(networkModelMock);
            }
        };
        trafficSaver.init(config);
        trafficSaver.dbManager = dbManager;
        hashMap.put(SECTOR_PARAM, SECTOR_VALUE);
        hashMap.put(TRAFFIC_PARAM, TRAFFIC_VALUE.toString());
    }

    @Test
    public void testCompleteingElement() {
        MappedData dataElement = new MappedData(hashMap);

        try {
            Set<IDataElement> mockResult = new HashSet<IDataElement>();
            mockResult.add(new DataElement(COLLECTED_SECTOR));
            when(networkModelMock.findElementByPropertyValue(NetworkElementNodeType.SECTOR,AbstractService.NAME, SECTOR_VALUE)).thenReturn(mockResult);
            when(
                    networkModelMock.completeProperties(new DataElement(eq(COLLECTED_SECTOR)), eq(COMPLETED_SECTOR),
                            any(Boolean.class))).thenReturn(new DataElement(COLLECTED_SECTOR));
            trafficSaver.save(dataElement);
            verify(networkModelMock, atLeastOnce()).completeProperties(any(IDataElement.class), any(Map.class),
                    any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testCompleteingElement error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIfSectorNotFound() {
        MappedData dataElement = new MappedData(hashMap);

        try {
            when(networkModelMock.findElement(eq(COLLECTED_SECTOR))).thenReturn(null);
            when(
                    networkModelMock.completeProperties(new DataElement(eq(COLLECTED_SECTOR)), eq(COMPLETED_SECTOR),
                            any(Boolean.class))).thenReturn(new DataElement(COLLECTED_SECTOR));
            trafficSaver.save(dataElement);
            verify(networkModelMock, never()).completeProperties(any(IDataElement.class), any(Map.class), any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testIfSectorNotFound error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIfThereIsNoValue() {
        MappedData dataElement = new MappedData(hashMap);

        try {
            COMPLETED_SECTOR.remove(TRAFFIC_PARAM);
            when(networkModelMock.findElement(eq(COLLECTED_SECTOR))).thenReturn(null);
            when(
                    networkModelMock.completeProperties(new DataElement(eq(COLLECTED_SECTOR)), eq(COMPLETED_SECTOR),
                            any(Boolean.class))).thenReturn(new DataElement(COLLECTED_SECTOR));
            trafficSaver.save(dataElement);
            verify(networkModelMock, never()).completeProperties(any(IDataElement.class), any(Map.class), any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testIfThereIsNoValue error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTransactionRollBackIfDatabaseExceptionThrow() {
        MappedData dataElement = new MappedData(hashMap);

        try {
            when(networkModelMock.findElement(any(Map.class))).thenThrow(new DatabaseException("required exception"));
            trafficSaver.save(dataElement);
        } catch (Exception e) {
            verify(dbManager, never()).commitThreadTransaction();
            verify(dbManager, atLeastOnce()).rollbackThreadTransaction();

        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTransactionContiniousIfRestExceptionThrow() {
        MappedData dataElement = new MappedData(hashMap);

        try {
            when(networkModelMock.findElement(any(Map.class))).thenThrow(new IllegalArgumentException("required exception"));
            trafficSaver.save(dataElement);
            verify(dbManager, never()).rollbackThreadTransaction();

        } catch (Exception e) {
            Assert.fail("unexpected exceptions");
        }
    }
}