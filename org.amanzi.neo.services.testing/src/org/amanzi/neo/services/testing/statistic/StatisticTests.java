package org.amanzi.neo.services.testing.statistic;


import java.util.ArrayList;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;

import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.amanzi.neo.services.statistic.internal.DatasetStatistic;
import org.amanzi.neo.services.statistic.internal.PropertyStatistics;
import org.amanzi.neo.services.statistic.internal.Vault;


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
        //clearDb();

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
    public void indexValueTrueTest(){
    	LOGGER.info("< indexValueTrueTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {
        	Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            Assert.assertTrue("value not index", datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1));
            
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< indexValueTrueTest end >");
        }
    	
    }
    @Test
    public void indexValueFalseTest(){
    	LOGGER.info("< indexValueFalseTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {
        	Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            Assert.assertFalse("null value  index", datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, null));
            
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< indexValueFalseTest end >");
        }
    	
    }
    @Test
    public void indexValueRuleFalseTest(){
    	LOGGER.info("< indexValueRuleFalseTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {
        	Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1);
            datasetStatistic.save();
            PropertyStatistics ps = (PropertyStatistics)datasetStatistic.findPropertyStatistic(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1);
            ps.register(ps.getKlass(), ChangeClassRule.IGNORE_NEW_CLASS);
                       
            Assert.assertFalse("new class value index, but filter is IGNORE_NEW_CLASS", datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, 5));
            
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< indexValueRuleFalseTest end >");
        }
    	
    }
    @Test
    public void indexValueRuleTrueTest(){
    	LOGGER.info("< indexValueRuleTrueTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {
        	Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1);
                       
            Assert.assertTrue("new class value not index, but filter is REMOVE_OLD_CLASS", datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, 5));
            
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< indexValueRuleTrueTest end >");
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
    @Test
    public void updateValueNewNullTest(){
    	LOGGER.info("< updateValueNewNullTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {

            Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1);
            datasetStatistic.updateValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, null, PROPERTY_VALUE_1);

            Assert.assertTrue("value isn't update",datasetStatistic.findPropertyStatistic(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1).getCount()==0);
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< updateValueNewNullTest end >");
        }
    }
    @Test
    public void updateValueOldNullTest(){
    	LOGGER.info("< updateValueOldNullTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {

            Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            
            datasetStatistic.updateValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1, null);

            Assert.assertTrue("value isn't update",datasetStatistic.findPropertyStatistic(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1).getValueMap().containsKey(PROPERTY_VALUE_1));
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< updateValueOldNullTest end >");
        }
    }
    @Test
    public void parseValueTrueTest(){
    	LOGGER.info("< parseValueTrueTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
    	try {
    		Node rootNode = graphDatabaseService.createNode();
    		DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
    		datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, 5l);
    		
    		Assert.assertTrue("value incorrect parse",datasetStatistic.parseValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, "5").getClass().equals(Long.class));
    		
    		tx.success();

    	} catch (Exception e) {
    		LOGGER.error(e.toString());

    	}finally{
    		tx.finish();
    		LOGGER.info("< parseValueTrueTest end >");
    	}
    }
    @Test
    public void avtoParseValueFloatTest(){
    	LOGGER.info("< avtoParseValueFloatTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
    	try {
    		Node rootNode = graphDatabaseService.createNode();
    		DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
    		
    		Assert.assertTrue("value incorrect parse",datasetStatistic.parseValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, "5.5").getClass().equals(Float.class));
    		
    		tx.success();

    	} catch (Exception e) {
    		LOGGER.error(e.toString());

    	}finally{
    		tx.finish();
    		LOGGER.info("< avtoParseValueFloatTest end >");
    	}
    }
    @Test
    public void avtoParseValueIntegerTest(){
    	LOGGER.info("< avtoParseValueIntegerTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
    	try {
    		Node rootNode = graphDatabaseService.createNode();
    		DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
    		
    		Assert.assertTrue("value incorrect parse",datasetStatistic.parseValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, "5").getClass().equals(Integer.class));
    		
    		tx.success();

    	} catch (Exception e) {
    		LOGGER.error(e.toString());

    	}finally{
    		tx.finish();
    		LOGGER.info("< avtoParseValueIntegerTest end >");
    	}
    }
    @Test
    public void avtoParseValueStringTest(){
    	LOGGER.info("< avtoParseValueStringTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
    	try {
    		Node rootNode = graphDatabaseService.createNode();
    		DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
    		
    		Assert.assertTrue("value incorrect parse",datasetStatistic.parseValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, "5,5").getClass().equals(String.class));
    		
    		tx.success();

    	} catch (Exception e) {
    		LOGGER.error(e.toString());

    	}finally{
    		tx.finish();
    		LOGGER.info("< avtoParseValueStringTest end >");
    	}
    }
    @Test
    public void getPropertyNameCollectionTrueTest(){
    	LOGGER.info("< getPropertyNameCollectionTrueTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {
            Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            ArrayList<String> col = new ArrayList<String>();
            col.add(PROPERTY_NAME_1);
            datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1);
            Assert.assertTrue("collection of propertyName is wrong", col.equals(datasetStatistic.getPropertyNameCollection(ROOT_KEY, NODE_TYPE_1,  new Comparable<Class<?>>() {

                @Override
                public int compareTo(Class o) {
                    return Comparable.class.isAssignableFrom(o) ? 0 : -1;
                }
            })));
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< getPropertyNameCollectionTrueTest end >");
        }
    }
    @Test
    public void getPropertyNameCollectionFalseTest(){
    	LOGGER.info("< getPropertyNameCollectionFalseTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
        try {
            Node rootNode = graphDatabaseService.createNode();
            DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);
            ArrayList<String> col = new ArrayList<String>();
            col.add(PROPERTY_NAME_1);
            datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1);
            datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_2, PROPERTY_VALUE_1);
            Assert.assertFalse("collection of propertyName is wrong", col.equals(datasetStatistic.getPropertyNameCollection(ROOT_KEY, NODE_TYPE_1,  new Comparable<Class<?>>() {

                @Override
                public int compareTo(Class o) {
                    return Comparable.class.isAssignableFrom(o) ? 0 : -1;
                }
            })));
            tx.success();
            
        } catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< getPropertyNameCollectionFalseTest end >");
        }
    }
    
    @Test
    public void findPropertyStatisticNullTest(){
    	LOGGER.info("< findPropertyStatisticNullTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
    	try {
    		Node rootNode = graphDatabaseService.createNode();
    		DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode);         
    		Assert.assertNull("find propertyStatistic", datasetStatistic.findPropertyStatistic(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1));
    		tx.success();

    	} catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< findPropertyStatisticNullTest end >");
        }
    }
    @Test
    public void findPropertyStatisticNotNullTest(){
    	LOGGER.info("< findPropertyStatisticNotNullTest begin >");
    	Transaction tx = graphDatabaseService.beginTx();
    	try {
    		Node rootNode = graphDatabaseService.createNode();
    		DatasetStatistic datasetStatistic = new DatasetStatistic(rootNode); 
    		datasetStatistic.indexValue(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1, PROPERTY_VALUE_1);
    		Assert.assertNotNull("don't find propertyStatistic", datasetStatistic.findPropertyStatistic(ROOT_KEY, NODE_TYPE_1, PROPERTY_NAME_1));
    		tx.success();

    	} catch (Exception e) {
        	LOGGER.error(e.toString());
                       
        }finally{
            tx.finish();
            LOGGER.info("< findPropertyStatisticNotNullTest end >");
        }
    }
	

}
