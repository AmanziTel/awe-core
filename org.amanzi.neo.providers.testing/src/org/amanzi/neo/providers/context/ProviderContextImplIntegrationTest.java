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

package org.amanzi.neo.providers.context;

import org.amanzi.neo.nodeproperties.INodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.providers.ContextException;
import org.amanzi.neo.providers.context.internal.AnotherTestService;
import org.amanzi.neo.providers.context.internal.MultiConstructorService;
import org.amanzi.neo.providers.context.internal.TestService;
import org.amanzi.neo.providers.impl.ProjectModelProvider;
import org.amanzi.neo.services.internal.IService;
import org.amanzi.testing.AbstractIntegrationTest;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProviderContextImplIntegrationTest extends AbstractIntegrationTest {

    private static final String CORRECT_NODE_PROPERTIES = "org.amanzi.neo.providers.testing.corretNodeProperties";

    private static final String UNEXISTING_NODE_PROPERTIES = "org.amanzi.neo.providers.testing.unexistingClass";

    private static final String UNKOWN_ID = "some id";

    private static final String INCORRECT_CLASS = "org.amanzi.neo.providers.testing.incorrectNodeProperties";

    private static final String TEST_SERVICE_ID = "org.amanzi.neo.providers.testing.TestService";

    private static final String CYCLE_DEPENDENCY_SERVICE_ID = "org.amanzi.service.CycleDependencyService2";

    private static final String CYCLE_DEPENDENCY_PROVIDER_ID = "cycledependencyprovider2";

    private static final String MULTI_CONSTRUCTOR_SERVICE_ID = "org.amanzi.service.MultiConstructorService";

    private static final String INCORRECT_PARAMETERS_SERVICE_ID = "org.amanzi.service.IncorrectParametersService";

    private static final String TEST_PROJECT_MODEL_PROVIDER_ID = "org.amanzi.providers.TestProjectModelProvider";

    private ProviderContextImpl context;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        context = new ProviderContextImpl();
    }

    @Test
    public void testCheckCorrectNodeProperties() throws Exception {
        INodeProperties nodeProperties = context.createNodeProperties(CORRECT_NODE_PROPERTIES);

        assertEquals("Unexpected class", GeneralNodeProperties.class, nodeProperties.getClass());
    }

    @Test(expected = CoreException.class)
    public void testCheckNodePropertiesWithUnexistingClass() throws Exception {
        context.createNodeProperties(UNEXISTING_NODE_PROPERTIES);
    }

    @Test(expected = ContextException.class)
    public void testCheckNodePropertiesWithUnknownId() throws Exception {
        context.createNodeProperties(UNKOWN_ID);
    }

    @Test(expected = ContextException.class)
    public void testCheckNodePropertiesWithIncorrectClass() throws Exception {
        context.createNodeProperties(INCORRECT_CLASS);
    }

    @Test
    public void testCheckResultOfCreateService() throws Exception {
        IService instance = context.createService(TEST_SERVICE_ID);

        assertEquals("Unexpected class of service", TestService.class, instance.getClass());
    }

    @Test(expected = ContextException.class)
    public void testCheckCycleDependencyOnServiceCreation() throws Exception {
        context.createService(CYCLE_DEPENDENCY_SERVICE_ID);
    }

    @Test(expected = ContextException.class)
    public void testCheckCycleDependencyOnProviderCreation() throws Exception {
        context.createService(CYCLE_DEPENDENCY_PROVIDER_ID);
    }

    @Test
    public void testCheckMultiConstructorServiceCreation() throws Exception {
        MultiConstructorService service = (MultiConstructorService)context.createService(MULTI_CONSTRUCTOR_SERVICE_ID);

        assertEquals("unexpected service 1", AnotherTestService.class, service.getService1().getClass());
        assertEquals("unexpected service 2", TestService.class, service.getService2().getClass());
    }

    @Test(expected = ContextException.class)
    public void testCheckIncorrectParametersServiceCreation() throws Exception {
        context.createService(INCORRECT_PARAMETERS_SERVICE_ID);
    }

    @Test
    public void testCheckProviders() throws Exception {
        Object result = context.get(TEST_PROJECT_MODEL_PROVIDER_ID);

        assertEquals("Unexpected result", ProjectModelProvider.class, result.getClass());
    }
}
