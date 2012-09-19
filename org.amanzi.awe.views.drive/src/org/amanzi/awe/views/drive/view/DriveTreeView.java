/**
 * 
 */
package org.amanzi.awe.views.drive.view;

import org.amanzi.awe.views.drive.provider.DriveTreeContentProvider;
import org.amanzi.awe.views.drive.provider.DriveTreeLabelProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
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

    protected DriveTreeView(final DriveTreeContentProvider contentProvider) {
        super(contentProvider);

    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(final Composite parent) {
        setSearchField(new Text(parent, SWT.BORDER));
        super.createPartControl(parent);

    }

    @Override
    protected void setProviders() {
        super.setProviders();
        getTreeViewer().setLabelProvider(new DriveTreeLabelProvider());
    }

    @Override
    protected ITreeItem< ? , ? > getTreeItem(final IModel model, final IDataElement element) {
        // TODO Auto-generated method stub
        return null;
    }
}
