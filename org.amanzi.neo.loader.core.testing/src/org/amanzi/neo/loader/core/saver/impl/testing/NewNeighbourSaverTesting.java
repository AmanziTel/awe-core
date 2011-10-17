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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.newsaver.NewNeighboursSaver;
import org.amanzi.neo.loader.core.newsaver.NewNetworkSaver;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * @author Vladislav_Kondratenko
 */
public class NewNeighbourSaverTesting {
    private NewNetworkSaver networkSaver;
    private NewNeighboursSaver neighbourSaver;
    private static String PATH_TO_BASE = "";
    private IConfiguration config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";
    private int MINIMAL_COLUMN_SIZE = 2;
    private DataLoadPreferenceInitializer initializer;
    static {
        PATH_TO_BASE = System.getProperty("user.home");
    }

    private HashMap<String, Object> hashMap = null;
    private GraphDatabaseService graphDatabaseService = null;

    @Before
    public void onStart() {
        graphDatabaseService = new EmbeddedGraphDatabase(PATH_TO_BASE);
        NeoServiceProvider.getProvider().init(graphDatabaseService, PATH_TO_BASE, null);
        networkSaver = new NewNetworkSaver();
        neighbourSaver = new NewNeighboursSaver();
        hashMap = new HashMap<String, Object>();
        initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();

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

    @Test
    public void testNeighbourNetworkSaver() {
        networkSaver.init((ConfigurationDataImpl)config, null);
        neighbourSaver.init((ConfigurationDataImpl)config, null);
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
        } catch (Exception e) {
            Assert.fail("Exception while saving row");
        }
    }
}
