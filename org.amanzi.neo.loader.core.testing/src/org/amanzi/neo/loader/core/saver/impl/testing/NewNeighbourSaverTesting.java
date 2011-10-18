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

package org.amanzi.neo.loader.core.saver.impl.testing;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.newsaver.NewNeighboursSaver;
import org.amanzi.neo.loader.core.newsaver.NewNetworkSaver;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel;
import org.amanzi.testing.AbstractAWETest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Vladislav_Kondratenko
 */
public class NewNeighbourSaverTesting extends AbstractAWETest {
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
    static {
        PATH_TO_BASE = System.getProperty("user.home");
    }

    private HashMap<String, Object> hashMap = null;

    @BeforeClass
    public static void prepare() {
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
        networkSaver = new NewNetworkSaver();
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
        networkSaver.init((ConfigurationDataImpl)config, null);
        NodeToNodeRelationshipModel model;
        model = mock(NodeToNodeRelationshipModel.class);
        neighbourSaver = new NewNeighboursSaver(model, (ConfigurationDataImpl)config);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        networkSaver.saveElement(rowContainer);
        try {
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
            rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
            header = new LinkedList<String>(hashMap.keySet());
            rowContainer.setHeaders(header);
            neighbourSaver.saveElement(rowContainer);
            values = prepareValues(hashMap);
            rowContainer.setValues(values);
            neighbourSaver.saveElement(rowContainer);

            verify(model).linkNode(any(IDataElement.class), any(IDataElement.class), any(Map.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNeighbourNetworkWithoutNecessaryParameters() {
        networkSaver.init((ConfigurationDataImpl)config, null);
        NodeToNodeRelationshipModel model;
        model = mock(NodeToNodeRelationshipModel.class);
        neighbourSaver = new NewNeighboursSaver(model, (ConfigurationDataImpl)config);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        networkSaver.saveElement(rowContainer);
        try {
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
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        } catch (AWEException e) {
            Assert.fail();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNeighbourNetworkWithoutExistingServer() {
        networkSaver.init((ConfigurationDataImpl)config, null);
        NodeToNodeRelationshipModel model;
        model = mock(NodeToNodeRelationshipModel.class);
        neighbourSaver = new NewNeighboursSaver(model, (ConfigurationDataImpl)config);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        networkSaver.saveElement(rowContainer);
        try {
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
            Assert.fail("Exception while saving row");
        }
    }
}
