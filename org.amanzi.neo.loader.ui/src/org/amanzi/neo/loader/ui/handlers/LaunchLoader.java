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

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.ui.Loader;
import org.amanzi.neo.loader.ui.wizards.IGraphicInterfaceForLoaders;
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
 * Launcher of loaders
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class LaunchLoader extends AbstractHandler {
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
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.neo.loader.ui.loaders");
        List<IConfigurationElement> loaders = new LinkedList<IConfigurationElement>();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement element = extensions[i];
            String localGuiId = element.getAttribute("gui");
            if (guiId.equals(localGuiId)) {
                loaders.add(element);
            }
        }
//        if (loaders.isEmpty()){
//            return null;
//        }
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
            ILoader< ? extends IDataElement,? extends IConfigurationData> loader = defineLoader(element);
            if (loader != null) {
                ((IGraphicInterfaceForLoaders)wizard).addLoader(loader,element.getChildren("pages"));
            }
        }
        if (wizard instanceof IGraphicInterfaceForLoaders) {
            IGraphicInterfaceForLoaders gui = (IGraphicInterfaceForLoaders)wizard;
//            gui.setLoaders(loaders);
            return gui;
        } else {
            return null;
        }
    }





    /**
     * Define loader.
     *
     * @param element the element
     * @return the i loader
     */
    private ILoader< ? extends IDataElement,? extends IConfigurationData> defineLoader(IConfigurationElement element) {
        Class cl=Loader.class;
        try {
            IParser< ? extends IDataElement, ? extends IConfigurationData> parser=defineParser(element);
            ISaver< ? extends IDataElement> saver=defineSaver(element);
            if (parser!=null&&saver!=null){
                Loader loader = (Loader)cl.newInstance();
                loader.setDescription(element.getAttribute("description"));
                //TODO define additional pages for loaders!
                loader.setParser(parser);
                loader.setSaver(saver);
                ILoaderInputValidator<? extends IConfigurationData>validator=defineValidator(element);
                if (validator!=null){
                    loader.setValidator(validator);
                }
                return loader;
                
            }
        } catch (InstantiationException e) {
            // TODO Handle InstantiationException
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Handle IllegalAccessException
            e.printStackTrace();
        }
        return null;
    }




    /**
     * Define validator.
     *
     * @param element the element
     * @return the i loader input validator
     */
    private ILoaderInputValidator< ? extends IConfigurationData> defineValidator(IConfigurationElement element) {
        String validatorClass = element.getAttribute("validator");
        if (StringUtils.isEmpty(validatorClass)){
            return null;
        }
        
        try {
            return (ILoaderInputValidator< ? extends IConfigurationData>)element.createExecutableExtension(validatorClass);
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
    private ISaver< ? extends IDataElement> defineSaver(IConfigurationElement element) {
        String saverId=element.getAttribute("saver");
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.loader.core.saver");
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement elementSaver = extensions[i];
            if (saverId.equals(elementSaver.getAttribute("id"))){
                try {
                    return (ISaver< ? extends IDataElement>)elementSaver.createExecutableExtension("class");
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
    private IParser< ? extends IDataElement, ? extends IConfigurationData> defineParser(IConfigurationElement element) {
        String parserId=element.getAttribute("parser");
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor("org.amanzi.loader.core.parser");
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement elementParser = extensions[i];
            if (parserId.equals(elementParser.getAttribute("id"))){
                try {
                    return (IParser< ? extends IDataElement, ? extends IConfigurationData>)elementParser.createExecutableExtension("class");
                } catch (CoreException e) {
                    // TODO Handle CoreException
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
