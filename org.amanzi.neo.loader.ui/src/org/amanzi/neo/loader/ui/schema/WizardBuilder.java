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

package org.amanzi.neo.loader.ui.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.ui.loaders.ILoader;
import org.amanzi.neo.loader.ui.validators.IValidator;
import org.amanzi.neo.loader.ui.wizards.AbstractLoaderPage;
import org.amanzi.neo.loader.ui.wizards.AbstractLoaderWizard;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public final class WizardBuilder {

    private static final String WIZARDS_EXTENSION_POINT = "org.amanzi.loader.wizards";

    private static final String WIZARD_PAGES_EXTENSION_POINT = "org.amanzi.loader.pages";
    
    private static final String LOADERS_EXTENSION_POINT = "org.amanzi.loaders";
    
    private static final String VALIDATORS_EXTENSION_POINT = "org.amanzi.loader.validators";
    
    private static final String PARSERS_EXTENSION_POINT = "org.amanzi.loader.parsers";
    
    private static final String SAVERS_EXTENSION_POINT = "org.amanzi.loader.savers";

    private static final String ID_ATTRIBUTE = "id";

    private static final String CLASS_ATTRIBUTE = "class";

    private static final String WIZARD_ID_ATTRIBUTE = "wizard";

    private static final String TITLE_ATTRIBUTE = "title";

    private static final String IS_MAIN_ATTRIBUTE = "isMain";

    private static final String PRIORITY_ATTRIBUTE = "priority";

    private static final String DESCRIPTION_ATTRIBUTE = "description";

    private static final String PAGE_ID_ELEMENT = "page";
    
    private static final String NAME_ATTRIBUTE = "name";
    
    private static final String VALIDATOR_ATTRIBUTE = "validator";
    
    private static final String PARSER_ATTRIBUTE = "parser";
    
    private static final String SAVERS_CHILDREN = "savers";
    
    private static final String SAVER_ATTRIBUTE = "saver";
    
    private static final String START_ELEMENT_ATTRIBUTE = "startElement";
    
    private static final String ALL_ELEMENTS_FOR_ATTRIBUTE = "allElementsFor";

    private final static Comparator<AbstractLoaderPage< ? >> PAGE_COMPARATOR = new Comparator<AbstractLoaderPage< ? >>() {

        @Override
        public int compare(AbstractLoaderPage< ? > o1, AbstractLoaderPage< ? > o2) {
            if (o1.isMain() == o2.isMain()) {
                return Integer.valueOf(o2.getPriority()).compareTo(Integer.valueOf(o1.getPriority()));
            }

            return Boolean.valueOf(o2.isMain()).compareTo(Boolean.valueOf(o1.isMain()));
        }

    };

    private static WizardBuilder instance;

    private IExtensionRegistry registry;

    private WizardBuilder() {
        registry = Platform.getExtensionRegistry();
    }

    public static WizardBuilder getBuilder() {
        if (instance == null) {
            instance = new WizardBuilder();
        }

        return instance;
    }

    public AbstractLoaderWizard getWizard(String wizardId) throws CoreException {
        AbstractLoaderWizard result = findWizard(wizardId);

        if (result != null) {
            // initialize wizard with pages
            List<AbstractLoaderPage< ? >> pages = findPages(wizardId);

            // sort by isMain (as first condition) and priority (as second condition)
            Collections.sort(pages, PAGE_COMPARATOR);

            for (IWizardPage singlePage : pages) {
                result.addPage(singlePage);
            }
            
            result.initAdditionPages();

        }

        return result;
    }

    private List<AbstractLoaderPage< ? >> findPages(String wizardId) throws CoreException {
        IConfigurationElement[] pageExtensions = registry.getConfigurationElementsFor(WIZARD_PAGES_EXTENSION_POINT);

        List<AbstractLoaderPage< ? >> result = new ArrayList<AbstractLoaderPage< ? >>();
        for (IConfigurationElement singlePage : pageExtensions) {
            if (singlePage.getAttribute(WIZARD_ID_ATTRIBUTE).equals(wizardId)) {
                result.add(createPage(singlePage));
            }
        }

        return result;
    }

    private AbstractLoaderPage< ? > createPage(IConfigurationElement pageElement) throws CoreException {
        AbstractLoaderPage< ? > result = (AbstractLoaderPage< ? >)pageElement.createExecutableExtension(CLASS_ATTRIBUTE);

        // title
        result.setTitle(pageElement.getAttribute(TITLE_ATTRIBUTE));
        // description
        result.setDescription(pageElement.getAttribute(DESCRIPTION_ATTRIBUTE));
        // isMain
        result.setMain(Boolean.parseBoolean(pageElement.getAttribute(IS_MAIN_ATTRIBUTE)));
        // priority
        result.setPriority(Integer.parseInt(pageElement.getAttribute(PRIORITY_ATTRIBUTE)));

        // initialize page with loaders
        for (ILoader singleLoader : findLoaders(pageElement.getAttribute(ID_ATTRIBUTE))) {
            result.addLoader(singleLoader);
        }

        return result;
    }

    private List<ILoader> findLoaders(String pageId) throws CoreException {
        IConfigurationElement[] allLoaders = registry.getConfigurationElementsFor(LOADERS_EXTENSION_POINT);
        
        List<ILoader> result = new ArrayList<ILoader>();
        
        for (IConfigurationElement loaderElement : allLoaders) {
            if (loaderElement.getAttribute(PAGE_ID_ELEMENT).equals(pageId)) {
                result.add(createLoader(loaderElement));
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("rawtypes")
    private ILoader createLoader(IConfigurationElement loaderElement) throws CoreException {
        ILoader result = (ILoader)loaderElement.createExecutableExtension(CLASS_ATTRIBUTE);
        
        result.setName(loaderElement.getAttribute(NAME_ATTRIBUTE));
        
        IValidator validator = createSimpleElement(loaderElement.getAttribute(VALIDATOR_ATTRIBUTE), VALIDATORS_EXTENSION_POINT);
        result.setValidator(validator);
        
        IParser parser = createSimpleElement(loaderElement.getAttribute(PARSER_ATTRIBUTE), PARSERS_EXTENSION_POINT);
        result.setParser(parser);
        
        List<ISaver> savers = new ArrayList<ISaver>();
        for (IConfigurationElement saverElement : loaderElement.getChildren(SAVERS_CHILDREN)) {
            String saverId = saverElement.getAttribute(SAVER_ATTRIBUTE);
            
            ISaver saver = createSaver(saverId);
            
            savers.add(saver);
        }
        result.setSavers(savers);
        
        return result;
    }
    
    private ISaver createSaver(String saverId) throws CoreException {
        IConfigurationElement[] allSavers = registry.getConfigurationElementsFor(SAVERS_EXTENSION_POINT);
        
        for (IConfigurationElement singleSaver : allSavers) {
            if (singleSaver.getAttribute(ID_ATTRIBUTE).equals(saverId)) {
                ISaver result = (ISaver)singleSaver.createExecutableExtension(CLASS_ATTRIBUTE);
                
                String startElement = singleSaver.getAttribute(START_ELEMENT_ATTRIBUTE);
                String allElementsFor = singleSaver.getAttribute(ALL_ELEMENTS_FOR_ATTRIBUTE);
                result.setAllElementsFor(allElementsFor);
                result.setStartElement(startElement);
                
                return result;
            }
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private <T> T createSimpleElement(String elementId, String extensionPoint) throws CoreException {
        IConfigurationElement[] allElements = registry.getConfigurationElementsFor(extensionPoint);
        
        for (IConfigurationElement singleElement : allElements) {
            if (singleElement.getAttribute(ID_ATTRIBUTE).equals(elementId)) {
                return (T)singleElement.createExecutableExtension(CLASS_ATTRIBUTE);
            }
        }
        
        return null;
    }

    private AbstractLoaderWizard findWizard(String wizardId) throws CoreException {
        IConfigurationElement[] wizardExtensions = registry.getConfigurationElementsFor(WIZARDS_EXTENSION_POINT);
        IConfigurationElement wizardElement = null;

        for (IConfigurationElement singleElement : wizardExtensions) {
            String elementId = singleElement.getAttribute(ID_ATTRIBUTE);

            if (elementId.equals(wizardId)) {
                wizardElement = singleElement;
                break;
            }
        }

        if (wizardElement != null) {
            AbstractLoaderWizard wizard = (AbstractLoaderWizard)wizardElement.createExecutableExtension(CLASS_ATTRIBUTE);
            
            wizard.setWindowTitle(wizardElement.getAttribute(TITLE_ATTRIBUTE));
            
            return wizard;
        }

        return null;
    }

}
