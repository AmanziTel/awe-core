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
import org.amanzi.neo.providers.IProviderContext.ContextException;
import org.amanzi.testing.AbstractMockitoTest;
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
public class ProviderContextImplIntegrationTest extends AbstractMockitoTest {

    private static final String CORRECT_NODE_PROPERTIES = "org.amanzi.neo.providers.testing.corretNodeProperties";

    private static final String UNEXISTING_NODE_PROPERTIES = "org.amanzi.neo.providers.testing.unexistingClass";

    private static final String UNKOWN_ID = "some id";

    private static final String INCORRECT_CLASS = "org.amanzi.neo.providers.testing.incorrectNodeProperties";

    private ProviderContextImpl context;

    @Before
    public void setUp() {
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
    public void testCacheOfNodeProperties() throws Exception {
        context = spy(context);

        doReturn(new GeneralNodeProperties()).when(context).createNodeProperties("some id");

        // put to cache
        context.getNodeProperties("some id");

        // get from cache
        context.getNodeProperties("some id");
        verify(context, atLeastOnce()).createNodeProperties("some id");
    }
}
