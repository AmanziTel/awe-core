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

package org.amanzi.awe.afp.testing.model;

import java.util.HashMap;
import java.util.Iterator;

import org.amanzi.awe.afp.models.AfpModelNew;
import org.amanzi.awe.afp.models.FrequencyDomain;
import org.amanzi.awe.afp.models.parameters.ChannelType;
import org.amanzi.awe.afp.models.parameters.FrequencyBand;
import org.amanzi.awe.afp.services.AfpService;
import org.amanzi.awe.afp.testing.engine.AbstractAfpTest;
import org.amanzi.awe.afp.testing.engine.internal.IDataset;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class AfpModelTest extends AbstractAfpTest {

    private static Logger LOGGER = Logger.getLogger(AfpModelTest.class);

    private static final int SCENARIO_NUMBER = 5;
    
    private static final String SCENARIO_PREFIX = "AfpScenario";

    private static long startTimestamp;

    private static DatasetService datasetService;

    private static AfpService afpService;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        startTimestamp = System.currentTimeMillis();
        LOGGER.info("Set up AFP Engine Test");

        try {
            initEnvironment();
            loadDataset();

            datasetService = NeoServiceFactory.getInstance().getDatasetService();
            afpService = AfpService.getService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();

        long duration = System.currentTimeMillis() - startTimestamp;
        int milliseconds = (int)(duration % 1000);
        int seconds = (int)(duration / 1000 % 60);
        int minutes = (int)(duration / 1000 / 60 % 60);
        int hours = (int)(duration / 1000 / 60 / 60 % 24);
        LOGGER.info("Test finished. Test time - " + hours + " hours " + minutes + " minutes " + seconds + " seconds "
                + milliseconds + " milliseconds");
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        Transaction tx = graphDatabaseService.beginTx();
        try {
            for (IDataset singleDataset : datasets) {
                Node rootNode = singleDataset.getRootNode();

                for (Node afpNode : afpService.getAllAfpNodes(rootNode)) {
                    afpNode.getSingleRelationship(DatasetRelationshipTypes.CHILD, Direction.INCOMING).delete();
                }
            }

            tx.success();
        } finally {
            tx.finish();
        }
    }

    @Test
    public void createModelTest() {
        LOGGER.info("Test to check correct creation of AFP Node");

        for (IDataset dataset : datasets) {
            long before = System.currentTimeMillis();
            LOGGER.info("Test on <" + dataset.getName() + "> dataset");

            // create an AFP model
            NetworkModel network = new NetworkModel(dataset.getRootNode());
            AfpModelNew model = new AfpModelNew(SCENARIO_PREFIX + "_" + dataset.getName(), network);

            // check manually that node was created
            Iterator<Node> afpNodes = getAfpNodesTraverser(dataset.getRootNode());

            Assert.assertTrue("Created AFP Node was not found", afpNodes.hasNext());

            Node afpNode = afpNodes.next();

            Assert.assertFalse("Duplicated AFP Node", afpNodes.hasNext());

            String scenarioName = datasetService.getNodeName(afpNode);

            Assert.assertEquals("Incorrect name of AFP Node", SCENARIO_PREFIX + "_" + dataset.getName(), scenarioName);
            Assert.assertEquals("Incorrect name of AFP Model", SCENARIO_PREFIX + "_" + dataset.getName(), model.getName());

            LOGGER.info("Test on <" + dataset.getName() + "> dataset finished in " + (System.currentTimeMillis() - before)
                    + " milliseconds");
        }
    }

    @Test
    public void twoAfpScenariosTest() {
        LOGGER.info("Test to check correct creation of multiple AFP Scenarios");

        for (IDataset dataset : datasets) {
            long before = System.currentTimeMillis();
            LOGGER.info("Test on <" + dataset.getName() + "> dataset");

            // create an AFP Scenario 1
            NetworkModel network = new NetworkModel(dataset.getRootNode());
            HashMap<String, AfpModelNew> afpModels = new HashMap<String, AfpModelNew>();

            for (int i = 0; i < SCENARIO_NUMBER; i++) {
                afpModels.put(SCENARIO_PREFIX + i, new AfpModelNew(SCENARIO_PREFIX + i, network));
            }

            Iterator<Node> scenariosIterator = getAfpNodesTraverser(dataset.getRootNode());

            for (int i = 0; i < SCENARIO_NUMBER; i++) {
                Assert.assertTrue("No AFP Scenario number " + i, scenariosIterator.hasNext());
                scenariosIterator.next();
            }

            LOGGER.info("Test on <" + dataset.getName() + "> dataset finished in " + (System.currentTimeMillis() - before)
                    + " milliseconds");
        }
    }

    private Iterator<Node> getAfpNodesTraverser(Node networkNode) {
        return afpService.getAllAfpNodes(networkNode).iterator();
    }

    @Test
    public void checkCountTest() {
        LOGGER.info("Test to check correct creation of multiple AFP Scenarios");

        for (IDataset dataset : datasets) {
            long before = System.currentTimeMillis();
            LOGGER.info("Test on <" + dataset.getName() + "> dataset");
            
            NetworkModel network = new NetworkModel(dataset.getRootNode());
            AfpModelNew afpModel = new AfpModelNew(SCENARIO_PREFIX, network);
            
            long beforeCount = System.currentTimeMillis();
            afpModel.countFreeDomains();
            LOGGER.info("Count Free Domains took <" + (System.currentTimeMillis() - beforeCount) + "> milliseconds");
            
            LOGGER.info("Checking Site count");
            Assert.assertEquals("Incorrect calculation of Site count", getSiteCount(network), afpModel.getSiteCount());
            LOGGER.info("Checking Sector count");
            Assert.assertEquals("Incorrect calculation of Sector count", getSectorCount(network), afpModel.getSectorCount());
            
            for (FrequencyDomain freeDomain : afpModel.getFreeFrequencyDomains().values()) {
                FrequencyBand band = FrequencyBand.findByText(freeDomain.getName());
                
                LOGGER.info("Checking Sector count for Band <" + band.getText() + ">");
                Assert.assertEquals("Incorrect calculation of Sector count for FrequencyBand <" + band.getText() + ">", getSectorCount(network, band), freeDomain.getSectorCount());
                LOGGER.info("Checking TRX count for Band <" + band.getText() + ">");
                Assert.assertEquals("Incorrect calculation of TRX count for FrequencyBand <" + band.getText() + ">", getTrxCount(network, band), freeDomain.getTrxCount());
                
                for (ChannelType channelType : ChannelType.values()) {
                    LOGGER.info("Checking TRX count for Band <" + band.getText() + "> for Channel <" + channelType.getText() + ">");
                    
                    Assert.assertEquals("Incorrect calculation of TRX count for FrequencyBand <" + band.getText() + "> with type " + channelType.getText(), 
                                        getTrxCount(network, band, channelType), freeDomain.getChannelCount().get(channelType));
                }
            }
            
            for (ChannelType channelType : ChannelType.values()) {
                LOGGER.info("Checking TRX count for <" + channelType.getText() + "> type");
                Assert.assertEquals("Incorrect calculation of TRX count for Channel <" + channelType.getText() + ">", getTrxCount(network, channelType), afpModel.getChannelCount().get(channelType));
            }
            
            LOGGER.info("Test on <" + dataset.getName() + "> dataset finished in " + (System.currentTimeMillis() - before)
                    + " milliseconds");
        }
    }
    
    private int getSiteCount(NetworkModel network) {
        int result = 0;
        Iterator<Node> siteIterator = network.getAllElementsByType(null, NodeTypes.SITE).iterator();
        while (siteIterator.hasNext()) {
            siteIterator.next();
            result++;
        }
        
        return result;
    }
    
    private int getSectorCount(NetworkModel network) {
        int result = 0;
        Iterator<Node> sectorIterator = network.getAllElementsByType(null, NodeTypes.SECTOR).iterator();
        while (sectorIterator.hasNext()) {
            sectorIterator.next();
            result++;
        }
        
        return result;
    }
    
    private int getSectorCount(NetworkModel network, FrequencyBand frequencyBand) {
        int result = 0;
        for (Node sector : network.getAllElementsByType(null, NodeTypes.SECTOR)) {
            String band = (String)sector.getProperty(INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, "");
            String layer = (String)sector.getProperty(INeoConstants.AFP_PROPERTY_LAYER_NAME, "");
            
            if (band.matches(frequencyBand.getRegExp()) ||
                layer.matches(frequencyBand.getRegExp())) {
                result++;
            }
        }
        
        return result;
    }
    
    private int getTrxCount(NetworkModel network, FrequencyBand frequencyBand) {
        int result = 0;
        for (Node trx : network.getAllElementsByType(null, NodeTypes.TRX)) {
            String band = (String)trx.getProperty(INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, "");
            
            if (band.matches(frequencyBand.getRegExp())) {
                result++;
            }
        }
        
        return result;
    }
    
    private int getTrxCount(NetworkModel network, ChannelType channelType) {
        int result = 0;
        for (Node trx : network.getAllElementsByType(null, NodeTypes.TRX)) {
            String band = (String)trx.getProperty(INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, "");
            
            boolean toContinue = true;
            for (FrequencyBand frequencyBand : FrequencyBand.values()) {
                if (band.matches(frequencyBand.getRegExp())) {
                    toContinue = false;
                }
            }
            if (toContinue) {
                continue;
            }
            
            boolean isBcch = (Boolean)trx.getProperty(INeoConstants.PROPERTY_BCCH_NAME, false);
            int hoppingType = (Integer)trx.getProperty(INeoConstants.PROPERTY_HOPPING_TYPE_NAME, -1);
            
            switch (channelType) {
            case BCCH:
                if (isBcch) {
                    result++;
                }
                break;
            case SY:
                if (!isBcch && (hoppingType == 1)) {
                    result++;
                }
                break;
            case TCH:
                if (!isBcch && (hoppingType == 0)) {
                    result++;
                }
                break;
            }
        }
        
        return result;
    }
    
    private int getTrxCount(NetworkModel network, FrequencyBand frequencyBand, ChannelType channelType) {
        int result = 0;
        for (Node trx : network.getAllElementsByType(null, NodeTypes.TRX)) {
            String band = (String)trx.getProperty(INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, "");
            
            if (!band.matches(frequencyBand.getRegExp())) {
                continue;
            }
            
            boolean isBcch = (Boolean)trx.getProperty(INeoConstants.PROPERTY_BCCH_NAME, false);
            int hoppingType = (Integer)trx.getProperty(INeoConstants.PROPERTY_HOPPING_TYPE_NAME, -1);
            
            switch (channelType) {
            case BCCH:
                if (isBcch) {
                    result++;
                }
                break;
            case SY:
                if (!isBcch && (hoppingType == 1)) {
                    result++;
                }
                break;
            case TCH:
                if (!isBcch && (hoppingType == 0)) {
                    result++;
                }
                break;
            }
        }
        
        return result;
    }
    
}
