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

package org.amanzi.neo.loader.ui.page.widgets.impl;

import java.util.List;

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.internal.LoaderCorePlugin;
import org.amanzi.neo.loader.ui.page.widgets.impl.CRSSelector.ICRSSelectorListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.ResourceSelectorWidget.IResourceSelectorListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.ResourceSelectorWidget.ResourceType;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveNameWidget.ISelectDriveListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget.ISelectDriveResourceListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectLoaderWidget.ISelectLoaderListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectNetworkNameWidget.ISelectNetworkListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class WizardFactory {

    private static class WizardFactoryHolder {
        private static volatile WizardFactory instance = new WizardFactory();
    }

    private final IProjectModelProvider projectModelProvider;

    private final INetworkModelProvider networkModelProvider;

    private final IDriveModelProvider driveModelProvider;

    private WizardFactory() {
        projectModelProvider = LoaderCorePlugin.getInstance().getProjectModelProvider();
        networkModelProvider = LoaderCorePlugin.getInstance().getNetworkModelProvider();
        driveModelProvider = LoaderCorePlugin.getInstance().getDriveModelProvider();
    }

    public static WizardFactory getInstance() {
        return WizardFactoryHolder.instance;
    }

    public SelectNetworkNameWidget addDatasetNameSelectorForNetwork(final Composite parent, final ISelectNetworkListener listener,
            final boolean isEditable, final boolean isEnabled) {
        return initializeWidget(new SelectNetworkNameWidget(parent, listener, isEditable, isEnabled, projectModelProvider,
                networkModelProvider));
    }

    public SelectDriveNameWidget getDatasetNameSelectorForDrive(final Composite parent, final ISelectDriveListener listener) {
        return initializeWidget(new SelectDriveNameWidget(parent, listener, projectModelProvider, driveModelProvider));
    }

    public SelectDriveResourcesWidget getDriveResourceSelector(final Composite parent, final ISelectDriveResourceListener listener) {
        return initializeWidget(new SelectDriveResourcesWidget(parent, listener));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends IConfiguration> SelectLoaderWidget<T> addLoaderSelector(final Composite parent,
            final ISelectLoaderListener listener, final List<ILoader<T, ? >> loaders) {
        return initializeWidget(new SelectLoaderWidget(true, parent, listener, loaders, projectModelProvider));
    }

    public ResourceSelectorWidget getFileSelector(final Composite parent, final IResourceSelectorListener listener,
            int numberOfControls, final String... fileExtensions) {
        return initializeWidget(new ResourceSelectorWidget(ResourceType.FILE, parent, listener, projectModelProvider,
                numberOfControls, fileExtensions));
    }

    public ResourceSelectorWidget getDirectorySelector(final Composite parent, final IResourceSelectorListener listener,
            int numberOfControls) {
        return initializeWidget(new ResourceSelectorWidget(ResourceType.DIRECTORY, parent, listener, projectModelProvider,
                numberOfControls));
    }

    public CRSSelector addCRSSelector(final Composite parent, final ICRSSelectorListener listener) {
        return initializeWidget(new CRSSelector(parent, listener));
    }

    protected static <T extends AbstractPageWidget< ? , ? >> T initializeWidget(final T widget) {
        widget.initializeWidget();
        return widget;
    }

}
