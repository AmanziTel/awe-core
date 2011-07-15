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

package org.amanzi.neo.services.testing.network_model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.amanzi.neo.services.IDatasetService;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.DatasetStructureHandler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Unit test for model
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class TestDatasetStructureHandler {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private Mockery mockContext;
    private Node baseNode;
    private IDatasetService service;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mockContext = new JUnit4Mockery();
        baseNode = mockContext.mock(Node.class);
        service=mockContext.mock(IDatasetService.class);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void emptyStructureInitializedWithNetworkElement() {
       
        checkingMockGetProperty(baseNode,"structure",null);
        DatasetStructureHandler handler = new DatasetStructureHandler(baseNode,service);
        assertArrayEquals( new String[] {NodeTypes.NETWORK.getId()},handler.getStructureLikeArray());
    }
    
    @Test
    public final void returnStructureDependsOnInitialElement() {
        checkingMockGetProperty(baseNode,"structure",new String[]{"1","2","3"});
        DatasetStructureHandler handler = new DatasetStructureHandler(baseNode,service);
        assertArrayEquals( new String[]{"1","2","3"},handler.getStructureLikeArray());
    }
    @Test
    public final void addChildNew(){
        checkingMockGetProperty(baseNode,"structure",null);
        DatasetStructureHandler handler = new DatasetStructureHandler(baseNode,service);
        handler.addType(NodeTypes.NETWORK,NodeTypes.BSC);
        handler.addType(NodeTypes.BSC,NodeTypes.SECTOR);
        handler.addType(NodeTypes.NETWORK,NodeTypes.SECTOR);
        assertArrayEquals( new String[]{NodeTypes.NETWORK.getId(),NodeTypes.BSC.getId(),NodeTypes.SECTOR.getId()},handler.getStructureLikeArray());
    }
    @Test
    public final void storeServiceWasCalled(){
        checkingMockGetProperty(baseNode,"structure", new String[]{NodeTypes.NETWORK.getId(),NodeTypes.BSC.getId(),NodeTypes.SECTOR.getId()}); 
        DatasetStructureHandler handler = new DatasetStructureHandler(baseNode,service);
        mockContext.checking(new Expectations(){{
            one(service).setStructure(baseNode,  new String[]{NodeTypes.NETWORK.getId(),NodeTypes.BSC.getId(),NodeTypes.SECTOR.getId()});
        }});
        handler.store();
    }
    @Test
    public final void checkConatin(){
        checkingMockGetProperty(baseNode,"structure",new String[]{NodeTypes.NETWORK.getId(),NodeTypes.BSC.getId(),NodeTypes.SECTOR.getId()});
        DatasetStructureHandler handler = new DatasetStructureHandler(baseNode,service);
        assertTrue(handler.contain(NodeTypes.NETWORK));
        
    }
    public void checkingMockGetProperty(final Node baseNode,final  String propertyName,final  Object propertyValue) {
        mockContext.checking(new Expectations() {
            {
                one(baseNode).getProperty(propertyName, null);
                will(returnValue(propertyValue));
            }
        });
    }

}
