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

package org.amanzi.neo.services.util;

import java.util.Locale;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.testing.AbstractMockitoTest;
import org.amanzi.testing.AbstractTest;
import org.junit.After;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractServiceTest extends AbstractMockitoTest {

    protected enum TestNodeType implements INodeType {
        TEST1, TEST2;

        @Override
        public String getId() {
            return name().toLowerCase(Locale.getDefault());
        }

    }

    private GraphDatabaseService service;

    private Transaction transaction;

    private boolean isSuccess;

    private boolean isReadOnlyTest;

    @BeforeClass
    public static void setUpClass() {
        AbstractTest.setUpClass();
        NodeTypeManager.registerNodeType(TestNodeType.class);
    }

    protected void setUp() {
        service = mock(GraphDatabaseService.class);

        transaction = mock(Transaction.class);

        isSuccess = true;
        isReadOnlyTest = false;

        when(service.beginTx()).thenReturn(transaction);
    }

    @After
    public void tearDown() {
        if (!isReadOnlyTest) {
            if (isSuccess) {
                verify(transaction).success();
            } else {
                verify(transaction).failure();
            }

            verify(transaction).finish();
        }
        verifyNoMoreInteractions(transaction, service);
    }

    protected void setMethodFailure() {
        isSuccess = false;
    }

    protected void setReadOnly() {
        isReadOnlyTest = true;
    }

    protected GraphDatabaseService getService() {
        return service;
    }

}
