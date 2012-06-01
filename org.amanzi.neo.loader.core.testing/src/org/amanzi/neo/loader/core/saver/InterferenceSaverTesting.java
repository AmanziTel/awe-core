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
import org.amanzi.neo.loader.core.config.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel;
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
public class InterferenceSaverTesting extends AbstractAWETest {
    private static final Logger LOGGER = Logger.getLogger(InterferenceSaverTesting.class);
    private InterferenceMatrixSaver interferenceSaver;
    private static String PATH_TO_BASE = "";
    private ISingleFileConfiguration config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";
    private int MINIMAL_COLUMN_SIZE = 2;
    private static DataLoadPreferenceInitializer initializer;
    private final static Map<String, Object> SECTOR1 = new HashMap<String, Object>();
    private final static Map<String, Object> SECTOR2 = new HashMap<String, Object>();
    private final static Map<String, Object> properties = new HashMap<String, Object>();
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
        dbManager = mock(IDatabaseManager.class);
    }

    static {
        PATH_TO_BASE = System.getProperty("user.home");
        SECTOR1.put("name", "sector1");
        SECTOR1.put("type", "sector");
        SECTOR2.put("name", "sector2");
        SECTOR2.put("type", "sector");
    }
    private HashMap<String, String> hashMap = null;

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("NewNeighbourSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Before
    public void onStart() {
        networkModel = mock(NetworkModel.class);
        node2model = mock(NodeToNodeRelationshipModel.class);
        hashMap = new HashMap<String, String>();
        config = new NetworkConfiguration();
        File testFile = new File(PATH_TO_BASE + "/testFile.txt");
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            LOGGER.error(" onStart error while trying to create file", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        config.setFile(testFile);
        interferenceSaver = new InterferenceMatrixSaver() {
            @Override
            public void init(ISingleFileConfiguration configuration) throws AWEException {
                setMainModel(node2model);
                this.networkModel = InterferenceSaverTesting.this.networkModel;
            }
        };
        interferenceSaver.dbManager = dbManager;
        hashMap.put("Serving Sector ", "bsc1");
        hashMap.put("Interfering Sector", "site1");
        hashMap.put("co", "3.123");
        hashMap.put("adj", "2.1234");
        hashMap.put("source", "sector1");

        properties.put("co", "3.123");
        properties.put("adj", "2.1234");
        properties.put("source", "sector1");

    }

    private List<String> prepareValues(HashMap<String, Object> map) {
        List<String> values = new LinkedList<String>();
        for (String key : map.keySet()) {
            values.add(map.get(key).toString());
        }
        return values;
    }

    @Test
    public void testLinkSectors() {
        MappedData dataElement = new MappedData(hashMap);
        try {
            when(networkModel.findElement(SECTOR1)).thenReturn(new DataElement(SECTOR1));
            when(networkModel.findElement(SECTOR2)).thenReturn(new DataElement(SECTOR2));
            interferenceSaver.saveElement(dataElement);
            verify(node2model).linkNode(new DataElement(SECTOR1), new DataElement(SECTOR2), eq(properties));
        } catch (Exception e) {
            LOGGER.error(" testNeighbourNetworkSaver error", e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIfOneSectorNotFound() {
        MappedData dataElement = new MappedData(hashMap);
        try {
            when(networkModel.findElement(SECTOR1)).thenReturn(null);
            when(networkModel.findElement(SECTOR2)).thenReturn(new DataElement(SECTOR2));
            interferenceSaver.saveElement(dataElement);
            verify(node2model, never()).linkNode(any(IDataElement.class), any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testNeighbourNetworkSaver error", e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIfThereIsNoEnoughtProperties() {
        hashMap.remove("Serving Sector");
        MappedData dataElement = new MappedData(hashMap);
        try {
            when(networkModel.findElement(SECTOR1)).thenReturn(null);
            when(networkModel.findElement(SECTOR2)).thenReturn(new DataElement(SECTOR2));
            interferenceSaver.saveElement(dataElement);
            verify(node2model, never()).linkNode(any(IDataElement.class), any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testNeighbourNetworkSaver error", e);
            e.printStackTrace();
        }
    }

    @Test
    public void testTransactionRollBackIfDatabaseExceptionThrow() {
        MappedData dataElement = new MappedData(hashMap);
        try {
            when(networkModel.findElement(any(Map.class))).thenThrow(new DatabaseException("required exception"));
            interferenceSaver.saveElement(dataElement);
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
            when(networkModel.findElement(any(Map.class))).thenThrow(new IllegalArgumentException("required exception"));
            interferenceSaver.saveElement(dataElement);
            verify(dbManager, never()).rollbackThreadTransaction();

        } catch (Exception e) {
            Assert.fail("unexpected exceptions");
        }
    }
}
