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

package org.amanzi.awe.ui.view.widgets;

import java.util.Collection;

import org.amanzi.awe.ui.view.widgets.NetworkComboWidget.INetworkSelectionListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractDatasetComboWidget;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkComboWidget extends AbstractDatasetComboWidget<INetworkModel, INetworkSelectionListener> {

    public interface INetworkSelectionListener extends AbstractComboWidget.IComboSelectionListener {

        void onNetworkModelSelected(INetworkModel model);

    }

    private static final Logger LOGGER = Logger.getLogger(DriveComboWidget.class);

    private final INetworkModelProvider networkModelProvider;

    /**
     * @param parent
     * @param label
     * @param projectModelProvider
     */
    protected NetworkComboWidget(final Composite parent, final INetworkSelectionListener listener, final String label,
            final IProjectModelProvider projectModelProvider, final INetworkModelProvider networkModelProvider,
            final int minimalLabelWidth) {
        super(parent, listener, label, projectModelProvider, minimalLabelWidth);
        this.networkModelProvider = networkModelProvider;
    }

    @Override
    protected void fireListener(final INetworkSelectionListener listener, final INetworkModel selectedItem) {
        listener.onNetworkModelSelected(selectedItem);
    }

    @Override
    protected Collection<INetworkModel> getItems() {
        IProjectModel activeProject = getActiveProject();
        if (activeProject != null) {
            try {
                return networkModelProvider.findAll(activeProject);
            } catch (ModelException e) {
                LOGGER.error("Error on searching for all Drive Models", e);
            }
        }
        return null;
    }
}
