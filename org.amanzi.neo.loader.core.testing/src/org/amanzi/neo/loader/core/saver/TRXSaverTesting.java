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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.db.manager.IDatabaseManager;
import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NetworkService.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
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
public class TRXSaverTesting extends AbstractAWEDBTest {
    private static final Logger LOGGER = Logger.getLogger(TRXSaverTesting.class);
    private TRXSaver trxSaver;
    private static String PATH_TO_BASE = "";
    private NetworkConfiguration config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";

    private static final String ID_PARAM = "trx_id";
    private static final String ID_VALUE = "1";
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
    private static DataLoadPreferenceInitializer initializer;
    private static final Map<String, Object> TRX = new HashMap<String, Object>();
    private static final Map<String, Object> FREQ_MAP = new HashMap<String, Object>();
    private static final Map<String, Object> SECTOR = new HashMap<String, Object>();
    private static  NetworkModel networkModelMock;
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

        TRX.put(AbstractService.TYPE, NetworkElementNodeType.TRX.getId());
        SECTOR.put(NAME_PARAM, SECTOR_VALUE);
        SECTOR.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
    }

    private Map<String, String> hashMap = null;

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("NewNetworkSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Before
    public void onStart() throws AWEException {
        networkModelMock = mock(NetworkModel.class);
        dbManager = mock(IDatabaseManager.class);
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
        trxSaver = new TRXSaver() {
            @Override
            public void init(NetworkConfiguration configuration) throws AWEException {
                // TODO: verify
                this.networkModel = networkModelMock;
                setMainModel(networkModelMock);
            }
        };
        trxSaver.init(config);
        trxSaver.dbManager = dbManager;
        hashMap.put(ID_PARAM, ID_VALUE);
        hashMap.put(SECTOR_PARAM, SECTOR_VALUE);
        hashMap.put(SUB_SECTOR_PARAM, SUB_SECTOR_VALUE);
        hashMap.put(NAME_PARAM, NAME_VALUE);
        hashMap.put(TRX_ID_PARAM, TRX_ID_VALUE.toString());
        hashMap.put(BAND_PARAM, BAND_VALUE);
        hashMap.put(EXTENDED_PARAM, EXTENDED_PARAM);
        hashMap.put(HOPING_TYPE_PARAM, HOPING_TYPE_VALUE.toString());
        hashMap.put(ISBCCH_PARAM, ISBCCH_VALUE.toString());
        hashMap.put(HSN_PARAM, HSN_VALUE);
        hashMap.put(MAIO_PARAM, "1");
        hashMap.put(ARFCN_PARAM + " " + 1, "2");

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSavingTRXWhenTRXNotExist() {
        MappedData dataElement = new MappedData(hashMap);
        try {
            Set<IDataElement> mockResult = new HashSet<IDataElement>();
            mockResult.add(new DataElement(SECTOR));
            List<IDataElement> trxList = new LinkedList<IDataElement>();
            when(networkModelMock.findElementByPropertyValue(any(INodeType.class), any(String.class), any(Object.class))).thenReturn(mockResult);
            when(networkModelMock.getChildren(new DataElement(eq(SECTOR)))).thenReturn(trxList);
            when(networkModelMock.createElement(new DataElement(eq(SECTOR)), eq(TRX))).thenReturn(new DataElement(TRX));
            when(networkModelMock.getRelatedNodes(any(IDataElement.class), eq(NetworkRelationshipTypes.ENTRY_PLAN))).thenReturn(
                    trxList);
            trxSaver.save(dataElement);
            verify(networkModelMock, atLeastOnce()).createElement(any(IDataElement.class), any(Map.class));
            verify(networkModelMock, atLeastOnce()).createElement(any(IDataElement.class), any(Map.class),
                    eq(NetworkRelationshipTypes.ENTRY_PLAN));
        } catch (Exception e) {
            LOGGER.error(" testSavingTRXWhenTRXNotExist error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSavingTRXWhenTRXExist() {
        MappedData dataElement = new MappedData(hashMap);
        try {
            List<IDataElement> trxList = new LinkedList<IDataElement>();
            trxList.add(new DataElement(TRX)); 
            Set<IDataElement> mockResult = new HashSet<IDataElement>();
            mockResult.add(new DataElement(SECTOR));
            when(networkModelMock.findElementByPropertyValue(any(INodeType.class), any(String.class), any(Object.class))).thenReturn(mockResult);
            when(networkModelMock.getChildren(new DataElement(eq(SECTOR)))).thenReturn(trxList);
            when(networkModelMock.createElement(new DataElement(eq(SECTOR)), eq(TRX))).thenReturn(new DataElement(TRX));
            when(networkModelMock.getRelatedNodes(any(IDataElement.class), eq(NetworkRelationshipTypes.ENTRY_PLAN))).thenReturn(
                    trxList);
            trxSaver.save(dataElement);
            verify(networkModelMock, never()).createElement(new DataElement(eq(SECTOR)), eq(TRX));
            verify(networkModelMock, atLeastOnce()).completeProperties(any(IDataElement.class), any(Map.class), any(Boolean.class));
        } catch (Exception e) {
            LOGGER.error(" testSavingTRXWhenTRXExist error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @Test
    public void testSavingTRXWhenSectorIsNotExist() {
        MappedData dataElement = new MappedData(hashMap);
        try {
            List<IDataElement> trxList = new LinkedList<IDataElement>();
            when(networkModelMock.findElement(eq(SECTOR))).thenReturn(null);
            when(networkModelMock.createElement(new DataElement(eq(SECTOR)), eq(TRX))).thenReturn(new DataElement(TRX));
            when(networkModelMock.getChildren(new DataElement(eq(SECTOR)))).thenReturn(trxList);
            when(networkModelMock.getRelatedNodes(new DataElement(eq(TRX)), eq(NetworkRelationshipTypes.ENTRY_PLAN))).thenReturn(
                    trxList);
            trxSaver.save(dataElement);
            verify(networkModelMock, never()).createElement(new DataElement(eq(SECTOR)), eq(TRX));
        } catch (Exception e) {
            LOGGER.error(" testSavingTRXWhenSectorIsNotExist error", e);
            Assert.fail("Exception while saving row");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTransactionRollBackIfDatabaseExceptionThrow() {
        MappedData dataElement = new MappedData(hashMap);
        try {
            when(networkModelMock.findElement(any(Map.class))).thenThrow(new DatabaseException("required exception"));
            trxSaver.save(dataElement);
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
            trxSaver.save(dataElement);
            verify(dbManager, never()).rollbackThreadTransaction();

        } catch (Exception e) {
            Assert.fail("unexpected exceptions");
        }
    }
}