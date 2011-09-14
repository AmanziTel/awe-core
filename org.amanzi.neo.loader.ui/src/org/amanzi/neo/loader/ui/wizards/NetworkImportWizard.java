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

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoaderNew;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * Network import wizard page
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkImportWizard extends AbstractLoaderWizard<CommonConfigData> {

    private CommonConfigData data;

    @Override
    protected List<IWizardPage> getMainPagesList() {
        List<IWizardPage> result = new ArrayList<IWizardPage>();
        result.add(new LoadNetworkMainPage());
        return result;
    }

    @Override
    public CommonConfigData getConfigurationData() {
        if (data == null) {
            data = new CommonConfigData();
        }
        return data;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        super.init(workbench, selection);
        setWindowTitle(NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_TITLE);
    }

    @Override
    public boolean performFinish() {
        if (getConfigurationData().getCharsetName() == null) {
            String characterSet = null;
            try {
                characterSet = org.amanzi.neo.loader.ui.NeoLoaderPlugin.getDefault().getPreferenceStore()
                        .getString(DataLoadPreferences.DEFAULT_CHARSET);
            } catch (Exception e) {
                characterSet = null;
            }
            getConfigurationData().setCharsetName(characterSet);
        }
        if (super.performFinish()) {
            NeoServicesUiPlugin.getDefault().getUpdateViewManager()
                    .fireUpdateView(new UpdateDatabaseEvent(UpdateViewEventType.GIS));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addNewLoader(ILoaderNew<IData, IConfiguration> loader, IConfigurationElement[] pageConfigElements) {
        LoaderInfo<CommonConfigData> info = new LoaderInfo<CommonConfigData>();
        info.setAdditionalPages(pageConfigElements);
        newloaders.put(loader, info);
    }

    @Override
    public IConfiguration getNewConfigurationData() {
        return null;
    }
}
