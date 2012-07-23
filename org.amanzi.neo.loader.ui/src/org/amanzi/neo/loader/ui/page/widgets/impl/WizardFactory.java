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

import org.amanzi.neo.loader.core.internal.LoaderCorePlugin;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

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
        private static volatile WizardFactory INSTANCE = new WizardFactory();
    }

    private final IProjectModelProvider projectModelProvider;

    private final INetworkModelProvider networkModelProvider;

    private WizardFactory() {
        projectModelProvider = LoaderCorePlugin.getInstance().getProjectModelProvider();
        networkModelProvider = LoaderCorePlugin.getInstance().getNetworkModelProvider();
    }

    public static WizardFactory getInstance() {
        return WizardFactoryHolder.INSTANCE;
    }

    public SelectNetworkNameWidget getDatasetNameSelectorForNetwork(final ILoaderPage< ? > loaderPage, final boolean isEditable,
            final boolean isEnabled) {
        return initializeWidget(new SelectNetworkNameWidget(loaderPage, isEditable, isEnabled, projectModelProvider,
                networkModelProvider));
    }

    public SelectLoaderWidget getLoaderSelector(final ILoaderPage< ? > loaderPage, final boolean isEnabled) {
        return initializeWidget(new SelectLoaderWidget(isEnabled, loaderPage, projectModelProvider));
    }

    protected static <T extends AbstractPageWidget< ? >> T initializeWidget(final T widget) {
        widget.initializeWidget();
        return widget;
    }

}
