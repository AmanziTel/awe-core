package org.amanzi.awe.views.drive.views;

import org.amanzi.awe.views.network.view.NetworkTreeView;
import org.amanzi.neo.core.enums.NetworkElementTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of Root nodes defined by
 * the DriveRoot.java class.
 * 
 * @author Cinkel_A
 * @since 1.0.0
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
    protected void showThisView() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
        } catch (Exception e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected StopEvaluator getSearchStopEvaluator() {
        return new StopEvaluator() {

            @Override
            public boolean isStopNode(TraversalPosition currentPos) {
                String nodeType = NetworkTreeView.getNodeType(currentPos.currentNode(), "");
                boolean result = nodeType.equals(NetworkElementTypes.NETWORK.toString());
                return result;
            }
        };
    }
}