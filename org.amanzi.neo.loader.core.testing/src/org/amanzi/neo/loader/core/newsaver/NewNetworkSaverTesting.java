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
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.newsaver.NewNetworkSaver;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.testing.AbstractAWETest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Kondratenko_Vladsialv
 */
public class NewNetworkSaverTesting extends AbstractAWETest {
    private NewNetworkSaver networkSaver;
    private static String PATH_TO_BASE = "";
    private IConfiguration config;
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
    static {
        PATH_TO_BASE = System.getProperty("user.home");
        BSC.put("name", "bsc1");
        BSC.put("type", "bsc");
        SITE.put("name", "site1");
        SITE.put("type", "site");
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
        NeoServiceFactory.getInstance().clear();

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
    }

    @Before
    public void onStart() {
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
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        fileList.add(testFile);
        config.setSourceFile(fileList);
        networkSaver = new NewNetworkSaver(model, (ConfigurationDataImpl)config);
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
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);

        try {
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
            e.printStackTrace();
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
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);

        try {
            when(model.findElement(SITE)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SITE))).thenReturn(new DataElement(SITE));
            when(model.findElement(SECTOR)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SECTOR))).thenReturn(new DataElement(SECTOR));
            networkSaver.saveElement(rowContainer);
            verify(model, times(2)).createElement(any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
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
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        try {
            networkSaver.saveElement(rowContainer);
            verify(model, never()).createElement(any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
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
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        try {
            when(model.findElement(SITE)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SITE))).thenReturn(new DataElement(SITE));
            when(model.findElement(SECTOR)).thenReturn(null);
            when(model.createElement(any(IDataElement.class), eq(SECTOR))).thenReturn(new DataElement(SECTOR));

            networkSaver.saveElement(rowContainer);
            verify(model, times(2)).createElement(any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            Assert.fail("Exception while saving row");
        }
    }
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // * propName already exists as PropertyStatistics instance
    // *
    // * @throws AWEException
    // */
    // @Test
    // public void parsePositiveTest() throws AWEException {
    // LOGGER.debug("start test parsePositiveTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    //
    // for (StoreForTestingParse testObj : testingList) {
    // Class< ? > klass = testObj.getPropType();
    // NewPropertyStatistics propStat = new NewPropertyStatistics(testObj.getPropName(), klass);
    // propVault.addPropertyStatistics(propStat);
    //
    // String parseValue = testObj.getParsePropValue();
    //
    // Object actualParseValue = propVault.parse(PROPERTIES, testObj.getPropName(), parseValue);
    // Assert.assertEquals("parse value has not expected type", actualParseValue.getClass(), klass);
    // LOGGER.debug("finish test parsePositiveTest()");
    //
    // }
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // * propValue impossible convert to required type instance
    // *
    // * @throws InvalidStatisticsParameterException
    // * @throws FailedParseValueException
    // */
    // @Test
    // public void parseNegativeTest() throws InvalidStatisticsParameterException,
    // FailedParseValueException {
    // LOGGER.debug("start test parseNegativeTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // int expectedCount = 6;
    // int count = 0;
    // for (StoreForTestingParse testObj : testingList) {
    // Class< ? > klass = testObj.getPropType();
    // NewPropertyStatistics propStat = new NewPropertyStatistics(testObj.getPropName(), klass);
    // propVault.addPropertyStatistics(propStat);
    //
    // String parseValue = "failedValue";
    //
    // try {
    // propVault.parse(PROPERTIES, testObj.getPropName(), parseValue);
    // } catch (Exception e) {
    // Assert.assertEquals("when parse to " + testObj.getPropType().getCanonicalName()
    // + " type throws not expected exception", FailedParseValueException.class, e.getClass());
    // count++;
    // }
    // }
    // Assert.assertEquals("expected exception throws for not all value types", expectedCount,
    // count);
    // LOGGER.debug("finish test parseNegativeTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // * propStat has unsupported type
    // *
    // * @throws AWEException
    // */
    // @Test(expected = UnsupportedClassException.class)
    // public void parseUnsupportedClassNegativeTest() throws AWEException {
    // LOGGER.debug("start test parseUnsupportedClassNegativeTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // NewPropertyStatistics propStat = new NewPropertyStatistics(PROPERTY_NAME_TIME, Date.class);
    // propVault.addPropertyStatistics(propStat);
    // propVault.parse(PROPERTIES, PROPERTY_NAME_TIME, "10/12/2011");
    // LOGGER.debug("finish test parseUnsupportedClassNegativeTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // * propName does not exist as PropertyStatistics instance and invoke avtoParse
    // *
    // * @throws AWEException
    // */
    // @Test
    // public void parseAutoParsePositiveTest() throws AWEException {
    // LOGGER.debug("start test parseAvtoParsePositiveTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // Map<String, NewPropertyStatistics> propStatMap;
    // boolean hasPropStat;
    //
    // for (StoreForTestingParse testObj : testingList) {
    // Class< ? > klass = testObj.getAutoParsePropType();
    // String parseValue = testObj.getParsePropValue();
    //
    // Object actualParseValue = propVault.parse(PROPERTIES, testObj.getPropName(), parseValue);
    // Assert.assertEquals("parse value has not expected type", actualParseValue.getClass(), klass);
    // propStatMap = propVault.getPropertyStatisticsMap();
    // hasPropStat = propStatMap.containsKey(testObj.getPropName());
    // Assert.assertTrue("Property vault not contains expected PropStat", hasPropStat);
    // }
    // LOGGER.debug("finish test parseAvtoParsePositiveTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when vault
    // * with given type does not exists
    // *
    // * @throws AWEException
    // */
    // @Test
    // public void parseCreateVaultPositiveTest() throws AWEException {
    // LOGGER.debug("start test parseCreateVaultPositiveTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    //
    // for (StoreForTestingParse testObj : testingList) {
    // Class< ? > klass = testObj.getAutoParsePropType();
    // String parseValue = testObj.getParsePropValue();
    //
    // Object actualParseValue = propVault.parse(NETWORK, testObj.getPropName(), parseValue);
    // Assert.assertEquals("parse value has not expected type", actualParseValue.getClass(), klass);
    // }
    //
    // boolean hasNetworkSubVault = propVault.getSubVaults().containsKey(NETWORK);
    // Assert.assertTrue("Network vault must be subVault PropVault", hasNetworkSubVault);
    // Map<String, NewPropertyStatistics> propStatMap =
    // propVault.getSubVaults().get(NETWORK).getPropertyStatisticsMap();
    //
    // boolean hasPropStat;
    // for (StoreForTestingParse testObj : testingList) {
    // hasPropStat = propStatMap.containsKey(testObj.getPropName());
    // Assert.assertTrue("Network vault not contains expected PropStat", hasPropStat);
    // }
    // LOGGER.debug("finish test parseCreateVaultPositiveTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) for
    // * avtoParse to Long value
    // *
    // * @throws AWEException
    // */
    // @Test
    // public void parseAutoParseLongValuePositiveTest() throws AWEException {
    // LOGGER.debug("start test parseAvtoParseLongValuePositiveTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // int maxInt = Integer.MAX_VALUE;
    // Object actualParseValue = propVault.parse(NETWORK, LONG_VALUE, String.valueOf((long)maxInt +
    // 1));
    // Assert.assertTrue("parse value has not expected type",
    // actualParseValue.getClass().equals(Long.class));
    // LOGGER.debug("finish test parseAvtoParseLongValuePositiveTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) for
    // * avtoParse to Double value
    // *
    // * @throws AWEException
    // */
    // @Test
    // public void parseAutoParseDoubleValuePositiveTest() throws AWEException {
    // LOGGER.debug("start test parseAvtoParseDoubleValuePositiveTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    //
    // Object actualParseValue = propVault.parse(NETWORK, DOUBLE_VALUE, "1.12345678e-13");
    // Assert.assertEquals("parse value has not expected type", actualParseValue.getClass(),
    // Double.class);
    // LOGGER.debug("finish test parseAvtoParseDoubleValuePositiveTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // string
    // * property value is empty
    // *
    // * @throws AWEException
    // */
    // @Test
    // public void parseEmptyStrValuePositiveTest() throws AWEException {
    // LOGGER.debug("start test parseEmptyStrValuePositiveTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // Object parseValue = propVault.parse(PROPERTIES, STRING_VALUE, EMPTY_STRING);
    // Assert.assertNull("value must be null", parseValue);
    // LOGGER.debug("finish test parseEmptyStrValuePositiveTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // string
    // * property value is null
    // *
    // * @throws AWEException
    // */
    // @Test
    // public void parseNullStrValuePositiveTest() throws AWEException {
    // LOGGER.debug("start test parseNullStrValuePositiveTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // Object actualValue = propVault.parse(PROPERTIES, STRING_VALUE, null);
    // Assert.assertNull("value must be null", actualValue);
    // LOGGER.debug("finish test parseNullStrValuePositiveTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // * parameter nodeType = null
    // *
    // * @throws AWEException
    // */
    // @Test(expected = InvalidStatisticsParameterException.class)
    // public void parseNullNodeTypeNegativeTest() throws AWEException {
    // LOGGER.debug("start test parseNullNodeTypeNegativeTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // propVault.parse(null, STRING_VALUE, PROPERTY_NAME_NAME_1);
    // LOGGER.debug("finish test parseNullNodeTypeNegativeTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // * parameter propertyName = null
    // *
    // * @throws AWEException
    // */
    // @Test(expected = InvalidStatisticsParameterException.class)
    // public void parseNullPropNameNegativeTest() throws AWEException {
    // LOGGER.debug("start test parseNullPropNameNegativeTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // propVault.parse(PROPERTIES, null, PROPERTY_NAME_NAME_1);
    // LOGGER.debug("finish test parseNullPropNameNegativeTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // * parameter nodeType is empty
    // *
    // * @throws AWEException
    // */
    // @Test(expected = InvalidStatisticsParameterException.class)
    // public void parseEmptyNodeTypeNegativeTest() throws AWEException {
    // LOGGER.debug("start test parseEmptyNodeTypeNegativeTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // propVault.parse(EMPTY_STRING, STRING_VALUE, PROPERTY_NAME_NAME_1);
    // LOGGER.debug("finish test parseEmptyNodeTypeNegativeTest()");
    //
    // }
    //
    // /**
    // * testing method parse(String nodeType, String propertyName, String propertyValue) when
    // * parameter propertyName is empty
    // *
    // * @throws AWEException
    // */
    // @Test(expected = InvalidStatisticsParameterException.class)
    // public void parseEmptyPropNameNegativeTest() throws AWEException {
    // LOGGER.debug("start test parseEmptyPropNameNegativeTest()");
    // StatisticsVault propVault = new StatisticsVault(PROPERTIES);
    // propVault.parse(PROPERTIES, EMPTY_STRING, PROPERTY_NAME_NAME_1);
    // LOGGER.debug("finish test parseEmptyPropNameNegativeTest()");
    //
    // }
}
