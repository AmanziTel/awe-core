package org.amanzi.awe.networktree.property;

import org.amanzi.awe.networktree.views.NetworkTreeView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * Property Sheet Page that shows Properties of Node that was selected on NetworkTree
 *
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NetworkPropertySheetPage extends PropertySheetPage implements ISelectionListener {
    
    /**
     * Constructor. Sets SourceProvider for this Page 
     * 
     * @param viewer
     */
    
    public NetworkPropertySheetPage() {
        super();        
        setPropertySourceProvider(new NetworkPropertySourceProvider());
    }
    
    /**
     * Creates a Control of this Page and adds a Listener for NetworkTreeView
     */
    
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        getSite().getPage().addSelectionListener(NetworkTreeView.NETWORK_TREE_VIEW_ID, this);
    }
}
