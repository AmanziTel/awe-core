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

/**
 * Class for udig perspective
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class UdigPerspectiveFactory extends AbstractPerspectiveFactory {

    public static final String UDIG_PERSPECTIVE = "org.amanzi.awe.perspective.uDig"; //$NON-NLS-1$
    private static final String CATALOG = "net.refractions.udig.catalog.ui.CatalogView"; //$NON-NLS-1$
    private static final String PROJECTS = "net.refractions.udig.project.ui.projectExplorer";
    private static final String LAYERS = "net.refractions.udig.project.ui.layerManager";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        // Get the editor area.
        String editorArea = layout.getEditorArea();

        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, getTopLeft(), editorArea);
        topLeft.addView(PROJECTS);

        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, getBottom(), editorArea);
        bottom.addView(CATALOG);

        // Here we are making folder layout to show two views side by side
        IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, getBottomLeft(), PROJECTS);
        bottomLeft.addView(LAYERS);

        layout.addPerspectiveShortcut(UDIG_PERSPECTIVE);
        layout.addPerspectiveShortcut(MapPerspective.ID_PERSPECTIVE);
    }

}
