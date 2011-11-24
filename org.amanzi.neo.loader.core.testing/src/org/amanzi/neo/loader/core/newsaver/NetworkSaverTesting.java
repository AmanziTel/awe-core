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

package org.amanzi.neo.loader.core.newsaver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * @author Kondratenko_Vladsialv
 */
public class NetworkSaverTesting extends AbstractAWETest {
    private static final Logger LOGGER = Logger.getLogger(NetworkSaverTesting.class);
    private NetworkSaver networkSaver;
    private static String PATH_TO_BASE = "";
    private ConfigurationDataImpl config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";
    private int MINIMAL_COLUMN_SIZE = 2;
    private static DataLoadPreferenceInitializer initializer;
    private final static Map<String, Object> BSC = new HashMap<String, Object>();
    private final static Map<String, Object> SITE = new HashMap<String, Object>();
    private final static Map<String, Object> SECTOR = new HashMap<String, Object>();
    private final static Map<String, Object> MSC = new HashMap<String, Object>();
    private final static Map<String, Object> CITY = new HashMap<String, Object>();
    private static NetworkModel model;
    private static Long startTime;
    private GraphDatabaseService service;
    private Transaction tx;
    static {
        PATH_TO_BASE = System.getProperty("user.home");
        BSC.put("name", "bsc1");
        BSC.put("type", "bsc");
        SITE.put("name", "site1");
        SITE.put("type", "site");
        SITE.put("lat", 3.123);
        SITE.put("lon", 2.1234);
        SECTOR.put("name", "sector1");
        SECTOR.put("type", "sector1");
        MSC.put("name", "msc1");
        MSC.put("type", "msc");
        CITY.put("name", "city1");
        CITY.put("type", "city");
    }

    private HashMap<String, Object> hashMap = null;

    @BeforeClass
    public static void prepare() {
        new LogStarter().earlyStartup();
        clearDb();
        initializeDb();

        initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
        startTime = System.currentTimeMillis();

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("NewNetworkSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Before
    public void onStart() throws AWEException {
        model = mock(NetworkModel.class);
        service = mock(GraphDatabaseService.class);
        tx = mock(Transaction.class);
        when(service.beginTx()).thenReturn(tx);
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
        networkSaver = new NetworkSaver(model, (ConfigurationDataImpl)config, service);
        hashMap.put("bsc", "bsc1");
        hashMap.put("site", "site1");
        hashMap.put("city", "city1");
        hashMap.put("msc", "msc1");
        hashMap.put("lat", "3.123");
        hashMap.put("lon", "2.1234");
        hashMap.put("sector", "sector1");
        hashMap.put("ci", "120");
        hashMap.put("lac", "332");
        hashMap.put("beamwidth", "3");

    }

    private List<String> prepareValues(HashMap<String, Object> map) {
        List<String> values = new LinkedList<String>();
        for (String key : map.keySet()) {
            values.add(map.get(key).toString());
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testForSavingAllElements() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            networkSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);

            when(model.findElement(BSC)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(BSC))).thenReturn(new DataElement(BSC));
            when(model.findElement(SITE)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SITE))).thenReturn(new DataElement(SITE));
            when(model.findElement(SECTOR)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SECTOR))).thenReturn(new DataElement(SECTOR));
            when(model.findElement(MSC)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(MSC))).thenReturn(new DataElement(MSC));
            when(model.findElement(CITY)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(CITY))).thenReturn(new DataElement(CITY));
            networkSaver.saveElement(rowContainer);
            verify(model, times(5)).createElement(any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testForSavingAllElements error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testForSavingSITESECTOR() {
        hashMap.remove("msc");
        hashMap.remove("bsc");
        hashMap.remove("city");
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            networkSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);

            when(model.findElement(SITE)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SITE))).thenReturn(new DataElement(SITE));
            when(model.findElement(SECTOR)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SECTOR))).thenReturn(new DataElement(SECTOR));
            networkSaver.saveElement(rowContainer);
            verify(model, times(2)).createElement(any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testForSavingSITESECTOR error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testForTyingToSaveOnlySector() {
        hashMap.remove("msc");
        hashMap.remove("bsc");
        hashMap.remove("city");
        hashMap.remove("site");
        hashMap.remove("lat");
        hashMap.remove("lon");
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            networkSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            networkSaver.saveElement(rowContainer);
            verify(model, never()).createElement(any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testForTyingToSaveOnlySector error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testForTyingToSaveSectorAndSiteWithLatAndLon() {
        hashMap.remove("msc");
        hashMap.remove("bsc");
        hashMap.remove("city");
        hashMap.remove("site");
        SITE.put("name", "sector");
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            networkSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            when(model.findElement(SITE)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SITE))).thenReturn(new DataElement(SITE));
            when(model.findElement(SECTOR)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SECTOR))).thenReturn(new DataElement(SECTOR));

            networkSaver.saveElement(rowContainer);
            networkSaver.finishUp();
            verify(model, times(2)).createElement(any(IDataElement.class), any(Map.class));
            verify(tx, atLeastOnce()).success();
        } catch (Exception e) {
            LOGGER.error(" testForTyingToSaveSectorAndSiteWithLatAndLon error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTransactionRollBackIfDatabaseExceptionThrow() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            networkSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            when(model.findElement(any(Map.class))).thenThrow(new DatabaseException("required exception"));
            networkSaver.saveElement(rowContainer);
        } catch (Exception e) {
            verify(tx, never()).success();
            verify(tx, atLeastOnce()).failure();
            verify(tx, times(1)).finish();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTransactionContiniousIfRestExceptionThrow() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            networkSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            when(model.findElement(any(Map.class))).thenThrow(new IllegalArgumentException("required exception"));
            networkSaver.saveElement(rowContainer);
        } catch (Exception e) {
            verify(tx, times(2)).success();
            verify(tx, never()).failure();
            verify(tx, times(3)).finish();
        }
    }
}
