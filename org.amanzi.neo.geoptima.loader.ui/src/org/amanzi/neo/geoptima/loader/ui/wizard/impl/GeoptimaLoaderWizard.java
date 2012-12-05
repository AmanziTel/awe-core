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

package org.amanzi.neo.geoptima.loader.ui.wizard.impl;

import org.amanzi.neo.geoptima.loader.core.IRemoteSupportConfiguration;
import org.amanzi.neo.geoptima.loader.impl.core.RemoteSupportConfiguration;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.wizard.impl.internal.AbstractLoaderWizard;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class GeoptimaLoaderWizard extends AbstractLoaderWizard<IRemoteSupportConfiguration> {
    private IRemoteSupportConfiguration configuration;

    public GeoptimaLoaderWizard() {
        setForcePreviousAndNextButtons(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends IConfiguration> C getConfiguration(final ILoaderPage<C> loaderPage) {
        if (configuration == null) {
            configuration = new RemoteSupportConfiguration();
        }

        return (C)configuration;
    }

    public IRemoteSupportConfiguration getWizardConfiguration() {
        return configuration;
    }

    @Override
    public boolean canFinish() {
        if (getPages().length < 3) {
            return false;
        }
        IWizardPage configPage = getPages()[0].getNextPage();
        IWizardPage filtersPage = configPage.getNextPage();
        return configPage.isPageComplete() && filtersPage.isPageComplete();
    }

}
