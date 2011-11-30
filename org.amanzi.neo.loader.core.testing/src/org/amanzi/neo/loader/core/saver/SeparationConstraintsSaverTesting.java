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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.db.manager.IDatabaseManager;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.parser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Vladislav_Kondratenko
 */
public class SeparationConstraintsSaverTesting extends AbstractAWETest {
    private static final Logger LOGGER = Logger.getLogger(SeparationConstraintsSaverTesting.class);
    private SeparationCostraintSaver separationSaver;
    private static String PATH_TO_BASE = "";
    private ConfigurationDataImpl config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";

    private static final String SECTOR_PARAM = "sector";
    private static final String SECTOR_VALUE = "s1";
    private static final String SEPARATION_PARAM = "separation";
    private static final Integer SEPARATION_VALUE = 123;
    private int MINIMAL_COLUMN_SIZE = 2;
    private static DataLoadPreferenceInitializer initializer;
    private final static Map<String, Object> COMPLETED_SECTOR = new HashMap<String, Object>();
    private final static Map<String, Object> COLLECTED_SECTOR = new HashMap<String, Object>();
    private static NetworkModel model;
    private static Long startTime;
    private static IDatabaseManager dbManager;

    @BeforeClass
    public static void prepare() {
        clearDb();
        initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
        new LogStarter().earlyStartup();
        startTime = System.currentTimeMillis();
    }

    static {
        PATH_TO_BASE = System.getProperty("user.home");
        COMPLETED_SECTOR.put(SEPARATION_PARAM, SEPARATION_VALUE);
        COMPLETED_SECTOR.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
        COLLECTED_SECTOR.put(AbstractService.NAME, SECTOR_VALUE);
        COLLECTED_SECTOR.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());

    }

    private HashMap<String, Object> hashMap = null;

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("NewNetworkSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Before
    public void onStart() throws AWEException {
        dbManager = mock(IDatabaseManager.class);
        model = mock(NetworkModel.class);
        hashMap = new HashMap<String, Object>();
        config = new ConfigurationDataImpl();
        config.getDatasetNames().put(NETWORK_KEY, NETWORK_NAME);
        config.getDatasetNames().put(PROJECT_KEY, PROJECT_NAME);
        List<File> fileList = new LinkedList<File>();
        File testFile = new File(PATH_TO_BASE + "/testFile.txt");
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            LOGGER.error(" onStart error while trying to create file", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        fileList.add(testFile);
        config.setSourceFile(fileList);
        separationSaver = new SeparationCostraintSaver(model, (ConfigurationDataImpl)config);
        separationSaver.dbManager = dbManager;
        hashMap.put(SECTOR_PARAM, SECTOR_VALUE);
        hashMap.put(SEPARATION_PARAM, SEPARATION_VALUE);
    }

    private List<String> prepareValues(HashMap<String, Object> map) {
        List<String> values = new LinkedList<String>();
        for (String key : map.keySet()) {
            values.add(map.get(key).toString());
        }
        return values;
    }

    @Test
    public void testCompleteingElement() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            separationSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            when(model.findElement(eq(COLLECTED_SECTOR))).thenReturn(new DataElement(COLLECTED_SECTOR));
            when(model.completeProperties(new DataElement(eq(COLLECTED_SECTOR)), eq(COMPLETED_SECTOR), any(Boolean.class)))
                    .thenReturn(new DataElement(COLLECTED_SECTOR));
            separationSaver.saveElement(rowContainer);
            verify(model, atLeastOnce()).completeProperties(new DataElement(eq(COLLECTED_SECTOR)), eq(COMPLETED_SECTOR),
                    any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testCompleteingElement error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIfSectorNotFound() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            separationSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            when(model.findElement(eq(COLLECTED_SECTOR))).thenReturn(null);
            when(model.completeProperties(new DataElement(eq(COLLECTED_SECTOR)), eq(COMPLETED_SECTOR), any(Boolean.class)))
                    .thenReturn(new DataElement(COLLECTED_SECTOR));
            separationSaver.saveElement(rowContainer);
            verify(model, never()).completeProperties(any(IDataElement.class), any(Map.class), any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testIfSectorNotFound error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIfThereIsNoValue() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            separationSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            COMPLETED_SECTOR.remove(SEPARATION_PARAM);
            when(model.findElement(eq(COLLECTED_SECTOR))).thenReturn(null);
            when(model.completeProperties(new DataElement(eq(COLLECTED_SECTOR)), eq(COMPLETED_SECTOR), any(Boolean.class)))
                    .thenReturn(new DataElement(COLLECTED_SECTOR));
            separationSaver.saveElement(rowContainer);
            verify(model, never()).completeProperties(any(IDataElement.class), any(Map.class), any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testIfThereIsNoValue error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @Test
    public void testTransactionRollBackIfDatabaseExceptionThrow() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            separationSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);

            rowContainer.setValues(values);
            when(model.findElement(any(Map.class))).thenThrow(new DatabaseException("required exception"));
            separationSaver.saveElement(rowContainer);
        } catch (Exception e) {
            verify(dbManager, never()).commitThreadTransaction();
            verify(dbManager, atLeastOnce()).rollbackThreadTransaction();

        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTransactionContiniousIfRestExceptionThrow() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            separationSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            when(model.findElement(any(Map.class))).thenThrow(new IllegalArgumentException("required exception"));
            separationSaver.saveElement(rowContainer);
            verify(dbManager, never()).rollbackThreadTransaction();

        } catch (Exception e) {
            Assert.fail("unexpected exceptions");
        }
    }
}
