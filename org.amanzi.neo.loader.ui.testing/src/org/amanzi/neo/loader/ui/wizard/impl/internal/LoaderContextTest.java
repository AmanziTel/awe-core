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

package org.amanzi.neo.loader.ui.wizard.impl.internal;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.internal.Loader;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.amanzi.testing.AbstractMockitoTest;
import org.eclipse.core.runtime.IExtensionRegistry;
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
public class LoaderContextTest extends AbstractMockitoTest {

    private static final String LOADER_WIZARD_ID = "some loader wizard id";

    private static final int PAGES_COUNT = 3;

    private IExtensionRegistry registry;

    private LoaderContext context;

    private Loader<IConfiguration, IData> loader;

    private ILoaderWizard wizard;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        registry = mock(IExtensionRegistry.class);

        wizard = mock(ILoaderWizard.class);

        context = spy(new LoaderContext(registry));
    }

    @Test
    public void testCheckActivityOnGetLoaderWizard() {
        doReturn(wizard).when(context).createLoaderWizard(LOADER_WIZARD_ID);

        List<ILoaderPage> loaderPages = getLoaderPages(PAGES_COUNT);
        doReturn(loaderPages).when(context).createLoaderPages(LOADER_WIZARD_ID);

        context.getLoaderWizard(LOADER_WIZARD_ID);

        verify(context).createLoaderWizard(LOADER_WIZARD_ID);
        verify(context).createLoaderPages(LOADER_WIZARD_ID);

        for (ILoaderPage page : loaderPages) {
            verify(wizard).addLoaderPage(page);
        }
    }

    @Test
    public void testCheckResultOnGetLoaderWizard() {
        doReturn(wizard).when(context).createLoaderWizard(LOADER_WIZARD_ID);

        List<ILoaderPage> loaderPages = getLoaderPages(PAGES_COUNT);
        doReturn(loaderPages).when(context).createLoaderPages(LOADER_WIZARD_ID);

        ILoaderWizard result = context.getLoaderWizard(LOADER_WIZARD_ID);

        assertEquals("unexpected loader wizard", wizard, result);
    }

    private List<ILoaderPage> getLoaderPages(final int number) {
        List<ILoaderPage> result = new ArrayList<ILoaderPage>();

        for (int i = 0; i < number; i++) {
            result.add(mock(ILoaderPage.class));
        }

        return result;
    }
}
