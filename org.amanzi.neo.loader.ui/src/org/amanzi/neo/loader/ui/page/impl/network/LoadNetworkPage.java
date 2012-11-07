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

import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.ResourceSelectorWidget;
import org.amanzi.awe.ui.view.widgets.ResourceSelectorWidget.IResourceSelectorListener;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.impl.SingleFileConfiguration;
import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.impl.internal.AbstractLoaderPage;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectLoaderWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectLoaderWidget.ISelectLoaderListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectNetworkNameWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectNetworkNameWidget.ISelectNetworkListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.WizardFactory;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class LoadNetworkPage extends AbstractLoaderPage<SingleFileConfiguration>
        implements
            ISelectLoaderListener,
            ISelectNetworkListener,
            IResourceSelectorListener {

    private SelectNetworkNameWidget networkNameCombo;

    private SelectLoaderWidget<SingleFileConfiguration> loaderCombo;

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

        networkNameCombo = WizardFactory.getInstance().addDatasetNameSelectorForNetwork(getMainComposite(), this, true, true);
        AWEWidgetFactory.getFactory().addCRSSelectorWidget(this, getMainComposite());

        resourceEditor = AWEWidgetFactory.getFactory().addFileSelector(getMainComposite(), this);

        loaderCombo = WizardFactory.getInstance().addLoaderSelector(getMainComposite(), this, getLoaders());

        update();
    }

    @Override
    public void dispose() {
        networkNameCombo.finishUp();
        loaderCombo.finishUp();
        resourceEditor.finishUp();
    }

    @Override
    public void onLoaderChanged() {
        update();
    }

    @Override
    public void onNetworkChanged() {
        ISingleFileConfiguration configuration = getConfiguration();

        if (networkNameCombo != null) {
            configuration.setDatasetName(networkNameCombo.getText());
        }

        update();
    }

    @Override
    public void onResourceChanged() {
        ISingleFileConfiguration configuration = getConfiguration();

        File file = new File(resourceEditor.getFileName());

        configuration.setFile(file);
        networkNameCombo.setText(FilenameUtils.getBaseName(resourceEditor.getFileName()));

        autodefineLoader();

        if (getCurrentLoader() != null) {
            loaderCombo.setText(getCurrentLoader().getName());
        }
    }

    @Override
    public void setCurrentLoader(final ILoader<SingleFileConfiguration, ? > currentLoader) {
        super.setCurrentLoader(currentLoader);
        loaderCombo.updateData();
    }
}
