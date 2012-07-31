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

import org.amanzi.awe.awe.views.view.provider.NetworkTreeContentProvider;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.amanzi.neo.models.distribution.IDistributionBar;
import org.amanzi.neo.models.distribution.IDistributionModel;
import org.amanzi.neo.models.distribution.IDistributionalModel;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of INetworkModel nodes
 * defined by the INetworkModel.java class.
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class NetworkTreeView extends AbstractTreeView implements IAWEEventListenter {

    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NewNetworkTreeView";

    public static final String PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.PropertiesView";

    public NetworkTreeView() {
        super();
    }

    /**
     * add required Listener
     */
    @Override
    protected void addEventListeners() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        this.tSearch = new Text(parent, SWT.BORDER);
        this.treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        this.treeViewer.setComparer(new IElementComparer() {

            @Override
            public boolean equals(Object a, Object b) {
                if ((a instanceof IDistributionalModel) && (b instanceof IDistributionalModel)) {
                    return ((IDistributionalModel)a).getName().equals(((IDistributionalModel)b).getName());
                } else if ((a instanceof IDistributionModel) && (b instanceof IDistributionModel)) {
                    IDistributionModel aa = (IDistributionModel)a;
                    IDistributionModel bb = (IDistributionModel)b;
                    return aa.getName().equals(bb.getName())
                            && aa.getAnalyzedModel().getName().equals(bb.getAnalyzedModel().getName());
                } else if ((a instanceof IDistributionBar) && (b instanceof IDistributionBar)) {
                    IDistributionBar aa = (IDistributionBar)a;
                    IDistributionBar bb = (IDistributionBar)b;
                    return aa.getName().equals(bb.getName())
                            && aa.getDistribution().getName().equals(bb.getDistribution().getName())
                            && aa.getDistribution().getAnalyzedModel().getName()
                                    .equals(bb.getDistribution().getAnalyzedModel().getName());

                } else {
                    return a == null ? b == null : a.equals(b);
                }
            }

            @Override
            public int hashCode(Object element) {
                return 0;
            }
        });

        setProviders();
        this.treeViewer.setInput(getSite());

        this.treeViewer.getTree().addMouseTrackListener(new MouseTrackListener() {

            @Override
            public void mouseEnter(MouseEvent e) {
            }

            @Override
            public void mouseExit(MouseEvent e) {
            }

            @Override
            public void mouseHover(MouseEvent e) {
            }
        });

        addSearchListener();
        getSite().setSelectionProvider(this.treeViewer);
        setLayout(parent);
    }

    @Override
    protected IContentProvider getContentProvider() {
        return new NetworkTreeContentProvider();
    }

    @Override
    public void onEvent(IEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }
}