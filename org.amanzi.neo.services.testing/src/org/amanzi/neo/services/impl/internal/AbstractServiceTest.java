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

package org.amanzi.neo.services.impl.internal;

import org.amanzi.testing.AbstractMockitoTest;
import org.junit.After;
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

    protected GraphDatabaseService service;

    private Transaction transaction;

    private boolean isSuccess;

    private boolean isReadOnlyTest;

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

}
