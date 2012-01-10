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

package org.amanzi.neo.loader.ui.handlers;

import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.ILoaderInfo;
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.loader.core.LoaderInfoImpl;
import org.amanzi.neo.loader.core.parser.IData;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.ui.loaders.Loader;
import org.amanzi.neo.loader.ui.wizards.IGraphicInterfaceForLoaders;
import org.amanzi.neo.services.model.IModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * <p>
 * Launch required plugin
 * </p>
 * 
 * @author Kondratenko_Vladsialv
 */
public class LaunchLoader extends AbstractHandler {
    private static Logger LOGGER = Logger.getLogger(LaunchLoader.class);
    /*
     * constants
     */
    protected static final String GUI_ID_ATTRIBUTE = "org.amanzi.neo.loader.ui.commands.guiId";
    protected static final String LOADERS_EXTENSION_POINT = "org.amanzi.neo.loader.ui.loaders";
    protected static final String WIZARD_GUI_EXTENSION_POIN = "org.amanzi.neo.loader.ui.wizard";
    protected static final String PAGES_EXTENSION_POIN = "org.amanzi.neo.loader.ui.pages";
    protected static final String SAVER_EXTENSION_POINT = "org.amanzi.loader.core.saver";
    protected static final String PARSER_EXTENSION_POINT = "org.amanzi.loader.core.parser";

    protected static final String GUI_ATTRIBUTE = "gui";
    protected static final String ID_ATTRIBUTE = "id";
    protected static final String CLASS_ATTRIBUTE = "class";
    protected static final String PAGES_ATTRIBUTE = "pages";
    protected static final String PAGE_ID_ATTRIBUTE = "page_id";
    protected static final String LOADER_ID_ATTRIBUTE = "loader_id";

    protected static final String LOADER_CLASS_ATRIBUTE = "loader_class";
    protected static final String LOADER_DATA_TYPE = "loader_data_type";
    protected static final String LOADER_TYPE_ATTRIBUTE = "loader_type";
    protected static final String LOADER_NAME = "loader_name";

    protected static final String VALIDATOR_ATTRIBUTE = "validator";
    protected static final String SAVER_ATTRIBUTE = "saver";
    protected static final String PARSER_ATTRIBUTE = "parser";

    protected String guiId;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        guiId = event.getParameter(GUI_ID_ATTRIBUTE);
        if (StringUtils.isEmpty(guiId)) {
            LOGGER.error("Wizzard id is empty");
        }
        IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor(WIZARD_GUI_EXTENSION_POIN);
        IConfigurationElement wizards = null;
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement element = extensions[i];
            String localGuiId = element.getAttribute(ID_ATTRIBUTE);
            if (guiId.equals(localGuiId)) {
                wizards = element;
            }
        }
        IWorkbenchWizard wizard = getWizardInstance(event, wizards);
        if (wizard != null) {
            wizard.init(workbenchWindow.getWorkbench(), null);
            Shell parent = workbenchWindow.getShell();
            WizardDialog dialog = new WizardDialog(parent, wizard);
            dialog.create();
            dialog.open();
        }
        return null;
    }

    /**
     * Gets the wizard instance.
     * 
     * @param arg0 the arg0
     * @param wizards the elements
     * @return the wizard instance
     */
    private IGraphicInterfaceForLoaders getWizardInstance(ExecutionEvent arg0, IConfigurationElement wizards) {
        Object wizard = null;
        IConfigurationElement[] wizardPages = wizards.getChildren(PAGE_ID_ATTRIBUTE);
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        try {
            
            IConfigurationElement[] registeredGui = reg.getConfigurationElementsFor(WIZARD_GUI_EXTENSION_POIN);
            for (IConfigurationElement gui : registeredGui) {
                if (gui.getAttribute(ID_ATTRIBUTE).equals(wizards.getAttribute(ID_ATTRIBUTE))) {
                    wizard = gui.createExecutableExtension(CLASS_ATTRIBUTE);
                    break;
                }
            }
            IConfigurationElement[] allPages = reg.getConfigurationElementsFor(PAGES_EXTENSION_POIN);
            for (IConfigurationElement wizardPage : wizardPages) {
                for (IConfigurationElement page : allPages) {
                    if (wizardPage.getAttribute(ID_ATTRIBUTE).equals(page.getAttribute(ID_ATTRIBUTE))) {
                        addLoadersToPage(wizard, reg, page);
                    }
                }
            }
        } catch (CoreException e) {
            LOGGER.error("Error while try to get wizzard instance", e);
        }
        if (wizard instanceof IGraphicInterfaceForLoaders) {
            IGraphicInterfaceForLoaders gui = (IGraphicInterfaceForLoaders)wizard;
            // gui.setLoaders(loaders);
            return gui;
        } else {
            return null;
        }
    }

    /**
     * try to find loader declarated in page element
     * 
     * @param wizard
     * @param reg
     * @param page
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addLoadersToPage(Object wizard, IExtensionRegistry reg, IConfigurationElement page) {
        IConfigurationElement[] allLoaders = reg.getConfigurationElementsFor(LOADERS_EXTENSION_POINT);
        IConfigurationElement[] pageLoaders = page.getChildren(LOADER_ID_ATTRIBUTE);
        IGraphicInterfaceForLoaders castedWizard = (IGraphicInterfaceForLoaders)wizard;
        for (IConfigurationElement pageLoader : pageLoaders) {
            for (IConfigurationElement loader : allLoaders) {
                if (loader.getAttribute(ID_ATTRIBUTE).equals(pageLoader.getAttribute(ID_ATTRIBUTE))) {
                    ILoader<IData, IConfiguration> initializedLoader = defineLoader(loader);
                    if (initializedLoader != null) {
                        castedWizard.addLoaderToWizard(initializedLoader, page);
                        break;
                    }

                }
            }
        }
    }

    /**
     * Define loader.
     * 
     * @param element the element
     * @return the loader
     */
    protected ILoader<IData, IConfiguration> defineLoader(IConfigurationElement element) {
        try {
            String loaderClass = element.getAttribute(LOADER_CLASS_ATRIBUTE);

            ILoader<IData, IConfiguration> loader = null;
            ILoaderInfo loaderInfo = null;
            if (loaderClass != null) {
                // String loaderType = element.getAttribute(LOADER_TYPE_ATTRIBUTE);
                String loaderName = element.getAttribute(LOADER_NAME);
                // String loaderDataType = element.getAttribute(LOADER_DATA_TYPE);
                loaderInfo = new LoaderInfoImpl(loaderName, null, null);
                loader = (ILoader<IData, IConfiguration>)element.createExecutableExtension(LOADER_CLASS_ATRIBUTE);
                loader.setLoaderInfo(loaderInfo);
            } else {
                Class cl = Loader.class;
                loader = (ILoader<IData, IConfiguration>)cl.newInstance();
            }
            IParser<ISaver<IModel, ? extends IData, IConfiguration>, IConfiguration, ? extends IData> parser = defineParser(element);
            List<ISaver< ? extends IModel, IData, IConfiguration>> saver = defineSaver(element);
            if (parser != null && saver != null) {
                loader.setParser(parser);
                loader.setSavers(saver);
                IValidator validator = defineValidator(element);
                loader.setValidator(validator);
                return loader;

            }
        } catch (InstantiationException e) {
            LOGGER.error("Cann't instantiate class", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Illigal access exception whyle try to define loader ", e);
        } catch (CoreException e) {
            LOGGER.error("Core exception while try to define loader ", e);
        }
        return null;
    }

    /**
     * Define validator.
     * 
     * @param element the element
     * @return the i loader input validator
     */
    protected IValidator defineValidator(IConfigurationElement element) {
        String validatorClass = element.getAttribute(VALIDATOR_ATTRIBUTE);
        if (StringUtils.isEmpty(validatorClass)) {
            return null;
        }

        try {
            return (IValidator)element.createExecutableExtension(VALIDATOR_ATTRIBUTE);
        } catch (CoreException e) {
            LOGGER.error("Core exception while try to define validator ", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Define savers.
     * 
     * @param element the element
     * @return the i saver<? extends i data element>
     */
    @SuppressWarnings("unchecked")
    protected List<ISaver< ? extends IModel, IData, IConfiguration>> defineSaver(IConfigurationElement element) {
        List<IConfigurationElement> saverElements = new LinkedList<IConfigurationElement>();
        for (IConfigurationElement innerElement : element.getChildren()) {
            if (innerElement.getName().equals(SAVER_ATTRIBUTE)) {
                saverElements.add(innerElement);
            }
        }
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor(SAVER_EXTENSION_POINT);
        List<ISaver< ? extends IModel, IData, IConfiguration>> saverList = new LinkedList<ISaver< ? extends IModel, IData, IConfiguration>>();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement elementSaver = extensions[i];
            for (IConfigurationElement saver : saverElements) {
                String saverId = saver.getAttribute(ID_ATTRIBUTE);
                if (saverId.equals(elementSaver.getAttribute(ID_ATTRIBUTE))) {
                    try {

                        saverList.add((ISaver<IModel, IData, IConfiguration>)elementSaver
                                .createExecutableExtension(CLASS_ATTRIBUTE));
                    } catch (CoreException e) {
                        LOGGER.info("Core exception while try to define saver ", e);
                        return null;
                    }
                }
            }
        }
        if (saverList.isEmpty()) {
            return null;
        } else {
            return saverList;
        }
    }

    /**
     * Define parser.
     * 
     * @param element the element
     * @return the i parser<? extends i data element,? extends i configuration data>
     */
    @SuppressWarnings("unchecked")
    protected IParser<ISaver<IModel, ? extends IData, IConfiguration>, IConfiguration, ? extends IData> defineParser(
            IConfigurationElement element) {
        String parserId = element.getAttribute(PARSER_ATTRIBUTE);
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor(PARSER_EXTENSION_POINT);
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement elementParser = extensions[i];
            if (parserId.equals(elementParser.getAttribute(ID_ATTRIBUTE))) {
                try {
                    return (IParser<ISaver<IModel, ? extends IData, IConfiguration>, IConfiguration, ? extends IData>)elementParser
                            .createExecutableExtension(CLASS_ATTRIBUTE);
                } catch (Exception e) {
                    LOGGER.error("exception while try to define parser ", e);
                    return null;
                }
            }
        }
        return null;
    }

}
