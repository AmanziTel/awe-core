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

package org.amanzi.neo.loader.ui.page.widgets.internal;

import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.events.ModifyEvent;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractSelectDatasetNameWidget extends AbstractComboWidget {

    /**
     * @param isEditable
     * @param isEnabled
     * @param loaderPage
     * @param projectModelProvider
     */
    protected AbstractSelectDatasetNameWidget(String labelText, final ILoaderPage< ? > loaderPage, final boolean isEditable,
            final boolean isEnabled, final IProjectModelProvider projectModelProvider) {
        super(labelText, isEditable, isEnabled, loaderPage, projectModelProvider);
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        getLoaderPage().update();
    }

}
