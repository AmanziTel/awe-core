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

package org.amanzi.neo.loader.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.ILoaderNew;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * Dataset import wizard
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class DatasetImportWizard extends AbstractLoaderWizardNew<ConfigurationDataImpl> {
    
    private ConfigurationDataImpl configData;

    @Override
    protected List<IWizardPage> getMainPagesList() {
        List<IWizardPage> result = new ArrayList<IWizardPage>();
        result.add(new LoadDatasetMainPage());
        return result;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(NeoLoaderPluginMessages.TemsImportWizard_PAGE_TITLE);
        super.init(workbench, selection);
    }

    @Override
    public boolean performFinish() {
        if (super.performFinish()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addNewLoader(ILoaderNew<IData, ConfigurationDataImpl> loader, IConfigurationElement[] pageConfigElements) {
        LoaderInfo<ConfigurationDataImpl> info = new LoaderInfo<ConfigurationDataImpl>();
        info.setAdditionalPages(pageConfigElements);
        newloaders.put(loader, info);
        requiredLoaders.put(loader, null);
    }

    @Override
    public ConfigurationDataImpl getNewConfigurationData() {
        if (getNewSelectedLoader() != null && configData != null) {
            requiredLoaders.put(getNewSelectedLoader(), configData);
        }
        if (configData == null) {
            configData = new ConfigurationDataImpl();
        }
        return configData;
    }

}
