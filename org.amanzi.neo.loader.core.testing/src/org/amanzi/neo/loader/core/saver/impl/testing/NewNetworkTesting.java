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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.newsaver.NewNetworkSaver;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * @author Kondratenko_Vladsialv
 */
public class NewNetworkTesting {
    NewNetworkSaver saver;
    private static String PATH_TO_BASE = "";
    private IConfiguration config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";
    private int MINIMAL_COLUMN_SIZE = 2;
    private DataLoadPreferenceInitializer initializer;
    static {
        PATH_TO_BASE = System.getProperty("user.home") + "/database";
    }

    private HashMap<String, Object> hashMap = null;
    private GraphDatabaseService graphDatabaseService = null;

    @SuppressWarnings("deprecation")
    @Before
    public void onStart() {
        saver = new NewNetworkSaver();
        hashMap = new HashMap<String, Object>();
        graphDatabaseService = new EmbeddedGraphDatabase(PATH_TO_BASE);
        NeoServiceProviderUi.initProvider(graphDatabaseService, PATH_TO_BASE);
        DatabaseManager.setDatabaseAndIndexServices(graphDatabaseService, NeoServiceProviderUi.getProvider().getIndexService());

        initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();

        config = new ConfigurationDataImpl();
        config.getDatasetNames().put(NETWORK_KEY, NETWORK_NAME);
        config.getDatasetNames().put(PROJECT_KEY, PROJECT_NAME);
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
    public void testSaver() {
        saver.init((ConfigurationDataImpl)config, null);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        saver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        saver.saveElement(rowContainer);
        try {
            saver.saveElement(rowContainer);
        } catch (Exception e) {
            Assert.fail("Exception while saving row");
        }
    }
}
