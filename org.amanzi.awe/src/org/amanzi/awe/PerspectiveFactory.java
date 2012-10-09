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
package org.amanzi.awe;

import net.refractions.udig.internal.ui.MapPerspective;

import org.amanzi.awe.internal.AbstractPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;

public class PerspectiveFactory extends AbstractPerspectiveFactory {

    public static final String AWE_PERSPECTIVE = "org.amanzi.awe.perspective"; //$NON-NLS-1$
    private static final String LAYERS = "net.refractions.udig.project.ui.layerManager"; //$NON-NLS-1$
    private static final String CATALOG = "net.refractions.udig.catalog.ui.CatalogView"; //$NON-NLS-1$

    /**
     * Creates the initial layout for a page.
     * <p>
     * Implementors of this method may add additional views to a perspective. The perspective
     * already contains an editor folder identified by the result of
     * <code>IPageLayout.getEditorArea()</code>. Additional views should be added to the layout
     * using this value as the initial point of reference.
     * </p>
     * 
     * @param layout the page layout
     */
    @Override
    public void createInitialLayout(final IPageLayout layout) {
        // Get the editor area.
        final String editorArea = layout.getEditorArea();

        final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, getTopLeft(), editorArea);
        topLeft.addView(LAYERS);

        // Here we are making folder layout to show three views side by side
        final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, getBottomLeft(), editorArea);
        bottomLeft.addView(CATALOG);

        bottomLeft.addView(IPageLayout.ID_PROP_SHEET);

        layout.addPerspectiveShortcut(AWE_PERSPECTIVE);
        layout.addPerspectiveShortcut(MapPerspective.ID_PERSPECTIVE);
        layout.addPerspectiveShortcut(IPageLayout.ID_PROP_SHEET);
    }
}
