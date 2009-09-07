package org.amanzi.awe.views.drive.views;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.view.NetworkTreeView;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.neo4j.neoclipse.view.NeoGraphViewPart;

/**
 * <p>
 * Drive Tree view
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class DriveTreeView extends NetworkTreeView {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.amanzi.awe.views.drive.views.DriveTreeView";

    @Override
    protected void setProviders(NeoServiceProvider neoServiceProvider) {
        viewer.setContentProvider(new DriveTreeContentProvider(neoServiceProvider));
        viewer.setLabelProvider(new DriveTreeLabelProvider(viewer));

    }

    @Override
    protected void showSelection(NeoNode nodeToSelect) {
        try {
            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(NeoGraphViewPart.ID);
            NeoGraphViewPart viewGraph = (NeoGraphViewPart)view;
            viewGraph.showNode(nodeToSelect.getNode());
            final StructuredSelection selection = new StructuredSelection(new Object[] {nodeToSelect.getNode()});
            viewGraph.getViewer().setSelection(selection, true);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
        } catch (Exception e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
}