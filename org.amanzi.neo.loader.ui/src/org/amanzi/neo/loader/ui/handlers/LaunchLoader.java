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
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.IData;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.ui.loaders.Loader;
import org.amanzi.neo.loader.ui.wizards.IGraphicInterfaceForLoaders;
import org.amanzi.neo.services.model.IModel;
import org.apache.commons.lang.StringUtils;
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

    private String guiId;

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        // TODO: LN: hard code!
        guiId = arg0.getParameter("org.amanzi.neo.loader.ui.commands.guiId");
        if (StringUtils.isEmpty(guiId)) {
            // TODO add descriptions
            throw new IllegalArgumentException();
        }
        IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(arg0);
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.neo.loader.ui.newloaders");
        List<IConfigurationElement> loaders = new LinkedList<IConfigurationElement>();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement element = extensions[i];
            String localGuiId = element.getAttribute("gui");
            if (guiId.equals(localGuiId)) {
                loaders.add(element);
            }
        }
        IWorkbenchWizard wizard = getWizardInstance(arg0, loaders);
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
     * @param elements the elements
     * @return the wizard instance
     */
    private IGraphicInterfaceForLoaders getWizardInstance(ExecutionEvent arg0, List<IConfigurationElement> elements) {
        Object wizard = null;
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.neo.loader.ui.gui");

        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement element = extensions[i];
            if (guiId.equals(element.getAttribute("id"))) {
                try {
                    wizard = element.createExecutableExtension("class");
                    break;
                } catch (CoreException e) {
                    // TODO Handle CoreException
                    e.printStackTrace();
                    return null;
                }
            }
        }
        for (IConfigurationElement element : elements) {
            ILoader<IData, IConfiguration> loader = defineLoader(element);
            if (loader != null) {
                ((IGraphicInterfaceForLoaders)wizard).addNewLoader(loader, element.getChildren("pages"));
            }
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
     * Define loader.
     * 
     * @param element the element
     * @return the loader
     */
    private ILoader<IData, IConfiguration> defineLoader(IConfigurationElement element) {
        try {
            String loaderClass = element.getAttribute("loader_class");

            ILoader<IData, IConfiguration> loader = null;
            ILoaderInfo loaderInfo = null;
            if (loaderClass != null) {
                String loaderType = element.getAttribute("loader_type");
                String loaderName = element.getAttribute("loader_name");
                String loaderDataType = element.getAttribute("loader_data_type");
                loaderInfo = new LoaderInfoImpl(loaderName, loaderType, loaderDataType);
                loader = (ILoader<IData, IConfiguration>)element.createExecutableExtension("loader_class");
                loader.setLoaderInfo(loaderInfo);
            } else {
                Class cl = Loader.class;
                loader = (ILoader<IData, IConfiguration>)cl.newInstance();
            }
            IParser<ISaver<IModel, ? extends IData, IConfiguration>, IConfiguration, ? extends IData> parser = defineParser(element);
            List<ISaver< ? extends IModel, IData, IConfiguration>> saver = defineSaver(element);
            if (parser != null && saver != null) {
                loader.setParser(parser);
                loader.setSaver(saver);
                IValidator validator = defineValidator(element);
                loader.setValidator(validator);
                return loader;

            }
        } catch (InstantiationException e) {
            // TODO: LN: handle exceptions!!!!!
            // TODO Handle InstantiationException
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Handle IllegalAccessException
            e.printStackTrace();
        } catch (CoreException e) {
            // TODO Handle CoreException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return null;
    }

    /**
     * Define validator.
     * 
     * @param element the element
     * @return the i loader input validator
     */
    private IValidator defineValidator(IConfigurationElement element) {
        String validatorClass = element.getAttribute("validator");
        if (StringUtils.isEmpty(validatorClass)) {
            return null;
        }

        try {
            return (IValidator)element.createExecutableExtension("validator");
        } catch (CoreException e) {
            // TODO Handle IllegalAccessException
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
    private List<ISaver< ? extends IModel, IData, IConfiguration>> defineSaver(IConfigurationElement element) {
        List<IConfigurationElement> saverElements = new LinkedList<IConfigurationElement>();
        for (IConfigurationElement innerElement : element.getChildren()) {
            if (innerElement.getName().equals("saver")) {
                saverElements.add(innerElement);
            }
        }
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.loader.core.newsaver");
        List<ISaver< ? extends IModel, IData, IConfiguration>> saverList = new LinkedList<ISaver< ? extends IModel, IData, IConfiguration>>();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement elementSaver = extensions[i];
            for (IConfigurationElement saver : saverElements) {
                String saverId = saver.getAttribute("id");
                if (saverId.equals(elementSaver.getAttribute("id"))) {
                    try {
                        saverList.add((ISaver<IModel, IData, IConfiguration>)elementSaver.createExecutableExtension("class"));
                    } catch (CoreException e) {
                        // TODO Handle CoreException
                        e.printStackTrace();
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
    private IParser<ISaver<IModel, ? extends IData, IConfiguration>, IConfiguration, ? extends IData> defineParser(
            IConfigurationElement element) {
        String parserId = element.getAttribute("parser");
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.loader.core.newparser");
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement elementParser = extensions[i];
            if (parserId.equals(elementParser.getAttribute("id"))) {
                try {
                    return (IParser<ISaver<IModel, ? extends IData, IConfiguration>, IConfiguration, ? extends IData>)elementParser
                            .createExecutableExtension("class");
                } catch (Exception e) {
                    // TODO Handle CoreException
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
