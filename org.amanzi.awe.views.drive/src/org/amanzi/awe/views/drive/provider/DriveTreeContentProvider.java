package org.amanzi.awe.views.drive.provider;

import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * @author Bondoronok_P
 */
public class DriveTreeContentProvider extends AbstractContentProvider<IDriveModel> {

    private final IDriveModelProvider driveModelProvider;

    public DriveTreeContentProvider() {
        this(AWEUIPlugin.getDefault().getDriveModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider());
    }

    /**
     * @param projectModelProvider
     */
    protected DriveTreeContentProvider(IDriveModelProvider driveModelProvider, IProjectModelProvider projectModelProvider) {
        super(projectModelProvider);
        this.driveModelProvider = driveModelProvider;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    protected boolean additionalCheckChild(Object element) throws ModelException {
        return false;
    }

    @Override
    protected void handleInnerElements(ITreeItem<IDriveModel> parentElement) throws ModelException {
        setChildren(parentElement.getParent().getChildren(parentElement.getDataElement()));
    }

    @Override
    protected List<IDriveModel> getRootElements() throws ModelException {
        return driveModelProvider.findAll(getActiveProjectModel());
    }

    @Override
    protected void handleRoot(ITreeItem<IDriveModel> item) throws ModelException {
        handleInnerElements(item);
    }

}
