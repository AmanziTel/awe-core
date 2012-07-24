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

import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectNetworkNameWidget.ISelectNetworkListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget.IAbstractPageEventListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractSelectDatasetNameWidget;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SelectNetworkNameWidget extends AbstractSelectDatasetNameWidget<ISelectNetworkListener> {

    public interface ISelectNetworkListener extends IAbstractPageEventListener {
        void onNetworkChanged();
    }

    private static final Logger LOGGER = Logger.getLogger(SelectNetworkNameWidget.class);

    private final INetworkModelProvider networkModelProvider;

    /**
     * @param parent
     * @param loaderPage
     * @param isEditable
     * @param isEnabled
     * @param projectModelProvider
     */
    protected SelectNetworkNameWidget(final Composite parent, ISelectNetworkListener listener, final boolean isEditable,
            final boolean isEnabled, final IProjectModelProvider projectModelProvider,
            final INetworkModelProvider networkModelProvider) {
        super(Messages.SelectNetworkNameWidget_Label, parent, listener, isEditable, isEnabled, projectModelProvider);
        this.networkModelProvider = networkModelProvider;
    }

    @Override
    public void fillData() {
        try {
            for (INetworkModel network : networkModelProvider.findAll(getActiveProject())) {
                getWidget().add(network.getName());
            }
        } catch (ModelException e) {
            LOGGER.error("Cannot fill Select Network Name Combobox", e); //$NON-NLS-1$
        }

        getWidget().setText(StringUtils.EMPTY);
    }

    public void setText(String text) {
        getWidget().setText(text);
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        for (ISelectNetworkListener listener : getListeners()) {
            listener.onNetworkChanged();
        }
    }
}
