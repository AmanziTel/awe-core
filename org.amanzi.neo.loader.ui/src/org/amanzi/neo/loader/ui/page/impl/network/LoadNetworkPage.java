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

package org.amanzi.neo.loader.ui.page.impl.network;

import java.io.File;

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.impl.internal.AbstractLoaderPage;
import org.amanzi.neo.loader.ui.page.widgets.impl.ResourceSelectorWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectLoaderWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectNetworkNameWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.WizardFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class LoadNetworkPage extends AbstractLoaderPage<ISingleFileConfiguration> {

    private SelectNetworkNameWidget networkNameCombo;

    private SelectLoaderWidget loaderCombo;

    private ResourceSelectorWidget resourceEditor;

    /**
     * @param pageName
     */
    public LoadNetworkPage() {
        super(Messages.LoadNetworkPage_PageName);
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);

        networkNameCombo = WizardFactory.getInstance().getDatasetNameSelectorForNetwork(this, true, true);

        resourceEditor = WizardFactory.getInstance().getFileSelector(this);

        loaderCombo = WizardFactory.getInstance().getLoaderSelector(this, getLoaders().size() > 1);

        update();
    }

    @Override
    public void autodefineLoader() {
        ISingleFileConfiguration configuration = getConfiguration();

        configuration.setFile(new File(resourceEditor.getFileName()));

        super.autodefineLoader();
    }

    @Override
    public void update() {
        ISingleFileConfiguration configuration = getConfiguration();

        if (networkNameCombo != null) {
            configuration.setDatasetName(networkNameCombo.getText());
        }

        super.update();
    }

    @Override
    public void setCurrentLoader(final ILoader<ISingleFileConfiguration, ? > currentLoader) {
        super.setCurrentLoader(currentLoader);
        loaderCombo.updateData();
    }
}
