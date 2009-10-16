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

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.rubypeople.rdt.ui.RubyUI;

public class PerspectiveFactory implements IPerspectiveFactory {
    public static final String AWE_PERSPECTIVE = "org.amanzi.awe.perspective"; //$NON-NLS-1$
    private static final String PROJECTS = "net.refractions.udig.project.ui.projectExplorer"; //$NON-NLS-1$
    private static final String LAYERS = "net.refractions.udig.project.ui.layerManager"; //$NON-NLS-1$
    private static final String CATALOG = "net.refractions.udig.catalog.ui.CatalogView"; //$NON-NLS-1$
    public static String NETWORK_VIEW_ID = "org.amanzi.awe.views.network.views.NetworkTreeView";
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
        
        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
        topLeft.addView(PROJECTS);
        topLeft.addView(RubyUI.ID_RUBY_EXPLORER);

        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.65f, editorArea);
        bottom.addView(CATALOG);
        
        //Here we are making folder layout to show two views side by side
        IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.25f,PROJECTS);
        bottomLeft.addView(LAYERS);  
        //Lagutko, 22.07.2009, add Network view to Perspective
        bottomLeft.addView(NETWORK_VIEW_ID);

        layout.addPerspectiveShortcut(AWE_PERSPECTIVE);
        layout.addPerspectiveShortcut(MapPerspective.ID_PERSPECTIVE);
        
        //Lagutko, 15.06.2009, add launch action set
        layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
    }

}
