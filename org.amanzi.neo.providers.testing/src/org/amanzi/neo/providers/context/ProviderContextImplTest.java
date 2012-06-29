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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.nodeproperties.INodeProperties;
import org.amanzi.neo.providers.IModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.neo.providers.IProviderContext.ContextException;
import org.amanzi.neo.services.internal.IService;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Status;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProviderContextImplTest extends AbstractMockitoTest {

    /** String PARAMETERS2 field */
    private static final String PARAMETERS2 = "parameters";

    /** String CLASS field */
    private static final String CLASS = "class";

    private static final String NODEPROPERTIES_EXTENSION_POINT = "org.amanzi.nodeproperties";

    private static final String SERVICE_EXTENSION_POINT = "org.amanzi.services";

    private static final String PROVIDER_EXTENSION_POINT = "org.amanzi.providers";

    private static final String SOME_ID = "some id";

    private static final String SOME_CLASS = String.class.getName();

    private static final String TEST_NODE_PROPERTIES_ID = "test.node.properties";

    private static final String TEST_SERVICE_ID = "test.service";

    private static final String TEST_PROVIDER_ID = "test.provider";

    private static final String[] TEST_IDS = new String[] {"other id 1", "other id 2"};

    private static final String[] SERVICE_PARAMETERS = new String[] {"serviceReference", "nodePropertiesReference"};

    private static final String[] UNKNOWN_SERVICE_PARAMTERS = new String[] {"unkonwn"};

    private ProviderContextImpl context;

    private IExtensionRegistry registry;

    private INodeProperties properties;

    private IConfigurationElement element;

    private IService service;

    private IModelProvider< ? , ? > provider;

    @Before
    public void setUp() {
        registry = mock(IExtensionRegistry.class);

        properties = mock(INodeProperties.class);

        service = mock(IService.class);

        context = new ProviderContextImpl(registry);

        provider = mock(IProjectModelProvider.class);

        GraphDatabaseService graphDb = mock(GraphDatabaseService.class);
        DatabaseManagerFactory.getDatabaseManager().setDatabaseService(graphDb);
    }

    @Test
    public void testCheckActivityOnCreateNodeProperties() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForNodeProperties(TEST_NODE_PROPERTIES_ID);

        when(registry.getConfigurationElementsFor(NODEPROPERTIES_EXTENSION_POINT)).thenReturn(elements);

        context.createNodeProperties(TEST_NODE_PROPERTIES_ID);

        verify(registry).getConfigurationElementsFor(NODEPROPERTIES_EXTENSION_POINT);
        verify(element).createExecutableExtension(CLASS);
    }

    @Test
    public void testCheckResultOnCreateNodeProperties() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForNodeProperties(TEST_NODE_PROPERTIES_ID);

        when(registry.getConfigurationElementsFor(NODEPROPERTIES_EXTENSION_POINT)).thenReturn(elements);

        INodeProperties result = context.createNodeProperties(TEST_NODE_PROPERTIES_ID);

        assertEquals("Unexpected node properties", properties, result);
    }

    @Test(expected = ContextException.class)
    public void testCheckContextExceptionOnUnexistingId() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForNodeProperties(null);

        when(registry.getConfigurationElementsFor(NODEPROPERTIES_EXTENSION_POINT)).thenReturn(elements);

        context.createNodeProperties(TEST_NODE_PROPERTIES_ID);
    }

    @Test(expected = CoreException.class)
    public void testCheckCoreExceptionOnUnderlyingError() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForNodeProperties(TEST_NODE_PROPERTIES_ID);
        when(registry.getConfigurationElementsFor(NODEPROPERTIES_EXTENSION_POINT)).thenReturn(elements);

        doThrow(new CoreException(Status.OK_STATUS)).when(element).createExecutableExtension(CLASS);

        context.createNodeProperties(TEST_NODE_PROPERTIES_ID);
    }

    @Test(expected = ContextException.class)
    public void testCheckClassCastExceptionOnCreateNodeProperties() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForNodeProperties(TEST_NODE_PROPERTIES_ID);
        when(registry.getConfigurationElementsFor(NODEPROPERTIES_EXTENSION_POINT)).thenReturn(elements);

        doThrow(new ClassCastException()).when(element).createExecutableExtension(CLASS);

        context.createNodeProperties(TEST_NODE_PROPERTIES_ID);
    }

    @Test
    public void testCacheOfNodeProperties() throws Exception {
        context = spy(new ProviderContextImpl());

        doReturn(properties).when(context).createNodeProperties(SOME_ID);

        // put to cache
        context.getNodeProperties(SOME_ID);

        // get from cache
        context.getNodeProperties(SOME_ID);
        verify(context, atLeastOnce()).createNodeProperties(SOME_ID);
    }

    @Test
    public void testCacheOnServices() throws Exception {
        context = spy(new ProviderContextImpl());

        doReturn(service).when(context).createService(SOME_ID);

        // put to cache
        context.getService(SOME_ID);

        // get from cache
        context.getService(SOME_ID);
        verify(context, atLeastOnce()).createService(SOME_ID);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnCreateService() throws Exception {
        IConfigurationElement[] subElements = getParameterConfigurationElements(SERVICE_PARAMETERS);
        IConfigurationElement[] parameters = getParameterBlockConfigurationElements(subElements);
        IConfigurationElement[] elements = getConfigurationElementsForService(TEST_SERVICE_ID, parameters);

        context = spy(context);

        when(registry.getConfigurationElementsFor(SERVICE_EXTENSION_POINT)).thenReturn(elements);

        doReturn(service).when(context).getService(SOME_ID);
        doReturn(properties).when(context).getNodeProperties(SOME_ID);
        doReturn(service).when(context).createInstance(any(Class.class), any(Map.class));

        context.createService(TEST_SERVICE_ID);

        verify(registry).getConfigurationElementsFor(SERVICE_EXTENSION_POINT);
        verify(element).getAttribute(CLASS);
        verify(element).getChildren(PARAMETERS2);

        for (IConfigurationElement parameterBlock : parameters) {
            verify(parameterBlock).getChildren();
        }

        for (IConfigurationElement parameter : subElements) {
            verify(parameter).getName();
            verify(parameter).getAttribute("refId");
        }

        verify(context).getNodeProperties(SOME_ID);
        verify(context).getService(SOME_ID);
    }

    @Test(expected = ContextException.class)
    public void testCheckContextExceptionOnUnexistingServiceId() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForService(null, null);

        when(registry.getConfigurationElementsFor(SERVICE_EXTENSION_POINT)).thenReturn(elements);

        context.createService(TEST_SERVICE_ID);
    }

    @Test(expected = ContextException.class)
    public void testCheckGeneralExceptionHandlingForCreateService() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForService(TEST_SERVICE_ID, null);
        when(registry.getConfigurationElementsFor(SERVICE_EXTENSION_POINT)).thenReturn(elements);

        context = spy(context);

        doThrow(new IllegalArgumentException()).when(context).createInstance(element);

        context.createService(TEST_SERVICE_ID);
    }

    @Test(expected = ContextException.class)
    public void testCheckUnkownServiceParameter() throws Exception {
        IConfigurationElement[] subElements = getParameterConfigurationElements(UNKNOWN_SERVICE_PARAMTERS);
        IConfigurationElement[] parameters = getParameterBlockConfigurationElements(subElements);
        getConfigurationElementsForService(TEST_SERVICE_ID, parameters);

        context.createInstance(element);
    }

    @Test
    public void testCheckExistingParameterBlock() throws Exception {
        IConfigurationElement[] subElements = getParameterConfigurationElements(UNKNOWN_SERVICE_PARAMTERS);
        IConfigurationElement[] parameters = getParameterBlockConfigurationElements(subElements);

        context = spy(context);

        doReturn(service).when(context).createInstance(String.class, parameters[0]);

        context.createInstance(String.class, parameters);

        verify(context).createInstance(String.class, parameters[0]);
    }

    @Test
    public void testCheckNonExistingParameterBlock() throws Exception {
        IConfigurationElement[] parameters = new IConfigurationElement[0];

        context = spy(context);

        doReturn(service).when(context).createInstance(String.class, (IConfigurationElement)null);

        context.createInstance(String.class, parameters);

        verify(context).createInstance(String.class, (IConfigurationElement)null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEmptyParametersBlock() throws Exception {
        context = spy(context);

        Map<Class< ? extends Object>, Object> parametersMap = new HashMap<Class< ? extends Object>, Object>();
        parametersMap.put(GraphDatabaseService.class, DatabaseManagerFactory.getDatabaseManager().getDatabaseService());

        doReturn(service).when(context).createInstance(any(Class.class), any(Map.class));

        context.createInstance(String.class, (IConfigurationElement)null);

        verify(context).createInstance(eq(String.class), eq(parametersMap));
    }

    @Test
    public void testCacheOnProviders() throws Exception {
        context = spy(new ProviderContextImpl());

        doReturn(provider).when(context).createModelProvider(SOME_ID);

        // put to cache
        context.get(SOME_ID);

        // get from cache
        context.get(SOME_ID);
        verify(context, atLeastOnce()).createModelProvider(SOME_ID);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnCreateProvider() throws Exception {
        IConfigurationElement[] subElements = getParameterConfigurationElements(SERVICE_PARAMETERS);
        IConfigurationElement[] parameters = getParameterBlockConfigurationElements(subElements);
        IConfigurationElement[] elements = getConfigurationElementsForService(TEST_PROVIDER_ID, parameters);

        context = spy(context);

        when(registry.getConfigurationElementsFor(PROVIDER_EXTENSION_POINT)).thenReturn(elements);

        doReturn(service).when(context).getService(SOME_ID);
        doReturn(properties).when(context).getNodeProperties(SOME_ID);
        doReturn(provider).when(context).createInstance(any(Class.class), any(Map.class));

        context.createModelProvider(TEST_PROVIDER_ID);

        verify(registry).getConfigurationElementsFor(PROVIDER_EXTENSION_POINT);
        verify(element).getAttribute(CLASS);
        verify(element).getChildren(PARAMETERS2);

        for (IConfigurationElement parameterBlock : parameters) {
            verify(parameterBlock).getChildren();
        }

        for (IConfigurationElement parameter : subElements) {
            verify(parameter).getName();
            verify(parameter).getAttribute("refId");
        }

        verify(context).getNodeProperties(SOME_ID);
        verify(context).getService(SOME_ID);
    }

    @Test(expected = ContextException.class)
    public void testCheckContextExceptionOnUnexistingProviderId() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForService(null, null);

        when(registry.getConfigurationElementsFor(PROVIDER_EXTENSION_POINT)).thenReturn(elements);

        context.createModelProvider(TEST_PROVIDER_ID);
    }

    private IConfigurationElement[] getParameterConfigurationElements(String[] names) {
        IConfigurationElement[] subResult = new IConfigurationElement[SERVICE_PARAMETERS.length];

        int i = 0;
        for (String reference : names) {
            IConfigurationElement subElement = mock(IConfigurationElement.class);
            when(subElement.getName()).thenReturn(reference);
            when(subElement.getAttribute("refId")).thenReturn(SOME_ID);

            subResult[i++] = subElement;
        }

        return subResult;
    }

    private IConfigurationElement[] getParameterBlockConfigurationElements(IConfigurationElement[] subElements) {
        IConfigurationElement[] result = new IConfigurationElement[1];

        IConfigurationElement resultElement = mock(IConfigurationElement.class);
        when(resultElement.getChildren()).thenReturn(subElements);

        result[0] = resultElement;

        return result;
    }

    private IConfigurationElement[] getConfigurationElementsForService(String correctId, IConfigurationElement[] parameters) {
        String[] ids = TEST_IDS;
        if (correctId != null) {
            ids = ArrayUtils.add(ids, correctId);
        }

        IConfigurationElement[] result = new IConfigurationElement[ids.length];

        int i = 0;
        for (String id : ids) {
            IConfigurationElement serviceElement = mock(IConfigurationElement.class);

            when(serviceElement.getAttribute(eq("id"))).thenReturn(id);

            result[i++] = serviceElement;

            if (id.equals(correctId)) {
                when(serviceElement.getAttribute(CLASS)).thenReturn(SOME_CLASS);

                when(serviceElement.getChildren(PARAMETERS2)).thenReturn(parameters);

                this.element = serviceElement;
            }
        }

        return result;
    }

    private IConfigurationElement[] getConfigurationElementsForNodeProperties(String correctId) throws CoreException {
        String[] ids = TEST_IDS;
        if (correctId != null) {
            ids = ArrayUtils.add(ids, correctId);
        }

        IConfigurationElement[] result = new IConfigurationElement[ids.length];

        int i = 0;
        for (String id : ids) {
            IConfigurationElement nodePropertiesElement = mock(IConfigurationElement.class);

            when(nodePropertiesElement.getAttribute(eq("id"))).thenReturn(id);

            result[i++] = nodePropertiesElement;

            if (id.equals(correctId)) {
                when(nodePropertiesElement.createExecutableExtension(CLASS)).thenReturn(properties);

                this.element = nodePropertiesElement;
            }
        }

        return result;
    }
}
