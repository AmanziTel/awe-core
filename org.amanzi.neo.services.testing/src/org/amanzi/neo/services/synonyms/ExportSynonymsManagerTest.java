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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonymType;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonyms;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.neo4j.graphdb.Node;

/**
 * Tests on Export Synonyms Manager
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class ExportSynonymsManagerTest extends AbstractNeoServiceTest {
    
    private static final String PROPERTY_NAME = "property";
    
    private static final String DATASET_NAME = "dataset";
    
    private static final String SYNONYM = "synonym";
    
    private static final String INCORRECT_SYNONYM = "incorrect_synonym";
    
    private static final String ANOTHER_SYNONYM = "another_synonym";
    
    private static final String ANOTHER_PROPERTY = "another_property";
    
    private static final INodeType NODE_TYPE = NodeTypes.SECTOR;
    
    private ExportSynonymsManager manager;
    
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
        manager = ExportSynonymsManager.getManager();
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSynonymsWithNullDatasetName() throws Exception {
        manager.getExportHeader(null, NODE_TYPE, PROPERTY_NAME);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getSynonymsWithoutNodeType() throws Exception {
        manager.getExportHeader(getDataModelMock(), null, PROPERTY_NAME);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getSynonymsWithoutPropertyName() throws Exception {
        manager.getExportHeader(getDataModelMock(), NODE_TYPE, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getSynonymsWithEmptyPropertyName() throws Exception {
        manager.getExportHeader(getDataModelMock(), NODE_TYPE, StringUtils.EMPTY);
    }
    
    @Test
    public void checkMethodWithoutAnySynonymsInDatabase() throws Exception {
        ExportSynonymsService service = getSynonymsService(null, false, false);
        ExportSynonymsManager.initializeService(service);
        
        String result = manager.getExportHeader(getDataModelMock(), NODE_TYPE, PROPERTY_NAME);
        
        assertEquals("Unexpected Synonym with Empty DB", PROPERTY_NAME, result);
        
        verify(service).getGlobalExportSynonyms();
        verify(service).getDatasetExportSynonyms(any(Node.class));
    }
    
    @Test
    public void checkMethodWithDatasetSynonyms() throws Exception {
        ExportSynonymsService service = getSynonymsService(ExportSynonymType.DATASET, true, null);
        ExportSynonymsManager.initializeService(service);
        
        String result = manager.getExportHeader(getDataModelMock(), NODE_TYPE, PROPERTY_NAME);
        
        assertEquals("Unexpected Synonym with Dataset Synonyms", SYNONYM, result);
        
        verify(service).getDatasetExportSynonyms(any(Node.class));
        verify(service, never()).getGlobalExportSynonyms();
    }
    
    @Test
    public void checkMethodWithIncorrectDatasetSynonyms() throws Exception {
        ExportSynonymsService service = getSynonymsService(ExportSynonymType.DATASET, false, true);
        ExportSynonymsManager.initializeService(service);
        
        String result = manager.getExportHeader(getDataModelMock(), NODE_TYPE, PROPERTY_NAME);
        
        assertEquals("Unexpected Synonym with Dataset Synonyms", ANOTHER_SYNONYM, result);
        
        verify(service).getDatasetExportSynonyms(any(Node.class));
        verify(service).getGlobalExportSynonyms();
    }
    
    @Test
    public void checkMethodWithIncorrectDatasetAndGlobalSynonyms() throws Exception {
        ExportSynonymsService service = getSynonymsService(ExportSynonymType.DATASET, false, false);
        ExportSynonymsManager.initializeService(service);
        
        String result = manager.getExportHeader(getDataModelMock(), NODE_TYPE, PROPERTY_NAME);
        
        assertEquals("Unexpected Synonym with Dataset Synonyms", PROPERTY_NAME, result);
        
        verify(service).getDatasetExportSynonyms(any(Node.class));
        verify(service).getGlobalExportSynonyms();
    }
    
    @Test
    public void checkMethodWithGlobalSynonyms() throws Exception {
        ExportSynonymsService service = getSynonymsService(ExportSynonymType.GLOBAL, false, true);
        ExportSynonymsManager.initializeService(service);
        
        String result = manager.getExportHeader(getDataModelMock(), NODE_TYPE, PROPERTY_NAME);
        
        assertEquals("Unexpected Synonym with Global Synonyms", SYNONYM, result);
        
        verify(service).getDatasetExportSynonyms(any(Node.class));
        verify(service).getGlobalExportSynonyms();
    }
    
    @Test
    public void checkMethodWithIncorrectGlobalSynonyms() throws Exception {
        ExportSynonymsService service = getSynonymsService(ExportSynonymType.GLOBAL, false, false);
        ExportSynonymsManager.initializeService(service);
        
        String result = manager.getExportHeader(getDataModelMock(), NODE_TYPE, PROPERTY_NAME);
        
        assertEquals("Unexpected Synonym with Global Synonyms", PROPERTY_NAME, result);
        
        verify(service).getDatasetExportSynonyms(any(Node.class));
        verify(service).getGlobalExportSynonyms();
    }
    
    /**
     * Creates Mockes IDataModel
     *
     * @return
     */
    private IDataModel getDataModelMock() {
        IDataModel result = mock(IDataModel.class);
        
        when(result.getName()).thenReturn(DATASET_NAME);
        
        return result;
    }
    
    /**
     * Creates mocked Exporty Synonyms
     *
     * @param nodeType node type of synonym
     * @param propertyName property name
     * @param synonym synonym
     * @return
     */
    private ExportSynonyms getExportSynonyms(INodeType nodeType, String propertyName, String synonym) {
        ExportSynonyms result = mock(ExportSynonyms.class);
        
        when(result.getSynonym(nodeType, propertyName)).thenReturn(synonym);
        
        return result;
    }
    
    /**
     * Creates Mock of Service
     * 
     * @param returnType type of mocked service. If null both Global and Dataset synonyms will be empty
     * @param correct should other Synonyms contain correct entry
     * @return
     * @throws DatabaseException
     */
    private ExportSynonymsService getSynonymsService(ExportSynonymType returnType, boolean correctDataset, Boolean correctGlobal) throws DatabaseException {
        ExportSynonymsService result = Mockito.mock(ExportSynonymsService.class);
        ExportSynonyms synonyms = null;
        
        if (returnType == null) {
            synonyms = getExportSynonyms(NODE_TYPE, ANOTHER_PROPERTY, INCORRECT_SYNONYM);
            when(result.getGlobalExportSynonyms()).thenReturn(synonyms);
            synonyms = getExportSynonyms(NODE_TYPE, ANOTHER_PROPERTY, INCORRECT_SYNONYM);
            when(result.getDatasetExportSynonyms(any(Node.class))).thenReturn(synonyms);
        } else {
            switch (returnType) {
            case DATASET:
                String synonym = SYNONYM;
                String property = PROPERTY_NAME;
                if (!correctDataset) {
                    synonym = INCORRECT_SYNONYM;
                    property = ANOTHER_PROPERTY;
                }
                synonyms = getExportSynonyms(NODE_TYPE, property, synonym);
                when(result.getDatasetExportSynonyms(any(Node.class))).thenReturn(synonyms);
                
                if (correctGlobal != null && !correctDataset) {
                    synonym = ANOTHER_SYNONYM;
                    property = PROPERTY_NAME;
                    if (!correctGlobal) {
                        synonym = INCORRECT_SYNONYM;
                        property = ANOTHER_PROPERTY;
                    }
                    synonyms = getExportSynonyms(NODE_TYPE, property, synonym);
                    when(result.getGlobalExportSynonyms()).thenReturn(synonyms);
                }
                
                break;
            case GLOBAL:
                
                synonyms = getExportSynonyms(NODE_TYPE, ANOTHER_PROPERTY, SYNONYM);
                when(result.getDatasetExportSynonyms(any(Node.class))).thenReturn(synonyms);
                
                synonym = SYNONYM;
                property = PROPERTY_NAME;
                if (!correctGlobal) {
                    synonym = INCORRECT_SYNONYM;
                    property = ANOTHER_PROPERTY;
                }
                synonyms = getExportSynonyms(NODE_TYPE, property, synonym);
                when(result.getGlobalExportSynonyms()).thenReturn(synonyms);
                
                break;
            }
        }
        
        return result;
    }

}
