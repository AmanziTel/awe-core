package org.amanzi.awe;

import net.refractions.udig.internal.ui.MapPerspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {
    public static final String AWE_PERSPECTIVE = "org.amanzi.awe.perspective"; //$NON-NLS-1$
    private static final String PROJECTS = "net.refractions.udig.project.ui.projectExplorer"; //$NON-NLS-1$
    private static final String LAYERS = "net.refractions.udig.project.ui.layerManager"; //$NON-NLS-1$
    private static final String CATALOG = "net.refractions.udig.catalog.ui.CatalogView"; //$NON-NLS-1$
    private static final String NETWORK_VIEW_ID = "org.amanzi.awe.networktree.views.NetworkTreeView";
    private static final String BOTTOM_LEFT = "bottomLeft";
    /** 
     * Creates the initial layout for a page.
     * <p>
     * Implementors of this method may add additional views to a
     * perspective.  The perspective already contains an editor folder
     * identified by the result of <code>IPageLayout.getEditorArea()</code>.  
     * Additional views should be added to the layout using this value as 
     * the initial point of reference.  
     * </p>
     *
     * @param layout the page layout
     */
    public void createInitialLayout(IPageLayout layout) {
        // Get the editor area.
        String editorArea = layout.getEditorArea();
        
        layout.addView(PROJECTS, IPageLayout.LEFT, 0.25f, editorArea);
        layout.addView(CATALOG, IPageLayout.BOTTOM, 0.65f, editorArea);
        
        //Below code is commented by Sachin P 
//      layout.addView(LAYERS, IPageLayout.BOTTOM, 0.25f, PROJECTS);
        //Below code is added by Sachin P 
        //Here we are making folder layout to show two views side by side
        IFolderLayout bottomLeft = layout.createFolder(BOTTOM_LEFT, IPageLayout.BOTTOM, 0.25f,PROJECTS);
        bottomLeft.addView(LAYERS);
        bottomLeft.addView(NETWORK_VIEW_ID);
        // TODO: This code seems redundant with the perspectiveExtensions in plugin.xml
        layout.addPerspectiveShortcut(AWE_PERSPECTIVE);
        layout.addPerspectiveShortcut(MapPerspective.ID_PERSPECTIVE);
    }

//	public void createInitialLayout(IPageLayout layout) {
//		layout.addFastView(PROJECTS);
//		layout.addView(LAYERS, IPageLayout.LEFT, 0.3f, IPageLayout.ID_EDITOR_AREA);
//		layout.addView(BOOKMARKS, IPageLayout.BOTTOM, 0.7f, LAYERS);
//	}

}
