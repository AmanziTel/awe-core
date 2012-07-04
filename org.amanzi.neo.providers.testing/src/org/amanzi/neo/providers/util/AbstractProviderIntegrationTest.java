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

package org.amanzi.neo.providers.util;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.providers.internal.AbstractProviderPlugin;
import org.amanzi.testing.AbstractIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractProviderIntegrationTest extends AbstractIntegrationTest {

    private static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = new GeneralNodeProperties();

    private static final AbstractProviderPlugin TEST_PROVIDER_PLUGIN = new AbstractProviderPlugin() {

        @Override
        public String getPluginId() {
            return null;
        }

    };

    private boolean isSuccessedTest;

    private Transaction transaction;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        isSuccessedTest = true;
        transaction = getGraphDatabaseService().beginTx();
    }

    @Override
    @After
    public void tearDown() {
        if (isSuccessedTest) {
            transaction.success();
        } else {
            transaction.failure();
        }
        transaction.finish();

        super.tearDown();
    }

    protected AbstractProviderPlugin getProviderPlugin() {
        return TEST_PROVIDER_PLUGIN;
    }

    protected IGeneralNodeProperties getGeneralNodeProperties() {
        return GENERAL_NODE_PROPERTIES;
    }

    protected void setTransactionFailed() {
        isSuccessedTest = false;
    }
}
