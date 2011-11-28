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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.DriveModel.DriveRelationshipTypes;
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
public class TemsSaverTesting extends AbstractAWETest {
    private static final Logger LOGGER = Logger.getLogger(TemsSaverTesting.class);
    private TemsSaver temsSaver;
    private static String PATH_TO_BASE = "";
    private IConfiguration config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String TIME = "time";
    private static final String TIMESTAMP = "timestamp";
    private static final String EVENT = "event";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String SECTOR_ID = "sector_id";
    private static String BCCH = "bcch";
    private static final String TCH = "tch";
    private static final String SC = "sc";
    private static final String PN = "PN";
    private static final String ECIO = "ecio";
    private static final String RSSI = "rssi";
    private static final String MS = "ms";
    private static final String MESSAGE_TYPE = "message_type";
    private static final String ALL_RXLEV_FULL = "all_rxlev_full";
    private static final String ALL_RXLEV_SUB = "all_rxlev_sub";
    private static final String ALL_RXQUAL_FULL = "all_rxqual_full";
    private static final String ALL_RXQUAL_SUB = "all_rxqual_sub";
    private static final String ALL_SQI = "all_sqi";
    private static final String ALL_SQI_MOS = "all_sqi_mos";
    private static final String CHANNEL = "channel";
    private static final String CODE = "code";
    private static final String MW = "mw";
    private static final String DBM = "dbm";
    private static final String PROJECT_NAME = "project";
    private static final String ALL_PILOT_SET_EC_IO = "all_pilot_set_ec_io_";
    private static final String ALL_PILOT_SET_CHANNEL = "all_pilot_set_channel_";
    private static final String ALL_PILOT_SET_PN = "all_pilot_set_pn_";
    private static final String ALL_PILOT_SET_COUNT = "all_pilot_set_count";
    private int MINIMAL_COLUMN_SIZE = 2;
    private static DataLoadPreferenceInitializer initializer;
    private final static Map<String, Object> collectedElement = new HashMap<String, Object>();
    private final static Map<String, Object> msCollected1 = new HashMap<String, Object>();
    private final static Map<String, Object> msCollected2 = new HashMap<String, Object>();
    private static DriveModel model;
    private static Long startTime;
    private Calendar workDate = Calendar.getInstance();
    static {
        PATH_TO_BASE = System.getProperty("user.home");
        collectedElement.put(TIME, "Aug 6 12:13:14.15");
        collectedElement.put(LATITUDE, 12d);
        collectedElement.put(NewNetworkService.NAME, "Aug 6 12:13:14.15");
        collectedElement.put(LONGITUDE, 13d);
        collectedElement.put(MS, "MS2");
        collectedElement.put(SECTOR_ID, "a1");
        collectedElement.put(BCCH, 1);
        collectedElement.put(TCH, 2);
        collectedElement.put(SC, 2);
        collectedElement.put(PN, 2);
        collectedElement.put(ECIO, 2);
        collectedElement.put(RSSI, 2);
        collectedElement.put(EVENT, "ev");
        collectedElement.put(MESSAGE_TYPE, "mt");
        collectedElement.put(NewAbstractService.TYPE, DriveNodeTypes.M.getId());
        msCollected1.put(MW, 15.848932266235352d);
        msCollected1.put(DBM, 12.0f);
        msCollected1.put(CODE, 32);
        msCollected1.put(NewAbstractService.TYPE, DriveNodeTypes.MS.getId());
        msCollected1.put(NewAbstractService.NAME, "32");
        msCollected1.put(CHANNEL, 22);
        msCollected1.put(TIMESTAMP, 0l);

        msCollected2.put(MW, 12.589254379272461d);
        msCollected2.put(DBM, 11.0f);
        msCollected2.put(CODE, 31);
        msCollected2.put(NewAbstractService.TYPE, DriveNodeTypes.MS.getId());
        msCollected2.put(NewAbstractService.NAME, "31");
        msCollected2.put(CHANNEL, 21);
        msCollected2.put(TIMESTAMP, 0l);
    }

    private HashMap<String, Object> hashMap = null;
    private DriveModel virtualModel;

    @BeforeClass
    public static void prepare() {
        new LogStarter().earlyStartup();
        clearDb();

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
        model = mock(DriveModel.class);
        virtualModel = mock(DriveModel.class);
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
        temsSaver = new TemsSaver(model, virtualModel, (ConfigurationDataImpl)config);
        hashMap.put(TIME, "Aug 6 12:13:14.15");
        hashMap.put("latitude", "12");
        hashMap.put("longitude", "13");
        hashMap.put(MS, "MS2");
        hashMap.put(EVENT, "ev");
        hashMap.put(SECTOR_ID, "a1");
        hashMap.put(BCCH, "1");
        hashMap.put(TCH, "2");
        hashMap.put(SC, "2");
        hashMap.put(PN, "2");
        hashMap.put(ECIO, "2");
        hashMap.put(RSSI, "2");
        hashMap.put(MESSAGE_TYPE, "mt");
        hashMap.put(ALL_RXLEV_FULL, "2");
        hashMap.put(ALL_RXLEV_SUB, "2");
        hashMap.put(ALL_RXQUAL_SUB, "2");
        hashMap.put(ALL_RXQUAL_FULL, "2");
        hashMap.put(ALL_SQI, "2");
        hashMap.put(ALL_SQI_MOS, "2");
        collectedElement.put(ALL_RXLEV_FULL, 2);
        collectedElement.put(ALL_RXLEV_SUB, 2);
        collectedElement.put(ALL_RXQUAL_SUB, 2);
        collectedElement.put(ALL_RXQUAL_FULL, 2);
        collectedElement.put(ALL_SQI, 2);
        collectedElement.put(ALL_SQI_MOS, 2);
        collectedElement.put(RSSI, 2);

        for (int i = 1; i < 2; i++) {
            hashMap.put(ALL_PILOT_SET_EC_IO + i, "1" + i);
            hashMap.put(ALL_PILOT_SET_CHANNEL + i, "2" + i);
            hashMap.put(ALL_PILOT_SET_PN + i, "3" + i);
            collectedElement.put(ALL_PILOT_SET_EC_IO + i, Integer.parseInt("1" + i));
            collectedElement.put(ALL_PILOT_SET_CHANNEL + i, Integer.parseInt("2" + i));
            collectedElement.put(ALL_PILOT_SET_PN + i, Integer.parseInt("3" + i));
        }
        hashMap.put(ALL_PILOT_SET_COUNT, "1");
        collectedElement.put(ALL_PILOT_SET_COUNT, 1);
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
    public void testSavingAllElement() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setFile(config.getFilesToLoad().get(0));
        rowContainer.setHeaders(header);
        Map<String, Object> createdMainElement = new HashMap<String, Object>();
        createdMainElement.putAll(collectedElement);
        createdMainElement.put(NewAbstractService.TYPE, DriveNodeTypes.M.getId());
        Map<String, Object> location = new HashMap<String, Object>();
        location.put(LATITUDE, collectedElement.get(LATITUDE));
        location.put(LONGITUDE, collectedElement.get(LONGITUDE));
        Set<IDataElement> locListSet = new HashSet<IDataElement>();
        locListSet.add(new DataElement(location));
        try {
            when(model.addFile(eq(rowContainer.getFile()))).thenReturn(new DataElement(new HashMap<String, Object>()));
            when(virtualModel.addFile(eq(rowContainer.getFile()))).thenReturn(new DataElement(new HashMap<String, Object>()));
            temsSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            collectedElement.put(TIMESTAMP, temsSaver.defineTimestamp(workDate, collectedElement.get(TIME).toString()));
            createdMainElement.putAll(collectedElement);
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement), any(Boolean.class))).thenReturn(
                    new DataElement(createdMainElement));
            when(virtualModel.addMeasurement(eq(rowContainer.getFile().getName()), eq(msCollected1), any(Boolean.class)))
                    .thenReturn(new DataElement(msCollected1));
            when(virtualModel.addMeasurement(eq(rowContainer.getFile().getName()), eq(msCollected2), any(Boolean.class)))
                    .thenReturn(new DataElement(msCollected2));
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement), any(Boolean.class))).thenReturn(
                    new DataElement(createdMainElement));
            when(model.getLocations(new DataElement(eq(createdMainElement)))).thenReturn(locListSet);
            when(virtualModel.getLocations(new DataElement(eq(msCollected2)))).thenReturn(locListSet);
            temsSaver.saveElement(rowContainer);

            verify(virtualModel, atLeastOnce()).addMeasurement(eq(rowContainer.getFile().getName()), eq(msCollected2),
                    any(Boolean.class));
            verify(model, atLeastOnce()).getLocations(new DataElement(eq(createdMainElement)));
            verify(virtualModel, atLeastOnce()).getLocations(new DataElement(eq(msCollected2)));
        } catch (Exception e) {
            LOGGER.error(" testSavingAllElement error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIfThereIsNoTimeValue() {
        hashMap.remove(TIME);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setFile(config.getFilesToLoad().get(0));
        rowContainer.setHeaders(header);
        Map<String, Object> createdMainElement = new HashMap<String, Object>();
        createdMainElement.putAll(collectedElement);
        createdMainElement.put(NewAbstractService.TYPE, DriveNodeTypes.M.getId());
        Map<String, Object> location = new HashMap<String, Object>();
        location.put(LATITUDE, collectedElement.get(LATITUDE));
        location.put(LONGITUDE, collectedElement.get(LONGITUDE));
        Set<IDataElement> locListSet = new HashSet<IDataElement>();
        locListSet.add(new DataElement(location));
        try {
            when(model.addFile(eq(rowContainer.getFile()))).thenReturn(new DataElement(new HashMap<String, Object>()));
            when(virtualModel.addFile(eq(rowContainer.getFile()))).thenReturn(new DataElement(new HashMap<String, Object>()));
            temsSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            collectedElement.put(TIMESTAMP, temsSaver.defineTimestamp(workDate, collectedElement.get(TIME).toString()));
            createdMainElement.putAll(collectedElement);
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement))).thenReturn(
                    new DataElement(createdMainElement));
            when(virtualModel.addMeasurement(eq(rowContainer.getFile().getName()), eq(msCollected1))).thenReturn(
                    new DataElement(msCollected1));
            when(virtualModel.addMeasurement(eq(rowContainer.getFile().getName()), eq(msCollected2))).thenReturn(
                    new DataElement(msCollected2));
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement))).thenReturn(
                    new DataElement(createdMainElement));
            when(model.getLocations(new DataElement(eq(createdMainElement)))).thenReturn(locListSet);
            temsSaver.saveElement(rowContainer);
            verify(virtualModel, never()).addMeasurement(any(String.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testSavingAllElement error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @Test
    public void testCanntFindPropertiesForVirtualDatasetElements() {
        for (int i = 1; i < 2; i++) {
            hashMap.remove(ALL_PILOT_SET_EC_IO + i);
            hashMap.remove(ALL_PILOT_SET_CHANNEL + i);
            hashMap.remove(ALL_PILOT_SET_PN + i);
            collectedElement.remove(ALL_PILOT_SET_CHANNEL + i);
            collectedElement.remove(ALL_PILOT_SET_EC_IO + i);
            collectedElement.remove(ALL_PILOT_SET_PN + i);
        }
        hashMap.remove(ALL_PILOT_SET_COUNT);
        collectedElement.remove(ALL_PILOT_SET_COUNT);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setFile(config.getFilesToLoad().get(0));
        rowContainer.setHeaders(header);
        Map<String, Object> createdMainElement = new HashMap<String, Object>();
        createdMainElement.putAll(collectedElement);
        createdMainElement.put(NewAbstractService.TYPE, DriveNodeTypes.M.getId());
        Map<String, Object> location = new HashMap<String, Object>();
        location.put(LATITUDE, collectedElement.get(LATITUDE));
        location.put(LONGITUDE, collectedElement.get(LONGITUDE));
        Set<IDataElement> locListSet = new HashSet<IDataElement>();
        locListSet.add(new DataElement(location));
        try {
            when(model.addFile(eq(rowContainer.getFile()))).thenReturn(new DataElement(new HashMap<String, Object>()));
            when(virtualModel.addFile(eq(rowContainer.getFile()))).thenReturn(new DataElement(new HashMap<String, Object>()));
            temsSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            collectedElement.put(TIMESTAMP, temsSaver.defineTimestamp(workDate, collectedElement.get(TIME).toString()));
            createdMainElement.putAll(collectedElement);
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement), any(Boolean.class))).thenReturn(
                    new DataElement(createdMainElement));
            when(virtualModel.addMeasurement(eq(rowContainer.getFile().getName()), eq(msCollected1), any(Boolean.class)))
                    .thenReturn(new DataElement(msCollected1));
            when(virtualModel.addMeasurement(eq(rowContainer.getFile().getName()), eq(msCollected2), any(Boolean.class)))
                    .thenReturn(new DataElement(msCollected2));
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement), any(Boolean.class))).thenReturn(
                    new DataElement(createdMainElement));
            when(model.getLocations(new DataElement(eq(createdMainElement)))).thenReturn(locListSet);
            temsSaver.saveElement(rowContainer);
            verify(model, atLeastOnce()).addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement),
                    any(Boolean.class));
            verify(model, atLeastOnce()).getLocations(new DataElement(eq(createdMainElement)));
        } catch (Exception e) {
            LOGGER.error(" testSavingAllElement error", e);
            Assert.fail("Exception while saving row");
        }
    }
}
