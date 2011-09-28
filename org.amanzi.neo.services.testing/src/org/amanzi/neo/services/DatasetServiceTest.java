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

package org.amanzi.neo.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.IDatasetService;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.testing.AbstractAWETest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 *Test for org.amanzi.neo.services.DatasetService
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class DatasetServiceTest extends AbstractAWETest {

    @BeforeClass
    public static final void beforeClass(){
        clearDb();
        initializeDb();
    }
    private Transaction tx;
    private IDatasetService service;
    @Before
    public final void before(){
        tx=graphDatabaseService.beginTx();
        service=new DatasetService(graphDatabaseService);
    }
    @After
    public final void after(){
        tx.failure();
        tx.finish();
        tx=null;
    }
    @Test
    public final void getPropertyListOfSectorRoot_return_empty_string_for_not_exist_property(){
        Node sector=createNodeWithType(NodeTypes.SECTOR.getId());
        List<String> result = service.getPropertyListOfSectorRoot(sector,NodeTypes.SECTOR.getId(),"not_exist_property");
        List<String>  expexcted=new ArrayList<String>();
        expexcted.add("");
        assertEquals(expexcted,result);
    }
    @Test
    public final void getPropertyListOfSectorRoot_return_property_value_for_exist_property(){
        Node sector=createNodeWithType(NodeTypes.SECTOR.getId());
        sector.setProperty("exist_property", 12);
        List<String> result = service.getPropertyListOfSectorRoot(sector,NodeTypes.SECTOR.getId(),"exist_property");
        List<String>  expexcted=new ArrayList<String>();
        expexcted.add("12");
        assertEquals(expexcted,result);
    }
    @Test
    public final void getPropertyListOfSectorRoot_return_property_value_for_TRX_property(){
        Node sector=createNodeWithType(NodeTypes.SECTOR.getId());
        Node trx1=createNodeWithType(NodeTypes.TRX.getId());
        trx1.setProperty("property1", 1);
        Node trx2=createNodeWithType(NodeTypes.TRX.getId());
        createRelation(sector,trx1,"CHILD");
        createRelation(sector,trx2,"CHILD");
        
        sector.setProperty("property1", 12);
        List<String> result = service.getPropertyListOfSectorRoot(sector,NodeTypes.TRX.getId(),"property1");
        assertEquals(2,result.size());
        assertTrue(result.contains(""));
        assertTrue(result.contains("1"));
        assertFalse(result.contains("12"));
    }
    

    @AfterClass
    public static final void afterClass(){
        stopDb();
        clearDb();
    }

}
