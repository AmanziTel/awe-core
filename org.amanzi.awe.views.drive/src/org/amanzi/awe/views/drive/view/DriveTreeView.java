/**
 * 
 */
package org.amanzi.awe.views.drive.view;

import org.amanzi.awe.views.drive.provider.DriveTreeContentProvider;
import org.amanzi.awe.views.drive.provider.DriveTreeLabelProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This View contains a tree of measurements found in the database.
 * 
 * @author Bondoronok_P
 */
public class DriveTreeView extends AbstractTreeView {

    public static final String DRIVE_TREE_VIEW_ID = "org.amanzi.awe.views.drive.DriveTreeView";

    /**
     * The Constructor
     */
    public DriveTreeView() {
        this(new DriveTreeContentProvider());
    }

    protected DriveTreeView(DriveTreeContentProvider contentProvider) {
        super(contentProvider);

    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        setSearchField(new Text(parent, SWT.BORDER));
        super.createPartControl(parent);
        addVirtualListener();
   
    }

    /**
     *
     */
    private void addVirtualListener() {
        getTreeViewer().getTree().setItemCount(2);
        getTreeViewer().getTree().addListener(SWT.SetData, new Listener() {

            @Override
            public void handleEvent(Event event) {
                TreeItem item = (TreeItem)event.item;
                TreeItem parentItem = item.getParentItem();
                if (parentItem != null) {
                    item.setItemCount(10);
                }

            }
        });

    }

    @Override
    protected void setProviders() {
        super.setProviders();
        getTreeViewer().setLabelProvider(new DriveTreeLabelProvider());
    }
}
