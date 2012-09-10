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
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DriveTreeContentProvider extends AbstractContentProvider<IDriveModel, Object> {
    private IDriveModelProvider driveModelProvider;

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected DriveTreeContentProvider(IDriveModelProvider driveModelProvider, IProjectModelProvider projectModelProvider) {
        super(projectModelProvider);
        this.driveModelProvider = driveModelProvider;
    }

    /**
     * create instance of network tree content provider
     */
    public DriveTreeContentProvider() {
        this(AWEUIPlugin.getDefault().getDriveModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider());
    }

    @Override
    public ITreeItem<IDriveModel, Object> createInnerItem(IDriveModel key, IDriveModel value) {
        return createInnerItem(key, value.asDataElement());
    }

    @Override
    public ITreeItem<IDriveModel, Object> createInnerItem(IDriveModel key, Object value) {
        // TODO Auto-generated method stub
        return new DriveTreeViewItem(key, value);
    }

    @Override
    public List<IDriveModel> getRootList() throws ModelException {
        return driveModelProvider.findAll(getProjectModelProvider().getActiveProjectModel());
    }

}
