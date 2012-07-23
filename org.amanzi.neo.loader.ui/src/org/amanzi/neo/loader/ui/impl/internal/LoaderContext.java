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
import java.util.List;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.internal.Loader;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.validator.IValidator;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class LoaderContext {

    private static final Logger LOGGER = Logger.getLogger(LoaderContext.class);

    protected static final String LOADER_WIZARD_EXTENSION_ID = "org.amanzi.loader.wizards";

    protected static final String LOADER_PAGE_EXTENSION_ID = "org.amanzi.loader.pages";

    protected static final String LOADER_EXTENSION_ID = "org.amanzi.loaders";

    protected static final String SAVER_EXTENSION_ID = "org.amanzi.savers";

    protected static final String PARSER_EXTENSION_ID = "org.amanzi.parsers";

    protected static final String VALIDATOR_EXTENSION_ID = "org.amanzi.validators";

    protected static final String ID_ATTRIBUTE = "id";

    protected static final String CLASS_ATTRIBUTE = "class";

    protected static final String WIZARD_ID_ATTRIBUTE = "wizardId";

    protected static final String TITLE_ATTRIBUTE = "title";

    protected static final String DESCRIPTION_ATTRIBUTE = "description";

    protected static final String LOADERS_CHILDREN = "loaders";

    protected static final String REFERENCE_ID = "refId";

    protected static final String PARSER_ATTRIBUTE = "parser";

    protected static final String VALIDATOR_ATTRIBUTE = "validator";

    protected static final String SAVER_CHILDREN = "savers";

    protected static final String SAVER_ATTRIBUTE = "saver";

    protected static final String NAME_ATTRIBUTE = "name";

    private static class LoaderContextHandler {
        private static volatile LoaderContext INSTANCE = new LoaderContext();
    }

    private final IExtensionRegistry registry;

    protected LoaderContext(final IExtensionRegistry registry) {
        this.registry = registry;
    }

    private LoaderContext() {
        this(Platform.getExtensionRegistry());
    }

    public static LoaderContext getInstance() {
        return LoaderContextHandler.INSTANCE;
    }

    public <T extends IConfiguration> ILoaderWizard<T> getLoaderWizard(final String id) {
        try {
            ILoaderWizard<T> result = createLoaderWizard(id);

            if (result != null) {
                List<ILoaderPage<T>> pages = createLoaderPages(id);

                for (ILoaderPage<T> page : pages) {
                    result.addLoaderPage(page);
                }

                return result;
            }
        } catch (CoreException e) {
            LOGGER.error("An exception on initialization LoaderWizard by ID <" + id + ">.", e);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T extends IConfiguration> ILoaderWizard<T> createLoaderWizard(final String id) throws CoreException {
        LOGGER.info("Creating LoaderWizard <" + id + ">.");

        IConfigurationElement wizardElement = findConfgiruationElement(LOADER_WIZARD_EXTENSION_ID, id);

        if (wizardElement != null) {
            ILoaderWizard<T> result = (ILoaderWizard<T>)wizardElement.createExecutableExtension(CLASS_ATTRIBUTE);

            result.setWindowTitle(wizardElement.getAttribute(TITLE_ATTRIBUTE));

            return result;
        } else {
            LOGGER.error("LoaderWizard Configuration Element not found by ID <" + id + ">.");
        }

        return null;
    }

    protected <T extends IConfiguration> List<ILoaderPage<T>> createLoaderPages(final String id) throws CoreException {
        LOGGER.info("Creating LoaderPages for Wizard <" + id + ">.");

        IConfigurationElement[] loaderPageElements = new IConfigurationElement[] {};

        for (IConfigurationElement element : registry.getConfigurationElementsFor(LOADER_PAGE_EXTENSION_ID)) {
            if (id.equals(element.getAttribute(WIZARD_ID_ATTRIBUTE))) {
                loaderPageElements = ArrayUtils.add(loaderPageElements, element);
            }
        }

        return initializeLoaderPages(loaderPageElements);
    }

    protected <T extends IConfiguration> List<ILoaderPage<T>> initializeLoaderPages(final IConfigurationElement[] loaderPageElements)
            throws CoreException {
        List<ILoaderPage<T>> result = new ArrayList<ILoaderPage<T>>();

        for (IConfigurationElement loaderPageElement : loaderPageElements) {
            ILoaderPage<T> page = initializeLoaderPage(loaderPageElement);

            if (page != null) {
                result.add(page);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    protected <T extends IConfiguration> ILoaderPage<T> initializeLoaderPage(final IConfigurationElement loaderPageElement)
            throws CoreException {
        String title = loaderPageElement.getAttribute(TITLE_ATTRIBUTE);
        String description = loaderPageElement.getAttribute(DESCRIPTION_ATTRIBUTE);

        ILoaderPage<T> result = (ILoaderPage<T>)loaderPageElement.createExecutableExtension(CLASS_ATTRIBUTE);

        result.setDescription(description);
        result.setTitle(title);

        for (IConfigurationElement loaderElement : loaderPageElement.getChildren(LOADERS_CHILDREN)) {
            String loaderId = loaderElement.getAttribute(REFERENCE_ID);

            ILoader<T, ? > loader = createLoader(loaderId);

            if (loader != null) {
                result.addLoader(loader);
            } else {
                LOGGER.error("No Loader found by ID <" + loaderId + ">");
            }
        }

        if (result.getLoaders().isEmpty()) {
            LOGGER.error("No Loaders was found for Page");
            return null;
        }

        return result;
    }

    protected <T extends IConfiguration, D extends IData> ILoader<T, D> createLoader(final String loaderId) throws CoreException {
        LOGGER.info("Creating Loader <" + loaderId + ">");

        IConfigurationElement loaderElement = findConfgiruationElement(LOADER_EXTENSION_ID, loaderId);
        ILoader<T, D> result = createLoader();

        String name = loaderElement.getAttribute(NAME_ATTRIBUTE);
        if (!StringUtils.isEmpty(name)) {
            result.setName(name);
        } else {
            LOGGER.error("No name provided for Loader <" + loaderId + ">.");
        }

        String parserId = loaderElement.getAttribute(PARSER_ATTRIBUTE);
        if (!StringUtils.isEmpty(parserId)) {
            IParser<T, D> parser = createParser(parserId);
            if (parser != null) {
                result.setParser(parser);
            } else {
                LOGGER.error("Cannot instantiate parser <" + parserId + ">.");
                return null;
            }
        } else {
            LOGGER.error("No parserId provided for Loader <" + loaderId + ">.");
            return null;
        }

        String validatorId = loaderElement.getAttribute(VALIDATOR_ATTRIBUTE);
        if (!StringUtils.isEmpty(validatorId)) {
            IValidator<T> validator = createValidator(validatorId);
            if (validator != null) {
                result.setValidator(validator);
            } else {
                LOGGER.error("Cannot instantiate validator <" + validatorId + ">.");
                return null;
            }
        } else {
            LOGGER.error("No validatorId provided for Loader <" + loaderId + ">.");
            return null;
        }

        List<ISaver<T, D>> savers = new ArrayList<ISaver<T, D>>();

        for (IConfigurationElement saverElement : loaderElement.getChildren(SAVER_CHILDREN)) {
            String saverId = saverElement.getAttribute(SAVER_ATTRIBUTE);
            if (!StringUtils.isEmpty(saverId)) {
                ISaver<T, D> saver = createSaver(saverId);
                if (saver != null) {
                    savers.add(saver);
                } else {
                    LOGGER.error("Cannot instantiate saver <" + saverId + ">.");
                }
            } else {
                LOGGER.error("No saverId provided for Loader <" + loaderId + ">.");
                return null;
            }
        }

        if (savers.isEmpty()) {
            LOGGER.error("No Savers initialized for Loader <" + loaderId + ">.");
            return null;
        } else {
            for (ISaver<T, D> saver : savers) {
                result.addSaver(saver);
            }
        }

        return result;
    }

    protected <T extends IConfiguration, D extends IData> ILoader<T, D> createLoader() {
        return new Loader<T, D>();
    }

    protected <T extends IConfiguration> IValidator<T> createValidator(final String validatorId) throws CoreException {
        LOGGER.info("Creating Validator <" + validatorId + ">");
        return createElement(VALIDATOR_EXTENSION_ID, validatorId);
    }

    protected <T extends IConfiguration, D extends IData> ISaver<T, D> createSaver(final String saverId) throws CoreException {
        LOGGER.info("Creating Saver <" + saverId + ">");
        return createElement(SAVER_EXTENSION_ID, saverId);
    }

    protected <T extends IConfiguration, D extends IData> IParser<T, D> createParser(final String parserId) throws CoreException {
        LOGGER.info("Creating Parser <" + parserId + ">");
        return createElement(PARSER_EXTENSION_ID, parserId);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Object> T createElement(final String extensionPoint, final String id) throws CoreException {
        IConfigurationElement element = findConfgiruationElement(extensionPoint, id);

        if (element != null) {
            return (T)element.createExecutableExtension(CLASS_ATTRIBUTE);
        }

        return null;
    }

    protected IConfigurationElement findConfgiruationElement(final String extensionPoint, final String id) {
        IConfigurationElement result = null;

        for (IConfigurationElement element : registry.getConfigurationElementsFor(extensionPoint)) {
            if (id.equals(element.getAttribute(ID_ATTRIBUTE))) {
                result = element;
                break;
            }
        }

        return result;
    }
}
