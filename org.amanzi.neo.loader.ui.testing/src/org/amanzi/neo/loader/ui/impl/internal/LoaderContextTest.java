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

package org.amanzi.neo.loader.ui.impl.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.validator.IValidator;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Status;
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

    private static final int PAGES_COUNT = 10;

    private static final int CORRECT_PAGE_ELEMENT_NUMBER = 5;

    private static final int WIZARD_COUNT = 5;

    private static final int CORRECT_WIZARD_ELEMENT_NUMBER = 3;

    private static final String DESCRIPTION = "description";

    private static final String TITLE = "title";

    private static final String[] LOADERS_IDS = {"id1", "id2", "id3"};

    private static final String[] SAVER_IDS = {"id1", "id2", "id3"};

    private static final String VALIDATOR_ID = "validator";

    private static final String PARSER_ID = "parser";

    private static final String SAVER_ID = "saver";

    private static final String LOADER_ID = "loader";

    private IExtensionRegistry registry;

    private LoaderContext context;

    private ILoaderWizard<IConfiguration> wizard;

    private ILoaderPage<IConfiguration> page;

    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        registry = mock(IExtensionRegistry.class);

        wizard = mock(ILoaderWizard.class);

        page = mock(ILoaderPage.class);

        context = spy(new LoaderContext(registry));
    }

    @Test
    public void testCheckActivityOnGetLoaderWizard() throws Exception {
        doReturn(wizard).when(context).createLoaderWizard(LOADER_WIZARD_ID);

        List<ILoaderPage<IConfiguration>> loaderPages = getLoaderPages(PAGES_COUNT);
        doReturn(loaderPages).when(context).createLoaderPages(LOADER_WIZARD_ID);

        context.getLoaderWizard(LOADER_WIZARD_ID);

        verify(context).createLoaderWizard(LOADER_WIZARD_ID);
        verify(context).createLoaderPages(LOADER_WIZARD_ID);

        for (ILoaderPage<IConfiguration> page : loaderPages) {
            verify(wizard).addLoaderPage(page);
        }
    }

    @Test
    public void testCheckResultOnGetLoaderWizard() throws Exception {
        doReturn(wizard).when(context).createLoaderWizard(LOADER_WIZARD_ID);

        List<ILoaderPage<IConfiguration>> loaderPages = getLoaderPages(PAGES_COUNT);
        doReturn(loaderPages).when(context).createLoaderPages(LOADER_WIZARD_ID);

        ILoaderWizard<IConfiguration> result = context.getLoaderWizard(LOADER_WIZARD_ID);

        assertEquals("unexpected loader wizard", wizard, result);
    }

    @Test
    public void testCheckActivityAndResultOnGetNoLoaderWizard() throws Exception {
        doReturn(null).when(context).createLoaderWizard(LOADER_WIZARD_ID);

        List<ILoaderPage<IConfiguration>> loaderPages = getLoaderPages(PAGES_COUNT);
        doReturn(loaderPages).when(context).createLoaderPages(LOADER_WIZARD_ID);

        ILoaderWizard<IConfiguration> result = context.getLoaderWizard(LOADER_WIZARD_ID);

        verify(context).createLoaderWizard(LOADER_WIZARD_ID);
        verify(context, never()).createLoaderPages(LOADER_WIZARD_ID);

        for (ILoaderPage<IConfiguration> page : loaderPages) {
            verify(wizard, never()).addLoaderPage(page);
        }

        assertNull("unexpected result", result);
    }

    @Test
    public void testCheckResultOfGetLoaderWizardOnException() throws Exception {
        doReturn(wizard).when(context).createLoaderWizard(LOADER_WIZARD_ID);

        doThrow(new CoreException(Status.CANCEL_STATUS)).when(context).createLoaderWizard(LOADER_WIZARD_ID);

        assertNull("wizard cannot be created", context.getLoaderWizard(LOADER_WIZARD_ID));
    }

    @Test
    public void testCheckActivityOnCreateLoaderWizard() throws Exception {
        IConfigurationElement[] elements = getWizardConfigurationElements(WIZARD_COUNT, LOADER_WIZARD_ID);
        when(registry.getConfigurationElementsFor(LoaderContext.LOADER_WIZARD_EXTENSION_ID)).thenReturn(elements);

        IConfigurationElement element = elements[CORRECT_WIZARD_ELEMENT_NUMBER];
        when(element.createExecutableExtension(LoaderContext.CLASS_ATTRIBUTE)).thenReturn(wizard);

        context.createLoaderWizard(LOADER_WIZARD_ID);

        verify(registry).getConfigurationElementsFor(LoaderContext.LOADER_WIZARD_EXTENSION_ID);

        for (int i = 0; i <= CORRECT_WIZARD_ELEMENT_NUMBER; i++) {
            verify(elements[i]).getAttribute(LoaderContext.ID_ATTRIBUTE);
        }
        for (int i = CORRECT_WIZARD_ELEMENT_NUMBER + 1; i < elements.length; i++) {
            verify(elements[i], never()).getAttribute(LoaderContext.ID_ATTRIBUTE);
        }

        verify(element).createExecutableExtension(LoaderContext.CLASS_ATTRIBUTE);
        verify(element).getAttribute(LoaderContext.TITLE_ATTRIBUTE);
        verify(wizard).setWindowTitle(TITLE);
    }

    @Test
    public void testCheckResultIfNoConfigElementForWizardFound() throws Exception {
        when(registry.getConfigurationElementsFor(LoaderContext.LOADER_WIZARD_EXTENSION_ID)).thenReturn(
                new IConfigurationElement[] {});

        assertNull("loader wizard should be null", context.createLoaderWizard(LOADER_WIZARD_ID));
    }

    @Test
    public void testCheckActivityOnCreateWizardPages() throws Exception {
        IConfigurationElement[] elements = getPagesConfigurationElements(PAGES_COUNT, LOADER_WIZARD_ID);
        when(registry.getConfigurationElementsFor(LoaderContext.LOADER_PAGE_EXTENSION_ID)).thenReturn(elements);

        doReturn(page).when(context).initializeLoaderPage(any(IConfigurationElement.class));

        context.createLoaderPages(LOADER_WIZARD_ID);

        verify(registry).getConfigurationElementsFor(LoaderContext.LOADER_PAGE_EXTENSION_ID);

        for (int i = 0; i < PAGES_COUNT; i++) {
            verify(elements[i]).getAttribute(LoaderContext.WIZARD_ID_ATTRIBUTE);
        }

        IConfigurationElement[] loaderConfigList = {elements[CORRECT_PAGE_ELEMENT_NUMBER]};
        verify(context).initializeLoaderPages(eq(loaderConfigList));
    }

    @Test
    public void testCheckActivityOnInitializationFromPageConfigurationElements() throws Exception {
        IConfigurationElement[] elements = getPagesConfigurationElements(PAGES_COUNT, LOADER_WIZARD_ID);
        when(registry.getConfigurationElementsFor(LoaderContext.LOADER_PAGE_EXTENSION_ID)).thenReturn(elements);

        doReturn(page).when(context).initializeLoaderPage(any(IConfigurationElement.class));

        context.initializeLoaderPages(elements);

        for (IConfigurationElement element : elements) {
            verify(context).initializeLoaderPage(element);
        }
    }

    @Test
    public void testCheckResultOnInitializationFromPageConfigurationElements() throws Exception {
        IConfigurationElement[] elements = getPagesConfigurationElements(PAGES_COUNT, LOADER_WIZARD_ID);
        when(registry.getConfigurationElementsFor(LoaderContext.LOADER_PAGE_EXTENSION_ID)).thenReturn(elements);

        doReturn(page).when(context).initializeLoaderPage(any(IConfigurationElement.class));

        List<ILoaderPage<IConfiguration>> result = context.initializeLoaderPages(elements);

        assertEquals("unexpected size of initialized loader pages", elements.length, result.size());
    }

    @Test
    public void testCheckActivityOnInitializationLoaderPage() throws Exception {
        ILoader<IConfiguration, IData>[] loaders = getLoaders(LOADERS_IDS.length);
        IConfigurationElement loaderPageElement = getLoaderPageElement(TITLE, DESCRIPTION, getLoadersElements(LOADERS_IDS));

        int i = 0;
        for (ILoader<IConfiguration, ? > loader : loaders) {
            doReturn(loader).when(context).createLoader(LOADERS_IDS[i++]);
        }

        when(loaderPageElement.createExecutableExtension(LoaderContext.CLASS_ATTRIBUTE)).thenReturn(page);

        context.initializeLoaderPage(loaderPageElement);

        verify(loaderPageElement).getAttribute(LoaderContext.DESCRIPTION_ATTRIBUTE);
        verify(loaderPageElement).getAttribute(LoaderContext.TITLE_ATTRIBUTE);
        verify(loaderPageElement).getChildren(LoaderContext.LOADERS_CHILDREN);

        verify(page).setTitle(TITLE);
        verify(page).setDescription(DESCRIPTION);

        for (int j = 0; j < LOADERS_IDS.length; j++) {
            String id = LOADERS_IDS[j];
            ILoader<IConfiguration, IData> loader = loaders[j];

            verify(context).createLoader(id);
            verify(page).addLoader(loader);
        }
    }

    @Test
    public void testVerifyNoExceptionsIfLoaderNotFound() throws Exception {
        ILoader<IConfiguration, IData>[] loaders = getLoaders(LOADERS_IDS.length);
        IConfigurationElement loaderPageElement = getLoaderPageElement(TITLE, DESCRIPTION, getLoadersElements(LOADERS_IDS));

        int i = 0;
        for (ILoader<IConfiguration, ? > loader : loaders) {
            doReturn(i == 0 ? null : loader).when(context).createLoader(LOADERS_IDS[i++]);
        }

        when(loaderPageElement.createExecutableExtension(LoaderContext.CLASS_ATTRIBUTE)).thenReturn(page);
        doReturn(Arrays.asList(loaders)).when(page).getLoaders();

        when(loaderPageElement.createExecutableExtension(LoaderContext.CLASS_ATTRIBUTE)).thenReturn(page);

        assertNotNull("result cannot be null", context.initializeLoaderPage(loaderPageElement));
    }

    @Test
    public void testVerifyNoExceptionsIfNoLoaderNotFound() throws Exception {
        IConfigurationElement loaderPageElement = getLoaderPageElement(TITLE, DESCRIPTION,
                getLoadersElements(ArrayUtils.EMPTY_STRING_ARRAY));

        when(loaderPageElement.createExecutableExtension(LoaderContext.CLASS_ATTRIBUTE)).thenReturn(page);

        assertNull("page should be null", context.initializeLoaderPage(loaderPageElement));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnCreateValidator() throws Exception {
        IValidator<IConfiguration> validator = mock(IValidator.class);
        doReturn(validator).when(context).createElement(LoaderContext.VALIDATOR_EXTENSION_ID, VALIDATOR_ID);

        context.createValidator(VALIDATOR_ID);

        verify(context).createElement(LoaderContext.VALIDATOR_EXTENSION_ID, VALIDATOR_ID);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnCreateSaver() throws Exception {
        ISaver<IConfiguration, IData> saver = mock(ISaver.class);
        doReturn(saver).when(context).createElement(LoaderContext.SAVER_EXTENSION_ID, SAVER_ID);

        context.createSaver(SAVER_ID);

        verify(context).createElement(LoaderContext.SAVER_EXTENSION_ID, SAVER_ID);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnCreateParser() throws Exception {
        IParser<IConfiguration, IData> parser = mock(IParser.class);
        doReturn(parser).when(context).createElement(LoaderContext.PARSER_EXTENSION_ID, PARSER_ID);

        context.createParser(PARSER_ID);

        verify(context).createElement(LoaderContext.PARSER_EXTENSION_ID, PARSER_ID);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnCreateLoader() throws Exception {
        ISaver<IConfiguration, IData>[] savers = new ISaver[SAVER_IDS.length];
        IConfigurationElement loaderElement = createSaverElement(SAVER_IDS);

        for (int i = 0; i < SAVER_IDS.length; i++) {
            ISaver<IConfiguration, IData> saver = mock(ISaver.class);

            doReturn(saver).when(context).createSaver(SAVER_IDS[i]);
            savers[i] = saver;
        }

        IParser<IConfiguration, IData> parser = mock(IParser.class);
        doReturn(parser).when(context).createParser(PARSER_ID);

        IValidator<IConfiguration> validator = mock(IValidator.class);
        doReturn(validator).when(context).createValidator(VALIDATOR_ID);

        ILoader<IConfiguration, IData> loader = mock(ILoader.class);
        doReturn(loader).when(context).createLoader();

        doReturn(loaderElement).when(context).findConfgiruationElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        context.createLoader(LOADER_ID);

        verify(loaderElement).getAttribute(LoaderContext.VALIDATOR_ATTRIBUTE);
        verify(loaderElement).getAttribute(LoaderContext.PARSER_ATTRIBUTE);

        verify(loader).setParser(parser);
        verify(loader).setValidator(validator);

        verify(loaderElement).getChildren(LoaderContext.SAVER_CHILDREN);

        for (int i = 0; i < SAVER_IDS.length; i++) {
            verify(context).createSaver(SAVER_IDS[i]);

            verify(loader).addSaver(savers[i]);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckLoaderWithoutValidator() throws Exception {
        doReturn(null).when(context).createValidator(VALIDATOR_ID);

        IParser<IConfiguration, IData> parser = mock(IParser.class);
        doReturn(parser).when(context).createParser(PARSER_ID);

        IConfigurationElement loaderElement = createSaverElement(SAVER_IDS);
        doReturn(loaderElement).when(context).findConfgiruationElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        createSaverElement(SAVER_IDS);

        ILoader<IConfiguration, IData> loader = mock(ILoader.class);

        doReturn(loader).when(context).createElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        assertNull("loader cannot exists without parser", context.createLoader(LOADER_ID));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckLoaderWithoutParser() throws Exception {
        doReturn(null).when(context).createParser(PARSER_ID);

        IValidator<IConfiguration> validator = mock(IValidator.class);
        doReturn(validator).when(context).createValidator(VALIDATOR_ID);

        IConfigurationElement loaderElement = createSaverElement(SAVER_IDS);
        doReturn(loaderElement).when(context).findConfgiruationElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        createSaverElement(SAVER_IDS);

        ILoader<IConfiguration, IData> loader = mock(ILoader.class);

        doReturn(loader).when(context).createElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        assertNull("loader cannot exists without validator", context.createLoader(LOADER_ID));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckLoaderWithoutSavers() throws Exception {
        IParser<IConfiguration, IData> parser = mock(IParser.class);
        doReturn(parser).when(context).createParser(PARSER_ID);

        IValidator<IConfiguration> validator = mock(IValidator.class);
        doReturn(validator).when(context).createValidator(VALIDATOR_ID);

        IConfigurationElement loaderElement = createSaverElement(SAVER_IDS);
        doReturn(loaderElement).when(context).findConfgiruationElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        for (String element : SAVER_IDS) {
            doReturn(null).when(context).createSaver(element);
        }

        ILoader<IConfiguration, IData> loader = mock(ILoader.class);

        doReturn(loader).when(context).createElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        assertNull("loader cannot exists without savers", context.createLoader(LOADER_ID));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckLoaderWhenOneSaverIsNull() throws Exception {
        IParser<IConfiguration, IData> parser = mock(IParser.class);
        doReturn(parser).when(context).createParser(PARSER_ID);

        IValidator<IConfiguration> validator = mock(IValidator.class);
        doReturn(validator).when(context).createValidator(VALIDATOR_ID);

        IConfigurationElement loaderElement = createSaverElement(ArrayUtils.add(SAVER_IDS, SAVER_ID));
        doReturn(loaderElement).when(context).findConfgiruationElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        for (String element : SAVER_IDS) {
            ISaver<IConfiguration, IData> saver = mock(ISaver.class);

            doReturn(saver).when(context).createSaver(element);
        }

        doReturn(null).when(context).createSaver(SAVER_ID);

        ILoader<IConfiguration, IData> loader = mock(ILoader.class);

        doReturn(loader).when(context).createElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        assertNotNull("loader should exists withoutyj one of savers", context.createLoader(LOADER_ID));
    }

    @Test
    public void testCheckFindElementActivity() throws Exception {
        IConfigurationElement[] elements = getWizardConfigurationElements(WIZARD_COUNT, LOADER_WIZARD_ID);

        when(registry.getConfigurationElementsFor(LoaderContext.LOADER_WIZARD_EXTENSION_ID)).thenReturn(elements);

        context.findConfgiruationElement(LoaderContext.LOADER_WIZARD_EXTENSION_ID, LOADER_WIZARD_ID);

        for (int i = 0; i <= CORRECT_WIZARD_ELEMENT_NUMBER; i++) {
            verify(elements[i]).getAttribute(LoaderContext.ID_ATTRIBUTE);
        }
        for (int i = CORRECT_WIZARD_ELEMENT_NUMBER + 1; i < WIZARD_COUNT; i++) {
            verify(elements[i], never()).getAttribute(LoaderContext.ID_ATTRIBUTE);
        }
    }

    @Test
    public void testCheckCreateElementWhenFound() throws Exception {
        IConfigurationElement element = mock(IConfigurationElement.class);
        Object result = new Object();
        when(element.createExecutableExtension(LoaderContext.CLASS_ATTRIBUTE)).thenReturn(result);

        doReturn(element).when(context).findConfgiruationElement(LoaderContext.LOADER_WIZARD_EXTENSION_ID, LOADER_WIZARD_ID);

        Object actualResult = context.createElement(LoaderContext.LOADER_WIZARD_EXTENSION_ID, LOADER_WIZARD_ID);

        verify(context).findConfgiruationElement(LoaderContext.LOADER_WIZARD_EXTENSION_ID, LOADER_WIZARD_ID);
        verify(element).createExecutableExtension(LoaderContext.CLASS_ATTRIBUTE);

        assertEquals("unexpected result", result, actualResult);
    }

    @Test
    public void testCheckCreateElementWhenNotFound() throws Exception {
        doReturn(null).when(context).findConfgiruationElement(LoaderContext.LOADER_WIZARD_EXTENSION_ID, LOADER_WIZARD_ID);

        Object actualResult = context.createElement(LoaderContext.LOADER_WIZARD_EXTENSION_ID, LOADER_WIZARD_ID);

        verify(context).findConfgiruationElement(LoaderContext.LOADER_WIZARD_EXTENSION_ID, LOADER_WIZARD_ID);

        assertNull("result should be null", actualResult);
    }

    private IConfigurationElement createSaverElement(final String[] saverIds) throws Exception {
        IConfigurationElement result = mock(IConfigurationElement.class);

        doReturn(result).when(context).createElement(LoaderContext.LOADER_EXTENSION_ID, LOADER_ID);

        when(result.getAttribute(LoaderContext.VALIDATOR_ATTRIBUTE)).thenReturn(VALIDATOR_ID);
        when(result.getAttribute(LoaderContext.PARSER_ATTRIBUTE)).thenReturn(PARSER_ID);

        IConfigurationElement[] savers = new IConfigurationElement[saverIds.length];
        for (int i = 0; i < savers.length; i++) {
            savers[i] = mock(IConfigurationElement.class);

            when(savers[i].getAttribute(LoaderContext.SAVER_ATTRIBUTE)).thenReturn(saverIds[i]);
        }

        when(result.getChildren(LoaderContext.SAVER_CHILDREN)).thenReturn(savers);

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ILoader<IConfiguration, IData>[] getLoaders(final int count) {
        ILoader[] result = new ILoader[count];

        for (int i = 0; i < count; i++) {
            result[i] = mock(ILoader.class);
        }

        return result;
    }

    private IConfigurationElement[] getLoadersElements(final String[] loaderIds) {
        IConfigurationElement[] result = new IConfigurationElement[loaderIds.length];

        int i = 0;
        for (String id : loaderIds) {
            IConfigurationElement loader = mock(IConfigurationElement.class);

            when(loader.getAttribute(LoaderContext.REFERENCE_ID)).thenReturn(id);

            result[i++] = loader;
        }

        return result;
    }

    private IConfigurationElement getLoaderPageElement(final String title, final String description,
            final IConfigurationElement... loaders) {
        IConfigurationElement result = mock(IConfigurationElement.class);

        when(result.getAttribute(LoaderContext.DESCRIPTION_ATTRIBUTE)).thenReturn(description);
        when(result.getAttribute(LoaderContext.TITLE_ATTRIBUTE)).thenReturn(title);

        when(result.getChildren(LoaderContext.LOADERS_CHILDREN)).thenReturn(loaders);

        return result;
    }

    @SuppressWarnings("unchecked")
    private List<ILoaderPage<IConfiguration>> getLoaderPages(final int number) {
        List<ILoaderPage<IConfiguration>> result = new ArrayList<ILoaderPage<IConfiguration>>();

        for (int i = 0; i < number; i++) {
            result.add(mock(ILoaderPage.class));
        }

        return result;
    }

    private IConfigurationElement[] getPagesConfigurationElements(final int number, final String correctWizardId) {
        IConfigurationElement[] result = new IConfigurationElement[number];
        for (int i = 0; i < number; i++) {
            String id = "some other id";
            if (i == CORRECT_PAGE_ELEMENT_NUMBER) {
                id = correctWizardId;
            }

            IConfigurationElement element = mock(IConfigurationElement.class);
            when(element.getAttribute(LoaderContext.WIZARD_ID_ATTRIBUTE)).thenReturn(id);

            result[i] = element;
        }

        return result;
    }

    private IConfigurationElement[] getWizardConfigurationElements(final int number, final String correctId) {
        IConfigurationElement[] result = new IConfigurationElement[number];
        for (int i = 0; i < number; i++) {
            String id = "some other id";
            if (i == CORRECT_WIZARD_ELEMENT_NUMBER) {
                id = correctId;
            }

            IConfigurationElement element = mock(IConfigurationElement.class);
            when(element.getAttribute(LoaderContext.ID_ATTRIBUTE)).thenReturn(id);
            when(element.getAttribute(LoaderContext.TITLE_ATTRIBUTE)).thenReturn(TITLE);

            result[i] = element;
        }

        return result;
    }
}
