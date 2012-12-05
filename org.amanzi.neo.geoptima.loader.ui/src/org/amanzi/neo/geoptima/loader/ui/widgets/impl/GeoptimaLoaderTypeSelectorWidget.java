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

package org.amanzi.neo.geoptima.loader.ui.widgets.impl;

import org.amanzi.neo.geoptima.loader.ui.widgets.impl.GeoptimaLoaderTypeSelectorWidget.IGeoptimaLoaderTypeChanged;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class GeoptimaLoaderTypeSelectorWidget<T extends IConfiguration>
        extends
            AbstractPageWidget<Composite, IGeoptimaLoaderTypeChanged> implements SelectionListener {

    private final java.util.List<ILoader<T, ? >> loaders;

    /**
     * @param isEnabled
     * @param parent
     * @param listener
     * @param projectModelProvider
     */
    public GeoptimaLoaderTypeSelectorWidget(final Composite parent, final IGeoptimaLoaderTypeChanged listener,
            final IProjectModelProvider projectModelProvider, final java.util.List<ILoader<T, ? >> loaders) {
        super(true, parent, listener, projectModelProvider);
        this.loaders = loaders;
    }

    public interface IGeoptimaLoaderTypeChanged extends AbstractPageWidget.IPageEventListener {
        void onLoaderChanged();
    }

    public String getSelectedLoader() {
        return loadersList.getSelection()[0];

    }

    private List loadersList;

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        final Composite panel = new Composite(parent, SWT.NONE);
        // TODO: LN: 10.10.2012, make a factory for Layouts
        panel.setLayout(new GridLayout(1, false));
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Label listLabel = new Label(panel, SWT.NONE);
        listLabel.setText("Choose loader");
        // TODO: LN: 10.10.2012, make a factory for Layouts
        listLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        loadersList = new List(panel, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
        loadersList.setLayoutData(new GridData(GridData.FILL_BOTH));
        for (ILoader<T, ? > loader : loaders) {
            loadersList.add(loader.getName());
        }
        loadersList.addSelectionListener(this);
        loadersList.select(0);
        return parent;
    }

    @Override
    protected int getStyle() {
        return 0;
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        update();
    }

    private void update() {
        for (IGeoptimaLoaderTypeChanged listener : getListeners()) {
            listener.onLoaderChanged();
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }
}
