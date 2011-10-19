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
import org.amanzi.neo.loader.core.newsaver.NewNeighboursSaver;
import org.amanzi.neo.loader.core.newsaver.NewNetworkSaver;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
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
public class NewNeighbourSaverTesting extends AbstractAWETest {
    private static Logger LOGGER = Logger.getLogger(NewNetworkSaverTesting.class);
    private NewNetworkSaver networkSaver;
    private NewNeighboursSaver neighbourSaver;
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
    private final static Map<String, Object> SECTOR1 = new HashMap<String, Object>();
    private final static Map<String, Object> SECTOR2 = new HashMap<String, Object>();
    private final static Map<String, Object> MSC = new HashMap<String, Object>();
    private final static Map<String, Object> CITY = new HashMap<String, Object>();
    private INetworkModel networkModel;
    private static Long startTime;
    static {
        PATH_TO_BASE = System.getProperty("user.home");
        BSC.put("name", "bsc1");
        BSC.put("type", "bsc");
        SITE.put("name", "site1");
        SITE.put("type", "site");
        SECTOR1.put("name", "sector1");
        SECTOR1.put("type", "sector");
        SECTOR2.put("name", "sector2");
        SECTOR2.put("type", "sector");
        MSC.put("name", "msc1");
        MSC.put("type", "msc");
        CITY.put("name", "city1");
        CITY.put("type", "city");
    }
    private HashMap<String, Object> hashMap = null;

    @BeforeClass
    public static void prepare() {
        clearDb();
        initializeDb();
        initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
        new LogStarter().earlyStartup();
        NeoServiceFactory.getInstance().clear();
        startTime = System.currentTimeMillis();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("NewNeighbourSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @Before
    public void onStart() {
        networkModel = mock(NetworkModel.class);

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
        networkSaver = new NewNetworkSaver(networkModel, (ConfigurationDataImpl)config);
        hashMap.put("bsc", "bsc1");
        hashMap.put("site", "site1");
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
    public void testNeighbourNetworkSaver() {
        NodeToNodeRelationshipModel model = mock(NodeToNodeRelationshipModel.class);
        neighbourSaver = new NewNeighboursSaver(model, networkModel, (ConfigurationDataImpl)config);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);

        try {
            when(networkModel.findElement(BSC)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(BSC))).thenReturn(new DataElement(BSC));
            when(networkModel.findElement(SITE)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SITE))).thenReturn(new DataElement(SITE));
            when(networkModel.findElement(SECTOR1)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SECTOR1))).thenReturn(new DataElement(SECTOR1));
            when(networkModel.findElement(SECTOR2)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SECTOR2))).thenReturn(new DataElement(SECTOR2));
            networkSaver.saveElement(rowContainer);
            hashMap.put("sector", "sector2");
            hashMap.put("ci", "119");
            hashMap.put("lac", "331");
            values = prepareValues(hashMap);
            rowContainer.setValues(values);
            networkSaver.saveElement(rowContainer);
            hashMap.clear();
            hashMap.put("Neighbour", "sector2");
            hashMap.put("Server", "sector1");
            hashMap.put("property", "site1");
            when(networkModel.findElement(SECTOR1)).thenReturn(new DataElement(SECTOR1));
            when(networkModel.findElement(SECTOR2)).thenReturn(new DataElement(SECTOR2));
            rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
            header = new LinkedList<String>(hashMap.keySet());
            rowContainer.setHeaders(header);
            neighbourSaver.saveElement(rowContainer);
            values = prepareValues(hashMap);
            rowContainer.setValues(values);
            neighbourSaver.saveElement(rowContainer);
            verify(model).linkNode(any(IDataElement.class), any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testNeighbourNetworkSaver error", e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testNeighbourNetworkWithoutNecessaryParameters() {
        NodeToNodeRelationshipModel model = mock(NodeToNodeRelationshipModel.class);
        neighbourSaver = new NewNeighboursSaver(model, networkModel, (ConfigurationDataImpl)config);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);

        try {
            when(networkModel.findElement(BSC)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(BSC))).thenReturn(new DataElement(BSC));
            when(networkModel.findElement(SITE)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SITE))).thenReturn(new DataElement(SITE));
            when(networkModel.findElement(SECTOR1)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SECTOR1))).thenReturn(new DataElement(SECTOR1));
            when(networkModel.findElement(SECTOR2)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SECTOR2))).thenReturn(new DataElement(SECTOR2));
            networkSaver.saveElement(rowContainer);
            values = prepareValues(hashMap);
            rowContainer.setValues(values);
            networkSaver.saveElement(rowContainer);
            hashMap.clear();
            hashMap.put("Neighbour", "sector4");
            hashMap.put("property", "site1");
            rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
            header = new LinkedList<String>(hashMap.keySet());
            rowContainer.setHeaders(header);
            neighbourSaver.saveElement(rowContainer);
            values = prepareValues(hashMap);
            rowContainer.setValues(values);
            neighbourSaver.saveElement(rowContainer);
            verify(model, never()).linkNode(any(IDataElement.class), any(IDataElement.class), any(Map.class));
            Assert.fail("if one of necessary parameters is null than nullPointerException should be thrown");
        } catch (AWEException e) {
            LOGGER.error(" testNeighbourNetworkWithoutNecessaryParameters error", e);
            Assert.fail();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNeighbourNetworkWithoutExistingServer() {
        NodeToNodeRelationshipModel model = mock(NodeToNodeRelationshipModel.class);
        neighbourSaver = new NewNeighboursSaver(model, networkModel, (ConfigurationDataImpl)config);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        try {
            when(networkModel.findElement(BSC)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(BSC))).thenReturn(new DataElement(BSC));
            when(networkModel.findElement(SITE)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SITE))).thenReturn(new DataElement(SITE));
            when(networkModel.findElement(SECTOR1)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SECTOR1))).thenReturn(new DataElement(SECTOR1));
            when(networkModel.findElement(SECTOR2)).thenReturn(null);
            when(networkModel.createElement(any(IDataElement.class), eq(SECTOR2))).thenReturn(new DataElement(SECTOR2));
            networkSaver.saveElement(rowContainer);
            values = prepareValues(hashMap);
            rowContainer.setValues(values);
            networkSaver.saveElement(rowContainer);
            hashMap.clear();
            hashMap.put("Neighbour", "sector4");
            hashMap.put("Server", "sector1");
            hashMap.put("property", "site1");
            rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
            header = new LinkedList<String>(hashMap.keySet());
            rowContainer.setHeaders(header);
            neighbourSaver.saveElement(rowContainer);
            values = prepareValues(hashMap);
            rowContainer.setValues(values);
            neighbourSaver.saveElement(rowContainer);
            verify(model, never()).linkNode(any(IDataElement.class), any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            LOGGER.error(" testNeighbourNetworkWithoutExistingServer error", e);
            Assert.fail("Exception while saving row");
        }
    }
}
