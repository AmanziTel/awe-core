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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.newsaver.CNASaver;
import org.amanzi.neo.loader.core.newsaver.NewNetworkSaver;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * @author Vladilsav_Kondratenko
 */
public class CNASaverTesting {
    private INetworkModel model;
    private static CNASaver cnaSaver;
    private static NewNetworkSaver networkSaver;
    private static ProjectModel projectModel;
    private static String PATH_TO_BASE = "";
    private static IConfiguration config;
    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";
    private int MINIMAL_COLUMN_SIZE = 2;
    static {
        PATH_TO_BASE = System.getProperty("user.home") + "/database";
    }

    private static HashMap<String, Object> hashMap = null;
    private static GraphDatabaseService graphDatabaseService = null;

    @BeforeClass
    public static void onStart() {
        graphDatabaseService = new EmbeddedGraphDatabase(PATH_TO_BASE);
        NeoServiceProvider.getProvider().init(graphDatabaseService, PATH_TO_BASE, null);
        projectModel = new ProjectModel(PROJECT_NAME);

        networkSaver = new NewNetworkSaver();
        cnaSaver = new CNASaver();
        hashMap = new HashMap<String, Object>();
        config = new ConfigurationDataImpl();
        config.getDatasetNames().put(NETWORK_KEY, NETWORK_NAME);
        config.getDatasetNames().put(PROJECT_KEY, PROJECT_NAME);
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
    public void netwokPreparationTest() {
        networkSaver.init((ConfigurationDataImpl)config, null);
        model = projectModel.findNetwork(NETWORK_NAME);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        networkSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        networkSaver.saveElement(rowContainer);
        Map<String, Object> findValue = new HashMap<String, Object>();
        findValue.put("name", "sector1");
        findValue.put("ci", "120");
        findValue.put("lac", "332");
        findValue.put("type", "sector");
        try {
            networkSaver.finishUp();
            IDataElement dataElement = model.findElement(new DataElement(findValue));
            Assert.assertNotNull("Expected not null value", dataElement);
        } catch (Exception e) {
            Assert.fail("Exception while saving row");
        }
    }

    @Test
    public void cnaTestSave() {
        model = projectModel.findNetwork(NETWORK_NAME);
        hashMap.clear();
        hashMap.put("bsc", "BSC");
        hashMap.put("site", "site1");
        hashMap.put("lat", "3.123");
        hashMap.put("lon", "2.1234");
        hashMap.put("sector", "sector2");
        hashMap.put("ci", "1202");
        hashMap.put("lac", "333");
        cnaSaver.init((ConfigurationDataImpl)config, null);
        CSVContainer rowContainer = new CSVContainer(MINIMAL_COLUMN_SIZE);
        List<String> header = new LinkedList<String>(hashMap.keySet());
        rowContainer.setHeaders(header);
        cnaSaver.saveElement(rowContainer);
        List<String> values = prepareValues(hashMap);
        rowContainer.setValues(values);
        cnaSaver.saveElement(rowContainer);

        try {
            cnaSaver.finishUp();
        } catch (Exception e) {
            Assert.fail("Exception while saving row");
        }
    }
}
