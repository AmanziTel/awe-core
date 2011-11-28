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
public class RomesSaverTesting extends AbstractAWETest {
    private static final Logger LOGGER = Logger.getLogger(RomesSaverTesting.class);
    private RomesSaver romesSaver;
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
    private static final String MESSAGE_TYPE = "message_type";
    private static final String PROJECT_NAME = "project";
    private int MINIMAL_COLUMN_SIZE = 2;
    private static DataLoadPreferenceInitializer initializer;
    private final static Map<String, Object> collectedElement = new HashMap<String, Object>();
    private static DriveModel model;
    private static Long startTime;
    private Calendar workDate = Calendar.getInstance();
    static {
        PATH_TO_BASE = System.getProperty("user.home");
        collectedElement.put(TIME, "Aug 6 12:13:14.15");
        collectedElement.put(LATITUDE, 12d);
        collectedElement.put(NewNetworkService.NAME, "Aug 6 12:13:14.15");
        collectedElement.put(LONGITUDE, 13d);
        collectedElement.put(SECTOR_ID, "a1");
        collectedElement.put(EVENT, "ev");
        collectedElement.put(MESSAGE_TYPE, "mt");
        collectedElement.put(NewAbstractService.TYPE, DriveNodeTypes.M.getId());
    }

    private HashMap<String, Object> hashMap = null;

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
        LOGGER.info("RomesSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Before
    public void onStart() throws AWEException {
        model = mock(DriveModel.class);
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
        romesSaver = new RomesSaver(model, (ConfigurationDataImpl)config);
        hashMap.put(TIME, "Aug 6 12:13:14.15");
        hashMap.put("latitude", "12");
        hashMap.put("longitude", "13");
        hashMap.put(EVENT, "ev");
        hashMap.put(SECTOR_ID, "a1");
        hashMap.put(MESSAGE_TYPE, "mt");

    }

    private List<String> prepareValues(HashMap<String, Object> map) {
        List<String> values = new LinkedList<String>();
        for (String key : map.keySet()) {
            values.add(map.get(key).toString());
        }
        return values;
    }

    @Test
    public void testCreatingNewElement() {
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
            romesSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            collectedElement.put(TIMESTAMP, romesSaver.defineTimestamp(workDate, collectedElement.get(TIME).toString()));
            createdMainElement.putAll(collectedElement);
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement), any(Boolean.class))).thenReturn(
                    new DataElement(createdMainElement));
            when(model.getLocations(new DataElement(eq(createdMainElement)))).thenReturn(locListSet);
            romesSaver.saveElement(rowContainer);

            verify(model, atLeastOnce()).addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement),
                    any(Boolean.class));
            verify(model).getLocations(new DataElement(eq(createdMainElement)));
        } catch (Exception e) {
            LOGGER.error(" testSavingAllElement error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @Test
    public void testIfThereAreNoTimeOrLatLonValues() {
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
            romesSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            collectedElement.put(TIMESTAMP, romesSaver.defineTimestamp(workDate, collectedElement.get(TIME).toString()));
            createdMainElement.putAll(collectedElement);
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement), any(Boolean.class))).thenReturn(
                    new DataElement(createdMainElement));
            when(model.getLocations(new DataElement(eq(createdMainElement)))).thenReturn(locListSet);
            romesSaver.saveElement(rowContainer);

            verify(model, never()).addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement), any(Boolean.class));
            verify(model, never()).getLocations(new DataElement(eq(createdMainElement)));
        } catch (Exception e) {
            LOGGER.error(" testSavingAllElement error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLinkNodes() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setFile(config.getFilesToLoad().get(0));
        rowContainer.setHeaders(header);
        collectedElement.put(TIMESTAMP, romesSaver.defineTimestamp(workDate, collectedElement.get(TIME).toString()));
        Map<String, Object> createdMainElement = new HashMap<String, Object>();
        createdMainElement.putAll(collectedElement);
        createdMainElement.put(NewAbstractService.TYPE, DriveNodeTypes.M.getId());
        Set<IDataElement> locListSet = new HashSet<IDataElement>();
        Map<String, Object> location = new HashMap<String, Object>();
        location.put(LATITUDE, collectedElement.get(LATITUDE));
        location.put(LONGITUDE, collectedElement.get(LONGITUDE));
        locListSet.add(new DataElement(location));
        Map<String, Object> secondElement = new HashMap<String, Object>();
        secondElement.putAll(collectedElement);
        locListSet.add(new DataElement(location));
        Map<String, Object> createdSecondElement = new HashMap<String, Object>();
        createdSecondElement.putAll(secondElement);
        createdSecondElement.put(NewAbstractService.TYPE, DriveNodeTypes.M.getId());
        try {
            when(model.addFile(eq(rowContainer.getFile()))).thenReturn(new DataElement(new HashMap<String, Object>()));
            romesSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(collectedElement), any(Boolean.class))).thenReturn(
                    new DataElement(createdMainElement));
            when(model.addMeasurement(eq(rowContainer.getFile().getName()), eq(secondElement), any(Boolean.class))).thenReturn(
                    new DataElement(createdSecondElement));
            when(model.getLocations(new DataElement(eq(createdMainElement)))).thenReturn(locListSet);
            romesSaver.saveElement(rowContainer);
            romesSaver.saveElement(rowContainer);

            verify(model, atLeastOnce())
                    .addMeasurement(eq(rowContainer.getFile().getName()), eq(secondElement), any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testSavingAllElement error", e);
            Assert.fail("Exception while saving row");
        }
    }
}
