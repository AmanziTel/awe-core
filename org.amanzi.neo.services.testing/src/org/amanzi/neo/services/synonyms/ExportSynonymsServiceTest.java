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

package org.amanzi.neo.services.synonyms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonymType;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonyms;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonymsNodeType;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonymsRelationshipTypes;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * Tests on ExportSynonyms Service
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class ExportSynonymsServiceTest extends AbstractNeoServiceTest {
    
    private static final Logger LOGGER = Logger.getLogger(ExportSynonymsServiceTest.class);
    
    private static final String TEST_SYNONYM = "synonym";
    
    private static final INodeType TEST_NODE_TYPE = new INodeType() {
        
        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        @Override
        public String getId() {
            return "test";
        }
    };
    
    private static final String TEST_PROPERTY_NAME = "synonym";
    
    private static final String TEST_KEY = TEST_NODE_TYPE.getId() + "." + TEST_PROPERTY_NAME;
    
    private ExportSynonymsService synonymsService = null;
    
    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
        clearServices();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        synonymsService = new ExportSynonymsService(graphDatabaseService);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        cleanUpReferenceNode();
    }
    
    /**
     * Cleans up all relationships from ReferenceNode
     */
    private void cleanUpReferenceNode() throws Exception {
        Transaction tx = graphDatabaseService.beginTx();
        try {
            for (Relationship relationship : graphDatabaseService.getReferenceNode().getRelationships()){ 
                relationship.delete();
            }
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Exception on cleaning up reference node", e);
            throw e;
        } finally {
            tx.finish();
        }
    }

    @Test
    public void testGetGlobalSynonymsDidNotThrowExceptions() throws Exception {
        synonymsService.getGlobalExportSynonyms();
    }
    
    @Test
    public void testResultOfGetGlobalSynonymsNotNull() throws DatabaseException {
        ExportSynonyms result = synonymsService.getGlobalExportSynonyms();
        
        Assert.assertNotNull("ExportSynonyms cannot be null", result);
    }
    
    @Test
    public void testResultOfGetGlobalSynonymsIsEmpty() throws DatabaseException {
        ExportSynonyms result = synonymsService.getGlobalExportSynonyms();
        
        Assert.assertTrue("ExportSynonyms should be empty", result.rawSynonyms.isEmpty());
    }
    
    @Test
    public void checkContentOfGlobalSynonyms() throws Exception {
        HashMap<String, String> content = new HashMap<String, String>();
        content.put(TEST_KEY, TEST_SYNONYM);
        
        createGlobalExportSynonyms(content);
        
        ExportSynonyms synonyms = synonymsService.getGlobalExportSynonyms();
        
        Assert.assertFalse("ExportSynonyms cannot be empty", synonyms.rawSynonyms.isEmpty());
        Assert.assertEquals("Size of Global Export Synonyms should be 1", 1, synonyms.rawSynonyms.size());
        Assert.assertTrue("Global Export Synonyms should contain Default Key", synonyms.rawSynonyms.containsKey(TEST_KEY));
        Assert.assertTrue("Global Export Synonyms should contain Default Synonym", synonyms.rawSynonyms.containsValue(TEST_SYNONYM));
    }
    
    @Test
    public void checkGetMethodOfExportSynonyms() throws Exception {
        HashMap<String, String> content = new HashMap<String, String>();
        content.put(TEST_KEY, TEST_SYNONYM);
        
        createGlobalExportSynonyms(content);
        
        ExportSynonyms synonyms = synonymsService.getGlobalExportSynonyms();
        
        assertNotNull("Global Export Synonyms should find synonym for Default Key", synonyms.getSynonym(TEST_NODE_TYPE, TEST_PROPERTY_NAME));
        assertEquals("Incorrect Synonym for Default Key", TEST_SYNONYM, synonyms.getSynonym(TEST_NODE_TYPE, TEST_PROPERTY_NAME));
    }
    
    @Test(expected = DatabaseException.class)
    public void incorrectNodeTypeOfGlobalExportSynonyms() throws DatabaseException {
        Transaction tx = graphDatabaseService.beginTx();
    
        Node fakeGlobalSynonymsNode = graphDatabaseService.createNode();
        fakeGlobalSynonymsNode.setProperty(NewAbstractService.TYPE, NodeTypes.CELL.getId());
        graphDatabaseService.getReferenceNode().createRelationshipTo(fakeGlobalSynonymsNode, ExportSynonymsRelationshipTypes.GLOBAL_SYNONYMS);
        
        tx.success();
        tx.finish();
        
        synonymsService.getGlobalExportSynonyms();
    }
    
    @Test(expected = DatabaseException.class)
    public void incorrectSynonymsTypeOfGlobalExportSynonyms() throws DatabaseException, Exception {
        Node globalNode = createGlobalExportSynonyms(new HashMap<String, String>());
        
        Transaction tx = graphDatabaseService.beginTx();
        globalNode.setProperty(ExportSynonymsService.EXPORT_SYNONYMS_TYPE, ExportSynonymType.DATASET.name());
        tx.success();
        tx.finish();
        
        synonymsService.getGlobalExportSynonyms();
    }
    
    
    /**
     * Creates Global Export Synonyms Node with some properties
     *
     * @throws Exception
     */
    private Node createGlobalExportSynonyms(HashMap<String, String> content) throws Exception {
        Transaction tx = graphDatabaseService.beginTx();
        
        Node exportSynonymsNode = null;
        
        try {
            exportSynonymsNode = graphDatabaseService.createNode();
            exportSynonymsNode.setProperty(ExportSynonymsService.TYPE, ExportSynonymsNodeType.EXPORT_SYNONYMS.getId());
            exportSynonymsNode.setProperty(ExportSynonymsService.EXPORT_SYNONYMS_TYPE, ExportSynonymType.GLOBAL.name());
            
            for (Entry<String, String> singleEntry : content.entrySet()) {
                exportSynonymsNode.setProperty(singleEntry.getKey(), singleEntry.getValue());
            }
            
            graphDatabaseService.getReferenceNode().createRelationshipTo(exportSynonymsNode, ExportSynonymsRelationshipTypes.GLOBAL_SYNONYMS);
            
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creating Global Export Synonyms node", e);
            throw e;
        } finally {
            tx.finish();
        }
        
        return exportSynonymsNode;
    }
    
    /**
     * Converts Global Export Synonyms Node with some properties to Dataset
     *
     * @throws Exception
     */
    private Node createDatasetExportSynonyms(Node datasetNode, HashMap<String, String> content) throws Exception {
        Transaction tx = graphDatabaseService.beginTx();
        
        Node exportSynonymsNode = null;
        
        try {
            exportSynonymsNode = createGlobalExportSynonyms(content);
            
            exportSynonymsNode.setProperty(ExportSynonymsService.TYPE, ExportSynonymsNodeType.EXPORT_SYNONYMS.getId());
            exportSynonymsNode.setProperty(ExportSynonymsService.EXPORT_SYNONYMS_TYPE, ExportSynonymType.DATASET.name());
            exportSynonymsNode.getSingleRelationship(ExportSynonymsRelationshipTypes.GLOBAL_SYNONYMS, Direction.INCOMING).delete();
            
            datasetNode.createRelationshipTo(exportSynonymsNode, ExportSynonymsRelationshipTypes.DATASET_SYNONYMS);
            
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creating Global Export Synonyms node", e);
            throw e;
        } finally {
            tx.finish();
        }
        
        return exportSynonymsNode;
    }

}
