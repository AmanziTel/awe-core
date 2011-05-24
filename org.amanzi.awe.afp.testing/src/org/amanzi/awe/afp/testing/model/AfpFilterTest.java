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

import org.amanzi.awe.afp.filters.AfpFilterNew;
import org.amanzi.awe.afp.filters.AfpFilterNew.ExpressionType;
import org.amanzi.awe.afp.filters.AfpFilterNew.FilterType;
import org.amanzi.awe.afp.testing.engine.AbstractAfpTest;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NodeTypes;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class AfpFilterTest extends AbstractAfpTest {
    
    private static Logger LOGGER = Logger.getLogger(AfpModelTest.class);

    private static long startTimestamp;

    private static DatasetService datasetService;

  
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        startTimestamp = System.currentTimeMillis();
        LOGGER.info("Set up AFP Filter Test");

        try {
            initEnvironment();
            
            // loadDataset();
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
    /**
     * @throws java.lang.Exception
     */
  
    /*
     * check nodes with the same property value
     */
    @Test
    public void SameValuesTest() {
        LOGGER.info("< checkSameValuesThest begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {

            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Value1");
            datasetService.getNodeType(firstNode);
            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "Value1");
            AfpFilterNew afpFilter=new AfpFilterNew(FilterType.EQUALS,ExpressionType.AND);
            LOGGER.info("--firstNode property: "+"'Name'; value: "+firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: "+"'Name'; value: "+secondNode.getProperty("Name"));
            LOGGER.info("----FilterType is 'EQUALS' ExpressionType is 'AND'");
            //afpFilter.addFilter(new AfpFilterNew(FilterType.EQUALS,ExpressionType.AND));
            afpFilter.setExpression(null, "Name", secondNode.getProperty("Name"));
            Assert.assertTrue("Values are not EQUALS ",afpFilter.check(firstNode));
            
            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
           
        }finally{
            tx.finish();
            LOGGER.info("< checkSameValuesThest end >");
        }
       
  
    }
    /*
     * check node with the width incorrect Expression value
     * FilterType EQUALS
     */
    @Test
    public void DifferentValuesTest() {
        LOGGER.info("< checkDifferentValuesTest begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {

            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Value");
            datasetService.getNodeType(firstNode);
            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "Value 1 6 kk");
  
            AfpFilterNew afpFilter=new AfpFilterNew(FilterType.EQUALS,ExpressionType.AND);
            LOGGER.info("--firstNode property: "+"'Name';value: "+firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: "+"'Name';value: "+secondNode.getProperty("Name"));
            LOGGER.info("----FilterType is 'EQUALS' ExpressionType is 'AND'");
            //afpFilter.addFilter(new AfpFilterNew(FilterType.EQUALS,ExpressionType.AND));
            afpFilter.setExpression(null, "Name", secondNode.getProperty("Name"));
            Assert.assertFalse("Values are EQUALS or expression is find ",afpFilter.check(firstNode));
            tx.success();
            
        } catch (Exception e) {
            e.printStackTrace();
           
        }finally{
            tx.finish();
            LOGGER.info("< checkDifferentValuesTest end >");
        }
     }
    
    /*
     * with 'Like' filterType and RegExp
     */
    @Test
    public void FilterTypeRE() {
        LOGGER.info("< checkFilterTypeRE begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {

            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Value1");
            datasetService.getNodeType(firstNode);
            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "Value");
            AfpFilterNew afpFilter=new AfpFilterNew(FilterType.LIKE,ExpressionType.AND);
            afpFilter.setExpression(null, "Name", ".*"+secondNode.getProperty("Name")+".*");
            LOGGER.info("--firstNode property: "+"'Name'; value: "+firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: "+"'Name'; value: "+secondNode.getProperty("Name"));
            LOGGER.info("----FilterType is 'LIKE' ExpressionType is 'AND'");
            Assert.assertTrue("Need regExp or values doesn't LIKE",afpFilter.check(firstNode));
            tx.success();
            
        } catch (Exception e) {
            e.printStackTrace();
           
        }finally{
            tx.finish();
            LOGGER.info("< checkFilterTypeRE end >");
        }
     }
    
    /*
     * check with 'Like' filterType and without RegExp
     */
    @Test
    public void FilterTypeWithoutRE() {
        LOGGER.info("< checkFilterTypeWithoutRE begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {

            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Value 1 2 4");
            datasetService.getNodeType(firstNode);
            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "Value");
            AfpFilterNew afpFilter=new AfpFilterNew(FilterType.LIKE,ExpressionType.AND);
            afpFilter.setExpression(null, "Name", secondNode.getProperty("Name"));
            LOGGER.info("--firstNode property: "+"'Name'; value: "+firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: "+"'Name'; value: "+secondNode.getProperty("Name"));
            LOGGER.info("----FilterType is 'LIKE' ExpressionType is 'AND'");
            Assert.assertFalse("has RegExp or values really LIKE",afpFilter.check(firstNode));
            tx.success();
            
        } catch (Exception e) {
            e.printStackTrace();
           
        }finally{
            tx.finish();
            LOGGER.info("< checkFilterTypeWithoutRE end >");
        }
     }
    
    /*
     *check afpFilter with 'EQUALS' filterType and 'OR' expressionType
     *addAfpFilter with 'EQUALS' filterType and with 'AND'  ExpressionType
     *with same 'Rest' property
     */
    @Test
    public void AdditionFilterEQ() {
        LOGGER.info("< checkAdditionFilterEQ begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", 44);
            firstNode.setProperty("Rest", "value1");
            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", 33);
            secondNode.setProperty("Rest", "value1");
            AfpFilterNew afpFilter=new AfpFilterNew(FilterType.EQUALS,ExpressionType.OR);
            AfpFilterNew addAfpFilter=new AfpFilterNew(FilterType.EQUALS);
           
            afpFilter.setExpression(null, "Name", ".*"+secondNode.getProperty("Name")+".*");
            addAfpFilter.setExpression(null,"Rest", secondNode.getProperty("Rest"));
           
            afpFilter.addFilter(addAfpFilter);
            LOGGER.info("--firstNode property: "+"'Name'; value: "+firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: "+"'Name'; value: "+secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'EQUALS' ExpressionType is 'OR'");
            LOGGER.info("---- addAfpFilter FilterType is 'EQUALS' ExpressionType is 'AND'");
            
            Assert.assertTrue("need the same 'Rest' or 'Name' property value ",afpFilter.check(firstNode));
            tx.success();
         } catch (Exception e) {
            e.printStackTrace();
         }finally{
            tx.finish();
            LOGGER.info("< checkAdditionFilterEQ end >");
        }
     }
    /*
     *check afpFilter with 'LIKE' filterType and 'AND' expressionType
     *addAfpFilter with 'LIKE' filterType and with 'AND'  ExpressionType
     */
    @Test
    public void AdditionFilterLike() {
        LOGGER.info("< checkAdditionFilterLike begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "name 1");
            firstNode.setProperty("Rest", "value 4");
            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value");
            AfpFilterNew afpFilter=new AfpFilterNew(FilterType.LIKE,ExpressionType.AND);
            AfpFilterNew addAfpFilter=new AfpFilterNew(FilterType.LIKE);
           
            afpFilter.setExpression(null, "Name", ".*"+secondNode.getProperty("Name")+".*");
            addAfpFilter.setExpression(null,"Rest", ".*"+secondNode.getProperty("Rest")+".*");
            afpFilter.addFilter(addAfpFilter);
             
            LOGGER.info("--firstNode property: "+"'Name'; value: "+firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: "+"'Name'; value: "+secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- addAfpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            
            Assert.assertTrue("values doesn't 'LIKE' each others",afpFilter.check(firstNode));
            tx.success();
         } catch (Exception e) {
            e.printStackTrace();
         }finally{
            tx.finish();
            LOGGER.info("< checkAdditionFilterLike end >");
        }
     }
    
    /*
     *check afpFilter with 'EQUALS' filterType and 'AND' expressionType
     *addAfpFilter with 'LIKE' filterType and with 'AND'  ExpressionType
     */
    @Test
    public void AdditionFilterEQL() {
        LOGGER.info("< checkAdditionFilterEQL begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "name 1");
            firstNode.setProperty("Rest", "value 4");
            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name 1");
            secondNode.setProperty("Rest", "value");
            AfpFilterNew afpFilter=new AfpFilterNew(FilterType.EQUALS,ExpressionType.AND);
            AfpFilterNew addAfpFilter=new AfpFilterNew(FilterType.LIKE);
           
            afpFilter.setExpression(null, "Name", secondNode.getProperty("Name"));
            addAfpFilter.setExpression(null,"Rest", ".*"+secondNode.getProperty("Rest")+".*");
            afpFilter.addFilter(addAfpFilter);
             
            LOGGER.info("--firstNode property: "+"'Name'; value: "+firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: "+"'Name'; value: "+secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'EQUALS' ExpressionType is 'AND'");
            LOGGER.info("---- addAfpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            
            Assert.assertTrue("'Name' property is not EQUALS or 'Rest' property is not LIKE to Expression",afpFilter.check(firstNode));
            tx.success();
         } catch (Exception e) {
            e.printStackTrace();
         }finally{
            tx.finish();
            LOGGER.info("< checkAdditionFilterEQL end >");
        }
     }
    
    /*
     *check afpFilter with 'LIKE' filterType and 'AND' expressionType 
     *addAfpFilter with 'EQUALS' filterType and with 'AND'  ExpressionType
    */
    @Test
    public void AdditionFilterLEQ() {
        LOGGER.info("< checkAdditionFilterLEQ begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "name 1");
            firstNode.setProperty("Rest", "value 4");
           
            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            
            AfpFilterNew afpFilter=new AfpFilterNew(FilterType.LIKE,ExpressionType.AND);
            AfpFilterNew addAfpFilter=new AfpFilterNew(FilterType.EQUALS);
           
            afpFilter.setExpression(null, "Name", ".*"+secondNode.getProperty("Name")+".*");
            addAfpFilter.setExpression(null,"Rest", secondNode.getProperty("Rest"));
            afpFilter.addFilter(addAfpFilter);
             
            LOGGER.info("--firstNode property: "+"'Name'; value: "+firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: "+"'Name'; value: "+secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- addAfpFilter FilterType is 'EQUALS' ExpressionType is 'AND'");
            
            Assert.assertTrue("'Name' property is not LIKE or 'Rest' property not EQUALS to Expression",afpFilter.check(firstNode));
            tx.success();
         } catch (Exception e) {
            e.printStackTrace();
         }finally{
            tx.finish();
            LOGGER.info("< checkAdditionFilterLEQ end >");
        }
     }
    
    /*
     *check if node doesn't consist propertyName
     *
    */
    @Test
    public void NullName() {
        LOGGER.info("< checkAdditionFilterLEQ begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.LIKE);
            afpFilter.setExpression(null, "Name", ".*" + secondNode.getProperty("Name") + ".*");

            LOGGER.info("--firstNode doesn't consist 'Name' property: ");
            LOGGER.info("--secondNode property: " + "'Name'; value: " + secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            Assert.assertFalse("'Name' property is found",afpFilter.check(firstNode));
        }catch (org.neo4j.graphdb.NotFoundException e) {
            e.printStackTrace();
            //Assert.fail("can't find propertyName in a node ");
        } 
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.finish();
            LOGGER.info("< checkNullName end >");
        }
    }
//    
//    /*
//     *check if node property value is  null
//     *
//    */
//    @Test
//    public void checkNullValue() {
//        LOGGER.info("< checkAdditionFilterLEQ begin >");
//        Transaction tx = graphDatabaseService.beginTx();
//        try {
//            Node firstNode = graphDatabaseService.createNode();
//            firstNode.setProperty("Name", null);
//            firstNode.setProperty("Rest", "value 4");
//
//            Node secondNode = graphDatabaseService.createNode();
//            secondNode.setProperty("Name", "name");
//            secondNode.setProperty("Rest", "value 4");
//            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.LIKE);
//            afpFilter.setExpression(null, "Name", ".*" + secondNode.getProperty("Name") + ".*");
//
//            LOGGER.info("--firstNode property: " + "'Name'; value: " + firstNode.getProperty("Name"));
//            LOGGER.info("--secondNode property: " + "'Name'; value: " + secondNode.getProperty("Name"));
//            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
//            LOGGER.info("---- addAfpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
//            Assert.assertTrue("'Name' property is not LIKE or 'Rest' property not EQUALS to Expression",afpFilter.check(firstNode));
//        }catch (java.lang.IllegalArgumentException e) {
//            e.printStackTrace();
//            Assert.fail("Value in filtering Node is null");
//        } 
//        catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            tx.finish();
//            LOGGER.info("< checkAdditionFilterLEQ end >");
//        }
//    }
    
    /*
     *check if propertyName in expression  is  null
     *FilterType 'LIKE'
    */
    @Test
    public void NullPropertyNameLike() {
        LOGGER.info("< checkPropertyNameNull begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Name11");
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.LIKE);
            afpFilter.setExpression(null,null, ".*" + secondNode.getProperty("Name") + ".*");

            LOGGER.info("--firstNode property: " + "'Name'; value: " + firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: " + "'Name'; value: " + secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- afpFilter Expression: null,null,'name'");
            Assert.assertFalse("Expression propertyName is null",afpFilter.check(firstNode));
        }catch (NullPointerException e){
            e.printStackTrace();
            //Assert.fail("Null pointer Exception in propertyName");
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.finish();
            LOGGER.info("< checkNullPropertyNameLike end >");
        }
    }
    
    /*
     *check if propertyName in expression  is  null
     *FilterType 'EQUALS'
    */
    @Test
    public void NullPropertyNameEQUALS() {
        LOGGER.info("< checkPropertyNameNull begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Name11");
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.EQUALS);
            afpFilter.setExpression(null,null,secondNode.getProperty("Name"));

            LOGGER.info("--firstNode property: " + "'Name'; value: " + firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: " + "'Name'; value: " + secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- afpFilter Expression: null,null,'name'");
            Assert.assertFalse("Expression propertyName is null",afpFilter.check(firstNode));
        }catch (NullPointerException e){
            e.printStackTrace();
            //Assert.fail("Null pointer Exception in propertyName");
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.finish();
            LOGGER.info("< checkNullPropertyNameEQUALS end >");
        }
    }
    
    /*
     *check if propertyName in expression  is  null
     *FilterType ERQUALS
    */
    @Test
    public void NullPropertyValueEQ() {
        LOGGER.info("< checkPropertyNameNull begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Name11");
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.EQUALS);
            afpFilter.setExpression(null,"Name", null);

            LOGGER.info("--firstNode property: " + "'Name'; value: " + firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: " + "'Name'; value: " + secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- afpFilter Expression: null,'Name',null");
            Assert.assertFalse("Expression propertyName is not null",afpFilter.check(firstNode));
        }catch (NullPointerException e){
            e.printStackTrace();
            //Assert.fail("Null pointer Exception in propertyValue");
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.finish();
            LOGGER.info("< checkNullPropertyValueEQ end >");
        }
    }
    /*
     *check if expression propertyName is  null
     *FilterType LIKE
    */
    @Test
    public void NullPropertyValueLike() {
        LOGGER.info("< checkPropertyNameNull begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Name11");
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.LIKE);
            afpFilter.setExpression(null,"Name", null);

            LOGGER.info("--firstNode property: " + "'Name'; value: " + firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: " + "'Name'; value: " + secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- afpFilter Expression: null,'Name',null");
            Assert.assertFalse("Expression propertyValue is not null",afpFilter.check(firstNode));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.finish();
            LOGGER.info("< checkNullPropertyValue end >");
        }
    }
    /*
     *check if expression propertyName and value null
     *FilterType LIKE
    */
    @Test
    public void NullBOTHLike() {
        LOGGER.info("< checkNullBOTHLike begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Name11");
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.LIKE);
            afpFilter.setExpression(null,null, null);

            LOGGER.info("--firstNode property: " + "'Name'; value: " + firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: " + "'Name'; value: " + secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- afpFilter Expression: null,null,null");
            Assert.assertFalse("Expression BOTH parameters is not null",afpFilter.check(firstNode));
        }catch (NullPointerException e){
            e.printStackTrace();
            //Assert.fail("Null pointer Exception in propertyValue or propertyName");
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.finish();
            LOGGER.info("< checkNullBOTHLike end >");
        }
    }
    /*
     * check if expression propertyName and value null
     * FilterType EQUALS
    */
    @Test
    public void NullBOTHEQ() {
        LOGGER.info("< checkNullBOTHLike begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            firstNode.setProperty("Name", "Name11");
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.EQUALS);
            afpFilter.setExpression(null,null, null);

            LOGGER.info("--firstNode property: " + "'Name'; value: " + firstNode.getProperty("Name"));
            LOGGER.info("--secondNode property: " + "'Name'; value: " + secondNode.getProperty("Name"));
            LOGGER.info("---- afpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- addAfpFilter FilterType is 'LIKE' ExpressionType is 'AND'");
            LOGGER.info("---- afpFilter Expression: null,null,null");
            Assert.assertFalse("Expression BOTH parameters is not null",afpFilter.check(firstNode));
        }catch (NullPointerException e){
            e.printStackTrace();
            //Assert.fail("Null pointer Exception in propertyValue or propertyName");
        }
        catch (Exception e) {
            e.printStackTrace();
    
        } finally {
            tx.finish();
            LOGGER.info("< checkNullBOTHLike end >");
        }
    }
    
    /*
     * check nodeType 
     * FilterType EQUALS
    */
    @Test
    public void SameNodeType() {
        LOGGER.info("< checkSameNodeType begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            datasetService.setNodeType(firstNode, NodeTypes.CITY);
            //System.out.println(datasetService.getNodeType(firstNode));
            firstNode.setProperty("Name", "Name11");
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.EQUALS,ExpressionType.AND);
            afpFilter.setExpression(NodeTypes.CITY,"Rest", "value 4");

            LOGGER.info("--firstNode nodeType:'CITY'; property: " + "'Rest'; value: " + firstNode.getProperty("Rest"));
            LOGGER.info("---- afpFilter FilterType is 'EQUALS' ExpressionType is 'AND'");
            LOGGER.info("---- afpFilter Expression: CITY,'Rest','value 4'");
            Assert.assertTrue("Expression NodeType Parameter and node type are not the same",afpFilter.check(firstNode));
        }catch (NullPointerException e){
            e.printStackTrace();
            //Assert.fail("Null pointer Exception in propertyValue or propertyName");
        }
        catch (Exception e) {
            e.printStackTrace();
    
        } finally {
            tx.finish();
            LOGGER.info("< checkSameNodeType end >");
        }
    }
    
    /*
     * check nodeType 
     * FilterType EQUALS
    */
    @Test
    public void DiffNodeType() {
        LOGGER.info("< checkNullBOTHLike begin >");
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node firstNode = graphDatabaseService.createNode();
            datasetService.setNodeType(firstNode, NodeTypes.CITY);
            //System.out.println(datasetService.getNodeType(firstNode));
            firstNode.setProperty("Name", "Name11");
            firstNode.setProperty("Rest", "value 4");

            Node secondNode = graphDatabaseService.createNode();
            secondNode.setProperty("Name", "name");
            secondNode.setProperty("Rest", "value 4");
            AfpFilterNew afpFilter = new AfpFilterNew(FilterType.EQUALS,ExpressionType.AND);
            //AfpFilterNew addafpFilter = new AfpFilterNew(FilterType.LIKE);
           // addafpFilter.setExpression(NodeTypes.CITY, "Name", ".*Name.*");
            afpFilter.setExpression(NodeTypes.BSC,"Rest", "value 4");
           // afpFilter.addFilter(addafpFilter);


            LOGGER.info("--firstNode nodeType:'CITY'; property: " + "'Rest'; value: " + firstNode.getProperty("Rest"));
            LOGGER.info("---- afpFilter FilterType is 'EQUALS' ExpressionType is 'AND'");
            LOGGER.info("---- afpFilter Expression: BSC,'Rest','value 4'");
            Assert.assertFalse("Expression NodeType Parameter and node type are the same",afpFilter.check(firstNode));
        }catch (NullPointerException e){
            e.printStackTrace();
            //Assert.fail("Null pointer Exception in propertyValue or propertyName");
        }
        catch (Exception e) {
            e.printStackTrace();
    
        } finally {
            tx.finish();
            LOGGER.info("< checkDiffNodeType end >");
        }
    }
}
