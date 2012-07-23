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

import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ResourceSelectorWidget extends AbstractPageWidget<Composite> {

    protected enum ResourceType {
        FILE, DIRECTORY;
    }

    private final ResourceType resourceType;

    private StringButtonFieldEditor editor;

    /**
     * @param isEnabled
     * @param loaderPage
     * @param projectModelProvider
     */
    protected ResourceSelectorWidget(final ResourceType resourceType, final ILoaderPage< ? > loaderPage,
            final IProjectModelProvider projectModelProvider) {
        super(true, loaderPage, projectModelProvider);
        this.resourceType = resourceType;
    }

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        switch (resourceType) {
        case DIRECTORY:
            editor = new DirectoryFieldEditor("resource", "Select Directory with Data", parent);
            break;
        case FILE:
            editor = new FileFieldEditor("resource", "Select File with Data", parent);
            break;
        }

        return parent;
    }

    @Override
    protected int getStyle() {
        return 0;
    }

}
