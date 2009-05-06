/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.views;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.catalog.json.beans.ExtTreeNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

/**
 * This class represents the Network View.
 */
public class NetworkTreeView extends ViewPart {
    public static final String NETWORK_VIEW_ID = "org.amanzi.awe.networktree.views.NetworkTreeView";
    /**
     * Tree viewer reference.
     */
    private TreeViewer viewer;
    /**
     * Action for double click event.
     */
    private Action doubleClickAction;

    private List<String> selectedTreeItems;
    private boolean initialized;

    /**
     * The constructor.
     */
    public NetworkTreeView() {
        selectedTreeItems = new ArrayList<String>();
        initialized = false;
    }
    /**
     * Disposes the title image when super is called and then hides the view.
     */
    @Override
    public void dispose() {
        super.dispose();
        getViewSite().getPage().hideView(this);
    }
    /**
     * This is a call back method that will allow us to create the view and initialize it with its
     * contents.
     */
    public void createPartControl( final Composite parent ) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        createMenu();
    }

    private void createMenu() {
        final Menu menu = new Menu(viewer.getTree());
        final MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Select on map");
        viewer.getTree().setMenu(menu);
        viewer.getTree().addMenuDetectListener(new MenuDetectListener(){

            public void menuDetected( MenuDetectEvent e ) {
                e.doit = viewer.getTree().getSelection().length != 0;
            }
        });
        item.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
            public void widgetSelected( SelectionEvent e ) {
                final TreeItem[] selection = viewer.getTree().getSelection();
                selectedTreeItems.clear();

                for( TreeItem treeItem : selection ) {
                    addSelected(treeItem.getText());
                }

            }
        });
    }

    private void addSelected( String item ) {
        if (!selectedTreeItems.contains(item)) {
            selectedTreeItems.add(item);
        }
    }
    /**
     * Consolidated actions.
     */
    public void makeActions() {
        doubleClickAction = new Action(){
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                ExtTreeNode extTreeNode = (ExtTreeNode) obj;
                if (extTreeNode.isLeaf()) {
                    System.out.println("Double click event on child");
                } else {
                    expand(obj);
                }
            }

            private void expand( Object obj ) {
                if (viewer.getExpandedState(obj))
                    viewer.setExpandedState(obj, false);
                else
                    viewer.setExpandedState(obj, true);
            }

        };
        /**
         * When user clicks on any node below method gets invoked.
         */
        viewer.addTreeListener(new ITreeViewerListener(){
            public void treeCollapsed( TreeExpansionEvent event ) {
                System.out.println("treeCollapsed");
            }
            public void treeExpanded( TreeExpansionEvent event ) {
                System.out.println("treeExpanded");
            }

        });
    }
    /**
     * Hooks the double click action.
     */
    public void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick( DoubleClickEvent event ) {
                doubleClickAction.run();
            }
        });
    }
    /**
     * setting the focus when view is opened.
     */
    public void setFocus() {
        if (viewer != null)
            viewer.getControl().setFocus();
    }
    /**
     * getter for Viewer so that other classes can access Viewer associated with this View.
     * 
     * @return TreeViewer
     */
    public TreeViewer getViewer() {
        return viewer;
    }
    /**
     * Setter for the viewer to set the viewer for this particular view.
     * 
     * @param viewer
     */
    public void setViewer( TreeViewer viewer ) {
        this.viewer = viewer;
    }

    /**
     * @return the selectedTreeItems
     */
    public List<String> getSelectedTreeItems() {
        return selectedTreeItems;
    }
    /**
     * @param selectedTreeItems the selectedTreeItems to set
     */
    public void setSelectedTreeItems( List<String> selectedTreeItems ) {
        this.selectedTreeItems = selectedTreeItems;
    }
    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    /**
     * @param initialized the initialized to set
     */
    public void setInitialized( boolean initialized ) {
        this.initialized = initialized;
    }

}