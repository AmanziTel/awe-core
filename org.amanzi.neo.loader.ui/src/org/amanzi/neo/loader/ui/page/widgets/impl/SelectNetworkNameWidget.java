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

import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractSelectDatasetNameWidget;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SelectNetworkNameWidget extends AbstractSelectDatasetNameWidget {

    private static final Logger LOGGER = Logger.getLogger(SelectNetworkNameWidget.class);

    private final INetworkModelProvider networkModelProvider;

    /**
     * @param parent
     * @param loaderPage
     * @param isEditable
     * @param isEnabled
     * @param projectModelProvider
     */
    protected SelectNetworkNameWidget(final ILoaderPage< ? > loaderPage, final boolean isEditable, final boolean isEnabled,
            final IProjectModelProvider projectModelProvider, final INetworkModelProvider networkModelProvider) {
        super(loaderPage, isEditable, isEnabled, projectModelProvider);
        this.networkModelProvider = networkModelProvider;
    }

    @Override
    public void fillData() {
        try {
            for (INetworkModel network : networkModelProvider.findAll(getActiveProject())) {
                getWidget().add(network.getName());
            }
        } catch (ModelException e) {
            LOGGER.error("Cannot fill Select Network Name Combobox", e);
        }

        getWidget().setText(StringUtils.EMPTY);
    }
}
