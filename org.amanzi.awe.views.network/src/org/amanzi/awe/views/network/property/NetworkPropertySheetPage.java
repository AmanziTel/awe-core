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
package org.amanzi.awe.views.network.property;

import org.amanzi.awe.views.network.view.NetworkTreeView;
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
