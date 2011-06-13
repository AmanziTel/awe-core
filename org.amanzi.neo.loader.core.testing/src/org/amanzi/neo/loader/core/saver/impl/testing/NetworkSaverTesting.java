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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.impl.NetworkSaver;
import org.amanzi.neo.loader.ui.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.internal.DatasetStatistic;
import org.amanzi.neo.services.statistic.internal.StatisticRelationshipTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NetworkSaverTesting {
    private NetworkSaver networkSaver;
    private ArrayList<BaseTransferData> listOfBTD;
    private static String PATH_TO_BASE = "";
    static
    {
    		PATH_TO_BASE = System.getProperty("user.home") + "/database";
    }
    
    private HashMap<String, String> hashMap = null;
    private GraphDatabaseService graphDatabaseService = null;
    private String projectName = "projectName";
    private String rootName = "rootName";
    private int partOfProjectName = 0;

    @SuppressWarnings("deprecation")
    @Before
    public void onStart() {
        listOfBTD = new ArrayList<BaseTransferData>();
        networkSaver = new NetworkSaver();
        hashMap = new HashMap<String, String>();
        graphDatabaseService = new EmbeddedGraphDatabase(PATH_TO_BASE);
        NeoServiceProviderUi.initProvider(graphDatabaseService, PATH_TO_BASE);
        DatabaseManager.setDatabaseAndIndexServices(graphDatabaseService, NeoServiceProviderUi.getProvider().getIndexService());
        
        DataLoadPreferenceInitializer initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
        
        BaseTransferData data = new BaseTransferData();
        data.setProjectName(projectName + partOfProjectName++);
        data.setRootName(rootName);
        hashMap.put("bsc", "bsc1");
        hashMap.put("site", "site1");
        hashMap.put("lat", "3.123");
        hashMap.put("lon", "2.1234");
        hashMap.put("sector", "sector1");
        hashMap.put("ci", "120");
        hashMap.put("lac", "332");
        hashMap.put("beamwidth", "3");
        data.putAll(hashMap);
        
        networkSaver.init(data);
        networkSaver.save(data);
        
        for (int i = 0; i < 20; i++) {
            BaseTransferData data2 = new BaseTransferData();
            hashMap.clear();
            hashMap.put("bsc", "bsc1");
            hashMap.put("site", "site" + (int)(i / 5));
            hashMap.put("lat", (new Double((3.1 + (i / 5)))).toString());
            hashMap.put("lon", (new Double((2.2 + (i / 5)))).toString());
            hashMap.put("sector", "sector" + i);
            hashMap.put("ci", (new Integer((120 + i))).toString());
            hashMap.put("lac", (new Integer((330 + i))).toString());
            hashMap.put("beamwidth", (new Integer((i))).toString());
            data2.putAll(hashMap);
            
            networkSaver.save(data2); 
            listOfBTD.add(data2);
        }
        
        DatasetStatistic st = new DatasetStatistic(networkSaver.getRootNode());
        st.save();
        st.init();
    }
    
    @Test
    public void correctWorkOfStatisticTest() {
        Relationship relation = networkSaver.getRootNode().getSingleRelationship(StatisticRelationshipTypes.STATISTIC_PROP, Direction.OUTGOING);
        for (Relationship rel : networkSaver.getRootNode().getRelationships()) {
        	System.out.println(rel.getType());
        }
        assertTrue(relation != null);
    }
    
    @Test
    public void getRootNodeTest() {
        Node rootNode = networkSaver.getRootNode();
        assertTrue(rootNode.getId() == 1);
        assertTrue(rootNode.getProperty("name").equals(rootName));
        assertTrue(rootNode.getProperty("type").equals(NodeTypes.NETWORK.getId()));
    }
    
    @Test
    public void getPossibleHeadersTest() {
        for (BaseTransferData data : listOfBTD) {
            Set<String> set = data.keySet();
            assertTrue(set.contains("sector") == true);
            assertTrue(set.contains("site") == true);
            assertTrue(set.contains("beamwidth") == true);
            assertTrue(set.contains("lon") == true);
            assertTrue(set.contains("ci") == true);
            assertTrue(set.contains("bsc") == true);
            assertTrue(set.contains("lac") == true);
            assertTrue(set.contains("lat") == true);
        }
    }
    
    @After
    public void onFinish() {
        NeoServiceProviderUi.getProvider().getIndexService().shutdown();
        graphDatabaseService.shutdown();
        
        File file = new File(PATH_TO_BASE);
        deleteFolder(file);
    }
    
    /**
     * Delete all folder
     *
     * @param file File with path to delete
     */
    private void deleteFolder(File file)
    {
        if(!file.exists())
            return;
        if(file.isDirectory())
        {
            for(File f : file.listFiles())
                deleteFolder(f);
            file.delete();
        }
        else
        {
            file.delete();
        }
    }
    
    /**
     * method to get network saver
     *
     * @return
     */
    public NetworkSaver getNetworkSaver() {
        return networkSaver;
    }
}
