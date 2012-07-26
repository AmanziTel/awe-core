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
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveNameWidget.ISelectDriveListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractSelectDatasetNameWidget;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.providers.IDriveModelProvider;
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
public class SelectDriveNameWidget extends AbstractSelectDatasetNameWidget<ISelectDriveListener> {

    private static final Logger LOGGER = Logger.getLogger(SelectDriveNameWidget.class);

    public interface ISelectDriveListener extends AbstractPageWidget.IPageEventListener {
        void onDriveChanged();
    }

    private final IDriveModelProvider driveModelProvider;

    /**
     * @param labelText
     * @param parent
     * @param listener
     * @param isEditable
     * @param isEnabled
     * @param projectModelProvider
     */
    public SelectDriveNameWidget(final Composite parent, final ISelectDriveListener listener,
            final IProjectModelProvider projectModelProvider, final IDriveModelProvider driveModelProvider) {
        super(Messages.SelectDriveNameWidget_Label, parent, listener, true, true, projectModelProvider);
        this.driveModelProvider = driveModelProvider;
    }

    @Override
    public void fillData() {
        try {
            for (IDriveModel network : driveModelProvider.findAll(getActiveProject())) {
                getWidget().add(network.getName());
            }
        } catch (ModelException e) {
            LOGGER.error("Cannot fill Select Drive Name Combobox", e); //$NON-NLS-1$
        }

        getWidget().setText(StringUtils.EMPTY);
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        for (ISelectDriveListener listener : getListeners()) {
            listener.onDriveChanged();
        }
    }

}
