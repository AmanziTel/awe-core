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
package org.amanzi.awe.views.network.view;

import org.amanzi.awe.awe.views.view.provider.NewNetworkTreeContentProvider;
import org.amanzi.awe.awe.views.view.provider.NewNetworkTreeLabelProvider;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of Root nodes defined by
 * the Root.java class.
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NewNetworkTreeView extends ViewPart {

    /*
     * ID of this View
     */
    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NetworkTreeView";

    public static final String TRANSMISSION_VIEW_ID = "org.amanzi.awe.views.neighbours.views.TransmissionView";
    public static final String NEIGHBOUR_VIEW_ID = "org.amanzi.awe.views.neighbours.views.NeighboursView";
    public static final String N2N_VIEW_ID = "org.amanzi.awe.views.neighbours.views.Node2NodeViews";
    public static final String DB_GRAPH_VIEW_ID = "org.neo4j.neoclipse.view.NeoGraphViewPart";

    /*
     * TreeViewer for database Nodes
     */
    protected TreeViewer viewer;

    /*
     * NeoService provider
     */
    private NeoServiceProviderUi neoServiceProvider;

    private Text tSearch;

    /**
     * The constructor.
     */
    public NewNetworkTreeView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {

        tSearch = new Text(parent, SWT.BORDER);
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    
        
        neoServiceProvider = NeoServiceProviderUi.getProvider();
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            setProviders(neoServiceProvider);
            viewer.setInput(getSite());          
            getSite().setSelectionProvider(viewer); 
        } finally {
            tx.finish();
        }
        setLayout(parent);
    }
    
    /**
     * @param parent
     */
    private void setLayout(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        tSearch.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(tSearch, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(100, -5);
        viewer.getTree().setLayoutData(formData);

    }

    /**
     * Set Label and Content providers for TreeView
     * 
     * @param neoServiceProvider
     */

    protected void setProviders(NeoServiceProviderUi neoServiceProvider) {
        viewer.setContentProvider(new NewNetworkTreeContentProvider(neoServiceProvider));
        viewer.setLabelProvider(new NewNetworkTreeLabelProvider(viewer));
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    /**
     * Select node
     * 
     * @param node - node to select
     */
    public void selectNode(Node node) {
        viewer.refresh();
        viewer.reveal(new DataElement(node));
        viewer.setSelection(new StructuredSelection(new Object[] {new DataElement(node)}));
    }
}
