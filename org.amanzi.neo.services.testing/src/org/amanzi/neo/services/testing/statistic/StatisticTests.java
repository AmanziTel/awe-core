package org.amanzi.neo.services.testing.statistic;


import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;

import org.amanzi.neo.services.statistic.internal.DatasetStatistic;


import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import org.junit.Test;

import org.neo4j.graphdb.Node;

import org.neo4j.graphdb.Transaction;

public class StatisticTests extends AbstractAWETest{
    private static Logger LOGGER = Logger.getLogger(StatisticTests.class);

    private static long startTimestamp;

    private static DatasetService datasetService;
    private static String ROOT_KEY = "root key";
    private static String NODE_TYPE_1 = "node type 1";
    private static String NODE_TYPE_2 = "node type 2";
    private static String PROPERTY_NAME_1 = "property name 1";
    private static String PROPERTY_NAME_2 = "property name 2";
    private static String PROPERTY_VALUE_1 = "property value 1";
    private static String PROPERTY_VALUE_2 = "property value 2";

  
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        startTimestamp = System.currentTimeMillis();
        LOGGER.info("Set up Statistic Test");

        try {
            initializeDb();
            initPreferences();
            
            datasetService = NeoServiceFactory.getInstance().getDatasetService();
            //afpService = AfpService.getService();
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
    @Test
    public void totalCountTrueTest(){
    	LOGGER.info("< totalCountTrueTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {
            Node rootNode = graphDatabaseService.createNode();
            
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            
        	datasetStatistic.save();
        	datasetStatistic.updateTypeCount(ROOT_KEY, NODE_TYPE_1, 10);
        	
            Assert.assertEquals("totalCount counts incorrectly", 10, datasetStatistic.getTotalCount(ROOT_KEY, NODE_TYPE_1));
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
           
        }finally{
            tx.finish();
            LOGGER.info("< totalCountTrueTest end >");
            
        }
    }
    @Test
    public void totalCountFalseTest(){
    	LOGGER.info("< totalCountFalseTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {



            Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            
            datasetStatistic.updateTypeCount(ROOT_KEY, NODE_TYPE_1, 10);
            Assert.assertFalse("totalCount counts incorrectly", datasetStatistic.getTotalCount(ROOT_KEY, NODE_TYPE_1)==11);
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< totalCountFalseTest end >");
        }
    }
    
   
    @Test
    public void updateValueTrueTest(){
    	LOGGER.info("< updateValueTrueTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {

            Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1);
            datasetStatistic.updateValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_2, PROPERTY_VALUE_1);
            datasetStatistic.save();
            Assert.assertTrue("value isn't update",datasetStatistic.findPropertyStatistic(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1).getValueMap().containsKey(PROPERTY_VALUE_2)
            		&& !datasetStatistic.findPropertyStatistic(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1).getValueMap().containsKey(PROPERTY_VALUE_1) );
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< updateValueTrueTest end >");
        }
    }
	
}
