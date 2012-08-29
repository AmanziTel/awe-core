/**
 * 
 */
package org.amanzi.awe.views.drive.view;

import org.amanzi.awe.views.drive.provider.DriveTreeContentProvider;
import org.amanzi.awe.views.drive.provider.DriveTreeLabelProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

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
    }

    @Override
    protected void setProviders() {
        super.setProviders();
        getTreeViewer().setLabelProvider(new DriveTreeLabelProvider());
    }
}
