package org.amanzi.awe;

import net.refractions.udig.internal.ui.MapPerspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Class for udig perspective
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class UdigPerspectiveFactory implements IPerspectiveFactory {
    
    public static final String UDIG_PERSPECTIVE = "org.amanzi.awe.perspective.uDig"; //$NON-NLS-1$
    private static final String CATALOG = "net.refractions.udig.catalog.ui.CatalogView"; //$NON-NLS-1$
    private static final String DATABASE_PROJECT_EXPLORER = "org.amanzi.awe.views.explorer.view.ProjectExplorerView";
    
    @Override
    public void createInitialLayout(IPageLayout layout) {
     // Get the editor area.
        String editorArea = layout.getEditorArea();

        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
        topLeft.addView(DATABASE_PROJECT_EXPLORER);
        
        // Here we are making folder layout to show two views side by side
        IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.25f, DATABASE_PROJECT_EXPLORER);
        bottomLeft.addView(CATALOG);

        layout.addPerspectiveShortcut(UDIG_PERSPECTIVE);
        layout.addPerspectiveShortcut(MapPerspective.ID_PERSPECTIVE);
    }

}
