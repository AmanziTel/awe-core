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
import static org.mockito.Mockito.times;
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
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.parser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.NodeToNodeTypes;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Vladislav_Kondrateno
 */
public class FreuencyConstraintTesting extends AbstractAWETest {
    private static final Logger LOGGER = Logger.getLogger(FreuencyConstraintTesting.class);
    private FrequencyConstraintSaver frequSaver;
    private static String PATH_TO_BASE = "";
    private ConfigurationDataImpl config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";
    private int MINIMAL_COLUMN_SIZE = 2;
    private static DataLoadPreferenceInitializer initializer;
    private static final String SECTOR = "sector";
    private static final String TRX_ID = "trx_id";
    private static final String CHANNEL_TYPE = "channel_type";
    private static final String FREQUENCY = "frequency";
    private static final String SCALLING_FACTOR = "scalling_factor";
    private static final String PENALTY = "penalty";
    private final static Map<String, Object> SECTOR1 = new HashMap<String, Object>();
    private final static Map<String, Object> FREQUENCY_NODE = new HashMap<String, Object>();
    private final static Map<String, Object> properties = new HashMap<String, Object>();
    private final static Integer FREQUENCY_VALUE = 12;
    private INetworkModel networkModel;
    private INodeToNodeRelationsModel node2model;
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
        SECTOR1.put(AbstractService.NAME, "sector1");
        SECTOR1.put(AbstractService.TYPE, "sector");
        FREQUENCY_NODE.put(AbstractService.NAME, String.valueOf(FREQUENCY_VALUE));
        FREQUENCY_NODE.put(FREQUENCY, FREQUENCY_VALUE);
        FREQUENCY_NODE.put(AbstractService.TYPE, NodeToNodeTypes.FREQUENCY.getId());
    }
    private HashMap<String, Object> hashMap = null;

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("NewNeighbourSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Before
    public void onStart() {
        dbManager = mock(IDatabaseManager.class);
        networkModel = mock(NetworkModel.class);
        node2model = mock(NodeToNodeRelationshipModel.class);
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
        frequSaver = new FrequencyConstraintSaver(node2model, networkModel, config);
        frequSaver.dbManager = dbManager;
        hashMap.put(SECTOR, "sector1");
        hashMap.put(CHANNEL_TYPE, "type");
        hashMap.put(PENALTY, "12.2");
        hashMap.put(FREQUENCY, "12");
        hashMap.put(TRX_ID, "1");
        hashMap.put(SCALLING_FACTOR, "type");

        properties.put(CHANNEL_TYPE, "type");
        properties.put(PENALTY, 12.2f);
        properties.put(SCALLING_FACTOR, "type");
    }

    private List<String> prepareValues(HashMap<String, Object> map) {
        List<String> values = new LinkedList<String>();
        for (String key : map.keySet()) {
            values.add(map.get(key).toString());
        }
        return values;
    }

    @Test
    public void testCreateFrequencyForSingleTRX() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            frequSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            List<IDataElement> findedTRX = new LinkedList<IDataElement>();
            Map<String, Object> findedTrx = new HashMap<String, Object>();
            findedTrx.put("name", "1");
            findedTrx.put("trx_id", 1);
            findedTRX.add(new DataElement(findedTrx));
            Set<IDataElement> findedSectors = new HashSet<IDataElement>();
            findedSectors.add(new DataElement(SECTOR1));
            when(
                    networkModel.findElementByPropertyValue(eq(NetworkElementNodeType.SECTOR), eq(AbstractService.NAME),
                            eq(SECTOR1.get(AbstractService.NAME)))).thenReturn(findedSectors);
            when(networkModel.getChildren(new DataElement(eq(SECTOR1)))).thenReturn(findedTRX);
            when(node2model.getFrequencyElement(eq(FREQUENCY_VALUE))).thenReturn(new DataElement(FREQUENCY_NODE));
            frequSaver.saveElement(rowContainer);
            verify(node2model).linkNode(eq(findedTRX.get(0)), new DataElement(eq(FREQUENCY_NODE)), eq(properties));
        } catch (Exception e) {
            LOGGER.error(" testNeighbourNetworkSaver error", e);
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateFrequencyForAllTRX() {
        hashMap.put(TRX_ID, "*");
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            frequSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            List<IDataElement> findedTRX = new LinkedList<IDataElement>();
            Map<String, Object> findedTrx = new HashMap<String, Object>();
            findedTrx.put("name", "1");
            findedTrx.put("trx_id", 1);
            findedTRX.add(new DataElement(findedTrx));
            findedTrx = new HashMap<String, Object>();
            findedTrx.put("name", "2");
            findedTrx.put("trx_id", 2);
            findedTRX.add(new DataElement(findedTrx));
            Set<IDataElement> findedSectors = new HashSet<IDataElement>();
            findedSectors.add(new DataElement(SECTOR1));
            when(
                    networkModel.findElementByPropertyValue(eq(NetworkElementNodeType.SECTOR), eq(AbstractService.NAME),
                            eq(SECTOR1.get(AbstractService.NAME)))).thenReturn(findedSectors);
            when(networkModel.getChildren(new DataElement(eq(SECTOR1)))).thenReturn(findedTRX);
            when(node2model.getFrequencyElement(eq(FREQUENCY_VALUE))).thenReturn(new DataElement(FREQUENCY_NODE));
            frequSaver.saveElement(rowContainer);
            verify(node2model, times(2)).linkNode(any(IDataElement.class), new DataElement(eq(FREQUENCY_NODE)), eq(properties));
        } catch (Exception e) {
            LOGGER.error(" testNeighbourNetworkSaver error", e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateFrequencyIfSectorNotFound() {
        hashMap.put(TRX_ID, "*");
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            frequSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            List<IDataElement> findedTRX = new LinkedList<IDataElement>();
            Map<String, Object> findedTrx = new HashMap<String, Object>();
            findedTrx.put("name", "1");
            findedTrx.put("trx_id", 1);
            findedTRX.add(new DataElement(findedTrx));
            findedTrx = new HashMap<String, Object>();
            findedTrx.put("name", "2");
            findedTrx.put("trx_id", 2);
            findedTRX.add(new DataElement(findedTrx));
            Set<IDataElement> findedSectors = new HashSet<IDataElement>();
            findedSectors.add(new DataElement(SECTOR1));
            when(
                    networkModel.findElementByPropertyValue(eq(NetworkElementNodeType.SECTOR), eq(AbstractService.NAME),
                            eq(SECTOR1.get(AbstractService.NAME)))).thenReturn(null);
            when(networkModel.getChildren(new DataElement(eq(SECTOR1)))).thenReturn(findedTRX);
            when(node2model.getFrequencyElement(eq(FREQUENCY_VALUE))).thenReturn(new DataElement(FREQUENCY_NODE));
            frequSaver.saveElement(rowContainer);
            verify(node2model, never()).linkNode(any(IDataElement.class), any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testNeighbourNetworkSaver error", e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTransactionRollBackIfDatabaseExceptionThrow() {
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        try {
            frequSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);

            rowContainer.setValues(values);
            when(networkModel.findElement(any(Map.class))).thenThrow(new DatabaseException("required exception"));
            frequSaver.saveElement(rowContainer);
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
            frequSaver.saveElement(rowContainer);
            List<String> values = prepareValues(hashMap);
            rowContainer.setValues(values);
            when(networkModel.findElement(any(Map.class))).thenThrow(new IllegalArgumentException("required exception"));
            frequSaver.saveElement(rowContainer);
            verify(dbManager, never()).rollbackThreadTransaction();

        } catch (Exception e) {
            Assert.fail("unexpected exceptions");
        }
    }
}
