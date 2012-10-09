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

package org.amanzi.awe.views.properties.views;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.ui.views.IAWEView;
import org.amanzi.awe.views.properties.views.internal.DataElementPropertySourceProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AWEPropertySheet extends PropertySheetPage {

    private final List<IAWEView> preInitializedView = new ArrayList<IAWEView>();

    public AWEPropertySheet() {
        super();

        setPropertySourceProvider(new DataElementPropertySourceProvider());
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);

        for (final IAWEView view : preInitializedView) {
            registerView(view);
        }
        preInitializedView.clear();
    }

    public void registerView(final IAWEView view) {
        if (getSite() == null) {
            preInitializedView.add(view);
        } else {
            getSite().getPage().addSelectionListener(view.getViewId(), this);
        }
    }

    public void unregisterView(final IAWEView view) {
        if (getSite() != null) {
            getSite().getPage().removeSelectionListener(view.getViewId(), this);
        }
    }

}
