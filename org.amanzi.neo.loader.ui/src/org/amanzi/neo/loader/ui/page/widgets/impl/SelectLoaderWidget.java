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

import java.util.List;

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectLoaderWidget.ISelectLoaderListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractComboWidget;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
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
// TODO: LN: 10.10.2012, refactor to use widgets from org.amanzi.awe.ui
public class SelectLoaderWidget<T extends IConfiguration> extends AbstractComboWidget<ISelectLoaderListener>
        implements
            SelectionListener {

    public interface ISelectLoaderListener extends AbstractPageWidget.IPageEventListener {
        void onLoaderChanged();
    }

    private final List<ILoader<T, ? >> loaders;

    /**
     * @param isEditable
     * @param isEnabled
     * @param loaderPage
     * @param projectModelProvider
     */
    protected SelectLoaderWidget(final boolean isEnabled, final Composite parent, final ISelectLoaderListener listener,
            final List<ILoader<T, ? >> loaders, final IProjectModelProvider projectModelProvider) {
        super(Messages.SelectLoaderWidget_Label, false, isEnabled, parent, listener, projectModelProvider);
        this.loaders = loaders;
    }

    @Override
    protected Combo createWidget(final Composite parent, final int style) {
        final Combo combo = super.createWidget(parent, style);

        combo.addSelectionListener(this);

        return combo;
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        // do nothing
    }

    @Override
    public void fillData() {
        for (final ILoader< ? , ? > loader : loaders) {
            getWidget().add(loader.getName());
        }
        updateData();
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        for (final ISelectLoaderListener listener : getListeners()) {
            listener.onLoaderChanged();
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

    @Override
    public void setText(final String text) {
        getWidget().setText(text);
    }

    @Override
    protected GridData getComboLayoutData() {
        // TODO: LN: 10.10.2012, make a factory for LayoutData
        return new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
    }

}
