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
    private static final String DATABASE_PROJECT_EXPLORER = "org.amanzi.awe.views.explorer.view.ProjectExplorerView";
    private static final String NETWORK_TREE = "org.amanzi.awe.views.network.views.NewNetworkTreeView";
    private static final String DRIVE_TREE = "org.amanzi.awe.views.drive.views.DriveTreeView";
    private static final String N2N = "org.amanzi.awe.views.neighbours.views.NodeToNodeRelationsView";
    private static final String DISTRIBUTION = "org.amanzi.awe.views.reuse.views.DistributionAnalyzerView";
    private static final String PROPERTIES = "org.eclipse.ui.views.PropertySheet";
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
    public void createInitialLayout(IPageLayout layout) {
        // Get the editor area.
        String editorArea = layout.getEditorArea();

        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, getTopLeft(), editorArea);
        topLeft.addView(DATABASE_PROJECT_EXPLORER);

        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, getBottom(), editorArea);
        bottom.addView(CATALOG);
        bottom.addView(PROPERTIES);
        bottom.addView(N2N);
        bottom.addView(DISTRIBUTION);

        // Here we are making folder layout to show three views side by side
        IFolderLayout bottomLeft = layout
                .createFolder("bottomLeft", IPageLayout.BOTTOM, getBottomLeft(), DATABASE_PROJECT_EXPLORER);
        bottomLeft.addView(LAYERS);
        bottomLeft.addView(NETWORK_TREE);
        bottomLeft.addView(DRIVE_TREE);

        layout.addPerspectiveShortcut(AWE_PERSPECTIVE);
        layout.addPerspectiveShortcut(MapPerspective.ID_PERSPECTIVE);
    }

}
