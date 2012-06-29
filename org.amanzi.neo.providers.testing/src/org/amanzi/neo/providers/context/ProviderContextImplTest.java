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
import org.amanzi.neo.providers.IProviderContext.ContextException;
import org.amanzi.neo.services.internal.IService;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Status;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProviderContextImplTest extends AbstractMockitoTest {

    private static final String NODEPROPERTIES_EXTENSION_POINT = "org.amanzi.nodeproperties";

    private static final String SERVICE_EXTENSION_POINT = "org.amanzi.services";

    private static final String SOME_ID = "some id";

    private static final String SOME_CLASS = "some class";

    private static final String TEST_NODE_PROPERTIES_ID = "test.node.properties";

    private static final String TEST_SERVICE_ID = "test.service";

    private static final String[] TEST_IDS = new String[] {"other id 1", "other id 2"};

    private ProviderContextImpl context;

    private IExtensionRegistry registry;

    private INodeProperties properties;

    private IConfigurationElement element;

    private IService service;

    @Before
    public void setUp() {
        registry = mock(IExtensionRegistry.class);

        properties = mock(INodeProperties.class);

        service = mock(IService.class);

        context = new ProviderContextImpl(registry);
    }

    @Test
    public void testCheckActivityOnCreateNodeProperties() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForNodeProperties(TEST_NODE_PROPERTIES_ID);

        when(registry.getConfigurationElementsFor(NODEPROPERTIES_EXTENSION_POINT)).thenReturn(elements);

        context.createNodeProperties(TEST_NODE_PROPERTIES_ID);

        verify(registry).getConfigurationElementsFor(NODEPROPERTIES_EXTENSION_POINT);
        verify(element).createExecutableExtension("class");
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

        doThrow(new CoreException(Status.OK_STATUS)).when(element).createExecutableExtension("class");

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
    public void testCachOnServices() throws Exception {
        context = spy(new ProviderContextImpl());

        doReturn(service).when(context).createService(SOME_ID);

        // put to cache
        context.getService(SOME_ID);

        // get from cache
        context.getService(SOME_ID);
        verify(context, atLeastOnce()).createService(SOME_ID);
    }

    @Test
    @Ignore
    public void testCheckActivityOnCreateService() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForService(TEST_SERVICE_ID);

        when(registry.getConfigurationElementsFor(SERVICE_EXTENSION_POINT)).thenReturn(elements);

        context.createService(TEST_SERVICE_ID);

        verify(registry).getConfigurationElementsFor(SERVICE_EXTENSION_POINT);
        verify(element).getAttribute("class");
    }

    @Test(expected = ContextException.class)
    public void testCheckContextExceptionOnUnexistingServiceId() throws Exception {
        IConfigurationElement[] elements = getConfigurationElementsForService(null);

        when(registry.getConfigurationElementsFor(SERVICE_EXTENSION_POINT)).thenReturn(elements);

        context.createService(TEST_SERVICE_ID);
    }

    private IConfigurationElement[] getConfigurationElementsForService(String correctId) throws Exception {
        String[] ids = TEST_IDS;
        if (correctId != null) {
            ids = ArrayUtils.add(ids, correctId);
        }

        IConfigurationElement[] result = new IConfigurationElement[ids.length];

        int i = 0;
        for (String id : ids) {
            IConfigurationElement element = mock(IConfigurationElement.class);

            when(element.getAttribute(eq("id"))).thenReturn(id);

            result[i++] = element;

            if (id.equals(correctId)) {
                when(element.getAttribute("class")).thenReturn(SOME_CLASS);

                when(element.getChildren("parameters")).thenReturn(null);

                this.element = element;
            }
        }

        return result;
    }

    private IConfigurationElement[] getConfigurationElementsForNodeProperties(String correctId) throws Exception {
        String[] ids = TEST_IDS;
        if (correctId != null) {
            ids = ArrayUtils.add(ids, correctId);
        }

        IConfigurationElement[] result = new IConfigurationElement[ids.length];

        int i = 0;
        for (String id : ids) {
            IConfigurationElement element = mock(IConfigurationElement.class);

            when(element.getAttribute(eq("id"))).thenReturn(id);

            result[i++] = element;

            if (id.equals(correctId)) {
                when(element.createExecutableExtension("class")).thenReturn(properties);

                this.element = element;
            }
        }

        return result;
    }
}
