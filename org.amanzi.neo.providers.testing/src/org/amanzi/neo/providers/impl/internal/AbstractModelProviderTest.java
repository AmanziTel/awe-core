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

package org.amanzi.neo.providers.impl.internal;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.DataInconsistencyException;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider.NameKey;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractModelProviderTest extends AbstractMockitoTest {

    private static class TestModelProvider extends AbstractModelProvider<AbstractModel, IModel> {

        private AbstractModel instance;

        public TestModelProvider(AbstractModel instance) {
            this.instance = instance;
        }

        @Override
        protected AbstractModel createInstance() {
            return instance;
        }
    }

    private static final String[] MODEL_NAMES = new String[] {"model1", "model2", "model3"};

    private AbstractModelProvider<AbstractModel, IModel> provider;

    private AbstractModel model;

    private Node node;

    private GraphDatabaseService service;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        service = mock(GraphDatabaseService.class);
        DatabaseManagerFactory.getDatabaseManager().setDatabaseService(service);

        node = getNodeMock();

        model = mock(AbstractModel.class);

        provider = new TestModelProvider(model);
    }

    @Test
    public void testCheckActivityOnCreateFromNode() throws Exception {
        provider.initializeFromNode(node);

        verify(model).initialize(eq(node));
    }

    @Test(expected = DataInconsistencyException.class)
    public void testDataInconsistencyExceptionOnInitializationFromNode() throws Exception {
        doThrow(new DataInconsistencyException(new IllegalArgumentException())).when(model).initialize(node);

        provider.initializeFromNode(node);
    }

    @Test(expected = FatalException.class)
    public void testFatalExceptionOnInitializationFromNode() throws Exception {
        doThrow(new FatalException(new IllegalArgumentException())).when(model).initialize(node);

        provider.initializeFromNode(node);
    }

    @Test
    public void testCheckCache() {
        List<AbstractModel> modelList = new ArrayList<AbstractModel>();

        for (String name : MODEL_NAMES) {
            NameKey key = new NameKey(name);
            AbstractModel model = mock(AbstractModel.class);

            provider.addToCache(model, key);

            modelList.add(model);
        }

        for (int i = 0; i < MODEL_NAMES.length; i++) {
            NameKey key = new NameKey(MODEL_NAMES[i]);

            AbstractModel model = provider.getFromCache(key);

            assertNotNull("Model should exist in cache", model);
            assertEquals("Unexpected model found by key", modelList.get(i), model);
        }
    }

    @Test
    public void testCheckCacheClearedAfterDbStop() {
        for (String name : MODEL_NAMES) {
            NameKey key = new NameKey(name);
            AbstractModel model = mock(AbstractModel.class);

            provider.addToCache(model, key);
        }

        DatabaseManagerFactory.getDatabaseManager().shutdown();

        for (String name : MODEL_NAMES) {
            NameKey key = new NameKey(name);
            assertNull("Cached model should not exists", provider.getFromCache(key));
        }
    }
}
