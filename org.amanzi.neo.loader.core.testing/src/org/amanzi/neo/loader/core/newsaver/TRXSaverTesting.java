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
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NewNetworkService.NetworkRelationshipTypes;
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
 * @author Vladislav_Kondratenko
 */
public class TRXSaverTesting extends AbstractAWETest {
    private static final Logger LOGGER = Logger.getLogger(TRXSaverTesting.class);
    private TRXSaver trxSaver;
    private static String PATH_TO_BASE = "";
    private ConfigurationDataImpl config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";

    private static final String SECTOR_PARAM = "sector";
    private static final String SECTOR_VALUE = "s1";
    private static final String SUB_SECTOR_PARAM = "subcell";
    private static final String SUB_SECTOR_VALUE = "s3";
    private static final String NAME_PARAM = "name";
    private static final String NAME_VALUE = "0";
    private static final String TRX_ID_PARAM = "trx_id";
    private static final Integer TRX_ID_VALUE = 0;
    private static final String BAND_PARAM = "band";
    private static final String BAND_VALUE = "900";
    private static final String EXTENDED_PARAM = "extended";
    private static final String HOPING_TYPE_PARAM = "hopping_type";
    private static final Integer HOPING_TYPE_VALUE = 1;
    private static final String ISBCCH_PARAM = "isBcch";
    private static final Boolean ISBCCH_VALUE = false;
    private static final String HSN_PARAM = "hsn";
    private static final String HSN_VALUE = "UL";
    private static final String MAIO_PARAM = "maio";
    private static final String ARFCN_PARAM = "arfcn";
    private int MINIMAL_COLUMN_SIZE = 2;
    private static DataLoadPreferenceInitializer initializer;
    private final static Map<String, Object> TRX = new HashMap<String, Object>();
    private final static Map<String, Object> FREQ_MAP = new HashMap<String, Object>();
    private final static Map<String, Object> SECTOR = new HashMap<String, Object>();
    private static NetworkModel networkModel;
    private static Long startTime;
    private GraphDatabaseService service;
    private Transaction tx;
    static {
        PATH_TO_BASE = System.getProperty("user.home");
        TRX.put(SUB_SECTOR_PARAM, SUB_SECTOR_VALUE);
        TRX.put(NAME_PARAM, NAME_VALUE);
        TRX.put(TRX_ID_PARAM, TRX_ID_VALUE);
        TRX.put(BAND_PARAM, BAND_VALUE);
        TRX.put(EXTENDED_PARAM, EXTENDED_PARAM);
        TRX.put(HOPING_TYPE_PARAM, HOPING_TYPE_VALUE);
        TRX.put(ISBCCH_PARAM, ISBCCH_VALUE);
        FREQ_MAP.put(HSN_PARAM, HSN_VALUE);
        FREQ_MAP.put(MAIO_PARAM, 1);
        FREQ_MAP.put(ARFCN_PARAM, new Integer[63]);

        TRX.put(NewAbstractService.TYPE, NetworkElementNodeType.TRX.getId());
        SECTOR.put(NAME_PARAM, SECTOR_VALUE);
        SECTOR.put(NewAbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
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
        networkModel = mock(NetworkModel.class);
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
        trxSaver = new TRXSaver(networkModel, (ConfigurationDataImpl)config, service);
        hashMap.put(SECTOR_PARAM, SECTOR_VALUE);
        hashMap.put(SUB_SECTOR_PARAM, SUB_SECTOR_VALUE);
        hashMap.put(NAME_PARAM, NAME_VALUE);
        hashMap.put(TRX_ID_PARAM, TRX_ID_VALUE);
        hashMap.put(BAND_PARAM, BAND_VALUE);
        hashMap.put(EXTENDED_PARAM, EXTENDED_PARAM);
        hashMap.put(HOPING_TYPE_PARAM, HOPING_TYPE_VALUE);
        hashMap.put(ISBCCH_PARAM, ISBCCH_VALUE);
        hashMap.put(HSN_PARAM, HSN_VALUE);
        hashMap.put(MAIO_PARAM, 1);
        hashMap.put(ARFCN_PARAM + " " + 1, 2);

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
    public void testSavingTRXWhenTRXNotExist() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        trxSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        List<IDataElement> trxList = new LinkedList<IDataElement>();
        try {
            when(networkModel.findElement(eq(SECTOR))).thenReturn(new DataElement(SECTOR));
            when(networkModel.getChildren(new DataElement(eq(SECTOR)))).thenReturn(trxList);
            when(networkModel.createElement(new DataElement(eq(SECTOR)), eq(TRX))).thenReturn(new DataElement(TRX));
            when(networkModel.getRelatedNodes(new DataElement(eq(TRX)), eq(NetworkRelationshipTypes.ENTRY_PLAN))).thenReturn(
                    trxList);
            trxSaver.saveElement(rowContainer);
            verify(networkModel, atLeastOnce()).createElement(any(IDataElement.class), any(Map.class));
            verify(networkModel, atLeastOnce()).createElement(any(IDataElement.class), any(Map.class),
                    eq(NetworkRelationshipTypes.ENTRY_PLAN));
        } catch (Exception e) {
            LOGGER.error(" testSavingTRXWhenTRXNotExist error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSavingTRXWhenTRXExist() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        trxSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        List<IDataElement> trxList = new LinkedList<IDataElement>();
        trxList.add(new DataElement(TRX));
        try {
            when(networkModel.findElement(eq(SECTOR))).thenReturn(new DataElement(SECTOR));
            when(networkModel.getChildren(new DataElement(eq(SECTOR)))).thenReturn(trxList);
            when(networkModel.createElement(new DataElement(eq(SECTOR)), eq(TRX))).thenReturn(new DataElement(TRX));
            when(networkModel.getRelatedNodes(new DataElement(eq(TRX)), eq(NetworkRelationshipTypes.ENTRY_PLAN))).thenReturn(
                    trxList);
            trxSaver.saveElement(rowContainer);
            verify(networkModel, never()).createElement(new DataElement(eq(SECTOR)), eq(TRX));
            verify(networkModel, atLeastOnce()).completeProperties(any(IDataElement.class), any(Map.class), any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testSavingTRXWhenTRXExist error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @Test
    public void testSavingTRXWhenSectorIsNotExist() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        trxSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        List<IDataElement> trxList = new LinkedList<IDataElement>();
        try {
            when(networkModel.findElement(eq(SECTOR))).thenReturn(null);
            when(networkModel.createElement(new DataElement(eq(SECTOR)), eq(TRX))).thenReturn(new DataElement(TRX));
            when(networkModel.getChildren(new DataElement(eq(SECTOR)))).thenReturn(trxList);
            when(networkModel.getRelatedNodes(new DataElement(eq(TRX)), eq(NetworkRelationshipTypes.ENTRY_PLAN))).thenReturn(
                    trxList);
            trxSaver.saveElement(rowContainer);
            verify(networkModel, never()).createElement(new DataElement(eq(SECTOR)), eq(TRX));
        } catch (Exception e) {
            LOGGER.error(" testSavingTRXWhenSectorIsNotExist error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTransactionRollBackIfDatabaseExceptionThrow() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        trxSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        try {
            rowContainer.setValues(values);
            when(networkModel.findElement(any(Map.class))).thenThrow(new DatabaseException("required exception"));
            trxSaver.saveElement(rowContainer);
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
        trxSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        try {
            rowContainer.setValues(values);
            when(networkModel.findElement(any(Map.class))).thenThrow(new IllegalArgumentException("required exception"));
            trxSaver.saveElement(rowContainer);
        } catch (Exception e) {
            verify(tx, times(2)).success();
            verify(tx, never()).failure();
            verify(tx, times(3)).finish();
        }
    }
}