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

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoaderInfo;
import org.amanzi.neo.loader.core.ILoaderNew;
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.loader.core.LoaderInfoImpl;
import org.amanzi.neo.loader.core.newparser.IParser;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.loader.ui.loaders.LoaderNew;
import org.amanzi.neo.loader.ui.wizards.IGraphicInterfaceForLoaders;
import org.amanzi.neo.services.networkModel.IModel;
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
public class LaunchLoaderNew extends AbstractHandler {

    private String guiId;

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
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
    @SuppressWarnings({"rawtypes", "unchecked"})
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
            ILoaderNew<IData, IConfiguration> loader = defineLoader(element);
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    private ILoaderNew<IData, IConfiguration> defineLoader(IConfigurationElement element) {
        try {
            String loaderClass = element.getAttribute("loader_class");
            ILoaderNew loader = null;
            ILoaderInfo loaderInfo = null;
            if (loaderClass != null) {
                String loaderType = element.getAttribute("loader_type");
                String loaderName = element.getAttribute("loader_name");
                String loaderDataType = element.getAttribute("loader_data_type");
                loaderInfo = new LoaderInfoImpl(loaderName, loaderType, loaderDataType);
                loader = (ILoaderNew)element.createExecutableExtension("loader_class");
                loader.setLoaderInfo(loaderInfo);
            } else {
                Class cl = LoaderNew.class;
                loader = (ILoaderNew)cl.newInstance();
            }
            IParser<ISaver<IModel, IData, IConfiguration>, IConfiguration, IData> parser = defineParser(element);
            ISaver<IModel, IData, IConfiguration> saver = defineSaver(element);
            if (parser != null && saver != null) {
                loader.setParser(parser);
                loader.setSaver(saver);
                IValidator validator = defineValidator(element);
                loader.setValidator(validator);
                return loader;

            }
        } catch (InstantiationException e) {
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
     * Define saver.
     * 
     * @param element the element
     * @return the i saver<? extends i data element>
     */
    @SuppressWarnings("unchecked")
    private ISaver<IModel, IData, IConfiguration> defineSaver(IConfigurationElement element) {
        String saverId = element.getAttribute("saver");
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.loader.core.newsaver");
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement elementSaver = extensions[i];
            if (saverId.equals(elementSaver.getAttribute("id"))) {
                try {
                    return (ISaver<IModel, IData, IConfiguration>)elementSaver.createExecutableExtension("class");
                } catch (CoreException e) {
                    // TODO Handle CoreException
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Define parser.
     * 
     * @param element the element
     * @return the i parser<? extends i data element,? extends i configuration data>
     */
    @SuppressWarnings("unchecked")
    private IParser<ISaver<IModel, IData, IConfiguration>, IConfiguration, IData> defineParser(IConfigurationElement element) {
        String parserId = element.getAttribute("parser");
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.loader.core.newparser");
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement elementParser = extensions[i];
            if (parserId.equals(elementParser.getAttribute("id"))) {
                try {
                    return (IParser<ISaver<IModel, IData, IConfiguration>, IConfiguration, IData>)elementParser
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
