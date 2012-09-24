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

package org.amanzi.awe.ui.view.widgets.internal;

import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget.IComboSelectionListener;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDatasetComboWidget<D extends IModel, L extends IComboSelectionListener> extends AbstractComboWidget<D, L> {

    private final IProjectModelProvider projectModelProvider;

    /**
     * @param parent
     * @param label
     */
    protected AbstractDatasetComboWidget(final Composite parent, final L listener, final String label, final IProjectModelProvider projectModelProvider, final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
        this.projectModelProvider = projectModelProvider;
    }

    protected IProjectModel getActiveProject() {
        return projectModelProvider.getActiveProjectModel();
    }

    @Override
    protected String getItemName(final D item) {
        return item.getName();
    }

}
