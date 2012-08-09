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

package org.amanzi.awe.ui.view.widget;

import java.util.Collection;

import org.amanzi.awe.ui.view.widget.DriveComboWidget.IDriveSelectionListener;
import org.amanzi.awe.ui.view.widget.internal.AbstractComboWidget;
import org.amanzi.awe.ui.view.widget.internal.AbstractDatasetComboWidget;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DriveComboWidget extends AbstractDatasetComboWidget<IDriveModel, IDriveSelectionListener> {

    private static final Logger LOGGER = Logger.getLogger(DriveComboWidget.class);

    public interface IDriveSelectionListener extends AbstractComboWidget.IComboSelectionListener {

        void onDriveModelSelected(IDriveModel model);

    }

    private final IDriveModelProvider driveModelProvider;

    /**
     * @param parent
     * @param label
     * @param projectModelProvider
     */
    protected DriveComboWidget(final Composite parent, final IDriveSelectionListener listener, final String label, final IProjectModelProvider projectModelProvider, final IDriveModelProvider driveModelProvider) {
        super(parent, listener, label, projectModelProvider);
        this.driveModelProvider = driveModelProvider;
    }

    @Override
    protected Collection<IDriveModel> getItems() {
        IProjectModel activeProject = getActiveProject();
        if (activeProject != null) {
            try {
                return driveModelProvider.findAll(activeProject);
            } catch (ModelException e) {
                LOGGER.error("Error on searching for all Drive Models", e);
            }
        }
        return null;
    }

    @Override
    protected void fireListener(final IDriveSelectionListener listener, final IDriveModel selectedItem) {
        listener.onDriveModelSelected(selectedItem);
    }

}
