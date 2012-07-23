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

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractComboWidget;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SelectLoaderWidget extends AbstractComboWidget implements SelectionListener {

    /**
     * @param isEditable
     * @param isEnabled
     * @param loaderPage
     * @param projectModelProvider
     */
    protected SelectLoaderWidget(final boolean isEnabled, final ILoaderPage< ? > loaderPage,
            final IProjectModelProvider projectModelProvider) {
        super(Messages.SelectLoaderWidget_Label, false, isEnabled, loaderPage, projectModelProvider);
    }

    @Override
    protected Combo createWidget(final Composite parent, final int style) {
        Combo combo = super.createWidget(parent, style);

        combo.addSelectionListener(this);

        return combo;
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        // do nothing
    }

    @Override
    public void fillData() {
        for (ILoader< ? , ? > loader : getLoaderPage().getLoaders()) {
            getWidget().add(loader.getName());
        }
        updateData();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void widgetSelected(final SelectionEvent e) {
        for (ILoader loader : getLoaderPage().getLoaders()) {
            if (loader.getName().equals(getText())) {
                getLoaderPage().setCurrentLoader(loader);
                break;
            }
        }
        getLoaderPage().update();
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

    @Override
    public void updateData() {
        ILoader< ? , ? > loader = getLoaderPage().getCurrentLoader();
        getWidget().setText(loader == null ? StringUtils.EMPTY : loader.getName());
    }
}
