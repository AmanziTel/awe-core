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
import org.amanzi.neo.loader.ui.page.widgets.impl.ResourceSelectorWidget.IResourceSelectorListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.ResourceSelectorWidget.ResourceType;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectLoaderWidget.ISelectLoaderListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectNetworkNameWidget.ISelectNetworkListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
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

    private WizardFactory() {
        projectModelProvider = LoaderCorePlugin.getInstance().getProjectModelProvider();
        networkModelProvider = LoaderCorePlugin.getInstance().getNetworkModelProvider();
    }

    public static WizardFactory getInstance() {
        return WizardFactoryHolder.instance;
    }

    public SelectNetworkNameWidget getDatasetNameSelectorForNetwork(Composite parent, ISelectNetworkListener listener,
            final boolean isEditable, final boolean isEnabled) {
        return initializeWidget(new SelectNetworkNameWidget(parent, listener, isEditable, isEnabled, projectModelProvider,
                networkModelProvider));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends IConfiguration> SelectLoaderWidget<T> getLoaderSelector(Composite parent, ISelectLoaderListener listener,
            List<ILoader<T, ? >> loaders) {
        return initializeWidget(new SelectLoaderWidget(true, parent, listener, loaders, projectModelProvider));
    }

    public ResourceSelectorWidget getFileSelector(Composite parent, IResourceSelectorListener listener, String... fileExtensions) {
        return initializeWidget(new ResourceSelectorWidget(ResourceType.FILE, parent, listener, projectModelProvider,
                fileExtensions));
    }

    protected static <T extends AbstractPageWidget< ? , ? >> T initializeWidget(final T widget) {
        widget.initializeWidget();
        return widget;
    }

}
