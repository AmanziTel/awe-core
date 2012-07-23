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
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractPageWidget<C extends Composite> {

    private final Composite parent;

    private final IProjectModel activeProject;

    private C widget;

    private final ILoaderPage< ? > loaderPage;

    private final boolean isEnabled;

    protected AbstractPageWidget(final boolean isEnabled, final ILoaderPage< ? > loaderPage,
            final IProjectModelProvider projectModelProvider) {
        this.parent = loaderPage.getComposite();
        this.loaderPage = loaderPage;
        this.isEnabled = isEnabled;

        this.activeProject = projectModelProvider.getActiveProjectModel();
    }

    public void initializeWidget() {
        widget = createWidget(parent, getStyle());
        widget.setEnabled(isEnabled);
    }

    protected abstract C createWidget(Composite parent, int style);

    protected abstract int getStyle();

    protected IProjectModel getActiveProject() {
        return activeProject;
    }

    protected ILoaderPage< ? > getLoaderPage() {
        return loaderPage;
    }

    protected C getWidget() {
        return widget;
    }

    protected static GridData getLabelLayout() {
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

}
